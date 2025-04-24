package com.sarveshsawant.webdev.util;

import com.sarveshsawant.webdev.accountauth.service.UserService;
import com.sarveshsawant.webdev.dbhealthcheck.service.DatabaseHealthCheckService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class MethodFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MethodFilter.class);

    DatabaseHealthCheckService databaseHealthCheckService;
    UserService userService;

    @Autowired
    public MethodFilter(DatabaseHealthCheckService databaseHealthCheckService, UserService userService) {
        this.databaseHealthCheckService = databaseHealthCheckService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        logger.info("requestUri " + requestUri);
        if(requestUri.equals("/healthz")){
            logger.info("healthz endpoint detected");
            String method = request.getMethod();
            //Allow only get requests
            if (!"GET".equals(method)) {
                logger.error("Only GET method is allowed ", method);
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }
        }
        if(requestUri.equals("/v1/verify")){
            logger.info("verify endpoint detected");
            String method = request.getMethod();
            //Allow only get requests
            if (!"GET".equals(method)) {
                logger.error("Only GET method is allowed ", method);
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }
        }
        if (requestUri.matches("/v1/user")) {
            logger.info("User endpoint detected");
            if (databaseHealthCheckService.isDatabaseHealthy()){
                logger.info("Database is healthy");
            }else {
                logger.error("Database is not healthy");
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }
            String method = request.getMethod();
            if (!"POST".equals(method)) {
                logger.error("Only POST method is allowed", method);
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }
        }

        if (requestUri.matches("/v1/user/self")) {
            logger.info("Authenticated User endpoint detected");
            if (databaseHealthCheckService.isDatabaseHealthy()) {
                logger.info("Database is healthy");
            } else {
                logger.error("Database is not healthy");
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }

            String method = request.getMethod();

            if (!("GET".equals(method) || "PUT".equals(method))) {
                logger.error("Only GET and PUT methods are allowed for user endpoint", method);
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();

                // Check if user is verified
                if (!userService.isUserAlreadyVerified(username)) {
                    logger.error("User is not verified");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    return;
                }
            }

        }

        if (requestUri.matches("/v1/user/self/pic")) {
            logger.info("Authenticated User endpoint detected");
            if (databaseHealthCheckService.isDatabaseHealthy()){
                logger.info("Database is healthy");
            }else {
                logger.error("Database is not healthy");
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }

            String method = request.getMethod();

            if (!("GET".equals(method) || "POST".equals(method) || "DELETE".equals(method))) {
                logger.error("Only GET POST DELETE methods are allowed for user endpoint", method);
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();

                // Check if user is verified
                if (!userService.isUserAlreadyVerified(username)) {
                    logger.error("User is not verified");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
