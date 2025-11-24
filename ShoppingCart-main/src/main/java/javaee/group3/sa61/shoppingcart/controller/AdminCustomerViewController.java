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
 * AdminCustomerViewController handles rendering admin customer views by calling internal REST endpoints.
 *
 * @author Huang Jun
 * @date 2025/10/06
 */
@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerViewController {

	private final RestTemplate restTemplate;

	/**
	 * Create a controller that renders admin customer pages by calling the REST endpoints.
	 *
	 * @param restTemplate client used to call the in-application admin API
     * @author Huang Jun
	 */
	@Autowired
	public AdminCustomerViewController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Render the admin customer list page.
	 *
	 * @param model Spring MVC model
	 * @param request current HTTP request
	 * @return view name for Thymeleaf
     * @author Huang Jun
	 */
	@GetMapping
	public String listCustomers(Model model, HttpServletRequest request) {
		String url = buildApiUrl(request, "/admin/api/customers");
		List<Map<String, Object>> customers = Collections.emptyList();
		try {
			ResponseEntity<List> response = restTemplate.exchange(
					url, HttpMethod.GET, createRequestEntity(request), List.class);
			List<?> body = response.getBody();
			if(body != null) {
				customers = convertToMapList(body);
			}
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load customers", ex);
		}
		model.addAttribute("customers", customers);
		return "admin-customers";
	}

	/**
	 * Render one customer profile with cart snapshot and order history.
	 *
	 * @param customerId identifier of the customer
	 * @param model Spring MVC model
	 * @param request current HTTP request
	 * @return view name for Thymeleaf
     * @author Huang Jun
	 */
	@GetMapping("/{customerId}")
	public String viewCustomer(@PathVariable int customerId, Model model, HttpServletRequest request) {
		String url = buildApiUrl(request, "/admin/api/customers/" + customerId);
		try {
			ResponseEntity<Map> response = restTemplate.exchange(
					url, HttpMethod.GET, createRequestEntity(request), Map.class);
			@SuppressWarnings("unchecked")
			Map<String, Object> body = response.getBody();
			if(body == null) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing customer details");
			}
			model.addAttribute("customerDetail", body);
		} catch (RestClientResponseException ex) {
			if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
			}
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load customer details", ex);
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load customer details", ex);
		}
		return "admin-customer-detail";
	}

    /**
     * Convert a list of unknown objects to a list of maps.
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
     * @return HTTP entity with headers
     * @author Huang Jun
     */
	private HttpEntity<Void> createRequestEntity(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if(request != null) {
			String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
			if(cookieHeader != null && !cookieHeader.isBlank()) {
				// Forward the complete cookie header so Spring Security reuses the same session.
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
     * Build a complete URL for the admin API based on the current request.
     *
     * @param request current HTTP request
     * @param path relative path to append to the base URL
     * @return complete URL as string
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
