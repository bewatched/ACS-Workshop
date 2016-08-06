package acs.ostm.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import alma.ACS.ComponentStates;
import alma.JavaContainerError.wrappers.AcsJContainerServicesEx;
import alma.acs.component.ComponentLifecycle;
import alma.acs.component.ComponentLifecycleException;
import alma.acs.container.ContainerServices;
import alma.maciErrType.wrappers.AcsJComponentCleanUpEx;
import acsws.DATABASE_MODULE.*;
import acsws.STORAGE_MODULE.*;
import acsws.SYSTEMErr.ImageAlreadyStoredEx;
import acsws.SYSTEMErr.InvalidProposalStatusTransitionEx;
import acsws.SYSTEMErr.ProposalNotYetReadyEx;
import acsws.TYPES.Proposal;
import acsws.TYPES.Target;

public class DatabaseImpl implements ComponentLifecycle, DataBaseOperations {
	
	private ContainerServices m_containerServices;
	private Logger m_logger;
	private Properties props;
	
	private ArrayList<Proposal> proposalList;
	private HashMap<Integer, Proposal> proposalHashMap;
	private int lastUsedPID = 0;
	private HashMap<String, CachedImage> cachedImages;
	
	/////////////////////////////////////////////////////////////
	// Implementation of ComponentLifecycle
	/////////////////////////////////////////////////////////////

	@Override
	public void initialize(ContainerServices containerServices) throws ComponentLifecycleException {
		m_containerServices = containerServices;
		m_logger = m_containerServices.getLogger();
		m_logger.finest("initialize() called.");
		
		proposalList = new ArrayList<Proposal>();
	}

