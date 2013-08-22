package com.autotest.libFunctions;


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;





/**************************************************************************************************************************
* Class Name		:	ReportingFunctionsXml
* Class Description	:	This class handles report creation in xml
* 
*************************************************************************************************************************/
public class ReportingFunctionsXml{
	public static int iStepFailCounter = 0;
	public static int iScenarioFlag =0;
	public static int iStepCounter=0;
	public static int radarCount = 0;
	public static int iTestScenarioNum;
	public static int iBPCNum;
	public static int intCount=0;
	public static int iStepCount=1;
	public static String strScnGroupName = StringUtils.EMPTY;
	public static String strScenarioName=StringUtils.EMPTY;
	public static String strScenarioDesc=StringUtils.EMPTY;
	public static String strExecutingMethodName=StringUtils.EMPTY;
	public static String strRadarScenarioName=StringUtils.EMPTY;
	public static String strRadarScenarioDesc=StringUtils.EMPTY;
	public static String strScenarioNameWithBPCCounter = StringUtils.EMPTY;
	public static String strRadarNumber = StringUtils.EMPTY;
	public static String strReportFileName=StringUtils.EMPTY;
	public static String strTestSuiteName = "Batch-Run Execution Scenarioes";
	public static Date dtBatchStartTime;
	public static Deque<String> deque = new LinkedList<String>();
	public static Deque<String> radarNumbersList = new LinkedList<String>();
	public static Document newDoc;
	public static Element elRoot;
	public static Element elTestSuite;
	public static Element elTestScenario; 
	public static Element elBPC;
	public static Element elTime;
	public static StringBuffer sbufFailedSteps = new StringBuffer();
	public static String strExeStartTime =StringUtils.EMPTY; 
	Result dest = null;
	private static Element resultElement = null;
	static Date dtValue = new Date();
	static SimpleDateFormat sdfDate = new SimpleDateFormat ("dd_MMM_yyyy_HH_mm_ss_a");
	static String strFileSuffix = sdfDate.format(dtValue).toString();
	public static String strPresentTime = StringUtils.EMPTY;
	public static String strExecDuration = StringUtils.EMPTY;
	private static ReportingFunctionsXml objSingleton;
	
	private static final Logger ReportingFunctionsXml_LOGS = LoggerFactory.getLogger(ReportingFunctionsXml.class);

	/* A private Constructor prevents any other class from instantiating. */
	private ReportingFunctionsXml(){
		PropertyConfigurator.configure(Initialization.pConfigFile.getProperty("LOGPROPERTIES_FILEPATH"));
	}
	
	//to avoid creating more objects
	public static ReportingFunctionsXml getInstance(){
		if(objSingleton == null){
			objSingleton = new ReportingFunctionsXml();
		}
		return objSingleton;
	}
	
	
	/*************************************************************************************************************************************
	 * Function Name		:	createCustomReportFile
	 * Function Description	:	This function initializes the report xml and creates header
	 * 
	 ************************************************************************************************************************************/
	public static void createCustomReportFile()
	{
		DocumentBuilderFactory domFactory;
		DocumentBuilder domBuilder;
		ProcessingInstruction pi;
		try
		{
			strExeStartTime = getPresentTime("dd/MMM/yyyy hh:mm:ss a");
			domFactory = DocumentBuilderFactory.newInstance(); 
			domBuilder = domFactory.newDocumentBuilder(); 
			newDoc = domBuilder.newDocument();
			pi = newDoc.createProcessingInstruction("xml-stylesheet", "href='Report.xsl' type='text/xsl'");
			//append this to document.
			newDoc.appendChild(pi); 
			// Root element 
			elRoot = newDoc.createElement("Report"); 
			newDoc.appendChild(elRoot); 
			elTestSuite = newDoc.createElement("TestSuite");
			elTestSuite.appendChild(newDoc.createTextNode(strTestSuiteName));
			elRoot.appendChild(elTestSuite);

			elTestSuite.setNodeValue(strTestSuiteName);
			elTestSuite.setAttribute("startTime",strExeStartTime);
			elTestSuite.setAttribute("Desc",strTestSuiteName);  
		}
		catch(Exception e) 
		{
			ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
			fnDisplayExceptionDetails(e);
		}

	}

