package com.example.social.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.response.MessageResponse;
import com.example.social.service.FileService;
import com.example.social.utils.CommonContants;

@Service
public class FileServiceImpl implements FileService{

	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Override
	public ResponseEntity<?> uploadImage(String username,MultipartFile multipartFile,String path,String callType) {		
		if (!isImage(multipartFile)) {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only JPEG images are allowed");
        }
		
		try {
			String projectPath = System.getProperty(CommonContants.USER_DIR);

			File projectDirectory = new File(projectPath);

			// Lấy đường dẫn tương đối của một thư mục con trong dự án
			String relativePath = getRelativePath(projectDirectory, path);

			Path uploadPath = Paths.get(relativePath);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			InputStream inputStream = multipartFile.getInputStream();
			String fileName = multipartFile.getOriginalFilename();
			String fileExtension =  FilenameUtils.getExtension(fileName);
			Path filePath = uploadPath.resolve(username+"."+fileExtension);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);	
			Map<String, String> response = new HashMap<>();
			if(callType.equals(CommonContants.CALLL_FROM_CTL)) {
				response.put("fileName", username+"."+fileExtension);
			} else {
				response.put("messsage","upload image ok");
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (IOException e) {			
			e.printStackTrace();
			logger.error("upload error by : "+e.getMessage());			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("upload image fail"));
		}
	}
	
	/**
	 * 
	 * @param baseDirectory
	 * @param subdirectory
	 * @return relative path
	 */
	private static String getRelativePath(File baseDirectory, String subdirectory) {
		File subdirectoryFile = new File(baseDirectory, subdirectory);
		return subdirectoryFile.getPath();
	}
	
	private boolean isImage(MultipartFile multipartFile) {			
		
		if (!multipartFile.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) && !multipartFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
			return false; 
		}
        try {            	
        	InputStream input = multipartFile.getInputStream();
        	ImageIO.read(input).toString();        	
        	return true;
        } catch (Exception e) {           
            return false;
        }
       
	}
	
	
}
