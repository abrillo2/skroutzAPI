package scrapData;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/*************************************************************************************************
 * 
 *                  Abstract Class To perform different form action:
 *                  1. Fill form
 *                  2. Save form data to database
 *                  3. Initialize SELENIUM 
 * 
 * 
 * 
 * 
 **************************************************************************************************/

public abstract class InitSelenium {

	
	//set up SELENIUM 
	public static WebDriver setUpSeleniumDriver(String chromeDriver) {
		
		//setting the driver executable
		System.setProperty("webdriver.chrome.driver", chromeDriver);
		
                ChromeOptions options = new ChromeOptions();
                
                options.addArguments("--lang=en-US");
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-blink-features=AutomationControlled");
                
               
                
                WebDriver driver=new ChromeDriver(options);
		//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		//open WEBDRIVE CHROME using string URL
		return driver;
	}
}
