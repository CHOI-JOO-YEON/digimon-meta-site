package com.joo.digimon.util;

import com.joo.digimon.request_log.entity.RequestLog;
import com.joo.digimon.request_log.repository.RequestLogRepository;
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
    private final RequestLogRepository requestLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user =null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JWT_TOKEN_COOKIE_NAME)) {
                    String jwtToken = cookie.getValue();
                    user = jwtProvider.getUserFromToken(jwtToken);
                    break;
                }
            }
        }
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        ReadableUserAgent userAgent = parser.parse(request.getHeader("User-Agent"));

        requestLogRepository.save(RequestLog.builder()
                .user(user)
                .requestMethod(requestMethod)
                .requestPath(requestPath)
                .type(userAgent.getTypeName())
                .deviceCategory(userAgent.getDeviceCategory().getName())
                .family(userAgent.getFamily().getName())
                .executionTime(executionTime)
                .build());

    }



}
