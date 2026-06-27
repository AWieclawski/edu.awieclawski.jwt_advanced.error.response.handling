package edu.awieclawski.app.jwt.inn;

import edu.awieclawski.app.config.AppDateTimeProvider;
import edu.awieclawski.app.cache.AuthorisedUserDto;
import edu.awieclawski.app.cache.UserCacheService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
@Component(value = JwtRequestFilter.BEAN_NAME)
@DependsOn(value = {JwtTokenUtil.BEAN_NAME, UserCacheService.BEAN_NAME})
class JwtRequestFilter extends OncePerRequestFilter {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtRequestFilter";
    static final String BEARER_NAME = "Bearer ";
    static final String REQUEST_EXCEPTION_ATTRIBUTE = "exception";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserCacheService userCacheService;
    private final AppDateTimeProvider appDateTimeProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String jwtToken = "";

        // TODO
        try {
            jwtToken = extractJwtFromRequest(request);
            if (StringUtils.hasText(jwtToken)) {
                //&& jwtTokenUtil.validateToken(jwtToken)) {
                Date dateValidTo = jwtTokenUtil.validateTokenTo(jwtToken);
                // Authenticate and create authentication instance
                UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(jwtToken), "",
                        jwtTokenUtil.getRolesFromToken(jwtToken));
                // if token is valid configure Spring Security to manually set
                // authentication
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                // Store authentication token for application to use
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                updateUserCacheRepository(userDetails, dateValidTo, jwtToken);
            } else {
                logger.debug("Any Token in Request not found. Security context not set.");
            }

        } catch (IllegalArgumentException e) {
            logger.info("Unable to get JWT Token: " + e.getMessage());
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Token is invalid");
            request.setAttribute(REQUEST_EXCEPTION_ATTRIBUTE, illegalArgumentException);
            throw illegalArgumentException;
        } catch (ExpiredJwtException e) {
            logger.info("JWT Token expired: " + jwtTokenUtil.getTokenShort(jwtToken));
            ExpiredJwtException expiredJwtException = new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token expired!");
            request.setAttribute(REQUEST_EXCEPTION_ATTRIBUTE, expiredJwtException);
            throw expiredJwtException;
        } catch (BadCredentialsException e) {
            logger.info("JWT Credentials error: " + e.getMessage());
            BadCredentialsException badCredentials = new BadCredentialsException("Bad credentials!");
            request.setAttribute(REQUEST_EXCEPTION_ATTRIBUTE, badCredentials);
            throw badCredentials;
        }

        chain.doFilter(request, response);
    }

    // JWT Token is in the form "Bearer token". Remove Bearer word and get
    // only the Token
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(BEARER_NAME.length());
        } else if (bearerToken != null) {
            logger.warn("Authorization Token [{}] does not begin with Bearer String: " + jwtTokenUtil.getTokenShort(bearerToken));
        }
        return null;
    }

    private void updateUserCacheRepository(UserDetails userDetails, Date dateValidTo, String token) {
        AuthorisedUserDto authorisedUserDto = AuthorisedUserDto.builder()
                .userLogin(userDetails.getUsername())
                .createdAt(appDateTimeProvider.getCurrentDate())
                .tokenValidTo(dateValidTo)
                .tokenShort(jwtTokenUtil.getTokenShort(token))
                .build();
        userCacheService.insertUserCacheAsync(authorisedUserDto);
    }

}
