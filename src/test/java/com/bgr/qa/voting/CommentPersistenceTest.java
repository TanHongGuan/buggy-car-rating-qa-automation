package com.bgr.qa.voting;

import com.bgr.qa.helpers.UiAccountHelper;
import java.time.Duration;
import java.util.UUID;
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

/** VOTE-TC13: a submitted comment persists with its author after one refresh. */
public class CommentPersistenceTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String CAR_URL =
        "https://buggy.justtestit.org/model/ckl2phsabijs71623vk0%7Cckl2phsabijs71623vpg";
    private static final By COMMENT = By.id("comment");
    private static final By VOTE_BUTTON = By.xpath("//button[normalize-space()='Vote!']");
    private static final By SUCCESS =
        By.xpath("//p[normalize-space()='Thank you for your vote!']");
    // Live DOM: table headers Date, Author, Comment; each entry has three td cells.
    private static final String COMMENT_TABLE =
        "//table[thead/th[2][normalize-space()='Author'] and thead/th[3][normalize-space()='Comment']]";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        UiAccountHelper.registerAndLogin(driver, wait);
    }

    @Test(groups = {"known-failure"}, description = "VOTE-TC13: comment persists with correct author after refresh")
    public void commentPersistsWithCorrectAuthorAfterRefresh() {
        driver.get(CAR_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        String marker = "QA-TC13-" + UUID.randomUUID();
        By commentCell = By.xpath(COMMENT_TABLE + "/tbody/tr/td[3][text()='" + marker + "']");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(COMMENT));
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON));
        // Inspect the existing controls before entering data; never output account credentials.
        System.out.println("VOTE-TC13 comment control: " + input.getAttribute("outerHTML"));
        System.out.println("VOTE-TC13 submission control: " + submit.getAttribute("outerHTML"));
        Assert.assertEquals(input.getTagName(), "textarea", "Expected the public comment input");
        Assert.assertTrue(driver.findElements(commentCell).isEmpty(), "Marker must be new");
        input.sendKeys(marker);
        // The UI submits the comment with Vote!: exactly one click, never retried.
        submit.click();
        wait.withMessage("Comment submission did not show success feedback")
            .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS));
        wait.withMessage("Submitted comment was not displayed before refresh")
            .until(ExpectedConditions.visibilityOfElementLocated(commentCell));
        System.out.println("VOTE-TC13 submission accepted; marker=" + marker);

        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        WebElement persisted = wait.withMessage("Submitted comment did not persist after refresh")
            .until(ExpectedConditions.visibilityOfElementLocated(commentCell));
        Assert.assertEquals(persisted.getText(), marker, "Exact submitted comment must persist");
        System.out.println("VOTE-TC13 persistence=PASS after one refresh");

        String author = persisted.findElement(By.xpath("../td[2]")).getText().trim();
        System.out.println("VOTE-TC13 author present=" + !author.isEmpty());
        Assert.assertFalse(author.isEmpty(),
            "Comment persisted but Author cell is empty; possible relation to VOTE-DF02 / KAN-2");
        // UiAccountHelper registers firstName=QA and lastName=Automation.
        Assert.assertEquals(author, "QA Automation",
            "Comment author must match the submitting account's registered display name");
        System.out.println("VOTE-TC13 author attribution=PASS");
    }

    @Test(description = "VOTE-TC16: comment submission immediately displays the submitted content")
    public void commentSubmissionDisplaysImmediateFeedback() {
        driver.get(CAR_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        String comment = "QA comment feedback " + UUID.randomUUID();
        By commentCell = By.xpath(COMMENT_TABLE + "/tbody/tr/td[3][text()='" + comment + "']");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(COMMENT));
        Assert.assertTrue(driver.findElements(commentCell).isEmpty(),
            "The unique comment must not already be published");
        input.sendKeys(comment);

        // The actual submission control also votes. Click once; no retry or refresh.
        wait.until(ExpectedConditions.elementToBeClickable(VOTE_BUTTON)).click();
        WebElement published = wait.withMessage(
            "Comment submission outcome was not communicated by displaying the submitted comment")
            .until(ExpectedConditions.visibilityOfElementLocated(commentCell));
        Assert.assertEquals(published.getText(), comment,
            "The immediate response must display the exact submitted comment");
        System.out.println("VOTE-TC16 comment-specific response: " + published.getText());
        // Observe vote feedback separately; it is not the comment-success assertion.
        for (WebElement feedback : driver.findElements(SUCCESS)) {
            if (feedback.isDisplayed()) {
                System.out.println("VOTE-TC16 accompanying vote notification: " + feedback.getText());
            }
        }
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
