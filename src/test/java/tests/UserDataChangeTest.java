package tests;

import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class UserDataChangeTest {

    protected UserApi userApi;
    protected UserData userData;
    protected String accessToken;
    protected String initialName;
    protected String initialEmail;

    @Before
    public void setUp(){
        userApi = new UserApi();
        initialName = "Login" + RandomStringUtils.randomAlphabetic(4);
        initialEmail = "user" + RandomStringUtils.randomAlphabetic(4).toLowerCase() + "@mail.ru";
        userData = new UserData(initialName, RandomStringUtils.randomAlphabetic(4), initialEmail);

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

        accessToken = response.extract().path("accessToken");
    }

    @After
    public void cleanUp(){
        ValidatableResponse response = userApi.deleteUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .and()
                .body("success", is(true));
    }

    @DisplayName("Check it is possible to update name")
    @Test
    public void updateNameTest(){

        String newName = "John";

        userData.setName(newName);
        userData.setPassword(null);
        userData.setEmail(null);

        ValidatableResponse response = userApi.updateUser(userData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        response = userApi.getUserData(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.name", is(newName));;
    }

    @DisplayName("Check it is possible to update email")
    @Test
    public void updateEmailTest(){

        String newEmail = "new_email" + RandomStringUtils.randomAlphabetic(4).toLowerCase() + "@mail.ru";

        userData.setName(null);
        userData.setPassword(null);
        userData.setEmail(newEmail);

        ValidatableResponse response = userApi.updateUser(userData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        response = userApi.getUserData(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.email", is(newEmail));
    }

    @DisplayName("Check it is not possible to update name without authorization")
    @Test
    public void notPossibleToUpdateNameWithoutAuthTest(){

        String newName = "John";

        userData.setName(newName);
        userData.setPassword(null);
        userData.setEmail(null);

        ValidatableResponse response = userApi.updateUserNoAuthorize(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", is(false));

        response = userApi.getUserData(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.name", is(initialName));
    }

    @DisplayName("Check it is not possible to update email without authorization")
    @Test
    public void notPossibleToUpdateEmailWithoutAuthTest(){

        String newEmail = "new_email" + RandomStringUtils.randomAlphabetic(4).toLowerCase() + "@mail.ru";

        userData.setName(null);
        userData.setPassword(null);
        userData.setEmail(newEmail);

        ValidatableResponse response = userApi.updateUserNoAuthorize(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", is(false));

        response = userApi.getUserData(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.email", is(initialEmail));
    }

}
