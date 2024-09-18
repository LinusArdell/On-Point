package com.test.onpoint.Class;

public class UserClass {
    private String username, userPicture, role;
    private String email;

    public UserClass() {

    }

    public UserClass(String username, String email, String userPicture, String role) {
        this.username = username;
        this.email = email;
        this.userPicture = userPicture;
        this.role = role;
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

    public String getRole() {
        return role;
    }
}
