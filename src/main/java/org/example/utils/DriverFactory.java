package org.example.utils;

import org.example.config.Config;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {
    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--window-size=1440,1000",
            "--disable-notifications",
            "--lang=en-US",
            "--disable-gpu",
            "--disable-dev-shm-usage",
            "--no-sandbox"
        );
        if (Config.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL); //EAGER //NONE

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));
        return driver;
    }
}
