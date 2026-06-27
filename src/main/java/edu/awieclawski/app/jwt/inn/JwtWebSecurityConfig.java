package edu.awieclawski.app.jwt.inn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * https://www.baeldung.com/spring-security-custom-access-denied-page
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@DependsOn({JwtRequestFilter.BEAN_NAME,
        JwtAuthenticationEntryPoint.BEAN_NAME,
        JwtUserDetailsService.BEAN_NAME,
        JwtPasswordEncoder.BEAN_NAME})
class JwtWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final static String EXT_WILD_CARD = "/**";

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtPasswordEncoder passwordEncoder;
    private final JwtRestApiAccessDeniedHandler restApiAccessDeniedHandler;

    @Value("${jwt.api.open.hello}")
    private String jwtApiHello;

    @Value("${jwt.api.open.login}")
    private String jwtApiLogIn;

    @Value("${jwt.api.open.register}")
    private String jwtApiRegister;

    @Value("${jwt.api.safe.user}")
    private String jwtApiUser;

    @Value("${jwt.api.safe.admin}")
    private String jwtApiAdmin;

    @Value("${jwt.api.safe.refresh-token}")
    private String jwtApiRefreshToken;

    @Value("${jwt.api.safe.errors}")
    private String jwtApiErrors;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder.getEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                .authorizeRequests()
                // requests need to be authenticated
                .antMatchers(jwtApiAdmin + EXT_WILD_CARD, jwtApiErrors).hasRole("ADMIN")
                .antMatchers(jwtApiUser + EXT_WILD_CARD, jwtApiRefreshToken).hasAnyRole("USER", "ADMIN")
                // dont authenticate this particular request
                .antMatchers(jwtApiLogIn, jwtApiRegister, jwtApiHello).permitAll().anyRequest().authenticated();

        httpSecurity.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        // The AccessDeniedHandler in Spring Boot is used to control
        // what happens when a user tries to access a resource they do not have permission for.
        httpSecurity.exceptionHandling().accessDeniedHandler(restApiAccessDeniedHandler);

        // make sure we use stateless session; session won't be used to
        // store user's state.
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
