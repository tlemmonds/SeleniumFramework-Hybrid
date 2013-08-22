package com.autotest.libFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autotest.bpcLib.BPCLibrary;



/**************************************************************************************************************************
* ClassName		:	CommonFile
* 
* Description  	:	This class holds the function fnExecuteBatch to execute
* 				:	the batch based on the data received from the scenario excel file
* 				:	the fnExecuteBPC calls the corresponding Execute function in the BPCLibrary class
* 
*************************************************************************************************************************/

public class CommonFile{
	String strClassNamePassed = StringUtils.EMPTY;
	public static String strStepsToExecute = StringUtils.EMPTY;
	public static int iBPCCounter =1;
	public static int iTestCasesCounter = 0;
	public static int iStepsToExeCount = 0;
	public static int iBpcExecutingCaseNum = 0;
	public static ArrayList<String> arrScenarioExcelData = new ArrayList<String>();
	public static ArrayList<String> arrBatchExcelData = new ArrayList<String>();
	public static ArrayList<Integer> arrResultRowList = new ArrayList<Integer>();
	public static ArrayList<String> arrResultCellColor = new ArrayList<String>();
	public static HashMap<String, String> hTestData = new HashMap<String, String>();
	ReportingFunctionsXml objReportingXml = null;
	
	private static final Logger CommonFile_LOGS = LoggerFactory
			.getLogger(CommonFile.class);
	
	public enum EIntergerValues{
		//declare the values which are needed for Enum values, this is simply grouping the data
		BLANK("Check for BLANK"),
		LESSTHANZERO("Check for LESS THAN ZERO VALUE"),
		EQUALTOZERO("Check for EQUAL TO ZERO VALUE"),
		GREATERTHANZERO("Check for GREATER THEN ZERO VALUE");
		
		private final String strDescription;
		//this will initialize the values when the Enum is called for the first time
		//This is the constructor
		EIntergerValues(String sDesc){
			this.strDescription = sDesc;
		}
		//This will return the value which is corresponding to the Enum values
		public String getDescription() {
			return this.strDescription;
		}
	}
	
	/**************************************************************************************************************************
	 * Function Name	:	fnCmCleanup
	 * Description		:	This is the function which Gets the Batch excel sheet
	 *               		and passes it to fnReadBatchXlsCell in the class DataReadWrite
	 *               
	 * @return void
	 **************************************************************************************************************************/
	public void fnCmCleanup()
	{
		BPCLibrary objCleanBpc = new BPCLibrary();
		objCleanBpc.bpcCleanup();
		CommonFile_LOGS.info("Calling the Memory Clean for common file");
		objCleanBpc = null;
		arrScenarioExcelData = null;
		arrBatchExcelData = null;          
	}
	
