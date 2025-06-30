package com.springboot.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.HtmlUtils;

public class SecurityUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    public static String getClientIP(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potential XSS vectors
        String sanitized = HtmlUtils.htmlEscape(input.trim());
        
        // Remove SQL injection vectors
        sanitized = sanitized.replaceAll("['\"\\\\;]", "");
        
        // Remove script tags and javascript
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        
        // Remove potentially dangerous characters but keep international characters
        sanitized = sanitized.replaceAll("[<>\"'%;&\\(\\)\\+]", "");
        
        return sanitized.toLowerCase();
    }

    public static boolean isValidLanguageCode(String language) {
        if (language == null || language.length() != 2) {
            return false;
        }
        
        // Check if contains only lowercase letters
        return language.matches("^[a-z]{2}$");
    }
}