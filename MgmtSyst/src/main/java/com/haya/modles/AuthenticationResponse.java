package com.haya.modles;

public class AuthenticationResponse {

    private String token;
    private String message;
    private int userId;
    private String userFirstName;
    private String userLastName;

    public AuthenticationResponse(String token, String message, int userId, String userFirstName, String userLastName) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }
}
