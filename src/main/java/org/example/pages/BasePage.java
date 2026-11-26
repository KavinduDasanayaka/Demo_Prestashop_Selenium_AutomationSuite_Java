package org.example.pages;

import org.example.config.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage contains reusable Selenium wait and action methods.
 * Every Page Object extends this class so we never write raw waits or Thread.sleep() in tests.
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final String baseUrl;

    public BasePage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    /** Creates an explicit WebDriverWait for the given number of seconds. */
    protected WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /** Opens a relative path on the store (e.g. "/" or "/cart?action=show"). */
    public void open(String path) {
        driver.get(baseUrl + path);
    }

    // ---------------- Explicit Waits ----------------

    public WebElement waitForPresent(By locator) {
        return getWait(Config.TIMEOUT_DEFAULT).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForVisible(By locator) {
        return waitForVisible(locator, Config.TIMEOUT_DEFAULT);
    }

    public WebElement waitForVisible(By locator, int seconds) {
        return getWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return getWait(Config.TIMEOUT_DEFAULT).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForGone(By locator, int seconds) {
        getWait(seconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForUrlContains(String text) {
        getWait(Config.TIMEOUT_LONG).until(ExpectedConditions.urlContains(text));
    }

    // ---------------- Common Actions ----------------

    /** Waits until the element is clickable, scrolls to it, and clicks it. */
    public void click(By locator) {
        getWait(Config.TIMEOUT_DEFAULT).until(d -> {
            try {
                WebElement el = waitForClickable(locator);
                ((JavascriptExecutor) d).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
                try {
                    el.click();
                } catch (WebDriverException e) {
                    ((JavascriptExecutor) d).executeScript("arguments[0].click();", el);
                }
                return true;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                return false;
            }
        });
    }

    /** Clears an input field and types the given text. */
    public void type(By locator, String text) {
        WebElement el = waitForClickable(locator);
        el.clear();
        el.sendKeys(text);
    }

    /** Returns the visible text of an element. */
    public String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /** Selects an option from a <select> dropdown by its visible text. */
    public void selectByVisibleText(By locator, String visibleText) {
        WebElement dropdown = waitForClickable(locator);
        new Select(dropdown).selectByVisibleText(visibleText);
    }

    /** Returns how many elements match the locator on the current page. */
    public int count(By locator) {
        return driver.findElements(locator).size();
    }

    /** Converts a price string like "€14.28" into a double (14.28). */
    public static double parsePrice(String priceText) {
        String cleaned = priceText.replaceAll("[^0-9.]", "");
        return Double.parseDouble(cleaned);
    }

    /** Rounds a number to 2 decimal places for currency comparisons. */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
