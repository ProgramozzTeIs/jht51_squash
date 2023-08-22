package pti.sb_squash_mvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pti.sb_squash_mvc.dto.MatchDto;
import pti.sb_squash_mvc.dto.PasswordChangeResponse;
import pti.sb_squash_mvc.model.Match;
import pti.sb_squash_mvc.model.Place;
import pti.sb_squash_mvc.model.Player;
import pti.sb_squash_mvc.service.AppService;


@Controller
public class AppController {
	
	private final AppService service;
	
	@Autowired
	public AppController(AppService service) {
		
		this.service = service;
	}
	
	@GetMapping("/")
	public String showAllMatches(
				Model model
			) {
		
		List<Player> playersList = service.getAllPlayers();
		List<Place> placesList = service.getAllPlaces();
		List<Match> matchesList = service.getAllMatches();
		
		model.addAttribute("playersList", playersList);
		model.addAttribute("placesList", placesList);
		model.addAttribute("matchesList", matchesList);

		return "index";
	}
	
	@GetMapping("/filter/player")
	public String showFilteredMatchesByPlayer(
				@RequestParam(name = "playerEmail") String playerEmail,
				Model model
			) {
		
		List<Player> playersList = service.getAllPlayers();
		List<Place> placesList = service.getAllPlaces();
		List<Match> matchesList = service.getAllMatchesByPlayerEmail(playerEmail);
		
		model.addAttribute("playersList", playersList);
		model.addAttribute("placesList", placesList);
		model.addAttribute("matchesList", matchesList);
		
		model.addAttribute("filter_playerEmail", playerEmail);
		
		return "index";
	}
	
	@GetMapping("/filter/place")
	public String showFilteredMatchesByPlace(
			Model model,
			@RequestParam(name="placeId") int placeId) {
		
		
		model.addAttribute("playersList", service.getAllPlayers());
		model.addAttribute("placesList", service.getAllPlaces());
		model.addAttribute("matchesList", service.getAllMatchesFilteredByPlaceId(placeId));
		
		model.addAttribute("filter_placeId", placeId);
		
		return "index.html";
	}
	
	
	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		
		model.addAttribute("loggedinuser", currentPrincipalName);
		model.addAttribute("playersList", service.getAllPlayers());
		model.addAttribute("placesList", service.getAllPlaces());
		model.addAttribute("matchdto", new MatchDto());
		
		return "admin.html";
	}
	
	
	@PostMapping("/admin/reg/player")
	public String registerNewPlayer(
			Model model,
			@RequestParam(name="newemail") String newAccEmail,
			@RequestParam(name="newname") String newAccUsername,
			@RequestParam(name="newrole") int newAccRoleId) {
		
		
		String dbUpdateResult = service.addNewPlayer(newAccEmail, newAccUsername, newAccRoleId);
		
		model.addAttribute("message", dbUpdateResult);
		
		model.addAttribute("loggedinuser", SecurityContextHolder.getContext().getAuthentication().getName());
		model.addAttribute("playersList", service.getAllPlayers());
		model.addAttribute("placesList", service.getAllPlaces());
		model.addAttribute("matchdto", new MatchDto());
		
		return "admin.html";
	}
	
	
	@PostMapping("/admin/reg/place")
	public String registerNewPlace(
			Model model,
			@RequestParam(name="newpname") String newPlaceName,
			@RequestParam(name="newpaddress") String newPlaceAddress,
			@RequestParam(name="newpfee") int newPlaceRentFee) {
		
		
		String dbUpdateResult = service.addNewPlace(newPlaceName, newPlaceAddress, newPlaceRentFee);
		
		model.addAttribute("message", dbUpdateResult);
		
		model.addAttribute("loggedinuser", SecurityContextHolder.getContext().getAuthentication().getName());
		model.addAttribute("playersList", service.getAllPlayers());
		model.addAttribute("placesList", service.getAllPlaces());
		model.addAttribute("matchdto", new MatchDto());
		
		return "admin.html";
	}
	
	@PostMapping("/admin/reg/match")
	public String registerNewMatch(
				@ModelAttribute MatchDto matchdto,
				Model model
			) {
		
		service.registerMatch(matchdto);
		
		model.addAttribute("playersList", service.getAllPlayers());
		model.addAttribute("placesList", service.getAllPlaces());
		model.addAttribute("matchdto", new MatchDto());
		
		return "admin";
	}
		
	@GetMapping("/player/changepwd")
	public String showPasswordChangeForm(Model model) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Player loggedInPlayer = service.getPlayerByEmailAddress(username);
		
		model.addAttribute("player", loggedInPlayer);
		
		return "changepwd.html";
	}
	
	@PostMapping("/player/changepwd")
	public String changePassword(
			Model model,
			@RequestParam(name="playerEmail") String playerEmail,
			@RequestParam(name="oldPassword") String oldPassword,
			@RequestParam(name="newPassword") String newPassword,
			@RequestParam(name="newPasswordAgain") String newPasswordAgain) {
		
		
		String targetPage = "changepwd.html";
		
		PasswordChangeResponse pwdChangeSuccess = service.amendPlayerPassword(playerEmail, oldPassword, newPassword, newPasswordAgain);
		
		if(pwdChangeSuccess.isSuccess() == true) {

			targetPage = "redirect:/?passwordupdated";
		}
		else {
			
			Player loggedInPlayer = service.getPlayerByEmailAddress(playerEmail);
			
			model.addAttribute("player", loggedInPlayer);
			model.addAttribute("message", pwdChangeSuccess.getResponseMessage());
		}
		
		
		return targetPage;
	}
	
	
}
