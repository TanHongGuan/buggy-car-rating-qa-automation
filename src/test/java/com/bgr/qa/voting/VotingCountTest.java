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

/** VOTE-TC12: one eligible vote increases the displayed count by exactly one. */
public class VotingCountTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";
    private static final By VOTE_BUTTON = By.xpath("//button[normalize-space()='Vote!']");
    private static final By SUCCESS =
        By.xpath("//p[normalize-space()='Thank you for your vote!']");
    // Live DOM inspected: <h4>Votes: <strong>2150</strong></h4>; no fixed count in locator.
    private static final By VOTE_COUNT =
        By.xpath("//h4[starts-with(normalize-space(.), 'Votes:')]/strong");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        UiAccountHelper.registerAndLogin(driver, wait);
    }

    @Test(description = "VOTE-TC12: one eligible vote increases the displayed count by exactly one")
    public void oneVoteIncreasesDisplayedCountByOne() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("Eligible Vote! control was not available")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be available for the fresh account");
        Assert.assertTrue(driver.findElements(SUCCESS).isEmpty(),
            "The fresh account must not already show an accepted vote");
        wait.until(ExpectedConditions.visibilityOfElementLocated(VOTE_COUNT));
        Assert.assertEquals(driver.findElements(VOTE_COUNT).size(), 1,
            "The displayed vote count must have a unique locator");
        long before = readDisplayedCount();
        System.out.println("VOTE-TC12 displayed count before=" + before);

        // Exactly one submission. No retry or refresh, even if the count is unexpected.
        voteButton.click();
        WebElement feedback = wait.withMessage("Vote submission did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        Assert.assertEquals(feedback.getText().trim(), "Thank you for your vote!");
        wait.until(ExpectedConditions.numberOfElementsToBe(VOTE_BUTTON, 0));
        // Wait for any change, not specifically +1: an unexpected change must fail below.
        long after = wait.withMessage("Displayed count did not change after the accepted vote; before=" + before)
            .until(d -> {
                long current = readDisplayedCount();
                return current != before ? current : null;
            });
        System.out.println("VOTE-TC12 displayed count after=" + after + ", delta=" + (after - before));
        Assert.assertEquals(after, before + 1,
            "Displayed count must increase by exactly one; investigate concurrent votes if it differs");
    }

    private long readDisplayedCount() {
        String text = driver.findElement(VOTE_COUNT).getText().trim();
        Assert.assertTrue(text.matches("[0-9]+"), "Unexpected displayed count format: " + text);
        return Long.parseLong(text);
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
