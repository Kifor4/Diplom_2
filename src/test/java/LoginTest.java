import clients.auth.LoginAPIClient;
import clients.auth.RegisterAPIClient;
import clients.auth.UserAPIClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@DisplayName("Авторизация пользователя")
public class LoginTest {
    private LoginAPIClient loginAPIClient;
    private UserAPIClient userAPIClient;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        RegisterAPIClient registerAPIClient = new RegisterAPIClient();
        loginAPIClient = new LoginAPIClient();
        userAPIClient = new UserAPIClient();
        password = randomAlphanumeric(8, 10);
        name = randomAlphabetic(5, 10);
        email = name.toLowerCase() + "@testmail.ru";
        Response response = registerAPIClient.registerUser(email, password, name);
        accessToken = registerAPIClient.getAccessTokenFromResponse(response);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void positiveUserLogin() {
        Response response = loginAPIClient.loginUser(email, password);
        loginAPIClient.checkPositiveUserAuthorization(response, email, name);
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void negativeUserLoginWithWrongPassword() {
        Response response = loginAPIClient.loginUser(email, password + " ");
        loginAPIClient.checkNegativeUserAuthorization(response);
    }

    @Test
    @DisplayName("Логин с неверным е-мэйлом")
    public void negativeUserLoginWithWrongEmail() {
        Response response = loginAPIClient.loginUser("a" + email, password);
        loginAPIClient.checkNegativeUserAuthorization(response);
    }

    @After
    public void tearDown() {
        userAPIClient.deleteUser(accessToken);
    }
}