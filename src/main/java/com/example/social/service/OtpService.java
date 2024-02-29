package com.example.social.service;

public interface OtpService {

	public String generateOtp(String username);
	
	public Boolean validateOTP(String key, String otpString);
}
