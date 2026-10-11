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
 * VOTE-TC03 and VOTE-TC04: first-vote eligibility and login without refresh.
 * Each method prepares its own account in a fresh browser; no execution order is required.
 * Verifies public UI evidence only.
 */
public class VotingEligibilityTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private UiAccountHelper.Credentials credentials;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";
    private static final By VOTE_BUTTON = By.xpath("//button[normalize-space()='Vote!']");
    private static final By SUCCESS =
        By.xpath("//p[normalize-space()='Thank you for your vote!']");

    private static final By GUEST_RESTRICTION =
        By.xpath("//*[normalize-space()='You need to be logged in to vote.']");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(description = "VOTE-TC03: one eligible vote is accepted with visible success feedback")
    public void firstEligibleVoteIsAccepted() {
        UiAccountHelper.registerAndLogin(driver, wait);
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

    @Test(description = "VOTE-TC04: login enables voting on the same car page without refresh")
    public void loginEnablesVotingWithoutRefresh() {
        // Registration leaves this fresh browser logged out; do not call registerAndLogin.
        credentials = UiAccountHelper.register(driver, wait);
        wait.withMessage("Account preparation did not leave a logged-out session")
            .until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("nav input[name='login']")));
        Assert.assertTrue(driver.findElements(By.linkText("Logout")).isEmpty(),
            "Account preparation must leave the browser logged out");

        driver.get(CAR_URL);
        WebElement restriction = wait.withMessage("Guest voting restriction was not shown")
            .until(ExpectedConditions.visibilityOfElementLocated(GUEST_RESTRICTION));
        Assert.assertEquals(restriction.getText().trim(), "You need to be logged in to vote.");
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must be absent before login");
        String guestCarUrl = driver.getCurrentUrl();
        Assert.assertEquals(guestCarUrl, CAR_URL, "The guest must be on the selected car page");

        // From here onward, do not navigate or refresh: observe the application's update.
        UiAccountHelper.login(driver, wait, credentials);
        Assert.assertEquals(driver.getCurrentUrl(), guestCarUrl,
            "Login must keep the browser on the same car URL");
        WebElement voteButton = wait.withMessage("Vote! did not become available after login")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must become visible and enabled without a manual refresh");
        Assert.assertEquals(driver.getCurrentUrl(), guestCarUrl,
            "The browser must remain on the same car URL when voting becomes available");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        credentials = null;
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
