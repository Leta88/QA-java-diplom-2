package tests;

import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class UserCreationErrorsTest {

    private final String UserNamePrefix = "UserError";

    @DisplayName("Check that it is impossible to create two identical users")
    @Test
    public void impossibleToCreateTwoIdenticalUsersTest(){
        UserData userData = new UserData(RandomStringUtils.randomAlphabetic(4), "password1234534", UserNamePrefix + "user@mail.ru");
        UserApi userApi = new UserApi();

        ValidatableResponse response = userApi.createUser(userData);
        response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("message", is("User already exists"));

        userData.setName(null);
        response = userApi.loginUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        String accessToken = response.extract().path("accessToken");
        response = userApi.deleteUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .body("success", is(true));
    }

    @DisplayName("Check user can not be created without Password")
    @Test
    public void userCanNotBeCreatedWithoutPasswordTest() {
        UserData userData = new UserData(RandomStringUtils.randomAlphabetic(4), null, UserNamePrefix + "user@mail.ru");

        UserApi userApi = new UserApi();
        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @DisplayName("Check user can not be created without email")
    @Test
    public void userCanNotBeCreatedWithoutEmailTest() {

        UserData userData = new UserData(UserNamePrefix + RandomStringUtils.randomAlphabetic(4), "password1234534", null);

        UserApi userApi = new UserApi();
        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

}