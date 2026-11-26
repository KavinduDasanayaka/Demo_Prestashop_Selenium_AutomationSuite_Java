import org.example.config.Config;
import org.example.pages.*;
import org.example.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class selenium_tests {

    private static final String PRODUCT_1 = "Mug The adventure begins";
    private static final String PRODUCT_2 = "Mug The best is yet to come";

    private String baseUrl;
    private WebDriver webDriver;

    @BeforeMethod
    public void setup() {
        webDriver = DriverFactory.createDriver();
        baseUrl = Config.BASE_URL.replaceAll("/$", "");
        System.out.println("[setup] Using shop instance: " + baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Test(description = "Full E2E Guest Shopping Flow")
    public void fullEndToEndGuestShoppingFlowTest() {
        SearchResultsPage search = new SearchResultsPage(webDriver, baseUrl);
        ProductPage product = new ProductPage(webDriver, baseUrl);
        CartPage cart = new CartPage(webDriver, baseUrl);
        CheckoutPage checkout = new CheckoutPage(webDriver, baseUrl);

        // 1. Search for "mug" and sort by "Price, low to high"
        search.openHome();
        search.search("mug");
        Assert.assertTrue(search.getResultCount() > 0, "Search should return products");
        Assert.assertTrue(search.getResultCountText().contains("products"), "Result count text should be displayed");

        search.sortBy("Price, low to high");
        List<Double> prices = search.getProductPrices();
        List<Double> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);
        Assert.assertEquals(prices, sortedPrices, "Products should be sorted by price ascending");

        // 2. Open Product 1 and increase quantity from 1 -> 3
        search.openProduct(PRODUCT_1);
        product.waitUntilLoaded();
        Assert.assertEquals(product.getName(), PRODUCT_1);
        double unitPrice1 = product.getUnitPrice();

        Assert.assertEquals(product.getQuantity(), 1);
        product.clickIncrement(2);
        Assert.assertEquals(product.getQuantity(), 3);

        // 3. Add Product 1 to cart and verify popup modal
        CartModal modal1 = product.addToCart();
        Assert.assertTrue(modal1.getTitle().toLowerCase().contains("added to your cart"));
        Assert.assertEquals(modal1.getProductName(), PRODUCT_1);
        Assert.assertEquals(modal1.getQuantity(), 3);
        Assert.assertEquals(BasePage.round2(modal1.getSubtotal()), BasePage.round2(unitPrice1 * 3));

        // 4. Click "Continue shopping" and verify header cart badge shows 3
        modal1.continueShopping();
        Assert.assertEquals(product.getHeaderCartCount(), 3);

        // 5. Search for Product 2 and add 1 unit to cart
        search.search("mug");
        search.openProduct(PRODUCT_2);
        product.waitUntilLoaded();
        Assert.assertEquals(product.getName(), PRODUCT_2);
        double unitPrice2 = product.getUnitPrice();

        CartModal modal2 = product.addToCart();
        Assert.assertEquals(modal2.getProductName(), PRODUCT_2);
        Assert.assertEquals(modal2.getQuantity(), 1);

        // 6. Go to Cart page and verify both products, quantities, and totals
        modal2.proceedToCheckout();
        cart.waitForLineCount(2);

        CartPage.CartLine line1 = cart.getLineByName(PRODUCT_1);
        CartPage.CartLine line2 = cart.getLineByName(PRODUCT_2);
        Assert.assertNotNull(line1, "Product 1 should be in the cart");
        Assert.assertNotNull(line2, "Product 2 should be in the cart");
        Assert.assertEquals(line1.quantity, 3);
        Assert.assertEquals(line2.quantity, 1);
        Assert.assertEquals(BasePage.round2(line1.lineTotal), BasePage.round2(unitPrice1 * 3));
        Assert.assertEquals(BasePage.round2(line2.lineTotal), BasePage.round2(unitPrice2 * 1));

        double expectedTotal = BasePage.round2(unitPrice1 * 3 + unitPrice2);
        Assert.assertEquals(cart.waitForCartSummaryAmount(expectedTotal), expectedTotal);
        Assert.assertTrue(cart.isCheckoutEnabled());

        // 7. Proceed to Guest Checkout: Personal Info -> Address -> Shipping -> Payment
        cart.proceedToCheckout();
        checkout.continueAsGuest();
        checkout.fillAndSubmitAddress();
        checkout.confirmShippingMethod();

        // 8. Verify Place Order is initially disabled, select Cash on Delivery + Terms, and verify enabled
        Assert.assertFalse(checkout.isPlaceOrderEnabled(), "Place Order should be disabled before selecting payment");
        checkout.selectCashOnDeliveryAndAcceptTerms();
        Assert.assertTrue(checkout.isPlaceOrderEnabled(), "Place Order should be enabled after selecting payment and terms");
    }
}
