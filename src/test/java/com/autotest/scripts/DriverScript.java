package com.autotest.scripts;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autotest.libFunctions.CommonFile;
import com.autotest.libFunctions.Initialization;
// import com.autotest.libFunctions.RecoveryScn;
import com.autotest.libFunctions.ReportingFunctionsXml;
import com.autotest.libFunctions.StringConstants;


/**************************************************************************************************************************
 * Class Name		:	DriverScript
 * Class Description:	This class holds the main function to call various functions to initialize driver,Launch application, 
 * 					:	Execute batch of scenarios and close the driver
 *************************************************************************************************************************/
public class DriverScript {


	private static final Logger DriverScript_LOGS = LoggerFactory.getLogger(DriverScript.class);
	/********************************************************************************************************************
	 * Function Name		:	main
	 * Function Description	:	This is the function which initiates the application,
	 * 						:	calls the initialization class to initialize variables such as URL and
	 * 						:	starts Launch application, Execute batch of scenarios and close the driver
	 * 
	 * @throws IOException  
	 *******************************************************************************************************************/
	public static void main(String[] args) throws IOException {
		String strBrowserName = StringUtils.EMPTY ;
		String strBatchPath = StringUtils.EMPTY;
		Initialization objInitialization = Initialization.getInstance();
		CommonFile objCommonFile = new CommonFile();
		DriverScript_LOGS.info(StringConstants.STRBATCHSTARTED);
		
		

		try {
			if (args.length == 2)
			{
				//args[0]=Browser Name Eg. Safari , FireFox if not passed it will take a Value from Properties File
				strBrowserName =args[0].toString();
				//args[1]= user defined Batch File name eg. src/tstBatch/tstBatch.xls , if not passed it will take a Value from Properties file
				strBatchPath = args[1].toString();
			}
			//To Specify the Property file path , outside "src" folder. To pass a  'BrowserName , Batch File Path' passed as Path command line )
			strBrowserName = objInitialization.fnGetAppConfigValues(strBrowserName , strBatchPath ) ;

			if(StringUtils.isNotBlank(strBrowserName)){
				//To close all the browser window(s) before we start the batch run to make sure only one instance of THIS browser is running
				//RecoveryScn.recCloseBrowser();
				objInitialization.fnStartDriver();
				objInitialization.fnLaunchApp();
				ReportingFunctionsXml.createCustomReportFile();
				objCommonFile.fnExecuteBatch(Initialization.strBatchFilePath);
				objInitialization.fnCloseDriver();
				objCommonFile.fnCmCleanup();
			}
		}catch (IOException e) {
			DriverScript_LOGS.error(StringConstants.STRIOEXCEPTION);
			DriverScript_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			DriverScript_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}catch (Exception e) {
			DriverScript_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			DriverScript_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		finally{
			//provide the information to the user about the completion of automation batch run
			DriverScript_LOGS.info(StringConstants.STRCOMPLETEDBATCHRUN);
			if(StringUtils.equalsIgnoreCase(null,strBrowserName))
				DriverScript_LOGS.error(StringConstants.STRCHECKBROWSERNAME);
			objCommonFile = null;
			objInitialization = null;
			Initialization.pConfigFile.clear();
			//The bellow code of line will make sure that the application terminates once the
			//test case execution gets completed and releases the JVM
			Runtime.getRuntime().exit(0); 
		}
	}

	
}