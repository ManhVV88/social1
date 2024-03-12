package com.example.social.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.AcceptAddFriendRequest;
import com.example.social.dto.request.UpdateRequest;

public interface UserService {

	public ResponseEntity<?> infoUser(String username);
	
	public ResponseEntity<?> home(Authentication authentication, int page, int size);
	
	public ResponseEntity<?> updateUser(Authentication authentication, MultipartFile multipartFile,UpdateRequest userRequestUpdate);
	
//	public ResponseEntity<?> updateInfoUser(Authentication authentication, UpdateRequest userRequestUpdate);
	
	public ResponseEntity<?> addFriend(Authentication authentication, Long toUser);
	
	public ResponseEntity<?> acceptAddFriend(Authentication authentication, AcceptAddFriendRequest acceptRequest,Long userIdSendInvite);
	
	public ResponseEntity<?> getStatistical(Authentication authentication);
	
	public ResponseEntity<?> getImage(Authentication authentication,String image,String path);
	
	public ResponseEntity<?> disableUser(String username);
	
	public ResponseEntity<?> enableUser(String username);
}
