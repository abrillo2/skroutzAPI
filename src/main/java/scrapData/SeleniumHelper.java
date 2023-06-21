/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scrapData;

import org.openqa.selenium.WebDriver;

/**
 *
 * @author owner
 */

/*************************************************************************************************
 * 
 *                  Child Class of InitSelenium
              Used for filling form fields in a given page
 **************************************************************************************************/


public class SeleniumHelper{
	
	
	//local fields 
	public String formUrl;
	public WebDriver driver;
	
	//constructor
	public SeleniumHelper(String chromeDriver) {
		this.driver = InitSelenium.setUpSeleniumDriver(chromeDriver);
	}
	
	//load page
	public void LoadPage(String url) {
		
		try {
		
			this.driver.get(url);
		}catch(Exception  e) {
			this.driver = null;
			System.out.println(e.getMessage());
			return;
		}
	}
	

	/*****************************************
	 *         Setters and getters
	 *****************************************/
	//form URL
	protected String getFormUrl() {
		return formUrl;
	}

	protected void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}
	
	//form drive
	protected WebDriver getDriver() {
		return driver;
	}

	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}

}

