package com.example.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social.entity.PasswordResetToken;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	
	PasswordResetToken findByToken(String token);	
	
	PasswordResetToken findByUserId(Long UserId);
	
}
