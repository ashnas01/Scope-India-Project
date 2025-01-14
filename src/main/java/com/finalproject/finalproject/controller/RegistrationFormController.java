package com.finalproject.finalproject.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.finalproject.finalproject.model.City;
import com.finalproject.finalproject.model.Contact;
import com.finalproject.finalproject.model.Country;
import com.finalproject.finalproject.model.Course;
import com.finalproject.finalproject.model.RegistrationForm;
import com.finalproject.finalproject.model.State;
import com.finalproject.finalproject.repository.CityRepository;
import com.finalproject.finalproject.repository.CountryRepository;
import com.finalproject.finalproject.repository.CourseRepository;
import com.finalproject.finalproject.repository.RegistrationFormRepository;
import com.finalproject.finalproject.repository.StateRepository;
import com.finalproject.finalproject.service.CountryService;
import com.finalproject.finalproject.service.RegistrationFormService;
import com.finalproject.finalproject.service.StateService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class RegistrationFormController {
	 @Autowired
	 private RegistrationFormRepository regRepo;
	@Autowired
	private RegistrationFormService regFormService;
	 @Autowired
	    private CountryRepository countryRepository;
	 @Autowired 
	 	private CourseRepository courseRepository;
	 @Autowired
	 	private StateRepository stateRepo;
	 @Autowired
	 	private CityRepository cityRepo;
	    @Autowired
	    private StateService stateService;

	    @Autowired
	    private CityRepository cityRepository;
	    @Autowired
	    private CountryService countryService;
	    @Autowired
		 private JavaMailSender sender;
	   
	@RequestMapping("show")
	public String showform(Model m) {
		m.addAttribute("reg",new RegistrationForm());
	    m.addAttribute("countryModel", countryService.countryList());
		return "registrationform";
	}
	@RequestMapping("/home")
	public String home(Model m) {
		m.addAttribute("reg",new RegistrationForm());
		return "myhome";
	}
	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("reg",new RegistrationForm());
		return "home";
	}
	   @GetMapping("/countries")
	    public List<Country> getCountries() {
	        return countryRepository.findAll();
	    }

	    @GetMapping("/states/{countryId}")
	    public @ResponseBody Iterable<State> getStateByCountry(@PathVariable Country countryId) {
	    	int co=countryId.getId();
	    	System.out.print("co=========================================="+co);
	        return stateService.getStateBy(countryId);
	    }

	    @GetMapping("/cities/{stateId}")
	    public @ResponseBody List<City> getCityByState(@PathVariable State stateId) {
	        return cityRepository.findByState(stateId);
	    }
	//@RequestMapping("save")
//	public String save(@Valid @ModelAttribute("reg")RegistrationForm reg,BindingResult result) {
//		if(result.hasErrors()) {
//			return "redirect:/show";
//		
//		}
//		else {
//			return "samp";
//		}
	//}
