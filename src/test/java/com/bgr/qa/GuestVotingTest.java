package com.bgr.qa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import java.time.Duration;

public class GuestVotingTest {

    WebDriver driver;

    // Test 1 : Smoke test to verify that the website loads successfully
    @Test
    public void verifyWebsiteLoads() {

        driver = new ChromeDriver();

        driver.get("https://buggy.justtestit.org/");

        Assert.assertFalse(
            driver.getTitle().isEmpty(),
            "Website title should not be empty"
        );
    }

    // Test 2 : Guest voting restrition
    @Test
    public void guestCannotSeeVoteButton() {

        driver = new ChromeDriver();

        driver.get("https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg");

        WebDriverWait wait = new WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        );

        // Wait until the guest voting section is ready
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath(
                    "//*[normalize-space()='You need to be logged in to vote.']"
                )
            )
        );

        // Find Vote button
        List<WebElement> voteButtons = driver.findElements(
             By.xpath("//button[normalize-space()='Vote!']")
        );

        // Guest should have no Vote button
        Assert.assertEquals(
            voteButtons.size(),
            0,
            "Vote button should be absent for guests"
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }


}