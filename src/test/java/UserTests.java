import clients.auth.RegisterAPIClient;
import clients.auth.UserAPIClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@DisplayName("Изменение данных пользователя")
public class UserTests {
    private UserAPIClient userAPIClient;
    private String email;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        RegisterAPIClient registerAPIClient = new RegisterAPIClient();
        userAPIClient = new UserAPIClient();
        String password = randomAlphanumeric(8, 10);
        name = randomAlphabetic(5, 10);
        email = name.toLowerCase() + "@testmail.ru";
        Response response = registerAPIClient.registerUser(email, password, name);
        accessToken = registerAPIClient.getAccessTokenFromResponse(response);
    }

    @Test
    @DisplayName("Изменение е-мэйла пользователя с авторизацией")
    public void positiveEmailChanging() {
        String newEmail = randomAlphabetic(5, 10).toLowerCase() + "@testmail.ru";
        Response response = userAPIClient.changeUserData(accessToken, newEmail, name);
        userAPIClient.checkPositiveUserChanging(response, newEmail, name);
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void positiveNameChanging() {
        String newName = randomAlphabetic(5, 10);
        Response response = userAPIClient.changeUserData(accessToken, email, newName);
        userAPIClient.checkPositiveUserChanging(response, email, newName);
    }

    @Test
    @DisplayName("Изменение е-мэйла пользователя без авторизации")
    public void negativeEmailChangingWithoutAuth() {
        String newEmail = randomAlphabetic(5, 10).toLowerCase() + "@testmail.ru";
        Response response = userAPIClient.changeUserData("", newEmail, name);
        userAPIClient.checkNegativeUserChangingWithoutAuth(response);
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void negativeNameChangingWithoutAuth() {
        String newName = randomAlphabetic(5, 10);
        Response response = userAPIClient.changeUserData("", email, newName);
        userAPIClient.checkNegativeUserChangingWithoutAuth(response);
    }

    @After
    public void tearDown() {
        userAPIClient.deleteUser(accessToken);
    }
}