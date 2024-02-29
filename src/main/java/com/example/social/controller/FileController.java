package com.example.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.serviceImpl.FileServiceImpl;
import com.example.social.utils.CommonContants;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/file")
public class FileController {
	@Autowired
	FileServiceImpl fileServiceImpl;
	
	@PostMapping(value ="/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )	
	public ResponseEntity<?> uploadImage(@RequestParam("avatar") MultipartFile multipartFile) {	
		return fileServiceImpl.uploadImage(SecurityContextHolder.getContext().getAuthentication().getName(),multipartFile,CommonContants.PATH_AVATAR,CommonContants.CALLL_FROM_CLIENT);
	}
}
