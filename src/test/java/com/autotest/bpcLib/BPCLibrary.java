package com.autotest.bpcLib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.autotest.libFunctions.CommonFile;
import com.autotest.libFunctions.Initialization;
import com.autotest.libFunctions.KeywordLibrary;
import com.autotest.libFunctions.RecoveryScn;
import com.autotest.libFunctions.StringConstants;
import com.autotest.objLibrary.ObjectMap;



/*********************************************************************************************************
 * ClassName	:	BPCLibrary
 * Description	:	This class holds the various BPCFunctions
 * 				:	Each BPC function calls the corresponding action from the Keyword Library class
 * 				:	based on the data from Scenario excel sheet
 * 
 **********************************************************************************************************/

public class BPCLibrary {

	private static final Logger BPCLibrary_LOGS = LoggerFactory.getLogger(BPCLibrary.class);
	String strTempValue = StringUtils.EMPTY;
	//This is to store the temporary value(s) and use the same in other function(s)(bpc's)
	public static String sTemp = StringUtils.EMPTY;
	public static int iflagCounter = 1;
	KeywordLibrary objKeywordLibrary = null;
	
	CommonFile objCommonFile = null;
	static Date dtValue = null;
	static SimpleDateFormat sdfDate = null;
	static String strDynamicSuffix = StringUtils.EMPTY;
	public static String strRoleName ="AutomationRoleName"+strDynamicSuffix;
	public static ArrayList<String> frstScreenXpathColl = null;
	public static ArrayList<String> secScreenXpathColl = null;
	public static String dispatchId = StringUtils.EMPTY;
	
	public static String strTempURL = StringUtils.EMPTY;
	static int iGenericCounter = 1;
	static boolean bTempValue=false;
	//private static int iTotalRecords = 0;
	
	//constructor call to initialize the objects during "this" class initialization
	public BPCLibrary() {
		this.objKeywordLibrary = new KeywordLibrary();
		
		this.objCommonFile = new CommonFile();
		dtValue = new Date();
		sdfDate = new SimpleDateFormat ("yyyyMMddhhmmss");
		strDynamicSuffix = sdfDate.format(dtValue).toString();
		frstScreenXpathColl = new ArrayList<String>();
		secScreenXpathColl = new ArrayList<String>();
		
	}

	/***********************************************************************************************************
	 * Function Name	:	bpcReInitializeBrowser
	 * Description		:	Refreshes the application
	 * 
	 * @return void
	 * 
	 **********************************************************************************************************/

	public void bpcReInitializeBrowser() throws Exception{
		try{
			for (iflagCounter = 1;iflagCounter <= CommonFile.iStepsToExeCount && KeywordLibrary.isTestScenarioContinue;iflagCounter++){
				fnDisplayStepCount();
				if (CommonFile.strStepsToExecute.charAt(iflagCounter - 1)!='0'){
					switch(CommonFile.iBpcExecutingCaseNum = iflagCounter){
					case 1:
						//To reinitialize the web browser
						this.objKeywordLibrary.klReInitDriver();
						break;
					default:
						BPCLibrary_LOGS.warn(StringConstants.STRSTEPEXEINFO);
					}
				}
				else
					BPCLibrary_LOGS.info(StringConstants.STRBPCEXEINFO);
			}
		}
		catch(Exception e){ 
			fnExceptionErrorLog(e);
		} finally {}
	}


	/******************************************************************************************************
	 * Function Name	:	fnExceptionErrorLog
	 * Description		:	This function will get the exception which is thrown from the BPC function.
	 * 						This will log the details of the exceptions which is useful for the debugging
	 * 						purpose to fix issues
	 * 			**** Do not use this method as public access modifier ****
	 * 
	 * @param e
	 * 
	 ******************************************************************************************************/
	private void fnExceptionErrorLog(Exception e) {
		BPCLibrary_LOGS.error("BPC Library error :");
		BPCLibrary_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getMessage(),e.getCause());
		BPCLibrary_LOGS.debug(StringConstants.STRSTACKTRACE + e);
	}

	/************************************************************************************************************************************
	 * Function Name	:	resetBPCExecuteCasesTo()
	 * Description		:	Function to set the current bpc test cases steps to continue or Skip the test step execution 
	 * 						and then start the next BPC if any
	 * @return void
	 * 
	 ***********************************************************************************************************************************/
	public void resetBPCExecuteCasesTo(boolean setTo) {

		BPCLibrary_LOGS.info("Setting the variable 'RecoveryScn.bIsContinueCurrentBPC' to :{}",Boolean.toString(setTo));
		RecoveryScn.bIsContinueCurrentBPC = setTo;
		BPCLibrary_LOGS.info("RecoveryScn.bIsContinueCurrentBPC Value setted as : {} "+RecoveryScn.bIsContinueCurrentBPC);
	}


	/***********************************************************************************************************
	* Function Name	:	fnDisplayStepCount
	* Description	:	This function used to display the step counter of the BPC 
	* 
	* @return void
	* 
	***********************************************************************************************************/
				
	private void fnDisplayStepCount() {
		BPCLibrary_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
		BPCLibrary_LOGS.info("\t Step # "+(iflagCounter));
		BPCLibrary_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
	}

	public void bpcCleanup()
	{
		System.out.println("Calling the Memory Clean up of BPC Library");
		objKeywordLibrary = null;         
		sdfDate = null;
		objCommonFile = null;
		dtValue = null;
		
	}


}


