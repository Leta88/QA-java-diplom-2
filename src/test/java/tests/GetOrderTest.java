package tests;

import api.IngredientsApi;
import api.OrderApi;
import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderData;
import model.UserData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class GetOrderTest {

    @DisplayName("Check it is impossible to get list of orders without authorization")
    @Test
    public void notPossibleToGetOrderListWithoutAuthTest(){
        OrderApi orderApi = new OrderApi();

        ValidatableResponse response = orderApi.getOrdersOfUserNoAuthorize();
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }

    @DisplayName("Check getting list of orders for selected user")
    @Test
    public void getOrderListTest(){
        UserApi userApi = new UserApi();
        UserData userData = new UserData("Login" + RandomStringUtils.randomAlphabetic(4),
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

        String accessToken = response.extract().path("accessToken");

        OrderApi orderApi = new OrderApi();

        String[] ingredients = IngredientsApi.getIngredients(2);
        OrderData orderData = new OrderData(ingredients);

        response = orderApi.createOrder(orderData, accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));

        response = orderApi.getOrdersOfUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));

        response = userApi.deleteUser(accessToken);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .and()
                .body("success", is(true));
    }
}
