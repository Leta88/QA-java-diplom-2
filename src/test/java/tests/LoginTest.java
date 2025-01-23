package tests;

import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class LoginTest {

    @DisplayName("Check user can log in")
    @Test
    public void loginIsPossibleTest(){

        UserApi userApi = new UserApi();
        UserData userData = new UserData("Login" + RandomStringUtils.randomAlphabetic(4), "passwordqwery", "user" + RandomStringUtils.randomAlphabetic(4) + "@mail.ru");

        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));

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
                .and()
                .body("success", is(true));
    }

    @DisplayName("Check user which doen't exist can not log in")
    @Test
    public void userNotExistsLoginTest(){
        UserApi userApi = new UserApi();
        UserData userData = new UserData(null, RandomStringUtils.randomAlphabetic(5), "user" + RandomStringUtils.randomAlphabetic(5) + "@mail.ru");

        ValidatableResponse response = userApi.loginUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("message", is("email or password are incorrect"));
    }

    @DisplayName("Check user can not login without email")
    @Test
    public void loginIsNotPossibleWithoutEmailTest(){

        UserApi userApi = new UserApi();
        UserData userData = new UserData("Login" + RandomStringUtils.randomAlphabetic(4), "passwordqwery", "user" + RandomStringUtils.randomAlphabetic(4) + "@mail.ru");

        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));

        userData.setName(null);
        response = userApi.loginUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));

        String accessToken = response.extract().path("accessToken");

        userData.setEmail(null);
        response = userApi.loginUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("message", is("email or password are incorrect"));

        response = userApi.deleteUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .and()
                .body("success", is(true));
    }
}
