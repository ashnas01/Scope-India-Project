package com.finalproject.finalproject.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.finalproject.finalproject.model.RegistrationForm;
import com.finalproject.finalproject.repository.RegistrationFormRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;


@Service
public class RegistrationFormService {
	 @Autowired
	 private RegistrationFormRepository regRepo;
	 @Autowired
	 private JavaMailSender sender;
	 
	 private String folder="/src/main/resources/static/images/";
	 public void saveRegForm(RegistrationForm regform) {
	        regRepo.save(regform);
	    }
//	 public void register(RegistrationForm regForm,String siteUrl) throws UnsupportedEncodingException,MessagingException{
//		 String randomCode=RandomString.make(64);
//		 regForm.setVerificationcode(randomCode);
//		 regForm.setEnabled(false);
//		 regRepo.save(regForm);
//		 sendVerificationEmail(regForm,siteUrl);
//	 }
	 
	 public void register(RegistrationForm reg, String siteUrl, MultipartFile file) throws MessagingException, IOException { 
		    // Define your image upload folder (e.g., "src/main/resources/static/images")
		    String folder = "src/main/resources/static/images"; // Adjust the path according to your project structure

		    // Ensure the upload directory exists
		    Path uploadPath = Paths.get(folder);
		    if (!Files.exists(uploadPath)) {
		        Files.createDirectories(uploadPath); // Create directory if it doesn't exist
		    }

		    // Get the original filename
		    String filename = file.getOriginalFilename();

		    // Resolve the full path where the file will be saved
		    Path path = uploadPath.resolve(filename);

		    // Save the file to the target directory
		    Files.write(path, file.getBytes());

		    // Store only the file name or a relative path in the database
		    reg.setFileupload(filename);  // Save only the filename (without 'images/' prefix)

		    // Generate random verification code and other fields
		    String randomCode = RandomString.make(64);
		    reg.setVerificationcode(randomCode);
		    reg.setEnabled(false);

		    // Save the registration data to the database
		    regRepo.save(reg);

		    // Send verification email
		    sendVerificationEmail(reg, siteUrl);
		}

	 public void sendVerificationEmail(RegistrationForm regForm,String siteUrl)  throws UnsupportedEncodingException, MessagingException{
		 String toaddr=regForm.getEmail();
		 String fromaddr="ashikaj888@gmail.com";
		 String senderName="Team Scope";
		 String subject="Verify Registration";
		 String message="Dear [[name]] please click below link to verify<h3> <a href=\"[[URL]]\" target=\"_blank\">VERIFY</a></h3>";
		 MimeMessage msg=sender.createMimeMessage();
		 MimeMessageHelper messageHelper=new MimeMessageHelper(msg);
		 
		 messageHelper.setFrom(fromaddr,senderName);
		 messageHelper.setTo(toaddr);
		 messageHelper.setSubject(subject);
		 message=message.replace("[[name]]", regForm.getFirstname()+" "+regForm.getLastname());
		 String url=siteUrl+"/verify?code="+regForm.getVerificationcode();
		 
		 message=message.replace("[[URL]]", url);
		 messageHelper.setText(message,true);
		 sender.send(msg);
	 }
	 public boolean verify(String verificationcode) {
		 RegistrationForm regForm=regRepo.findByVerificationcode(verificationcode);
		 if(regForm==null||regForm.isEnabled()) {
			 return false;
		 }else {
			 regForm.setVerificationcode(null);
			 regForm.setEnabled(true);
			 regRepo.save(regForm);
		return true;
		 
	 }
	 }
		 
	 public void sendEmail(RegistrationForm regForm, String otp) throws UnsupportedEncodingException, MessagingException {
		    String toAddr = regForm.getEmail();
		    String fromAddr = "ashikaj888@gmail.com";
		    String senderName = "Team Scope";
		    String subject = "Your OTP Code";
		    String message = "Your OTP code is [[otp]].";

		    MimeMessage msg = sender.createMimeMessage();
		    MimeMessageHelper messageHelper = new MimeMessageHelper(msg);

		    messageHelper.setFrom(fromAddr, senderName);
		    messageHelper.setTo(toAddr);
		    messageHelper.setSubject(subject);
		    message = message.replace("[[otp]]", otp);
		    messageHelper.setText(message, true);

//		    regForm.setOtp(otp); 

		    sender.send(msg);
		}
	 public void sendHtmlEmail(String from, String to, String subject, String message) throws MessagingException {
	        MimeMessage mimeMessage =sender.createMimeMessage();

	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	        helper.setFrom(from);
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(message, true);
	        sender.send(mimeMessage);
	    }

	public void edit(RegistrationForm stud, MultipartFile file,RegistrationForm stu) throws IOException {
		String folders = "src/main/resources/static/images"; // Adjust the path according to your project structure

	    // Ensure the upload directory exists
	    Path uploadPath = Paths.get(folders);
	    if (!Files.exists(uploadPath)) {
	        Files.createDirectories(uploadPath); // Create directory if it doesn't exist
	    }

	    // Get the original filename
	    String filename = file.getOriginalFilename();

	    // Resolve the full path where the file will be saved
	    Path path = uploadPath.resolve(filename);

	    // Save the file to the target directory
	    Files.write(path, file.getBytes());

	    // Store only the file name or a relative path in the database
	    stu.setFileupload(filename);
		
	}
	 
	
}