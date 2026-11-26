# PrestaShop Demo Store – Test Case Design & Selenium Automation (Java)

## 1. Prerequisites

- **Java JDK 21+** (configured with JDK 27 in `pom.xml`)
- **Apache Maven** (or bundled Maven inside IntelliJ IDEA)
- **Google Chrome** (installed normally). ChromeDriver is downloaded automatically by Selenium Manager, so no manual driver setup is needed.
- **Internet access** (the tests run against the live PrestaShop demo store).

---

## 2. Setup

```bash
git clone <repo-url>
cd "Demo_Prestashop_Selenium_AutomationSuite - Java"
mvn test-compile
```

*(Or open the project folder directly in **IntelliJ IDEA** and let Maven import dependencies from `pom.xml`.)*

---

## 3. Running the tests

### Fast Setup – Active Demo Store URL (Required before running):
The PrestaShop demo site (<https://demo.prestashop.com/#/en/front>) wraps a temporary store instance inside an `<iframe>` (`iframe#framelive`), which takes 30–90 seconds to provision on first visit. To skip the wrapper waiting time and run tests directly against an active store instance:

1. Open <https://demo.prestashop.com/#/en/front> in Chrome and wait for the store to load.
2. Add an item to the cart.
3. Copy the generated store domain (e.g., `https://agile-riddle.demo.prestashop.com`) from the link `href` in the Cart page **"Proceed to checkout"** button.(right-click inside the cart and click **Inspect** to get to Chrome DevTools).
4. Set it in `src/main/java/org/example/config/Config.java` (`BASE_URL`):

```java
public static final String BASE_URL = "https://<your-instance>.demo.prestashop.com/";
```

### Run via IntelliJ IDEA (Recommended)
1. Open `src/test/java/selenium_tests.java`.
2. Click the green **Run** icon in the gutter next to `class selenium_tests` or `fullEndToEndGuestShoppingFlowTest()`.

### Run via Maven CLI
```powershell
mvn test
```

Optional headless execution via environment variable:
```powershell
$env:HEADLESS="true"   # PowerShell: run Chrome in headless mode
mvn test
```

---

## 4. Project structure

```text
Demo_Prestashop_Selenium_AutomationSuite - Java/
├── pom.xml                                            Maven dependencies (Selenium Java 4.50.0, TestNG 7.12.0)
├── src/
│   ├── main/java/org/example/
│   │   ├── config/
│   │   │   └── Config.java                            BASE_URL, timeouts, headless flag, guest customer data
│   │   ├── utils/
│   │   │   └── DriverFactory.java                     Chrome WebDriver initialization & ChromeOptions
│   │   └── pages/
│   │       ├── BasePage.java                          Explicit WebDriverWait helpers, click/type actions, price parsing
│   │       ├── SearchResultsPage.java                 Top search bar, results grid, result count, price sorting
│   │       ├── ProductPage.java                       Product title, price, quantity (+) button, add to cart, cart badge
│   │       ├── CartModal.java                         "Added to your cart" confirmation modal (Continue shopping / Proceed)
│   │       ├── CartPage.java                          Cart line items, quantities, line totals, summary total, checkout button
│   │       └── CheckoutPage.java                      Guest checkout: Personal Info -> Address -> Shipping -> Payment
│   └── test/java/
│       └── selenium_tests.java                        TestNG full multi-product End-to-End guest shopping flow test
└── README.md
```


## full E2E Workflow

https://github.com/user-attachments/assets/f00b7de7-97c6-4940-9bdc-e15129894305



