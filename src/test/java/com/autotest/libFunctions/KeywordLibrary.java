package com.autotest.libFunctions;

import java.awt.Robot;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**************************************************************************************************************************
 * Class Name		:	KeywordLibrary 
 * ClassDescription	:	This class holds the collection of library functions
 * 
 *************************************************************************************************************************/

@SuppressWarnings({"static-method","boxing"})
public class KeywordLibrary extends CommonFile {

	public static int iRowNum;
	public static String strStoreValueForDynamicObjectId = StringUtils.EMPTY;
	// Variable to store the dynamic run time data
	public static HashMap<String, String> hDynTestData = new HashMap<String, String>();
	public static HashMap<String, String> hTagType = new HashMap<String, String>();
	public static String strElementType = StringUtils.EMPTY;
	public static String strTemp = "Temp";
	public static int iPageOrRecordCount=0;
	public static boolean isTestScenarioContinue = true; 
	ArrayList<String> strXpathValues =  new ArrayList<String>();
	public WebElement wbElement=null;
	Robot rEventHandler = null;
	public static String strFindBy="xpath";
	public String strTagName = StringUtils.EMPTY;
	
	private static final Logger KeywordLibrary_LOGS = LoggerFactory
			.getLogger(KeywordLibrary.class);
	
	/*******************************************************************************************************
	 * Function Name		:	KeywordLibrary
	 * Function Description	:	Constructor to initialize the HashMap hTagType
	 *
	 ********************************************************************************************************/
	public KeywordLibrary() {
		PropertyConfigurator.configure(Initialization.pConfigFile.getProperty("LOGPROPERTIES_FILEPATH"));
		klPopulateElementType();
	}

	/*******************************************************************************************************
	 * Function Name		:	klPopulateElementType
	 * Function Description :	This function is to set the values for tags and their respective types
	 *
	 ********************************************************************************************************/
	
	private void klPopulateElementType(){
		hTagType.put("A", "LINK");
		hTagType.put("INPUT", "INPUT");
		hTagType.put("SUBMIT", "BUTTON");
		hTagType.put("OTHERS", "WEBELEMENT");
		hTagType.put("IMG", "IMAGE");
		hTagType.put("DIV", "WEBELEMENT");

	}
	/*******************************************************************************************************
	 * Function Name		:	klFindTagType
	 * Function Description	:	This function finds the type of tag associated with the element 
	 * 						:	from hTagType by passing the key
	 *
	 ********************************************************************************************************/
	
	private String klFindTagType(String strTagName){
		String strTagType="WebElement";
		if(hTagType.containsKey(strTagName.toUpperCase())){
			strTagType=hTagType.get(strTagName);
			KeywordLibrary_LOGS.info("Tag type is "+strTagType);
		}
		return strTagType;
	}
	
	
	/*******************************************************************************************************
	 * Function Name		:	klFindElement() 
	 * Function Description	:	This function is find an element based on the find type:id,name,cssSelector,xpath,linktext etc
	 * 
	 * Input				:	the ID,Name,cssSelector,LinkText or xpath value
	 * 						:	It utilizes the strFindBy Global value to decide find element
	 *
	 ********************************************************************************************************/
	
	public boolean klFindElement(String strFindValue){
		boolean bReturnStatus = false;
		this.strTagName = StringUtils.EMPTY;
		try{
			setDrvWaitToDefault();
			bReturnStatus = verifyElementPresenceByOption(strFindValue);
		KeywordLibrary_LOGS.info(bReturnStatus ?"-Element found on the page":"Element NOT found on the page");

		if(this.strTagName.equalsIgnoreCase("INPUT")){
			KeywordLibrary_LOGS.info("Type of INPUT is :",this.wbElement.getAttribute("type").toString());
			bReturnStatus = this.wbElement.getAttribute("type").toString().equalsIgnoreCase("hidden") ? false : true;
		}
		strElementType=klFindTagType(this.strTagName);
		strFindBy="xpath";
		}
		catch(NoSuchElementException e){
			KeywordLibrary_LOGS.error("Unable to identify the element type :"+strFindValue);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
		}
		catch(Exception e){
			KeywordLibrary_LOGS.error("Error in klFindElement() for input xpath ..."+strFindValue);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
		}
		finally{
		}
		return bReturnStatus;
	}
	
	
	
	/*******************************************************************************************************
	 * Function Name		:	klFindElementCustomWait
	 * Function Description	:	This function is find an element based on the find type:id,name,cssSelector,xpath,linktext etc
	 * 
	 * Input				:	the ID,Name,cssSelector,LinkText or xpath value
	 * 						:	It utilizes the strFindBy Global value to decide find element. 
	 * 						:	Also, you can set explicit wait.
	 *  
	 ********************************************************************************************************/
	
	public boolean klFindElementCustomWait(String strFindValue,int explicitWait){
		boolean bReturnStatus = false;
		try{
			setDrvWaitTo(explicitWait);
		bReturnStatus = verifyElementPresenceByOption(strFindValue);
		
		strElementType=klFindTagType(this.strTagName);
		strFindBy="xpath";
		}
		catch(NoSuchElementException e){
			KeywordLibrary_LOGS.error("Unable to identify the element type :"+strFindValue);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
		}
		catch(Exception e){
			KeywordLibrary_LOGS.error("Error in klFindElement() for input xpath ..."+strFindValue);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
		}
		finally{
			setDrvWaitToDefault();
			KeywordLibrary_LOGS.info("into the finally for klFindElement() fxn...");
		}
		return bReturnStatus;
	}

	/*******************************************************************
	 * Function Name		:	verifyElementPresenceByOption
	 * Function Description	:	This function is find an element based on the find type:id,name,cssSelector,xpath,linktext etc
	 * 
	 * Input				: 	the ID,Name,cssSelector,LinkText or xpath value
	 * 						:	It utilizes the strFindBy Global value to decide find element. Also, you can set explicit wait.
	 * 
	 ********************************************************************/
	
