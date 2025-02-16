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
import pageobject.*;

import java.util.UUID;

import static utils.WebDriverFactor.getWebDriver;
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

    @Parameterized.Parameters(name="Browser {0}")
    public static Object[][] initParams() {
        return new Object[][] {
                {"chrome"},
                {"yandex"}
        };
    }
    public ProfilePageTest(String browserName) {
        this.browserName = browserName;
    }
    @Before
    @Step("Запуск браузера, подготовка тестовых данных")
    public void startUp() {
        driver = getWebDriver(browserName);
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
    public void checkLinkToProfileIsSuccess() {
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
    public void checkLinkToConstructorIsSuccess() {
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
    public void checkLinkOnLogoIsSuccess() {
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
    public void checkLinkLogOutIsSuccess() {
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

