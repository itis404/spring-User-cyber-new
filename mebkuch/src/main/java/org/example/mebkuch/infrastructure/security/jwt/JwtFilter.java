package org.example.mebkuch.infrastructure.security.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends GenericFilterBean {

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doInternalFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doInternalFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = findJwtFromCookies(httpServletRequest);

        if (token != null && jwtService.validateToken(token)) {

            String email = jwtService.extractEmail(token);
            List<GrantedAuthority> roles = jwtService.extractRole(token);

            log.info("ROLES:   " + roles.stream().map(auth -> auth.getAuthority()).toList());

            Authentication auth = new UsernamePasswordAuthenticationToken(email, null, roles);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String findJwtFromCookies(HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies == null) return null;

        for (Cookie cookie: cookies){
            if (cookie.getName().equals("json-web-token")){
                return cookie.getValue();
            }
        }

        return null;
    }

}
