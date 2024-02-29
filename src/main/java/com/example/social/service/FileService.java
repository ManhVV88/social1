package com.example.social.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	public ResponseEntity<?> uploadImage(String username,MultipartFile multipartFile,String path,String callType);
}
