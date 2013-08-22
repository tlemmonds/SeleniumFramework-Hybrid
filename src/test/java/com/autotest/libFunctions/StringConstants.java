package com.autotest.libFunctions;


/**************************************************************************************************************************
* ClassName		:	StringConstants
* 
* Description	:	This class contains only the constant strings which can be used in all the other classes
*   			:	Enter all the constant strings in this class and make sure that the strings are all final static variables 
*   			:	You can modify it in one place which can be reflected in all other places/class files
*   			:	This is the better approach to make sure that all the constant variables are declared in one file
*   			:	so that in future , if we need to change any text , then we can change in one class file alone
*   			:	which will reduce the time of code change and easy for maintaining the code as well.
* 
*************************************************************************************************************************/

public class StringConstants {
	
	public final static String STRVALUESNOTEQUAL = "\tValues are NOT EQUAL.";
	public final static String STRVALUESEQUAL = "\tValues are EQUAL.";
	public final static String STREXPECTEDHYPERLINKSTATUS = "Expected value for hyperlink status to be :%s ";
	public final static String STREXPECTEDHYPERLINKSTATUSSUCCESS = "SUCCESS - Expected value for hyperlink status to be :%s and the actual value of hyperlink status is: %s";
	public final static String STREXPECTEDHYPERLINKSTATUSFAILURE = "FAILURE - Expected value for hyperlink status to be :%s and but the actual value of hyperlink status is: %s";
	public final static String STRCHKBTWNSCREEN ="Expected value from the FIRST validation page SHOULD MATCH with actual value from the SECOND validation page.";
	public final static String STRCHKBTWNSCREENSUCCS ="Expected value from the FIRST validation page :'%s' MATCHED with actual value from the SECOND validation page :'%s'.";
	public final static String STRCHKBTWNSCREENFAILURE ="Expected value from the FIRST validation page :'%s' DOES NOT MATCHES with actual value from the SECOND validation page :'%s'.";
	public final static String STREXPCHKBOXSTATEON ="Expected to set the Checkbox :'%s' 'ON' state.";
	public final static String STREXPCHKBOXSTATEONSUCCS ="Checkbox :'%s' is setted to 'ON' state as expected Successfully.";
	public final static String STREXPCHKBOXSTATEONFAILURE ="Unable to Set the Checkbox :'%s' to 'ON' state.Please Verify Manually....";
	public final static String STRCHKBOXSTATEISON ="Already the Checkbox :'%s' state is set to 'ON'.Please Verify Manually....";
	public final static String STREXPCHKBOXSTATEOFF ="Expected to set the Checkbox :'%s' 'OFF' state.";
	public final static String STREXPCHKBOXSTATEOFFSUCCS ="Checkbox :'%s' is setted to 'OFF' state as expected Successfully.";
	public final static String STREXPCHKBOXSTATEOFFFAILURE ="Unable to Set the Checkbox :'%s' to 'OFF' state.Please Verify Manually....";
	public final static String STRCHKBOXSTATEISOFF ="Already the Checkbox :'%s' state is 'set to OFF'.Please Verify Manually....";
	public final static String STREXPSHDMATCHACTUAL = "Expected Value SHOULD MATCH with Actual Value.";
	public final static String STREXPMATCHACTUALFAILURE = "FAILED - Expected Value :'%s' is NOT matching with the Actual Value :'%s'.";
	public final static String STREXPMATCHACTUALSUCCS = "SUCCESS - Expected Value :'%s' is MATCHING with the Actual Value :'%s'.";
	public final static String STRKEYVALUEMISSING = "No Corresponding value found for the Parameter :'%s' in the test data sheet.Please verify it Manually...";
	public final static String STRSTEPEXEINFO= "\tThe step needs to be executed, \n\t but no Operation information found for this step.";
	public final static String STRPLACEHOLDEREXPVAL ="Expected PLACEHOLDER attribute value :'%s' SHOULD MATCH with the WebElement placeholder :'%s'.";
	public final static String STRPLACEHOLDEREXPVALSUCCS ="Expected PLACEHOLDER attribute value :'%s' MATCHES with the actual placeholder value :'%s'.";
	public final static String STRPLACEHOLDEREXPVALFAILURE ="Expected PLACEHOLDER attribute value :'%s' DOES NOT MATCHES with the actual placeholder value :'%s'.";
	public final static String STRDDLDEFVAL ="Expected to get the DEFAULT VALUE :'%s' selected item from weblist box :'%s'.";
	public final static String STRDDLDEFVALSUCCS ="SUCCESSFULLY GOT the Expected DEFAULT VALUE :'%s' from the weblist box :'%s'.";
	public final static String STRDDLDEFVALFAILURE ="FAILED to get the DEFAULT VALUE :'%s' the from the weblist box :'%s'. Please verify it manually....";
	public final static String STRBPCEXEINFO ="\tThe flag is 0, hence no need to execute the step.";
	public final static String STREXPSHDNOTMATCHACTUAL = "Expected value :'%s' SHOULD NOT MATCHES or PRESENT with actual value :'%s'.";
	public final static String STREXPSHDNOTMATCHACTUALSUCCS = "SUCCESS - Expected value :'%s' DOES NOT MATCHS or PRESENT with actual value :'%s'.";
	public final static String STREXPSHDNOTMATCHACTUALFAILURE = "FAILURE - Expected value :'%s' MATCHES or PRESENT with actual value :'%s'.";
	public final static String STREXPDIGITPRESENCETRUE = "Expected to CONTAIN the digit values in the string.";
	public final static String STRACTDIGITPRESENCETRUE = "Actual result CONTAINS the digit in the string.";
	public final static String STRACTDIGITPRESENCEFALSE = "Actual result DOES NOT contains the digit in the string.";
	public final static String STRSHDELEFOUND = "Element SHOULD PRESENT on the Web page.";
	public final static String STRSHDELEFOUNDFAILURE = "Element DOES NOT PRESENT on the Web page.";
	public final static String STRSHDELEFOUNDSUCCS = "Element :'%s' PRESENT on the Web page as Expected.";
	public final static String STRSHDNOTELEFOUND = "Element %s SHOULD NOT PRESENT on the Web page.";
	public final static String STRSHDNOTELEFOUNDFAILURE = "Element :'%s' PRESENT on the Web page which is NOT Expected.";
	public final static String STRSHDNOTELEFOUNDSUCCS = "Element :'%s' DOES NOT PRESENT on the Web page as Expected.";
	public final static String STRPLACEHOLDERVAL ="Expected to get the place holder attribute value from the web element :'%s'.";
	public final static String STRUNRECBROWEXCEP = "Unreachable Browser Exception hence following steps and BPC are skipped.";
	public final static String STRWEBDRVEXCEPBPCSKIP = "WebDriver exception while checking for Mandatory object hence following steps and BPC are SKIPPED";
	public final static String STRMANELEBPCCHK ="Mandatory object :'%s' - SHOULD PRESENT on the webpage inorder to continue with this TEST CASE.";
	public final static String STRMANFUNCEXE ="Mandatory to perform this action to continue to current TEST CASE EXECUTION.";
	public final static String STRMANFUNCEXESUCCESS ="SUCCESS-Able to complete the Mandatory action to continue the current TEST CASE EXECUTION.";
	public final static String STRMANFUNCEXEFAILURE ="FAILED- Unable to complete the Mandatory action and Skipped the current TEST CASE EXECUTION.";
	public final static String STRMANELEBPCCHKSUCCS ="Mandatory object :'%s' - PRESENT on the webpage hence TEST CASE can continue with remaining BPC's.";
	public final static String STRMANELEBPCCHKERR ="Mandatory object :'%s' - NOT PRESENT on the webpage hence following steps and remaining BPC's are SKIPPED.";
	public final static String STRWEBDRVEXCEP = "WebDriver Exception. This may be due to element state availability(element not present). Please Verify it manually...";
	public final static String STRFUNNOTEXEC = "Function Not able to execute due to following error :\n";
	public final static String STREMPTYPARAMVAL = "Parameter value :'%s' for :'%s' in the input excel sheet cannot be EMPTY. Please verify it manually in the input data sheet...";
	public final static String STREXPENTRINPUTVAL ="EXPECTED to ENTER input data : '%s' into TEXT field: '%s'.";
	public final static String STREXPENTRINPUTVALSUCCS ="SUCCESS- Value '%s' has been entered into the TextField '%s'.";
	public final static String STREXPENTRINPUTVALFAILURE ="Value '%s' has NOT been entered into the TextField '%s'. Plese Verify it manually...";
	public final static String STREXPECTEDNUMRANGEINVALID ="Please Enter valid range. %s does not exist/not able to validate.";
	public final static String STREXPECTEDNUMRANGE ="EXPECTED the numeric value :%d to be :%s to :%d.%d";
	public final static String STREXPECTEDNUMRANGESUCCESS ="SUCCESS - EXPECTED the numberic value :%d is : %s with the actual numeric value :%d.";
	public final static String STREXPECTEDNUMRANGEFAILURE ="FAILURE - EXPECTED the numberic value :%d is not : %s with the actual numeric value :%d.";
	public final static String STRCHKLOG ="Please check the LOGFILE (And/OR) CONSOLE for more details related to the error message. ";
	public final static String STREXPECTEDNUMRANGEBTWNSUCCESS ="SUCCESS - EXPECTED the numberic value :%d is : %s with the actual numeric value :%d and %d";
	public final static String STREXPECTEDNUMRANGEBTWNFAILURE ="FAILURE - EXPECTED the numberic value :%d is not : %s with the actual numeric value :%d and %d";
	public final static String STRCOUNTNOTEQUAL = "Validation list counts are not equal. Please verify it with the 2 validation steps.";
	public final static String STRCOUNTEQUAL = "Validation list counts should be equal before we validate between 2 validation steps.";
	public final static String STRDDLOPTIONEXPECTEDMATCH ="Expected to match all the values in the dropdown .";
	public final static String STRDDLOPTIONEXPECTEDMATCHSUCCESS = "SUCCESS - Expected a dropdown list item :%s - AVAILABLE in the dropdown.";
	public final static String STRDDLOPTIONEXPECTEDMATCHFAILURE = "FAILURE - Expected a dropdown list item :%s - IS NOT AVAILABLE in the dropdown.Please verify it manually...";
	public final static String STRDDLNOTAVAILABLE = "The dropdown Element itself is NOT AVAILABLE.Please verify it manually...";
	public final static String STRDDLSHOULDNOTAVAILABLE = "Expected a dropdown list item value SHOULD NOT BE AVAILABLE in the list.";
	public final static String STRDDLSHOULDNOTAVAILABLESUCCS = "SUCCESS -Expected dropdown list item value :'%s' IS NOT AVAILABLE in the list.";
	public final static String STRDDLSHOULDNOTAVAILABLEFAILURE = "FAILURE - Expected a dropdown list item value: '%s' IS AVAILABLE in the list.Please verify it manually....";
	public final static String STRSHDSELECTLISTITM = "Expected to select the item : '%s' from the list: '%s'.";
	public final static String STRSHDSELECTLISTITMINDX = "Expected to select the item at index: '%s' from the list: '%s'.";
	public final static String STRSHDSELECTLISTITMSUCCS = "List item : '%s' has been SELECTED from the list :  '%s' - SUCCESSFULLY.";
	public final static String STRSHDSELECTLISTITMFAILURE = "List item : '%s' has NOT BEEN SELECTED from the list : '%s'.Please verify it manually.";
	public final static String STRROWDATAAVAILABLE = "Row data available at row #: '%d'";
	public final static String STRROWDATAAVAILABILITYREPORT  = "Row data NOT available,setting to default value: '%d' and trying to verify in next page if next page is present";
	public final static String STRRADARERROR = "Radar Creation error :";
	public final static String STRWEBDRIVERERROR = "Could not create Selenium Webdriver :";
	public final static String STRSTOPSERVERERR = "Could not stop Selenium Server :";
	public final static String STRAPPLNLAUNCHERR = "Application Launch error :";
	public final static String STRREADCONFIGERR = "Reading Config file error :";
	public final static String STRINITERR = "Initialization file error :";
	public final static String STRREPFUNCERR = "Reporting Functions error :";
	public final static String STRKEYLIBERR = "Keyword Library error :";
	public final static String STRCOMFILEERR = "CommonFile Script error :";
	public final static String STRDRIVERSCRIPTERR = "Driver script error :";
	public final static String STRNOSUCHELEEXCEP = "No Such Element Exception.";
	public final static String STRCLKELEEXCEP = "Exception occurred while clicking the element.";
	public final static String STRCLKELE = "EXPECTED to CLICK on the Web Element.'";
	public final static String STRCLKELEEXPECTED = "EXPECTED to CLICK on the Web Element :'%s'";
	public final static String STRCLKELESUCCS = "CLICKED Successfully on the Web Element :'%s' as EXPECTED.";
	public final static String STRCLKELEFAILURE = "Not able to CLICK on the Web element :'%s' as EXPECTED. Please verify it.....";
	public final static String STRTEXTINPEXCEP = "Exception occurred while text input.";
	public final static String STRGENEXCEPCAUGHT ="Exception got while executing the function :'%s'.";
	public final static String STRFUNCNOTSEEOBJ = "\tFunction not able to see the object:.";
	public final static String STRSCHKSTATEXCEP = "Exception occurred while checking for status.";
	public final static String STRMOUSEEVENTEXCEP = "Exception occurred while mouse over event.";
	public final static String STRENTERKEYEVENTEXCEP = "Exception occurred while performing enter Key Operation.";
	public final static String STRWAITTIMEEXCEP = "Exception occurred while waiting for specified time.";
	public final static String STRHANDLEALERTEXCEP = "Exception occurred while handling the alert message.";
	public final static String STRIMGERR = "Exception occurred while checking for Image icon.";
	public final static String STRSCREENAVAILERR = "Exception occurred while checking for Screen availability.";
	public final static String STRENTERCALDATAERR = "Exception occurred while entering Calendar Data Value.";
	public final static String STRGETVALERR = "Exception occurred while getting the value.";
	public final static String STRINDEXOUTOFBOUND = "IndexOutOfBoundsException Occured....";
	public final static String STRIOEXCEPTION = "IOException Occured....";
	public final static String STRFILENOTFOUND ="FileNotFoundException Occured....";
	public final static String STRNULLPTREXCEP = "NullPointerException Occured....";
	public final static String STRSTOREVALERR = "Exception occurred while storing the value.";
	public final static String STRSELITEMEXCEP = "Exception occurred while selecting the item.";
	public final static String STRVERIFYCOLEXCEP = " Exception occurred while verifying columns.";
	public final static String STRSORTEXCEP = "Exception occurred while sorting the values.";
	public final static String STREXPECTEDSORT ="Expected the Sort to be successful";
	public final static String STREXPECTEDSORTFAILURE ="FAILED-the sorting was Unsuccessfull..Current Value:'%s' and Next Value:'%s'.Please verify it..";
	public final static String STREXPECTEDSORTSUCCESS ="SUCCESS-the sorting was Successfull.";
	public final static String STRSORTElEMENTNOTFOUND ="Sort was not successful, since table/column to be sorted was not found";
	public final static String STRPAGINATIONERR = "Exception Occurred while verifying the pagingation.";
	public final static String STRVERIFYPAGEEXCEP = " Exception occurred while verifying page..";
	public final static String STRVERIFYPGCOUNTER = "Exception occurred while verifying page count.";
	public final static String STRVERIFYCHILDCOUNT = "Exception occurred while verifying CHILD count.";
	public final static String STREDITERR = "Exception occurred while checking whether the field is editable.";
	public final static String STRMANDCHKPASS = "Field is empty. Hence mandatory field error is thrown.";
	public final static String STRMANDCHKFAIL = "Field is empty. But mandatory field error is not thrown.";
	public final static String STRMANDCHKEXCEP = "Exception occurred while checking for mandatory fields.";
	public final static String STREXPNETPRCVAL ="Expected Net Price value is :'%s' and the actual Net Price value is : '%s'.";
	public final static String STRVERIFYBPCTOCONTINUE ="Verifying whether the current BPC has to continue the remaining cases or not.";
	public final static String STRVERIFYBPCTOCONTINUESUCCS ="The check for remaining BPC's cases to continue is SUCCESS and hence the remaining CURRENT BPC CASES will gets executed based on bpc's settings";
	public final static String STRVERIFYBPCTOCONTINUEFAIL ="The check for remaining BPC's cases to continue is FAILED (immediate previous step may FAILED)and hence the remaining cases CURRENT BPC CASES WILL NOT execute.";
	public final static String STRCOUNTSHDMATCH ="Input xpath parameter count SHOULD match with the Logical name count.";
	public final static String STRCOUNTSHDMATCHFAILURE ="Input xpath parameter count:'%d'  does not match with the logical Name Counts : '%d'.";
	public final static String STREXPTOGETFOCUSFORENTERKEY ="Expected to press the Enter key for the object that is in focus.";
	public final static String STREXPTOGETFOCUSFORENTERKEYSUCCS ="SUCCESS - Pressed on Enter Key for the object that is in focus.";
	public final static String STREXPTOGETFOCUSFORENTERKEYFAILURE ="FAILURE - Unable to press the Enter Key for the object that is in focus";
	public final static String STRTESTDATADBPARAMVALUE ="Please verify whether you have provided the value(NON-EMPTY) in the test data sheet for the key value : '%s'";
	public final static String STREXPPAGELOADING = "Looking for page loading window to appear.......";
	public final static String STREXPPAGELOADINGSUCCS = "Page/Table Loading Window Appeared and completed loading as expected......";
	public final static String STREXPPAGELOADINGFAILURE = "Warning: The application response is too slow (waiting for more than '%d' Seconds)which may affect the Automation script,exiting from current test case. \n please verify it manually...";
	
