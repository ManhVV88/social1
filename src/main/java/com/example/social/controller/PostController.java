package com.example.social.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.LikeRequest;
import com.example.social.dto.request.PostEditRequest;
import com.example.social.dto.request.PostRequest;
import com.example.social.service.PostService;
import com.example.social.service.UserService;

//import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/post")
public class PostController {
	
	@Autowired
	PostService postService;
	
	@Autowired
	UserService userService;
	
	@PostMapping(value="" ,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE ,MediaType.APPLICATION_JSON_VALUE})
//	@ApiParam(name = "file", value = "Select the file to Upload", required = false, allowMultiple=true)
	ResponseEntity<Object> newPost(@RequestPart(value="image",required = false)  MultipartFile[] image, PostRequest postRequest) {		
		return postService.newPost(SecurityContextHolder.getContext().getAuthentication(),postRequest,image);
	}
	
	@PutMapping(value="/edit/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
//	@ApiParam(name = "file", value = "Select the file to Upload", required = false, allowMultiple=true)
	ResponseEntity<?> editPost(
			@PathVariable  @Valid @Min(1) Long id,
			@Valid PostEditRequest postEditRequest,
			@RequestPart(value="imageNew",required = false) MultipartFile[] imageNew
	){
		
		return postService.editPost(SecurityContextHolder.getContext().getAuthentication(),postEditRequest,id,imageNew);
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<?> deletePost(@PathVariable @Valid @Min(1) Long id) {
		return postService.deletePost(SecurityContextHolder.getContext().getAuthentication(),id);
	}
		
	@PostMapping("/like")
	ResponseEntity<?> like(@Valid @RequestBody LikeRequest likeRequest) {
		return postService.like(SecurityContextHolder.getContext().getAuthentication(),likeRequest);
	}
	
	@GetMapping("/home")
	public ResponseEntity<?> home(@Valid @Min(0) @RequestParam(defaultValue = "0") int page,@Valid @Min(0) @RequestParam(defaultValue = "10") int size) {
		return userService.home(SecurityContextHolder.getContext().getAuthentication(), page, size);
	}
}