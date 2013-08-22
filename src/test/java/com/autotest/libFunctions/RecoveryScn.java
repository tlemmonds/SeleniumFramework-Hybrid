package com.autotest.libFunctions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*************************************************************************************************************
* ClassName		:	RecoveryScn
* 
* Description	:	This class sets the flag as pass or fail based on the execution result
* 
*************************************************************************************************************/
//@SuppressWarnings("restriction")
public class RecoveryScn {
           
            //This flag make sure that the current BPC can be skipped or not. This is important since in some of the scenarios 
            //we need to skip the execution of the remaining switch cases. for example. if the Apple care protection plan is 
            //Available for the region what we are selected, then we need to test the remaining scenarios else we need to skip that
            //particular BPC alone and then start executing the remaining BPC's.This way we can avoid the test scenarios to exit 
            //from the test cases. by default , this boolean tends to be true.
            public static boolean bIsContinueCurrentBPC = true;
			private static ScriptEngine scriptEngine = null;
			private static RecoveryScn objSingleTon;
			private static final Logger RecoveryScn_LOGS = LoggerFactory.getLogger(RecoveryScn.class);

			private RecoveryScn(){
				PropertyConfigurator.configure(Initialization.pConfigFile.getProperty("LOGPROPERTIES_FILEPATH"));
			}
			public static RecoveryScn getInstance(){
				if(objSingleTon == null)
					objSingleTon = new RecoveryScn();
				return objSingleTon;
			}
			
            /**************************************************************************************************************
            * Function Name		:	recTestScnExit
            * Description		:	This functions sets the iExitScn to true and based on that variable value CommonFile 
            * 					:	fnReadScenarioXlsCell would exist the scenario	and passes the data to fnExecuteScenario
            * 					:	in the class DriverScript
            * 
            * @return void
            * 
            *************************************************************************************************************/
            public static void recTestScnExitTrue ()
            {
            	KeywordLibrary.isTestScenarioContinue =false;  
            }

            public static void recUnReachableTestScnExitTrue ()
            {
            	KeywordLibrary.isTestScenarioContinue =false;  
            	recCloseBrowser();
            }

            /**************************************************************************************************************
             * Function Name		:	recTestScnExitFalse
             * 
             * Function Description :	This functions sets the iExitScn to false and based on that variable value CommonFile 
             * 						:	fnReadScenarioXlsCell would exist the scenario and fail the data to fnExecuteScenario 
             * 						:	in the class DriverScript
             * 
             * @return void
             * 
             *************************************************************************************************************/
             public static void recTestScnExitFalse ()
             {
            	 KeywordLibrary.isTestScenarioContinue =true;  
             }
           
            /**************************************************************************************************************
             * Function Name		:	recRetTstScnExitFlag
             * Function Description :	This functions returns the iExitScn based on that variable value CommonFile fnReadScenarioXlsCell
             * 
             * @return void
             * 
             *************************************************************************************************************/
             public static boolean recRetTstScnExitFlag ()
             {
            	 return KeywordLibrary.isTestScenarioContinue;  

             }
             
            /**************************************************************************************************************
             * Function Name		:	recRestartBroswer
             * Function Description :	This functions restarts the browser when it is in unreachable state
             * 
             * @return void
             * 
             *************************************************************************************************************/
            public static void recRestartBroswer ()
            {
            	try{
            	Initialization objInitialization = Initialization.getInstance();
    			objInitialization.fnCloseDriver();
    			objInitialization.fnStartDriver();
    			objInitialization.fnLaunchApp();
    			RecoveryScn_LOGS.info("Started the driver and relaunched the application");
    		} catch (Exception e) {
    			RecoveryScn_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
    			RecoveryScn_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
        				e.getMessage(),e.getCause());
    		}
            }
            
            

            {
            	String strBrowser = StringUtils.EMPTY;
            	try{
            		if(Initialization.strConfigBrowser.equals("SafariDriver"))
            			strBrowser = "Safari";
            		if(Initialization.strConfigBrowser.equals("FirefoxDriver"))
            			strBrowser = "FireFox";
            		if(Initialization.strConfigBrowser.equals("ChromeDriver"))
            			strBrowser = "Google Chrome";

            		RecoveryScn_LOGS.info("Browser to Activate : "+strBrowser);
            		scriptEngine = new ScriptEngineManager().getEngineByName("AppleScript");
            		//to activate the browser window and also to maximize the window using apple script
            		String vale = "tell application \"" + strBrowser + "\" \n activate \n try\n set bounds of every window to " +
            				"{0,0,"+Initialization.screenWidth+","+Initialization.screenHeight+"}\n end try \n end tell";
            		scriptEngine.eval(vale);
            		RecoveryScn_LOGS.info("Activated Browser :  "+strBrowser);
            	}
            	catch(Exception e)
            	{
            		RecoveryScn_LOGS.error("Exception in Activating the browser.Please verify Activate Script");
            		RecoveryScn_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
            				e.getMessage(),e.getCause());
            		RecoveryScn_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
            	}finally{
            		if(scriptEngine != null)
            		scriptEngine = null;
            	}
            }

            
            /**************************************************************************************************************
             * Function Name		:	recCloseBrowser
             * Function Description :	this will force-quite the browser using apple script
             * 
             * @return void
             * 
             *************************************************************************************************************/
			public static void recCloseBrowser ()
            {
            	try{
            		if(StringUtils.equals("SafariDriver",Initialization.strConfigBrowser))
            			Initialization.strBrowserName = "Safari";
            		if(StringUtils.equals("FirefoxDriver",Initialization.strConfigBrowser))
            			Initialization.strBrowserName = "FireFox";
            		if(StringUtils.equals("ChromeDriver",Initialization.strConfigBrowser))
            			Initialization.strBrowserName = "Google Chrome";
            		
            		RecoveryScn_LOGS.info("browser name is :"+Initialization.strBrowserName);
            		//This will quit the All the specific browser on which the user needs to run the scripts
            		scriptEngine = new ScriptEngineManager().getEngineByName("AppleScript");
            		String script = "tell application \"" + Initialization.strBrowserName + "\" \n quit window \n end tell";
            		scriptEngine.eval(script);
            		RecoveryScn_LOGS.info("Closed and Quited the browser :"+Initialization.strBrowserName);
            	}
            	catch(Exception e){
            		RecoveryScn_LOGS.debug(StringConstants.STRSTACKTRACE+ e);
            		RecoveryScn_LOGS.error(StringConstants.STRFUNCMSGCAUSE, Thread.currentThread().getStackTrace()[1].getMethodName(),
            				e.getMessage(),e.getCause());
            	}
            	finally{
            		if(scriptEngine != null)
            		scriptEngine = null;
            	}
            }    
}