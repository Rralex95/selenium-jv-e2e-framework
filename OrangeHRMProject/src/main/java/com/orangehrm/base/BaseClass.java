package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

	public static Logger logger = LoggerManager.getLogger(BaseClass.class);

	@BeforeSuite
	public void loadConfig() throws IOException {
		// Load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");

		// Start the Extent Report
		// ExtentManager.getReporter(); -- This has been implemented in TestListener
	}

	@BeforeMethod
	public synchronized void setup() throws IOException {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
		logger.info("WebDriver initialized and browser maximized");
		/*
		 * logger.trace("This is a trace message");
		 * logger.error("This is a error message");
		 * logger.debug("This is a debug message");
		 * logger.fatal("This is a fatal message");
		 * logger.warn("This is a warn message");
		 */

		/*
		 * // Initialize the actionDriver only once if (actionDriver == null) {
		 * actionDriver = new ActionDriver(driver);
		 * logger.info("ActionDriver instance is created " +
		 * Thread.currentThread().getId()); }
		 */

		// Initialize Action Driver for the current Thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());

	}

	/*
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private synchronized void launchBrowser() {

		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			// driver = new ChromeDriver();
			driver.set(new ChromeDriver()); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver instance is created");
		} else if (browser.equalsIgnoreCase("firefox")) {
			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver()); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("FirefoxDriver instance is created");
		} else if (browser.equalsIgnoreCase("safari")) {
			// driver = new SafariDriver();
			driver.set(new SafariDriver()); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("SafariDriver instance is created");
		} else if (browser.equalsIgnoreCase("edge")) {
			// driver = new EdgeDriver();
			driver.set(new EdgeDriver()); // New Changes as per Thread
			logger.info("EdgeDriver instance is created");
		} else {
			throw new IllegalArgumentException("Browser Not Supported: " + browser);
		}
	}

	/*
	 * Configure browser settings such as implicit wait, maximize the browser and
	 * navigate to the URL
	 */
	private void configureBrowser() {
		// Implicit Wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

		// Maximize the browser
		getDriver().manage().window().maximize();

		// Navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to Navigate to the URL: " + e.getMessage());
		}
	}

	@AfterMethod
	public void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				System.out.println("Unable to quit the driver: " + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		// driver = null;
		// actionDriver = null;
		//ExtentManager.endTest(); -- This has been implemented in TestListener
	}

	// Getter method for WebDriver
	public static WebDriver getDriver() {

		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// Getter method for ActionDriver
	public static ActionDriver getActionDriver() {

		if (actionDriver.get() == null) {
			System.out.println("ActionDriver is not initialized");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return actionDriver.get();
	}

	// Getter method for prop
	public static Properties getProp() {
		return prop;
	}

	// Driver setter method
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}

	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