//	@RequestMapping("save")
//	public String send(RegistrationForm regForm,HttpServletRequest request) throws MessagingException, IOException {
//		regFormService.register(regForm, getSiteURL(request));
//		return "success";
//	}
	    
	    
	private String getSiteURL(HttpServletRequest request) {
		String siteurl=request.getRequestURL().toString();
		return siteurl.replace(request.getServletPath(), "");
	}
	@RequestMapping("/verify")
	public String verify(@Param("code")String code,Model m) {
		System.out.println(code);
		m.addAttribute("form",new RegistrationForm());
		if(regFormService.verify(code)) {
			return "otpsnd";
		}
		else {
			return "error";
		}
		}
	@RequestMapping("/sendotp")
	public String sendOtp(Model model, @RequestParam("email") String email) throws UnsupportedEncodingException, MessagingException {
	    RegistrationForm existingUser = regRepo.findByEmail(email);
	    
	    if (existingUser != null) {
	        String newOtp = generatedRandomOtp();
	        existingUser.setOtp(newOtp);
	        regRepo.save(existingUser);
	        regFormService.sendEmail(existingUser, newOtp); 
	        model.addAttribute("register", existingUser);
	        model.addAttribute("email", email);
	        return "verifyotp";
	    } else {
	        model.addAttribute("error", "Email not found. Please register first.");
	        return "registrationform";
	    }
	}

	
	public String generatedRandomOtp() {
	 	String otp=String.valueOf(new Random().nextInt(900000)+100000);
	 	return	otp;
}
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("email")String email,@RequestParam("otp") String enteredOTP,Model model ) {
		RegistrationForm user=regRepo.findByEmail(email);
		if(user!=null && user.getOtp()!=null && user.getOtp().equals(enteredOTP)){
			user.setVerified(true);
			regRepo.save(user);
			model.addAttribute("user",user);
			model.addAttribute("email", email);
			return "setnewpassword";
		}
		else {
			model.addAttribute("email", "invalid otp");
			return("verifyotp");
		}
	}
	
	@RequestMapping("/sendpassword")
		public String setpassword(Model m,@RequestParam("email")String email,@RequestParam("password")String password,@RequestParam("confirmpassword")String confirmpassword)
		{
		RegistrationForm user=regRepo.findByEmail(email);
		if(password.equals(confirmpassword)) {
			user.setPassword(password);
			user.setConfirmpassword(confirmpassword);
			regRepo.save(user);
			return "home";
		}
		else{
			m.addAttribute("error", "password entered is incorrect");
			return "setnewpassword";
			}
		}
	 @RequestMapping("/login")
	    public String loginUser(Model m) {
	        m.addAttribute("u", new RegistrationForm());
	        return "login"; 
	    }

	    @RequestMapping("/userlogin")
	    public String log(Model m,@RequestParam("email") String email, @RequestParam("password") String password,HttpServletResponse response,HttpServletRequest request,HttpSession session,@RequestParam(value="KeepLoggedIn",required=false)String KeepLoggedIn) {
	        RegistrationForm user = regRepo.findByEmail(email);

	        
	        if (user != null && email.equals(user.getEmail())&&password.equals(user.getPassword())) {
	        	 user.setLogin(true);
	        	 regRepo.save(user);
	            session.setAttribute("username", user.getEmail()); 
//	            Cookie[] userCookie = request.getCookies();
//	            if(userCookie!=null) {
//	            	for(Cookie cookies:userCookie) {
//	            		if("username".equals(cookies.getName())) {
//	            			cookies.setMaxAge(60);
//	            			response.addCookie(cookies);
//	            			m.addAttribute(cookies);
//	            			return "redirect:/dashboard"; 
//	            		}
//	            	}
//	            }
	          Cookie cookies=new Cookie("username",user.getEmail());
	          if("true".equals(KeepLoggedIn)) {
	              cookies.setMaxAge(60*60);
	          }
	         // cookies.setMaxAge(60);
	          cookies.setPath("/");
	          cookies.setHttpOnly(true);
	          cookies.setSecure(true);
	          response.addCookie(cookies);
	          m.addAttribute("user",new RegistrationForm());
	          return "redirect:/dashboard";
	          
	       
	        }
	        else {
	        	return "error";
	        }
	        }

	    @GetMapping("/dashboard")
	    public String studentDashboard(Model m, HttpServletRequest request, HttpSession session) {
	       
	        String sessionUser = (String) session.getAttribute("username");
	        System.out.println(sessionUser);
	       // if (sessionUser != null) {
	         //   m.addAttribute("username", sessionUser);
	         //   return "dashboard"; 
	       // }

	        Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if ("username".equals(cookie.getName())) {
	                    String email = cookie.getValue();
	                    RegistrationForm user = regRepo.findByEmail(email);
	                    String country=user.getCountry();
	                    String state=user.getState();
	                    String city=user.getCity();
	                    Integer c=Integer.valueOf(country);
	                    Integer s=Integer.valueOf(state);
	                    Integer ci=Integer.valueOf(city);
	                    Country con=countryRepository.getById(c);
	                    State st=stateRepo.getById(s);
	                    City cit=cityRepo.getById(ci);
	                    	//System.out.print("country======================================="+con.getName());
	                    if (user != null) {
	                    	//String fname=user.getFirstname();
	                        m.addAttribute("username", email);
	                        m.addAttribute("stud",user);
	                        m.addAttribute("count",con);
	                        m.addAttribute("sta",st);
	                        m.addAttribute("city",cit);
	                       
//	                        m.addAttribute("firstname",fname);
//	                        m.addAttribute("lastname",user.getLastname());
//	                        m.addAttribute("dob",user.getDob());
//	                        m.addAttribute("email",user.getEmail());
//	                        m.addAttribute("phone",user.getPhone());
//	                        m.addAttribute("con",user.getCountry());
//	                        m.addAttribute("stat",user.getState());
//	                        m.addAttribute("city",user.getCity());
//	                        m.addAttribute("hob",user.getHobbies());
	                        return "dashboard";
	                    }
	                }
	            }
	        }
	        return "login";
	    }
	    @RequestMapping("save")
		public String send(@Valid @ModelAttribute("reg")RegistrationForm reg,@RequestParam("fileupload") MultipartFile file, BindingResult result,Model model,HttpServletRequest request) throws MessagingException, IOException {
			 if (file.isEmpty()) {
		            result.rejectValue("file", "error.file", "Please select an image to upload");
		        }

			if(result.hasErrors()) {
				return "registrationForm";
		    }else {
				regFormService.register(reg, getSiteURL(request),file);
				model.addAttribute("registeruser",reg);
				return "success";
			}
	    }
	    @RequestMapping("/searchcourses")
	    private String searchCourses(Model m) {
	    	m.addAttribute(new RegistrationForm());
	    	m.addAttribute("coursemodel",new RegistrationForm());
	    	return "courses";
	    }
