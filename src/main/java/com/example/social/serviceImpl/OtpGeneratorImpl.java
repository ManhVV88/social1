package com.example.social.serviceImpl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.social.service.OtpGenerator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpGeneratorImpl implements OtpGenerator{

	private static final Integer EXPIRE_SECONDS = 60;
	private LoadingCache<String, String> otpCache;

	public OtpGeneratorImpl() {
		super();

		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS)
				.build(new CacheLoader<String, String>() {
					@Override
					public String load(String s) throws Exception {
						return "";
					}
				});
	}
	
	@Override
	public String generateOTP(String key) {
		Random random = new Random();

		int otpNumber = random.nextInt(1000000);

		String otp = String.format("%06d", otpNumber);
		otpCache.put(key, otp);

		return otp;
	}

	@Override
	public String getOPTByKey(String key) {
		return otpCache.getIfPresent(key);
	}
	
	@Override
	public void clearOTPFromCache(String key) {
		otpCache.invalidate(key);
	}

}
