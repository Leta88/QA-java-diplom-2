package tests;

import api.OrderApi;
import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderData;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class OrderCreationAuthTest {

    protected UserApi userApi;
    protected UserData userData;
    protected String accessToken;

    @Before
    public void setUp(){
        userApi = new UserApi();
        userData = new UserData("Login" + RandomStringUtils.randomAlphabetic(4),
                RandomStringUtils.randomAlphabetic(4),
                "user" + RandomStringUtils.randomAlphabetic(4).toLowerCase() + "@mail.ru");

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

    @DisplayName("Check it is possible to create order with correct list of ingredients with authorization")
    @Test
    public void orderCreationWithCorrectIngredientsWithAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrder(orderData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));
    }

    @DisplayName("Check it is not possible to create order with incorrect list of ingredients with authorization")
    @Test
    public void notPossibleToCreateOrderWithIncorrectIngredientsWithAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {"AAA", "BBB"};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrder(orderData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("Check it is not possible to create order without ingredients with authorization")
    @Test
    public void notPossibleToCreateOrderWithoutIngredientsWithAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrder(orderData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }
}
