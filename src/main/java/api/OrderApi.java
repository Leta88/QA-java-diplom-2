package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi{

    private static final String ACT_ORDER_URI = "/api/orders";

    @Step("Create order without authorization")
    public ValidatableResponse createOrderNoAuthorize(OrderData order){
        return given()
                .spec(requestSpecification())
                .and()
                .body(order)
                .when()
                .post(ACT_ORDER_URI)
                .then();
    }

    @Step("Create order with authorization")
    public ValidatableResponse createOrder(OrderData order, String token){
        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", token)
                .and()
                .body(order)
                .when()
                .post(ACT_ORDER_URI)
                .then();
    }

    @Step("Get all orders for selected user")
    public ValidatableResponse getOrdersOfUser(String token){
        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", token)
                .when()
                .get(ACT_ORDER_URI)
                .then();
    }

    @Step("Get all orders for selected user without authorization")
    public ValidatableResponse getOrdersOfUserNoAuthorize(){
        return given()
                .spec(requestSpecification())
                .when()
                .get(ACT_ORDER_URI)
                .then();
    }
}