//	    @RequestMapping("/courses")
//	    private String course(Model m,RegistrationForm regForm,HttpSession session) {
//	      	String student=(String) session.getAttribute("username");
//	    	RegistrationForm user=regRepo.findByEmail(student);
//	    	if(user!=null) {
//	    		user.setCourse(regForm.getCourse());
//	    		regRepo.save(user);
//	    		return "success";  	
//	    }
//	    	return "error";
//	    }
	    @RequestMapping("/course/{id}")
    	public String cour(Model m,@PathVariable("id") int id) {
    		Course cou=courseRepository.getById(id);
    		String a=cou.getCourseName();
    		String b=cou.getDuration();
    		m.addAttribute("details",cou);
    		return "courseDetails";	
    		
    	}
	    @RequestMapping("/choose/{id}")
	    public String choose(Model m,@PathVariable("id") int id,HttpSession session) {
	    	String student=(String) session.getAttribute("username");
	    	RegistrationForm user=regRepo.findByEmail(student);
	    	if(user.isLogin()){
	    		user.setCourse(id);
	    		regRepo.save(user);
	    		return "redirect:/home";
	    	}
	    	else {
	    		return "redirect:/dashboard";
	    	}
	    	
	    	
	    }
	    @RequestMapping("/profileedit")
	    	private String profile(Model m,HttpSession session) {
	        	String sessionUser = (String) session.getAttribute("username");
	        	RegistrationForm user=regRepo.findByEmail(sessionUser);
		    	//m.addAttribute("users",new RegistrationForm());
	        	m.addAttribute("countryModel", countryService.countryList());
		    	 m.addAttribute("users",user);
		    	return "profile";	
	    }
	    @RequestMapping("/profile")
	    public String edit(HttpSession session,Model m,RegistrationForm user,@RequestParam("avatar")MultipartFile file) throws IOException {
	    	String student=(String) session.getAttribute("username");
	    	m.addAttribute("users",new RegistrationForm());
	    	RegistrationForm stud=regRepo.findByEmail(student);
	    	if(stud!=null) {
	    		stud.setFirstname(user.getFirstname());
	    		stud.setLastname(user.getLastname());
	    		stud.setCountry(user.getCountry());
	    		stud.setDob(user.getDob());
	    		stud.setGender(user.getGender());
	    		stud.setHobbies(user.getHobbies());
	    		stud.setPhone(user.getPhone());
	    		stud.setFileupload(user.getFileupload());
	    		regFormService.edit(user,file,stud);
	    		regRepo.save(stud);
	    		return "redirect:/dashboard";
	    	}
	    	else {
	    		return "redirect:/profileedit";
	    	}
	    	}
	    	@RequestMapping("/changepassword")
	    		private String changepassword(Model m,HttpSession session) {
	    			String sessionUser=(String) session.getAttribute("username");
	    			m.addAttribute("mod",new RegistrationForm());
	    			return "changePassword";
	    	}
	    	
	    	@RequestMapping("/passwordchange")
	    		private String change(Model m,HttpSession session,@RequestParam("password") String password,@RequestParam("confirmpassword") String changepassword,RegistrationForm reg) {
	    		String student=(String) session.getAttribute("username");
	    		RegistrationForm stud=regRepo.findByEmail(student);
	    		if(stud!=null && stud.getPassword()!=null && stud.getPassword().equals(password)) {
	    			stud.setPassword(changepassword);
	    			stud.setConfirmpassword(changepassword);
	    			regRepo.save(stud);
	    			return "redirect:/login";
	    		}
	    		else {
	    			return "redirect:/changepassword";
	    		}
	    		
	    	}
	    	@RequestMapping("logout")
	        public String logout(HttpSession session, HttpServletResponse response) {
	            session.setAttribute("username", null);
	            Cookie cookie = new Cookie("username", null);
	            cookie.setMaxAge(0);
	            cookie.setSecure(true);
	            cookie.setHttpOnly(true);
	            cookie.setPath("/");
	            response.addCookie(cookie);
	            return "redirect:/login";
	        }
	    	@RequestMapping("forgotpassword")
	    	public String forgot(Model m) {
	    		m.addAttribute("form",new RegistrationForm());
	    		return "forgot";
	    	}
	    	@RequestMapping("/forgotpass")
	    	public String forpass(Model m,@RequestParam("email") String email,RegistrationForm regForm,HttpSession session) throws UnsupportedEncodingException, MessagingException{
	    		String student=(String) session.getAttribute("username");
	    		RegistrationForm stud=regRepo.findByEmail(student);
	    		if(stud.getEmail().equals(email)) {
	    			 String newOtp = generatedRandomOtp();
	    		        stud.setOtp(newOtp);
	    		        regRepo.save(stud);
	    		        regFormService.sendEmail(stud, newOtp); 
	    		        m.addAttribute("register", stud);
	    		        m.addAttribute("email", email);
	    		        return "verifyotp";
	    		}
	    		else {
	    			return "redirect:/forgotpassword";
	    		}
	    	}
	    	@RequestMapping("/contact")
	    	public String contact(Model m) {
	    		m.addAttribute("reg",new Contact());
	    		return "contact";
	    	}
