package javaee.group3.sa61.shoppingcart.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * Prehandle function that checks if user is logged in or not and redirects users to correct pages.
     * @author Yue-Sheng
     * @date 2025/10/3
     */

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws IOException {

        String uri = request.getRequestURI();

        HttpSession sessionObj = request.getSession();
        String role = (String) sessionObj.getAttribute("role");
        if (role == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (role.equals("customer")) {
            if (request.getRequestURI().contains("/admin")) {
                response.sendRedirect("/error");
                return false;
            }
        }
        else if (role.equals("admin")) {
            if (!request.getRequestURI().contains("/admin")) {
                response.sendRedirect("/error");
                return false;
            }
        }

        return true;
    }
}
