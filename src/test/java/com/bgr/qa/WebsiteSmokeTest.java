package com.bgr.qa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;

public class WebsiteSmokeTest {

    private WebDriver driver;

    @Test
    public void verifyWebsiteLoads() {

        ChromeOptions options = new ChromeOptions();
        // CI opts in; local runs remain headed unless explicitly requested.
        if (Boolean.parseBoolean(System.getenv("HEADLESS"))) {
            options.addArguments("--headless=new", "--window-size=1440,900");
        }
        driver = new ChromeDriver(options);

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