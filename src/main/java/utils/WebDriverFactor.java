package utils;

import java.io.FileInputStream;
import java.io.IOException;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Properties;

public class WebDriverFactor {
    public static WebDriver getWebDriver() {
        // Создаем объект Properties
        Properties properties = new Properties();

        try {
            // Загружаем файл config.properties
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            // Обработка ошибки, если файл не найден или не может быть прочитан
            throw new RuntimeException("Failed to load config.properties", e);
        }

        // Получаем значение браузера из файла
        String browserName = properties.getProperty("browser", "chrome"); // Значение по умолчанию - "chrome"

        // Создаем драйвер на основе значения из файла
        return createDriver(browserName);
    }

    private static WebDriver createDriver(String browserName) {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
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
