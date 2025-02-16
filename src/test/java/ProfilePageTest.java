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
import pageobject.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

@DisplayName("Проверки личного кабинета пользователя")
@RunWith(Parameterized.class)
public class ProfilePageTest {
    private WebDriver driver;
    private String browserName;
    private AuthorizationPage authorizationPage;
    private MainPage mainPage;
    private ProfilePage profilePage;
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

    public ProfilePageTest(String browserName) {
        this.browserName = browserName;
    }

    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        driver = WebDriverFactor.getWebDriver();
        driver.get(NecessaryLinks.URL_MAIN_PAGE);

        authorizationPage = new AuthorizationPage(driver);
        mainPage = new MainPage(driver);
        profilePage = new ProfilePage(driver);

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
        driver.quit();
        newUserApi.deleteTestUser(email, password);
    }
    @Step("Процесс авторизации")
    private void authUser() {
        authorizationPage.setEmail(email);
        authorizationPage.setPassword(password);

        authorizationPage.clickAuthButton();

        authorizationPage.waitFormSubmitted();
    }
    @Step("Переход в личный кабинет")
    private void goToProfile() {
        driver.get(NecessaryLinks.URL_LOGIN_PAGE);
        authorizationPage.waitAuthFormVisible();

        authUser();

        mainPage.clickLinkToProfile();
        profilePage.waitAuthFormVisible();
    }
    @Test
    @DisplayName("Проверка перехода по клику на «Личный кабинет»")
    @Description("Проверка перехода по клику на «Личный кабинет»")
    public void checkLinkToProfileIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        goToProfile();

        MatcherAssert.assertThat(
                "Некорректный URL страницы Личного кабинета",
                driver.getCurrentUrl(),
                containsString("/account/profile")
        );
    }
    @Test
    @DisplayName("Проверка перехода из личного кабинета по клику на «Конструктор»")
    @Description("Проверка перехода из личного кабинета по клику на «Конструктор»")
    public void checkLinkToConstructorIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        goToProfile();

        profilePage.clickLinkToConstructor();
        mainPage.waitHeaderIsVisible();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }
    @Test
    @DisplayName("Проверка перехода из личного кабинета по клику на логотип Stellar Burgers")
    @Description("Проверка перехода из личного кабинета по клику на логотип Stellar Burgers")
    public void checkLinkOnLogoIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        goToProfile();

        profilePage.clickLinkOnLogo();
        mainPage.waitHeaderIsVisible();

        MatcherAssert.assertThat(
                "Ожидается надпись «Оформить заказ» на кнопке в корзине",
                mainPage.getBasketButtonText(),
                equalTo("Оформить заказ")
        );
    }
    @Test
    @DisplayName("Проверка выхода из личного кабинета по клику на кнопку Выйти")
    @Description("Проверка выхода из личного кабинета по клику на кнопку Выйти")
    public void checkLinkLogOutIsSuccessTest() {
        Allure.parameter("Браузер", browserName);

        goToProfile();

        profilePage.clickLogoutLink();
        authorizationPage.waitAuthFormVisible();

        MatcherAssert.assertThat(
                "Некорректный URL страницы Авторизации",
                driver.getCurrentUrl(),
                containsString("/login")
        );
    }
}

