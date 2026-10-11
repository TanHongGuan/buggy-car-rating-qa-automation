package com.bgr.qa;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * VOTE-TC03: first eligible vote.
 * Precondition: a newly registered account that has never voted for this car.
 * Objective: submit one vote; expect success feedback and removal of voting controls.
 * Verifies public UI evidence only, not database records or server-side uniqueness.
 */
public class EligibleVotingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";
    private static final By VOTE_BUTTON = By.xpath("//button[normalize-space()='Vote!']");
    private static final By SUCCESS =
        By.xpath("//p[normalize-space()='Thank you for your vote!']");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        UiAccountHelper.registerAndLogin(driver, wait);
    }

    @Test(description = "VOTE-TC03: one eligible vote is accepted with visible success feedback")
    public void firstEligibleVoteIsAccepted() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("Eligible Vote! control was not available")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be visible and enabled before submission");
        Assert.assertTrue(driver.findElements(SUCCESS).isEmpty(),
            "A fresh account must not already show the voted state");

        // Do not retry this click: voting changes persistent public-demo data.
        voteButton.click();

        WebElement feedback = wait.withMessage("Vote submission did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        Assert.assertEquals(feedback.getText().trim(), "Thank you for your vote!");
        wait.withMessage("Voting controls remained after success")
            .until(ExpectedConditions.numberOfElementsToBe(VOTE_BUTTON, 0));
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must be removed after the vote is accepted");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