	/*************************************************************************************************************************************
	 * Function Name		:	addTestScenarioNode
	 * Function Description	:	This function adds a test scenario node
	 * 
	 ************************************************************************************************************************************/
	protected static void addTestScenarioNode()
	{
		elTestScenario = newDoc.createElement("TestCase"); 
		elTestScenario.appendChild(newDoc.createTextNode(strScenarioName));
		elTestSuite.appendChild(elTestScenario);
		String strPresentTime=getPresentTime("dd/MMM/yyyy hh:mm:ss a");
		elTestScenario.setNodeValue(strScenarioName);
		elTestScenario.setAttribute("startTime",strPresentTime);

		//To write empty Test scenario name in case of fatal error display
		if(StringUtils.isNotBlank(strScenarioName))
			elTestScenario.setAttribute("Desc",String.format("%s : %s", strScenarioName,strScenarioDesc));
		else
			elTestScenario.setAttribute("Desc","");

		//Include an attribute Status and initially set it as 1-Pass-To get the count of total,passed and failed test cases		
		elTestScenario.setAttribute("Status","1");
	}
	/*************************************************************************************************************************************
	 * Function Name		:	addTestScenarioNode
	 * Function Description	:	This function adds a test scenario node
	 * 
	 ************************************************************************************************************************************/
	protected static void addRadarToScenarioNode() throws Exception
	{
		if (StringUtils.isNotBlank(strRadarNumber))
		{
			//updating the scenario description with Radar Number
			strRadarScenarioName= String.format("%s : Failed-[%s] : %s",strScenarioName,strRadarNumber,strScenarioDesc);
			ReportingFunctionsXml_LOGS.info("Radar Scenario Name :"+strRadarScenarioName);
			elTestScenario.setAttribute("Desc",strRadarScenarioName);
			try {
				saveReport();
			} catch (Exception e) {
				fnDisplayExceptionDetails(e);
			}
		}
	}
	
	
	/*************************************************************************************************************************************
	 * Function Name		:	getPresentTime
	 * Function Description : This method gets the present time in the date format that is passed as parameter type (strFormatType)
	 * 
	 * @param strFormatType - has format of dd/MMM/YYYY.
	 * @return strPresentTime
	 * 
	 ************************************************************************************************************************************/
	public static String getPresentTime(String strFormatType) {
		String strPresentTime=StringUtils.EMPTY;
		Date objPresentDate = null;
		DateFormat mediumFormatter = null;
		try{
			//code added for reporting along with date and time
			objPresentDate = Calendar.getInstance().getTime();
			mediumFormatter=new SimpleDateFormat(strFormatType);
			strPresentTime=mediumFormatter.format(objPresentDate);			
		}
		catch (Exception e) {
			ReportingFunctionsXml_LOGS.error(StringConstants.STRKEYLIBERR);
			fnDisplayExceptionDetails(e);
		} 
		finally{
			//cleaning up the memory once the process is done
			objPresentDate = null;
			mediumFormatter = null;
		}
		return strPresentTime;
	}	

	
	/*************************************************************************************************************************************
	 * Function Name		:	addBPCNode
	 * Function Description	:	This function adds a bpc node
	 * 
	 *
	 ************************************************************************************************************************************/
	protected static void addBPCNode()
	{
		try
		{
			elBPC = newDoc.createElement("BP"); 
			elBPC.appendChild(newDoc.createTextNode(strExecutingMethodName));
			elTestScenario.appendChild(elBPC);
			String strPresentTime=getPresentTime("dd/MMM/yyyy hh:mm:ss a");

			elBPC.setAttribute("startTime",strPresentTime);
			elBPC.setAttribute("Desc",strExecutingMethodName);
		}
		catch (Exception e) 
		{		
			ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
			fnDisplayExceptionDetails(e);

		}		
	}

	
	/*************************************************************************************************************************************
	 * Function Name		:	addTimeNode
	 * Function Description	:	This function adds a time node
	 * 
	 ************************************************************************************************************************************/
	protected static void addTimeNode()
	{
		elTime = newDoc.createElement("Time"); 
		String strPresentTime=getPresentTime("dd/MMM/yyyy hh:mm:ss a");	
		elTime.appendChild(newDoc.createTextNode("Time"));
		elRoot.appendChild(elTime);
		elTime.setAttribute("StartTime",strExeStartTime);
		elTime.setAttribute("EndTime",strPresentTime); 	
		ReportingFunctionsXml_LOGS.info("the start time :"+strExeStartTime);
		ReportingFunctionsXml_LOGS.info("the end time :"+strPresentTime);
		String strExecDuration = fnCalcTimeDiff(strExeStartTime,strPresentTime);
		ReportingFunctionsXml_LOGS.info("Duration at save :"+strExecDuration);
		elTime.setAttribute("Duration",strExecDuration);
		
		//This will get the details of the build , platform Environment and the Environment URL
		if(StringUtils.isBlank(elTime.getAttribute("BuildEnvironment"))){
		elTime.setAttribute("BuildEnvironment",Initialization.strExecutionEnvironment);
		elTime.setAttribute("BuildURL",Initialization.strAppURL);
		elTime.setAttribute("BuildNumber",Initialization.strBuildNumber);
		elTime.setAttribute("BrowserAndVersion", Initialization.strBrowserName+"-"+Initialization.strBrowserVersion);
		elTime.setAttribute("OSAndVersion", System.getProperty("os.name")+"-"+System.getProperty("os.version"));
		}
		
		
	}
	/*************************************************************************************************************************************
	 * Function Name		:	addResultNode
	 * Function Description :	Adds result node
	 *  
	 ************************************************************************************************************************************/
	
