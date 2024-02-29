package com.example.social;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.entity.User;
import com.example.social.repository.UserRepository;
import com.example.social.utils.JwtUtils;

@SuppressWarnings("rawtypes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminController {
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;
	
	private static String USERNAME = "vanmanhvn2@gmail.com";

	private static String PASSWORD = "123";

	private static String token;

	@BeforeAll
	public void setUp() {

		ResponseEntity<Map> resData = getSetupData(USERNAME, PASSWORD);

		token = resData.getBody().get("authToken").toString();
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
	public void testAdmin_Success_ReturnOk() {
		String url = "http://localhost:" + port + "/api/v1/social/admin/info/" + USERNAME;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<User> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<User>() {
				});

		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = response.getBody();
		assertEquals(User.class, user.getClass());
	}
	
	@Test
	public void testUser_Fail_ReturnForbiden() {
		
		ResponseEntity<Map> resData = getSetupData("202401241716522@gmail.com", PASSWORD);
		
		String url = "http://localhost:" + port + "/api/v1/social/admin/info/" + USERNAME;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + resData.getBody().get("authToken").toString());

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,String.class);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
}
