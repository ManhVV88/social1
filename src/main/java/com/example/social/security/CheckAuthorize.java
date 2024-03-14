package com.example.social.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.social.entity.User;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.utils.CommonContants;

@Component(value = "checkAuthorize")
public class CheckAuthorize {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;

	public boolean isPostOwnedByUser(Long postId,String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		
		return postRepository.existsByIdAndUserId(postId, user.getId());
	}
}
