package pti.sb_squash_mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
		.authorizeHttpRequests((requests) -> requests
			.requestMatchers("/").permitAll()
			.requestMatchers("/filter/**", "/player/changepwd").hasAnyAuthority("ADMIN", "PLAYER")
			.requestMatchers("/admin/**", "/admin").hasAuthority("ADMIN")
			.anyRequest().authenticated()
		)
		.formLogin((form) -> form
			.permitAll()
			.successHandler(new AuthSuccessHandler())
		)
		.logout((logout) -> logout
			.permitAll()
		)
		.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
		.csrf((csrf) -> csrf
				.disable())
		.exceptionHandling((exception) -> exception
				.accessDeniedPage("/?unauthorized"));

    	
    	return http.build();
    }
	
}
