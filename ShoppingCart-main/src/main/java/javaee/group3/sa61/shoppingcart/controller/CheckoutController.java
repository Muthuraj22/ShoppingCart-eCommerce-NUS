package javaee.group3.sa61.shoppingcart.controller;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CheckoutInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.Order;

/**
 * CheckoutController orchestrates rendering the checkout page and completing purchases.
 *
 * @author Sujitha
 * @date 2025/10/8
 */
@Controller
@RequestMapping("/orders")
public class CheckoutController {

    private final CartInterface cartService;
    private final CheckoutInterface checkoutService;

    public CheckoutController(CartInterface cartService,
                              CheckoutInterface checkoutService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }
    /**
     * Render the checkout view populated with cart details or empty cart notice.
     *@param sessionObj HTTP session containing cart and cartItem information
     * @return 'checkout' view template showing the checkout page
     * @author Sujitha
     * @date 2025/10/8
     */

    @GetMapping("/checkout")
    public String showCheckout(HttpSession sessionObj, Model model) {
        Integer cartId = (Integer) sessionObj.getAttribute("cart_id");

        Cart cart = null;
        if (cartId != null) {
            cart = cartService.getCartByCartId(cartId);
        }
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            model.addAttribute("cartEmpty", true);
        } else {
            model.addAttribute("cart", cart);
            model.addAttribute("orderTotal", cartService.getTotalPrice(cart));
        }
        model.addAttribute("cartId", cartId);
        return "checkout";
    }
    /**
     * Complete the checkout workflow and render success or error messages.
     *@param sessionObj HTTP session containing cart and customer information
     * @return 'checkout-success' view template showing the checkout success page
     * @author Sujitha
     * @date 2025/10/8
     */

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                  @RequestParam("cartId") int cartIdParam,
                                  HttpSession sessionObj,
                                  Model model) {
        Integer sessionCartId = (Integer) sessionObj.getAttribute("cart_id");
        Integer customerId = (Integer) sessionObj.getAttribute("id");

        if (sessionCartId == null || customerId == null) {
            model.addAttribute("checkoutError", true);
            return showCheckout(sessionObj, model);
        }

        if (!Objects.equals(sessionCartId, cartIdParam)) {
            sessionCartId = cartIdParam;
        }

        Cart cart = cartService.getCartByCartId(sessionCartId);
        if (cart == null || cart.getCustomer() == null
                || cart.getCustomer().getId() != customerId.intValue()
                || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            model.addAttribute("checkoutError", true);
            return showCheckout(sessionObj, model);
        }

        sessionObj.setAttribute("cart_id", cart.getId());
        Order order = checkoutService.processCheckout(sessionCartId, paymentMethod);
        if (order == null) {
            model.addAttribute("checkoutError", true);
            return showCheckout(sessionObj, model);
        }

        sessionObj.setAttribute("count", 0);
        model.addAttribute("order", order);
        return "checkout-success";
    }
}
