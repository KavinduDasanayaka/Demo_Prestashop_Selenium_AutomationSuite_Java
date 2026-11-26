package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Page Object for the Shopping Cart Page (/cart?action=show).
 */
public class CartPage extends BasePage {

    /** Simple data holder for one product row in the cart. */
    public static class CartLine {
        public final String name;
        public final int quantity;
        public final double unitPrice;
        public final double lineTotal;

        public CartLine(String name, int quantity, double unitPrice, double lineTotal) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
        }
    }

    // Locators
    private final By checkoutLink = By.cssSelector(".cart-summary a.btn-primary[href*='order'], a.btn.btn-primary[href*='order']");
    private final By cartSummaryValue = By.cssSelector(".cart-summary__value");

    public CartPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    /** Reads all product rows currently in the cart. */
    @SuppressWarnings("unchecked")
    public List<CartLine> getLines() {
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ((JavascriptExecutor) driver).executeScript(
            "const money = (s) => parseFloat(String(s).replace(/[^0-9.]/g, ''));" +
            "const titles = [...document.querySelectorAll('a.product-line__title')].filter(a => a.textContent.trim());" +
            "return titles.map(a => {" +
            "  let row = a.parentElement;" +
            "  while (row && !row.querySelector('input') && row.parentElement) row = row.parentElement;" +
            "  const qty = parseInt(row.querySelector('input').value, 10);" +
            "  const prices = [...row.querySelectorAll('[class*=\"price\"]')]" +
            "    .filter(e => e.children.length === 0 && /[0-9]/.test(e.textContent))" +
            "    .map(e => money(e.textContent));" +
            "  return { name: a.textContent.trim(), quantity: qty, unitPrice: prices[0], lineTotal: prices[prices.length - 1] };" +
            "});"
        );

        List<CartLine> lines = new ArrayList<>();
        for (Map<String, Object> item : raw) {
            lines.add(new CartLine(
                String.valueOf(item.get("name")),
                ((Number) item.get("quantity")).intValue(),
                ((Number) item.get("unitPrice")).doubleValue(),
                ((Number) item.get("lineTotal")).doubleValue()
            ));
        }
        return lines;
    }

    /** Waits until the cart has the expected number of product rows. */
    public void waitForLineCount(int expectedCount) {
        getWait(15).until(d -> getLines().size() == expectedCount);
    }

    /** Finds a specific cart row by product name (or returns null if not found). */
    public CartLine getLineByName(String productName) {
        for (CartLine line : getLines()) {
            if (line.name.equals(productName)) {
                return line;
            }
        }
        return null;
    }

    /** Waits until the cart summary subtotal equals the expected amount and returns it. */
    public double waitForCartSummaryAmount(double expected) {
        getWait(15).until(d -> round2(parsePrice(getText(cartSummaryValue))) == round2(expected));
        return round2(parsePrice(getText(cartSummaryValue)));
    }

    public boolean isCheckoutEnabled() {
        return count(checkoutLink) > 0;
    }

    public void proceedToCheckout() {
        click(checkoutLink);
        waitForUrlContains("order");
    }
}
