package org.example.pages;

import org.example.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object for Guest Checkout (/order):
 * Step 1: Personal Information -> Step 2: Addresses -> Step 3: Shipping -> Step 4: Payment.
 */
public class CheckoutPage extends BasePage {

    // Step 1: Personal Information
    private final By genderMr = By.cssSelector("#field-id_gender_1");
    private final By firstName = By.cssSelector("#field-firstname");
    private final By lastName = By.cssSelector("#field-lastname");
    private final By email = By.cssSelector("#field-email");
    private final By termsPrivacy = By.cssSelector("#field-psgdpr");
    private final By customerPrivacy = By.cssSelector("#field-customer_privacy");
    private final By continuePersonal = By.cssSelector("[data-link-action='register-new-customer']");

    // Step 2: Addresses
    private final By address1 = By.cssSelector("#field-address1");
    private final By postcode = By.cssSelector("#field-postcode");
    private final By city = By.cssSelector("#field-city");
    private final By country = By.cssSelector("#field-id_country");
    private final By state = By.cssSelector("select[name='id_state']");
    private final By stateOptions = By.cssSelector("select[name='id_state'] option");
    private final By continueAddress = By.cssSelector("button[name='confirm-addresses']");

    // Step 3: Shipping Method
    private final By continueShipping = By.cssSelector("button[name='confirmDeliveryOption']");

    // Step 4: Payment
    private final By cashOnDeliveryRadio = By.cssSelector("#payment-option-2");
    private final By termsCheckbox = By.cssSelector("input[name^='conditions_to_approve']");
    private final By placeOrderButton = By.cssSelector("#payment-confirmation button[type='submit'].btn-primary");

    public CheckoutPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    /** Step 1: Fills guest personal details, ticks privacy checkboxes, and clicks Continue. */
    public void continueAsGuest() {
        waitForVisible(firstName);
        click(genderMr);
        type(firstName, Config.FIRST_NAME);
        type(lastName, Config.LAST_NAME);
        type(email, Config.EMAIL);
        click(termsPrivacy);
        click(customerPrivacy);
        click(continuePersonal);
        waitForVisible(address1);
    }

    /** Step 2: Fills shipping address, waits for US states dropdown to load, and clicks Continue. */
    public void fillAndSubmitAddress() {
        type(address1, Config.ADDRESS);
        type(postcode, Config.POSTCODE);
        type(city, Config.CITY);
        selectByVisibleText(country, Config.COUNTRY);

        // Wait for the US States dropdown options to load via AJAX
        getWait(15).until(d -> count(stateOptions) > 1);
        selectByVisibleText(state, Config.STATE);

        click(continueAddress);
        waitForVisible(continueShipping);
    }

    /** Step 3: Confirms the default shipping carrier and moves to the Payment step. */
    public void confirmShippingMethod() {
        click(continueShipping);
        waitForPresent(cashOnDeliveryRadio);
    }

    /** Step 4: Selects "Pay by Cash on Delivery" and ticks the Terms of Service checkbox. */
    public void selectCashOnDeliveryAndAcceptTerms() {
        WebElement cod = waitForPresent(cashOnDeliveryRadio);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cod);

        WebElement terms = waitForPresent(termsCheckbox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", terms);

        getWait(15).until(d -> isPlaceOrderEnabled());
    }

    /** Checks whether the final "Place Order" button is enabled. */
    public boolean isPlaceOrderEnabled() {
        WebElement btn = waitForPresent(placeOrderButton);
        String cls = btn.getAttribute("class");
        return btn.isEnabled() && (cls == null || !cls.contains("disabled"));
    }
}
