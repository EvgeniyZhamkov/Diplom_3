import io.qameta.allure.Description;
import utils.NewUserApi;
import utils.NecessaryLinks;
import utils.WebDriverFactor; // Импорт класса WebDriverFactor
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
import pageobject.AuthorizationPage;
import pageobject.PageForgottenPassword;
import pageobject.MainPage;
import pageobject.RegistrationPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@DisplayName("Авторизация пользователя")
@RunWith(Parameterized.class)
public class AuthorizationTest {
    private WebDriver webDriver;
    private String browserName;
    private AuthorizationPage authorizationPage;
    private MainPage mainPage;
    private RegistrationPage registerPage;
    private PageForgottenPassword pageForgottenPassword;
    private String name, email, password;
    private NewUserApi newUserApi;

    @Parameterized.Parameters(name = "Browser {0}")
    public static Object[][] initParams() {
        // Загружаем настройки из файла config.properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        // Получаем список браузеров из файла
        String browsers = properties.getProperty("browsers", "chrome,yandex"); // Значение по умолчанию
        return Arrays.stream(browsers.split(","))
                .map(browser -> new Object[]{browser.trim()})
                .toArray(Object[][]::new);
    }

    public AuthorizationTest(String browserName) {
        this.browserName = browserName;
    }

    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        // Создаем драйвер, используя WebDriverFactor (без передачи browserName)
        webDriver = WebDriverFactor.getWebDriver();
        webDriver.get(NecessaryLinks.URL_MAIN_PAGE);

        authorizationPage = new AuthorizationPage(webDriver);
        mainPage = new MainPage(webDriver);
        registerPage = new RegistrationPage(webDriver);
        pageForgottenPassword = new PageForgottenPassword(webDriver);

        name = "name";
        email = "email_" + UUID.randomUUID() + "@gmail.com";
        password = "pass_" + UUID.randomUUID();

        Allure.addAttachment("Имя", name);
        Allure.addAttachment("Email", email);
        Allure.addAttachment("Пароль", password);

        newUserApi = new NewUserApi();
        newUserApi.createUser(name, email,password);
    }
    @After
    @Step("Закрытие браузера и очистка данных")
    public void tearDown() {
        webDriver.quit();
        newUserApi.deleteTestUser(email, password);
    }

    @Step("Процесс авторизации")
    private void authUser() {
        authorizationPage.setEmail(email);
        authorizationPage.setPassword(password);

        authorizationPage.clickAuthButton();

        authorizationPage.waitFormSubmitted();
    }

    @Test
    @DisplayName("Вход по кнопке «Войти в аккаунт» на главной")
    @Description("Вход по кнопке «Войти в аккаунт» на главной")
    public void authFromMainIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        mainPage.clickAuthButton();
        authorizationPage.waitAuthFormVisible();

        authUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку «Личный кабинет»")
    @Description("Вход через кнопку «Личный кабинет»")
    public void authFromLinkToProfileIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        mainPage.clickLinkToProfile();
        authorizationPage.waitAuthFormVisible();

        authUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации")
    @Description("Вход через кнопку в форме регистрации")
    public void authLinkFromRegFormIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        webDriver.get(NecessaryLinks.URL_REGISTER_PAGE);

        registerPage.clickAuthLink();
        authorizationPage.waitAuthFormVisible();

        authUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }

    @Test
    @DisplayName("Вход через кнопку в форме восстановления пароля")
    @Description("Вход через кнопку в форме восстановления пароля")
    public void authLinkFromForgotPasswordFormIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        webDriver.get(NecessaryLinks.URL_FORGOT_PASSWORD_PAGE);

        pageForgottenPassword.clickAuthLink();
        authorizationPage.waitAuthFormVisible();

        authUser();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }
}