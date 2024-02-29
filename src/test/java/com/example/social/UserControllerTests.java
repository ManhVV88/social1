package com.example.social;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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

import com.example.social.dto.request.AcceptAddFriendRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.dto.request.SendAddFriendRequest;
import com.example.social.dto.request.UpdateRequest;
import com.example.social.entity.User;
import com.example.social.repository.UserRepository;
import com.example.social.utils.CommonContants;
import com.example.social.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;
	
	private static String USERNAME = "vanmanhvn@gmail.com";

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
	public void testInfoUser_Success_ReturnOK() {
		String url = "http://localhost:" + port + "/api/v1/social/user/info/" + USERNAME;

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
	public void testInfoUser_NotFound_ReturnBadRequest() {
		String url = "http://localhost:" + port + "/api/v1/social/user/info/2321312@gmail.com";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testUpdateAvatar_Success_ReturnOK() throws IOException {
		String url = "http://localhost:" + port + "/api/v1/social/user/upload-avatar";
		// Tạo tệp giả lập
		MockMultipartFile file = new MockMultipartFile("avatar", "avatar.png", MediaType.IMAGE_PNG_VALUE,
				"avatar content".getBytes());

		// Tạo phần thân yêu cầu
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("avatar", new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		});

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testUpdateUser_Success_ReturnOk() throws ParseException, JsonProcessingException {
		String url = "http://localhost:" + port + "/api/v1/social/user/update-user";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		String dateString = "1988-12-02";

		UpdateRequest bodyUpdateRequest = new UpdateRequest("Vũ", "Mạnh", dateString, 1,
				"Vĩnh Thọ - Hương Mạc - Từ Sơn - Bắc Ninh");

		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(bodyUpdateRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAddFriend_Success_ReturnOk() throws JsonProcessingException {
		String url = "http://localhost:" + port + "/api/v1/social/user/addfriend";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		Long toUser = (long) 10;

		SendAddFriendRequest sendAddFriendRequest = new SendAddFriendRequest(toUser);

		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(sendAddFriendRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testHadSend_AddFriendSuccess_ReturnOk() throws JsonProcessingException {

		// Userid = 10
		ResponseEntity<Map> resData = getSetupData("20240124131845@gmail.com", "123");

		String url = "http://localhost:" + port + "/api/v1/social/user/addfriend";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + resData.getBody().get("authToken").toString());

		Long toUser = (long) 1;

		SendAddFriendRequest sendAddFriendRequest = new SendAddFriendRequest(toUser);

		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(sendAddFriendRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testHasFriend_AddFriendFail_ReturnBadRequest() throws JsonProcessingException {
		// Userid = 10
		ResponseEntity<Map> resData = getSetupData("20240124131845@gmail.com", PASSWORD);

		String url = "http://localhost:" + port + "/api/v1/social/user/addfriend";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + resData.getBody().get("authToken").toString());

		Long toUser = (long) 1;

		SendAddFriendRequest sendAddFriendRequest = new SendAddFriendRequest(toUser);

		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(sendAddFriendRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testAcceptAddFriend_Success_ReturnOK() throws JsonProcessingException {
		String url = "http://localhost:" + port + "/api/v1/auth/signup";
    	String date = jwtUtils.getSystemDateCurrnet();
		String username = date+"1"+"@gmail.com";
		String username2 = date+"2"+"@gmail.com";
		
    	LoginRequest signUpRequest = new LoginRequest(username, PASSWORD);
    	LoginRequest signUpRequest2 = new LoginRequest(username2, PASSWORD);    	
    	testRestTemplate.postForEntity(url, signUpRequest, String.class);
    	testRestTemplate.postForEntity(url, signUpRequest2, String.class);
    	
    	ResponseEntity<Map> resData = getSetupData(username, PASSWORD);
    	ResponseEntity<Map> resData2 = getSetupData(username2, PASSWORD);
    	/*
    	 * Start send AddFriend
    	 */
    	String url2 = "http://localhost:" + port + "/api/v1/social/user/addfriend";
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + resData.getBody().get("authToken").toString());

		User user = userRepository.findByUsername(username2).orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		SendAddFriendRequest sendAddFriendRequest = new SendAddFriendRequest(user.getId());

		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(sendAddFriendRequest);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		testRestTemplate.exchange(url2, HttpMethod.POST, requestEntity, String.class);
		
		/*
		 * End send AddFriend
		 */
		
		/*
		 * Start accept
		 */
		User user1 = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		String url3 = "http://localhost:" + port + "/api/v1/social/user/accept_addfriend/"+user1.getId();
		
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setContentType(MediaType.APPLICATION_JSON);
		headers1.set("Authorization", "Bearer " + resData2.getBody().get("authToken").toString());
		
		
		AcceptAddFriendRequest accept = new AcceptAddFriendRequest(true);
		
		ObjectMapper mapper1 = new ObjectMapper();
		String requestBody1 = mapper1.writeValueAsString(accept);

		HttpEntity<String> requestEntity1 = new HttpEntity<>(requestBody1, headers1);
		ResponseEntity<String> resAccept = testRestTemplate.exchange(url3, HttpMethod.PUT, requestEntity1, String.class);
		
		assertEquals(HttpStatus.OK, resAccept.getStatusCode());
		/*
		 * End accept
		 */
		
	}
	
	
	@Test
	public void testHome_NoPost_ReturnOk() {
		String url = "http://localhost:" + port + "/api/v1/social/user/home";
		
		ResponseEntity<Map> resData = getSetupData("20240124131845@gmail.com", PASSWORD);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + resData.getBody().get("authToken").toString());

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testHome_Success_ReturnOk() {
		String url = "http://localhost:" + port + "/api/v1/social/user/home";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,String.class);
		System.out.println("response : "+response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testStatistical_Success_ReturnOk() {
		String url = "http://localhost:" + port + "/api/v1/social/user/statistical";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<byte[]> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,byte[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		
	}
	
	@Test
	public void testGetAvatar_success_returnOk() {
		String url = "http://localhost:" + port + "/api/v1/social/user/avatar";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
