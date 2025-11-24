package javaee.group3.sa61.shoppingcart.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CartInterceptor implements HandlerInterceptor {


    /**
     * Post handler that injects updated cart item count from sessionoObject before returning back to frontend view.
     *
     * @author Yue-Sheng
     * @date 2025/10/13
     */

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        HttpSession sessionObj = request.getSession();
        String role = (String) sessionObj.getAttribute("role");

        if (role.equals("customer") && !request.getRequestURI().contains("/admin")) {
            int cartItemCount = (int) sessionObj.getAttribute("count");
            modelAndView.addObject("count", cartItemCount);
        }
    }
}
