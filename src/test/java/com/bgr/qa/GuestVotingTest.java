package com.bgr.qa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;

public class GuestVotingTest {

    WebDriver driver;

    @Test
    public void verifyWebsiteLoads() {

        driver = new ChromeDriver();

        driver.get("https://buggy.justtestit.org/");

        Assert.assertFalse(
            driver.getTitle().isEmpty(),
            "Website title should not be empty"
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}