	private boolean verifyElementPresenceByOption(String strFindValue) {
		boolean bReturnStatus = false;
		try{
			if(strFindBy.equalsIgnoreCase("xpath")){
				this.wbElement=Initialization.driver.findElement(By.xpath(strFindValue));
				//bReturnStatus = this.wbElement.isDisplayed();
			}
			else if(StringUtils.equalsIgnoreCase(strFindBy, "cssSelector"))
				this.wbElement=Initialization.driver.findElement(By.cssSelector(strFindValue));
			else if(StringUtils.equalsIgnoreCase(strFindBy,"id"))
				this.wbElement=Initialization.driver.findElement(By.id(strFindValue));
			else if(StringUtils.equalsIgnoreCase(strFindBy,"name"))
				this.wbElement=Initialization.driver.findElement(By.name(strFindValue));
			else if(StringUtils.equalsIgnoreCase(strFindBy,"linkText"))
				this.wbElement=Initialization.driver.findElement(By.linkText(strFindValue));
			else 
				this.wbElement=Initialization.driver.findElement(By.xpath(strFindValue));
			bReturnStatus = true;
		}catch (Exception e) {
			bReturnStatus= false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return bReturnStatus;
	}

	/************************************************************************************
	 * Function Name		:	getWebElementDetails
	 * 
	 * Function Description	:	This method will simply get the webdriver details and displayed in the console.
	 * 						:	[Note: For future implementation, if we need to add some more details to be displayed 
	 * 						:	for the web element , we can do it in this method which will reflect ]
	 * 
	 *************************************************************************************/
	
	public void getWebElementDetails() {
		try{
			KeywordLibrary_LOGS.info("webelement :"+this.wbElement);
			this.strTagName=this.wbElement.getTagName();
			KeywordLibrary_LOGS.info("Tag type is :"+this.strTagName);
		}catch(Exception e){
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}

	/**************************************************************************************
	 * Function Name		:	setDrvWaitTo
	 * 
	 * Function Description	:	This will always sets the driver wait to user defined wait.
	 * 							Once this is set , then the driver will wait for the same
	 * 							amount of time(in seconds) until another reset happens.
	 * 							use this method if we need to reset the driver to its default value
	 * 
	 * @param explicitWait
	 *************************************************************************************/
	
	public void setDrvWaitTo(int explicitWait) {
		Initialization.driver.manage().timeouts().implicitlyWait(explicitWait, TimeUnit.SECONDS);
	}
	
	/**************************************************************************************
	 * Function Name		:	setDrvWaitToDefault
	 * 
	 * Function Description	:	This will always sets the driver wait to Default value which is defined in the 
	 * 						:	Configuration file. Once this is set , then the driver will wait for the same
	 * 						:	amount of time(in seconds) until another reset happens.
	 * 						:	use this method if we need to reset the driver to its default value
	 * 
	 * @param explicitWait
	 *************************************************************************************/
	
	public void setDrvWaitToDefault() {
		Initialization.driver.manage().timeouts().implicitlyWait(Initialization.strDefaultDriverWait, TimeUnit.SECONDS);
	}
	

	/*******************************************************************************************************
	 * Function Name		:	klSetFindElementBy
	 * Function Description	:	This function is to set the value for the type of search for an 
	 * 						:	element: id,name,xpath,cssSelector etc
	 * 						:	It sets the value for strFindBy based on the value passed
	 *
	 ********************************************************************************************************/
	
	public void klSetFindElementBy(String strSetValue){
		KeywordLibrary_LOGS.info(strSetValue);
		KeywordLibrary_LOGS.info(strFindBy);
		strFindBy=strSetValue;
	}

	/****************************************************************************************************
	 * Function Name		:	klIsElementPresent
	 * Function Description	:	This Function checks for the element on the web page, if present returns true, else false
	 * 
	 * InPuts				:	strFindProperty - Xpath of the Web Element
	 * 						:	strName - Logical Name of the Web Element 
	 * 
	 ***************************************************************************************************/
	
	public boolean klIsElementPresent(String strFindProperty, String strName) {
		boolean bReturnStatus = false;
		try {
			if(klFindElement(strFindProperty)){
				bReturnStatus = true;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "",
						StringConstants.STRSHDELEFOUND, String.format(StringConstants.STRSHDELEFOUNDSUCCS,strName));
			}else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						StringConstants.STRSHDELEFOUND, String.format(StringConstants.STRSHDELEFOUNDFAILURE,strName));
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STRSHDELEFOUND, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STRSHDELEFOUND, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STRSHDELEFOUND, StringConstants.STRTEXTINPEXCEP.concat(StringConstants.STRCHKLOG));
		}
		return bReturnStatus;
	}

	
	/****************************************************************************************************
	 * Function Name		:	klVerifyMultipleElementsPresent
	 * 
	 * Function Description	:	Sometimes we will have the scenarios to test the multiple elements in the same page
	 * 						:	In that time, we can use this method and just path the elements Xpath in an string 
	 * 						:	array list and display the result. Instead of calling klIsElementPresent() multiple 
	 * 						:	times, we can use this method calling one time to check multiple elements. This function 
	 * 						:	will report as it is like the normal pass and failed scenarios. 
	 * 
	 * InPuts				:	strXpaths - Xpath(s) of the Web Element
	 * 						:	strNames - Logical Name(s) of the Web Element 
	 * 
	 * Note 				: 	Maximum of 5 elements should be used
	 * 
	 ***************************************************************************************************/
	
	public boolean klVerifyMultipleElementsPresent(String strXpaths, String strNames) {
		boolean bReturnStatus = false;
		String [] strXpath = StringUtils.split(strXpaths,";");
		String [] strName = StringUtils.split(strNames,";");
		int iCount = strXpath.length;
		int i =0;
		try {
			if(strXpath.length == strName.length){
			for(;i<iCount;i++){
			if(klFindElement(strXpath[i].toString())){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName[i].toString(), "",
						StringConstants.STRSHDELEFOUND, String.format(StringConstants.STRSHDELEFOUNDSUCCS,strName[i].toString()));
			}else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName[i].toString(), "",
						StringConstants.STRSHDELEFOUND, StringConstants.STRSHDELEFOUNDFAILURE);
			}}else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "CountNotMatch", "",
						StringConstants.STRCOUNTSHDMATCH,String.format(StringConstants.STRCOUNTSHDMATCHFAILURE,strXpath.length,strName.length));
			
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName[i].toString(), "",
					StringConstants.STRSHDELEFOUND, StringConstants.STRUNRECBROWEXCEP);
			bReturnStatus = false;
			RecoveryScn.recUnReachableTestScnExitTrue();
		} catch (Exception e) {
			bReturnStatus=false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName[i].toString(), "",
						StringConstants.STRSHDELEFOUND,  StringConstants.STRTEXTINPEXCEP.concat(StringConstants.STRCHKLOG));
			}
		return bReturnStatus;
	}
	
	/****************************************************************************************************
	 * Function Name		:	klIsElemPresentNoReport
	 * 
	 * Function Description	:	This will not report any error when there is an exception but this will 
	 * 						:	report internally the exception to the log files when there is some exception
	 * 
	 * InPuts				:	strXpath - Xpath of the Web Element  
	 * 
	 ***************************************************************************************************/
	public boolean klIsElementPresentNoReport(String strFindProperty) {
		boolean bReturnStatus = false;
		try {
			bReturnStatus = klFindElement(strFindProperty);
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return bReturnStatus;
	}

	
	/****************************************************************************************************
	 * Function Name		:	klCheckCurrentBPCToContinue 
	 * 
	 * Function Description	:	This function will check for the current BPC to execute the remaining test steps 
	 * 							or not,Since there might be some scenarios where we need to just verify one element 
	 * 							and based on the presence of that element , we need to continue the remaining BPC 
	 * 							cases. Please make sure that you are resetting the option of this method.
	 * 
	 * Inputs				:	strFindProperty - strFindProperty of the Web Element  
	 * 
	 * @bIsBPCContinue		:	user defined value whether we need to continue the current bpc cases or not. once this 
	 * 							is set to true: then it will compare and then execute the remaining cases in the current 
	 * 							BPCs only if it true if it is set to false: then it will compare and then execute the 
	 * 							remaining cases in the current BPCs only if it false

	 ***************************************************************************************************/
	public boolean klCheckCurrentBPCToContinue(String strFindProperty,String strName,boolean bIsBPCContinue) {
		try {
			RecoveryScn.bIsContinueCurrentBPC = (klIsElementPresentNoReport(strFindProperty) == bIsBPCContinue);
			if(RecoveryScn.bIsContinueCurrentBPC)
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "CheckForCurntBPCExe",
						StringConstants.STRVERIFYBPCTOCONTINUE,RecoveryScn.bIsContinueCurrentBPC ? StringConstants.STRVERIFYBPCTOCONTINUESUCCS : StringConstants.STRVERIFYBPCTOCONTINUEFAIL);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "CheckForCurntBPCExe",
					StringConstants.STRVERIFYBPCTOCONTINUE, String.format(StringConstants.STRGENEXCEPCAUGHT,"klCheckCurrentBPCToContinue()"));
		}
		return RecoveryScn.bIsContinueCurrentBPC;
	}

	/****************************************************************************************************
	 * Function Name - klSetExecutionOfCurrentBPCTo 
	 * Function Description - This function will set the current BPC to continue to TRUE or FALSE based
	 * on the return status of the other function's.This is just similar to <b>klCheckCurrentBPCToContinue()</b>
	 * except the passing parameter is the only difference 
	 * input : boolean status 
	 * 
	 ***************************************************************************************************/
	public boolean klSetExecutionOfCurrentBPCTo(boolean bIsBPCContinue) {
		try {
			RecoveryScn.bIsContinueCurrentBPC = bIsBPCContinue;
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bIsBPCContinue, "", "CheckForCurrrentBPCToProceed",
					StringConstants.STRVERIFYBPCTOCONTINUE, bIsBPCContinue ? StringConstants.STRVERIFYBPCTOCONTINUESUCCS :  StringConstants.STRVERIFYBPCTOCONTINUEFAIL);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "", "CheckForCurrrentBPCToProceed",
					StringConstants.STRVERIFYBPCTOCONTINUE, String.format(StringConstants.STRGENEXCEPCAUGHT,"klSetExecutionOfCurrentBPCTo()"));
		}
		return RecoveryScn.bIsContinueCurrentBPC ;
	}

	
	
	
	/****************************************************************************************************
	 * Function Name - klGetColumnOrRowCountForTbl 
	 * Function Description - This function will get the column and the row counts from the table.
	 * Input: strTableXpath-This can be the Xpath value for the table header(in such case: give upto : //table/tbody/tr/th)
	 * or table data (in such case: give upto : //table/tbody/tr/td)
	 * this will report internally the exception to the log files when there is some exception.
	 * return type: int-Which can be number of headers/columns or number of rows/data rows as well
	 * 
	 ***************************************************************************************************/
	public int klGetColumnOrRowCountForTbl(String strTableXpath) {
		int iCount = 0;
		try {
			if(klFindElement(strTableXpath)){
				iCount = Initialization.driver.findElements(By.xpath(strTableXpath)).size(); 
			}
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return iCount;
	}

	
	
	/****************************************************************************************************
	 * Function Name - klVrfyTblDetails 
	 * Function Description - This function will verify the value against the expected values in the test data sheet
	 * Input: strTableXpath-This can be the Xpath value for the table header(in such case: give upto : //table/tbody/tr/th)
	 * or table data (in such case: give upto : //table/tbody/tr/td).Use this function in case the table header names 
	 * should be in correct order.
	 * this will report internally the exception to the log files when there is any exception.
	 * Also reports that the table data is as per the expected data or not in the report xml file.
	 * return boolean.
	 * 
	 ***************************************************************************************************/
	public boolean klVrfyTblDetails(String strTableXpath,String strParamName,String strExpValues,boolean bDataSource) {
		String[] strExpVal = null;
		boolean bReturnStatus = false;
		try {
			if(klIsElementPresentNoReport(strTableXpath)){
				int iCount = klGetColumnOrRowCountForTbl(strTableXpath);
				strExpVal = bDataSource ? StringUtils.split(super.fnGetParamValue(strExpValues),";") : StringUtils.split(strExpValues,";");
				for(int i=1;i<=iCount;i++){
					//this is to concatenate the table data value index (%d) dynamically
					klVPWebElementText(String.format(strTableXpath.concat("[").concat("%d").concat("]"),i), strParamName, strExpVal[i-1].toString().trim(),false);
					bReturnStatus= true;
				}
			}else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, "", "Table Should present for verification",StringConstants.STRNOSUCHELEEXCEP);
				bReturnStatus= false;
			}
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus= false;
			KeywordLibrary_LOGS.error(StringConstants.STRNOSUCHELEEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, "",
						 "Table Should present for verification",StringConstants.STRNOSUCHELEEXCEP);
			}catch (UnreachableBrowserException e) {
				bReturnStatus= false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, "",
					"Table Should present for verification", StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			bReturnStatus= false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, "",
						"Table Should present for verification",  StringConstants.STRWEBDRVEXCEP);
		} 
		catch (Exception e) {
			bReturnStatus= false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRCLKELEEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, "",
						"Table Should present for verification", StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}


	/*******************************************************************************************************
	 * Function Name		:	klReInitDriver() 
	 * Function Description	:	This function Reinitializes the Browser 
	 * 
	 ********************************************************************************************************/

	public void klReInitDriver() {
		try {
			Initialization objInitialization =Initialization.getInstance();
			objInitialization.fnCloseDriver();
			klWait(Initialization.strWaitTime);
			objInitialization.fnStartDriver();
			klWait(Initialization.strWaitTime);
			objInitialization.fnLaunchApp();
			klWait(Initialization.strWaitTime);
			KeywordLibrary_LOGS.info("Started the driver and relaunched the application");
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}

	/*******************************************************************************************************
	 * Function Name		:	klSoldToShipTo 
	 * Function Description	:	This Function stores the account number in a temporary variable. 
	 * InPuts				:	strValue - key to find the corresponding value
	 ********************************************************************************************************/


	public boolean klSoldToShipTo(String strValue)
	throws Exception {
		try {
			strTemp = super.fnGetParamValue(strValue);			
			return true;
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t Exception occurred while saving the object value in the strTemp");
			return false;
		}

	}

	/*******************************************************************************************************
	 * Function Name		:	klWebElementClick
	 * Function Description	:	This Function verifies the WebElement object with respect to XPath and clicks on the
	 * 						:	specified Web Element.
	 *  
	 * InPuts				:	strXpath - XPath of the Web Element
	 * 						:	strName - Logical Name of the Web Element  
	 * 
	 ********************************************************************************************************/
	public boolean klWebElementClick(String strFindProperty, String strName)
	throws Exception {
		boolean bReturnStatus = false;
		try {
			if (klIsElementPresentNoReport(strFindProperty)){
				this.wbElement.click();
				Thread.sleep(1000);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
							"", String.format(StringConstants.STRCLKELEEXPECTED,strName), String.format(StringConstants.STRCLKELESUCCS,strName));
				this.wbElement=null;
				bReturnStatus = true;
			} else 
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
							"",  String.format(StringConstants.STRCLKELEEXPECTED,strName), String.format(StringConstants.STRCLKELEFAILURE,strName));
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRNOSUCHELEEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRNOSUCHELEEXCEP);
		}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRCLKELEEXPECTED,strName),  StringConstants.STRWEBDRVEXCEP);
		}catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}

	/****************************************************************************************************
	 * Function Name		:	klElementNotPresent
	 * Function Description	:	This Function Checks if a particular element is not present.
	 * 
	 * InPuts				:	strXpath - Xpath of the Web Element
	 * 						:	strName - Logical Name of the Web Element
	 * 
	 ***************************************************************************************************/

	public boolean klElementNotPresent(String strXpath, String strName) {
		boolean bReturn =false;
		try {
			if(!klIsElementPresentNoReport(strXpath)){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "",
						String.format(StringConstants.STRSHDNOTELEFOUND, strName), String.format(StringConstants.STRSHDNOTELEFOUNDSUCCS,strName));
				bReturn=true;
			}
			else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRSHDNOTELEFOUND, strName), String.format(StringConstants.STRSHDNOTELEFOUNDFAILURE,strName));
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.info(StringConstants.STRUNRECBROWEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRSHDNOTELEFOUND, strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		} catch (Exception e) {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRSHDNOTELEFOUND, strName), StringConstants.STRFUNNOTEXEC);
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}finally{
			setDrvWaitToDefault();
		}
		return bReturn;
	}

	/****************************************************************************************************
	 * Function Name		:	klVPElmntPresentAndExit
	 * Function Description	:	This Function Checks if an Element is present.
	 * 						:	If not then Exits the test case by setting the RecoveryScn.recTestScnExitTrue.
	 * 
	 * InPuts				:	strXpath - Xpath of the Web Element
	 * 						:	strName - Logical Name of the Web Element 
	 * 
	 ***************************************************************************************************/

	public boolean klVPElmntPresentAndExit(String strXpath, String strName) {
		try {
				KeywordLibrary.isTestScenarioContinue = klIsElementPresentNoReport(strXpath);
				KeywordLibrary_LOGS.info(String.format(StringConstants.STRMANELEBPCCHK,strName));
				ReportingFunctionsXml.fnSetReportBPCStepStatus(KeywordLibrary.isTestScenarioContinue, strName, "",
						String.format(StringConstants.STRMANELEBPCCHK,strName), KeywordLibrary.isTestScenarioContinue ? String.format(StringConstants.STRMANELEBPCCHKSUCCS, strName)
								:String.format(StringConstants.STRMANELEBPCCHKERR,strName));
		} catch (NoSuchElementException e) {
			KeywordLibrary.isTestScenarioContinue = false;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRMANELEBPCCHK,strName),StringConstants.STRNOSUCHELEEXCEP);
				RecoveryScn.recTestScnExitTrue();
		} 
		catch (UnreachableBrowserException e) {
			KeywordLibrary.isTestScenarioContinue = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRMANELEBPCCHK,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}
		catch (WebDriverException e) {
			KeywordLibrary.isTestScenarioContinue = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRMANELEBPCCHK,strName), StringConstants.STRWEBDRVEXCEPBPCSKIP);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			RecoveryScn.recTestScnExitTrue();
		} catch (Exception e) {
			KeywordLibrary.isTestScenarioContinue = false;
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						String.format(StringConstants.STRMANELEBPCCHK,strName), StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
				KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
						+ e.getMessage() + " and :" + e.getStackTrace());
		}
		return KeywordLibrary.isTestScenarioContinue;
	}

	
	/****************************************************************************************************
	 * Function Name		:	klPerformMandatoryOperation 
	 * Function Description	:	This function will perform the given mandatory operation and sets the 
	 * 						:	"KeywordLibrary.isTestScenarioContinue" flag to true or false based on 
	 * 						:	the output of the performed function
	 * return				:	void
	 *
	 ***************************************************************************************************/

	public boolean klPerformMandatoryOperation(boolean bIsTestScenarioContinue) {
			KeywordLibrary.isTestScenarioContinue = bIsTestScenarioContinue;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bIsTestScenarioContinue, "", "",
						StringConstants.STRMANFUNCEXE,(bIsTestScenarioContinue)?StringConstants.STRMANFUNCEXESUCCESS:StringConstants.STRMANFUNCEXEFAILURE);
				return KeywordLibrary.isTestScenarioContinue;
	}
	
	/*******************************************************************************************************
	 * Function Name		:	klRemoveFilters
	 * Function Description	:	This function removes all the filters present
	 * 
	 ********************************************************************************************************/
	public boolean klRemoveFilters(String strFindProperty, String strName) throws Exception {
		String strExpectedValue="Filter should be successfully removed";
		String strActualValue="";
		int iIter = 0;

		try {
			while (klIsElementPresentNoReport(strFindProperty)) {
				this.wbElement.click();
				strActualValue = "Filter removed successfully";
				klWait(Initialization.strWaitTime);
				if(iIter==Integer.parseInt(Initialization.strMaxIteration)){
					break;
				}
				iIter++;
			} 
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
					strName, "", strExpectedValue, strActualValue);
			this.wbElement=null;
			return true;
		} catch (NoSuchElementException e) {
			strActualValue="Filter not found on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			return false;
		} 
		catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}
		catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			strActualValue="WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			strActualValue=" Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage() + " and :" + e.getStackTrace());
			return false;
		}

	}
	/******************************************************************************************
	 * Function Name		:	klWebEditTextChange 
	 * Function Description	:	This Function enters the data in the edit box 
	 * InPuts				:	strXpath - Xpath of the Web Button , strName - Logical Name of the Web Edit Box 
	 * 
	 * strValue				:	Value to be entered in the Edit box, strDataSource - False(Value directly passed
	 * 						:	in the function), True(Value is to be retrieved from Datasheet) 
	 * 
	 *******************************************************************************************/
	public boolean klWebEditTextChange(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnStatus = false;
		try {
			strValue = (bDataSource) ? super.fnGetParamValue(strValue) : strValue;
			if(!StringUtils.isNotBlank(strValue))
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strValue,
						String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), String.format(StringConstants.STREMPTYPARAMVAL,strValue,strName));
			else{
				if (klIsElementPresentNoReport(strFindProperty)) {
					this.wbElement.clear();
					this.wbElement.sendKeys(strValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,strValue,
							String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), String.format(StringConstants.STREXPENTRINPUTVALSUCCS,strValue,strName));
					strStoreValueForDynamicObjectId=strValue;
					this.wbElement=null;
					bReturnStatus = true;
				} else 
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
							strValue, String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), String.format(StringConstants.STREXPENTRINPUTVALFAILURE,strValue,strName));
			}
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					 String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), StringConstants.STRNOSUCHELEEXCEP);
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					 String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue,  String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRTEXTINPEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue,  String.format(StringConstants.STREXPENTRINPUTVAL,strValue,strName), StringConstants.STRTEXTINPEXCEP.concat(StringConstants.STRCHKLOG));
		}
		return bReturnStatus;
	}


	
	/******************************************************************************************
	 * Function Name		:	klReadFromConfigurationProperties
	 * 
	 * Function Description :	This function is used to get the data from the configuration.properties file
	 * 						:	and returns the string.
	 * 
	 *******************************************************************************************/
	public String klReadFromConfigurationProperties(String strKey,boolean bDataSource)
			throws Exception {
		String strKeyValue =  StringUtils.EMPTY;
		try {
			strKeyValue = (bDataSource) ? super.fnGetParamValue(strKey) : strKey;
				if(!StringUtils.isNotBlank(strKeyValue))
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "",
						"","", String.format(StringConstants.STRTESTDATADBPARAMVALUE,strKey));
			
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(String.format(StringConstants.STRGENEXCEPCAUGHT,Thread.currentThread().getStackTrace()[1].getMethodName().toString()));
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		KeywordLibrary_LOGS.info(String.format("Value is '%s'",Initialization.pConfigFile.getProperty(strKeyValue)));
		return Initialization.pConfigFile.getProperty(strKeyValue);

	}
	
	
	
	
	
	
	/*******************************************************************************************************
	 * Function Name		:	klWebListSelect Function 
	 * Function Description	:	This Function selects the value from the List box 
	 * InPuts				:	strXpath - Xpath of the	WebList Box , strName - Logical Name of the WebList Box 
	 * 							strValue - Value to be selected from List box, 
	 * 							strDataSource - False(Value directly passed in the function), True(Value is to be retrieved from Datasheet) 
	 * 
	 ********************************************************************************************************/

	public boolean klWebListSelect(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnStatus = false;
		try {
			if(klIsElementPresentNoReport(strFindProperty)){
				if (bDataSource) {
					strValue = super.fnGetParamValue(strValue).trim().toString();
					KeywordLibrary_LOGS.info("StrValue :"+strValue);
				}
				Select select = new Select(this.wbElement);
				select.selectByVisibleText(strValue);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						strValue, String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), String.format(StringConstants.STRSHDSELECTLISTITMSUCCS,strValue,strName));
				bReturnStatus = true;
				this.wbElement=null;
			} else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						strValue, String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), String.format(StringConstants.STRSHDSELECTLISTITMFAILURE,strValue,strName));
			}

		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNCNOTSEEOBJ
					+ strName);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
		}
		return bReturnStatus;
	}


	/*******************************************************************************************************
	 * Function Name		:	klGetWebListDefaultOption Function
	 * 
	 * Function Description	:	This Function will gets the default selected value from the List box 
	 * 
	 * InPuts				:	strXpath - Xpath of the WebList Box
	 * 						:	strName - Logical Name of the WebList Box 
	 * 						:	strValue - default value selected in the List box, 
	 * 						:	strDataSource - False(Value directly passed in the function) / True(Value is to be retrieved from Data sheet) 
	 * 
	 ********************************************************************************************************/

	public boolean klGetWebListDefaultOption(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		String strActualValue;
		boolean bReturnType = false;
		try {
			strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
			if(!StringUtils.isNotBlank(strValue))
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strValue,
						String.format(StringConstants.STRDDLDEFVAL,strValue,strName), String.format(StringConstants.STREMPTYPARAMVAL,strValue,strName));
			else if (klIsElementPresentNoReport(strFindProperty)) {
				Select select = new Select(this.wbElement);
				strActualValue = select.getFirstSelectedOption().getText().toString().trim();
				bReturnType = StringUtils.equals(strActualValue, strValue);
				if(bReturnType)
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName,
						strValue, String.format(StringConstants.STRDDLDEFVAL,strValue,strName),
						String.format(StringConstants.STRDDLDEFVALSUCCS,strValue,strName));
				else
						ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName,strValue,
								String.format(StringConstants.STRDDLDEFVAL,strValue,strName),
								String.format(StringConstants.STRDDLDEFVALFAILURE,strValue,strName));
			}else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName,strValue,
						String.format(StringConstants.STRDDLDEFVAL,strValue,strName),
						StringConstants.STRNOSUCHELEEXCEP.concat(StringConstants.STRCHKLOG));
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName, strValue,
					String.format(StringConstants.STRDDLDEFVAL,strValue,strName),StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName,
					strValue, String.format(StringConstants.STRDDLDEFVAL,strValue,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnType, strName,
					strValue,  String.format(StringConstants.STRDDLDEFVAL,strValue,strName),
					String.format(StringConstants.STRGENEXCEPCAUGHT,Thread.currentThread().getStackTrace()[1].getMethodName()));
		}
		finally {
		this.wbElement = null;
		}
		return bReturnType;
	}

	/*******************************************************************************************************
	 * Function Name		:	klVerifyNumericValue Function 
	 * 
	 * Function Description	:	This function will verify whether the numeric value is equal to ZERO,
	 * 						:	(greater then ZERO)positive values, negative values (less than ZERO). 
	 * 						:	This function can be used when we need to test whether the given number 
	 * 						:	from the string contains value which is greater than less than, or equal to ZERO.  
	 * 						:	Based on the expectation, it will report the status of the test step to the report xml file.
	 * 
	 * 
	 * Input				:	strXpath - Xpath of the WebElement
	 * 						:	strName - Logical Name.
	 * 						:	EIntergerValues- Give the value as Enum type which shows all the 3 options
	 * 						:	iPosition - this is the position of the number that are present in the string, the position starts from 1,2 and so on.
	 * 
	 ********************************************************************************************************/

	public void klVerifyNumericValue(String strXpath, String strName, EIntergerValues eNumValue, int iPosition)
	throws Exception {
		String strActualValue = StringUtils.EMPTY;
		ArrayList<String> numArrayList =  new ArrayList<String>();
		String sNumber = StringUtils.EMPTY;
		try {
			if (klIsElementPresentNoReport(strXpath)) {
				numArrayList = klGetNumericValueFromGivenString(strXpath);
				if(numArrayList.size() == 0 && (eNumValue != EIntergerValues.BLANK)) {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
							StringConstants.STREXPSHDMATCHACTUAL, "Expected Net Price Value should not be Empty");
				}
				else if(eNumValue == EIntergerValues.BLANK) {
					strActualValue = "Expected Net Price value is :"+eNumValue.toString()+" and the actual Net Price value is : 'EMPTY or BLANK'";
					sNumber = EIntergerValues.BLANK.toString();
				}
				else {
					double iActualNum = Double.parseDouble(numArrayList.get(iPosition-1));
					KeywordLibrary_LOGS.info("Actual Number: "+iActualNum);
					if(iActualNum >0)
						sNumber = EIntergerValues.GREATERTHANZERO.toString();
					else if(iActualNum == 0)
						sNumber = EIntergerValues.EQUALTOZERO.toString();
					else if (iActualNum < 0)
						sNumber = EIntergerValues.LESSTHANZERO.toString();

					strActualValue = "Expected Net Price value is :"+eNumValue.toString()+" and the actual Net Price value is :"+iActualNum;
				}
				if(StringUtils.equalsIgnoreCase(sNumber, eNumValue.toString())){
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, eNumValue.getDescription().toString(),
							StringConstants.STREXPSHDMATCHACTUAL, sNumber);
				}
				else {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, eNumValue.getDescription().toString(),
							StringConstants.STREXPSHDMATCHACTUAL, sNumber);
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, eNumValue.getDescription().toString(),
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRNOSUCHELEEXCEP);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
		}
	}

	
	/*******************************************************************************************************
	 * Function Name - klClearTextField Function 
	 * Description -This function will reset or clears the values in the text field which are editable
	 * Value InputParam - strXpath - Xpath of the WebElement which can contain more than one XPaths in it
	 * separated by ";"
	 * XpathCollString - This is taken from the String value
	 * bDataSource - Boolean value for taking the values from the data base or not
	 * This function can be used when we need to clear the values which are already there in the text field 
	 * for eg) in the customer panel screen , we are having the text fields which are having values for the 
	 * customer address. This function will not report anything in the report xml since we are using this function 
	 * for internal use only. But the console will have debug trace details. so pls verify this if there is any issues 
	 * in clearing the values in the text field.
	 * 
	 ********************************************************************************************************/
	
	public void klClearTextField(String XpathCollString)
	throws Exception {
		String[] ipXpaths = null;
		try {
			 ipXpaths = StringUtils.split(XpathCollString,";");
			
			 for(String ipXpath : ipXpaths) {
				 if(klIsElementPresentNoReport(ipXpath)){
				 this.wbElement.clear();
				 KeywordLibrary_LOGS.info(ipXpath.concat(" Value is cleared"));
				 }
				 else
					 KeywordLibrary_LOGS.error(ipXpath.concat(" Value is not cleared"));
			 }
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		finally{
			KeywordLibrary_LOGS.info("Into finally block for KL : klClearTextField");
			ipXpaths = null;
		}
	}
	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyNumericValueXls Function 
	 * Input Parameter - strXpath - Xpath of the WebElement, strName - Logical Name.
	 * iPosition - this is the position of the number that are present in the string, the position starts from 1,2 and so on.
	 * Description - This function will verify whether the numeric value is equal to ZERO,(greater then ZERO)positive values,
	 * negative values (less than ZERO) or Blank.
	 * Pre-requisite to use this kl - In Excel, pass the following values based on your expected comparison : GREATERTHANZERO,EQUALTOZERO,LESSTHANZERO,BLANK
	 * Based on the expectation, it will report the status of the test step to the report xml file.
	 * (Be cautious : Type the exact value in excel from the options given in expected comparison)
	 ********************************************************************************************************/

	public void klVerifyNumericValueXls(String strXpath, String strName, String strValue,int iPosition)
	throws Exception {
		String strActualValue = StringUtils.EMPTY;
		ArrayList<String> numArrayList =  new ArrayList<String>();
		String sNumber = StringUtils.EMPTY;
		try {
			//Start1
			if (klIsElementPresentNoReport(strXpath)) {
				strValue = super.fnGetParamValue(strValue);
				KeywordLibrary_LOGS.info("strValue "+strValue);
				numArrayList = klGetNumericValueFromGivenString(strXpath);
				//Start2
				if(numArrayList.size() == 0 && (!StringUtils.equalsIgnoreCase(strValue.trim(),"BLANK"))) {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
							StringConstants.STREXPSHDMATCHACTUAL, "Net Price Value is Empty");
				}
				else if(StringUtils.equalsIgnoreCase(strValue.trim(),"BLANK")) {
					sNumber = "BLANK";
					strActualValue = String.format(StringConstants.STREXPNETPRCVAL, strValue,"'EMPTY' or 'BLANK'.");
				}
				else {
					double iActualNum = Double.parseDouble(numArrayList.get(iPosition-1));
					KeywordLibrary_LOGS.info("Actual Number: "+iActualNum);
					if(iActualNum >0)
						sNumber = "GREATERTHANZERO";
					else if(iActualNum == 0)
						sNumber = "EQUALTOZERO";
					else if (iActualNum < 0)
						sNumber = "LESSTHANZERO";
					strActualValue = String.format(StringConstants.STREXPNETPRCVAL, strValue,iActualNum);
				}
				//End2

				//Start3
				if(sNumber.equalsIgnoreCase(strValue)) {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
				}
				else {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
				}
				//End3
			}
			//End1
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRNOSUCHELEEXCEP);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
		}
	}	



	/*******************************************************************************************************
	 * Function Name - klVerifyHyperLinkStatusInWebElement Function 
	 * Description - This function gets the element hyper link status state and reports based on the expected 
	 * conditions 
	 * Value InputParam - strXpath - Xpath of the WebElement, strName - Logical Name.
	 * bExpectedHyperlinkStatus- Give the value true, if u need the hyperlink status to be present(available for the webelement),
	 * else set to false which will indicate that the web element should not contains the hyperlink status enable for that.
	 * This function can be used when we need to check whether the webelement contains any hyperlink or not and based on that we
	 * need to report the same to the report xml sheet as per the expected result.
	 * If we do not want to do reporting and we need only the hyperlink present or not status, then we SHOULD NOT USE this function at all.
	 * 
	 ********************************************************************************************************/

	public boolean klVerifyHyperLinkStatus(String strXpath, String strName,boolean bExpectedHyperlinkStatus)
			throws Exception {
		String strExpectedResult = StringUtils.EMPTY;
		String strActualResult = StringUtils.EMPTY;
		boolean bReturnStatus = false;
		try {
			if (klIsElementPresent(strXpath,strName)) {
				strActualResult = this.wbElement.getTagName().toString();
				KeywordLibrary_LOGS.info("actualValue "+strActualResult);
				boolean bActualHyperlink = StringUtils.equals(strActualResult,"A");
				strExpectedResult =  String.format(StringConstants.STREXPECTEDHYPERLINKSTATUS,bExpectedHyperlinkStatus);
				strActualResult = (bReturnStatus = (bActualHyperlink == bExpectedHyperlinkStatus)) ? String.format(StringConstants.STREXPECTEDHYPERLINKSTATUSSUCCESS,bExpectedHyperlinkStatus,bActualHyperlink) 
						: String.format(StringConstants.STREXPECTEDHYPERLINKSTATUSFAILURE,bExpectedHyperlinkStatus,bActualHyperlink);
				//This will report to the xml report file based on the expected and actual success/failure result report
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName, "Hyperlink",strExpectedResult, strActualResult);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "Hyperlink",
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"Hyperlink", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"Hyperlink", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC+ e.getMessage());
		}
		return bReturnStatus;
	}


	/*******************************************************************************************************
	 * Function Name - klGetNumericValueFromGivenString Function 
	 * Description - This function gets the will get the string as the input ad
	 * Value InputParam - strXpath - Xpath of the WebElement
	 * This function will return the integer, double(decimal),both positive and negative values that
	 * are present in the string. We just need to pass the Xpath for the element and it will return the
	 * strings of numbers which are present in the string. Later we need to convert the strings to int/float/decimal 
	 * values based on the requirement in the test cases.This will not report any failures to the report sheet. 
	 * This will just return only the list of digits which are present in the string.
	 * 
	 ********************************************************************************************************/

	public ArrayList<String> klGetNumericValueFromGivenString(String strXpath)
	throws Exception {
		String strValue = StringUtils.EMPTY;
		ArrayList<String> numListValues  =  new ArrayList<String>();
		try {
			if (klIsElementPresentNoReport(strXpath)) {
				strValue = this.wbElement.getText().toString();
				KeywordLibrary_LOGS.info("String Value is :"+strValue);
				//this pattern will match the integer values as well as decimal values which are both positive and negative
				Matcher numPattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?").matcher(strValue);
				while (numPattern.find()) {
					KeywordLibrary_LOGS.info(numPattern.group());
					numListValues.add(numPattern.group().toString());
				}
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			strValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			KeywordLibrary_LOGS.error(strValue);
		}
		return numListValues;
	}


	/*******************************************************************************************************
	 * Function Name - klVerifyStringContainsDigitsOrNot Function 
	 * Description - This function gets the element hyper link status state and reports based on the expected 
	 * conditions 
	 * Value InputParam - strXpath - Xpath of the WebElement, strName - Logical Name.
	 * bIsDigitPresent - make this as true ,if we need the string to contain the digits in it.
	 * else make this as false, this indicates that the string should not contain the digits in it.
	 * This function will report to the report xml file against your specification for the expected digit
	 * to present or not.
	 * This function will return whether the string contains the digit or not as a boolean value. based on this
	 * we can perform some of the test steps which will get the digits based on the presence of digit alone.
	 * 
	 ********************************************************************************************************/

	public boolean klVerifyStringContainsDigitsOrNot(String strXpath, String strName,boolean bIsDigitPresent)
	throws Exception {
		String strValue = StringUtils.EMPTY;
		boolean bStatusOfDigit = false;
		try {
			if (klIsElementPresentNoReport(strXpath)) {
				strValue = this.wbElement.getText().toString();
				KeywordLibrary_LOGS.info("actualValue "+strValue);
				Matcher numPattern  = Pattern.compile("-?[0-9]+(\\.[0-9]+)?").matcher(strValue);
				bStatusOfDigit = numPattern.find();
				if (bStatusOfDigit == bIsDigitPresent) {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							StringConstants.STREXPDIGITPRESENCETRUE, StringConstants.STRACTDIGITPRESENCETRUE.concat(strValue));
				} else {
					KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESNOTEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strValue,
							StringConstants.STREXPDIGITPRESENCETRUE, StringConstants.STRACTDIGITPRESENCEFALSE.concat(strValue));
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPDIGITPRESENCETRUE, String.format(StringConstants.STRSHDELEFOUNDFAILURE,strName));
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPDIGITPRESENCETRUE, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPDIGITPRESENCETRUE, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPDIGITPRESENCETRUE, StringConstants.STRFUNNOTEXEC+ e.getMessage());
		}
		return bStatusOfDigit;
	}


	/*******************************************************************************************************
	 * Function Name - klGetWebElementHyperlinkStatus Function 
	 * Description - This function gets the element hyper link status state and returns true or false based on 
	 * hyperlink status.
	 * Value InputParam - strXpath - Xpath of the WebElement, strName - Logical Name.
	 * This function can be used when we need to check whether the webelement contains any hyperlink or not
	 * This will not report anything in the report xml file since this is used as a return true or false condition alone
	 * 
	 ********************************************************************************************************/

	public boolean klGetHyperlinkStatus(String strXpath)
	throws Exception {
		String strActualValue = StringUtils.EMPTY;
		try {

			if (klIsElementPresentNoReport(strXpath)) {
				strActualValue = this.wbElement.getTagName().toString();
				KeywordLibrary_LOGS.info("actualValue "+strActualValue);
			}
		}
		catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
		}
		return strActualValue.equalsIgnoreCase("href");
	}

	/*******************************************************************************************************
	 * Function Name - klVerifyContainsTextPresent Function 
	 * Description - This function gets the text value for the given path and verifies with the Expected text
	 * Since this is the regular expression function ,this will test whether the expected string is present
	 * inside the string received from the Xpath or not alone.
	 * Value InputParam - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement, strExpValues - Value to be verified which is separated by ";".eg) if we need to test only word "test" ,
	 * then enter input value as "test;"-- This ";" only for word separations alone. so in the string expected value 
	 * is "test" instead of "test;" So need to makes sure that the value entered should be ended with the separator
	 * This function can be used when we need to check whether the string is present inside another string or not.
	 * Here the string denotes the complete word rather than the sub-word inside another word.  
	 * 
	 ********************************************************************************************************/

	public boolean  klVerifyContainsTextPresent(String strXpath, String strName,
			String strExpValues, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String actualValue = StringUtils.EMPTY;
		String [] strExpVal = null;
		try {
				strExpVal = (bDataSource)? StringUtils.split(super.fnGetParamValue(strExpValues),";") : StringUtils.split(strExpValues,";");
			if (klIsElementPresentNoReport(strXpath)) {
				actualValue = this.wbElement.getText().toString();
				// checks whether expected string present inside the actual string or not
				//by splitting the value based on the separator.
				int iCount = strExpVal.length;
				for(int i=0 ; i < iCount ; i++) {
					KeywordLibrary_LOGS.info("actualValue "+actualValue);
					KeywordLibrary_LOGS.info("expectedvalue "+strExpVal[i].toString());
					if (strExpVal[i].toString().trim().length() > 0 && actualValue.contains(strExpVal[i].toString())) {
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpVal[i].toString(),
								StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALSUCCS, strExpVal[i].toString(),actualValue));
						bReturnValue = true;
					} else {
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpVal[i].toString(),
								StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE, strExpVal[i].toString(),actualValue));
						bReturnValue=false;
					}
				}
			}
			else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch (Exception e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return bReturnValue;
	}
	/*******************************************************************************************************
	 * Function Name - klVerifyContainsTextNotPresent Function 
	 * Description - This function gets the text value for the given path and verifies with the Expected text
	 * not present inside the actual text value.
	 ********************************************************************************************************/
	
	public boolean  klVerifyContainsTextNotPresent(String strXpath, String strName,
			String strExpValues, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		String actualValue = StringUtils.EMPTY;
		String [] strExpVal = null;
		try {
			strExpVal = (bDataSource) ? StringUtils.split(super.fnGetParamValue(strExpValues),";") : StringUtils.split(strExpValues,";");
			if (klIsElementPresentNoReport(strXpath)) {
				strActualValue = this.wbElement.getText().toString();
				// checks whether expected string present inside the actual string or not
				//by splitting the value based on the separator.
				int iCount = strExpVal.length;
				for(int i=0 ; i < iCount ; i++) {
					KeywordLibrary_LOGS.info("ActualValue :"+actualValue);
					KeywordLibrary_LOGS.info("Expectedvalue :"+strExpVal[i]);
					if (!actualValue.contains(strExpVal[i])) {
						KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESNOTEQUAL);
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpVal[i],
								String.format(StringConstants.STREXPSHDNOTMATCHACTUAL,strExpVal[i],strActualValue),
								String.format(StringConstants.STREXPSHDNOTMATCHACTUALSUCCS,strExpVal[i],strActualValue));
						bReturnValue = true;
					} else {
						KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESEQUAL);
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpVal[i],
								String.format(StringConstants.STREXPSHDNOTMATCHACTUAL,strExpVal[i],strActualValue),
								String.format(StringConstants.STREXPSHDNOTMATCHACTUALFAILURE,strExpVal[i],strActualValue));
						bReturnValue = false;
					}
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
						StringConstants.STREXPSHDNOTMATCHACTUAL, StringConstants.STRNOSUCHELEEXCEP);
				bReturnValue = false;
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
					StringConstants.STREXPSHDNOTMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			bReturnValue = false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnValue = false;
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues, StringConstants.STREXPSHDNOTMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnValue = false;
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues, StringConstants.STREXPSHDNOTMATCHACTUAL, strActualValue);
		}
		return bReturnValue;
	}


	
	/*******************************************************************************************************
	 * Function Name - klCompareChildElemText Function 
	 * Description - This function used to get the child elements inside inside the parent node and compare the 
	 * text value of the child elements with the expected text values.
	 * Value InputParam - strXpath - Xpath of the WebElement-this is the parent Xpath to get the number of children
	 * element and iterate thru the node and get the values of the child element, strName - Logical Name
	 * of the WebElement, strExpValues - Value to be verified which is separated by ";".eg) if we need to test more than one words
	 * then enter input value as "test1;test2;.."-- This ";" only for word separations alone."
	 * So need to makes sure that the value entered should be ended with the separator
	 * Here the string denotes the complete word rather than the sub-word inside another word.  
	 * return : boolean type which tells whether the comparison is success or failure
	 * 
	 ********************************************************************************************************/

	public boolean  klCompareChildElemText(String strXpath, String strName,int iChildCount,
			String strExpValues, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String actualValue = StringUtils.EMPTY;
		String [] strExpVal = null;
		try {
				strExpVal = (bDataSource) ? StringUtils.split(super.fnGetParamValue(strExpValues),";") : StringUtils.split(strExpValues,";");
				//not sure why the first child index starts with number :1 rather than number :0
				//thats the reason the child count starts from 1 rather 0 in string format of element persence check
				for(int i=0;i<strExpVal.length;i++){
					if (klIsElementPresentNoReport(String.format(strXpath, i+1))) {
						actualValue = Initialization.driver.findElement(By.xpath(String.format(strXpath, i+1))).getText().toString();
						/*checks whether expected string matches with the actual string or not(This checks for the exact match only)
				by splitting the value based on the separator provided (";").*/

						KeywordLibrary_LOGS.info("ActualValue From Application:"+actualValue);
						KeywordLibrary_LOGS.info("ExpectedValue From Application:"+strExpVal[i].toString());
						if (StringUtils.equals(actualValue,strExpVal[i].toString())) {
							KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESEQUAL);
							ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpVal[i].toString(),
									StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALSUCCS,strExpVal[i].toString(),actualValue));
							bReturnValue = true;
						}else {
							KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESNOTEQUAL);
							ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpVal[i].toString(),
									StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE,strExpVal[i].toString(),actualValue));
							bReturnValue = false;
						}
					}
					else{
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
								StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRSHDNOTELEFOUND,strName));
					}
				}
		}catch (UnreachableBrowserException e) {
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
				RecoveryScn.recUnReachableTestScnExitTrue();
			}catch (WebDriverException e) {
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						strExpValues, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
			} catch (Exception e) {
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						strExpValues, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
			}
		finally{
			actualValue = StringUtils.EMPTY;
			strExpVal = null;
		}
		return bReturnValue;
	}

	

	/*******************************************************************************************************
	 * Function Name - klVerifyExactTextPresent Function 
	 * Description - This function will verify the exact text present in the actual string
	 * the actual string will be received from the Xpath Value
	 * Value InputParam - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement, strExpValue - String value that to be verified with the actual test String
	 * This function can be used when we need to test the presence of complete string in the application.
	 * Provide the complete string that need to be tested for the expected string and then check string with
	 * actual value by comparing the same for exact string value.
	 * 
	 ********************************************************************************************************/

	public boolean klVerifyExactTextPresent(String strXpath, String strName,
			String strExpValue, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		String actualValue = StringUtils.EMPTY;
		try {
			if(bDataSource && super.fnGetParamValue(strExpValue).trim().length() == 0){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
						StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRKEYVALUEMISSING,strExpValue));
			}else{
				strExpValue = bDataSource ? super.fnGetParamValue(strExpValue) : strExpValue;
				if (klIsElementPresentNoReport(strXpath)){
				actualValue = this.wbElement.getText().toString();
				// checks whether expected string and the actual string matches (exactly) completely or not
				KeywordLibrary_LOGS.info("actualValue "+actualValue);
				KeywordLibrary_LOGS.info("expectedvalue "+strExpValue);
				if (StringUtils.equals(actualValue,strExpValue)) {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpValue,
							StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALSUCCS, strExpValue,actualValue));
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESNOTEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpValue,
							StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE, strExpValue,actualValue));
				} }else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
		} }catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValue, StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
		}
		return bReturnValue;
	}

	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyExactTextsPresent Function 
	 * Description - This function will verify the exact text present in the actual string
	 * the actual string will be received from the Xpath Value.This will verify all the webelements text in 
	 * single case. Multiple web elements texts can be verified in this functionality.
	 * Value InputParam - strXpath(s) - Xpath of the WebElement, strName(s) - Logical Name
	 * of the WebElement, strExpValue(s) - String value that to be verified with the actual test String
	 * This function can be used when we need to test the presence of complete string in the application.
	 * Provide the complete string that need to be tested for the expected string and then check string with
	 * actual value by comparing the same for exact string value.
	 * 
	 ********************************************************************************************************/

	public boolean klVerifyExactTextsPresent(String strXpaths, String strName,
			String strExpValues, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		int iXpathCount,i =0;
		String actualValue = StringUtils.EMPTY;
		String[] strExpValue = null;
		String[] strXpath = null;
		try {
			if(bDataSource && super.fnGetParamValue(strExpValues).trim().length() == 0){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "", "",
						StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRKEYVALUEMISSING,strExpValues));
			}else{
				strExpValue = bDataSource ? StringUtils.split(super.fnGetParamValue(strExpValues),";") : StringUtils.split(strExpValues,";");
				strXpath = StringUtils.split(strXpaths,";");
				iXpathCount = strXpath.length;
				for(;i<iXpathCount;i++){
				if (klIsElementPresentNoReport(strXpath[i])){
				actualValue = this.wbElement.getText().toString();
				// checks whether expected string and the actual string matches (exactly) completely or not
				KeywordLibrary_LOGS.info("actualValue "+actualValue);
				KeywordLibrary_LOGS.info("expectedvalue "+strExpValue[i].toString());
				if (StringUtils.equals(actualValue,strExpValue[i].toString())) {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpValue[i].toString(),
							StringConstants.STREXPSHDMATCHACTUAL,  String.format(StringConstants.STREXPMATCHACTUALSUCCS,strExpValue[i].toString(), actualValue));
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESNOTEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpValue[i].toString(),
							StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE, strExpValue[i].toString(),actualValue));
					bReturnValue = false;
				} }else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue[i].toString(),
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
				bReturnValue = false;
				}
		} }}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRCHKLOG);
		}
		return bReturnValue;
	}

	
	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyStringValidationBtwStepsForEqualCount Function 
	 * Description - This function will verify the complete text present in the actual string
	 * the actual string will be received from the Xpath Value.s
	 * Value InputParam - strFirstStepXpath - Xpath Values of the first validation web page Elements,
	 * strSecondStepXpathValues - Xpath Values of the Second validation web page Elements,
	 * This function can be used when we need to test the presence of complete string in the application which
	 * can come from 2 different web pages. for eg) selecting customer address from one page and validation against the 
	 * details from the second page after selection.
	 * We need to pass two values in the array list and the reporting is done by comparing the 2 array list values against each other
	 * Only the exact matches has been given as a PASS result else it will result in FAIL conditions
	 * Before we use this method we need to pass the input parameters as a array list, so make sure that we are ready with the array list.
	 * 
	 ********************************************************************************************************/

	public boolean klVerifyStringValidationBtwScreenForEqualCount(ArrayList<String> strFirstStepXpathValues, ArrayList<String> strSecondStepXpathValues)
	throws Exception {
		boolean bReturnValue = false;
		String strFirstValue = StringUtils.EMPTY;
		String strSecondValue = StringUtils.EMPTY;
		try {
			// checks whether expected string and the actual string matches (exactly) completely or not
			if(strFirstStepXpathValues.size() == strSecondStepXpathValues.size()) {
				int iCount = strFirstStepXpathValues.size();
				for(int i=0;i<iCount;i++) {
					strFirstValue = strFirstStepXpathValues.get(i).toString();
					strSecondValue = strSecondStepXpathValues.get(i).toString();
					KeywordLibrary_LOGS.info("ActualValue :"+strFirstValue);
					KeywordLibrary_LOGS.info("Expectedvalue :"+strSecondValue);
					if (StringUtils.equals(strFirstValue,strSecondValue)) {
						KeywordLibrary_LOGS.info(StringConstants.STRVALUESEQUAL);
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strFirstValue, strSecondValue,
								StringConstants.STRCHKBTWNSCREEN, String.format(StringConstants.STRCHKBTWNSCREENSUCCS, strFirstValue,strSecondValue));
						bReturnValue = true;
					} else {
						KeywordLibrary_LOGS.info(StringConstants.STRVALUESNOTEQUAL);
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strFirstValue, strSecondValue,
								StringConstants.STRCHKBTWNSCREEN, String.format(StringConstants.STRCHKBTWNSCREENFAILURE, strFirstValue,strSecondValue));
						bReturnValue = false;
					}
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "FirstLevelValidation", "SecondLevelValidation",
						StringConstants.STRCOUNTEQUAL, StringConstants.STRCOUNTNOTEQUAL);
				bReturnValue = false;
			}
		} catch (UnreachableBrowserException e) {
			bReturnValue = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false,  "FirstLevelValidation", "SecondLevelValidation",
					StringConstants.STRCOUNTEQUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			bReturnValue = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false,  "FirstLevelValidation", "SecondLevelValidation",
					StringConstants.STRCOUNTEQUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			bReturnValue = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "FirstLevelValidation", "SecondLevelValidation",
					StringConstants.STRCHKBTWNSCREEN, StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
		}
		finally{}
			return bReturnValue;
	}

	/*******************************************************************************************************
	 * Function Name - klGetTextFromWebElement Function 
	 * Description - This function gets the value from the Xpath and store it in the array.
	 * The return statement is the array list. This we can use to get the list of xpath values and 
	 * use it for validation purpose. for example,we can use this method as a input parameter to the klVerifyStringValidationBtwSteps() 
	 * method.
	 * To use this method, input the xpath strings each separated by ";" separator
	 * Value InputParam - strXpathCollectionList - collection of Xpath to get the string value from it.
	 * This will report only FAIL conditions, ie) when the element is not there condition and does not report PASS conditions
	 * This function can be used, if u need to collect the collection of strings for a collection(set) of xpath values
	 * then pass the key as the collection list, then the value as the collection of xpath which is separated by ";".
	 * once we do this, then we can get the collections  of set of strings which are corresponding to each xpath.
	 * 
	 ********************************************************************************************************/

	public ArrayList<String> klGetTextFromWebElement(String strXpathCollectionKey)
	throws Exception {
		String strValue = StringUtils.EMPTY;
		this.strXpathValues.clear();
		try {
			//Just retrieve the string from the XPath web element
			String [] strXpath = StringUtils.split(super.fnGetParamValue(strXpathCollectionKey),";");
			int iCount = strXpath.length;
			
			for(int i=0;i < iCount;i++) {
				if(klIsElementPresentNoReport(strXpath[i].toString())) {
					strValue = this.wbElement.getText().toString();
					strValue = StringUtils.isNotBlank(strValue) ? strValue: "EmptyValueForArray" ;
					KeywordLibrary_LOGS.info("Xpath String :"+strXpath[i].toString()+" And Its Xpath Value is :"+strValue);
					this.strXpathValues.add(strValue);
					KeywordLibrary_LOGS.info("Array list of index Value :" +i+" is :"+ this.strXpathValues.get(i).toString());
				}
				else
				{
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strXpathCollectionKey, strXpath[i].toString(), 
							StringConstants.STRSHDELEFOUND, StringConstants.STRSHDELEFOUNDFAILURE);
				}
			}
		}
		catch(Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+e.getMessage());
		}
		return this.strXpathValues;
	}

	
	
	/*******************************************************************************************************
	 * Function Name - klGetTextFromWebElementNoReport Function 
	 * Description - This function gets the text value for the given path and returns the string to the 
	 * calling function.This will not report anything in the xml file. This will not work for textbox element
	 * Value InPuts - strXpath - Xpath of the WebElement, strName - Logical Name
	 *
	 ********************************************************************************************************/
	public String klGetTextFromWebElementNoReport(String strFindProperty,int iWaitTime)
			throws Exception {
		String strValue = StringUtils.EMPTY;
		try{
		if (klIsElementPresentNoReportExplicitWait(strFindProperty,iWaitTime)) {
				strValue = this.wbElement.getText().trim().toString();
		KeywordLibrary_LOGS.info("value is:"+strValue);
		}
		}catch(Exception e){
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return strValue;
	}


	/*******************************************************************************************************
	 * Function Name - klVPWebElementText Function 
	 * Description - This function
	 * gets the text value for the given path and verifies with the Expected
	 * Value InPuts - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement strValue - Value to be verified, strDataSource -
	 * False(Value directly passed in the function), True(Value is to be
	 * retrieved from Datasheet) 
	 * This function will verify the actual and the expected result in 2 ways in either or condition.
	 * 1) if the actual and expected are exactly matching (OR)
	 * 2) if the actual and expected are same and expected contains inside the actual string retreived from the Xpath 
	 ********************************************************************************************************/
	public boolean klVPWebElementText(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnStatus = false;
		try {
			String actualValue = StringUtils.EMPTY;
			if(bDataSource && StringUtils.isBlank(super.fnGetParamValue(strValue))){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRKEYVALUEMISSING,strValue));
			}else{
				strValue = bDataSource ? super.fnGetParamValue(strValue): strValue;
				if (klIsElementPresentNoReport(strFindProperty)) {
				actualValue = this.wbElement.getText().trim().toString();
				// compares the expected value and the actual value
				KeywordLibrary_LOGS.info("actualValue :"+actualValue);
				KeywordLibrary_LOGS.info("expectedvalue :"+strValue);
				if (StringUtils.contains(strValue,actualValue) || StringUtils.equals(strValue,actualValue)) {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALSUCCS,strValue,actualValue));
					bReturnStatus = true;
					this.wbElement=null;
				} else {
					KeywordLibrary_LOGS.info(StringConstants.STRVALUESNOTEQUAL);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE,strValue,actualValue));
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
			}
				}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue,  StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRCHKLOG);
		}
		return bReturnStatus;
	}

	
	/*******************************************************************************************************
	 * Function Name - klVerfiyMultipleWebElementsValue Function 
	 * Description - This function gets the text value for the given path and verifies with the Expected
	 * Value InPuts - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement strValue - Value to be verified, strDataSource -
	 * False(Value directly passed in the function), True(Value is to be
	 * retrieved from Datasheet) 
	 * This function will verify the actual and the expected result 
	 * 1) if the actual and expected are exactly matching  
	 * 
	 ********************************************************************************************************/
	public boolean klVerfiyMultipleWebElementsValue(String strXpaths, String strName,
			String strExpValues, boolean bDataSource) throws Exception {
		boolean bReturnStatus = false;
		try {
			String[] strExpValue = null;
			String[] strXpathCollection = StringUtils.split(strXpaths,";");
			String actualValue,actualReport = StringUtils.EMPTY;
			int iValueCount =0;
			if(bDataSource && !StringUtils.isNotBlank(super.fnGetParamValue(strExpValues))){
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
						StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRKEYVALUEMISSING,strExpValues));
			}else{
				strExpValue = bDataSource ? StringUtils.split(super.fnGetParamValue(strExpValues),";"): StringUtils.split(strExpValues,";");
				for (String strXpath : strXpathCollection) {
				if (klIsElementPresentNoReport(strXpath)) {
					//Set the reporting variables and then pass as reporting function parameter
					actualValue = this.wbElement.getText().trim().toString();
					//The item can present inside the array irrespective of positions
					bReturnStatus = Arrays.asList(strExpValue).contains(actualValue);
					actualReport = bReturnStatus ? String.format(StringConstants.STREXPMATCHACTUALSUCCS,strExpValue[iValueCount],actualValue) :
						String.format(StringConstants.STREXPMATCHACTUALFAILURE,strExpValue[iValueCount],actualValue);
					//passing the setted reporting variables into the reporting function
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName, strExpValue[iValueCount],
							StringConstants.STREXPSHDMATCHACTUAL, actualReport);
			}else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue[iValueCount],
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
				iValueCount ++;
		}}}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValues,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValues,  StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRCHKLOG);
		}
		return bReturnStatus;
	}

	
	/*******************************************************************************************************
	 * Function Name - klGetAttriValueFromWebElement 
	 * Input Parameter - strXpathCollectionKey(In CSV format; CSV : xpath strings each separated by ";" separator)
	 * String strNames = This is the logical name for set of values which is passed for XPath.
	 * Description - This function gets the value from the Xpath/Xpaths and store it in the ArrayList<String>.
	 * Returns -  ArrayList<String> of the values ,retrieved from the Xpath/Xpaths.for example,we can use this method as a input parameter to the klVerifyStringValidationBtwSteps() 
	 * Note - 
	 * We can use this method as a input parameter to the klVerifyStringValidationBtwSteps() 
	 * This will report only FAIL conditions, ie) when the element is not there condition and does not report for PASS conditions.
	 ********************************************************************************************************/

	public ArrayList<String> klGetAttriValueFromWebElement(String strXpathCollectionKey,String strNames)
	throws Exception {
		String strValue = StringUtils.EMPTY;
		this.strXpathValues.clear();
		try {
			//Just retrieve the string from the XPath web element
			String [] strXpath = StringUtils.split(strXpathCollectionKey,";");
			String [] strName = StringUtils.split(strNames,";");
			int iCount = strXpath.length;
			this.strXpathValues.ensureCapacity(iCount);
			for(int i=0;i < iCount;i++) {
				if(klIsElementPresentNoReport(strXpath[i].toString())) {
					//We are using getAttribute("Value") to get the value from the text box since the getText() will not fetch the values
					//from the text box(INPUT Type).
					this.wbElement.getAttribute("value").toString();
					strValue = this.wbElement.getAttribute("value").toString();
					strValue = StringUtils.isNotBlank(strValue) ? strValue : "EmptyValue" ;
					KeywordLibrary_LOGS.info("Xpath String :"+strXpath[i].toString()+" And Its Xpath Value is :"+strValue);
					this.strXpathValues.add(strValue);
				}
				else
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false,strName[i].toString(), "",
							StringConstants.STRSHDELEFOUND, StringConstants.STRSHDELEFOUNDFAILURE);
			}
		}
		catch(Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+e.getMessage());
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "ExceptionOccured","", 
						"", StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
		}
		return this.strXpathValues;
	}

	
	/*******************************************************************************************************
	 * Function Name - klVPPlaceHolderValFrmWebElem 
	 * Input Parameter - Xpath for the placeholder web element
	 * Description - This function gets the placeholder value from the Xpath
	 * Returns -  String value which is the value for the place holder 
	 * This function can be used when we need to get the placeholder value for any input fields , for example in search parts screen
	 * we haev the search window pop up when we click on add parts button, there we can see the parts search input text box contains the 
	 * place holder values as "Parts Number", "Parts description", "EEE Code" as a value in it.  
	 * This will report PASS and FAIL conditions based on the comparision value
	 * 
	 ********************************************************************************************************/

	public boolean klVPPlaceHolderValFrmWebElem(String strXpath,String strName,String strExpValue, boolean bDataSource)
			throws Exception {
		String strActValue = StringUtils.EMPTY;
		boolean bReturnStatus = false;
		try {
			strExpValue = (bDataSource) ? super.fnGetParamValue(strExpValue).toString() : strExpValue;
			if(klIsElementPresent(strXpath,strName)) {
				strActValue = Initialization.driver.findElement(By.xpath(strXpath)).getAttribute("placeholder").toString();
				KeywordLibrary_LOGS.info("Actual Value :"+strActValue);
				
				if(StringUtils.equals(strExpValue,strActValue)){
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpValue,
							String.format(StringConstants.STRPLACEHOLDEREXPVAL,strExpValue,strName), String.format(StringConstants.STRPLACEHOLDEREXPVALSUCCS,strExpValue,strActValue));
					bReturnStatus = true;
				}else
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
							String.format(StringConstants.STRPLACEHOLDEREXPVAL,strExpValue,strName), String.format(StringConstants.STRPLACEHOLDEREXPVALFAILURE,strExpValue,strActValue));
			}else
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
						String.format(StringConstants.STRPLACEHOLDEREXPVAL,strExpValue,strName), StringConstants.STRSHDELEFOUNDFAILURE);
		}
		catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					String.format(StringConstants.STRPLACEHOLDERVAL,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strExpValue, String.format(StringConstants.STRPLACEHOLDERVAL,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpValue,
					String.format(StringConstants.STRPLACEHOLDERVAL,strName), StringConstants.STRCHKLOG);
		}
		return bReturnStatus;
	}

	/*******************************************************************************************************
	 * Function Name - klVPGetTxtBxAttrVal Function 
	 * Description - This function gets the text value entered in a Text field by fetching value stored in 
	 * value attribute and verifies with the Expected ( This function is used 
	 * since gettext is not able to fetch the text stored in a text box)
	 * Value InPuts - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement strValue - Value to be verified, strDataSource -
	 * False(Value directly passed in the function), True(Value is to be
	 * retrieved from Datasheet) ,
	 *  isExactMatch -False when we need to verify UI contains the expected text ,
	 * true - when we need to verify UI matches exactly with the Expected text
	 * This function will verify the actual and the expected result in 2 ways in either or condition.
	 * 1) if the actual and expected are exactly matching (OR)
	 * 2) if the actual and expected are same and expected contains inside the actual string retreived from the Xpath 
	 ********************************************************************************************************/
	public boolean klVPGetTxtBoxAttrVal(String strFindProperty, String strName,
			String strValue, boolean bDataSource, boolean isExactMatch) throws Exception {
		boolean bReturnValue = false;
		String strExpectedValue = "Expected value should match with actual value";
		String strActualValue = StringUtils.EMPTY;

		try {
			String actualValue = StringUtils.EMPTY;
				strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
			if (klIsElementPresentNoReport(strFindProperty)) {
				actualValue = this.wbElement.getAttribute("value").toString();
				// compares the expected value and the actual value
				KeywordLibrary_LOGS.info("actualValue "+actualValue);
				KeywordLibrary_LOGS.info("expectedvalue "+strValue);
				if(isExactMatch)
				{
					KeywordLibrary_LOGS.info("\t Actual Value should match exactly with the Expected Value");
					bReturnValue = StringUtils.equals(strValue,actualValue) ;
					strActualValue = String.format("Expected Value '%s' '%s' the Actual Value '%s'",strValue,bReturnValue ? "MATCHES" : "DOES NOT MATCHES",actualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnValue, strName, strValue,strExpectedValue, strActualValue);
					this.wbElement=null;
					return bReturnValue;

				}
				if (StringUtils.equals(strValue,actualValue) || StringUtils.contains(actualValue,strValue)) {
					KeywordLibrary_LOGS.info("\t Values are Equal");
					strActualValue = String.format(StringConstants.STREXPMATCHACTUALSUCCS,strValue,actualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							strExpectedValue, strActualValue);
					this.wbElement=null;
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info("\t Values are Not Equal");
					strActualValue = String.format(StringConstants.STREXPMATCHACTUALFAILURE,strValue,actualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							strExpectedValue, strActualValue);
					bReturnValue = false;
				}	}

			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						strExpectedValue, StringConstants.STRNOSUCHELEEXCEP);
				bReturnValue = false;
			}
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error("Unreachable Browser Exception hence following steps and BPC are skipped");
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			bReturnValue = false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			strActualValue = "Function Not able to execute due to following error: "
				+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			bReturnValue = false;
		}
		return bReturnValue;

	}


	/*******************************************************************************************************
	 * Function Name - klVPWebElementTextExistAndExit Function 
	 * Description - This function
	 * gets the text value for the given path and verifies with the Expected
	 * Value . In case the values are not equal then call recovery scenario
	 * InPuts - strXpath - Xpath of the WebElement, strName - Logical Name
	 * of the WebElement strValue - Value to be verified, strDataSource -
	 * False(Value directly passed in the function), True(Value is to be
	 * retrieved from Datasheet) 
	 ********************************************************************************************************/


	public boolean klVPWebElementTextExistAndExit(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		try {
			String actualValue = StringUtils.EMPTY;
				strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
			if (klIsElementPresentNoReport(strFindProperty)) {
				actualValue = this.wbElement.getText().toString();
				// compares the expected value and the actual value
				KeywordLibrary_LOGS.info("actualValue "+actualValue);
				KeywordLibrary_LOGS.info("expectedvalue "+strValue);
				if (StringUtils.equals(strValue,actualValue)|| StringUtils.contains(actualValue,strValue)) {
					KeywordLibrary_LOGS.info("\t Values are Equal");
					strActualValue = String.format(StringConstants.STREXPMATCHACTUALSUCCS, strValue,actualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info("\t Values are Not Equal");
					bReturnValue = false;
					strActualValue = "Expected value "+strValue+" did not match with actual value "+actualValue+ ". Mandatory object not present on the webpage hence following steps and BPC are skipped";
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
					RecoveryScn.recTestScnExitTrue();
				}
			}
			else{
				bReturnValue = false;
				strActualValue="Mandatory object not present on the webpage hence following steps and BPC are skipped";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
				RecoveryScn.recTestScnExitTrue();
			}
			this.wbElement=null;
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			bReturnValue = false;
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			bReturnValue = false;
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue,  StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC+ e.getMessage());
		}
		return bReturnValue;
	}
	
	
	/*******************************************************************************************************
	 * Function Name - klVPAttributeInTextField Function 
	 * Description - This
	 * function verifies for the attribute present in the text field InPuts -
	 * strXpath - Xpath of the WebElement, strName - Logical Name of the
	 * WebElement strValue - Value to be verified, strDataSource - False(Value
	 * directly passed in the function), True(Value is to be retrieved from
	 * Datasheet) 
	 ********************************************************************************************************/


	public boolean klVPAttributeInTextField(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		try {
			String actualValue = StringUtils.EMPTY;
			if (klIsElementPresent(strFindProperty, strName)) {
					strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
				actualValue = this.wbElement.getText();
				// compares the expected value and the actual value
				if (StringUtils.equalsIgnoreCase(strValue,actualValue)) {
					strActualValue = String.format(StringConstants.STREXPMATCHACTUALSUCCS, strValue,actualValue);
					KeywordLibrary_LOGS.info(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
					bReturnValue = true;
				} else {
					strActualValue = String.format(StringConstants.STREXPMATCHACTUALFAILURE, strValue,actualValue);
					KeywordLibrary_LOGS.info(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
					bReturnValue = false;
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
				bReturnValue = false;
			}
			this.wbElement=null;
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
			bReturnValue = false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			bReturnValue = false;
		}
		return bReturnValue;

	}

	/*******************************************************************************************************
	 * Function Name - klVPAttributeInTextFieldNotEqual Function 
	 * Description - This
	 * function verifies for the attribute present in the text field is not equal to value been passed. InPuts -
	 * strXpath - Xpath of the WebElement, strName - Logical Name of the
	 * WebElement strValue - Value to be verified, strDataSource - False(Value
	 * directly passed in the function), True(Value is to be retrieved from
	 * Datasheet) 
	 ********************************************************************************************************/

	public boolean klVPAttributeInTextFieldNotEqual(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		String strExpectedValue = "Expected Value should not present in the Actual Value";
		try {
			String actualValue = StringUtils.EMPTY;
			if (klIsElementPresent(strFindProperty, strName)) {
					strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
				actualValue = this.wbElement.getAttribute("@value");
				// compares the expected value and the actual value
				if (!(StringUtils.equalsIgnoreCase(strValue,actualValue))) {
					KeywordLibrary_LOGS.info("\t Expected value "+strValue+" did not match with actual value "+actualValue);
					strActualValue = "Expected value "+strValue+" did not match with actual value "+actualValue;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strValue,
							strExpectedValue, strActualValue);
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info("\t Expected Value "+strValue+" matched with the actual value "+actualValue);
					strActualValue = "Expected Value "+strValue+" matched with the actual value "+actualValue;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
							strExpectedValue, strActualValue);
					bReturnValue = false;
				}
			}
			else{
				strActualValue="Element not found on the web page";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						strExpectedValue, strActualValue);
				bReturnValue = false;
			}
			this.wbElement=null;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			bReturnValue = false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			strActualValue = "Function Not able to execute due to following error: "
				+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			bReturnValue = false;
		}
		return bReturnValue;

	}


	/*******************************************************************************************************
	 * Function Name - klVPAttributeInTextFieldNotEqualSplit Function 
	 * Description - This function verifies for the attribute present in the text field is not equal to value been passed. 
	 * InPuts - strXpath - Xpath of the WebElement, strName - Logical Name of the
	 * WebElement strValue - Value to be verified, strDataSource - False(Value
	 * directly passed in the function), True(Value is to be retrieved from Datasheet) 
	 ********************************************************************************************************/

	public boolean klVPAttributeInTextFieldNotEqualSplit(String strFindProperty, String strName,
			String strValue, boolean bDataSource) throws Exception {
		boolean bReturnStatus = false;
		String strActualResult;
		try {
			String actualValue = "";

			if (klIsElementPresent(strFindProperty, strName)) {
					strValue = (bDataSource )? super.fnGetParamValue(strValue) : strValue;
				actualValue = this.wbElement.getText();

				if(StringUtils.upperCase(actualValue).contains("USD"))
					actualValue=klGetNumericValueFromGivenString(strFindProperty).get(0);

				// compares the expected value and the actual value
				strActualResult = (bReturnStatus = StringUtils.equalsIgnoreCase(strValue, actualValue) ) ? String.format(StringConstants.STREXPMATCHACTUALSUCCS, strValue,actualValue) :
					String.format(StringConstants.STREXPMATCHACTUALFAILURE, strValue,actualValue);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName, strValue,StringConstants.STREXPSHDMATCHACTUAL, strActualResult);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRCHKLOG);
		}
		return bReturnStatus;

	}

	/*******************************************************************************************************
	 * Function Name - klVPcheckboxRadioBtnChecked Function 
	 * Description - This
	 * Function verifies if the checkbox/radio button is selected or not 
	 * InPuts- strXpath - Xpath of the WebElement, strName - Logical Name of the
	 * WebElement strValue - True(To verify selected) or False(To verify Not
	 * selected), strDataSource - False(Value directly passed in the function),
	 * True(Value is to be retrieved from Datasheet) 
	 ********************************************************************************************************/
	public boolean klVPcheckboxRadioBtnChecked(String strFindProperty,
			String strName, String strValue, boolean bDataSource)
	throws Exception {
		boolean bReturnValue = false;
		String strExpectedValue = "Expected value should match with actual value";
		String strActualValue;
		boolean bExpectedValue = false;
		try {
			if (klIsElementPresent(strFindProperty, strName)) {
					strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
				boolean bActualValue = this.wbElement.isSelected();
				if (StringUtils.equalsIgnoreCase(strValue,"true")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					if (bExpectedValue == bActualValue) {
						KeywordLibrary_LOGS.info("\t Checkbox or Radio button is selected, as expected");
						strActualValue = "Checkbox or Radio button is selected, as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						bReturnValue = true;
					} else {
						KeywordLibrary_LOGS.info("\t Checkbox or Radio button is NOT selected");
						strActualValue = "Checkbox or Radio button is NOT selected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						bReturnValue = false;
					}
				}
				if (StringUtils.equalsIgnoreCase(strValue,"false")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					if (bExpectedValue == bActualValue) {
						KeywordLibrary_LOGS.info("\t Checkbox or Radio button is NOT selected,as expected");
						strActualValue = "Checkbox or Radio button is not selected, as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						bReturnValue = true;
					} else {
						KeywordLibrary_LOGS.info("\t Checkbox or Radio button is selected");
						strActualValue = "Checkbox or Radio button is selected, not as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						bReturnValue = false;
					}
				}
			}
			else{
				strActualValue="Element not found on the web page";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						strExpectedValue, strActualValue);
				bReturnValue = false;
			}
			this.wbElement=null;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			strActualValue = "Function Not able to execute due to following error: "
				+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			bReturnValue = false;
		}
		return bReturnValue;
	}
	/*******************************************************************************************************
	 * Function Name - klGetCurrentDate 
	 * Function Description - This function
	 * gets the current system date 
	 ********************************************************************************************************/
	public String klGetCurrentDate() throws Exception {
		String strCurrentDate = " ";
		try {
			DateFormat dfFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date dtToday = Calendar.getInstance().getTime();
			String strReportDate = dfFormat.format(dtToday);
			KeywordLibrary_LOGS.info("\t Report Date: " + strReportDate);
			final String[] strExpectedCurrentDate = StringUtils.split(strReportDate,"/");
			StringBuffer strBuffActResb = new StringBuffer();
			strBuffActResb.append(strExpectedCurrentDate[1]);
			strCurrentDate = strBuffActResb.toString();
			KeywordLibrary_LOGS.info("\t CurrentDate: " + strCurrentDate);
		}  catch (Exception e) {
			KeywordLibrary_LOGS.error("\t Failed to Get the Current System Date.");
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return strCurrentDate;
	}


	
	/*******************************************************************************************************
	 * Function Name - klWebElementSetCheckboxOn 
	 * Function Description - This function sets the Check box on 
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/

	public boolean klWebElementSetCheckboxOn(String strFindProperty, String strName) {
		boolean bReturnStatus = false;
		try {
			if(klIsElementPresentNoReport(strFindProperty)){
			if (!this.wbElement.isSelected()) {
					this.wbElement.click();
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true,strName,"",String.format(StringConstants.STREXPCHKBOXSTATEON, strName), 
							String.format(StringConstants.STREXPCHKBOXSTATEONSUCCS, strName));
					this.wbElement=null;
					bReturnStatus = true;
				} else {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true,strName, "", String.format(StringConstants.STREXPCHKBOXSTATEON, strName),
							String.format(StringConstants.STREXPCHKBOXSTATEON, strName));
				}
			} else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,"",
						String.format(StringConstants.STREXPCHKBOXSTATEON, strName),StringConstants.STRNOSUCHELEEXCEP);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			bReturnStatus = false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STREXPCHKBOXSTATEON,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STREXPCHKBOXSTATEON,strName), StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}

	/*******************************************************************************************************
	 * Function Name - klVerifyUserExpectedCheckboxState 
	 * Function Description - This function will verify that the checkbox value is as equal to the user expected value
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue = the value which can be either true or false based on the user expectation.
	 * bDataSource = data value to be taken from the test data excel sheet
	 ********************************************************************************************************/

	public boolean klVerifyUserExpectedCheckboxState(String strFindProperty, String strName,String strValue ,boolean bDataSource) {
		String strActualValue;
		boolean bReturnStatus = false;
		boolean bActualUICheckboxState;
		boolean bUserExpectedCheckboxState;
		try {
			strValue = (bDataSource) ? super.fnGetParamValue(strValue) : strValue;
			 bUserExpectedCheckboxState=Boolean.parseBoolean(strValue);
			//this 'isSelected()'will verify only the selected state or not and will not check whether the checkbox is enabled/disabled
			
				if (klIsElementPresentNoReport(strFindProperty)) {
					 bActualUICheckboxState = this.wbElement.isSelected();
					 strActualValue = (bReturnStatus = (bActualUICheckboxState == bUserExpectedCheckboxState)) ? String.format(StringConstants.STREXPECTEDCHECKBOXSTATESUCCESS,bUserExpectedCheckboxState,bActualUICheckboxState): 
						 String.format(StringConstants.STREXPECTEDCHECKBOXSTATEFAILURE,bUserExpectedCheckboxState,bActualUICheckboxState) ;
					 ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus,
								strName, strValue, StringConstants.STREXPECTEDCHECKBOXSTATE,strActualValue);
				}
				else ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
						strName, strValue, StringConstants.STREXPECTEDCHECKBOXSTATE, StringConstants.STRSHDELEFOUNDFAILURE);
				
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPECTEDCHECKBOXSTATE, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPECTEDCHECKBOXSTATE, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					StringConstants.STREXPECTEDCHECKBOXSTATE, StringConstants.STRCLKELEEXCEP);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}
	
	
	/*******************************************************************************************************
	 * Function Name - klWebElementSetCheckboxOn 
	 * Function Description - This function sets the Check box off 
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * Xpath can also be given in comma sepated form
	 ********************************************************************************************************/

	public boolean klWebElementSetCheckboxOff(String strFindProperty, String strName) {
		boolean bReturnStatus = false;
		String[] ipXpaths = null;
		try {
			ipXpaths = StringUtils.split(strFindProperty,";");

			for(String ipXpath : ipXpaths){
				
			if(klIsElementPresentNoReport(ipXpath)){
			if (this.wbElement.isSelected()) {
					this.wbElement.click();
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true,strName,"",String.format(StringConstants.STREXPCHKBOXSTATEOFF, strName), 
							String.format(StringConstants.STREXPCHKBOXSTATEOFFSUCCS, strName));
					this.wbElement=null;
					bReturnStatus = true;
				} else {
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true,strName, "", String.format(StringConstants.STREXPCHKBOXSTATEOFF, strName),
							String.format(StringConstants.STRCHKBOXSTATEISOFF, strName));
				}
			} else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,"",
						String.format(StringConstants.STREXPCHKBOXSTATEOFF, strName),StringConstants.STRNOSUCHELEEXCEP);
			}
			}
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			bReturnStatus = false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STREXPCHKBOXSTATEOFF,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					String.format(StringConstants.STREXPCHKBOXSTATEOFF,strName), StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}

	/*******************************************************************************************************
	 * Function Name - klWebImgClick 
	 * Function Description - This function Clicks the Image 
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/

	public boolean klWebImgClick(String strFindProperty, String strName) {
		String strExpectedValue = "Expected to click the Image";
		String strActualValue;
		try {
			if (klIsElementPresent(strFindProperty, strName)) {
				this.wbElement.click();
				strActualValue = "Clicked the Image Successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				this.wbElement=null;
				return true;
			}
			strActualValue = "Did not find the Image on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			return false;
		} catch (Exception e) {
			strActualValue = "Exception occurred while trying to click the image";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, strActualValue);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			return false;
		}
	}

	/*******************************************************************************************************
	 * Function Name - klVPWebElementCheckBoxCheck 
	 * Function Description - This function verifies whether the Check box is on 
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue - Value to be entered in the Edit box, strDataSource - False(Value directly passed
	 * in the function), True(Value is to be retrieved from Datasheet) 
	 * 
	 ********************************************************************************************************/

	public boolean klVPWebElementCheckBoxCheck(String strXpath, String strName,
			String strValue, boolean bDataSource) {
		String strExpectedValue = "Expected to have the checkbox checked";
		String strActualValue = StringUtils.EMPTY;
		boolean bRtnValue = false;
		boolean bExpectedValue = false;
		boolean bActualValue = false;
		try {
			if (klIsElementPresent(strXpath, strName)) {
					strValue = bDataSource ?super.fnGetParamValue(strValue) :strValue; 
				bActualValue = this.wbElement.isSelected();
				if (StringUtils.equalsIgnoreCase(strValue,"true")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					KeywordLibrary_LOGS.info("expectedValue "+bExpectedValue);
					KeywordLibrary_LOGS.info("actualValue "+bActualValue);
					if (bExpectedValue == bActualValue) {
						strActualValue = "Checkbox is selected, as expected";
						KeywordLibrary_LOGS.info("\t Checkbox is selected, as expected");
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						bRtnValue = true;
					} else {
						strActualValue = "Checkbox is not selected, as expected";
						KeywordLibrary_LOGS.info("\t Checkbox is NOT selected");
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						bRtnValue = false;
					}
				}
				if (StringUtils.equalsIgnoreCase(strValue,"false")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					if (bExpectedValue == bActualValue) {
						strActualValue = "Checkbox is not selected, as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						KeywordLibrary_LOGS.info("\t Checkbox is NOT selected,as expected");
						bRtnValue = true;
					} else {
						strActualValue = "Checkbox is selected,not as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						KeywordLibrary_LOGS.info("\t Checkbox is selected");
						bRtnValue = false;
					}
				}
			}
			else{
				strActualValue="Element not found on the web page";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						strExpectedValue, strActualValue);
				bRtnValue = false;
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			bRtnValue = false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSCHKSTATEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRSCHKSTATEXCEP);
			bRtnValue = false;
		}
		return bRtnValue;
	}

	/*******************************************************************************************************
	 * Function Name - klVPWebChkNotChk 
	 * Function Description - This function verifies whether the Check box is off 
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue - Value to be entered in the Edit box, strDataSource - False(Value directly passed
	 * in the function), True(Value is to be retrieved from Datasheet) 
	 * 
	 ********************************************************************************************************/

	public boolean klVPWebChkNotChk(String strXpath, String strName,
			String strValue, boolean bDataSource) {
		String strExpectedValue = "Expected to have the checkbox checked";
		String strActualValue;
		boolean bRtnValue = false;
		boolean bActualValue =false;
		boolean bExpectedValue = false;
		try {
			if (klIsElementPresent(strXpath, strName)) {
					strValue =  bDataSource ?super.fnGetParamValue(strValue) : strValue;
				bActualValue = this.wbElement.isSelected();

				if (StringUtils.equalsIgnoreCase(strValue,"false")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					if (bExpectedValue == bActualValue) {
						strActualValue = "Checkbox is not selected, as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						KeywordLibrary_LOGS.info("\t Checkbox is NOT selected,as expected");
						bRtnValue = true;
					} else {
						strActualValue = "Checkbox is selected,not as expected";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						KeywordLibrary_LOGS.info("\t Checkbox is selected");
						bRtnValue = false;
					}
				}
				if (StringUtils.equalsIgnoreCase(strValue,"true")) {
					// Convert the boolean value to String
					bExpectedValue = Boolean.parseBoolean(strValue);
					KeywordLibrary_LOGS.info("expectedValue "+bExpectedValue);
					KeywordLibrary_LOGS.info("actualValue "+bActualValue);
					if (bExpectedValue == bActualValue) {
						strActualValue = "Checkbox is selected, as expected";
						KeywordLibrary_LOGS.info("\t Checkbox is selected, as expected");
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
								strName, strValue, strExpectedValue,
								strActualValue);
						bRtnValue = true;
					} else {
						strActualValue = "Checkbox is not selected, as expected";
						KeywordLibrary_LOGS.info("\t Checkbox is NOT selected");
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
								strName, strValue, strExpectedValue,
								strActualValue);
						bRtnValue = false;
					}
				}
			}
			else{
				strActualValue="Element not found on the web page";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
						strExpectedValue, strActualValue);
				bRtnValue = false;
			}

		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug("Corresponding StackTrace :-", e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug("Corresponding StackTrace :-", e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			bRtnValue = false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug("Corresponding StackTrace :-", e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCHKLOG);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRCHKLOG);
			bRtnValue = false;
		}
		return bRtnValue;
	}

	/*******************************************************************************************************
	 * Function Name - klMouseOver 
	 * Function Description - This Function hovers the mouse over the object
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element 
	 ********************************************************************************************************/

	public boolean klMouseOver(String strXpath, String strName)
	throws Exception {
		String strExpectedValue = "Expected to hover the mouse over an Element";
		KeywordLibrary_LOGS.info(strExpectedValue + strName);
		String strActualValue= StringUtils.EMPTY;
		try {
			if (klIsElementPresent(strXpath, strName)) {
				Actions actBuilder = new Actions(Initialization.driver);
				actBuilder.moveToElement(this.wbElement).perform();
				strActualValue = "Mouse Hovered Successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			strActualValue = "Did not find the element to hover";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRMOUSEEVENTEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRMOUSEEVENTEXCEP);
			return false;
		}
	}
	

	
	/*******************************************************************************************************
	 * Function Name - klWebButtonClick 
	 * Function Description - This Function verifies the WebButton object with respect to Xpath and 
	 * clicks on the specified Web Button. 
	 * InPuts - strXpath - Xpath of the Web Button ,strName - Logical Name of the Web Button 
	 ********************************************************************************************************/

	public boolean klWebButtonClick(String strFindProperty, String strName)
	throws Exception {
		boolean bReturnStatus = false;
		try {
			if(klIsElementPresentNoReport(strFindProperty)){
				this.wbElement.click();
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,"", String.format(StringConstants.STRCLKELEEXPECTED,strName),String.format(StringConstants.STRCLKELESUCCS, strName));
				bReturnStatus = true;
			} else 
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,"", String.format(StringConstants.STRCLKELEEXPECTED,strName), String.format(StringConstants.STRCLKELEFAILURE,strName));
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRNOSUCHELEEXCEP.concat(StringConstants.STRCHKLOG));
		}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",String.format(StringConstants.STRCLKELEEXPECTED,strName), StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}

	
	/*******************************************************************************************************
	 * Function Name - klSelectMulIPTypes 
	 * Function Description - This function will select the multiple web items which is of input types  as either 
	 * radio buttons or checkboxes type.for example, in the add parts screen popover window , we have multiple
	 * parts checkboxes, so if we need to select more than one checkboxes, we can identify what is the unique Xpaths for
	 * all the checkboxes and which is different one.In that case, only the part number is the different one when
	 * compare to all the xpaths, so once we path the different part number, the selection can be done
	 * based on the part number.
	 * InPuts - strFindProperty - Xpath of the Web Button ,strName - Logical Name of the Web Button 
	 * strValues-multiple input values, bDataSource-trur for taking value from the excel and false for taking values 
	 * directly from the parameters.
	 * 
	 ********************************************************************************************************/

	public boolean klSelectMulIPTypes(String strFindProperty, String strName,String strValues,boolean bDataSource)
	throws Exception {
		boolean bReturnStatus = false;
		int i;
		String[] strExpValue = (bDataSource) ?  StringUtils.split(super.fnGetParamValue(strValues),";") : StringUtils.split(strValues,";");
		try {
			for(i=0;i<strExpValue.length;i++){
				strFindProperty = String.format(strFindProperty, strExpValue[i].toString().trim());
				KeywordLibrary_LOGS.info("Dynamic Xpath Value : "+strFindProperty);
			if (klIsElementPresentNoReport(strFindProperty)) {
				this.wbElement.click();
				Thread.sleep(1000);//to give a snap of 1 sec for every execution for selecting the item(s)
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,"", String.format(StringConstants.STRCLKELEEXPECTED,strExpValue[i].trim().toString()),
						String.format(StringConstants.STRCLKELESUCCS, strExpValue[i].trim().toString()));
				this.wbElement=null;
				bReturnStatus = true;
			} else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,"", String.format(StringConstants.STRCLKELEEXPECTED,strExpValue[i].trim().toString()),
						StringConstants.STRNOSUCHELEEXCEP);
				bReturnStatus = false;
			}
			}
		} catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",StringConstants.STRCLKELE,
					StringConstants.STRNOSUCHELEEXCEP.concat(StringConstants.STRCHKLOG));
		}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",StringConstants.STRCLKELE,
					StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			bReturnStatus = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",StringConstants.STRCLKELE,
					StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			bReturnStatus = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRCLKELEEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",StringConstants.STRCLKELE,
					StringConstants.STRCLKELEEXCEP);
		}
		return bReturnStatus;
	}
	

	/*******************************************************************************************************
	 * Function Name - klPutDynData 
	 * Function Description - This function stores the dynamic run time data with (sKey , sValue) pair 
	 * InPuts - sKey(Key or Variable Name) , sValue (Variable Value) 
	 ********************************************************************************************************/

	public void klPutDynData(String strKey, String strValue) {
		try {
			KeywordLibrary_LOGS.info("Storing the Value in Hash Map");
			KeywordLibrary_LOGS.info("-------------------------------------");
			hDynTestData.put(strKey, strValue);
			KeywordLibrary_LOGS.info(String.format("Stored Key:'%s' and its Value:'%s'",strKey,hDynTestData.get(strKey)));
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRCOMFILEERR);
			KeywordLibrary_LOGS.error("    Function : klPutDynData");
			KeywordLibrary_LOGS.error("    Message : " + e.getMessage());
			KeywordLibrary_LOGS.error("    Cause : " + e.getCause());
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}

	/*******************************************************************************************************
	 * Function Name - klGetDynData 
	 * Function Description - This function retrieve the dynamic run time data with value. 
	 * InPuts - sKey (Key Name ,Variable Name to retrieve.) 
	 ********************************************************************************************************/

	public String klGetDynData(String strKey) {
		try {
			KeywordLibrary_LOGS.info("Retriving the Value from Hash Map");
			KeywordLibrary_LOGS.info("-------------------------------------");
			KeywordLibrary_LOGS.info("hDynTestData.get(strKey);"+hDynTestData.get(strKey));
			return hDynTestData.get(strKey);

		}  catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRKEYLIBERR);
			KeywordLibrary_LOGS.error("    Function : fnExecuteBatch");
			KeywordLibrary_LOGS.error("    Message : " + e.getMessage());
			KeywordLibrary_LOGS.error("    Cause : " + e.getCause());
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			return "Error";
		}
	}

	/*************************************************************************************************************
	 * Function Name - klEnterKeyEvent 
	 * Function Description - This function will trigger an return(enter) key event using the Selenium Native function
	 **************************************************************************************************************/

	public boolean klEnterKeyEvent(String strXpath, String strName) {
		boolean bEventStatus = false;
		try {
			if (klIsElementPresent(strXpath,strName)) {
				
				
				this.wbElement.click();
				//To press the enter key using Selenium sendKeys function. We can even use Keys.Enter in below code but Keys.Enter is throwing an error in Application in FireFox Browser
				this.wbElement.sendKeys(Keys.RETURN);
				
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", StringConstants.STREXPTOGETFOCUSFORENTERKEY,  StringConstants.STREXPTOGETFOCUSFORENTERKEYSUCCS);
				bEventStatus = true;
			} else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						"", StringConstants.STREXPTOGETFOCUSFORENTERKEY,  StringConstants.STREXPTOGETFOCUSFORENTERKEYFAILURE);
			}
		} catch (UnreachableBrowserException e) {
			bEventStatus = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPTOGETFOCUSFORENTERKEY,StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			bEventStatus = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPTOGETFOCUSFORENTERKEY, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			bEventStatus = false;
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					StringConstants.STREXPTOGETFOCUSFORENTERKEY, StringConstants.STRENTERKEYEVENTEXCEP);
		}
		finally{
			if(this.rEventHandler != null)
				this.rEventHandler = null;
		}
		return bEventStatus;
	}

	/*******************************************************************************************************
	 * Function Name - klWait 
	 * Function Description - This Function waits specified number of time 
	 * InPuts - strTime which is in milliseconds format
	 ********************************************************************************************************/

	public boolean klWait(int strTime) throws Exception {
		try {
			KeywordLibrary_LOGS.info("Into wait for "+strTime+" milliseconds");
			Thread.sleep(strTime);
			KeywordLibrary_LOGS.info("Out from wait after :"+strTime+"milliseconds");
			return true;
		}

		catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRWAITTIMEEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			return false;
		}
	}

	/*******************************************************************************************************
	 * Function Name - klWaitForPageLoading 
	 * Function Description - This Function waits till the page loading for maximum time of "iMaxAppResWaitTime"
	 * input: strXpath = xpath for the pageloading Object
	 * iDriverWaitTime- This is the driver wait time before the page loading window appears
	 * iMaxAppResWaitTime- This is set to wait for the maximum time that the user can expect the application to 
	 * provide the response to the user action.
	 * This method will wait till iMaxAppResWaitTime = "N" secs maximum time and then quit this loop
	 * and set the test case to exit to true and starts the next scenario if any. DO NOT GIVE VALUE "0" FOR "iMaxAppResWaitTime"
	 * Return-boolean condition whether we need to excute the current test case or not. True : scripts can continue, False : Vice Versa
	 * 
	 ********************************************************************************************************/

	public boolean klWaitForPageLoading(String strXpath,int iDriverWaitTime,int iMaxAppResWaitTime) throws Exception {
		int iUserDefinedMaxResponseTime = iMaxAppResWaitTime;
		try {
			KeywordLibrary_LOGS.info(StringConstants.STREXPPAGELOADING);
			while(klFindElementCustomWait(strXpath,iDriverWaitTime) && iMaxAppResWaitTime > 0){
				Thread.sleep(1000);
				iMaxAppResWaitTime -= 1;
			}
			KeywordLibrary.isTestScenarioContinue = iMaxAppResWaitTime <= 0 ? false : true;
				 ReportingFunctionsXml.fnSetReportBPCStepStatus(KeywordLibrary.isTestScenarioContinue, "Page/Table loading...", "","",KeywordLibrary.isTestScenarioContinue ? 
						 StringConstants.STREXPPAGELOADINGSUCCS : String.format(StringConstants.STREXPPAGELOADINGFAILURE,Integer.valueOf(iUserDefinedMaxResponseTime)));
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} finally {
			//resets to default wait for driver. Check the .properties file for more details related to driver default wait time
			setDrvWaitToDefault();
		}
		return KeywordLibrary.isTestScenarioContinue;
	}


	/*******************************************************************************************************
	 * Function Name - klClickUnexpectedPopUpwithButton 
	 * Function Description -This function verifies if the PopupObject exist or not and clicks the button. Since
	 * we are trying to click the unexpected popup, we no need to report to the xml file
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * 
	 ********************************************************************************************************/
	public boolean klClickUnexpectedPopUpwithButton(String strXpath,
			String strName) throws Exception {
		String strExpectedValue = "Expected to click the button on Unexpected pop up";
		String strActualValue = StringUtils.EMPTY;
		try {
			if (klIsElementPresent(strXpath, strName)) {
				Initialization.driver.findElement(By.xpath(strXpath)).click();
				Thread.sleep(5000);
				strActualValue = "Closed the pop up successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			strActualValue = "Pop Up was not found";
			//				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
			//						"", strExpectedValue, strActualValue);
			return true;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			//			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
			//			      strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			//			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
			//					strExpectedValue, strActualValue);
			return false;
		} catch (Exception e) {
			strActualValue = "Exception occurred while clicking the unexpected pop up";
			//			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
			//					strExpectedValue, strActualValue);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			return false;
		}

	}
	/*******************************************************************************************************
	 * Function Name - klVPAlertMessageAppears 
	 * Function Description -This functions verifies that the alert message is displayed and clicks it
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * 
	 ********************************************************************************************************/

	public boolean klVPAlertMessageAppears(String strXpath, String strName)
	throws Exception {
		String strExpectedValue = "Alert Message Pop Up Expected";
		String strActualValue;
		try {
			if (klIsElementPresentNoReport(strXpath)){
				this.wbElement.click();
				strActualValue = "Alert Available and Clicked";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			strActualValue = "There is no alert message pops-up during the execution of this scenario.";
			KeywordLibrary_LOGS.info(strActualValue);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRHANDLEALERTEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRHANDLEALERTEXCEP);
			return false;
		}
	}


	/*******************************************************************************************************
	 * Function Name - klVPImageIconDisplayed 
	 * Function Description -This functions verifies that the image icon is displayed
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * 
	 ********************************************************************************************************/
	public boolean klVPImageIconDisplayed(String strXpath, String strName) {
		String strExpectedValue = "Expected the Image Icon to be displayed";
		String strActualValue = StringUtils.EMPTY;
		try {
			if (klIsElementPresent(strXpath, strName)) {
				strActualValue = "Image Icon available";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			KeywordLibrary_LOGS.info("There is no Image icon displayed.");
			strActualValue = "There is no Image icon displayed";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue,StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRIMGERR);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRIMGERR);
			return false;

		}

	}


	/*******************************************************************************************************
	 * Function Name - klVPScreenDisplayed 
	 * Function Description -This functions verifies that the screen is displayed
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * 
	 ********************************************************************************************************/
	public boolean klVPScreenDisplayed(String strXpath, String strName) {
		String strExpectedValue = "Expected  the Screen to be displayed";
		String strActualValue= StringUtils.EMPTY;
		try {
			if (klIsElementPresent(strXpath, strName)) {
				strActualValue = "Screen is available";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			KeywordLibrary_LOGS.info("The Screen is not displayed.");
			strActualValue = "The Screen is not displayed";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRSCREENAVAILERR);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRSCREENAVAILERR);
			return false;

		}
	}
	/*******************************************************************************************************
	 * Function Name - klVPWebElementFieldIsEditable 
	 * Function Description -This function inputs the data into the text field to check that it is editable
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * 
	 ********************************************************************************************************/

	public boolean klVPWebElementFieldIsEditable(String strXpath,
			String strName, String strValue, boolean bDataSource)
	throws Exception {
		String strExpectedValue = "Expected the Field to be editable";
		String strActualValue = StringUtils.EMPTY;

		try {
				strValue = bDataSource ? super.fnGetParamValue(strValue): strValue;

			if (klIsElementPresent(strXpath, strName)) {
				this.wbElement.clear();
				this.wbElement.sendKeys(strValue);
				this.wbElement.clear();
				strActualValue = "Field is editable";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			strActualValue = "The field is not found to be edited.";
			KeywordLibrary_LOGS.info(strActualValue);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			return false;
		}catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		} catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STREDITERR);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STREDITERR);
			return false;
		}

	}

	/*******************************************************************************************************
	 * Function Name - klCloseBrowser 
	 * Function Description -This function Closes the browser
	 * 
	 ********************************************************************************************************/

	public void klCloseBrowser() {
		try {
			KeywordLibrary_LOGS.info("Closing the Browser");
			Initialization.driver.close();
			Initialization.driver.quit();
		} catch(UnreachableBrowserException e){
			KeywordLibrary_LOGS.error("Issues in the Browser as :"+e.getMessage()+"and :"+e.getSupportUrl()+ " and :"+e.getStackTrace());
			RecoveryScn.recUnReachableTestScnExitTrue();
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		finally{
			Initialization.driver = null;
		}
	}
	/*******************************************************************************************************
	 * Function Name - klOpenURL 
	 * Function Description -This function Reopens the browser
	 * 
	 ********************************************************************************************************/

	public void klOpenURL(String strURL) {
		try {
			klWait(Initialization.strWaitTime);
			Initialization.driver.get(strURL);
			//some times due to network issues, there might be some delay in the initial loading of page
			//this is the reason, we have hard coded the seconds as "100" seconds. if we are sure that there is no
			//network issue, we can use the predefined value from the configuration file. ie."Initialization.strDefaultDriverWait"
			setDrvWaitTo(30);
			KeywordLibrary_LOGS.info("Reopening the Broswer");
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error("Issues in the Browser as :"+e.getMessage()+"and :"+e.getSupportUrl()+ " and :"+e.getStackTrace());
			RecoveryScn.recUnReachableTestScnExitTrue();
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (Exception e) {

			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}
	/*******************************************************************************************************
	 * Function Name - klTextInputFieldForCalendar 
	 * Function Description -This function inputs the data into calendar field
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue - Value to be entered in the Edit box, strDataSource - False(Value directly passed
	 * in the function), True(Value is to be retrieved from Datasheet) 
	 * 
	 ********************************************************************************************************/

	public boolean klTextInputFieldForCalendar(String strXpath, String strName,
			String strValue, boolean bDataSource) throws Exception {
		String strExpectedValue = "Expected the Calendar Data to be entered";
		String strActualValue = StringUtils.EMPTY;

		try {
				strValue = bDataSource ? super.fnGetParamValue(strValue) : strValue;
			if (klIsElementPresentNoReport(strXpath)) {
				this.wbElement.clear();
				this.wbElement.sendKeys(strValue);
				strActualValue = "Calendar data entered successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						strValue, strExpectedValue, strActualValue);
				return true;
			}
			strActualValue = "Calendar data object not found on the page";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, strActualValue);
			return false;
		}catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		} catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRENTERCALDATAERR);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strValue, strExpectedValue, StringConstants.STRENTERCALDATAERR);
			return false;
		}

	}
	/*******************************************************************************************************
	 * Function Name         - klRdAnStrObjTxt
	 * InPuts   - strXpath - Xpath of the object , strName   - Logical Name of the object.
	 * Finds the webelement value from the browser and stores it in strTemp
	 ********************************************************************************************************/

	public boolean klRdAnStrObjTxt(String strXpath, String strName)
	{
		boolean bReturnStatus = false;
		try {
			if (klIsElementPresent(strXpath,strName))
			{
				strTemp = this.wbElement.getText();
				KeywordLibrary_LOGS.info("Storing the Value: "+strTemp+" to Temp ");
				bReturnStatus = true;
			}
			KeywordLibrary_LOGS.info("The object to get text is not available on the web page");
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();		
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRGETVALERR);
		}
		return bReturnStatus;
	}

	/*******************************************************************************************************
	 * Function Name         - klSplAnStrTxnId
	 * Function Description - This functions reads the object text and store it in temporary variable strTemp
	 ********************************************************************************************************/

	public boolean klSplAnStrTxnId()
	{
		String strExpectedValue = "The Txn Id is stored in a variable strTxnId";
		String strActualValue = StringUtils.EMPTY;
		String strValue = "Null";
		try {
			String[] strSplit = null;
			if (strTemp.contains(":")) {
				strSplit = StringUtils.split(strTemp,":");
			} else {
				strSplit = StringUtils.split(strTemp,": ");
			}

			if (strSplit.length > 1)
			{
				strValue = strSplit[1].trim();
				klPutDynData("strTxnId" , strValue);           

				strActualValue   = "The transaction is successful with Id : "+strValue;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, "strTxnId",
						strValue, strExpectedValue, strActualValue);
				return true;
			}
			strValue = strSplit[0];
			strActualValue   = "The transaction is not successful";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "strTxnId",
					strValue, strExpectedValue, strActualValue);
			return true;

		}
		catch (IndexOutOfBoundsException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRINDEXOUTOFBOUND);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "strTxnId",
					strValue, strExpectedValue, StringConstants.STRINDEXOUTOFBOUND);
			return false;
		}catch (NullPointerException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRNULLPTREXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "strTxnId",
					strValue, strExpectedValue, StringConstants.STRNULLPTREXCEP);
			return false;
		}
		catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "strTxnId",
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSTOREVALERR);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "strTxnId",
					strValue, strExpectedValue, StringConstants.STRSTOREVALERR);
			return false;

		}

	}
	
	
	/*******************************************************************************************************
	 * Function Name         - klSplitStringDelimiter
	 * Function Description - This functions reads the object text and store it in temporary variable strTemp
	 ********************************************************************************************************/

	public String klSplitStringDelimiter(String strVal,String delimiter,int position)
	{
	
		try {
			String[] strSplit = null;
			
				strSplit = StringUtils.split(strVal,delimiter);
				for (int i = 0; i < strSplit.length; i++) {
					KeywordLibrary_LOGS.info("strSplit[i] : "+strSplit[i]);
				}
				return klStringAtIndex(strSplit, position);
			
			
		}
		catch (IndexOutOfBoundsException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRINDEXOUTOFBOUND);
			return null;
		}catch (NullPointerException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRNULLPTREXCEP);
			return null;
		}
		catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			return null;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSTOREVALERR);
			return null;

		}

	}
	
	
	/*******************************************************************************************************
	 * Function Name         - klStringAtIndex
	 * Function Description - This function will get the index at the position of the string
	 ********************************************************************************************************/
	public String klStringAtIndex(String[] strVal,int position)
	{
		try {
			KeywordLibrary_LOGS.info("strVal["+position+"] : "+strVal[position]);
				return strVal[position];
		}
		catch (IndexOutOfBoundsException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRINDEXOUTOFBOUND);
			return null;
		}catch (NullPointerException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRNULLPTREXCEP);
			return null;
		}
		catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			return null;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSTOREVALERR);
			return null;

		}

	}
	
	

	/*******************************************************************************************************
	 * Function Name - klSelectFirstDDLOption 
	 * Function Description - This function selects the first item in the list option from the DDL
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/

	public boolean klSelectFirstDDLOption(String strXpath, String strName) {
		String strExpectedValue = "Expected  to select the item successfully";
		String strActualValue;
		try {
			if (klIsElementPresentNoReport(strXpath)) {
				Select select = new Select(this.wbElement);
				// selects the options available in the lists
				select.selectByIndex(1);
				strActualValue = "Selected the first item";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,
						"", strExpectedValue, strActualValue);
				return true;
			}
			KeywordLibrary_LOGS.info("The item to be selected is not available.");
			strActualValue = "The item to be selected is not available";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", strExpectedValue, strActualValue);
			return false;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSELITEMEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					strExpectedValue,StringConstants.STRSELITEMEXCEP);
			return false;

		}
	}
	/*******************************************************************************************************
	 * Function Name - klSelectDDLOption 
	 * Function Description - This function selects the item in the list option from the DropDownList
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue= keyvalue, bDataSource= boolean condition to set the value from input sheet or not.
	 ********************************************************************************************************/

	public boolean klSelectDDLOption(String strXpath, String strName,String strValue, boolean bDataSource) {
		boolean bReturnStatus = false;
		try {
				strValue = (bDataSource ) ? super.fnGetParamValue(strValue) : strValue;
			if (klIsElementPresentNoReport(strXpath)) {
				Select select = new Select(this.wbElement);
				// selects the options available in the lists
				select.selectByVisibleText(strValue);
				bReturnStatus = true;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strValue,
						String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), bReturnStatus ? String.format(StringConstants.STRSHDSELECTLISTITMSUCCS,strValue,strName) :
							String.format(StringConstants.STRSHDSELECTLISTITMFAILURE,strValue,strName));
		}} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRSELITEMEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRSELITEMEXCEP);
		}
		return bReturnStatus;
	}
	
	
	/*******************************************************************************************************
	 * Function Name - klSelectDDLOptionByValueOrIndex 
	 * Function Description - This function selects the item in the list option from the DropDownList
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * strValue= keyvalue, bDataSource= boolean condition to set the value from input sheet or not.
	 * Date 20Sep2012 

	 ********************************************************************************************************/

	public boolean klSelectDDLOptionByValueOrIndex(String strXpath, String strName,String strValue,String genericSelect, boolean bDataSource) {
		boolean bReturnStatus = false;
		
		try {
				strValue = (bDataSource ) ? super.fnGetParamValue(strValue) : strValue;
			if (klIsElementPresentNoReport(strXpath)) {
				Select select = new Select(this.wbElement);
				// selects the options available in the lists
				
				if(StringUtils.equalsIgnoreCase(genericSelect,"Value"))
					select.selectByValue(strValue);
				else
				{
					select.selectByIndex(Integer.parseInt(strValue));
				}
				
				bReturnStatus = true;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strValue,
						String.format(StringConstants.STRSHDSELECTLISTITMINDX,strValue,strName), bReturnStatus ? String.format(StringConstants.STRSHDSELECTLISTITMSUCCS,strValue,strName) :
							String.format(StringConstants.STRSHDSELECTLISTITMFAILURE,strValue,strName));
		}} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRSELITEMEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strValue,
					String.format(StringConstants.STRSHDSELECTLISTITM,strValue,strName), StringConstants.STRSELITEMEXCEP);
		}
		return bReturnStatus;
	}
	
	
	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyAllAvailableDDLOptions 
	 * Function Description - This function matches all the Dropdown options in the UI with the Expected values
	 * mentioned in Data sheets
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/

	public boolean klVerifyAllAvailableDDLOptions(String strXpath, String strName,String strExpValue, boolean bDataSource) {
		boolean bReturnStatus = false;
		String [] strExpValues= null;
		String actualResult = StringUtils.EMPTY;
		try {
			strExpValue=bDataSource?super.fnGetParamValue(strExpValue):strExpValue;
			strExpValues = StringUtils.split(strExpValue,";");

			if (klIsElementPresentNoReport(strXpath))
			{
				Select select = new Select(this.wbElement);
				// selects the options available in the lists
				List<WebElement> options = select.getOptions(); 
				int iListSize = options.size();
				String [] uiListItms = new String[iListSize];
				for (int i=0; i < iListSize;i++)
					uiListItms[i] = options.get(i).getText().toString().trim();
				List<String> lUIList = new ArrayList<String>(Arrays.asList(uiListItms));

				//This will report each of the list elements whether it is available or not as expected.
				for(String sExpItm : strExpValues)
				{
					actualResult = (bReturnStatus = lUIList.contains(sExpItm.trim())) ? String.format(StringConstants.STRDDLOPTIONEXPECTEDMATCHSUCCESS,sExpItm) :
						String.format(StringConstants.STRDDLOPTIONEXPECTEDMATCHFAILURE,sExpItm);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,sExpItm, StringConstants.STRDDLOPTIONEXPECTEDMATCH, actualResult);
					actualResult = StringUtils.EMPTY;
				}
			}
			else 
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						strExpValue, StringConstants.STRDDLOPTIONEXPECTEDMATCH, StringConstants.STRDDLNOTAVAILABLE);
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLOPTIONEXPECTEDMATCH, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLOPTIONEXPECTEDMATCH, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturnStatus = false;
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLOPTIONEXPECTEDMATCH, StringConstants.STRCHKLOG);
		}
		return bReturnStatus;
	}
	/*******************************************************************************************************
	 * Function Name - KlAvailableDDLOptionsCount
	 * Function Description - This function matches the Dropdown count in the UI with the Key values
	 * mentioned in Data sheets
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/
	public boolean klAvailableDDLOptionsCount(String strXpath, String strName,String strKeyValue, boolean bDatasource) {
		
		boolean bReturnValue = false;
		String strActualValue = StringUtils.EMPTY;
		String actualValue = StringUtils.EMPTY;
		String strExpVal = StringUtils.EMPTY;
		try {
				strExpVal = (bDatasource) ? super.fnGetParamValue(strKeyValue) : strKeyValue;
			if (klIsElementPresentNoReport(strXpath))
			{
				Select select = new Select(this.wbElement);
				actualValue = Integer.toString(select.getOptions().size()); 
							
				if (actualValue.equals(strExpVal)) {
					KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESEQUAL);
					strActualValue = "Expected value '"+strExpVal+"' matched with actual value '"+actualValue+"'";
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strExpVal,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
					bReturnValue = true;
				} else {
					KeywordLibrary_LOGS.info("\t"+StringConstants.STRVALUESNOTEQUAL);
					strActualValue = "Expected value '"+strExpVal+"' did not match with actual value '"+actualValue+"'";
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strExpVal,
							StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
				}
			}
			else{
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strKeyValue,
						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strKeyValue,
					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strKeyValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRFUNNOTEXEC+ e.getMessage());
			strActualValue = StringConstants.STRFUNNOTEXEC+ e.getMessage();
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					strKeyValue, StringConstants.STREXPSHDMATCHACTUAL, strActualValue);
		}
		return bReturnValue;
	}

	/*******************************************************************************************************
	 * Function Name - klVerifyNotAvailableDDLOptions 
	 * Function Description - This function matches the Dropdown options which is not present in the UI with the
	 * Expected values mentioned in Data sheets
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 ********************************************************************************************************/

	public void klVerifyNotAvailableDDLOptions(String strXpath, String strName,String strExpValue) {
		String [] strExpValues= null;

		try {
			strExpValue = super.fnGetParamValue(strExpValue);
			strExpValues = StringUtils.split(strExpValue,";");

			if (klIsElementPresentNoReport(strXpath))
			{
				Select select = new Select(Initialization.driver.findElement(By.xpath(strXpath)));
				// selects the options available in the lists
				List<WebElement> options = select.getOptions(); 
				int iListSize = options.size();
				String [] uiListItms = new String[iListSize];
				for (int i=0; i < iListSize;i++)
				{
					uiListItms[i] = options.get(i).getText().toString().trim();
				}
				List<String> lUIList = new ArrayList<String>(Arrays.asList(uiListItms));

				for(String sExpItm : strExpValues)
				{
					if(!lUIList.contains(sExpItm.trim())){
						ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,sExpItm, StringConstants.STRDDLSHOULDNOTAVAILABLE,
								String.format(StringConstants.STRDDLSHOULDNOTAVAILABLESUCCS,sExpItm));
					}
					else
					{
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, sExpItm,StringConstants.STRDDLSHOULDNOTAVAILABLE,
								String.format(StringConstants.STRDDLSHOULDNOTAVAILABLEFAILURE,sExpItm));
					}
				}
			}
			else {
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
						strExpValue, StringConstants.STRDDLSHOULDNOTAVAILABLE, StringConstants.STRDDLNOTAVAILABLE);
			}
		}catch (NoSuchElementException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLSHOULDNOTAVAILABLE, StringConstants.STRNOSUCHELEEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLSHOULDNOTAVAILABLE, StringConstants.STRUNRECBROWEXCEP.concat(StringConstants.STRCHKLOG));
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLSHOULDNOTAVAILABLE, StringConstants.STRWEBDRVEXCEP.concat(StringConstants.STRCHKLOG));
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
					StringConstants.STRDDLSHOULDNOTAVAILABLE, StringConstants.STRFUNNOTEXEC.concat(StringConstants.STRCHKLOG));
		}
	}


	/*******************************************************************************************************
	 * Function Name - klFindRowFromTable 
	 * Function Description - This function will select the correct row in the table provided the input values 
	 * row into it.
	 * InPuts - strXpath - Xpath of the Web Element table name or can be header name as well
	 * strCombinedColXpath - combined value of the column Xpath which could be a combined headers xpath.
	 * strNames - this is the names of the headers in the table
	 * strValue-Key for all the columns separated by";" in the excel sheet,
	 * bDataSource- data source in which we can get the value 
	 * from the excel sheet,strNames-This is the names for the table column,iDriverTime-Implicit time for the 
	 * driver instance to wait for further actions.
	 * This function will set the row number where the given record is present in the table rather this will
	 * not set the page number. if fails to find the row, then it will returns false and sets the row as 0 
	 * which is default value and no record will exist in the row count 0
	 * 
	 ********************************************************************************************************/

	public boolean klFindRowFromTable(String strXpath, String strCombinedColXpath,String strNames, String strValue, boolean bDataSource,int iDriverWaitTime) {
		String actualValue=StringUtils.EMPTY;
		String expectedValue=StringUtils.EMPTY;
		boolean bReturn = false;
		int iColNum =0;
		int iRecCount = 1;
			String[] strCombinedColXpaths = StringUtils.split(strCombinedColXpath,";");
			int iTotalCol = strCombinedColXpaths.length;
			String[] strValues = (bDataSource) ? StringUtils.split(super.fnGetParamValue(strValue),";") : StringUtils.split(strValue,";");
			
			KeywordLibrary_LOGS.info("Expected data values :");
			for (int i = 0; i < strValues.length; i++) {
				KeywordLibrary_LOGS.info(String.format("\nstrValues[iColNum] = strValues[%d]: '%s'",iColNum,strValues[iColNum].toString()));
			}
			KeywordLibrary_LOGS.info("Total number of records in the table : "+klWebElementCount(strXpath));

			int iCurrentRecordCount = klWebElementCount(strXpath);
			
			for(iRecCount = 1;iRecCount <= iCurrentRecordCount;iRecCount++){
				//This will iterate throughout the entire row for all number of record count
				if(klIsElementPresentNoReport(String.format("%s[%d]",strXpath,iRecCount))){
					for(iColNum =0;iColNum<iTotalCol;iColNum++){
						try {
							actualValue=klGetTextFromWebElementNoReport(String.format(strCombinedColXpaths[iColNum].toString(),iRecCount,strValues[iColNum].toString()),iDriverWaitTime);
							expectedValue=strValues[iColNum].toString();
							KeywordLibrary_LOGS.info("actualValue : "+actualValue);
							KeywordLibrary_LOGS.info("expectedValue : "+expectedValue);
									if(StringUtils.equalsIgnoreCase(expectedValue, actualValue)){
								bReturn = true;
							}else{
								bReturn = false;
								break;
							}
						} catch (Exception e) {
							bReturn = false;
							KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
						}
					}
				}
				if(iColNum == iTotalCol){
					iRowNum = bReturn ? iRecCount : 0;
					break;
				}
			}
			
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, "TableRow", "", "To get table data row number.", bReturn ? String.format(StringConstants.STRROWDATAAVAILABLE,iRecCount) : String.format(StringConstants.STRROWDATAAVAILABILITYREPORT,0));
			
		return bReturn;
	}
	

	/*******************************************************************************************************
	 * Function Name - klVerifySort 
	 * Function Description - This function sorts the particular column in ascending or descending order based on the argument passed
	 * InPuts - strXpath - Xpath of the table , strSortOrder - Common XPath of all the Rows, strSortOrder-Order in which to be sorted
	 * Prerequisite-Call klGetWebCount() before this fn in the BPC to get page count
	 ********************************************************************************************************/
	public boolean klVerifySort(String strXpath, String strSubPath,String strSubPathEnd,String strSortOrder) {
		String strExpectedValue = "Expected  the Sort to be successful";
		String strActualValue = StringUtils.EMPTY;
		boolean bReturn = false;
		String currentValue =StringUtils.EMPTY;
		String nextValue = StringUtils.EMPTY;
		try {
			if (klIsElementPresentNoReport(strXpath)){
				int iSize =  Initialization.driver.findElements(By.xpath(strXpath)).size();

				if(iPageOrRecordCount>1){
					for (iRowNum = 1; iRowNum < iSize; iRowNum++) {

						KeywordLibrary_LOGS.info(strSubPath+iRowNum+strSubPathEnd);
						currentValue=Initialization.driver.findElement(By.xpath(strSubPath+iRowNum+strSubPathEnd)).getText().toString();
						KeywordLibrary_LOGS.info(strSubPath);
						nextValue=Initialization.driver.findElement(By.xpath(strSubPath+(iRowNum+1)+strSubPathEnd)).getText().toString();
						KeywordLibrary_LOGS.info("nextValue"+nextValue);
						KeywordLibrary_LOGS.info("currentValue"+currentValue);
						if(strSortOrder.equalsIgnoreCase("descending")){
							if(currentValue.compareTo(nextValue) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							KeywordLibrary_LOGS.info(strSubPath);
							break;
						}

						else if(strSortOrder.equalsIgnoreCase("ascending")){
							if(nextValue.compareTo(currentValue) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							break;

						}
						//To make sure the loop is executed only specified number of times as in config file to avoid infinite loop
						if(iRowNum>=Integer.parseInt(Initialization.strMaxIteration)){
							KeywordLibrary_LOGS.info("the value of iRownum is "+iRowNum);
							KeywordLibrary_LOGS.info("the value of strMaxIteration is "+Initialization.strMaxIteration);
							break;
						}
					}
				}
				else{
					KeywordLibrary_LOGS.info("Only single record, hence no need to sort");
					bReturn=true;
				}
				if(bReturn){
					strActualValue="All Values sorted successfully";
					KeywordLibrary_LOGS.info("all the column values sorted");
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strSortOrder,"", strExpectedValue, strActualValue);
				}else{

					strActualValue="All Values not sorted successfully";
					KeywordLibrary_LOGS.warn("all the column values not sorted");
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,"", strExpectedValue, strActualValue);

				}
			}
			else {

				bReturn= false;
				strActualValue="Sort was not successful, since table/column to be sorted was not found";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,
						"", strExpectedValue, strActualValue);
			}
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, strActualValue);
			return false;
		} catch (Exception e) {
			strActualValue = "Exception occurred while sorting the values";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, strActualValue);
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRSORTEXCEP);
			return false;

		}
		return bReturn;
	}

	
	/*******************************************************************************************************
	 * Function Name - klVerifyDataSort 
	 * Function Description - This function sorts the particular column in ascending or descending order based on the argument passed
	 * InPuts - 
	 * strTableXpath - Xpath of the table to be verified .
	 * strRowXpath -make sure that we are provided the correct column xpath with %d as the string format, where %d= row number
	 * strSortOrder-Order in which to be sorted
	 * iRecordCount = total number of records in that table to be sorted.
	 * 
	 ********************************************************************************************************/
	public boolean klVerifyDataSort(String strTableXpath,String strRowXpath,String strName,int iRecordCount,String strSortOrder) {
		boolean bReturn = false;
		String strActualValue=StringUtils.EMPTY;
		String currentValue=StringUtils.EMPTY;
		String nextValue=StringUtils.EMPTY;
		int iColumnNum =1;
		try {
			if(klIsElementPresent(strTableXpath,strName)){
				if(iRecordCount > 1){
					for (iColumnNum = 1; iColumnNum < iRecordCount; iColumnNum++) {
						currentValue=Initialization.driver.findElement(By.xpath(String.format(strRowXpath,iColumnNum))).getText().toString();
						nextValue=Initialization.driver.findElement(By.xpath(String.format(strRowXpath,iColumnNum+1))).getText().toString();
						KeywordLibrary_LOGS.info("nextValue : "+nextValue);
						KeywordLibrary_LOGS.info("currentValue : "+currentValue);
						if(StringUtils.equalsIgnoreCase(strSortOrder,"descending")){
							if(!(bReturn = currentValue.compareTo(nextValue) >= 0 )){
								bReturn = false;
							 break;
							}
						}else if(strSortOrder.equalsIgnoreCase("ascending")){
							if(!(bReturn = nextValue.compareTo(currentValue) >= 0 )){
								bReturn = false;
								break;
							}
						}
					}
				}
					KeywordLibrary_LOGS.info(strActualValue = bReturn ? StringConstants.STREXPECTEDSORTSUCCESS : String.format(StringConstants.STREXPECTEDSORTFAILURE,currentValue,nextValue));
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturn, strSortOrder,"", StringConstants.STREXPECTEDSORT, strActualValue);
			}else 
				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturn, strSortOrder,
						"", StringConstants.STREXPECTEDSORT, StringConstants.STRSORTElEMENTNOTFOUND);
			}catch (UnreachableBrowserException e) {
				KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
				bReturn = false;
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					StringConstants.STREXPECTEDSORT, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturn = false;
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					StringConstants.STREXPECTEDSORT, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			bReturn = false;
			KeywordLibrary_LOGS.error(StringConstants.STRSORTEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					StringConstants.STREXPECTEDSORT, StringConstants.STRSORTEXCEP);
		}
		return bReturn;
	}
	
	
	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyDateSort 
	 * Function Description - This function sorts the particular column in ascending or descending order based on the argument passed
	 * InPuts - strXpath - Xpath of the table , strSortOrder - Common XPath of all the Rows, strSortOrder-Order in which to be sorted
	 * Prerequisite-Call klGetPageCount before this fn in the BPC to get page count
	 ********************************************************************************************************/
	public boolean klVerifyDateSort(String strXpath, String strSubPath,String strSubPathEnd,String strSortOrder) {
		String strExpectedValue = "Expected  the Sort to be successful";
		String strActualValue=StringUtils.EMPTY;
		boolean bReturn = false;
		String currentValue = StringUtils.EMPTY;
		String nextValue = StringUtils.EMPTY;
		try {
			if (klIsElementPresent(strXpath, "DateSort")) {
				int iSize =  Initialization.driver.findElements(By.xpath(strXpath)).size();

				if(iPageOrRecordCount>1){
					for (iRowNum = 1; iRowNum < iSize; iRowNum++) {

						KeywordLibrary_LOGS.info(strSubPath+iRowNum+strSubPathEnd);
						currentValue=Initialization.driver.findElement(By.xpath(strSubPath+iRowNum+strSubPathEnd)).getText().toString().split(":")[1].toString();
						KeywordLibrary_LOGS.info(strSubPath);
						nextValue=Initialization.driver.findElement(By.xpath(strSubPath+(iRowNum+1)+strSubPathEnd)).getText().toString().split(":")[1].toString();
						KeywordLibrary_LOGS.info("nextValue"+nextValue);
						KeywordLibrary_LOGS.info("currentValue"+currentValue);					

						Date CurrentDate = new SimpleDateFormat("MM/dd/yy").parse(currentValue);
						Date NextDate = new SimpleDateFormat("MM/dd/yy").parse(nextValue);
						if(StringUtils.equalsIgnoreCase("descending",strSortOrder)){
							if(CurrentDate.compareTo(NextDate) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							KeywordLibrary_LOGS.info(strSubPath);
							break;
						}

						else if(StringUtils.equalsIgnoreCase("ascending",strSortOrder)){
							if(nextValue.compareTo(currentValue) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							break;

						}
						//To make sure the loop is executed only specified number of times as in config file to avoid infinite loop
						if(iRowNum>=Integer.parseInt(Initialization.strMaxIteration)){
							KeywordLibrary_LOGS.info("the value of iRownum is "+iRowNum);
							KeywordLibrary_LOGS.info("the value of strMaxIteration is "+Initialization.strMaxIteration);
							break;
						}
					}
				}
				else{
					KeywordLibrary_LOGS.info("Only single record, hence no need to sort");
					bReturn=true;
				}
				if(bReturn){
					strActualValue="All Values sorted successfully";
					KeywordLibrary_LOGS.info(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strSortOrder,"", strExpectedValue, strActualValue);
				}else{

					strActualValue="All Values not sorted successfully";
					KeywordLibrary_LOGS.warn(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,"", strExpectedValue, strActualValue);

				}
			}
			else {

				bReturn= false;
				strActualValue="Sort was not successful, since table/column to be sorted was not found";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,
						"", strExpectedValue, strActualValue);
			}
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRSORTEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRSORTEXCEP);
			return false;

		}
		return bReturn;
	}

	
	/*******************************************************************************************************
	 * Function Name - klVerifyDateTimeSort 
	 * Function Description - This function sorts the particular column in ascending or descending order based on the argument passed
	 * InPuts - strXpath - Xpath of the table , strSortOrder - Common XPath of all the Rows, strSortOrder-Order in which to be sorted
	 * Prerequisite-Call klGetPageCount before this fn in the BPC to get page count
	 ********************************************************************************************************/
	public boolean klVerifyDateTimeSort(String strXpath, String strStrtPath,String strSubPathEnd,String strSortOrder) {
		String strExpectedValue = "Expected  the Sort to be successful";
		String strActualValue = StringUtils.EMPTY;
		boolean bReturn = false;
		String currentValue = StringUtils.EMPTY;
		String nextValue = StringUtils.EMPTY;
		String timeFrstXpath=StringUtils.EMPTY;
		String timeScndXpath=StringUtils.EMPTY;
		int iRowNumNxt=0;
		try {
			if (klIsElementPresent(strXpath, "DateTimeSort")) {
				int iSize =  Initialization.driver.findElements(By.xpath(strXpath)).size();

				if(iPageOrRecordCount>1){
					for (iRowNum = 1; iRowNum <= iSize; iRowNum++) {

						timeFrstXpath=strStrtPath+iRowNum+strSubPathEnd;
						KeywordLibrary_LOGS.info("timeFrstXpath : "+timeFrstXpath);
						
						
						if(klIsElementPresentNoReportExplicitWait(timeFrstXpath,1)){
							
							for (iRowNumNxt = iRowNum+1; iRowNumNxt <iSize; iRowNumNxt++) {
								
							timeScndXpath=strStrtPath+(iRowNumNxt)+strSubPathEnd;
							KeywordLibrary_LOGS.info("timeScndXpath : "+timeScndXpath);
							
							if(klIsElementPresentNoReportExplicitWait(timeScndXpath,1)){
						currentValue=this.wbElement.getText().toString();
						KeywordLibrary_LOGS.info("currentValue :"+currentValue);
						
						nextValue=this.wbElement.getText().toString();
						KeywordLibrary_LOGS.info("nextValue"+nextValue);
						
						Date CurrentDate = new SimpleDateFormat("MM/dd/yy").parse(currentValue);
						Date NextDate = new SimpleDateFormat("MM/dd/yy").parse(nextValue);
						if(StringUtils.equalsIgnoreCase("descending",strSortOrder)){
							if(CurrentDate.compareTo(NextDate) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							KeywordLibrary_LOGS.info(timeFrstXpath);
							break;
						}

						else if(StringUtils.equalsIgnoreCase("ascending",strSortOrder)){
							if(nextValue.compareTo(currentValue) >= 0 ){
								bReturn=true;
								continue;
							}
							bReturn =false;
							break;

						}
						
						//To make sure the loop is executed only specified number of times as in config file to avoid infinite loop
						if(iRowNum>=Integer.parseInt(Initialization.strMaxIteration)){
							KeywordLibrary_LOGS.info("the value of iRownum is "+iRowNum);
							KeywordLibrary_LOGS.info("the value of strMaxIteration is "+Initialization.strMaxIteration);
							break;
						}
					}		
				}
				}	
				} 		
			}			
				
				else{
					KeywordLibrary_LOGS.info("Only single record, hence no need to sort");
					bReturn=true;
				}
				if(bReturn){
					strActualValue="All Values sorted successfully";
					KeywordLibrary_LOGS.info(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strSortOrder,"", strExpectedValue, strActualValue);
				}else{

					strActualValue="All Values not sorted successfully";
					KeywordLibrary_LOGS.warn(strActualValue);
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,"", strExpectedValue, strActualValue);

				}
			}
			else {

				bReturn= false;
				strActualValue="Sort was not successful, since table/column to be sorted was not found";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder,
						"", strExpectedValue, strActualValue);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRSORTEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strSortOrder, "",
					strExpectedValue, StringConstants.STRSORTEXCEP);
			return false;

		}
		return bReturn;
	}
	
	
	
	/*******************************************************************************************************
	 * Function Name - klVerifyPagination 
	 * Function Description - This function verifies the pagination in case multiple pages are present
	 * InPuts - strXpath - Xpath of the Web Element , strName - Logical Name of the Web Element
	 * Prerequisite-Call klGetPageCount before this fn in the BPC to get page count
	 ********************************************************************************************************/

	public boolean klVerifyPagination(String strXpath, String strAllPageLinks,String strExpLinkStSingle,String strExpLinkStMult, String strLinkOrder) {
		String strExpectedValue = "Expected  the pagination verification to be successful";
		String strActualValue=StringUtils.EMPTY;
		String strNoOfPages="Total Pages";
		String strExpectedLinkStatus[]=null;
		boolean bResult=false;
		boolean bLinkStatus=false;
		int iFailCounter=0;
		int iRowNo=0;

		try{
			String strAllPageLink[]=StringUtils.split(strAllPageLinks,";");

			if (klIsElementPresent(strXpath, "Pagination")) {
				int iSize = Initialization.driver.findElements(By.xpath(strXpath)).size();
				strNoOfPages=strNoOfPages+" "+(iSize+1);
				String strLinkOrders[] = StringUtils.split(strLinkOrder,";");
				if(iSize>= 1 ){
					strExpectedLinkStatus=StringUtils.split(strExpLinkStMult,";");
				}
				else if(iSize == 0){
					strExpectedLinkStatus=StringUtils.split(strExpLinkStSingle,";");
				}				
				if(iPageOrRecordCount>1){
					for(iRowNo=0;iRowNo<strAllPageLink.length;iRowNo++){
						KeywordLibrary_LOGS.info(strAllPageLink[iRowNo]);
						//Verifying the links available
						bLinkStatus=Initialization.driver.findElement(By.xpath(strAllPageLink[iRowNo])).isEnabled();
						KeywordLibrary_LOGS.info(Integer.toString(strAllPageLink.length));
						KeywordLibrary_LOGS.info("iRowNo"+iRowNo);
						if(strExpectedLinkStatus != null && strExpectedLinkStatus.length != 0) {							
							if(bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("enabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is Enabled as expected";
								Initialization.driver.findElement(By.xpath(strAllPageLink[iRowNo])).click();
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" as expected and clicked");
								bResult=true;
							}else if(bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("disabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is not Disabled as expected";
								iFailCounter++;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" not as expected");
								bResult=false;
							}else if(!bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("disabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is Disabled as expected";
								bResult=true;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" as expected");
							}else if(!bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("enabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is not Enabled as expected";
								iFailCounter++;
								bResult=false;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" not as expected");
							}
						}
						//To make sure the loop is executed only specified number of times as in config file to avoid infinite loop
						if(iRowNum>=Integer.parseInt(Initialization.strMaxIteration)){
							KeywordLibrary_LOGS.info("the value of iRownum is "+iRowNum);
							KeywordLibrary_LOGS.info("the value of strMaxIteration is "+Initialization.strMaxIteration);
							break;
						}
					}
				}
				else{
					strActualValue="Single page available, hence no multiple pages to check pagination";
					KeywordLibrary_LOGS.info(strActualValue);
					iFailCounter=0;
				}


				if(iFailCounter==0){
					bResult=true;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bResult, "Page Links",
							strNoOfPages, strExpectedValue, strActualValue);
				}
				else{
					bResult=false;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bResult, "Page Links",
							strNoOfPages, strExpectedValue, strActualValue);
				}

			} else {
				strActualValue = "Verification unsuccessful";
				KeywordLibrary_LOGS.warn(strActualValue);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links",
						strNoOfPages, strExpectedValue, strActualValue);
				bResult=false;
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			bResult=false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRPAGINATIONERR);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRPAGINATIONERR);
			bResult=false;
		}
		return bResult;
	}
	/*******************************************************************************************************
	 * Function Name - klCheckSelectedPageNo 
	 * Function Description -This function checks the page number in a multiple page records on the browser
	 * 
	 ********************************************************************************************************/
	public boolean klCheckSelectedPageNo(String strXpath, String strPageNo) {
		String strActualPage = StringUtils.EMPTY;
		String strExpectedValue = "Page Number should be as expected";
		String strActualValue = StringUtils.EMPTY;
		String strValue = "Page No is " + strPageNo;
		try {
			if (klIsElementPresent(strXpath, "PageNo")) {
				strActualPage = this.wbElement.getText().trim();
			}
			if (iPageOrRecordCount >= Integer.parseInt(strPageNo)) {
				if (StringUtils.equalsIgnoreCase(strActualPage,strPageNo)) {
					strActualValue = "Page No is as expected " + strActualPage;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true,
							strValue, "" + strActualPage, strExpectedValue,
							strActualValue);
					return true;
				}
				strActualValue = "Given Page number " + strPageNo
				+ " does not match with actual Page No "
				+ strActualPage;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false,
						strValue, "" + strActualPage, strExpectedValue,
						strActualValue);
				return false;
			}
			strActualValue = "Only Single page available on web ";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strValue,
					"" + strActualPage, strExpectedValue, strActualValue);
			return true;
		} catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strValue, ""
					+ strActualPage, strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		} catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "Element Not Found";
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strValue, ""
					+ strActualPage, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRVERIFYPAGEEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strValue, ""
					+ strActualPage, strExpectedValue, StringConstants.STRVERIFYPAGEEXCEP);
			return false;

		}
	}
	
	
	/*******************************************************************************************************
	 * Function Name - klGetChildCount
	 * Function Description -This function gets the total number of child present inside the parent node.
	 * eg)<div id="example">
	 * <ul id='div_child_1'>1st Child for example id
	 * <li> 1st child for div_child_1</li>
	 * <li> 2nd child for div_child_1</li>
	 * <li> 3rd child for div_child_1</li>
	 * </ul>
	 * </div>
	 * klGetChildCounts(xpath=div[@id='example'])= gives count as 1 
	 * klGetChildCounts(xpath=div[@id='example']/ul[@id='div_child_1'])= gives child count as 1
	 *  klGetChildCounts(xpath=div[@id='example']/ul[@id='div_child_1']/li)= gives child count as 3
	 * we need to pass the Xpath correctly so that it will give u the correct number of children for
	 * the respective parents. This function can be used when we need to dynamically pass the number of child count
	 * to any xpath and get all the values for the child.
	 * for example.xpath = div[@id='example']/ul[@id='div_child_1']/li[childcount].Here 'childcount' = 3, 
	 * so, it gets the value for 3 child when we iterate using 'for' loop condition
	 * 
	 ********************************************************************************************************/

	public int klGetChildCount(String strXpath) {
		int iChildCount =0;
		try {
			if (klIsElementPresentNoReport(strXpath)) {
				iChildCount=Initialization.driver.findElements(By.xpath(strXpath)).size();
				KeywordLibrary_LOGS.info("The total number of Children : "+iChildCount);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRVERIFYCHILDCOUNT);
		}
		return iChildCount;
	}
	
	
	/*******************************************************************************************************
	 * Function Name		:	klWebElementCount
	 * Function Description	:	This function gets the number of children(count) for the given Xpath(parent)
	 * 
	 ********************************************************************************************************/

	public int klWebElementCount(String strXpath) {
		try {
			iPageOrRecordCount=0;
			if (klIsElementPresentNoReport(strXpath)) {
				iPageOrRecordCount=Initialization.driver.findElements(By.xpath(strXpath)).size();
				KeywordLibrary_LOGS.info("The total number of child counts are "+iPageOrRecordCount);
			}
		} catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRVERIFYPGCOUNTER);
		}
		return iPageOrRecordCount;
	}
	
	
	
	/*******************************************************************************************************
	 * Function Name		:	klRefreshPage 
	 * Function Description	:	This function refreshes the page
	 * 
	 ********************************************************************************************************/
	public void klRefreshPage() throws Exception {
		try{
			Initialization.driver.navigate().refresh();	 				
		}
		catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.info(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);

			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);

		} catch(Exception e)
		{
			KeywordLibrary_LOGS.error("Function '"+Thread.currentThread().getStackTrace()[1].getMethodName()+"' NOT able to execute due to following error: "+e.getMessage()+ "and trace :"+e.getStackTrace()); 
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("Error while refreshing the current page...");
		}
	}

	/*******************************************************************************************************
	 * Function Name		:	klGetCurrentPageURL 
	 * Function Description	:	This function will get the current page URL and return back to the calling method
	 *
	 * @return Current web page URL (String)
	 * 
	 ********************************************************************************************************/
	
	public String klGetCurrentPageURL() throws Exception {
		String strCurrentPageURL =StringUtils.EMPTY;
		try{
			//This will simulate the browser back event
//			Initialization.driver.navigate().back();
			strCurrentPageURL = 	Initialization.driver.getCurrentUrl();
		KeywordLibrary_LOGS.info("URL is :"+strCurrentPageURL);
		}
		catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch(Exception e)
		{
			KeywordLibrary_LOGS.error("Function '"+Thread.currentThread().getStackTrace()[1].getMethodName()+"' NOT able to execute due to following error: "+e.getMessage()+ "and trace :"+e.getStackTrace()); 
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("Error while trying to get the current page URL");
		}
	 	return strCurrentPageURL;
	}
	
	//go back to url 
	
	public void klGoBackToURL(String strGoToURL) throws Exception {
		try{
			//This will simulate the browser back event
			Initialization.driver.get(strGoToURL);
		KeywordLibrary_LOGS.info("Navigated to URL :"+strGoToURL);
		}
		catch (UnreachableBrowserException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch(Exception e)
		{
			KeywordLibrary_LOGS.error("Function '"+Thread.currentThread().getStackTrace()[1].getMethodName()+"' NOT able to execute due to following error: "+e.getMessage()+ "and trace :"+e.getStackTrace()); 
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("Error while trying to navigate and go to navigated URL");
		}
	}
	
	


	/*******************************************************************************************************
	 * Function Name		:	klMailInRepairCheckPartsPrice 
	 * Function Description	:	For MailIn Repair, this function checks a part's price, and returns true if
	 * 						:	all parts are having non zero prices in Parts table in the confirm screen
	 * 
	 ********************************************************************************************************/
	public boolean klMailInRepairCheckPartsPrice(String strXpath, String strName) {

		String strNetPrice = StringUtils.EMPTY;
		String strpartNumber = StringUtils.EMPTY;
		String strExpectedValue="Price value should be greater than zero";
		String strActualValue=StringUtils.EMPTY;
		int netPriceCol = 0;
		
		try {

			//to get the row count
			strXpath = strXpath + "/tr";
			int i=0;
			//to get the rows in the table
			int rowCount = Initialization.driver.findElements(By.xpath(strXpath)).size();
			KeywordLibrary_LOGS.info("Number of rows: " + rowCount);
			String tempXpath = strXpath + "[1]/td";			
			int colCount = Initialization.driver.findElements(By.xpath(tempXpath)).size();
			//to find the location of Net Price column
			for(i = 1; i <= colCount; i++) {
				
				String tempColumn = tempXpath + "[" + i + "]";
				String colName = Initialization.driver.findElement(By.xpath(tempColumn)).getText();
				if(colName.compareToIgnoreCase("Net Price") == 0) {
					
					netPriceCol = i;
					break;
				}
				
			}

			//looping through each row. Row number starts from 1. To skip the header row i is started from 2
			for( i = 2; i <= rowCount; i++) {
				try {    				
					String tempColumn = strXpath + "[" + i + "]/td[" + netPriceCol + "]";
					strNetPrice = Initialization.driver.findElement(By.xpath(tempColumn)).getText();
					tempColumn = strXpath + "[" + i + "]/td[1]";
					strpartNumber = Initialization.driver.findElement(By.xpath(tempColumn)).getText();

					//converting blank entries to 0.0 price
					if(strNetPrice.isEmpty() && !strpartNumber.isEmpty()) {

						strNetPrice = "USD 0.0";
					}
					else if(strNetPrice.isEmpty() && strpartNumber.isEmpty()) {

						//some times selenium counts one row additionally, in such cases parsing can cause exception. 
						//Eventhough there are only 3 rows displayed in UI, sometimes seelenium counts it as 4 
						continue;
					}
					//Seperating string USD and price value
					String tempNetPrice[] = StringUtils.split(strNetPrice," ");
					float fNetPrice = 0.0f;
					//if there is only price, but not the currency
					if(tempNetPrice.length == 1) {
						
						fNetPrice = Float.parseFloat(tempNetPrice[0]);
					}
					//if there is price and currency, then the price will come as the 2nd element
					else {
						
						fNetPrice = Float.parseFloat(tempNetPrice[1]);
					}
					
					if(fNetPrice == 0.0f || fNetPrice == 0) {

						//false is returned for zero price
						strActualValue = "Price for " + tempNetPrice[0] + " part is zero";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
						return false;
					}    				

				}
				//some times selenium counts one row additionally, in such cases exception happens
				catch(NoSuchElementException e) {

					continue;
				}
				catch(WebDriverException e) {

					continue; 
				}
				catch(Exception e) {

					continue;
				}

			}
			//all parts have prices defined
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "", strExpectedValue, strActualValue);
			return true;
		}
		catch(UnreachableBrowserException e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;    		
		}
		catch(WebDriverException e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;
		}
		catch(Exception e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;
		}
	}



	/*******************************************************************************************************
	 * Function Name		:	klCarryInRepairCheckPartsPriceDoNotReplenish 
	 * Function Description	:	For Carry In Repair with DoNot Replenish option checked, this function checks 
	 * 						:	a part's price, and returns true if all parts are having Blanks prices in Parts 
	 * 						:	table in the confirm screen.
	 * 
	 ********************************************************************************************************/
	public boolean klCarryInRepairCheckPartsPriceDoNotReplenish(String strXpath, String strName) {

		String strNetPrice = StringUtils.EMPTY;
		String strpartNumber = StringUtils.EMPTY;
		String strExpectedValue="Price value should Blank";
		String strActualValue=StringUtils.EMPTY;

		try {

			//to get the row count
			String strXpathRow = strXpath + "/tr";
			String strXpathCol = strXpath + "/tr[1]/td";
			int i=0;
			//to get the rows in the table
			int rowCount = Initialization.driver.findElements(By.xpath(strXpathRow)).size();
			//to get the columns in the table
			int colcount = Initialization.driver.findElements(By.xpath(strXpathCol)).size();

			KeywordLibrary_LOGS.info("Number of rows: " + rowCount);
			KeywordLibrary_LOGS.info("Number of columns: " + colcount);

			//looping through each row. Row number starts from 1. To skip the header row i is started from 2
			for( i = 2; i <= rowCount; i++) {
				try {   
					//Assuming that the price column will be the last column, so colcount variable is the position of the price column
					String tempColumn = strXpath + "/tr[" + i + "]/td[" + colcount + "]";
					strNetPrice = Initialization.driver.findElement(By.xpath(tempColumn)).getText();
					tempColumn = strXpath + "/tr[" + i + "]/td[1]";
					strpartNumber = Initialization.driver.findElement(By.xpath(tempColumn)).getText();

					//step fails if the price is not empty
					if(StringUtils.isNotBlank(strNetPrice)) {

						strActualValue = "Price for " + strpartNumber + " is not Blank";
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
						return false;
					}    				   				

				}
				catch(NoSuchElementException e) {

					continue;
				}
				catch(WebDriverException e) {

					continue; 
				}
				catch(Exception e) {

					continue;
				}

			}
			//all parts have prices as Blank
			strExpectedValue = "All parts have price set as Blank";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "", strExpectedValue, strActualValue);
			return true;
		}
		catch(UnreachableBrowserException e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;    		
		}
		catch(WebDriverException e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;
		}
		catch(Exception e) {

			strActualValue = "Element cannot be identified";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			return false;
		}
	}



	/*******************************************************************************************************
	 * Function Name		:	klSelectCurrentDateFromCalendar 
	 * Function Description	:	It selects today's date from the calendar
	 * 
	 ********************************************************************************************************/ 
	public void klSelectCurrentDateFromCalendar(String strXpath,String strName,String calendarWeek) throws Exception 
	{
		//For reporting purpose
		String strExpectedValue = "Click today's date in calendar.";
		KeywordLibrary_LOGS.info("strName " + strName);
		String strActualValue=StringUtils.EMPTY;
		try
		{
			String objCalendarDateSelection = StringUtils.EMPTY;

			int calendarWeekCount=0;
			int ctr=0;
			String todayDateUI=null;
			String todayDate=null;

			calendarWeekCount= Initialization.driver.findElements(By.xpath(calendarWeek)).size();

			if(klIsElementPresentNoReportExplicitWait(strXpath,100)) 
			{
				//click in textbox for calendar so that it is seen in UI.
				Initialization.driver.findElement(By.xpath(strXpath)).click();
				KeywordLibrary_LOGS.info("Clicked calendar textbox.");
				
				klWait(Initialization.strWaitTime);
				
				for(ctr=1;ctr<=calendarWeekCount;ctr++)
				{
					//XPath of calandar pop up
					objCalendarDateSelection="//table[@class='ui-datepicker-calendar']/tbody/tr["+ctr+"]/td[@class[contains(.,'today')]]/a";	

					if(klIsElementPresentNoReportExplicitWait(objCalendarDateSelection,5))
					{
						todayDateUI=this.wbElement.getText();
						KeywordLibrary_LOGS.info("todayDateUI : "+todayDateUI);

						//Getting today's date
						Date date = new Date();
						todayDate=String.valueOf(date).toString();
						KeywordLibrary_LOGS.info("todayDate :"+todayDate);

						if(todayDateUI.equals(todayDate)) 
						{
							Initialization.driver.findElement(By.xpath(objCalendarDateSelection)).click();
							KeywordLibrary_LOGS.info("Found today's date in calendar as todayDateUI and todayDate are equal..and clicked on it.");
							strActualValue = "Today's date in calendar selected .";
							KeywordLibrary_LOGS.info(strActualValue);
							ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName,"", strExpectedValue, strActualValue);
						}
					}
				}
			}


		}
		catch (UnreachableBrowserException e) 
		{
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();

		}
		catch (WebDriverException e) 
		{
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "WebDriver Exception";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",strExpectedValue, strActualValue);

		}
		catch (Exception e) 
		{
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			strActualValue = "Exception occurred while trying to click the button";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",strExpectedValue, strActualValue);
		}

	}
	
	/*******************************************************************************************************
	 * Function Name		:	klSelectDateFromCalendar 
	 * Function Description	:	Selects the date as per users wish.
	 * 
	 * Example				:	A numeric value as "0" is passed, then it will selects the current date
	 * 							when we pass numeric value as "1", then it will add +1 to the current date 
	 * 							and selects the resultant date when we pass number as "-1" , then it will 
	 * 							reduce the current date from that value, which means select the date which 
	 * 							is 1 day less than the current date.
	 * 
	 * Note					:	Do not append "+" if u need to add the number of days to the current date. 
	 * 							Since the code will take care about the addition internally. So DO NOT ADD 
	 * 							PREFIX AS "+" AT ANY TIME TO THE NUMBER VALUE
	 * 
	 * Parameters			:	bDataSource - which takes the strValue from the test data excel sheet
	 * 
	 ********************************************************************************************************/ 
	public void klSelectDateFromCalendar(String strXpath,String strName,String strValue,boolean bDataSource) 
			throws Exception 
	{
		int iDate,iYear;
		String sMonth = StringUtils.EMPTY;
		DateFormat df= new SimpleDateFormat("MM/dd/yyyy");
		DateFormatSymbols dfs = new DateFormatSymbols();
		KeywordLibrary_LOGS.info("strName : " + strName);
		String[] strCalenderObj = StringUtils.split(strXpath,";");
		try
		{
			strValue =  bDataSource ? fnGetParamValue(strValue) : strValue;
			if(Integer.parseInt(strValue) == 0)
				goToCorrectDate(strCalenderObj[2].toString(), strName);
			else{
			Calendar oCalDateCalendar = getDateForCalendar(strValue,df,dfs);
			iDate = oCalDateCalendar.get(Calendar.DATE);
			sMonth = dfs.getMonths()[oCalDateCalendar.get(Calendar.MONTH)];
			iYear = oCalDateCalendar.get(Calendar.YEAR);
			KeywordLibrary_LOGS.info("to select date :"+iDate);
			KeywordLibrary_LOGS.info("to select month :"+sMonth);
			KeywordLibrary_LOGS.info("to select Year :"+iYear);
			
			getToCorrectYear(strCalenderObj,strName, iYear);
			getToCorrectMonth(strCalenderObj,strName, sMonth,strValue);
			goToCorrectDate(String.format(strCalenderObj[3].toString(), iDate), strName);
			}
		}
		catch (Exception e) 
		{
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRCLKELEEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}

	}

	/******************************************************************************************
	 * Function Name		:	goToCorrectDate
	 * Function Description	:	This function will click on the date which the user suppose to select from the calendar
	 * 
	 * @param strName
	 * @param strXpath
	 * @throws Exception
	 * 
	 *******************************************************************************************/
	private void goToCorrectDate(String strXpath, String strName)
			throws Exception {
		try{
		if(klWebButtonClick(strXpath, strName))
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "","", "Selected Expected Date from the Calendar Object");
		else
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Not able to select Date from the Calendar Object");
		}catch(Exception e){
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRNULLPTREXCEP);
		}
	}

	/********************************************************************************************
	 * Function Name		:	getToCorrectMonth()
	 * Function Description	:	Selects the correct month from the provided date
	 * 
	 * @param strName
	 * @param sMonth
	 * @param sYear
	 * @throws InterruptedException
	 ********************************************************************************************/
	private void getToCorrectMonth(String[] strXpath,String strName, String sMonth,String sDateDiff)
			throws InterruptedException {
		try{
		if(klIsElementPresentNoReport(strXpath[1].toString())){
			boolean bMonthEqual = this.wbElement.getText().trim().toString().equalsIgnoreCase(sMonth);
			if(!bMonthEqual){
				boolean bClick= false;
				if(Integer.parseInt(sDateDiff) < 0){
				while(!bClick){
					if(klIsElementPresentNoReport(strXpath[4].toString())){
						this.wbElement.click();
						if(klIsElementPresentNoReport(strXpath[1].toString()))
							bClick = this.wbElement.getText().trim().toString().equalsIgnoreCase(sMonth);
						else{
							ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Month Not Present in Calender");
							bClick = true;
						}
					}else{
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Prev Arrow Button Not Present in Calender");
						bClick = true;
					}
				}
			}else if(Integer.parseInt(sDateDiff) > 0){
				while(!bClick){
					if(klIsElementPresentNoReport(strXpath[5].toString())){
						this.wbElement.click();
						if(klIsElementPresentNoReport(strXpath[1].toString()))
							bClick = this.wbElement.getText().trim().toString().equalsIgnoreCase(sMonth);
						else{
							ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Month Not Present in Calender");
							bClick = true;
						}
					}else{
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Next Arrow Button Not Present in Calender");
						bClick = true;
					}
				}

			}}
		}else
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Month Not Present in Calender");
		}catch(Exception e){
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error("Exception Occured in getToCorrectMonth()...");
		}
	}

	/********************************************************************************************
	 * Function Name		:	getToCorrectYear
	 * Function Description	:	This function will select the correct year which the user wants 
	 * 						:	to select from the calendar object
	 * 
	 * @param strName
	 * @param iYear
	 * @throws NumberFormatException
	 * @throws InterruptedException
	 *********************************************************************************************/
	private void getToCorrectYear(String[] strXpaths,String strName, int iYear)
			throws NumberFormatException, InterruptedException {
		try{
		if(klIsElementPresentNoReport(strXpaths[0].toString())){
			int iDiffYr = iYear - Integer.parseInt(this.wbElement.getText().trim().toString());
			if(iDiffYr < 0){
				boolean bClick= false;
				while(!bClick){

					if(klIsElementPresentNoReport(strXpaths[4].toString())){
						this.wbElement.click();

						if(klIsElementPresentNoReport(strXpaths[0].toString()))
							bClick = (Integer.parseInt(this.wbElement.getText().toString().trim()) == iYear);
						else{
							ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Year Not Present in Calender");
							bClick = true;
						}
					}else{
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Prev Arrow Button Not Present in Calender");
						bClick = true;
					}
				}
			}else if(iDiffYr > 0){
				boolean bClick= false;
				while(!bClick){

					if(klIsElementPresentNoReport(strXpaths[5].toString())){
						this.wbElement.click();

						if(klIsElementPresentNoReport(strXpaths[0].toString()))
							bClick = (Integer.parseInt(this.wbElement.getText().toString().trim()) == iYear);
						else{
							bClick = true;
							ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Year Not Present in Calender");
						}
					}else{
						bClick = true;
						ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Next Arrow Button Not Present in Calender");
					}
			}}
		}else
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "","", "Current Year Not Present in Calender");
		}
		catch(Exception e){
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			e.getStackTrace();
			KeywordLibrary_LOGS.error("Exception in getToCorrectYear() function...");
		}
	}
	
	/**********************************************************************************************
	 * Function Name		:	getDateForCalendar
	 * Function Description	:	This function will get the current date from the calendar and then 
	 * 						:	return back the correct date.
	 * 
	 * @throws NumberFormatException
	 *********************************************************************************************/
	private Calendar getDateForCalendar(String sDate,DateFormat df,DateFormatSymbols dfs) throws NumberFormatException {
		Date currentDate = new Date();
		Calendar oCalender = Calendar.getInstance();
		KeywordLibrary_LOGS.info("Current month in Number,"+oCalender.get(Calendar.MONTH));
		oCalender.setTime(currentDate);
		String[] s = StringUtils.split(df.format(currentDate),"/");
		KeywordLibrary_LOGS.info("Current month is :"+dfs.getMonths()[oCalender.get(Calendar.MONTH)]);
		KeywordLibrary_LOGS.info("Current Year is :"+s[1].toString());
		KeywordLibrary_LOGS.info("Current date is :"+s[2].toString());
		KeywordLibrary_LOGS.info(currentDate.toString());
		oCalender.add(Calendar.DAY_OF_MONTH,Integer.parseInt(sDate));
		KeywordLibrary_LOGS.info("Calculated month number,"+ oCalender.get(Calendar.MONTH));
		KeywordLibrary_LOGS.info("Calculated month name :"+dfs.getMonths()[oCalender.get(Calendar.MONTH)]);
		
		return oCalender;
	}
	

	/****************************************************************************************************
	 * Function Name		:	klIsElementPresentNoReportExplicitWait 
	 * Function Description	:	This will not report any error when there is an exception but
	 * 						:	this will report internally the exception to the log files when there is 
	 * 						:	some exception. Also, wait time can be altered according to the need.
	 * 
	 * InPuts				:	strXpath - Xpath of the Web Element  
	 ***************************************************************************************************/
	public boolean klIsElementPresentNoReportExplicitWait(String strFindProperty, int explicitWait) {
		boolean preCheckEle = false;
		try {
			preCheckEle = klFindElementCustomWait(strFindProperty,explicitWait);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.error("Exception occured in 'klIsEleNoRprtExplctWait'....");
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			preCheckEle = false;
		}
		finally
		{
			KeywordLibrary_LOGS.info("into finally statement for 'klIsEleNoRprtExplctWait'...");
		}
		return preCheckEle;

	}

	/*******************************************************************************************************
	 * Function Name		:	klWebEditTextChangeWithString 
	 * Function Description	:	This function is to enter string of length 'strLength' in the given
	 * 						:	text field, which is pointed by the xpath.
	 * 
	 ********************************************************************************************************/
	public boolean klWebEditTextChangeWithString(String strXpath, String strName,
			String strLength, String strValueType, boolean bDataSource) throws Exception {

		boolean bResult;
		int iLength, i;
		String strSubString = StringUtils.EMPTY;
		String strFinalString = StringUtils.EMPTY;
		String strActualValue = StringUtils.EMPTY;
		String strExpectedValue = "Expected to enter input data into text field";
		try {

			if (bDataSource == true) {
				strLength = super.fnGetParamValue(strLength);				
			}

			iLength = Integer.parseInt(strLength);
			i = 0;

			//if the required set of characters are with alphabets alone
			if(strValueType.compareToIgnoreCase("String") == 0) {

				strSubString = "abcde";
			}
			//if the required string is of number type
			else if(strValueType.compareToIgnoreCase("Number") == 0) {

				strSubString = "12345";
			}
			//if the required string is of alpha numeric type
			else if(strValueType.compareToIgnoreCase("AlphaNumeric") == 0) {

				strSubString = "abc12";
			}
			while(i < iLength) {

				strFinalString = strFinalString.concat(strSubString);
				i = i + strSubString.length();    			
			}
			//to truncate additional characters in the final string
			strFinalString = strFinalString.substring(0, iLength);
			boolean bInputtedValue = klWebEditTextChange(strXpath, strName, strFinalString, false);

			if(bInputtedValue == true) {    			
				strActualValue = "Input data entered successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strLength, strExpectedValue, strActualValue);
				bResult = true;
				//return true;
			}
			else {    			
				strActualValue = "Input data not entered successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
				bResult = false;
				//return false;
			}

		}

		catch (NoSuchElementException e) {

			strActualValue="Text Input field not found on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			bResult = false;
			//return false;
		} 

		catch (UnreachableBrowserException e) {	

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			bResult = false;
			//return false;
		}

		catch (WebDriverException e) {

			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "Text Input field not updated on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
			bResult = false;
			//return false;
		} 

		catch (Exception e) {

			KeywordLibrary_LOGS.error("\t"+StringConstants.STRTEXTINPEXCEP);
			strActualValue = "Exception occurred while text input";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
			bResult = false;
			//return false;
		}

		return bResult;
	}

	/****************************************************************************************************************************
	 * Function Name		:	klWebEditTextChangeWithStringWithUIValidation 
	 * Function Description	:	This function is applicable for all text input fields in which, level 0 validation happens
	 * 						:	for the maximum number of charecters that can be inputted
	 * 						:	This function enters a string of length 'strLength'(which is greater than 'strAllowedMaxLength')
	 * 						:	in the given text field (pointed by the xpath) and checks whether characters are entered only till
	 * 						:	'strAllowedMaxLength'.
	 *****************************************************************************************************************************/

	public boolean klWebEditTextChangeWithStringWithUIValidation(String strXpath, String strName, String strLength, String strAllowedMaxLength, 
			String strValueType, boolean bDataSource) throws Exception {

		boolean bResult = false;
		int iAllowedLength = 0;
		String strActualValue = StringUtils.EMPTY;
		String strExpectedValue = "Maximum length of the input field should be " + strAllowedMaxLength;

		try {

			if (bDataSource == true) {
				strLength = super.fnGetParamValue(strLength);			
				strAllowedMaxLength = super.fnGetParamValue(strAllowedMaxLength);	
			}

			iAllowedLength = Integer.parseInt(strAllowedMaxLength);

			//inputing the string in the text field. Usually strLength should be greater than strAllowedLength, then only -ve testing can be done
			boolean bInputtedValue = klWebEditTextChangeWithString(strXpath, strName, strLength, strValueType, false);
			if(bInputtedValue == true) {

				//To get the string that got inputted in the text field 
				String strActualStringInputted = Initialization.driver.findElement(By.xpath(strXpath)).getAttribute("value");
				//checking whether the length of string in UI is greater than the allowed limit. 
				if(strActualStringInputted.length() <= iAllowedLength) {

					strActualValue = "Input string restricted correctly";
					ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, strLength, strExpectedValue, strActualValue);
					bResult = true;
				}
				else {

					strActualValue = "Input string not restricted correctly";
					ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
					bResult = false;
				}

			}
			else {

				strActualValue = "Input data not entered successfully";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
				bResult = false;
			}    		

		}
		catch (NoSuchElementException e) {
			strActualValue="Text Input field not found on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			bResult = false;
		} 

		catch (UnreachableBrowserException e) {	
			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			strActualValue="Unreachable Browser Exception hence following steps and BPC are skipped";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			RecoveryScn.recUnReachableTestScnExitTrue();
			bResult = false;
		}

		catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			strActualValue = "Text Input field not updated on the webpage";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
			bResult = false;
		} 
		catch (Exception e) {

			KeywordLibrary_LOGS.error("\t"+StringConstants.STRTEXTINPEXCEP);
			strActualValue = "Exception occurred while text input";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strLength, strExpectedValue, strActualValue);
			bResult = false;
		}

		return bResult;
	}
	
	
	/*******************************************************************************************************
	 * Function Name		:	klReportMandatoryFieldResult 
	 * Function Description	:	This function is to report the result for mandatory field validation.

	 ********************************************************************************************************/
	public void klReportMandatoryFieldResult(boolean result , String strName){
		String strActualValue =  StringUtils.EMPTY;
		String strExpectedValue = strName+" field is mandatory and cannot be empty";
		try{
			if(result){
				strActualValue=strName+" is empty. Hence mandatory field error is thrown.";
				KeywordLibrary_LOGS.info("\t"+StringConstants.STRMANDCHKPASS);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strName, "", strExpectedValue, strActualValue);
			}else{
				strActualValue=strName+" is empty. But mandatory field error is not thrown.";
				KeywordLibrary_LOGS.info("\t"+StringConstants.STRMANDCHKFAIL);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "", strExpectedValue, strActualValue);
			}
		}
		catch(Exception e)
		{
			KeywordLibrary_LOGS.error(StringConstants.STRMANDCHKEXCEP);
			strActualValue = "Exception occurred while checking for mandatory fields";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",strExpectedValue, strActualValue);
		}

	}
	/*******************************************************************************************************
	 * Function Name		:	klVerifyPaginationbkup 
	 * Function Description	:	This function verifies the pagination in case multiple pages are present
	 * 
	 * InPuts				:	strXpath - Xpath of the Web Element
	 * 						:	strName - Logical Name of the Web Element
	 * 
	 * Prerequisite			:	Call klGetPageCount before this fn in the BPC to get page count
	 ********************************************************************************************************/

	public boolean klVerifyPaginationbkup(String strXpath, String strAllPageLinks,String strExpLinkStSingle,String strExpLinkStMult, String strLinkOrder) {
		String strExpectedValue = "Expected  the pagination verification to be successful";
		String strActualValue= StringUtils.EMPTY;
		String strNoOfPages="Total Pages";
		String strExpectedLinkStatus[]=null;
		boolean bResult=false;
		boolean bLinkStatus=false;
		int iFailCounter=0;

		try{
			String strAllPageLink[]=StringUtils.split(strAllPageLinks,";");

			if (klIsElementPresentNoReport(strXpath)) {
				WebElement lSize = Initialization.driver.findElement(By.xpath(strXpath));
				List<?> divElements = lSize.findElements(By.tagName("a"));

				KeywordLibrary_LOGS.info("Number of Pages "+divElements.size());
				int iSize = divElements.size()+1;
				iPageOrRecordCount=iSize;
				strNoOfPages=strNoOfPages+" "+(iSize);
				String strLinkOrders[] = StringUtils.split(strLinkOrder,";");
				if(iSize>= 1 ){
					strExpectedLinkStatus=StringUtils.split(strExpLinkStMult,";");
				}
				else if(iSize<1){
					strExpectedLinkStatus=StringUtils.split(strExpLinkStSingle,";");
				}				
				if(iSize>1){
					for(int iRowNo=0;iRowNo<strAllPageLink.length;iRowNo++){
						KeywordLibrary_LOGS.info(strAllPageLink[iRowNo]);
						//Verifying the links available
						bLinkStatus=Initialization.driver.findElement(By.xpath(strAllPageLink[iRowNo])).isEnabled();
						KeywordLibrary_LOGS.info(Integer.toString(strAllPageLink.length));
						KeywordLibrary_LOGS.info("iRowNo"+iRowNo);
						if(strExpectedLinkStatus != null && strExpectedLinkStatus.length != 0) {							
							if(bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("enabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is Enabled as expected";
								Initialization.driver.findElement(By.xpath(strAllPageLink[iRowNo])).click();
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" as expected and clicked");
								bResult=true;
							}else if(bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("disabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is not Disabled as expected";
								iFailCounter++;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" not as expected");
								bResult=false;
							}else if(!bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("disabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is Disabled as expected";
								bResult=true;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" as expected");
							}else if(!bLinkStatus && strExpectedLinkStatus[iRowNo].equalsIgnoreCase("enabled")){
								strActualValue=strActualValue+"\n"+"The Link"+strLinkOrders[iRowNo]+" is not Enabled as expected";
								iFailCounter++;
								bResult=false;
								KeywordLibrary_LOGS.info("Link is "+strExpectedLinkStatus[iRowNo]+" not as expected");
							}
						}

					}
				}
				else{
					strActualValue="Single page available, hence no multiple pages to check pagination";
					KeywordLibrary_LOGS.info("Single page available, hence no multiple pages to check pagination");
					iFailCounter=0;
				}


				if(iFailCounter==0){
					//strActualValue = "Verification successful";
					bResult=true;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bResult, "Page Links",
							strNoOfPages, strExpectedValue, strActualValue);
				}
				else{
					//strActualValue = "Verification unsuccessful";
					bResult=false;
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bResult, "Page Links",
							strNoOfPages, strExpectedValue, strActualValue);
				}

			} else {
				strActualValue = "Verification unsuccessful";
				KeywordLibrary_LOGS.error(strActualValue);
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links",
						strNoOfPages, strExpectedValue, strActualValue);
				bResult=false;
			}
		}catch (UnreachableBrowserException e) {

			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
			return false;
		}catch (WebDriverException e) {
			KeywordLibrary_LOGS.error(StringConstants.STRWEBDRVEXCEP);
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			bResult=false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Page Links", strNoOfPages,
					strExpectedValue, StringConstants.STRCHKLOG);
			KeywordLibrary_LOGS.error(StringConstants.STRCHKLOG);
			bResult=false;

		}
		return bResult;
	}

	/*******************************************************************************************************
	 * Function Name		:	klVPWebElmStringUserPreCondition Function 
	 * Function Description	:	(Checks only the abscence or presence of text)This function gets the text 
	 * 						:	value for the given path
	 * 
	 * Parameters			:	strFindProperty - Xpath of the WebElement
	 * 						:	strName - Logical Name of the WebElement
	 * 						:	bDataSource - value to be taken from data source
	 * 						:	sExpTxtStsKey - This is the boolean condition that sets whether the web element
	 * 						:					should contains the text or not
	 * 
	 * XML Report			:	If there is any mismatch btwn the expected and the actual, then this will report 
	 * 						:	based on the user expected boolean condition if you set the "bDataSource" 
	 * 						:	parameter to true , then "sWebElmTextStatus" param is mandatory and  
	 * 						:	"sExpTxtStsKey" param can be left Empty ("") : If sExpTxtStsKey : key passed from 
	 * 						:	excel , which should have a boolean value.
	 * 
	 * return type			:	void
	 * 
	 ********************************************************************************************************/
	public void klVPWebElmStringUserPreCondition(String strFindProperty, String strName,
			String sExpTxtStsKey, boolean bDataSource) throws Exception {
		String sActTxt = StringUtils.EMPTY; 
		String sActualStatus = StringUtils.EMPTY;
		boolean bActStatus =false;
		boolean sExpTxtSts = bDataSource ? Boolean.parseBoolean(super.fnGetParamValue(sExpTxtStsKey)) : Boolean.parseBoolean(sExpTxtStsKey);
		String sExpectedStatus = String.format(StringConstants.STREXPECTEDELEMENTTEXTSTATUS,sExpTxtSts);
		
		try {
			if (klIsElementPresent(strFindProperty,strName)) {
				sActTxt = this.wbElement.getText().toString();
				KeywordLibrary_LOGS.info("actualValue :'"+sActTxt+"'");
				boolean sActTxtSts = (sActTxt.trim().length() > 0);
				sActualStatus = (bActStatus = (sExpTxtSts == sActTxtSts)) ? String.format(StringConstants.STREXPECTEDELEMENTTEXTSTATUSSUCCS,sExpTxtSts,sActTxtSts) :
					String.format(StringConstants.STREXPECTEDELEMENTTEXTSTATUSFAILURE,sExpTxtSts,sActTxtSts,sActTxt);
					KeywordLibrary_LOGS.info(sActualStatus);
					//reporting to xml file based on status condition
					ReportingFunctionsXml.fnSetReportBPCStepStatus(bActStatus, strName, "",
							sExpectedStatus,sActualStatus);
			}
		} catch (UnreachableBrowserException e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, "",
					sExpectedStatus, StringConstants.STRUNRECBROWEXCEP);
			RecoveryScn.recUnReachableTestScnExitTrue();
		}catch (WebDriverException e) {
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", sExpectedStatus, StringConstants.STRWEBDRVEXCEP);
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC
					+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
					"", sExpectedStatus, StringConstants.STRCHKLOG);
		}
	}

	
	/*******************************************************************************************************
	 * Function Name		:	klGetTableHeaderCount Function 
	 * Function Description	:	This function will get the number of headers that are present in the table.
	 * 						:	for eg) check with the tier screen where u will get the table which shows 
	 * 						:	the tier part details.  The radio button column, part mumber, part description, 
	 * 						:	estimated price. so here the number of header count is 4. This we need in order 
	 * 						:	to display the values of tables in the record.
	 * 
	 * Return Type			:	int(number of header count)
	 * 
	 * Note					:	This will not report the presence of table or not. This will just get the 
	 * 						:	number of header count in it. so it is recommended to use the kl which will 
	 * 						:	get the presence of table (klisElementPresent())
	 * 
	 ********************************************************************************************************/
	public int klGetTableHeaderAndRowCount(String strFindProperty) throws Exception {
		int iHeaderCount = 0;
		try {
			if (klIsElementPresentNoReport(strFindProperty)) {
				iHeaderCount = Initialization.driver.findElements(By.xpath(strFindProperty)).size();
			}
		} catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRCHKLOG.concat("Method").concat("klGetTableHeaderCount()"));
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		return iHeaderCount;
	}
	
	
	/*******************************************************************************************************
	 * Function Name		:	klSplAnStrTxnIdWithKey
	 * Function Description	:	This functions reads a message banner and stores the confirmation ids under 
	 * 						:	the keyvalue mentioned in the datasheet
	 * 
	 ********************************************************************************************************/

	public boolean klSplAnStrTxnIdWithKey(String strKeyValue, boolean bDatasource)
	{
		String strExpectedValue = "The Txn Id is stored in a variable strTxnId";
		String strActualValue =  StringUtils.EMPTY;
		String strValue = "Null";


		if(bDatasource) {

			strKeyValue = super.fnGetParamValue(strKeyValue);
			if(strKeyValue.isEmpty()) {

				// Case where no keyvalue mentioned in the data sheet
				strExpectedValue = "Key value should be mentioned in the datasheet";
				strActualValue = "There is no key value mentioned in the data sheet";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Key Value", "", strExpectedValue, strActualValue);
				return false;    			
			}
		}
		else {

			strKeyValue = "strTxnId";
		}


		try {
			String[] strSplit = null;
			if (strTemp.contains(":")) {
				strSplit = StringUtils.split(strTemp,":");
			} else {
				strSplit = StringUtils.split(strTemp,": ");
			}

			if (strSplit.length > 1)
			{
				strValue = strSplit[1].trim();
				klPutDynData(strKeyValue , strValue);           

				strActualValue   = "The transaction is successful with Id : "+strValue;
				ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strKeyValue,
						strValue, strExpectedValue, strActualValue);
				return true;
			}
			strValue = strSplit[0];
			strActualValue   = "The transaction is not successful";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strKeyValue,
					strValue, strExpectedValue, strActualValue);
			return true;

		}
		catch (IndexOutOfBoundsException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRINDEXOUTOFBOUND);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
					strValue, strExpectedValue, StringConstants.STRINDEXOUTOFBOUND);
			return false;
		}catch (NullPointerException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRNULLPTREXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
					strValue, strExpectedValue, StringConstants.STRNULLPTREXCEP);
			return false;
		}
		catch (WebDriverException e) {
			KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
					strValue, strExpectedValue, StringConstants.STRWEBDRVEXCEP);
			return false;
		} catch (Exception e) {
			KeywordLibrary_LOGS.error("\t"+StringConstants.STRSTOREVALERR);
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
					strValue, strExpectedValue, StringConstants.STRSTOREVALERR);
			return false;

		}

	}


	/*******************************************************************************************************
	 * Function Name		:	klGetDynDataWithKey 
	 * Function Description	:	This function retrieve the dynamic run time data value corresponding to the keyvalue mentioned in the sheet. 
	 * 							The same key which is used to store the confirmation ID should be used here
	 * Parameters			:	sKey (Key Name ,Variable Name to retrieve.) 
	 * 
	 ********************************************************************************************************/

	public String klGetDynDataWithKey(String strKeyValue, boolean bDatasource) {

		String strExpectedValue = "The Txn Id is stored in a variable strTxnId";
		String strActualValue =  StringUtils.EMPTY;

		if(bDatasource) {

			strKeyValue = super.fnGetParamValue(strKeyValue);
			if(!StringUtils.isNotBlank(strKeyValue)) {

				// Case where no keyvalue mentioned in the data sheet
				strExpectedValue = "Key value should be mentioned in the datasheet";
				strActualValue = "There is no key value mentioned in the data sheet";
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Key Value", "", strExpectedValue, strActualValue);
				return "false";    			
			}
		}
		else {

			strKeyValue = "strTxnId";
		}

		try {
			KeywordLibrary_LOGS.info("Retriving the Value from Hash Map");
			KeywordLibrary_LOGS.info("-------------------------------------");
			return hDynTestData.get(strKeyValue);

		}  catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRKEYLIBERR);
			KeywordLibrary_LOGS.error("    Function : fnExecuteBatch");
			KeywordLibrary_LOGS.error("    Message : " + e.getMessage());
			KeywordLibrary_LOGS.error("    Cause : " + e.getCause());
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			return "Error";
		}
	}
	
	
	/*******************************************************************************************************
	 * Function Name		:	klGetDynDataXls 
	 * Function Description	:	First it uses the key you passed , to get the value from excel. Then it uses this value , as a key for hashmap and gets the value from hashmap.  
	 * Returns				:	Value from hashmap as a string.												
	 * Inputs				:	String key
	 * 
	 ********************************************************************************************************/

	public String klGetDynDataXls(String strKeyValue) {
		
    	String strExpectedValue = "The Txn Id is stored in a variable strTxnId";
    	String strActualValue =  StringUtils.EMPTY;
		strKeyValue = super.fnGetParamValue(strKeyValue);
    		if(!StringUtils.isNotBlank(strKeyValue)) {
    			
    			// Case where no keyvalue mentioned in the data sheet
    			strExpectedValue = "Key value should be mentioned in the datasheet";
        		strActualValue = "There is no key value mentioned in the data sheet";
        		ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Key Value", "", strExpectedValue, strActualValue);
        		return "false";    			
    		}
    	
		try {
			KeywordLibrary_LOGS.info("Retreiving the Value from Hash Map");
			KeywordLibrary_LOGS.info("-------------------------------------");
			return hDynTestData.get(strKeyValue);

		}  catch (Exception e) {
			KeywordLibrary_LOGS.error(StringConstants.STRKEYLIBERR);
			KeywordLibrary_LOGS.error("    Function : fnExecuteBatch");
			KeywordLibrary_LOGS.error("    Message : " + e.getMessage());
			KeywordLibrary_LOGS.error("    Cause : " + e.getCause());
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			return "Error";
		}
	}
	
	
	 /*******************************************************************************************************
     * Function Name		:	klSplStrTxnIdXls
     * Function Description	:	This functions reads a message banner and stores the confirmation ids in a hashmap.
     * 
     * Note 				:	This kl ,has specially been made ,so that different keys can be entered into a hashmap. 
     * 						:	'klSplAnStrTxnId' allows us to store only 1 key(="strTxnId")
     * 						:	in the hashmap.Therefore, based on need decide :
     * 						:		Single id has to be stored : Use 'klSplAnStrTxnId'
     * 						:		Multiple ids have to be stored : Use 'klSplStrTxnIdXls'.
     ********************************************************************************************************/

     public boolean klSplStrTxnId(String strKeyValue,boolean bDatasource)
     {
     	String strExpectedValue = "The Txn Id is stored in a variable "+strKeyValue+"";
     	String strActualValue = StringUtils.EMPTY;
     	String strValue = "Null";
     
     	//Reading the value of the key ,from excel.
     	if(bDatasource){
     	
     		strKeyValue = super.fnGetParamValue(strKeyValue);
     		strExpectedValue= "The Txn Id is stored in a variable "+strKeyValue+"";
     		// Case where no keyvalue mentioned in the data sheet
     		if(strKeyValue.isEmpty()) {
     				KeywordLibrary_LOGS.error("There is no value mentioned in the data sheet corresponding to key input by the user.");
     			}
     	}
   
 	try {
 		String[] strSplit = null;
 		 if (strTemp.contains(":")) {
 			strSplit = StringUtils.split(strTemp,":");
 		 } else {
 			strSplit = StringUtils.split(strTemp,": ");
 		 }
 	    
 	     if (strSplit.length > 1)
 	     {
 	           strValue = strSplit[1].trim();
 	           klPutDynData(strKeyValue , strValue);           
 	          
 	           strActualValue   = "The transaction is successful with Id : "+strValue+" and "+strExpectedValue;
 	           ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strKeyValue,
 	                       strValue, strExpectedValue, strActualValue);
 	           return true;
 	     }
		strValue = strSplit[0];
		 strActualValue   = "The transaction is not successful";
		   ReportingFunctionsXml.fnSetReportBPCStepStatus(true, strKeyValue,
		               strValue, strExpectedValue, strActualValue);
		   return true;
 	    
 	     }
 	catch (IndexOutOfBoundsException e) {
 	   KeywordLibrary_LOGS.error("\t "+StringConstants.STRINDEXOUTOFBOUND);
 	   strActualValue = "IndexOutOfBoundsException occurred";
 	   ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
 	               strValue, strExpectedValue, strActualValue);
 	   return false;
 	}catch (NullPointerException e) {
 	KeywordLibrary_LOGS.error("\t "+StringConstants.STRNULLPTREXCEP);
 	strActualValue = "NullPointerException occurred";
 	ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
 	            strValue, strExpectedValue, strActualValue);
 	return false;
 	}
 	catch (WebDriverException e) {
 		   KeywordLibrary_LOGS.error("\t "+StringConstants.STRWEBDRVEXCEP);
 	     strActualValue = "WebDriver Exception";
 	     ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
 	                 strValue, strExpectedValue, strActualValue);
 	     return false;
 	} catch (Exception e) {
 	     strActualValue = "Exception occurred while storing the value";
 	     KeywordLibrary_LOGS.error("\t"+StringConstants.STRSTOREVALERR);
 	     ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strKeyValue,
 	                 strValue, strExpectedValue, strActualValue);
 	     return false;
 	
 	}
 	
 	}
     
     
 	/*******************************************************************************************************
 	 * Function Name		:	klUpdateRepairSelectPart 
 	 * Function Description	:	This function selects the newly added part(by taking the last added part from the list), in the update repair 'Parts' tab
 	 * 						:	This function also selects the first option from the drop down for the Comptia Code and Comptia Modifier
 	 * 
 	 * Parameters			:	strValue - this contains the part number of the newly added part
 	 * 
 	 ********************************************************************************************************/

 	public void klUpdateRepairSelectPart() {

 		String strExpectedValue = "The part is selected";
 		String strActualValue = StringUtils.EMPTY;
 		String listXpath = "//ul[@id='part_added_listing']/li";
 		int rowCount = Initialization.driver.findElements(By.xpath(listXpath)).size();
 		//the position of the newly added part in the list, will be row count - 1
 		int position = rowCount - 1; 
		KeywordLibrary_LOGS.info("Number of rows: " + rowCount); 
 		//String tempXpath = "//strong[@id='part_added_serial_number'][contains(.,'";
 		
 		String xpathPart = "//li[@id='part_";
 		String xpathComptiaCode = "_comptiaCodeDropDown']";
 		String xpathComptiaModifier = "_comptiaModifierDropDown']";

 		try {
 			
 			//to dynamically generate the xpath of the new added part
 			xpathPart = xpathPart + position + "_tab']/a/span";
 			//to dynamically generate the xpath of the Comptia code drop down for the newly added part
 			xpathComptiaCode = "//select[@id='part_" + position + xpathComptiaCode;
 			//to dynamically generate the xpath of the Comptia modifier drop down for the newly added part
 			xpathComptiaModifier = "//select[@id='part_" + position + xpathComptiaModifier;

 			strActualValue = "The part is selected";
 			klWebButtonClick(xpathPart, "Newly added part");
 			klSelectFirstDDLOption(xpathComptiaCode, "Comptia Code");
 			klSelectFirstDDLOption(xpathComptiaModifier, "Comptia Modifier");
 			
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, "PartNumber", "", strExpectedValue, strActualValue);

 		}  catch (Exception e) {
 			KeywordLibrary_LOGS.error("    Function : klSelectPart");
 			KeywordLibrary_LOGS.error("    Message : " + e.getMessage());
 			KeywordLibrary_LOGS.error("    Cause : " + e.getCause());
 			strActualValue = "The part is not selected";
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "PartNumber", "", strExpectedValue, strActualValue);
 			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
 			
 		}
 	}

 	
 	
 	/*******************************************************************************************************
	 * Function Name		:	klVerifyWebElementIsEnable() 
	 * Function Description	:	This function will check whether the provided object Xpath element state is
	 * 						:	enabled or disabled using "IsEnabled()" method provided by selenium 
	 *
	 ********************************************************************************************************/
	public boolean klVerifyWebElementIsEnable(String strXpath,String strName,String strExpVal,boolean bDataSource){
 		boolean bReturnStatus=false;
 		String strExpectedResult = StringUtils.EMPTY;
		try{
			strExpVal=bDataSource?fnGetParamValue(strExpVal):strExpVal;
			strExpectedResult = String.format(StringConstants.STREXPECTEDELEMENTSTATE, strExpVal);
			if(klIsElementPresentNoReport(strXpath))
				//To check for the actual and expected value
				bReturnStatus = (Boolean.parseBoolean(strExpVal) == this.wbElement.isEnabled());
		}
		catch(NoSuchElementException e){
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strExpVal,strExpectedResult, StringConstants.STRNOSUCHELEEXCEP);
		}
		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strExpVal, strExpectedResult, StringConstants.STRCHKLOG);
				}
		finally{
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strExpVal, strExpectedResult, 
					String.format(StringConstants.STREXPECTEDELEMENTSTATESUCCESS,strExpVal,Boolean.toString(bReturnStatus)));
		}
		return bReturnStatus;
	}
 	
 	
 	/*******************************************************************************************************
 	 * Function Name		:	klVerifyNumericRange
 	 * Function Description	:	This function will compare the numeric values against the user defined numeric 
 	 * 						:	range values
 	 * 
 	 * Parameters			:	iValidateRangeNumber - input user number from the user
 	 * 						:	iUINumber - from application
 	 * 						:	strName - the user defined name
 	 * 						:	strVerifyRange - this is the range in which the user has to provide the range values :"lessthan",
 	 * 						:					"lessthanorequalto","greaterthan","greaterthanorequalto".
 	 * 						:					Make sure that we are entering the correcty string as the verifyrange
 	 * 						:	bDataSource - get the value from the data source sheet
 	 *
 	 ********************************************************************************************************/
 	public boolean klVerifyNumericRange(int iUINumber,int iValidateRangeNumber,String strName,String strVerifyRange,boolean bDataSource) throws Exception{
 			boolean bReturnStatus=false;
 		try{
 			strVerifyRange = (bDataSource) ? super.fnGetParamValue(strVerifyRange) : strVerifyRange;
			if(strVerifyRange.trim().length() == 0)
				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,strVerifyRange,
						String.format(StringConstants.STREXPECTEDNUMRANGE,iUINumber,strVerifyRange,iValidateRangeNumber), String.format(StringConstants.STREMPTYPARAMVAL,strVerifyRange,strName));
			
			if(strVerifyRange.equalsIgnoreCase("lessthan"))
				bReturnStatus = iUINumber < iValidateRangeNumber;
			else if(strVerifyRange.equalsIgnoreCase("lessthanorequalto"))
				bReturnStatus = iUINumber <= iValidateRangeNumber;
			else if(strVerifyRange.equalsIgnoreCase("greaterthan"))
				bReturnStatus = iUINumber > iValidateRangeNumber;
			else if(strVerifyRange.equalsIgnoreCase("greaterthanorequalto"))
					bReturnStatus = iUINumber > iValidateRangeNumber;
			else
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange,
								String.format(StringConstants.STREXPECTEDNUMRANGE,iUINumber,strVerifyRange,iValidateRangeNumber),
							String.format(StringConstants.STREXPECTEDNUMRANGEINVALID,strVerifyRange));

			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange,
							String.format(StringConstants.STREXPECTEDNUMRANGE,iUINumber,strVerifyRange,iValidateRangeNumber),
							bReturnStatus ? String.format(StringConstants.STREXPECTEDNUMRANGESUCCESS,iUINumber,strVerifyRange,iValidateRangeNumber) :
								String.format(StringConstants.STREXPECTEDNUMRANGEFAILURE,iUINumber,strVerifyRange,iValidateRangeNumber));
		
 		}
 		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange, "",StringConstants.STRFUNNOTEXEC);
				}
 		return bReturnStatus;
 		}
 	
 	/*******************************************************************************************************
 	 * Function Name		:	klVerifyNumberBetweeenRanges
 	 * Function Description	:	This function will compare the numeric values against the user defined numeric 
 	 * 						:	range values.  Make sure that we are entering the correcty string as the verifyrange
 	 * 
 	 * Parameter			:	iValidateRangeNumber1 - input user number from the user minvalue
 	 * 						:	iValidateRangeNumber2 - input user number from the user maxvalue
 	 * 						:	strName - the user defined name
 	 * 						:	strVerifyRange - this is the range in which the user has to provide the range values :"between",
 	 * 						:	bDataSource - get the value from the data source sheet
 	 *
 	 ********************************************************************************************************/
 	public boolean klVerifyNumberBetweeenRanges(int iUINumber,int iValidateMinRange,int iValidateMaxRange,String strName,String strVerifyRange) throws Exception{
 			boolean bReturnStatus=false;
 		try{
			if(StringUtils.equalsIgnoreCase(strVerifyRange,"between"))
				bReturnStatus = (iUINumber >=  iValidateMinRange && iUINumber <= iValidateMaxRange);
			else
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange,
								String.format(StringConstants.STREXPECTEDNUMRANGE,iUINumber,strVerifyRange,iValidateMinRange,iValidateMaxRange),
							String.format(StringConstants.STREXPECTEDNUMRANGEINVALID,strVerifyRange));

			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange,
							String.format(StringConstants.STREXPECTEDNUMRANGE,iUINumber,strVerifyRange,iValidateMinRange,iValidateMaxRange),
							bReturnStatus ? String.format(StringConstants.STREXPECTEDNUMRANGEBTWNSUCCESS,iUINumber,strVerifyRange,iValidateMinRange,iValidateMaxRange) :
								String.format(StringConstants.STREXPECTEDNUMRANGEBTWNFAILURE,iUINumber,strVerifyRange,iValidateMinRange,iValidateMaxRange));
		
 		}
 		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			KeywordLibrary_LOGS.error(StringConstants.STRFUNNOTEXEC+ e.getMessage());
			ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnStatus, strName,strVerifyRange, "",StringConstants.STRFUNNOTEXEC);
				}
 		return bReturnStatus;
 		}
 	
 	/*******************************************************************************************************
 	 * Function Name		:	klGetDateDifference 
 	 * Function Description	:	This function is to obtain the difference between the current date and one
 	 * 						:	previous date fetched from excel sheet.
 	 * 
 	 ********************************************************************************************************/
 	
 	public String klGetDateDifference(String strPrevDate, boolean bDataSource) throws Exception {
		String strCurrentDate = StringUtils.EMPTY;
		String strDateDiff = StringUtils.EMPTY;
		String expValue = "To calculate difference in dates";
		String actValue = StringUtils.EMPTY;
		
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Date dtToday = Calendar.getInstance().getTime();
			strCurrentDate = df.format(dtToday);
			KeywordLibrary_LOGS.info("\t Current Date: " + strCurrentDate);
			strPrevDate = (bDataSource ) ? super.fnGetParamValue(strPrevDate) : strPrevDate;
			Date currentDate = df.parse(strCurrentDate);
			Date prevDate = df.parse(strPrevDate);    
			long dateDiff = (currentDate.getTime()-prevDate.getTime())/(24*60*60*1000);
			KeywordLibrary_LOGS.info("Date difference is : " +dateDiff);
			strDateDiff = "-"+dateDiff;
			KeywordLibrary_LOGS.info("Difference in string format : " +strDateDiff);
			actValue = "Date difference correctly calculated";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(true, "Date Difference", "", expValue, actValue);

		}  catch (Exception e) {
			KeywordLibrary_LOGS.error("\t Failed to Get the Difference between Dates.");
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			actValue = "Date difference not correctly calculated";
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, "Date Difference", "", expValue, actValue);
		}
		return strDateDiff;
	}

 	
 	
 	
 	/*******************************************************************************************************
 	 * Function Name		:	klUserReport
 	 * Function Description	:	This function is used to do reporting based on user's need. we can pass the 
 	 * 						:	expeced and actual values from the executing BPC itself. This is customized 
 	 * 						:	way of reporting the reports to the XML file
 	 * 
 	 * Parameters			:	strParamName --this paramaeter is used to user defined name
 	 * 						:	strParamValue --this parameter value 
 	 * 						:	strExpResult - this is the user expected result text message
 	 * 						:	strActResult  --this is the  actual result 
 	 * 
 	 * @return void
 	 * @param bFlag
 	 *
 	 ********************************************************************************************************/
 	public void klUserReport(boolean bFlag,String strParamName,String strParamValue,String strExpResult, String strActResult)
 	{
 		try
 		{
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(bFlag, strParamName,strParamValue, strExpResult,strActResult);
 		}
 		catch (Exception e) {
			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
 	}


 	
 	
 	/*******************************************************************************************************
 	 * Function Name		:	klVerifyElementContainsTextPresent 
 	 * 
 	 * Function Description	:	This function gets the actual text value for the given path and verifies 
 	 * 						:	with the Expected text value.
 	 * 
 	 * Parameters			: 	strXpath - Xpath of the WebElement
 	 * 						:	strName - the user defined name
 	 *  					:	iCount - the no. of records on the page
 	 *  					:	iStartingPosition - Indicates the starting position of the first row.It can be either '0' or '1'
 	 * 						:						 Pass '0' if row starts from tr[0] otherwise 1 if row starts from tr[1]
 	 * 						:	strExpValue - the expected value that needs to be passed;
 	 *  					:	bDataSource - pass the value as 'true' if the value needs to get from data sheet else pass 'false'
 	 * 
 	 ********************************************************************************************************/

 	public boolean  klVerifyElementContainsTextPresent(String strXpath, String strName,int iCount,int iStartingPosition,
 			String strExpValue, boolean bDataSource) throws Exception{
 		boolean bReturnValue = false;	
 		String strExpVal = StringUtils.EMPTY;	
 		String actualValue = StringUtils.EMPTY;
 		try {
 			
 			if(bDataSource && super.fnGetParamValue(strExpValue).trim().length() == 0){
 				ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
 						StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STRKEYVALUEMISSING,strExpValue));
 			}else{
 				strExpVal = (bDataSource)? super.fnGetParamValue(strExpValue) : strExpValue;
 				for(int i=0 ; i < iCount ; i++) {							
 				// Format the Xpath which is passed as a parameter
 					if (klIsElementPresentNoReport(String.format(strXpath,i+iStartingPosition))) {
 				actualValue = this.wbElement.getText().toString();
 				// checks whether expected string present inside the actual string or not
 					KeywordLibrary_LOGS.info("actualValue "+actualValue);
 					KeywordLibrary_LOGS.info("expectedvalue "+strExpVal);
 					if (bReturnValue = actualValue.equals(strExpVal)) {
 					ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnValue, strName, strExpVal,
 								StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALSUCCS, strExpVal,actualValue));					
 				} else 
 						ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnValue, strName,strExpVal,
 								StringConstants.STREXPSHDMATCHACTUAL, String.format(StringConstants.STREXPMATCHACTUALFAILURE, strExpVal,actualValue));											
 					}				 
 			else
 				ReportingFunctionsXml.fnSetReportBPCStepStatus(bReturnValue, strName, strExpValue,
 						StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRSHDELEFOUNDFAILURE);
 				}
 		 }
 	}
 		 catch (UnreachableBrowserException e) {
 			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
 			KeywordLibrary_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName, strExpValue,
 					StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRUNRECBROWEXCEP);
 			RecoveryScn.recUnReachableTestScnExitTrue();
 		}catch (WebDriverException e) {
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
 					strExpValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRWEBDRVEXCEP);
 			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
 		} catch (Exception e) {
 			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strName,
 					strExpValue, StringConstants.STREXPSHDMATCHACTUAL, StringConstants.STRFUNNOTEXEC);
 			KeywordLibrary_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
 		}
 		return bReturnValue;
 	       }
 	  	
 	 }



 	
 	

