
package com.autotest.libFunctions;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**************************************************************************************************************************
 * ClassName	:	Initialization
 * Generated    :	6/08/2012
 * Description  : 	This class holds the functions to Initialize the driver, get the values from the configuration.properties 
 * 				:	and launch the application, Close the driver
 * 
 *************************************************************************************************************************/
public class Initialization {

	public static int screenWidth = 0;
	public static int screenHeight = 0;
	public static int strWaitTime = 0;
	public static int strMidWait = 0;
	public static int strLongWait = 0;
	public static int strDefaultDriverWait = 0;

	//For updating repair in DB
	public static String jdbcDriver = StringUtils.EMPTY;
	public static String strDBUrl = StringUtils.EMPTY;
	public static String strDBUserName = StringUtils.EMPTY;
	//For Radar Integration
	public static String strRadarLoginPath = StringUtils.EMPTY;
	public static String strRadarInpNewProbTemplatePath = StringUtils.EMPTY;
	public static String strRadarInpNewProbPath = StringUtils.EMPTY;
	public static String strRadarOutputNewProbPath = StringUtils.EMPTY;
	public static String strCreateRadar=StringUtils.EMPTY;
	//Environment details declarations
	public static String strExecutionEnvironment = StringUtils.EMPTY;
	public static String strConfigBrowser = StringUtils.EMPTY;
	public static String strBrowserName = StringUtils.EMPTY;
	public static String strBrowserVersion = StringUtils.EMPTY;
	public static String strBaseURL  = StringUtils.EMPTY;
	public static String strNodeURL  = StringUtils.EMPTY;
	public static String strAppURL  = StringUtils.EMPTY;
	public static String strDBPassword  = StringUtils.EMPTY;
	public static String strBatchFilePath = StringUtils.EMPTY;
	public static String strScenarioFilePath = StringUtils.EMPTY;
	public static String strBatchSheetName = StringUtils.EMPTY;
	public static String strResultReportPath = StringUtils.EMPTY;
	public static String strBuildNumberURL = StringUtils.EMPTY;
	public static String strMaxIteration = StringUtils.EMPTY;
	public static String strBuildNumber = "Temp";

	public static Properties pConfigFile = new Properties();
	DesiredCapabilities browserCapabilities = null;
	public static WebDriver driver = null;
	public static Dimension screenSize = null;

	//for getting the keyvalue pair for the respective build number
	public static Map<String, String> kvBuildNameAndNumber = new HashMap<String,String>();
	
	//For logger declaration
	
	private static final Logger Initialization_LOGS = LoggerFactory.getLogger(Initialization.class);
	private static Initialization objSingleTon ;
	
	//private constructor
	private Initialization(){
		//Load the logger configuration file for Application
		fnLoadAppConfigPropertyValuesFrom("");
		//This will set the prefix for the application log file extension and also the DateTime format {dd/MMM/yyyy hh:mm:ss a}
		System.setProperty("logFileNameSuffix",(new SimpleDateFormat ("dd_MMM_yyyy_HH_mm_ss_a").format(new Date())).toString());
		PropertyConfigurator.configure(pConfigFile.getProperty("LOGPROPERTIES_FILEPATH"));
		Initialization_LOGS.info("Execution Environment :{}",strExecutionEnvironment);
	}

