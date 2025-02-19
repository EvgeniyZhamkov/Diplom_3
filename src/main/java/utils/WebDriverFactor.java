package utils;

import java.io.FileInputStream;
import java.io.IOException;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactor {

    /**
     * Метод для создания драйвера на основе указанного имени браузера.
     *
     * @param browserName Имя браузера (например, "chrome", "yandex").
     * @return Экземпляр WebDriver.
     */
    public static WebDriver getWebDriver(String browserName) {
        // Проверяем, что имя браузера не пустое
        if (browserName == null || browserName.isEmpty()) {
            throw new IllegalArgumentException("Browser name cannot be null or empty");
        }

        // Создаем драйвер на основе значения из параметра
        return createDriver(browserName.toLowerCase());
    }

    /**
     * Приватный метод для создания драйвера на основе имени браузера.
     *
     * @param browserName Имя браузера (например, "chrome", "yandex").
     * @return Экземпляр WebDriver.
     */
    private static WebDriver createDriver(String browserName) {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true); // Запускаем браузер в безголовом режиме
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        switch (browserName) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver-win64/chromedriver.exe");
                return new ChromeDriver(options);

            case "yandex":
                System.setProperty("webdriver.chrome.driver", "src/main/resources/yandexdriver1/yandexdriver.exe");
                options.setBinary("C:\\Users\\User\\AppData\\Local\\Yandex\\YandexBrowser\\Application\\browser.exe");
                return new ChromeDriver(options);

            default:
                throw new RuntimeException("Incorrect browser name: " + browserName);
        }
    }
}
