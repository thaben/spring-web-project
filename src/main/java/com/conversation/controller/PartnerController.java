package com.conversation.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.conversation.model.Conversation;
import com.conversation.model.Message;
import com.conversation.model.Partner;
import com.conversation.model.Review;
import com.conversation.model.User;
import com.conversation.repository.ConversationData;
import com.conversation.repository.PartnerData;
import com.conversation.repository.ReviewData;
import com.conversation.repository.UserData;
import com.conversation.service.UserService;

@Controller
public class PartnerController {

	@Autowired
	UserService userService;
	
	@Autowired
	UserData userData;
	
	@Autowired
	PartnerData pd;
	
	@Autowired
	ConversationData convData;
	
	@Autowired
	ReviewData reviewData;
	
	
	
	@GetMapping("/partner/registration")
	public String profilePartners(Model model) {
		
		return "registration-partner";
	}
	
	
	@PostMapping("/partner/registration/submit")
	public String formsubmit(@RequestParam("username")String username, @RequestParam("password")String password,
			@RequestParam("email")String email,
			@RequestParam("firstname")String firstName,@RequestParam("type")String role,
			@RequestParam("lastname")String lastName,
			Pageable pageable,Model model,HttpSession session,RedirectAttributes rm) {
		
		System.out.println(username);
		System.out.println(email);
		System.out.println(password);
		System.out.println(firstName);
		System.out.println(role);
		
		
		if(!userData.existsByUsername(username)) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		
			if (role.equals("partner")) {
				Partner p = new Partner();
				p.setPassword(hashedPassword);
				p.setRole(role);
				p.setUsername(username);
				p.setFirstName(firstName);
				p.setLastName(lastName);
				p.setEmail(email);
				pd.save(p);
				System.out.println("PARTNER CREATED");
			} else if (role.equals("user")) {
				User u = new User();
				u.setPassword(hashedPassword);
				u.setRole(role);
				u.setUsername(username);
				u.setFirstName(firstName);
				u.setLastName(lastName);
				u.setEmail(email);
				userData.save(u);
				System.out.println("USER CREATED");
			} else {
				model.addAttribute("error", true);
			}
		}else {
			 rm.addFlashAttribute("errorUsername",true);
		}
		
		return "redirect:/login";
	}
	
	
	
	@RequestMapping("/partner/profile")
	public String profileUpdate(HttpSession session,Model model) {
		if(session.getAttribute("loggedin")!=null){
		
		Integer id=(Integer) session.getAttribute("id");
		System.out.println(id);
		Optional<Partner> p=pd.findById(id);
		System.out.println(p);
		model.addAttribute("partner",p.get());
		
		return "profile-partner-form";
		}else {
			return "redirect:/login";
		}
	}
	
	@RequestMapping("/partner/saveUser")
	public String profileSave(@ModelAttribute("partner") Partner partner,HttpSession session) {
		
		pd.save(partner);
		
		return "redirect:/logout";
	}
	
	
	
	
	
	@RequestMapping("/partner/profile/{id}")
	public String profilePartner(@PathVariable(value="id")Integer id, Pageable pageable,Model model,HttpSession session) {

		if (session.getAttribute("loggedin") != null) {
			System.out.println(id);
			Optional<Partner> p = pd.findById(id);
			model.addAttribute("partner", p.get());
			model.addAttribute("reviews", p.get().getReviews());
			return "ProfilePartner";
		} else {
			return "redirect:/login";
		}
	}
	
	
	@GetMapping("/partner/profile/start/{id}")
	public String startConversation(@PathVariable(value = "id")Integer id,@RequestParam("title")String title,@RequestParam("text")String text, 
			Pageable pageable,Model model,HttpSession session) {
		
		int userid=(int) session.getAttribute("id");
		
		Optional<Partner> p=pd.findById(id);
		User u=userData.findById(userid);
		
		Conversation c=new Conversation();
		c.setInterlocutor(p.get());
		c.setCreator(u);
		c.setTitle(title);
		
		Message m=new Message();
		m.setSender(u);
		m.setReceiver(p.get());
		m.setText(text);
		m.setUnread(true);
		c.getMessages().add(m);
		
		convData.save(c);
		model.addAttribute("uid", c.getId());
		
		
//		RedirectView redirectView = new RedirectView();
//		redirectView.setUrl("http://127.0.0.1:8080/showConversation?conversationId="+c.getId());
//		return redirectView
		return "redirect:/showConversation?conversationId="+c.getId();
	}
	
	
	@GetMapping("/partner/review/{id}")
	public String partnerReview(@PathVariable(value = "id")Integer id,@RequestParam("title")String title,@RequestParam("review")String reviewText,
			@RequestParam("rating")double points,Model model,HttpSession session,RedirectAttributes rm) {
		Integer PartnerId=id;
		Integer ReviewerId=(Integer) session.getAttribute("id");		
		
		Optional<Partner> p=pd.findById(PartnerId);
		Optional<User> r=userData.findById(ReviewerId);
		
		Review review=new Review();
		review.setPartner(p.get());
		review.setReviewer(r.get());
		review.setPoints(points);
		review.setReview(reviewText);
		review.setTitle(title);
		
		reviewData.save(review);
		p.get().setTotalPoints(reviewData.countPoints(id));
		
		pd.save(p.get());
		model.addAttribute("partner",p.get());
		
		rm.addFlashAttribute("reviews",reviewData.findAllByPartner(p.get()));
		
		return "redirect:/partner/profile/"+p.get().getId();
	}
	
	
	
	
}	
	


