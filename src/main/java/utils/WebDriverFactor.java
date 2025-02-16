/*package utils;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactor {
    public static WebDriver getWebDriver(String browserName) {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        switch (browserName) {
            case "chrome":
                return new ChromeDriver(options);

            case "yandex":
                return new ChromeDriver(options.setBinary("C:\\Users\\User\\AppData\\Local\\Yandex\\YandexBrowser\\Application"));

            default:
                throw new RuntimeException("Incorrect browser name");
        }
    }
}*/

package utils;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactor {
    public static WebDriver getWebDriver(String browserName) {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        switch (browserName) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver-win64/chromedriver.exe");
                return new ChromeDriver(options);

            case "yandex":
                // Укажите путь к YandexDriver
                System.setProperty("webdriver.chrome.driver", "src/main/resources/yandexdriver1/yandexdriver.exe");
                // Укажите путь к бинарному файлу Yandex Browser
                options.setBinary("C:\\Users\\User\\AppData\\Local\\Yandex\\YandexBrowser\\Application\\browser.exe");
                return new ChromeDriver(options);

            default:
                throw new RuntimeException("Incorrect browser name");
        }
    }
}
