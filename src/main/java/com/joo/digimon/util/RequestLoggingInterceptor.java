package com.joo.digimon.util;

import com.joo.digimon.security.provider.JwtProvider;
import com.joo.digimon.user.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private final JwtProvider jwtProvider;
    private static final String USER_ATTRIBUTE = "loggedInUser";
    private static final String JWT_TOKEN_COOKIE_NAME = "JWT_TOKEN";
    private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JWT_TOKEN_COOKIE_NAME)) {
                    String jwtToken = cookie.getValue();
                    User user = jwtProvider.getUserFromToken(jwtToken);
                    request.setAttribute(USER_ATTRIBUTE, user);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        User user = (User) request.getAttribute(USER_ATTRIBUTE);
        boolean isAuthenticated = user != null;
        Integer userId = user != null ? user.getId() : null;
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        ReadableUserAgent userAgent = parser.parse(request.getHeader("User-Agent"));

    }



}
