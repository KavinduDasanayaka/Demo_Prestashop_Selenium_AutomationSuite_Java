package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Product Details Page.
 */
public class ProductPage extends BasePage {

    // Locators
    private final By name = By.cssSelector("h1.product__name");
    private final By price = By.cssSelector(".product__prices .product__price, .product__price");
    private final By quantityInput = By.cssSelector("#quantity_wanted");
    private final By incrementButton = By.cssSelector("button.js-increment-button");
    private final By addToCartButton = By.cssSelector("button.product__add-to-cart-button");
    private final By headerCartLink = By.cssSelector("a.header-block__action-btn[href*='cart?action=show']");

    public ProductPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public void waitUntilLoaded() {
        waitForVisible(name);
        waitForPresent(quantityInput);
    }

    public String getName() {
        return getText(name);
    }

    public double getUnitPrice() {
        return parsePrice(getText(price));
    }

    public int getQuantity() {
        String value = waitForPresent(quantityInput).getAttribute("value");
        return Integer.parseInt(value.trim());
    }

    /** Clicks the (+) button the given number of times and waits for the quantity refresh AJAX to finish. */
    public void clickIncrement(int times) {
        for (int i = 0; i < times; i++) {
            int before = getQuantity();
            click(incrementButton);
            getWait(15).until(d -> getQuantity() == before + 1);
        }
        waitForAjaxIdle();
    }

    /** Clicks "Add to cart" and returns the CartModal popup object. */
    public CartModal addToCart() {
        waitForAjaxIdle();
        click(addToCartButton);
        CartModal modal = new CartModal(driver, baseUrl);
        modal.waitUntilVisible();
        return modal;
    }

    /** Reads the number of items shown in the top-right Cart icon (e.g. "Cart 3" -> 3). */
    public int getHeaderCartCount() {
        String digits = waitForPresent(headerCartLink).getText().replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    /** Waits until jQuery has no active background AJAX calls. */
    private void waitForAjaxIdle() {
        getWait(15).until(d -> Boolean.TRUE.equals(
            ((JavascriptExecutor) d).executeScript("return typeof jQuery === 'undefined' || jQuery.active === 0;")
        ));
    }
}
