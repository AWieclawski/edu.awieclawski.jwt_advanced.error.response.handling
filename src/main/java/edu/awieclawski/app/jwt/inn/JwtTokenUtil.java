package edu.awieclawski.app.jwt.inn;


import edu.awieclawski.app.config.AppDateTimeProvider;
import edu.awieclawski.app.jwt.dto.JwtResponse;
import edu.awieclawski.app.util.DateTimeUtils;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

@Slf4j
@Component(value = JwtTokenUtil.BEAN_NAME)
@RequiredArgsConstructor
class JwtTokenUtil implements Serializable {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.JwtTokenUtil";
    static final Integer DEF_MILLIS_VALIDITY = 300000;
    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.validity}")
    private String validityInMinutes;

    @Value("${jwt.refresh}")
    private String refreshValidityInMinutes;

    private Long validityInMilliseconds;
    private Long refreshValidityMilliseconds;

    private final AppDateTimeProvider appDateTimeProvider;

    @PostConstruct
    protected void init() {
        this.validityInMilliseconds = getTokenValidity(validityInMinutes);
        log.info("JwtTokenUtil initialized with token validity in minutes: [{}] and secret length: [{}] ",
                validityInMinutes, (secretKey != null ? secretKey.length() : 0));
        this.refreshValidityMilliseconds = getTokenValidity(refreshValidityInMinutes);
    }

    //generate token for user
    JwtResponse generateToken(UserDetails userDetails, boolean isRefreshToken) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            claims.put("isAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            claims.put("isUser", true);
        }
        return doGenerateToken(claims, userDetails.getUsername(), isRefreshToken);
    }


    //validate token
    Date validateTokenTo(String authToken) {
        try {
            // ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time
            // before the time this method is invoked.
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            log.debug("Jwt token [{}] valid to: [{}] for: [{}] validation OK", getTokenShort(authToken), claims.getBody().getExpiration(), claims.getBody().getSubject());
            return claims.getBody().getExpiration();
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        List<SimpleGrantedAuthority> roles = null;
        Boolean isAdmin = claims.get("isAdmin", Boolean.class);
        Boolean isUser = claims.get("isUser", Boolean.class);
        if (isAdmin != null && isAdmin) {
            roles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (isUser != null && isUser) {
            roles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return roles;
    }

    boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    String getTokenShort(String token) {
        if (token != null) {
            String sign = token.split("\\.")[2];
            return sign.substring(0, 16);
        }
        return null;
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private JwtResponse doGenerateToken(Map<String, Object> claims, String subject, boolean isRefreshToken) {
        long millisNow = appDateTimeProvider.getCurrentTimestamp().getTime();
        Date dateFrom = new Date(millisNow);
        Date dateTo = isRefreshToken ? new Date(millisNow + refreshValidityMilliseconds) : new Date(millisNow + validityInMilliseconds);
        String rawToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(dateFrom)
                .setExpiration(dateTo)
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
        return JwtResponse.builder().jwtToken(rawToken).tokenValidTo(DateTimeUtils.dateToStringMulti(dateTo)).build();
    }

    private Long getTokenValidity(String input) {
        return input != null ? Long.parseLong(input) * 60 * 1000 : DEF_MILLIS_VALIDITY;
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

}
