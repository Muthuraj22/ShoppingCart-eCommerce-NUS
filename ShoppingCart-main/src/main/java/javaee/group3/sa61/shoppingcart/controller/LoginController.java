package javaee.group3.sa61.shoppingcart.controller;

import java.util.List;

import jakarta.validation.Valid;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.model.LoginUser;
import javaee.group3.sa61.shoppingcart.service.CartItemImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.UserInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.User;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.UserImplementation;

/**
*
* LoginController handles all login related web requests
*
* @author Yue-Sheng
* @date 2025/10/2
*
**/
@Controller
public class LoginController {

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
    @GetMapping({ "/login", "/" })
    public String loginPage(HttpSession sessionObj, Model model) {
        String role = (String) sessionObj.getAttribute("role");
        if (role != null) {
            if (role.equals("admin")) {
                return "redirect:/admin";
            } else if (role.equals("customer")) {
                return "redirect:/products";
            }
        }
        if (!model.containsAttribute("loginUser")) {
            model.addAttribute("loginUser", new LoginUser());
        }
        return "login";
    }

    /**
     *
     * Main Authentication page to validate if username/password matches database and saving session info.
     *
     * @author Yue-Sheng
     * @date 2025/10/2
     *
     **/
    @PostMapping("/login")
    public String authenticate(@Valid @ModelAttribute LoginUser loginUser,
                               BindingResult bindingResult,
                               HttpSession sessionObj) {

        if (bindingResult.hasErrors()) {
            return "/login";
        }

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
                return "redirect:/products";
            } else if (authenticatedUser.getRole().equals("admin")) {
                return "redirect:/admin";
            }
        }
        return "/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession sessionObj) {
        sessionObj.invalidate();
        return "redirect:http://localhost:5173/login";
    }
}
