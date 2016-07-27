package acs.ostm.database;

import java.util.logging.Logger;

import alma.ACS.ComponentStates;
import alma.acs.component.ComponentLifecycle;
import alma.acs.component.ComponentLifecycleException;
import alma.acs.container.ContainerServices;
import alma.maciErrType.wrappers.AcsJComponentCleanUpEx;
import acsws.DATABASE_MODULE.*;
import acsws.SYSTEMErr.ImageAlreadyStoredEx;
import acsws.SYSTEMErr.InvalidProposalStatusTransitionEx;
import acsws.SYSTEMErr.ProposalNotYetReadyEx;
import acsws.TYPES.Proposal;
import acsws.TYPES.Target;

public class DatabaseImpl implements ComponentLifecycle, DataBaseOperations {
	
	private ContainerServices m_containerServices;
	private Logger m_logger;

	
	/////////////////////////////////////////////////////////////
	// Implementation of ComponentLifecycle
	/////////////////////////////////////////////////////////////

	@Override
	public void initialize(ContainerServices containerServices) throws ComponentLifecycleException {
		m_containerServices = containerServices;
		m_logger = m_containerServices.getLogger();
		m_logger.info("initialize() called...");
		
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
	// Implementation of HelloDemoOperations
	/////////////////////////////////////////////////////////////

	@Override
	public int storeProposal(Target[] targets) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProposalStatus(int pid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeProposal(int pid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[][] getProposalObservations(int pid) throws ProposalNotYetReadyEx {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Proposal[] getProposals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProposalStatus(int pid, int status) throws InvalidProposalStatusTransitionEx {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeImage(int pid, int tid, int[] image) throws ImageAlreadyStoredEx {
		// TODO Auto-generated method stub
		
	}


	
	

}
