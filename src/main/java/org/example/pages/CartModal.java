package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the "Added to your cart" confirmation popup modal.
 */
public class CartModal extends BasePage {

    // Locators
    private final By root = By.cssSelector("#blockcart-modal");
    private final By title = By.cssSelector("#blockcart-modal-title");
    private final By productName = By.cssSelector("#blockcart-modal .blockcart-modal__name");
    private final By quantity = By.cssSelector("#blockcart-modal .blockcart-modal__quantity.d-lg-block");
    private final By subtotal = By.cssSelector("#blockcart-modal .blockcart-modal__total .subtotals.value");
    private final By continueShoppingButton = By.xpath("//div[contains(@class,'modal') and contains(@class,'show')]//button[@data-bs-dismiss='modal' and normalize-space()='Continue shopping']");
    private final By proceedToCheckoutLink = By.cssSelector("#blockcart-modal .modal-footer a.btn-primary");

    public CartModal(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public void waitUntilVisible() {
        waitForVisible(title, 30);
        waitForClickable(continueShoppingButton);
    }

    public String getTitle() {
        return getText(title);
    }

    public String getProductName() {
        return getText(productName);
    }

    public int getQuantity() {
        String digits = getText(quantity).replaceAll("\\D", "");
        return Integer.parseInt(digits);
    }

    public double getSubtotal() {
        return parsePrice(getText(subtotal));
    }

    public void continueShopping() {
        click(continueShoppingButton);
        waitForGone(root, 20);
    }

    public void proceedToCheckout() {
        click(proceedToCheckoutLink);
        waitForUrlContains("cart");
    }
}
