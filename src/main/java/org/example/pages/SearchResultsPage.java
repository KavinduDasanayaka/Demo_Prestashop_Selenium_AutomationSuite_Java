package org.example.pages;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the top Search Bar and the Search Results page.
 */
public class SearchResultsPage extends BasePage {

    // Locators
    private final By searchInput = By.cssSelector("#ps_searchbar_input");
    private final By productCards = By.cssSelector("article.product-miniature");
    private final By productTitles = By.cssSelector("article.product-miniature a.product-miniature__title");
    private final By productPrices = By.cssSelector("article.product-miniature [class*='price']");
    private final By resultCount = By.cssSelector("#js-product-list-top .products__count");
    private final By sortButton = By.cssSelector("#sort_dropdown_button");

    public SearchResultsPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    private By sortOption(String label) {
        return By.xpath("//div[contains(@class,'products__sort-dropdown')]//a[normalize-space()='" + label + "']");
    }

    private By productTitleLink(String productName) {
        return By.xpath("//article[contains(@class,'product-miniature')]//a[contains(@class,'product-miniature__title') and normalize-space()='" + productName + "']");
    }

    // Actions

    public void openHome() {
        open("/");
        waitForVisible(searchInput);
    }

    public void search(String keyword) {
        WebElement input = waitForClickable(searchInput);
        input.click();
        input.clear();
        input.sendKeys(keyword, Keys.ENTER);
        waitForUrlContains("search");
        getWait(20).until(d -> count(productCards) > 0);
    }

    public void sortBy(String label) {
        click(sortButton);
        click(sortOption(label));
        // Wait until the Sort dropdown button text updates to the chosen option
        getWait(15).until(d -> waitForPresent(sortButton).getText().trim().equals(label));
        getWait(15).until(d -> count(productCards) > 0);
    }

    public int getResultCount() {
        return count(productCards);
    }

    public String getResultCountText() {
        String text = getText(resultCount);
        System.out.println(text);
        return text;
    }

    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement el : driver.findElements(productTitles)) {
            names.add(el.getText().trim());
        }
        return names;
    }

    /** Reads the main price of each product card on the page. */
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (WebElement card : driver.findElements(productCards)) {
            for (WebElement priceEl : card.findElements(By.cssSelector("[class*='price']"))) {
                String text = priceEl.getText().trim();
                if (text.matches(".*\\d+.*") && priceEl.findElements(By.xpath("./*")).isEmpty()) {
                    prices.add(parsePrice(text));
                    break;
                }
            }
        }
        return prices;
    }

    public void openProduct(String productName) {
        click(productTitleLink(productName));
        waitForUrlContains(".html");
    }
}
