package javaee.group3.sa61.shoppingcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartInterface cartservice;

    @Autowired
    public void setCartService(CartImplementation cartserviceImpl) {
        this.cartservice = cartserviceImpl;
    }

    /**
     * Display the cart page for the logged-in customer retrieved from the session.
     *
     * @param sessionObj Session object to retrieve the logged-in customer's identifier
     * @param model      Spring Model to pass data to the view
     * @return 'cart-display' view template showing the shopping cart
     *
     *  @author Priya, Maha
     *  @date 2025/10/1
     */
    @GetMapping
    public String displayCart(HttpSession sessionObj, Model model) {
        Integer customerId = (Integer) sessionObj.getAttribute("id");
        Cart cart = cartservice.getCartByCustomerId(customerId);
        sessionObj.setAttribute("cart_id", cart.getId());
        model.addAttribute("cart", cart);
        double totalPrice = (cart.getCartItems() == null) ? 0.0 : cartservice.getTotalPrice(cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart-display";
    }


}
