package com.bgr.qa.helpers;

import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Small public-UI account helper; keeps credentials in memory without logging them. */
public final class UiAccountHelper {
    public record Credentials(String username, String password) {
        @Override
        public String toString() {
            return "Credentials[redacted]";
        }
    }

    private UiAccountHelper() {
    }

    public static void registerAndLogin(WebDriver driver, WebDriverWait wait) {
        login(driver, wait, register(driver, wait));
    }

    /** Registers one account and returns its credentials without logging in. */
    public static Credentials register(WebDriver driver, WebDriverWait wait) {
        String username = "tc03_" + UUID.randomUUID().toString().replace("-", "");
        String password = UUID.randomUUID() + "aA!7";

        driver.get("https://buggy.justtestit.org/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")))
            .sendKeys(username);
        driver.findElement(By.id("firstName")).sendKeys("QA");
        driver.findElement(By.id("lastName")).sendKeys("Automation");
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("confirmPassword")).sendKeys(password);
        // One submission only: a timeout must not create another account.
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[normalize-space()='Register']"))).click();
        wait.withMessage("Account preparation: registration success was not shown")
            .until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[normalize-space()='Registration is successful']")));
        return new Credentials(username, password);
    }

    /** Logs in on the current page without navigating or refreshing it. */
    public static void login(WebDriver driver, WebDriverWait wait, Credentials credentials) {
        // Scope to nav: the registration form also has a password input.
        driver.findElement(By.cssSelector("nav input[name='login']"))
            .sendKeys(credentials.username());
        driver.findElement(By.cssSelector("nav input[name='password']"))
            .sendKeys(credentials.password());
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//nav//button[normalize-space()='Login']"))).click();
        wait.withMessage("Account preparation: login did not reach the authenticated UI")
            .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
    }
}
