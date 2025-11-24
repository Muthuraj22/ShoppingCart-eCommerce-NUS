package javaee.group3.sa61.shoppingcart.model;

public class LoginUser extends User {
    public LoginUser() {
        super();
    }

    public LoginUser(String username, String password) {
        super("", username, password, "", "", "");
    }
}