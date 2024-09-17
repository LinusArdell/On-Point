package com.test.onpoint.Class;

public class UserClass {
    private String username, userPicture;
    private String email;

    public UserClass() {

    }

    public UserClass(String username, String email, String userPicture) {
        this.username = username;
        this.email = email;
        this.userPicture = userPicture;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    public String getUserPicture() {
        return userPicture;
    }

}
