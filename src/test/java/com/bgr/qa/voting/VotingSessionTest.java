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

/** VOTE-TC08–TC09: guest voting restrictions after logout, before and after refresh. */
public class VotingSessionTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";
    private static final By VOTE_BUTTON = By.xpath("//button[normalize-space()='Vote!']");
    private static final By GUEST_RESTRICTION =
        By.xpath("//*[normalize-space()='You need to be logged in to vote.']");

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        UiAccountHelper.registerAndLogin(driver, wait);
    }

    @Test(description = "VOTE-TC08: logout restores guest voting restrictions without refresh")
    public void logoutRestrictsVotingWithoutRefresh() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement voteButton = wait.withMessage("Voting was not available before logout")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        Assert.assertTrue(voteButton.isDisplayed() && voteButton.isEnabled(),
            "Vote! must be visible and enabled before logout");

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
        // Observe the application's update only: no refresh, navigation or vote submission.
        wait.withMessage("The navigation login form did not return after logout")
            .until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("nav input[name='login']")));
        WebElement restriction = wait.withMessage("Guest voting restriction did not appear after logout")
            .until(ExpectedConditions.visibilityOfElementLocated(GUEST_RESTRICTION));
        Assert.assertEquals(restriction.getText().trim(), "You need to be logged in to vote.");
        wait.withMessage("Voting controls remained after logout")
            .until(ExpectedConditions.and(
                ExpectedConditions.numberOfElementsToBe(VOTE_BUTTON, 0),
                ExpectedConditions.numberOfElementsToBe(By.id("comment"), 0)));
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must be absent after logout without a refresh");
        Assert.assertTrue(driver.findElements(By.id("comment")).isEmpty(),
            "Comment input must be absent in the guest-restricted state");
    }

    /**
     * TC09 covers UI prevention after logout and refresh. An absent Vote! button
     * prevents the manual submission attempt; this does not prove server-side rejection.
     */
    @Test(description = "VOTE-TC09: unauthenticated voting is prevented after logout and refresh")
    public void logoutAndRefreshRestrictsVoting() {
        driver.get(CAR_URL);
        wait.withMessage("Car page did not restore the authenticated session")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        wait.withMessage("Authenticated car voting content did not load")
            .until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));

        // No authenticated vote: establish the logged-out precondition through the UI.
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
        wait.withMessage("Logout did not restore the navigation login form")
            .until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("nav input[name='login']")));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.linkText("Logout"), 0));
        Assert.assertEquals(driver.getCurrentUrl(), CAR_URL,
            "The target car page must be open before the refresh");

        driver.navigate().refresh();
        wait.withMessage("Refreshed page did not show the guest login form")
            .until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("nav input[name='login']")));
        // Wait for car-specific guest content so a loading page cannot pass by absence.
        WebElement restriction = wait.withMessage("Refreshed car page did not show guest restriction")
            .until(ExpectedConditions.visibilityOfElementLocated(GUEST_RESTRICTION));
        Assert.assertEquals(restriction.getText().trim(), "You need to be logged in to vote.");
        Assert.assertEquals(driver.getCurrentUrl(), CAR_URL,
            "Refresh must keep the browser on the target car page");
        Assert.assertTrue(driver.findElements(By.linkText("Logout")).isEmpty(),
            "The session must remain logged out after refresh");
        wait.withMessage("Vote! remained available to a guest after refresh")
            .until(ExpectedConditions.numberOfElementsToBe(VOTE_BUTTON, 0));
        Assert.assertTrue(driver.findElements(VOTE_BUTTON).isEmpty(),
            "Vote! must be absent: unauthenticated submission is unavailable through the UI");
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
