package com.example.social.serviceImpl;


import org.springframework.stereotype.Service;

import com.example.social.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService{
		
	private OtpGeneratorImpl otpGenerator;	
	
		
	public OtpServiceImpl(OtpGeneratorImpl otpGenerator) {
		this.otpGenerator = otpGenerator;
	}

	@Override
	public String generateOtp(String username) {	              
        
        return otpGenerator.generateOTP(username);
	}
	
	@Override
	public Boolean validateOTP(String key, String otpString)
    {
        // get OTP from cache
        String cacheOTP = otpGenerator.getOPTByKey(key);
        
        if (cacheOTP!=null && cacheOTP.equals(otpString))
        {
            otpGenerator.clearOTPFromCache(key);
            return true;
        }
        return false;
    }
	
}