	private static void addResultNode(String status,String result)
	{
		try
		{
			resultElement = newDoc.createElement("Result"); 
			elBPC.appendChild(resultElement);
			resultElement.setAttribute("Status",status);
			resultElement.appendChild(newDoc.createTextNode(result));
			//To add the failed steps if any
			if(StringUtils.equals("0",status)){
				sbufFailedSteps.append(", ");
				sbufFailedSteps.append(iStepCount);
				elTestScenario.setAttribute("Status","3");
				addImageNode();
			}
			//Call to save the report after action execution completes if enabled
				saveReport();
		}
		catch (Exception e) 
		{
			fnDisplayExceptionDetails(e);
		}
		//in this finally method we are making sure that the object allocated are again return back to its memory pool
		finally{
			resultElement = null;
		}
	}

	/*************************************************************************************************************************************
	 * Function Name		:	addImageNode
	 * Function Description :	Adds error image node
	 *  
	 ************************************************************************************************************************************/
	private static void addImageNode()
	{
		String sbufFilePath = StringUtils.EMPTY;
		File fBuffFilePath = null;
		try
		{
			resultElement = newDoc.createElement("Result"); 
			elBPC.appendChild(resultElement);
			strExecutingMethodName = strExecutingMethodName.replace(":", "");
			sbufFilePath=System.getProperty("user.dir")+"/ErrorImages/"+strScenarioName.trim()+"_"+getPresentTime("ddMMMhh_mm_ssa")+"."+Initialization.pConfigFile.getProperty("IMAGE_TYPE").trim();
			fBuffFilePath = new File(sbufFilePath);
			
			captureScreenShot(fBuffFilePath.getAbsolutePath().toString());
			//this is to set the attribute value to the image path so that once the user clicks on the link, the image file opens
			resultElement.setAttribute("ImagePath",fBuffFilePath.getAbsolutePath().toString().trim());
			}
		catch (Exception e) 
		{
			fnDisplayExceptionDetails(e);
		}      
		finally{
			fBuffFilePath = null;
			resultElement = null;
		}
	}