	public final static String STREXPECTEDELEMENTSTATE = "Expected web element's enable state: '%s'";
	public final static String STREXPECTEDELEMENTSTATEFAILURE = "Expected web element's state:'%s' value does not matches with Actual web element state:'%s' value.";  
	public final static String STREXPECTEDELEMENTSTATESUCCESS = "Expected web element's state:'%s' value MATCHES with Actual web element state:'%s' value.";  
	
	public final static String STREXPECTEDELEMENTTEXTSTATUS = "Expected Web Element Text Status to be present is :%b";
	public final static String STREXPECTEDELEMENTTEXTSTATUSSUCCS = "SUCCESS - Expected Web Element Text Status to be present is :%b and Actual is :%b";
	public final static String STREXPECTEDELEMENTTEXTSTATUSFAILURE = "FAILED - Expected Web Element Text Status to be present is :%b and Actual is :%b. " + "And the String is:%s";

	public final static String STREXPECTEDCHECKBOXSTATE = "Expected to verify that CHECKBOX STATE is EQUAL to USER EXPECTED CHECKBOX STATE.";
	public final static String STREXPECTEDCHECKBOXSTATEFAILURE = "FAILURE - user expected checkbox state:'%s' DOES NOT MATCH with actual checkbox state:'%s'.";  
	public final static String STREXPECTEDCHECKBOXSTATESUCCESS = "SUCCESS - user expected checkbox state:'%s' MATCHES with actual checkbox state:'%s'.";  
	
