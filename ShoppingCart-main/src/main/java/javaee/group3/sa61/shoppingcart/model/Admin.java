package javaee.group3.sa61.shoppingcart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * Admin entity class that extends the User class.
 *
 * @author Group 3
 * @date 2025/10/1
 *
 **/

@Entity
@DiscriminatorValue("Admin")
public class Admin extends User{
	
}