	/*************************************************************************************************************************************
	 * Function Name		:	captureScreenShot
	 * Function Description :	This method is used to get the dimensions of the screen for
	 * 						:	capturing screen shot
	 * 
	 * @param String fileName
	 * @return None
	 * 
	 ************************************************************************************************************************************/
	private static void captureScreenShot(String fileName)
	{
		File screenshot;
		try
		{
			
			
			screenshot = ((TakesScreenshot)Initialization.driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot,new File(fileName));
		}
		catch(Exception e)
		{
			ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
			fnDisplayExceptionDetails(e);
		}		
		finally{
			screenshot = null;
		}
	}
	

	/*************************************************************************************************************************
	 * Function Name		:	saveReport
	 * Function Description	:	Saves Report in xml format
	 * 
	 ************************************************************************************************************************/

	protected static void saveReport()
	{
		Result dest = null;
		Source src = null;
		TransformerFactory tranFactory = null;
		Transformer transformer = null;
		try
		{
			// Save the document to the disk file 
			tranFactory = TransformerFactory.newInstance(); 
			transformer = tranFactory.newTransformer(); 

			//Get the start and end of execution in the xml
			intCount++;
			//First time create the tag
			if(intCount==1)
				addTimeNode();
			else{
				//reset the end time and reset the duration
				strPresentTime=getPresentTime("dd/MMM/yyyy hh:mm:ss a");	
				elTime.setAttribute("EndTime",strPresentTime); 	
				ReportingFunctionsXml_LOGS.info("the start time :"+strExeStartTime);
				ReportingFunctionsXml_LOGS.info("the end time :"+strPresentTime);
				strExecDuration = fnCalcTimeDiff(strExeStartTime,strPresentTime);
				ReportingFunctionsXml_LOGS.info("Duration at save :"+strExecDuration);
				elTime.setAttribute("Duration",strExecDuration);
			}
			src = new DOMSource(newDoc);
			
			if(StringUtils.isBlank(strReportFileName))
			{
				fnSetResultFile();
				
			}
			dest = new StreamResult(new File(strReportFileName)); 
			transformer.transform(src, dest); 
		}
		catch(Exception e){
			ReportingFunctionsXml_LOGS.warn("Problem while performing the save operation");
			fnDisplayExceptionDetails(e);
		}
		//this is to release back the memory
		finally{
			tranFactory = null;
			transformer = null;
			src = null;
			dest = null;
		}
	}
	
	
	/**************************************************************************************************************************
	 * Function Name		:	fnSetReportBPCStepStatus
	 * Function Description :	This is the function which sets the BPC step status in the run report in excel form
	 * 
	 * @return void
	 *************************************************************************************************************************/
	protected static void fnSetReportBPCStepStatus(boolean bFlag,String strParamName,String strParamValue,String strExpResult, String strActResult)
	{
		//The result from keyword library updates the step status
		try{
		if (StringUtils.isBlank(strParamValue))
			strParamValue = "NA";

		//Test Code for Xml Result
		
		String strTagtype=KeywordLibrary.strElementType;
		if (StringUtils.isBlank(strTagtype))
			strTagtype="NA";
		String strResult = "Case:"+CommonFile.iBpcExecutingCaseNum+"-Name: "+strParamName+"||Type: "+strTagtype+"||Data: "+strParamValue+"||Expected Result: "+strExpResult+"||Actual Result: "+strActResult;
		
		String strRadarEntry="BPC Name-"+strExecutingMethodName+";"+"\n\n"+"Description :" + "\n"+strScenarioName.trim()+"\n\nType:"+strTagtype+"\n\nData:\n"+strParamName+":-"+strParamValue+"\n\nExpected Result :\n"+strExpResult+"\n\nActual Result :\n"+strActResult+"\n\n";
		//**Ravi_kiran03:Code for radar integration-End*/
		KeywordLibrary.strElementType=StringUtils.EMPTY;
		if(bFlag)
			addResultNode("1", strResult);
		else
		{
			addResultNode("0", strResult);
			deque.push(strRadarEntry.toString());
		}
			
		//End Test Code for xml Result
		}catch(Exception e){
		ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
		fnDisplayExceptionDetails(e);
	}
	}
	
