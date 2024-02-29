package com.example.social.service;

public interface OtpGenerator {
	
	public String generateOTP(String key);
	
	public String getOPTByKey(String key);
	
	public void clearOTPFromCache(String key);
	
}
