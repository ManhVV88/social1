package com.example.social;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.CommentRequest;
import com.example.social.dto.request.LikeRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.entity.Comment;
import com.example.social.entity.Image;
import com.example.social.repository.CommentRepository;
import com.example.social.repository.ImageRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	CommentRepository commentRepository;
	
	private static String USERNAME = "vanmanhvn@gmail.com";

	private static String PASSWORD = "123";

	private static String token;

	private static String date;
	@BeforeAll
	public void setUp() {

		ResponseEntity<Map> resData = getSetupData(USERNAME, PASSWORD);

		token = resData.getBody().get("authToken").toString();
		
		date = jwtUtils.getSystemDateCurrnet();
		
	}	
	
	private ResponseEntity<Map> getSetupData(String username, String password) {
		String url = "http://localhost:" + port + "/api/v1/auth/signin";

		LoginRequest loginRequest = new LoginRequest(username, password);
		ResponseEntity<Map> response = testRestTemplate.postForEntity(url, loginRequest, Map.class);

		String url2 = "http://localhost:" + port + "/api/v1/auth/verifyOtp";
		OtpRequest otpRequest = new OtpRequest(response.getBody().get("otp").toString(), username);

		ResponseEntity<Map> response1 = testRestTemplate.postForEntity(url2, otpRequest, Map.class);
		return response1;
	}
	

	@Test
	public void testNewPost_Success_ReturnOK() throws IOException {
		String url = "http://localhost:" + port + "/api/v1/social/post/newpost";
		// Tạo tệp giả lập
		MockMultipartFile[] mockFiles = new MockMultipartFile[2];
		mockFiles[0] = new MockMultipartFile("post1", "image1.png", MediaType.IMAGE_PNG_VALUE,
				"image1 content".getBytes());
		mockFiles[1] = new MockMultipartFile("post2", "image2.png", MediaType.IMAGE_PNG_VALUE,
				"image2 content".getBytes());		
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("content", "hôm nay trời lạnh quá "+ date);

        // Thêm các tệp tin hình ảnh vào phần thân yêu cầu
        for (MultipartFile image : mockFiles) {
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
        }
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		
		
	}
	
	@Test
	public void testEditpost_SuccessReturn_Ok() throws IOException {
		Image image = imageRepository.findFirstByImageStartsWithOrderByIdDesc(USERNAME);      
        
        
		String url1 = "http://localhost:" + port + "/api/v1/social/post/editposttitle/"+image.getPost().getId();
		// Tạo tệp giả lập
		MockMultipartFile[] mockFiles1 = new MockMultipartFile[2];
		mockFiles1[0] = new MockMultipartFile("post3", "image3.png", MediaType.IMAGE_PNG_VALUE,
				"image3 content".getBytes());
		mockFiles1[1] = new MockMultipartFile("post4", "image4.png", MediaType.IMAGE_PNG_VALUE,
				"image4 content".getBytes());	
		
		MultiValueMap<String, Object> body1 = new LinkedMultiValueMap<>();
        body1.add("content", "hôm nay trời lạnh quá "+ date);

        // Thêm các tệp tin hình ảnh vào phần thân yêu cầu
        for (MultipartFile image1 : mockFiles1) {
            body1.add("imageNew", new ByteArrayResource(image1.getBytes()) {
                @Override
                public String getFilename() {
                    return image1.getOriginalFilename();
                }
            });
        }
        
        body1.add("idImage", image.getId());
        
		HttpHeaders headers1 = new HttpHeaders();
		headers1.set("Authorization", "Bearer " + token);
		headers1.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> requestEntity1 = new HttpEntity<>(body1, headers1);

		ResponseEntity<String> response1 = testRestTemplate.exchange(url1, HttpMethod.PUT, requestEntity1, String.class);

		assertEquals(HttpStatus.OK, response1.getStatusCode());
	}
	
	@Test
	public void testNewComment_Success_ReturnOK() throws JsonProcessingException {
		String url = "http://localhost:" + port + "/api/v1/social/post/newcomment";
		Long postId = postRepository.findFirstByOrderByIdDesc().get().getId();
		CommentRequest commentRequest = new CommentRequest(postId,"ok find" + date);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(commentRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
	}
	
	@Test
	public void testNewLike_SuccessReturnOk() throws JsonProcessingException {
		String url = "http://localhost:" + port + "/api/v1/social/post/like";
		Long postId = postRepository.findFirstByOrderByIdDesc().get().getId();
		LikeRequest likeRequest = new LikeRequest(postId,true);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(likeRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testDeletePost_Success_ReturnOkNoContent() {
		Image image = imageRepository.findFirstByImageStartsWithOrderByIdDesc(USERNAME); 
		String url = "http://localhost:" + port + "/api/v1/social/post/deletepost/"+image.getPost().getId();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testDeletePost_Fail_ReturnBadRequest() {
		String url = "http://localhost:" + port + "/api/v1/social/post/deletepost/454545353";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testDeleteComment_Success_ReturnOk() {
		
		Comment comment = commentRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new RuntimeException("no comment"));
		
		String url = "http://localhost:" + port + "/api/v1/social/post/deletecomment/"+comment.getId();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testDeleteComent_Fail_ReturnBadRequest() {
		String url = "http://localhost:" + port + "/api/v1/social/post/deletecomment/454545353";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
