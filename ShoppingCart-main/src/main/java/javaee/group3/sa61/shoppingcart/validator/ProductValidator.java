package javaee.group3.sa61.shoppingcart.validator;

import java.util.Locale;
import java.util.Set;

import javaee.group3.sa61.shoppingcart.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Product entities.
 * Currently, it only validates the imagePath field to ensure it is either a valid URL or a relative path
 * ending with a supported image file extension (jpg, png, gif, etc).
 *
 * @author Huang Jun
 */
@Component
public class ProductValidator implements Validator {
    private static final Set<String> SUPPORTED_IMAGE_TYPES =
            Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg");

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof Product)) {
            return;
        }
        Product product = (Product) target;
        validateImagePath(product, errors);
    }

    /**
     * Validate the imagePath field of the Product.
     *
     * @param product the Product entity being validated
     * @param errors  the Errors object to register validation errors
     * @author Huang Jun
     */
    private void validateImagePath(Product product, Errors errors) {
        String rawPath = product.getImagePath();
        if (rawPath == null) {
            return;
        }

        String trimmed = rawPath.trim();
        if (trimmed.isEmpty()) {
            product.setImagePath(null);
            return;
        }

        if (trimmed.chars().anyMatch(Character::isWhitespace)) {
            errors.rejectValue("imagePath", "product.imagePath.whitespace",
                    "Image path must not contain whitespace characters.");
            return;
        }

        if (!looksLikeUrlOrPath(trimmed) || !hasSupportedExtension(trimmed)) {
            errors.rejectValue("imagePath", "product.imagePath.invalid",
                    "Use http(s) URL or relative path ending with an image file (jpg, png, gif, etc).");
            return;
        }

        product.setImagePath(trimmed);
    }

    /**
     * Check if the value looks like a URL or a relative path
     *
     * @author Huang Jun
     */
    private boolean looksLikeUrlOrPath(String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return true;
        }
        return !value.contains("://") && !value.startsWith("//");
    }

    /**
     * Check if the path or URL has a supported image file extension
     *
     * @author Huang Jun
     */
    private boolean hasSupportedExtension(String value) {
        String path = value.split("[?#]", 2)[0];
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == path.length() - 1) {
            return false;
        }
        String ext = path.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return SUPPORTED_IMAGE_TYPES.contains(ext);
    }
}
