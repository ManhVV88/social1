package com.example.social.service;

import org.springframework.http.ResponseEntity;

import com.example.social.dto.request.ChangPassRequest;
import com.example.social.dto.request.ForgotPasswordRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;

public interface AuthService {
	
	public ResponseEntity<?> authenticateUser(LoginRequest user);
	
	public ResponseEntity<?> verifyOtp(OtpRequest otpRequest);
	
	public ResponseEntity<?> registerUser(LoginRequest userForm);
	
	public ResponseEntity<?> forgotPassword(ForgotPasswordRequest requestUser);
	
	public ResponseEntity<?> changePassword(String token,ChangPassRequest requestUser);
	
}