//	    	@GetMapping("/mail")
	    //	public String contact(Model model,Contact user)

//	    	{
//	    		
//	    		String from=user.getFrom();
//	    		String to="ashikaj888@gmail.com";		
//	    		SimpleMailMessage messsage=new SimpleMailMessage();
//	    		messsage.setFrom(from);
//	    		messsage.setTo(to);
//	    		messsage.setSubject(user.getSubject());
//	    		messsage.setText(user.getMessage());
//	    		sender.send(messsage);
//	    		return "success";
//	    	}
	    	 public String sendEmail(@ModelAttribute("user") Contact user, Model model,@RequestParam("message") String message,@RequestParam("from") String from,@RequestParam("to") String to,@RequestParam("subject") String subject)throws MessagingException {

	 	        String msg=" <b>From</b>:<i>[[from]]</i><br> <b>To</b>:<i>[[to]]<br><b>Subject</b>:<i>[[subject]]<br><b>Message</b>:<i>[[message]]";
	 	        msg=msg.replace("[[from]]", from);
	 	        msg=msg.replace("[[to]]", to);
	 	        msg=msg.replace("[[subject]]", subject);
	 	        msg=msg.replace("[[message]]", message);
	 	       regFormService.sendHtmlEmail(user.getFrom(),user.getTo(),user.getSubject(), msg);
	 	       // model.addAttribute("message", "Email sent successfully");
	 	        return "emailsuccess";
	 	    }
	    
	}


	
