package pti.sb_squash_mvc.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pti.sb_squash_mvc.model.Player;
import pti.sb_squash_mvc.service.AppService;

@Component
public class FirstTimeLoginFilter extends OncePerRequestFilter {

	@Autowired
	private AppService service;
	
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		if(username != null && username.equals("anonymousUser") == false && request.getRequestURI().equals("/player/changepwd") == false)  {

			Player loggedInPlayer = service.getPlayerByEmailAddress(username);

			if(loggedInPlayer.isActivated() == false) {

				response.sendRedirect("/player/changepwd");
			}
		}
		
		filterChain.doFilter(request, response);

	}
	
	
	

}

	







	
	


