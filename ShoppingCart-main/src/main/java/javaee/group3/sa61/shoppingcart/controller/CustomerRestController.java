package javaee.group3.sa61.shoppingcart.controller;

import javaee.group3.sa61.shoppingcart.interfacemethods.CartInterface;
import javaee.group3.sa61.shoppingcart.interfacemethods.UserInterface;
import javaee.group3.sa61.shoppingcart.model.Cart;
import javaee.group3.sa61.shoppingcart.model.Customer;
import javaee.group3.sa61.shoppingcart.service.CartImplementation;
import javaee.group3.sa61.shoppingcart.service.UserImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {

    @Autowired
    private UserInterface uservice;
    private CartInterface cartservice;

    @Autowired
    public void setUserService(UserImplementation userviceImpl) {
        this.uservice = userviceImpl;
    }

    @Autowired
    public void setCartService(CartImplementation cartserviceImpl) {
        this.cartservice = cartserviceImpl;
    }

    /**
     * Create customer from POST request sent from React.
     *
     * @param customer     Customer object sent from React
     * @return Response HTTP status and message if user exists.
     * @author Yue-Sheng, Maha
     * @date 2025/10/10
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer) {

        if(uservice.findUsersByUsername(customer.getUsername()).isEmpty()){
            uservice.saveCustomer(customer);
            Cart cartToSave = new Cart();
            cartToSave.setCustomer(customer);
            cartservice.saveCart(cartToSave);

            Map<String, Object> responseBody = Map.of(
                    "message","Customer Account created successfully"
            );

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        Map<String, Object> responseBody = Map.of("error", "Username already exists");
        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }
}