	public final static String STREXPMATCHACTUALFAILUREINT = "Expected Value : '%s' : '%d' does not matches with the Actual Value : '%s' : '%d'.";
	public final static String STREXPMATCHACTUALSUCCSINT = "Expected Value : '%s' : '%d' matches with the Actual Value : '%s' : '%d'.";
	
	public final static String STREXPNXTPAGELOADFAILURE = "The user did not navigate to the other page successfully";
	public final static String STREXPNXTPAGELOADSUCCS = "The user navigates to the other page successfully";
	
	public final static String STREXPCOLUMNFAILURE = "%s column header is not present in the table";
	public final static String STREXPCOLUMNSUCCESS = "%s column header is present in the table";
	
	public final static String STREXPPERSONALIZECOLUMNSUCCESS = "Only personalized columns should be present in the table";
	public final static String STREXPPERSONALIZECOLUMNFAILURE = "%d extra column headers are present in the table";
	
	public final static String STRROWCOUNTEQUAL = "The number of rows in DB is EQUAL to the number of rows in the UI";
	public final static String STRROWCOUNTNOTEQUAL = "The number of rows in DB is NOT EQUAL to the number of rows in the UI";
	
	public final static String STREXPROWCOUNTEQUAL = " Expected the number of rows in DB and UI should be EQUAL";
	public final static String STREXPRETURNPARAMETERVALUE = " Expected to return VALUE for the provided KEY : '%s'";
	public final static String STREXPRETURNPARAMETERVALUESUCCESS = " SUCCESS-Found VALUE :'%s' for the KEY :'%s'";
	public final static String STREXPRETURNPARAMETERVALUEFAILURE = " FAILURE- NO MATCHING KEY :'%s' exist is the test data input sheet. Please Verify....";
	
