package com.example.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.social.dto.request.ChangPassRequest;
import com.example.social.dto.request.ForgotPasswordRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTests {


	@LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;
   
    @Autowired
	JwtUtils jwtUtils;
    
    private static String USERNAME = "vanmanhvn@gmail.com";    
    
    private static String PASSWORD = "123";
    
    @SuppressWarnings("unused")
	private static String token;
    
    @BeforeAll
    public void setUp() {
    	
    	ResponseEntity<Map> resData = getSetupData();    	
    	
    	token = resData.getBody().get("authToken").toString();
    }
    
    private ResponseEntity<Map> getSetupData() {
    	String url = "http://localhost:" + port + "/api/v1/auth/signin";
        
        LoginRequest loginRequest = new LoginRequest(USERNAME, "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity(url, loginRequest, Map.class);
        
        String url2 = "http://localhost:" + port + "/api/v1/auth/verifyOtp";   	
    	OtpRequest otpRequest = new OtpRequest(response.getBody().get("otp").toString(),USERNAME);
    	
    	ResponseEntity<Map> response1 = testRestTemplate.postForEntity(url2, otpRequest, Map.class);
    	return response1;
    }
    
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void testAuthenticateUser_SuccessfulAuthentication_ReturnsOkResponseWithOtp() {
        String url = "http://localhost:" + port + "/api/v1/auth/signin";
       
        LoginRequest loginRequest = new LoginRequest(USERNAME, PASSWORD);
        ResponseEntity<Map> response = testRestTemplate.postForEntity(url, loginRequest, Map.class);
        String otp = response.getBody().get("otp").toString();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());      
        assertEquals(6, otp.length());
        
        
        String url2 = "http://localhost:" + port + "/api/v1/auth/verifyOtp"; 
        
    	OtpRequest otpRequest = new OtpRequest(otp,USERNAME);    	
    	ResponseEntity<Map> response1 = testRestTemplate.postForEntity(url2, otpRequest, Map.class);
    	ArrayList<Map<String,String>> role =    (ArrayList<Map<String, String>>) response1.getBody().get("role");
    	
    	assertEquals(HttpStatus.OK, response1.getStatusCode());
    	assertEquals("vanmanhvn@gmail.com", response1.getBody().get("username"));   	
    	assertEquals("ROLE_USER", role.get(0).get("authority"));
    	assertEquals("Bearer", response1.getBody().get("typeToken"));
    	
    	assertTrue(jwtUtils.validateJwtToken(response1.getBody().get("authToken").toString()));
    }    
    
    
    @Test
    public void testVerifyOtp_notValid_ReturnBadRequest() {   	
        
        String url2 = "http://localhost:" + port + "/api/v1/auth/verifyOtp"; 
        
    	OtpRequest otpRequest = new OtpRequest("123456",USERNAME);    	
    	ResponseEntity<String> response1 = testRestTemplate.postForEntity(url2, otpRequest, String.class);
    	
    	assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
    	
    }
    
    @Test
    public void testVerifyOtp_NotFoundUser_ReturnBadRequest() {
    	String url = "http://localhost:" + port + "/api/v1/auth/signin";
        
        LoginRequest loginRequest = new LoginRequest(USERNAME, PASSWORD);
        ResponseEntity<Map> response1 = testRestTemplate.postForEntity(url, loginRequest, Map.class);
        
        String otp = response1.getBody().get("otp").toString();
    	String url2= "http://localhost:" + port + "/api/v1/auth/verifyOtp"; 
    	OtpRequest otpRequest = new OtpRequest(otp,"123556@gmail.com");    	
    	ResponseEntity<String> response = testRestTemplate.postForEntity(url2, otpRequest, String.class);
    	
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testRegisterUser_Success_ReturnOk() {
    	String url = "http://localhost:" + port + "/api/v1/auth/signup";
    	String date = jwtUtils.getSystemDateCurrnet();
		String username = date+"@gmail.com";
    	LoginRequest signUpRequest = new LoginRequest(username, PASSWORD);
    	
    	ResponseEntity<String> response = testRestTemplate.postForEntity(url, signUpRequest, String.class);
    	assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    public void testRegisterUser_Exist_ReturnBadRequest() {
    	String url = "http://localhost:" + port + "/api/v1/auth/signup";
    	LoginRequest signUpRequest = new LoginRequest(USERNAME, PASSWORD);
    	
    	ResponseEntity<String> response = testRestTemplate.postForEntity(url, signUpRequest, String.class);
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testforgotPasswordAndChangePassword_Succes_ReturnOk() throws JsonProcessingException {
    	String url = "http://localhost:" + port + "/api/v1/auth/forgotpassword";
    	
    	ForgotPasswordRequest requestUser = new ForgotPasswordRequest("20240124100500@gmail.com");
    	ResponseEntity<Map> response = testRestTemplate.postForEntity(url, requestUser, Map.class);
    	
    	String tokenPass = response.getBody().get("tokenReset").toString();
    	
    	System.out.println("tokenPass"+ tokenPass);
    	
//    	String urlChangPassword = "http://localhost:" + port + "/api/v1/auth/changpassword";
    	
    	ChangPassRequest changePass = new ChangPassRequest("456","456");    	
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(changePass);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> resChange = testRestTemplate.exchange(tokenPass, HttpMethod.PUT, requestEntity, String.class);
    	
    	assertEquals(HttpStatus.OK, resChange.getStatusCode()); 
    	
    }
    
//    @Test
//    public void testChangePassword_NullToken_ReturnBadRequest() throws JsonProcessingException {
//    	String urlChangPassword = "http://localhost:" + port + "/api/v1/auth/changpassword";
//    	
//    	ChangPassRequest changePass = new ChangPassRequest("456","654");    
//    	
//    	HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String requestBody = mapper.writeValueAsString(changePass);
//
//		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//		ResponseEntity<String> resChange = testRestTemplate.exchange(urlChangPassword, HttpMethod.PUT, requestEntity, String.class);
//    	
//    	assertEquals(HttpStatus.BAD_REQUEST, resChange.getStatusCode()); 
//    }
    
    @Test
    public void testChangePassword_NotExistToken_ReturnBadRequest() throws JsonProcessingException {
    	String urlChangPassword = "http://localhost:" + port + "/api/v1/auth/changpassword/854987359";
    	
    	ChangPassRequest changePass = new ChangPassRequest("456","654");    
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(changePass);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> resChange = testRestTemplate.exchange(urlChangPassword, HttpMethod.PUT, requestEntity, String.class);
    	
    	assertEquals(HttpStatus.BAD_REQUEST, resChange.getStatusCode());    	
    }
    
    @Test
    public void testChangePassword_OldNewPassNotMatching_ReturnBadRequest() throws JsonProcessingException {
    	String url = "http://localhost:" + port + "/api/v1/auth/forgotpassword";
    	
    	ForgotPasswordRequest requestUser = new ForgotPasswordRequest("20240124100500@gmail.com");
    	ResponseEntity<Map> response = testRestTemplate.postForEntity(url, requestUser, Map.class);
    	
    	String tokenPass = response.getBody().get("tokenReset").toString();
    	
//    	String urlChangPassword = "http://localhost:" + port + "/api/v1/auth/changpassword";    	
    	
    	ChangPassRequest changePass = new ChangPassRequest("456","654");    
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(changePass);

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> resChange = testRestTemplate.exchange(tokenPass, HttpMethod.PUT, requestEntity, String.class);
    	
    	assertEquals(HttpStatus.BAD_REQUEST, resChange.getStatusCode());    	
    }
    @Test
	public void testUnAuthorize_ReturnUnAuthorize() {
		String url = "http://localhost:" + port + "/api/v1/social/user/info/" + USERNAME;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " );

		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity,String.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		
	}
    
    @Test
    public void TestMethodArgumentNotValidException_ReturnBadRequest() {
    	String url = "http://localhost:" + port + "/api/v1/auth/signin";
        
        LoginRequest loginRequest = new LoginRequest("vanmanhvn", PASSWORD);
        ResponseEntity<String> response = testRestTemplate.postForEntity(url, loginRequest, String.class);    	
    	assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
}
