package com.example.social.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social.dto.request.ChangPassRequest;
import com.example.social.dto.request.ForgotPasswordRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.service.AuthService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	@Autowired
	AuthService authService;
	
	/**
	 * 
	 * @param user
	 * @return otp if signin success
	 */
	@PostMapping("/signin")
	ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest user) {		
		return authService.authenticateUser(user);
	}
	
	/**
	 * 
	 * @param otpRequest
	 * @return token after verifyOtp
	 */
	@PostMapping("/verifyOtp")
	ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest otpRequest){
		return authService.verifyOtp(otpRequest);
	}

	
	@PostMapping("/signup")
	ResponseEntity<?> registerUser(@Valid @RequestBody LoginRequest userForm) {
		return authService.registerUser(userForm);		
	}

	@PostMapping("/forgotpassword")
	ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest requestUser) {
		return authService.forgotPassword(requestUser);
	}
	
	@PutMapping("/changpassword/{token}")
	ResponseEntity<?> changePassword(@PathVariable String token ,@Valid @RequestBody ChangPassRequest requestUser) {
		return authService.changePassword(token,requestUser);
	}
	
}
