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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class UserCreationTest {

    protected String accessToken;
    protected UserData userData;
    protected UserApi userApi;

    private final String name;
    private final String password;
    private final String email;

    public UserCreationTest (String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {"Login" + RandomStringUtils.randomAlphabetic(4), "passwordqwery", "user" + RandomStringUtils.randomAlphabetic(4) + "@mail.ru"},
                {"!@#$%^&*()_+}", "!@#$%^&*()_+}", "user@mail.ru" + RandomStringUtils.randomAlphabetic(4).toLowerCase()},
        };
    }

    @Before
    public void setUp() {
        userApi = new UserApi();
        userData = new UserData(name, password, email);
    }

    @After
    public void cleanUp() {
        userData.setName(null);
        ValidatableResponse response = userApi.loginUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        accessToken = response.extract().path("accessToken");
        response = userApi.deleteUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .body("success", is(true));
    }

    @DisplayName("Check user can be created")
    @Test
    public void userCanBeCreatedTest() {
        ValidatableResponse response = userApi.createUser(userData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }
}
