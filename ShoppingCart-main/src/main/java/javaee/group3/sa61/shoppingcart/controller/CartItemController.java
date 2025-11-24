package javaee.group3.sa61.shoppingcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.CartItemInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.ProductInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.CartItem;
import javaee.group3.sa61.shoppingcart.model.CartItemId;
import javaee.group3.sa61.shoppingcart.model.Product;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.CartItemImplementation;
import javaee.group3.sa61.shoppingcart.service.ProductImplementation;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart/items")
public class CartItemController {

    @Autowired
    private CartItemInterface ciservice;
    private ProductInterface pservice;
    private CartInterface cartservice;

    @Autowired
    public void setCartItemService(CartItemImplementation ciserviceImpl) {
        this.ciservice = ciserviceImpl;
    }

    @Autowired
    public void setProductService(ProductImplementation pserviceImpl) {
        this.pservice = pserviceImpl;
    }

    @Autowired
    public void setCartService(CartImplementation cserviceImpl) {
        this.cartservice = cserviceImpl;
    }

    /**
     * Handles adding a product to the cart using session-derived identifiers.
     *
     * @param productId identifier of the product to add
     * @param redirect  optional redirect target (products, cart, details)
     * @param sessionObj HTTP session containing customer and cart information
     * RedirectAttributes survives exactly one redirect and shown as flash attributes
     * @return redirect URL to refresh the appropriate page
     *
     * @author Maha, Priya, Huang Jun
     * @date 2025/10/1
     */
    @PostMapping("/add")
    @Transactional
    public String addCartItem(@RequestParam("productId") int productId,
                              @RequestParam(value = "redirect", required = false) String redirect,
                              HttpSession sessionObj,RedirectAttributes redirectAttributes) {
        Cart cart = resolveCart(sessionObj);
        Product product;
        try {
            product = pservice.findProductById(productId);
        } catch (IllegalArgumentException ex) {
            removeCartItemIfExists(cart, productId);
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        if (product.getStockQty() <= 0) {
            removeCartItemIfExists(cart, productId);
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        CartItem item = ciservice.addCartItem(cart, product);

        if(item==null) {
            redirectAttributes.addFlashAttribute("message", "Not enough items in stock ");
            redirectAttributes.addFlashAttribute("outOfStockId", productId);
            return determineRedirect(redirect,productId);
        }

        ciservice.save(item);
        sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));

        return determineRedirect(redirect, productId);

    }

    /**
     * Handles reducing the quantity of a product in the cart by one.
     *
     * @param productId identifier of the product to reduce
     * @param redirect  optional redirect target (products, cart, details)
     * @param sessionObj HTTP session containing customer and cart information
     * @return redirect URL to refresh the appropriate page
     *
     * @author Maha, Priya, Huang Jun
     * @date 2025/10/1
     */
    @PostMapping("/reduce")
    @Transactional
    public String reduceCartItem(@RequestParam("productId") int productId,
                                 @RequestParam(value = "redirect", required = false) String redirect,
                                 HttpSession sessionObj,
                                 RedirectAttributes redirectAttributes) {
        Cart cart = resolveCart(sessionObj);
        Product product;
        try {
            product = pservice.findProductById(productId);
        } catch (IllegalArgumentException ex) {
            removeCartItemIfExists(cart, productId);
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        if (product.getStockQty() <= 0) {
            removeCartItemIfExists(cart, productId);
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        CartItem item = ciservice.reduceCartItem(cart, product);
        CartItemId cartItemId = new CartItemId(cart.getId(), product.getId());
        if (item == null) {
            CartItem existing = ciservice.getCartItemById(cartItemId);
            if (existing != null) {
                ciservice.delete(existing);
                if (cart.getCartItems() != null) {
                    cart.getCartItems().remove(existing);
                }
            }
        } else {
            ciservice.save(item);
        }

        sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
        return determineRedirect(redirect, productId);
    }

    /**
     * Handles removing a product entirely from the cart.
     *
     * @param productId identifier of the product to remove
     * @param redirect  optional redirect target (products, cart, details)
     * @param sessionObj HTTP session containing customer and cart information
     * @return redirect URL to refresh the appropriate page
     *
     * @author Maha, Priya, Huang Jun
     * @date 2025/10/1
     */
    @PostMapping("/delete")
    @Transactional
    public String deleteCartItem(@RequestParam("productId") int productId,
                                 @RequestParam(value = "redirect", required = false) String redirect,
                                 HttpSession sessionObj,
                                 RedirectAttributes redirectAttributes) {
        Cart cart = resolveCart(sessionObj);
        Product product;
        try {
            product = pservice.findProductById(productId);
        } catch (IllegalArgumentException ex) {
            removeCartItemIfExists(cart, productId);
            redirectAttributes.addFlashAttribute("message", "Product is no longer available.");
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        if (product.getStockQty() <= 0) {
            removeCartItemIfExists(cart, productId);
            redirectAttributes.addFlashAttribute("message", "Product is no longer available.");
            sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
            return determineRedirect(redirect, productId);
        }
        CartItemId cartItemId = new CartItemId(cart.getId(), product.getId());
        if (ciservice.checkCartItemById(cartItemId)) {
            CartItem item = ciservice.getCartItemById(cartItemId);
            ciservice.delete(item);
            if (cart.getCartItems() != null) {
                cart.getCartItems().remove(item);
            }
        }

        sessionObj.setAttribute("count", ciservice.countByCartId(cart.getId()));
        return determineRedirect(redirect, productId);
    }


    /**
     *  Retrive the cart from the customer session
     *
     * @param sessionObj HTTP session containing customer and cart information
     * @return 'cart' view template showing the shopping cart
     *
     * @author Huang Jun
     * @date 2025/10/13
     */

    private Cart resolveCart(HttpSession sessionObj) {
        Integer cartId = (Integer) sessionObj.getAttribute("cart_id");
        Cart cart = null;
        if (cartId != null) {
            cart = cartservice.getCartByCartId(cartId);
        }
        if (cart == null) {
            Integer customerId = (Integer) sessionObj.getAttribute("id");
            if (customerId == null) {
                throw new IllegalStateException("No logged-in customer found in session");
            }
            cart = cartservice.getCartByCustomerId(customerId);
            sessionObj.setAttribute("cart_id", cart.getId());
        }
        return cart;
    }

    /**
     *  Deletes the cartItem from database and Removes the cartItem from the cart
     *
     * @param cart cart from which cartItem to be removed
     * @param productId identifier of the product to be removed
     *
     * @author Maha, Priya, Huang Jun
     * @date 2025/10/1
     */

    private void removeCartItemIfExists(Cart cart, int productId) {
        if (cart == null) {
            return;
        }
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        if (!ciservice.checkCartItemById(cartItemId)) {
            return;
        }
        CartItem existing = ciservice.getCartItemById(cartItemId);
        if (existing != null) {
            ciservice.delete(existing);
        }
        if (cart.getCartItems() != null) {
            cart.getCartItems().remove(existing);
        }
    }

    /**
     * Determines the redirect path
     * @param redirect redirection path
     * @param productId identifier of the product
     * @return redirects to products/ cart/ product-details view based on the redirection path
     *
     * @author Huang Jun
     * @date 2025/10/13
     */

    private String determineRedirect(String redirect, int productId) {
        if ("cart".equalsIgnoreCase(redirect)) {
            return "redirect:/cart";
        }
        if ("details".equalsIgnoreCase(redirect)) {
            return "redirect:/products/" + productId;
        }
        return "redirect:/products";
    }
}