	//to get one instance at any time with singleton approach
	public static Initialization getInstance(){
		if(objSingleTon == null)
			objSingleTon = new Initialization();
		return objSingleTon;
	}
	/*************************************************************************************************************************
	 *  Function Name			:	fnstartDriver
	 * 	Function Description	:	This functions Starts the Respective Driver
	 * 
	 ***********************************************************************************************************************/
	public void fnStartDriver() throws Exception {
		if (driver == null)
			Initialization_LOGS.info("Starting the Driver");
		try {
			//For Safari
			if (StringUtils.equalsIgnoreCase("SafariDriver", strConfigBrowser)){
				// Set to true if we need to avoid installing the web driver
				System.setProperty("webdriver.safari.noinstall", "false");
				this.browserCapabilities= DesiredCapabilities.safari();
				driver = new SafariDriver(this.browserCapabilities);
			}
			//For Firefox
			else if (StringUtils.equalsIgnoreCase("FirefoxDriver", strConfigBrowser)){
				this.browserCapabilities= DesiredCapabilities.firefox();
				driver = new FirefoxDriver(this.browserCapabilities);
			}
			//For Google Chrome
			else if (StringUtils.equalsIgnoreCase("ChromeDriver", strConfigBrowser)){
				this.browserCapabilities= DesiredCapabilities.chrome();
				//This is required since we need to set the system property for Chrome:
				//check information on: https://code.google.com/p/chromedriver/wiki/GettingStarted
				//by considering where the Google Chrome browser has been installed on the system
				System.setProperty("webdriver.chrome.driver", "/Applications/Google Chrome.app/Contents/MacOS/chromedriver");
				driver = new ChromeDriver(this.browserCapabilities);
			}//For Internet Explorer
			else if (StringUtils.equalsIgnoreCase("InternetExplorerDriver", strConfigBrowser)){
				//InternetExploror browser is not yet tested in Mac OS Machine as the InternetExplorer Driver will work only on Windows!
				//Check the information on : https://code.google.com/p/selenium/wiki/InternetExplorerDriver
				//more info on DesiredCapabilities : http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/remote/DesiredCapabilities.html
				this.browserCapabilities= DesiredCapabilities.internetExplorer();
				this.browserCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				driver = new InternetExplorerDriver(this.browserCapabilities);
			}
			//to get the browser version for the executing web driver
			strBrowserVersion = ((RemoteWebDriver) driver).getCapabilities().getVersion().toString();
		} 
		catch (Exception e) {
			Initialization_LOGS.error(StringConstants.STRWEBDRIVERERROR);
			fnShowExceptionLogMessage(e);
		}
	}



	/*************************************************************************************************************************
	 *  Function Name			:	fncloseDriver
	 *  Function Description	:	This functions Closes the browser and quits the Driver
	 *  
	 ************************************************************************************************************************/
	public void fnCloseDriver() throws Exception {
		Initialization_LOGS.info("Closing the Driver");
		try {
			if (driver != null) {
				driver.close();
				driver.quit();
				driver = null;
			}//In some cases the driver == null, so we need to quit all windows anyway
			else RecoveryScn.recCloseBrowser();
		}catch (Exception e) {
			Initialization_LOGS.error(StringConstants.STRSTOPSERVERERR);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
			//This will call the apple script to close the browser to force quit since the browser is 
			//unreachable thru the selenium command and hence quieting will not work using selenese command
			//This will work on the platform where the script is getting supported
			RecoveryScn.recCloseBrowser();
		}
	}


	/*************************************************************************************************************************
	 *  Function Name			:	fnLaunchApp
	 *  Function Description	:	This functions launches the application based on the URL passed
	 *  
	 *************************************************************************************************************************/
	public void fnLaunchApp() throws Exception {
		try {
			driver.get(strAppURL);  
			driver.manage().timeouts().implicitlyWait(Initialization.strDefaultDriverWait, TimeUnit.SECONDS);
			//get the screen resolution, this will depends on the hardware
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenHeight = screenSize.height;
			screenWidth = screenSize.width;
			//This is to make sure there is some time gap before executing Java Script so that the web page load
			//event occurred and then the script executes , else "detected unloadPage event error message displays
			
		} catch(UnreachableBrowserException e){
			Initialization_LOGS.error(StringConstants.STRUNRECBROWEXCEP);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
			RecoveryScn.recCloseBrowser();
		}catch (WebDriverException e) {
			Initialization_LOGS.error(StringConstants.STRAPPLNLAUNCHERR);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
		}catch (Exception e) {
			Initialization_LOGS.error(StringConstants.STRCHKLOG);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
		}
	}


