package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());

	}

	@Test
	public void verifyValidLoginTest() {
		
		ExtentManager.starTest("Valid Login Test");
		System.out.println("Running testMethod1 on thread: " + Thread.currentThread().getId());
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login("admin", "admin123");
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible after successfull login");
		ExtentManager.logStep("Validation Succesful");
		homePage.logout();
		ExtentManager.logStep("Logged out Succesfully!");
		staticWait(2);
	}

	@Test
	public void inValidLoginTest() {
		
		ExtentManager.starTest("In-valid Login Test");
		System.out.println("Running testMethod2 on thread: " + Thread.currentThread().getId());
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login("admin", "passerroneo");
		String expectedErrorMessage = "Invalid credentials1";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage), "Test Failed: Invalid error message");
		ExtentManager.logStep("Validation Succesful");
		ExtentManager.logStep("Logged out Succesfully!");

	}

}
