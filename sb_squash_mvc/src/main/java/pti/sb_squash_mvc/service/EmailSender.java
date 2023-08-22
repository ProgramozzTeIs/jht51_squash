package pti.sb_squash_mvc.service;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
	
	private JavaMailSenderImpl mailSender;
	
	
	public EmailSender() {
		
		this.mailSender = new JavaMailSenderImpl();
		
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", true);
		mailProperties.put("mail.smtp.starttls.enable", true);
		
		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("david.remetei@gmail.com");
		mailSender.setPassword("agjotvqmqgbjmshh");
	}
	
	
	
	public void sendEmail(String targetAddress, String emailSubject, String emailText) {
		
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("david.remetei@gmail.com");
		message.setTo(targetAddress);
		message.setText(emailText);
		message.setSubject(emailSubject);

		
		mailSender.send(message);
	}




	

}
