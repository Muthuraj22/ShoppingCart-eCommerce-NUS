package javaee.group3.sa61.shoppingcart.interfacemethods;

import javaee.group3.sa61.shoppingcart.model.Order;

public interface CheckoutInterface {

    /**
     * Complete the checkout process for the provided cart using the supplied payment method.
     *
     * @param cartId identifier of the cart to convert into an order
     * @param paymentMethod customer's selected payment option
     * @return newly persisted order or null when checkout cannot be completed
     */
    Order processCheckout(int cartId, String paymentMethod);
}
