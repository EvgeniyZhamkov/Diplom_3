import io.qameta.allure.Description;
import utils.NewUserApi;
import utils.NecessaryLinks;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import pageobject.RegistrationPage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import utils.WebDriverFactor;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static utils.WebDriverFactor.getWebDriver;

@DisplayName("Регистрация пользователя")
@RunWith(Parameterized.class)
public class RegisterPageTest {
    private WebDriver webDriver;
    private String browserName;
    private RegistrationPage registrationPage;
    private String email, name, password;

    @Parameterized.Parameters(name = "Browser {0}")
    public static Object[][] initParams() {
        // Загружаем настройки из файла config.properties
        Properties properties = new Properties();
        try (InputStream input = RegisterPageTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден в ресурсах");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        // Получаем список браузеров из файла
        String browsers = properties.getProperty("browsers", "chrome,yandex"); // Значение по умолчанию
        return Arrays.stream(browsers.split(","))
                .map(browser -> new Object[]{browser.trim()})
                .toArray(Object[][]::new);
    }
    public RegisterPageTest(String browserName) {
        this.browserName = browserName;
    }

    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        webDriver = WebDriverFactor.getWebDriver();
        webDriver.get(NecessaryLinks.URL_REGISTER_PAGE);
        registrationPage = new RegistrationPage(webDriver);

        email = "email_" + UUID.randomUUID() + "@gmail.com";
        name = "name";
        password = "pass_" + UUID.randomUUID();

        Allure.addAttachment("Имя", name);
        Allure.addAttachment("Email", email);
        Allure.addAttachment("Пароль", password);
    }

    @After
    @Step("Закрытие браузера и очистка данных")
    public void tearDown() {
        webDriver.quit();
        new NewUserApi().deleteTestUser(email, password);
    }

    @Test
    @DisplayName("Успешная регистрация")
    @Description("Успешная регистрация")
    public void registerNewUserIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        registrationPage.setEmail(email);
        registrationPage.setName(name);
        registrationPage.setPassword(password);

        registrationPage.clickRegisterButton();

        registrationPage.waitFormSubmitted("Вход");

        checkFormReload();
    }

    @Test
    @DisplayName("Регистрация с коротким паролем")
    @Description("Регистрация с коротким паролем")
    public void registerNewUserLowPasswordIsFailedTest() {
        Allure.parameter("Браузер", browserName);

        registrationPage.setEmail(email);
        registrationPage.setName(name);
        registrationPage.setPassword(password.substring(0, 3));

        registrationPage.clickRegisterButton();

        registrationPage.waitErrorIsVisible();

        checkErrorMessage();
    }

    @Step("Проверка перезагрузки формы регистрации")
    private void checkFormReload() {
        MatcherAssert.assertThat(
                "Форма регистрации не перезагрузилась",
                webDriver.getCurrentUrl(),
                containsString("/login")
        );
    }

    @Step("Проверка появления сообщения об ошибке")
    private void checkErrorMessage() {
        MatcherAssert.assertThat(
                "Некорректное сообщение об ошибке",
                registrationPage.getErrorMessage(),
                equalTo("Некорректный пароль")
        );
    }
}