	/*************************************************************************************************************************
	 *  Function Name			:	fnGetAppConfigValues
	 *  Function Description	:	This function will get the Application configuration setup value from the parameter and then
	 *  						:	start the read operation and sets the corresponding values to the StringConstants 
	 *  
	 *  @return returns the valid browser name as per the user requirement when input value is proper else returns null
	 *  
	 *************************************************************************************************************************/
	public String fnGetAppConfigValues(String strLclBrowserName , String strLclBatchPath){
		try{
			//To read the configuration for application settings from property file
			Initialization_LOGS.info("Reading the configuration properties");
			//To set the application URL
			setApplicationURLConfigurationFor(strExecutionEnvironment);
			//To set the DB Configuration values
			setApplicationDBConfigurationFor(strExecutionEnvironment);
			//To set the Executing build URL link to get the build number for the batch run 
			
			//To update the BrowserName , Batch File path if passed from Command line args , otherwise keep the value passed from Properties file
			if (StringUtils.isNotBlank(strLclBrowserName))
			{
				strConfigBrowser = strLclBrowserName;
				strBatchFilePath = strLclBatchPath;	
				Initialization_LOGS.info("Browser Name :='"+strLclBrowserName+"' and Batch File Name: '"+ strLclBatchPath +"' is PASSED from Command Argument");
			}
			else
				Initialization_LOGS.info("Browser Name :='"+strConfigBrowser+"' and Batch File Name: '"+ strBatchFilePath +"' is passed NOT PASSED from Command Argument. Hence Consider Properties file value ");

			Initialization_LOGS.info("Batch File Path :="+ strBatchFilePath );

			if (StringUtils.equalsIgnoreCase("IE",strConfigBrowser)){
				strConfigBrowser = "InternetExplorerDriver";
			}
			else if (StringUtils.equalsIgnoreCase("FIREFOX",strConfigBrowser)){
				strConfigBrowser = "FirefoxDriver";
			}
			else if (StringUtils.equalsIgnoreCase("GOOGLECHROME",strConfigBrowser)){
				strConfigBrowser = "ChromeDriver";
			}
			else if (StringUtils.equalsIgnoreCase("SAFARI",strConfigBrowser)){
				strConfigBrowser = "SafariDriver";
			}
			//Since no driver is matching with the expected with the available drivers.Please check the respective properties file at the location comments for available 
			//browsers. Check with the key name "BROWSERDRIVER", provide accepted values provided in the comments in properties file
			else return null;
		}
		catch(Exception e){
			Initialization_LOGS.error(StringConstants.STRREADCONFIGERR);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
		}
		return strConfigBrowser; 
	}


	/************************************************************************************************************************
	 *  Function Name			:	fnLoadAppConfigPropertyValuesFrom
	 *  Function Description	:	This functions reads config values from Configuration.properties file
	 *  
	 ************************************************************************************************************************/
	public static void fnLoadAppConfigPropertyValuesFrom(final String strConfigFilePath) {
		try {
			//Variables to hold the property values
			//Load the property file from the path entered by the user
			pConfigFile.load(new FileInputStream(strConfigFilePath));
			strExecutionEnvironment = pConfigFile.getProperty("EXECUTION_ENVIRONMENT");
			strConfigBrowser = pConfigFile.getProperty("BROWSERDRIVER");
			strResultReportPath= pConfigFile.getProperty("RESULT_REPORT_PATH");
			strBatchFilePath = pConfigFile.getProperty("BATCH_FILE_PATH");
			strBatchSheetName=pConfigFile.getProperty("BATCH_SHEET_NAME");

			strWaitTime = Integer.parseInt(pConfigFile.getProperty("strWaitTime"));
			strMidWait = Integer.parseInt(pConfigFile.getProperty("strMidWait"));
			strLongWait = Integer.parseInt(pConfigFile.getProperty("strLongWait"));
			strDefaultDriverWait=Integer.parseInt(pConfigFile.getProperty("strDefaultDriverWait"));
			strScenarioFilePath= pConfigFile.getProperty("SCENARIO_FILE_PATH");
			strMaxIteration=pConfigFile.getProperty("MAX_ITER");
			strCreateRadar=pConfigFile.getProperty("LOG_RADAR");

			jdbcDriver=pConfigFile.getProperty("JDBC_DRIVER");

			//For Radar Integration
			strRadarInpNewProbTemplatePath = pConfigFile.getProperty("RADAR_INPUT_NEWPROBLEM_TEMPLATE");
			strRadarInpNewProbPath = pConfigFile.getProperty("RADAR_INPUT_NEWPROBLEM_INPUT");
			strRadarOutputNewProbPath = pConfigFile.getProperty("RADAR_OUTPUT_NEWPROBLEM_OUTPUT");
			strRadarLoginPath = pConfigFile.getProperty("RADAR_LOGIN_CLI_LOGIN");

		} 
		catch (IOException e) {
			Initialization_LOGS.error(StringConstants.STRINITERR);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
		}
		catch(Exception e){
			Initialization_LOGS.error(StringConstants.STRCHKLOG);
			Initialization_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
			fnShowExceptionLogMessage(e);
		}
	}


