package com.example.social.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social.dto.request.CommentRequest;
import com.example.social.service.PostService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

	@Autowired
	PostService postService;
	
	@PostMapping("")
	ResponseEntity<?> newComment(@Valid @RequestBody CommentRequest commentRequest){
		return postService.newComment(SecurityContextHolder.getContext().getAuthentication(),commentRequest);
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteComment(@PathVariable @Valid @Min(1) Long id) {
		return postService.deleteComment(SecurityContextHolder.getContext().getAuthentication(),id);
	}
}
