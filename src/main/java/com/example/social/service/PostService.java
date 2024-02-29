package com.example.social.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.CommentRequest;
import com.example.social.dto.request.LikeRequest;
import com.example.social.dto.request.PostEditRequest;
import com.example.social.dto.request.PostRequest;

public interface PostService {

	public ResponseEntity<Object> newPost(Authentication authentication, PostRequest postRequest,MultipartFile[] image);
	
	public ResponseEntity<?> editPost(Authentication authentication, PostEditRequest postEditRequest , Long PostId,MultipartFile[] imageNew);
	
	public ResponseEntity<?> newComment(Authentication authentication , CommentRequest commentRequest);
	
	public ResponseEntity<?> like(Authentication authentication , LikeRequest likeRequest);
	
	public ResponseEntity<?> deleteComment(Authentication authentication , Long idComment);
	
	public ResponseEntity<?> deletePost(Authentication authentication , Long idPost);
}
