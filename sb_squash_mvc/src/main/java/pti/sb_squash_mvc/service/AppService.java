package pti.sb_squash_mvc.service;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pti.sb_squash_mvc.dao.Database;
import pti.sb_squash_mvc.dto.MatchDto;
import pti.sb_squash_mvc.dto.PasswordChangeResponse;
import pti.sb_squash_mvc.model.Match;
import pti.sb_squash_mvc.model.Place;
import pti.sb_squash_mvc.model.Player;

@Service
public class AppService {
	
	private final Database db;
	private final EmailSender emailSender;
	
	@Autowired
	public AppService(Database db, EmailSender emailSender) {
		
		this.db = db;
		this.emailSender = emailSender;
	}
	
	// These methods are up for debate, they are nowhere near finalized!
	
	public List<Player> getAllPlayers() {
		
		List<Player> playersList = db.getAllUsers();
		
		return playersList;
	}

	public List<Place> getAllPlaces() {
		
		List<Place> placesList = db.getAllPlaces();
		
		return placesList;
	}

	public List<Match> getAllMatchesByPlayerEmail(String email) {
		
		List<Match> filteredMatches = db.getAllMatchesByUserEmail(email);
		
		return filteredMatches;
	}

	public List<Match> getAllMatches() {
		
		List<Match> allMatches = db.getAllMatches();
		
		return allMatches;
	}
	
	public List<Match> getAllMatchesFilteredByPlaceId(int placeId) {
		
		List<Match> matches = db.getAllMatchesByPlaceId(placeId);
		
		return matches;
	}
	
	
	public String addNewPlayer(String email, String username, int roleId) {
		
		String updateResult = null;
		
		Player playerToRegister = new Player();
		playerToRegister.setEmail(email);
		playerToRegister.setPlayerName(username);
		playerToRegister.setActivated(false);
		
		PasswordGeneratorService rndPwService = new PasswordGeneratorService();
		String randomPassword = rndPwService.generatePassword();
		System.out.println(">>> New password generated for " + email + " account: " + randomPassword + " <<<");
		
		String passwordToEncode = randomPassword;
		
		playerToRegister.setPassword( new BCryptPasswordEncoder(12).encode(passwordToEncode) );
		
		
		try {
			
			db.addUser(playerToRegister, roleId);
			updateResult = "Account '" + playerToRegister.getEmail() + "' registered successfully.";
			
			emailSender.sendEmail(
					playerToRegister.getEmail(), 
					"You have been registered at HungarianSquashAssociation.com", 
					"Your new account is ready for use. You can log in with your email address and the following generated password: " + randomPassword
					);
		}
		catch(ConstraintViolationException e) {
			
			updateResult = "Account registration failed: " + e.getErrorMessage();
		}

		
		return updateResult;
	}

	
	public String addNewPlace(String newPlaceName, String newPlaceAddress, int newPlaceRentFee) {

		String updateResult = null;
		
		Place placeToRegister = new Place();
		placeToRegister.setId(0);
		placeToRegister.setPlaceName(newPlaceName);
		placeToRegister.setAddress(newPlaceAddress);
		placeToRegister.setRentFeePerHourInHuf(newPlaceRentFee);
		
		
		try {
			
			db.addPlace(placeToRegister);
			updateResult = "Place '" + placeToRegister.getPlaceName() + "' registered successfully.";
		}
		catch(ConstraintViolationException e) {
			
			updateResult = "Place registration failed: " + e.getErrorMessage();
		}
		
		
		return updateResult;
	}

	public void registerMatch(MatchDto matchDto) {
		
		Match newMatch = new Match();
		newMatch.setPlayer1Score(matchDto.getPlayer1Score());
		newMatch.setPlayer2Score(matchDto.getPlayer2Score());
		newMatch.setDate(matchDto.getDate());
		
		db.addMatch(newMatch, matchDto.getPlaceId(), matchDto.getPlayer1Email(), matchDto.getPlayer2Email());
	}
	
	public Player getPlayerByEmailAddress(String email) {
		
		Player player = db.getUserByEmail(email);
		
		return player;
	}

	
	public PasswordChangeResponse amendPlayerPassword(String playerEmail, String oldPassword, String newPassword,
			String newPasswordAgain) {
		
		PasswordChangeResponse responseObj = null;
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder(12);
		
		Player player = db.getUserByEmail(playerEmail);
		
		boolean oldPasswordMatches = pwdEncoder.matches(oldPassword, player.getPassword()); 

		if(oldPasswordMatches == true) {
			
			if(newPassword.equals(newPasswordAgain) == true) {
				
				player.setPassword(pwdEncoder.encode(newPassword));
				player.setActivated(true);
				
				db.updateUser(player);
				
				responseObj = new PasswordChangeResponse(true, "Password has been changed succesfully.");
			}
			else {
				
				responseObj = new PasswordChangeResponse(false, "Password change failed: The new password and its repeat do not match.");
			}
		}
		else {
			
			responseObj = new PasswordChangeResponse(false, "Password change failed: Old password mismatch.");
		}
		
		
		return responseObj;
	}

}
