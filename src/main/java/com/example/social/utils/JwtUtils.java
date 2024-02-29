package com.example.social.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

//	@Value("${social.app.jwtSecret}")
//	private String jwtSecret;

	@Value("${social.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	SecretKey key = Jwts.SIG.HS256.key().build();
	
	public String generateJwtToken(Authentication authentication) {
		return Jwts.builder().subject(authentication.getName())
				.issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key).compact();
	}


	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(key).build().parse(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("JWT claims string is not match: {}", e.getMessage());
		}

		return false;
	}

	/**
	 * 
	 * @param expiryDate
	 * @return true when currentDateTime <= targetDateTime
	 * falsse when currentDateTime > targetDateTime
	 */
	public boolean checkDate(Date expiryDate) {		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime targetDateTime = LocalDateTime.parse(expiryDate.toString(), formatter);

        LocalDateTime currentDateTime = LocalDateTime.now();
        if(currentDateTime.isBefore(targetDateTime)) {
        	return true;
        }

		return false;
	}

	/**
	 * 
	 * @param newPass
	 * @param confirmPass
	 * @return return false newPass not matching with confirmPass 
	 * true if matching 
	 */
	public boolean checkPass(String newPass, String confirmPass) {
		if (newPass.equals(confirmPass)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return String system date current
	 */
	public String getSystemDateCurrnet() {
		LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime.toString();
	}
	
	public Date getDateTimeCurrent() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTime();
	}
}