	/**************************************************************************************************************************
	 * Function Name	:	fnExecuteBatch
	 * Description		:	This is the function which Gets the Batch excel sheet
	 *               		and passes it to fnReadBatchXlsCell in the class DataReadWrite
	 *               
	 * @return void
	 *************************************************************************************************************************/
	public void fnExecuteBatch(final String strBatchFilePath){
		try{
			CommonFile_LOGS.info(String.format(StringConstants.STRSTARTTIMEFORMAT,ReportingFunctionsXml.strFileSuffix));
			CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
			fnReadBatchXlsCell(strBatchFilePath, Initialization.strBatchSheetName);
			ReportingFunctionsXml.dtBatchStartTime = ReportingFunctionsXml.dtValue;
			ReportingFunctionsXml.fnGetExecutionTime(ReportingFunctionsXml.dtBatchStartTime,"Batch");
		}catch(Exception e){
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}
	/**************************************************************************************************************************
	 * Function Name	:	fnReadBatchXlsCell
	 * Description		:	This is the function which reads the batch excel sheet
	 * 						and passes each row to fnReadScenarioXlsCell to read individual scenario
	 * 
	 * @return void
	 *************************************************************************************************************************/					
	@SuppressWarnings("boxing")
	private void fnReadBatchXlsCell(String strXlsPath,String strSheetName)  {
		try {
			String strExecFlag = StringUtils.EMPTY;
			String strGroupXls = StringUtils.EMPTY;
			String strScenarioXls = StringUtils.EMPTY;
			Cell executionFlag =null;
			
			File fBatchFile = new File (strXlsPath);
			if (fBatchFile.exists()){
				Workbook workbook = Workbook.getWorkbook(fBatchFile);
				Sheet sheet = workbook.getSheet(strSheetName);
				if(sheet!=null){
					int iTotalRows = sheet.getRows();
			for (int iRowCounter = 1; iRowCounter < iTotalRows; iRowCounter++)
			{
				executionFlag = sheet.getCell(0, iRowCounter);
				strExecFlag = executionFlag.getContents();
				CommonFile_LOGS.info(String.format(StringConstants.STRCHECKSCENARIO, iRowCounter));
				if (StringUtils.equalsIgnoreCase(strExecFlag, Initialization.pConfigFile.getProperty("FLAGYES"))) {
					iTestCasesCounter++;
					arrResultRowList.add(iRowCounter);
					//get the group name which is the workbook name
					ReportingFunctionsXml.strScnGroupName = sheet.getCell(1, iRowCounter).getContents();
					//get the Scenario name which is the test case scenario name mostly the sheet name  
					ReportingFunctionsXml.strScenarioName =  sheet.getCell(2, iRowCounter).getContents();
					//get the Description of the test case that we are about to execute  
					ReportingFunctionsXml.strScenarioDesc = sheet.getCell(3, iRowCounter).getContents();
					
					arrBatchExcelData.add(ReportingFunctionsXml.strScenarioName);
					//current framework only supports .xls format for the file input alone
					strGroupXls = Initialization.strScenarioFilePath+ReportingFunctionsXml.strScnGroupName+Initialization.pConfigFile.getProperty("FLAGSHEETFORMAT");
					strScenarioXls = Initialization.strScenarioFilePath+ReportingFunctionsXml.strScenarioName+Initialization.pConfigFile.getProperty("FLAGSHEETFORMAT");
					 
					CommonFile_LOGS.info(String.format(StringConstants.STRCALLINGFUNCTION,Thread.currentThread().getStackTrace()[1].getMethodName(),strScenarioXls));
					CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
					CommonFile_LOGS.info(String.format(StringConstants.STREXEGROUPSTARTS,ReportingFunctionsXml.strScnGroupName));
					CommonFile_LOGS.info(String.format(StringConstants.STREXESCENARIOSTARTS,ReportingFunctionsXml.strScenarioName));
					CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
					 
					//Added code to test the XML Result
					ReportingFunctionsXml.addTestScenarioNode();
					
 					fnReadScenarioXlsCell(strGroupXls,ReportingFunctionsXml.strScenarioName,-1);
					
 					
					CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
					CommonFile_LOGS.info(String.format(StringConstants.STREXESCENARIOSTOPS,ReportingFunctionsXml.strScenarioName));
				} 
				else
				{
					hTestData.clear();
					CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
					CommonFile_LOGS.info(StringConstants.STRSCENARIONOTEXECUTE);
				}
				CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
			}
				}
				else{
					CommonFile_LOGS.error(StringConstants.STRNOSHEETERROR);
					throw new NullPointerException();
				}
			}
			else
			{
				CommonFile_LOGS.error(StringConstants.STRNOFILEERROR);
				throw new FileNotFoundException();
			}
		}catch (IndexOutOfBoundsException e) {
			CommonFile_LOGS.error(StringConstants.STRINDEXOUTOFBOUND);
			CommonFile_LOGS.error(StringConstants.STRBATCHDETAILSERROR);
			CommonFile_LOGS.error(StringConstants.STRMANDATORYFIELDMISSINGERROR);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}catch (FileNotFoundException e) {
			CommonFile_LOGS.error(StringConstants.STRCHECKAPPCONFIGFILE);
			CommonFile_LOGS.error(StringConstants.STRFILENOTFOUND);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		} catch (IOException e) {
			CommonFile_LOGS.error(StringConstants.STRIOEXCEPTION);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}catch (NullPointerException e) {
			CommonFile_LOGS.error(StringConstants.STRNOSHEETERROR);
			CommonFile_LOGS.error(StringConstants.STRNULLPTREXCEP);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}catch(Exception e){
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}
	/**************************************************************************************************************************
	 * Function Name	:	fnReadScenarioXlsCell
	 * Description		:	This is the function which reads the Scenario excel sheet
	 * 						and passes the data to fnExecuteScenario in the class DriverScript
	 * 
	 * @return void
	 *************************************************************************************************************************/
	private void fnReadScenarioXlsCell(String strXlsPath,String strScnName,int iRow)  {
		try{
			int iRowCounter=0;
			int iColCounter = 0;
			int iTotalColumns = 0;
			int iTotalRows = 0;
			Cell cell = null;
			String strTempValue = StringUtils.EMPTY;
			
			File fScnXLSFile = new File(strXlsPath);
			//Check for the Existence of the test data excel sheet
			if (fScnXLSFile.exists()) {
				Workbook workbook = Workbook.getWorkbook(fScnXLSFile);
					/*******************************/	
					Sheet sheet= workbook.getSheet(strScnName);
					arrBatchExcelData.add(strScnName);
					CommonFile_LOGS.info("scenario name is :"+strScnName);
					 iTotalColumns = sheet.getColumns();
					 iTotalRows = sheet.getRows();
					//to read the row in the sheet
					if (iRow == -1) {
					RecoveryScn.recTestScnExitFalse(); 
					
					for (iRowCounter = 1; iRowCounter<iTotalRows && KeywordLibrary.isTestScenarioContinue; iRowCounter++)
					{
						CommonFile_LOGS.info(String.format(StringConstants.STRCHECKBPCNUM, iRowCounter));
						CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
						//This "if" check is necessary since we need to avoid unnecessary reading and storing of variables inside the array list
						//when the scenario is set to "No" in the test data sheet
						strTempValue = sheet.getCell(0, iRowCounter).getContents();
						if(StringUtils.isNotBlank(strTempValue) && !StringUtils.equalsIgnoreCase(strTempValue, Initialization.pConfigFile.getProperty("FLAGNO"))){
						// row starting from 1 since the first row is header
						for (iColCounter = 0; iColCounter < iTotalColumns; iColCounter++) 
						{
							cell = sheet.getCell(iColCounter, iRowCounter);
							if(StringUtils.isNotBlank(cell.getContents())) {
								arrScenarioExcelData.add(cell.getContents());
							}else break;
						}
							CommonFile_LOGS.info(StringConstants.STRBPCSETYESINFO);
							 if(RecoveryScn.recRetTstScnExitFlag() && arrScenarioExcelData.size() > 0)// This functions would return the Scenario exit flag value
				             {
								 //This will set the name of the bpc node on the reporting XML
								 ReportingFunctionsXml.strExecutingMethodName = "BPC_"+iRowCounter+"_"+arrScenarioExcelData.get(2).toString();
								 ReportingFunctionsXml.strScenarioNameWithBPCCounter = ReportingFunctionsXml.strScenarioName.trim().concat("_BPC_").concat(Integer.toString(iRowCounter));
								 ReportingFunctionsXml.addBPCNode();
								 fnExecuteScenario(arrScenarioExcelData,iRowCounter);
				             }
				             else
				                         CommonFile_LOGS.error(StringConstants.STRSCENARIOFAILEDANDEXIT);        
						}
						else
						{
							CommonFile_LOGS.info(StringConstants.STRBPCNOTOEXECUTE);
						}
						CommonFile_LOGS.info(StringConstants.STRSIMPLEDIVIDER);
						arrScenarioExcelData.clear();
						hTestData.clear();
						CommonFile_LOGS.info("arrScenarioExcelData "+arrScenarioExcelData);
						CommonFile_LOGS.info("hTestData "+hTestData);
					}
				}
					
				}
			else{
				throw new FileNotFoundException();
			}
			ReportingFunctionsXml.fnGetExecutionTime(ReportingFunctionsXml.dtValue,ReportingFunctionsXml.strScenarioName);
			//to set the BPC count as 1 before executing a new scenario
			iBPCCounter=1;
		}
		catch(FileNotFoundException e){
			CommonFile_LOGS.error(StringConstants.STRFILENOTFOUND);
			CommonFile_LOGS.error(String.format(StringConstants.STRNOSCENARIOFILEEXIST,strXlsPath));
			ReportingFunctionsXml.iStepFailCounter=-1;
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch(NullPointerException e){
			CommonFile_LOGS.error(String.format(StringConstants.STRNOSCENARIOFILEEXIST,strXlsPath));
			CommonFile_LOGS.info("iScenarioFlag"+ReportingFunctionsXml.iScenarioFlag);
			
			CommonFile_LOGS.error(StringConstants.STRNULLPTREXCEP);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (IOException e) {
			CommonFile_LOGS.error(StringConstants.STRIOEXCEPTION);
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch (Exception e) {
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}
	
	
	/**************************************************************************************************************************
	 * Function Name	:	fnExecuteScenario
	 * Description		:	This is the function which Gets the data from the
	 * 						Scenario excel sheet and passes it to fnExecuteBPC in the class CommonFile
	 * 
	 * @return void
	 *************************************************************************************************************************/
	private void fnExecuteScenario(ArrayList<?> arrScenarioExcelData,int iBPCCounter){
		try{
			iBPCCounter++;
			fnExecuteBPC(arrScenarioExcelData);
		}
		catch(Exception e){
			CommonFile_LOGS.info(StringConstants.STRDRIVERSCRIPTERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
	}
	/**************************************************************************************************************************
	* Function Name :	fnExecuteBPC
	* Description 	:	This is the function which calls the Execute function  
	* 				:	in the BPCLibrary class based on the input from the scenario excel sheet
	* 
	* @return void
	*************************************************************************************************************************/		
	
	private void fnExecuteBPC(ArrayList<?> arrScenarioInput){
		Method method = null;
		Object objInvoke = null;
		ReportingFunctionsXml.strExecutingMethodName = arrScenarioInput.get(2).toString();
		try{
			strClassNamePassed = BPCLibrary.class.getCanonicalName();
			method = Class.forName(strClassNamePassed).getMethod(ReportingFunctionsXml.strExecutingMethodName);
			CommonFile_LOGS.info("\t The bpc method to be called is: "+ReportingFunctionsXml.strExecutingMethodName);
			objInvoke = Class.forName(strClassNamePassed).newInstance();
			fnGetStepsToExeCount(arrScenarioInput);
			method.invoke(objInvoke);
		}
		catch(NoSuchMethodException e){
			ReportingFunctionsXml.strExecutingMethodName =ReportingFunctionsXml.strExecutingMethodName +": Method Name not correct";
			ReportingFunctionsXml.iStepFailCounter = 1;
			CommonFile_LOGS.info(ReportingFunctionsXml.strExecutingMethodName);

			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		catch(Exception e){
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		finally {
			//Cleaning the object of the BPCLibrary objects
			objInvoke=null;
			method = null;
			ReportingFunctionsXml.iStepFailCounter = 0;
			ReportingFunctionsXml.iStepCounter = 0;
			iBPCCounter++;
		}
	}
	
	
	/**************************************************************************************************************************
	* Function Name :	fnGetStepsToExeCount
	* Description 	:	This is the function which sets the number of steps to be executed
	* 
	* @return String
	*************************************************************************************************************************/	
	private void fnGetStepsToExeCount(ArrayList<?> arryBPCInput) {
		try{
			strStepsToExecute =StringUtils.EMPTY;
			iStepsToExeCount = 0;
		CommonFile_LOGS.info(String.format("Executing the BPC :'%s' From the class :'%s'", 
				ReportingFunctionsXml.strExecutingMethodName,BPCLibrary.class.getSimpleName()));
		CommonFile_LOGS.info("\t The values passed are : "+arryBPCInput);
		iStepsToExeCount= (strStepsToExecute = arryBPCInput.get(1).toString()).length(); 
		}catch(Exception e){
			CommonFile_LOGS.info("Exception in :"+ReportingFunctionsXml.strExecutingMethodName);
		}
	}
	
	
	/**************************************************************************************************************************
	* Function Name	:	fnGetParamValue
	* Description 	:	This is the function which returns the parameter value to the Keyword library class based on the parameter
	* 				:	name passed from the Keyword library class
	* 		**** Make sure that the Key Text should be equal and case sensitive ****
	* 
	* @return <i>String value<b>(NON-Empty Text)</b> for the corresponding passed key when present else , return <b>Empty</b> String</i>
	* 
	*************************************************************************************************************************/	
	public String fnGetParamValue(String strParamName) {
		String strParamValue = StringUtils.EMPTY;
		int iKeyIndex = 0;
		try{
			CommonFile_LOGS.info("strParamName "+strParamName);
			if(arrScenarioExcelData.contains(strParamName)) {
				iKeyIndex = arrScenarioExcelData.indexOf(strParamName);
				CommonFile_LOGS.info("Key is present at the Index of :"+iKeyIndex);
				CommonFile_LOGS.info("Key at iKeyIndex value :"+arrScenarioExcelData.get(iKeyIndex));
				//To get the next immediate index value which is the correct Value for the Provided Key/Value Pair
				strParamValue = arrScenarioExcelData.size() >= (iKeyIndex+1) ? arrScenarioExcelData.get(iKeyIndex+1) : StringUtils.EMPTY;
				CommonFile_LOGS.info("Value is :"+strParamValue);
				if(hTestData.containsKey(strParamName)) {
					hTestData.remove(strParamName);
					CommonFile.hTestData.put(strParamName, strParamValue);
				}
			}
		}
		catch(Exception e){
			CommonFile_LOGS.error(StringConstants.STRCOMFILEERR);
			CommonFile_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
					e.getMessage(),e.getCause());
			CommonFile_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
		}
		finally {
			//to report when the key is not available in the test data sheet
			if(StringUtils.isBlank(strParamValue))
			ReportingFunctionsXml.fnSetReportBPCStepStatus(false, strParamName, strParamValue, String.format(StringConstants.STREXPRETURNPARAMETERVALUE,strParamName), 
						String.format(StringConstants.STREXPRETURNPARAMETERVALUEFAILURE,strParamName));
		}
		return strParamValue;
	}
	
}
		