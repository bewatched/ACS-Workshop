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
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
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
	private Properties props;
	
	private String dbFile = "database.properties";
    private String url;
    private String user;
    private String passwd;

	
	/////////////////////////////////////////////////////////////
	// Implementation of ComponentLifecycle
	/////////////////////////////////////////////////////////////

	@Override
	public void initialize(ContainerServices containerServices) throws ComponentLifecycleException {
		m_containerServices = containerServices;
		m_logger = m_containerServices.getLogger();
		m_logger.info("initialize() called...");
		
		// Find the database configuration file in the usual directories
		FileInputStream in = null;
		String pathToFile = ((String)File.separator) + "config" + ((String)File.separator) + "database.properties";
		String fileFound = findFileInACS(pathToFile);
		if (fileFound == null)
			throw new ComponentLifecycleException("Could not find "+ pathToFile + " database configuration file.");
		
		// Open and parse the file
        try {
        	in = new FileInputStream(pathToFile);
            props.load(in);
        } catch (IOException ex) {
        	m_logger.log(Level.SEVERE, ex.getMessage(), ex);
        	throw new ComponentLifecycleException("Could not open " + pathToFile + " database configuration file. Please");
        }finally {            
            try {
            	if (in != null)
                     in.close();
            } catch (IOException ex) {
            	m_logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        passwd = props.getProperty("db.passwd");
        m_logger.log(Level.INFO, "Opening connection to database using: user/passwd@url: " + user + "/" + passwd + "@" + url );
        
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            pst = con.prepareStatement("SELECT VERSION()");
            rs = pst.executeQuery();

            while (rs.next()) {
            	System.out.println(rs.getString(1));
            }
        } catch (Exception ex) {
            m_logger.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
            	m_logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        
        
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


	/////////////////////////////////////////////////////////////
	// Auxiliary methods
	/////////////////////////////////////////////////////////////
	
	String findFileInACS(String pathToFile){
	
	    List<String> dirs = new ArrayList<String>();
	    String introot = System.getenv("INTROOT");
	    if (introot != null) {
	        dirs.add(introot);
	    }
	    String intlist = System.getenv("INTLIST");
	    if (intlist != null) {
	        String[] intlistDirs = intlist.split(":");
	        for (String d : intlistDirs) {
	            dirs.add(d);
	        }
	    }
	    String acsroot = System.getenv("ACSROOT");
	    if (acsroot != null) {
	        dirs.add(acsroot);
	    }
	
	    for (String dir : dirs) {
	        String cf = dir + pathToFile;
	        File f = new File(cf);
	        if (f.exists() && !f.isDirectory() ) {
	            return cf;
	        }
	    }
	    
	    return null;
	}
	

}
