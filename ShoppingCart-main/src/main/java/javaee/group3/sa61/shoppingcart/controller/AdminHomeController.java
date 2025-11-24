package javaee.group3.sa61.shoppingcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple landing page for administrators to navigate between management views.
 *
 * @author Huang Jun
 * @date 2025/10/07
 */
@Controller
public class AdminHomeController {

    @GetMapping("/admin")
    public String adminHome() {
        return "admin-home";
    }
}
