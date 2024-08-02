package com.luckvicky.blur.global.interceptor;

import static com.luckvicky.blur.global.constant.StringFormat.LEAGUE_TYPE_MODEL;

import com.luckvicky.blur.global.execption.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class LeagueAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new UnauthorizedAccessException();
        }

        Map<String, String[]> parameters = request.getParameterMap();

        if (!parameters.containsKey("leagueType")) {
            return true;
        }

        // 리그 타입을 받고
        String leagueType = request.getParameter("leagueType");

        switch (leagueType) {

            // 모델일 경우 AUTH_USER 권한이 있는지 확인
            case LEAGUE_TYPE_MODEL -> {

                if (isAuthUser(authentication)) {
                    return true;
                }

                throw new UnauthorizedAccessException();

            }

        }

        return true;

    }

    private boolean isAuthUser(Authentication authentication) {

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_AUTH_USER"));

    }

}
