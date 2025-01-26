package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.UserData;

import static io.restassured.RestAssured.given;

public class UserApi extends RestApi{

    public static final String CREATE_USER_URI = "/api/auth/register";
    public static final String LOGIN_USER_URI = "/api/auth/login";
    public static final String ACT_USER_URI = "/api/auth/user";

    @Step("Create user")
    public ValidatableResponse createUser(UserData user){
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_URI)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String token){
        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", token)
                .when()
                .delete(ACT_USER_URI)
                .then();
    }

    @Step("Login")
    public ValidatableResponse loginUser(UserData user){
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_URI)
                .then();
    }

    @Step("Update user")
    public ValidatableResponse updateUser(UserData user, String token){
        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(ACT_USER_URI)
                .then();
    }

    @Step("Update user without authorization")
    public ValidatableResponse updateUserNoAuthorize(UserData user){
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .patch(ACT_USER_URI)
                .then();
    }

    @Step("Get user data")
    public ValidatableResponse getUserData(String token){
        return given()
                .spec(requestSpecification())
                .and()
                .header("Authorization", token)
                .when()
                .get(ACT_USER_URI)
                .then();
    }

    @Step("Get user data without authorization")
    public ValidatableResponse getUserDataNoAuthorize(){
        return given()
                .spec(requestSpecification())
                .when()
                .get(ACT_USER_URI)
                .then();
    }
}

