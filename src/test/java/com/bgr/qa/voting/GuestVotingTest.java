package com.bgr.qa.voting;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import java.util.List;
import java.time.Duration;

public class GuestVotingTest {

    private WebDriver driver;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";

    // Runs before each guest restriction test
    @BeforeMethod
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        // CI opts in; local runs remain headed unless explicitly requested.
        if (Boolean.parseBoolean(System.getenv("HEADLESS"))) {
            options.addArguments("--headless=new", "--window-size=1440,900");
        }
        driver = new ChromeDriver(options);

        driver.get(CAR_URL);

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
    }

    // VOTE-TC01: Guest voting restriction
    @Test(description = "VOTE-TC01: guest cannot vote")
    public void guestCannotSeeVoteButton() {

        List<WebElement> voteButtons = driver.findElements(
            By.xpath("//button[normalize-space()='Vote!']")
        );

        Assert.assertEquals(
            voteButtons.size(),
            0,
            "Vote button should be absent for guests"
        );
    }

    // VOTE-TC02: Guest comment restriction
    @Test(description = "VOTE-TC02: guest cannot comment")
    public void guestCannotSeeCommentSection() {

        List<WebElement> commentInputs = driver.findElements(
            By.id("comment")
        );

        Assert.assertEquals(
            commentInputs.size(),
            0,
            "Comment input should be absent for guests"
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}