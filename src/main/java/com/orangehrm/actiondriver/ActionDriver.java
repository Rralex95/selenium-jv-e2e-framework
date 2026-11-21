package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("WebDriver instances is created");
	}

	// Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("clicked an element:"+elementDescription);
			logger.info("Clicked an element-->" + elementDescription);
		} catch (Exception e) {
			System.out.println("Unable to click element:" + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element:", elementDescription+"_unable to click");
			logger.error("Unable to click element");
		}
	}

	// Method to enter text into an input field -- Avoid Code Duplication - fix
	// Multiple calling method
	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on " + getElementDescription(by) + "-->" + value);
		} catch (Exception e) {
			logger.error("Unable to enter the value: " + e.getMessage());
		}
	}

	// Method to get text from an input field
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			return driver.findElement(by).getText();
		} catch (Exception e) {
			logger.error("Unable to get the text:" + e.getMessage());
			return "";
		}
	}

	// Method to compare two text -- changed the return type
	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				logger.info("Texts are Matching:" + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare Text", "Text Verified Successfully!"+actualText+ " equals "+expectedText);
				return true;
			} else {
				logger.error("Texts are not Matching:" + actualText + " not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Compare Text", "Text Verified Successfully!"+actualText+ " equals "+expectedText);
				return false;
			}
		} catch (Exception e) {
			logger.error("Unable to compare Texts:" + e.getMessage());
		}
		return false;
	}

	// Simplified the method and remove redundant conditions
	public boolean isDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			logger.info("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: "+getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed: ", "Element is displayed: "+getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			logger.error("Element is not displayed: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(),"Element is not displayed: ","Element is not displayed: "+getElementDescription(by));
			return false;
		}
	}

	// Wait for the page to load
	public void waitForPageLoad(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page loaded successfully.");
		} catch (Exception e) {
			logger.error("Page did not load within " + timeOutInSec + " seconds. Exception: " + e.getMessage());
		}
	}

	// Scroll to an element -- FIXED JavaScript Syntax
	public void scrollToElement(By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			logger.error("Unable to locate element:" + e.getMessage());
		}
	}

	// Wait for element to be clickable
	private void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element is not clickable: " + e.getMessage());
		}
	}

	// Wait for element to be Visible -- FIXED Condition from Invisibility to
	// Visibility
	public void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element is not visible: " + e.getMessage());
		}
	}

	// Method to get the description of an element using By locator
	public String getElementDescription(By locator) {
		// Check for null driver or locator to avoid NULLPointer Exception
		if (driver == null)
			return "Driver is null";
		if (locator == null)
			return "Locator is null";

		try {
			// Find the element using the locator
			WebElement element = driver.findElement(locator);

			// Get Element Attributes
			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String text = element.getText();
			String className = element.getDomAttribute("class");
			String placeHolder = element.getDomAttribute("placeholder");

			// Return the description based on element attributes
			if (isNotEmpty(name)) {
				return "Element with name:" + name;
			} else if (isNotEmpty(id)) {
				return "Element with id:" + id;
			} else if (isNotEmpty(text)) {
				return "Element with text:" + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class:" + className;
			} else if (isNotEmpty(placeHolder)) {
				return "Element with placeholder:" + placeHolder;
			}
		} catch (Exception e) {
			logger.error("Unable to describe the element" + e.getMessage());
		}
		return "";
	}

	// Utility method to check a string is not Null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// Utility method to truncate long String
	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";
	}

}
