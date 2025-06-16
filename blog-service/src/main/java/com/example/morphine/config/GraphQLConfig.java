package com.example.morphine.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class to extract HTTP headers in GraphQL resolvers.
 * This allows extracting headers like X-User-Id from the HTTP request context.
 */
@Component
public class GraphQLConfig {

    /**
     * Extracts the X-User-Id header from the current HTTP request context.
     * This method can be called from GraphQL resolvers to get the user ID.
     */
    public static String extractUserIdFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("X-User-Id");
        }
        return null;
    }
}
