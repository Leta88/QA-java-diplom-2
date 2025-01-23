package tests;

import api.OrderApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderData;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class OrderCreationNoAuthTest {

    @DisplayName("Check it is possible to create order with correct list of ingredients without authorization")
    @Test
    public void orderCreationWithCorrectIngredientsWithoutAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrderNoAuthorize(orderData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", is(true));
    }

    @DisplayName("Check it is not possible to create order with incorrect list of ingredients without authorization")
    @Test
    public void notPossibleToCreateOrderWithIncorrectIngredientsWithoutAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {"AAA", "BBB"};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrderNoAuthorize(orderData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("Check it is not possible to create order without ingredients without authorization")
    @Test
    public void notPossibleToCreateOrderWithoutIngredientsWithoutAuthTest(){

        OrderApi orderApi = new OrderApi();

        String[] ingredients = {};
        OrderData orderData = new OrderData(ingredients);

        ValidatableResponse response = orderApi.createOrderNoAuthorize(orderData);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }
}
