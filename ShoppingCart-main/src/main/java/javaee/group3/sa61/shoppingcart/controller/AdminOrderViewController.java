package javaee.group3.sa61.shoppingcart.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * AdminOrderViewController handles rendering admin order views by calling internal REST endpoints.
 *
 * @author Huang Jun
 * @date 2025/10/06
 */

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderViewController {

	private final RestTemplate restTemplate;

	/**
	 * Create a controller that renders admin order pages by delegating to REST endpoints.
	 *
	 * @param restTemplate client used for server-to-server calls inside the app
     * @author Huang Jun
	 */
	@Autowired
	public AdminOrderViewController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Render the order list page for admins.
	 *
	 * @param model Spring MVC model
	 * @param request current HTTP request
	 * @return view name for Thymeleaf
     * @author Huang Jun
	 */
	@GetMapping
	public String listOrders(Model model, HttpServletRequest request) {
		String url = buildApiUrl(request, "/admin/api/orders");
		List<Map<String, Object>> orders = Collections.emptyList();
		try {
			ResponseEntity<List> response = restTemplate.exchange(
					url, HttpMethod.GET, createRequestEntity(request), List.class);
			List<?> body = response.getBody();
			if(body != null) {
				orders = convertToMapList(body);
			}
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load orders", ex);
		}
		model.addAttribute("orders", orders);
		return "admin-orders";
	}

	/**
	 * Render a single order with summary and line items.
	 *
	 * @param orderId identifier of the order
	 * @param model Spring MVC model
	 * @param request current HTTP request
	 * @return view name for Thymeleaf
     * @author Huang Jun
	 */
	@GetMapping("/{orderId}")
	public String viewOrder(@PathVariable int orderId, Model model, HttpServletRequest request) {
		String url = buildApiUrl(request, "/admin/api/orders/" + orderId);
		try {
			ResponseEntity<Map> response = restTemplate.exchange(
					url, HttpMethod.GET, createRequestEntity(request), Map.class);
			@SuppressWarnings("unchecked")
			Map<String, Object> payload = response.getBody();
			if(payload == null) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing order details");
			}
			Object orderDetailObj = payload.get("orderDetail");
			Object customerSummaryObj = payload.get("customerSummary");
			if(!(orderDetailObj instanceof Map) || !(customerSummaryObj instanceof Map)) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Malformed response from admin API");
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> orderDetail = (Map<String, Object>) orderDetailObj;
			@SuppressWarnings("unchecked")
			Map<String, Object> customerSummary = (Map<String, Object>) customerSummaryObj;
			model.addAttribute("orderDetail", orderDetail);
			model.addAttribute("customerSummary", customerSummary);
		} catch (RestClientResponseException ex) {
			if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
			}
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load order", ex);
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load order", ex);
		}
		return "admin-order-detail";
	}

    /**
     * Convert a list of unknown objects to a list of maps with string keys and object values.
     *
     * @param body list of unknown objects
     * @return list of maps with string keys and object values
     * @author Huang Jun
     */
	private List<Map<String, Object>> convertToMapList(List<?> body) {
		List<Map<String, Object>> result = new ArrayList<>();
		for(Object value : body) {
			if(value instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> mapValue = (Map<String, Object>) value;
				result.add(mapValue);
			}
		}
		return result;
	}

    /**
     * Create an HTTP entity with JSON accept header and session cookie if available.
     *
     * @param request current HTTP request
     * @return HTTP entity with headers set
     * @author Huang Jun
     */
	private HttpEntity<Void> createRequestEntity(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if(request != null) {
			String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
			if(cookieHeader != null && !cookieHeader.isBlank()) {
				// Preserve the full cookie string so the internal call shares the browser session.
				headers.add(HttpHeaders.COOKIE, cookieHeader);
			} else {
				var session = request.getSession(false);
				if(session != null) {
					headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + session.getId());
				}
			}
		}
		return new HttpEntity<>(headers);
	}

    /**
     * Build a full URL for internal API calls based on the current request.
     *
     * @param request current HTTP request
     * @param path API path to append to the base URL
     * @return full URL as a string
     * @author Huang Jun
     */
	private String buildApiUrl(HttpServletRequest request, String path) {
		StringBuilder builder = new StringBuilder();
		builder.append(request.getScheme())
				.append("://")
				.append(request.getServerName());
		int port = request.getServerPort();
		boolean isHttp = "http".equalsIgnoreCase(request.getScheme()) && port != 80;
		boolean isHttps = "https".equalsIgnoreCase(request.getScheme()) && port != 443;
		if(isHttp || isHttps) {
			builder.append(":").append(port);
		}
		builder.append(request.getContextPath()).append(path);
		return builder.toString();
	}
}
