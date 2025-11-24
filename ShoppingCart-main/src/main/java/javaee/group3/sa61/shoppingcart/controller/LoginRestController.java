package javaee.group3.sa61.shoppingcart.controller;

import java.util.List;
import java.util.Map;

import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.LoginUser;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.CartItemImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import javaee.group3.sa61.shoppingcart.interfacemethods.UserInterface;
import javaee.group3.sa61.shoppingcart.model.User;
import javaee.group3.sa61.shoppingcart.service.UserImplementation;

/**
 *
 * LoginController handles all login related web requests
 *
 * @author Yue-Sheng
 * @date 2025/10/2
 *
 **/
@CrossOrigin
@RestController
@RequestMapping("/api")
public class LoginRestController {

    @Autowired
    private UserInterface uservice;
    private CartInterface cartservice;
    private CartItemInterface cartItemService;

    @Autowired
    public void setUserService(UserImplementation userviceImpl) {
        this.uservice = userviceImpl;
    }

    @Autowired
    public void setCartService(CartImplementation cartserviceImpl) {
        this.cartservice = cartserviceImpl;
    }

    @Autowired
    public void setCartItemService(CartItemImplementation cartItemServiceImpl) {
        this.cartItemService = cartItemServiceImpl;
    }

    /**
     *
     * Main Login page - starting point for login form input and passing info to POST mapping
     *
     * @author Yue-Sheng
     * @date 2025/10/2
     *
     **/
    @GetMapping("/login")
    public ResponseEntity<?> loginPage(HttpSession sessionObj, Model model) {
        String role = (String) sessionObj.getAttribute("role");
        System.out.println(role);

        if (role != null) {
            Map<String, Object> responseBody = Map.of(
                    "role", role
            );

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

        Map<String, Object> responseBody = Map.of("error", "Not logged in");
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    /**
     *
     * Main Authentication page to validate if username/password matches database and saving session info.
     *
     * @author Yue-Sheng, Maha
     * @date 2025/10/2
     *
     **/
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUser loginUser, HttpSession sessionObj) {

        List<User> userAuth = uservice.findUsersByUsername(loginUser.getUsername());
        if (!userAuth.isEmpty() && userAuth.get(0).getPassword().equals(loginUser.getPassword())) {
            User authenticatedUser = userAuth.get(0);
            sessionObj.setAttribute("id", authenticatedUser.getId());
            sessionObj.setAttribute("name", authenticatedUser.getName());
            sessionObj.setAttribute("role", authenticatedUser.getRole());

            if (authenticatedUser.getRole().equals("customer")) {
                Cart cartAuth = cartservice.getCartByCustomerId(authenticatedUser.getId());
                sessionObj.setAttribute("cart_id", cartAuth.getId());
                sessionObj.setAttribute("count", cartItemService.countByCartId(cartAuth.getId()));
            }

            Map<String, Object> responseBody = Map.of(
                    "role", authenticatedUser.getRole()
            );

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

        Map<String, Object> responseBody = Map.of("error", "Invalid username or password");
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

}
