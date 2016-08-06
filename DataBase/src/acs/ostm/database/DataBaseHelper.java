package acs.ostm.database;

import java.util.logging.Logger;

import alma.acs.component.ComponentLifecycle;
import alma.acs.container.ComponentHelper;
import acsws.DATABASE_MODULE.*;

public class DataBaseHelper extends ComponentHelper
{
	public DataBaseHelper(Logger containerLogger)
	{
		super(containerLogger);
	}
	protected ComponentLifecycle _createComponentImpl()
	{
		return new DatabaseImpl();
	}

	protected Class _getPOATieClass()
	{
		return DataBasePOATie.class;
	}

	protected Class _getOperationsInterface()
	{
		return DataBaseOperations.class;
	}

}