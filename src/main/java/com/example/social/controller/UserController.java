package com.example.social.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.AcceptAddFriendRequest;
import com.example.social.dto.request.SendAddFriendRequest;
import com.example.social.dto.request.UpdateRequest;
import com.example.social.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping(value = {"/info","/info/{username}"})
	public ResponseEntity<?> infoUser(@PathVariable(required = false) String username) {
		if(username == null) {
			username = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		return userService.infoUser(username);
	}

	@GetMapping(value = {"/avatar","/avatar/{image}"})
	public ResponseEntity<?> getAvatar(@PathVariable(required = false) String image) {		
		return userService.getImage(SecurityContextHolder.getContext().getAuthentication(),image,"Avatar");
	}

	@PostMapping(value = "/update-user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )	
	public ResponseEntity<?> updateUser(@RequestPart(value="avatar",required = false) MultipartFile multipartFile,UpdateRequest updateRequest) {
		return userService.updateUser(SecurityContextHolder.getContext().getAuthentication(), multipartFile,updateRequest);
	}

//	@PostMapping("/update-user")
//	public ResponseEntity<?> updateInfoUser(@Valid @ModelAttribute UpdateRequest updateRequest) {
//		return userService.updateInfoUser(SecurityContextHolder.getContext().getAuthentication(), updateRequest);
//	}

	@PostMapping("/addfriend")
	public ResponseEntity<?> addFriend(@Valid @RequestBody SendAddFriendRequest sendAddFriendRequest) {
		return userService.addFriend(SecurityContextHolder.getContext().getAuthentication(),
				sendAddFriendRequest.getToUser());
	}

	@PutMapping("/accept_addfriend/{userIdSendInvite}")
	public ResponseEntity<?> acceptAddFriend(@PathVariable @Valid @Min(1) Long userIdSendInvite ,@Valid @RequestBody AcceptAddFriendRequest acceptRequest) {
		return userService.acceptAddFriend(SecurityContextHolder.getContext().getAuthentication(), acceptRequest , userIdSendInvite);
	}

	

	@GetMapping("/statistical")
	public ResponseEntity<?> getStatistical() {
		return userService.getStatistical(SecurityContextHolder.getContext().getAuthentication());
	}
}
