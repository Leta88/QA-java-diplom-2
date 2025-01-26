package api;

import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class IngredientsApi extends RestApi{

    private static final String INGREDIENTS_URL = "https://stellarburgers.nomoreparties.site/api/ingredients";

    public static String[] getIngredients(int number){
        ValidatableResponse response = given()
                .spec(requestSpecification())
                .when()
                .get(INGREDIENTS_URL)
                .then();

        List<String> ids = response.extract().jsonPath().getList("data._id", String.class);
        return ids.stream().limit(number).toArray(String[]::new);
    }
}