	public final static String STRWARNINGALERTBOXSUCCESS="Warning alert box appears";
	public final static String STRWARNINGALERTBOXFAILURE="Warning alert does not appears";
	
	public final static String STREXEPECTEDPRICE="Expected price from the Part details SHOULD MATCH with actual value from the formula.";
	public final static String STREXEPECTEDPRICESUCCESS="SUCCESS - Expected price from the Part details:'%s' MATCHES with ACTUAL value:'%s' from the formula.";
	public final static String STREXEPECTEDPRICEFAILURE="FAILURE - Expected price from the Part details:'%s' DOES NOT MATCH with ACTUAL value:'%s' from the formula.";
	
	
	public final static String STRWORKLISTCOUNTEQUAL="The number of rows in settings is EQUAL to the number of rows in the UI";
	public final static String STRRWORKLISTCOUNTNOTEQUAL="The number of rows in settngs is NOT EQUAL to the number of rows in the UI";
	public final static String STREXEPECTEDDETAILS="Expected details : %s from table SHOULD MATCH with details in another screen";
	public final static String STREXEPECTEDDETAILSSUCCESS="Expected details : %s from table MATCHES with details : %s in another screen";
	public final static String STREXEPECTEDDETAILSFAILURE="Expected details : %s from table MATCHES with details : %s in another screen";
	
