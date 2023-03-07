import clients.auth.RegisterAPIClient;
import clients.auth.UserAPIClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@RunWith(JUnitParamsRunner.class)
@DisplayName("Регистрация пользователя")
public class RegisterTests {
    private RegisterAPIClient registerAPIClient;
    private UserAPIClient userAPIClient;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        registerAPIClient = new RegisterAPIClient();
        userAPIClient = new UserAPIClient();
        password = randomAlphanumeric(8, 10);
        name = randomAlphabetic(5, 10);
        email = name.toLowerCase() + "@testmail.ru";
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void positiveUserRegistration() {
        Response response = registerAPIClient.registerUser(email, password, name);
        registerAPIClient.checkPositiveUserRegistration(response, email, name);
        accessToken = registerAPIClient.getAccessTokenFromResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void negativeUserDuplicateRegistration() {
        Response response = registerAPIClient.registerUser(email, password, name);
        registerAPIClient.checkPositiveUserRegistration(response, email, name);
        accessToken = registerAPIClient.getAccessTokenFromResponse(response);
        response = registerAPIClient.registerUser(email, password, name);
        registerAPIClient.checkNegativeUserDuplicateRegistration(response);
    }

    @Test
    @Parameters({", pass, Ivan", "test@testmail.ru, , Ivan", "test@testmail.ru, pass, "})
    @DisplayName("Создание пользователя с незаполненным обязательным полем")
    public void negativeUserRegistrationWithoutParam(String email, String password, String name) {
        Response response = registerAPIClient.registerUser(email, password, name);
        registerAPIClient.checkNegativeUserRegistrationWithoutParam(response);
    }

    @After
    public void tearDown() {
        try {
            userAPIClient.deleteUser(accessToken);
        } catch (IllegalArgumentException ignored) {

        }
    }
}