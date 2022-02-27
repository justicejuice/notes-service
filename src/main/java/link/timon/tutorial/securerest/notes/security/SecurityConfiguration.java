package link.timon.tutorial.securerest.notes.security;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import link.timon.tutorial.securerest.notes.common.EntityNotFoundException;
import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Security configuration to use JWT in a Stateless environment.
 *
 * @author Timon
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final JwtFilter jwtFilter;

    public SecurityConfiguration(UserRepository userRepository, JwtFilter jwtFilter) {
        super();
        this.userRepository = userRepository;
        this.jwtFilter = jwtFilter;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = enableCors(http);
        http = enableStateless(http);
        http = addExceptionHandling(http);

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, RestConstants.API_V1 + "/users/register").permitAll()
                .antMatchers(HttpMethod.POST, RestConstants.API_V1 + "/users/login").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService((email) -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", email))));
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    private HttpSecurity addExceptionHandling(HttpSecurity http) throws Exception {
        return http.exceptionHandling().authenticationEntryPoint((req, res, ex) -> {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }).and();
    }

    private HttpSecurity enableStateless(HttpSecurity http) throws Exception {
        return http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
    }

    private HttpSecurity enableCors(HttpSecurity http) throws Exception {
        return http.cors().and().csrf().disable();
    }

}