	@Override
	public void execute() throws ComponentLifecycleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() throws AcsJComponentCleanUpEx {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aboutToAbort() {
		try {
			cleanUp();
		} catch (AcsJComponentCleanUpEx e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_logger.info("managed to abort...");
		
	}
	
	/////////////////////////////////////////////////////////////
	// Implementation of ACSComponent
	/////////////////////////////////////////////////////////////
	
	@Override
	public ComponentStates componentState() {
		return m_containerServices.getComponentStateManager().getCurrentState();
	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String name() {
		return m_containerServices.getName();
	}

	/////////////////////////////////////////////////////////////
	// Implementation of DataBaseOperations
	/////////////////////////////////////////////////////////////

	@Override
	public int storeProposal(Target[] targets) {
		// Preparation of new proposal
		Proposal newProposal = new Proposal();
		newProposal.pid = this.lastUsedPID + 1;
		newProposal.status = 0; // 0 = Queued
		newProposal.targets = targets;
		
		// Addition of the new proposal to in memory proposal list
		this.proposalHashMap.put(newProposal.pid, newProposal);
		
		// Add 1 to the current pid
		this.lastUsedPID = newProposal.pid;
		
		return this.lastUsedPID;
	}

	@Override
	public int getProposalStatus(int pid) {
		if( proposalHashMap.containsKey( pid ) ){
			Proposal temp = proposalHashMap.get(pid);
			return temp.status;
		}else{
			// TODO: Log warning
			return -1; // -1 for error, as valid results starts from 0
		}
		
	}

	@Override
	public void removeProposal(int pid) {
		if( proposalHashMap.containsKey( pid ) ){
			proposalHashMap.remove(pid);
		}else{
			// TODO: Log warning
		}		
	}

	@Override
	public byte[][] getProposalObservations(int pid) throws ProposalNotYetReadyEx {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Proposal[] getProposals() {
		Proposal[] proposals;
		Collection<Proposal> proposalsValues = proposalHashMap.values();
		proposals = new Proposal[proposalsValues.size()];
		
		int i = 0;
		for( Proposal prop : proposalHashMap.values() ){
			proposals[i] = prop;
			i++;
		}
		return proposals;
	}

	@Override
	public void setProposalStatus(int pid, int status) throws InvalidProposalStatusTransitionEx {
		if( status < 0 && status > 2 ){ // Not a valid status value
			// TODO: Log exception and its cause
			InvalidProposalStatusTransitionEx ex = new InvalidProposalStatusTransitionEx();
			throw ex;
		}		
		if( proposalHashMap.containsKey( pid ) ){
			if( proposalHashMap.get(pid).status == 0 && status == 1 ){
				proposalHashMap.get(pid).status = status;
			}else if ( proposalHashMap.get(pid).status == 1 && status == 2 ){
				proposalHashMap.get(pid).status = status;
			}else{
				// TODO: Log erroneous transition
				InvalidProposalStatusTransitionEx ex = new InvalidProposalStatusTransitionEx();
				throw ex;
			}
		}else{
			// TODO: Log warning
		}	
		
	}

	@Override
	public void storeImage(int pid, int tid, byte[] image) throws ImageAlreadyStoredEx {
		Storage m_storageComponent = null;
		try {
			m_storageComponent = StorageHelper.narrow(
					this.m_containerServices.getComponent("STORAGE") );
		} catch (AcsJContainerServicesEx e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( proposalHashMap.containsKey( pid ) ){ // Find the proposal
			Proposal prop = proposalHashMap.get(pid);
			boolean targetFound = false;
			for( Target target : prop.targets ){ // Find the target in the proposal
				if( target.tid == tid ) targetFound = true;
			}
			if( targetFound ){
				if( cacheImageInMemory(pid, tid, image) < 0 ){
					// TODO: Log failure
					throw new ImageAlreadyStoredEx();
				}else{
					// TODO: Log success in caching image
					if( isEveryImageCached(pid) ){
						// initiate storage of images into database
						m_storageComponent.storeObservation( prop, allImages(pid) );
						removeImages(pid);
					}
				}
			}else{
				// TODO: Fail and Log warning
			}
		}else{
			// TODO: Fail and Log warning
			return;
		}		
		
	}
	
	/////////////////////////////////////////////////////////////
	// Auxiliary methods
	/////////////////////////////////////////////////////////////
	
	private byte[][] allImages(int pid){
		Proposal prop = proposalHashMap.get(pid);
		byte[][] images = new byte[prop.targets.length][];
		int i = 0;
		for( Target targ : prop.targets){
			images[i] = cachedImages.get( generateId( pid, targ.tid)).image;
			i++;
		}
		
		return images;
	}
	
	private void removeImages(int pid){
		Proposal prop = proposalHashMap.get(pid);
		for ( Target targ : prop.targets ){
			if( cachedImages.containsKey( generateId(pid,targ.tid) ) ){
				cachedImages.remove( generateId(pid,targ.tid) );
			}
		}
	}
	
	private boolean isEveryImageCached(int pid){
		Proposal prop = proposalHashMap.get(pid);
		int imagesCachedSoFar = 0;
		for ( Target targ : prop.targets){
			if( cachedImages.containsKey( generateId(pid,targ.tid) ) ){
				imagesCachedSoFar++;
			}
		}
		if( imagesCachedSoFar == prop.targets.length ){
			return true;
		}else{
			return false;
		}
	}
	
	private int cacheImageInMemory(int pid, int tid, byte[] image ){
		if( cachedImages.containsKey( generateId(pid, tid) ) ){
			return -1; // Cannot cache, as there is already an image
		}else{
			CachedImage newCachedImage = new CachedImage(pid, tid, image);
			cachedImages.put( generateId(pid, tid), newCachedImage);
			return 0; // Success in cache
		}		
		
	}
	
	public static String generateId(int pid, int tid){
		return Integer.toString(pid) + "," + Integer.toString(tid);
	}
	
	private class CachedImage{
		private int pid;
		private int tid;
		private boolean stored;
		private byte[] image;
		
		public CachedImage(int pid, int tid, byte[] image ){
			this.pid = pid;
			this.tid = tid;
			this.stored = false;
			this.image = image;
		}
		
		public String getId(){
			return generateId(this.pid, this.tid);
		}
		
		
		public int getPid(){
			return this.pid;
		}
		
		public int getTid(){
			return this.tid;
		}
		
		public boolean isStored(){
			return stored;
		}
		
		public void setStored(){
			this.stored = true;
		}
		
		
	}

}