	/************************************************************************************************************************
	 *  Function Name			:	setApplicationDBonfigurationFor()
	 *  Function Description	:	This function will set the Application DB Configuration based on the Execution 
	 *  						:	Environmental Type Passed
	 *  
	 * 	@param  strAppDBConfig = Environmental Variable to be set for DB Configuration
	 *	@return void
	 ************************************************************************************************************************/

	private static void setApplicationDBConfigurationFor(String strAppDBConfig) {
		if(StringUtils.equalsIgnoreCase("DEV",strAppDBConfig)){
			strDBUrl = pConfigFile.getProperty("DB_URL_DEV");
			strDBUserName = pConfigFile.getProperty("DB_USERNAME_DEV");
			strDBPassword = pConfigFile.getProperty("DB_PASSWORD_DEV");
		}else if(StringUtils.equalsIgnoreCase("UT",strAppDBConfig)){
			strDBUrl = pConfigFile.getProperty("DB_URL_UT");
			strDBUserName = pConfigFile.getProperty("DB_USERNAME_UT");
			strDBPassword = pConfigFile.getProperty("DB_PASSWORD_UT");
		}else if(StringUtils.equalsIgnoreCase("IT",strAppDBConfig)){
			strDBUrl = pConfigFile.getProperty("DB_URL_IT");
			strDBUserName = pConfigFile.getProperty("DB_USERNAME_IT");
			strDBPassword = pConfigFile.getProperty("DB_PASSWORD_IT");
		}
	}


	/************************************************************************************************************************
	 *  Function Name			:	setApplicationURLConfigurationFor()
	 *  Function Description	:	This function will set the Application URL Configuration based on the Execution Type Passed
	 *
	 *  @return void
	 *  @param  strAppURLConfig = Environmental Variable to be set for Application URL Configuration
	 ************************************************************************************************************************/

	private static void setApplicationURLConfigurationFor(String strAppURLConfig) {
		if(StringUtils.equalsIgnoreCase("DEV",strAppURLConfig)){
			strBaseURL = pConfigFile.getProperty("BASE_URL_DEV");
			strNodeURL = pConfigFile.getProperty("NODE_URL_DEV");
		}else if(StringUtils.equalsIgnoreCase("UT",strAppURLConfig)){
			strBaseURL = pConfigFile.getProperty("BASE_URL_UT");
			strNodeURL = pConfigFile.getProperty("NODE_URL_UT");
		}else if(StringUtils.equalsIgnoreCase("IT",strAppURLConfig)){
			strBaseURL = pConfigFile.getProperty("BASE_URL_IT");
			strNodeURL = pConfigFile.getProperty("NODE_URL_IT");
		}
		strAppURL = strBaseURL.concat(strNodeURL);
	}

	

	
	
	
	/************************************************************************************
	 * Function Name			:	setApplicationURLConfigurationFor()
	 * Function Description		:	This function will get the exception which is thrown from the calling function.
	 * 							:	This will log the details of the exceptions which is useful for the debugging
	 * 							:	purpose to fix issues
	 * 			**** Do not use this method as public access modifier ****
	 * @param e
	 *************************************************************************************/
	private static void fnShowExceptionLogMessage(Exception e) {
		//this [2] will get the exact method name
		Initialization_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[2].getMethodName(),
				e.getMessage(),e.getCause());
	}

}