	//general declaration for the SCENARIO execution information
	public final static String STRCALLINGFUNCTION = "Calling function :%s to read scenario file : %s";
	public final static String STREXEGROUPSTARTS ="Starting the Execution of the Group :%s";
	public final static String STREXESCENARIOSTARTS ="Starting the Execution of the Scenario :%s";
	public final static String STREXESCENARIOSTOPS ="End to the Execution of the Scenario :%s";
	public final static String STRSCENARIONOTEXECUTE ="The scenario is not needed to be executed since the corresponding flag in batch file is set to 'NO'.";
	public final static String STRNOSHEETERROR = "Check The name of the sheet in the batch excel sheet";
	public final static String STRNOFILEERROR ="\t The File name entered doesnt exist, please check the path: %s";
	public final static String STRBATCHDETAILSERROR ="The Batch File should have the Group sheet, scenario name and the description of scenario";
	public final static String STRMANDATORYFIELDMISSINGERROR ="Some mandatory value is missing from the Excel sheet.";
	public final static String STRCHECKAPPCONFIGFILE ="Check the name of the Excel File in the Application Configuration properties file.";
	public final static String STRFUNCMSGCAUSE ="Function : {}\n. Message : {}\n. Cause :{}.";
	public final static String STRSTACKTRACE ="Corresponding StackTrace :";
	public final static String STRCHECKSCENARIO = "Checking for scenario # :'%s'";
	public final static String STRBPCNOTOEXECUTE ="\t The flag is set to 'NO' in the scenario excel for this bpc, hence this row doesnot need to be executed.";
	public final static String STRNOSCENARIOFILEEXIST ="Scenario file not found with the name :%s . Please verify in the test data sheets.";
	public final static String STRBPCSETYESINFO = "The flag for the BPC is set to yes and hence executing the bpc";
	public final static String STRSCENARIOFAILEDANDEXIT = "\t The Scenario Failed at the previous BPC and hence exiting the remaining BPC of the Scenario without executing the rest of the rows.";
	public final static String STRCHECKBPCNUM = "Checking for the bpc # :%d in the scenario sheet";
	public final static String STRSTARTTIMEFORMAT = "Start Time in the format yyyy-mm-dd-hhmmss :%s";
	public final static String STRBATCHSTARTED ="Started Executing the Batch Run....";
	public final static String STRCHECKBROWSERNAME ="Check the Browser name provided in the GSXApplicationConfiguration.properties file or from command level.";
	public final static String STRCOMPLETEDBATCHRUN ="Test case execution completed for the current batch";
	
	
	//for exceptions related to SQL Execution
	public final static String STRSQLEXCEPTION ="SQL exception while trying to close DB connection hence following steps and BPC are skipped";
	
	
	public final static String STRSIMPLEDIVIDER ="--------------------------------------------------------------------------------------------------";



}