	/**************************************************************************************************************************
	 * Function Name		:	fnGetExecutionTime
	 * Function Description :	This is the function is used to get start and end time for the scenario execution
	 * 
	 * @return void
	 * @throws InterruptedException 
	 *************************************************************************************************************************/
	protected static void fnGetExecutionTime(Date dtValue,String strName) throws NullPointerException, InterruptedException{
		Date dtEndValue;
		SimpleDateFormat sdfEndDate;
		try{
			dtEndValue = new Date();
			sdfEndDate = new SimpleDateFormat ("yyyy-MM-dd-hhmmss");
			ReportingFunctionsXml_LOGS.info("End time :"+sdfEndDate.format(dtEndValue).toString());
			int iExecutionTime = (int) (((dtEndValue.getTime()-dtValue.getTime())/1000)); 
			ReportingFunctionsXml_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
			ReportingFunctionsXml_LOGS.info("Total Execution time for "+strName+" in seconds : "+iExecutionTime);
			ReportingFunctionsXml_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
		}
		catch (Exception e) {
			ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
			fnDisplayExceptionDetails(e);
		}finally{
			dtEndValue = null;
			sdfEndDate = null;
		}
	}

	/**************************************************************************************************************************
	 * Function Name		:	fnCalcTimeDiff
	 * Function Description :	This is the function to get the time difference between two date in hh:mm:ss format
	 * @return string
	 *************************************************************************************************************************/
	public static String fnCalcTimeDiff(String strStartTime,String strEndTime) {
		SimpleDateFormat sdfFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");  
		Date dtStart = new Date();
		Date dtEnd = new Date();
		long diff = 0;
		
		try {
		       dtStart = sdfFormat.parse(strStartTime);
		       dtEnd = sdfFormat.parse(strEndTime);
		       diff= dtEnd.getTime() - dtStart.getTime();
		       ReportingFunctionsXml_LOGS.info("Total Time Duration : "+DurationFormatUtils.formatDurationWords(diff,true,true));
		     
		} catch (ParseException e) {
			fnDisplayExceptionDetails(e);
		    return null;
		} catch (NullPointerException e) {
			fnDisplayExceptionDetails(e);
		    return null;
		} catch (Exception e) {
			fnDisplayExceptionDetails(e);
		    return null;
		}
		return DurationFormatUtils.formatDurationWords(diff, true, true) ;
	}


	/****************************************************************************************************
	 * Function Name		:	fnDisplayExceptionDetails
	 * Function Description	:	This is the function to display the exception information to the logger
	 * 
	 * @return void
	 * 
	 *****************************************************************************************************/
	private static void fnDisplayExceptionDetails(Exception e) {
		ReportingFunctionsXml_LOGS.error(String.format(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getMessage(),e.getCause()));
		ReportingFunctionsXml_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
	
	}
	
	/****************************************************************************************************
	 * Function Name		:	fnSetResultFile
	 * Function Description	:	This function would set the result file name and location
	 * 
	 * @return void
	 *  
	 ****************************************************************************************************/
	private static void fnSetResultFile()
	{
		try 
		{
			String strBrowserPrefix = Initialization.strConfigBrowser;
			strBrowserPrefix = strBrowserPrefix.substring(0 , 4);
			strReportFileName = Initialization.strResultReportPath+"/Xml/"+Initialization.pConfigFile.getProperty("OUTPUT_BATCH_FILE_NAME").concat("_")+strBrowserPrefix+"_"+strFileSuffix.replace(",", "")+".xml";
				
		}
		catch (NullPointerException e) 
		{
			ReportingFunctionsXml_LOGS.error(StringConstants.STRNULLPTREXCEP);
			fnDisplayExceptionDetails(e);
		} 
		catch (Exception e) 
		{
			ReportingFunctionsXml_LOGS.error(StringConstants.STRREPFUNCERR);
			fnDisplayExceptionDetails(e);
		}
	}
}
