package org.example.config;

public class Config {
    // Paste your active PrestaShop demo store URL here (extracted from iframe#framelive)
    public static final String BASE_URL = "https://stable-steel.demo.prestashop.com/";

    public static final boolean HEADLESS = "true".equalsIgnoreCase(System.getenv("HEADLESS"));

    // Explicit wait timeouts in seconds
    public static final int TIMEOUT_SHORT = 5;
    public static final int TIMEOUT_DEFAULT = 15;
    public static final int TIMEOUT_LONG = 45;

    // Guest Customer Data
    public static final String GENDER = "Mr.";
    public static final String FIRST_NAME = "Jae";
    public static final String LAST_NAME = "Smith";
    public static final String EMAIL = "qa.autotest@example.com";
    public static final String ADDRESS = "123 Main St";
    public static final String CITY = "New York";
    public static final String POSTCODE = "10001";
    public static final String COUNTRY = "United States";
    public static final String STATE = "New York";
}
