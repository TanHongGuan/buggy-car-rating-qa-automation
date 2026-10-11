package com.bgr.qa.voting;

import com.bgr.qa.helpers.UiAccountHelper;
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
 * VOTE-TC05–TC07: prevent repeat voting without refresh, after refresh and after revisit.
 * Each method prepares a fresh eligible account and submits only one vote.
 * Precondition: a fresh eligible account. Expected: success feedback and no Vote! button.
 * Checks the observed UI restriction, not server-side duplicate-vote enforcement.
 */
public class RepeatVotingTest {
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

    @Test(description = "VOTE-TC05: a second vote is unavailable without refreshing")
    public void secondVoteIsPreventedWithoutRefresh() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("First Vote! control was not available")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be visible and enabled before the first vote");
        Assert.assertTrue(driver.findElements(SUCCESS).isEmpty(),
            "A fresh account must not already show an accepted vote");

        // Submit once only. No retries, refresh, navigation or logout after this point.
        voteButton.click();
        WebElement feedback = wait.withMessage("The first vote did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        Assert.assertEquals(feedback.getText().trim(), "Thank you for your vote!");

        // The observed UI removes the button; do not try to bypass it with a second click.
        wait.withMessage("Vote! remained available after the accepted vote")
            .until(ExpectedConditions.numberOfElementsToBe(VOTE_BUTTON, 0));
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "The UI must prevent a second vote by removing Vote! without a refresh");
    }

    @Test(description = "VOTE-TC06: repeat voting remains restricted after one page refresh")
    public void repeatVoteIsPreventedAfterRefresh() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("First Vote! control was not available")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be available before the first vote");
        Assert.assertTrue(driver.findElements(SUCCESS).isEmpty(),
            "A fresh account must not already show an accepted vote");

        // Exactly one submission; never retry an uncertain vote.
        voteButton.click();
        WebElement feedback = wait.withMessage("The first vote did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        Assert.assertEquals(feedback.getText().trim(), "Thank you for your vote!");

        driver.navigate().refresh();
        wait.withMessage("Authentication was not restored after refresh")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        // Wait for voting content, so an empty loading page cannot satisfy absence.
        // Either known state resolves the wait; the assertion rejects a returned button.
        wait.withMessage("Voting state did not load after refresh")
            .until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(SUCCESS),
                ExpectedConditions.visibilityOfElementLocated(VOTE_BUTTON)));
        Assert.assertEquals(driver.getCurrentUrl(), CAR_URL,
            "Refresh must keep the browser on the selected car page");
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must remain absent after refresh to prevent another vote");
    }

    @Test(description = "VOTE-TC07: repeat voting remains restricted after revisiting the car page")
    public void repeatVoteIsPreventedAfterRevisit() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("First Vote! control was not available")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be available before the first vote");
        Assert.assertTrue(driver.findElements(SUCCESS).isEmpty(),
            "A fresh account must not already show an accepted vote");

        // One submission only; never retry or attempt a second vote.
        voteButton.click();
        WebElement feedback = wait.withMessage("The first vote did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        Assert.assertEquals(feedback.getText().trim(), "Thank you for your vote!");

        // Leave through the application's home link, verified against the public UI.
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Buggy Rating"))).click();
        wait.withMessage("The home link did not navigate away from the car page")
            .until(ExpectedConditions.urlToBe("https://buggy.justtestit.org/"));
        wait.until(ExpectedConditions.stalenessOf(feedback));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));

        // Revisit the same model without logging out; this is not a page refresh.
        driver.get(CAR_URL);
        wait.withMessage("Authentication was not restored after revisiting")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        // Do not let an empty loading page satisfy the absence assertion.
        wait.withMessage("Voting state did not load after revisiting")
            .until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(SUCCESS),
                ExpectedConditions.visibilityOfElementLocated(VOTE_BUTTON)));
        Assert.assertEquals(driver.getCurrentUrl(), CAR_URL,
            "The browser must return to the same car model URL");
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must remain absent after revisiting to prevent another vote");
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
