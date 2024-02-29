package com.example.social.serviceImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.social.dto.request.ChangPassRequest;
import com.example.social.dto.request.ForgotPasswordRequest;
import com.example.social.dto.request.LoginRequest;
import com.example.social.dto.request.OtpRequest;
import com.example.social.dto.response.MessageResponse;
import com.example.social.entity.ERole;
import com.example.social.entity.PasswordResetToken;
import com.example.social.entity.Role;
import com.example.social.entity.User;
import com.example.social.repository.PasswordTokenRepository;
import com.example.social.repository.RoleRepository;
import com.example.social.repository.UserRepository;
import com.example.social.service.AuthService;
import com.example.social.utils.CommonContants;
import com.example.social.utils.JwtUtils;

@Service
@Transactional
public class AuthServiceImpl implements AuthService{

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	OtpServiceImpl otpService;

	@Autowired
	PasswordTokenRepository passwordTokenRepository;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	/**
	 * 
	 * @param user
	 * @return 401 if user password not matching and respone otp if username password is matching 
	 */
	@Override
	public ResponseEntity<?> authenticateUser(LoginRequest user) {		
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String otp = otpService.generateOtp(user.getUsername());
			Map<String, Object> response = new HashMap<>();
			response.put("otp", otp);
			return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * @param otpRequest
	 * @return return 400 if user and otp not matching else return token
	 */
	@Override
	public ResponseEntity<?> verifyOtp(OtpRequest otpRequest) {
		String username = otpRequest.getUsername();
		String otp = otpRequest.getOtp();

		boolean isOtpValid = otpService.validateOTP(username, otp);
		if (!isOtpValid) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_OTP_IS_VALID));
		}

		UserDetailsImpl user = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);
		if (user == null) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_USER_NOT_FOUND));
		}
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
				user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		Map<String, Object> response = new HashMap<>();
		response.put("username", user.getUsername());
		response.put("role", user.getAuthorities());
		response.put("authToken", jwt);
		response.put("typeToken", "Bearer");

		return ResponseEntity.ok(response);

	}

	/**
	 * 
	 * @param userForm
	 * @return register user
	 */
	@Override
	public ResponseEntity<?> registerUser(LoginRequest userForm) {
		if (userRepository.existsByUsername(userForm.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_USER_IS_EXIST));
		}

		// Create new user's account
		User userRegister = new User(userForm.getUsername(), encoder.encode(userForm.getPassword()));

		Set<Role> roles = new HashSet<>();

		Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_ROLE_NOT_FOUND));
		roles.add(userRole);

		userRegister.setEnable(CommonContants.IS_ENABLE);
		userRegister.setRoles(roles);
		userRepository.save(userRegister);

		return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(CommonContants.USER_SIGNUP_SUCCESS));
	}

	/**
	 * 
	 * @param requestUser
	 * @return 200 if matching username
	 */
	@Override
	public ResponseEntity<?> forgotPassword(ForgotPasswordRequest requestUser) {
		User user = userRepository.findByUsername(requestUser.getUsername())
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));

		
		
		String token = UUID.randomUUID().toString();
		
		PasswordResetToken myToken = passwordTokenRepository.findByUserId(user.getId());
		if(myToken == null) {
			myToken = new PasswordResetToken(token, user);
		} else {
			myToken.setToken(token);
			myToken.setExpiryDate();
		}
		
				//new PasswordResetToken(token, user);
		passwordTokenRepository.save(myToken);

		Map<String, Object> response = new HashMap<>();
		response.put("tokenReset", "http://localhost:8080/api/v1/auth/changpassword/"+token);
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param requestUser
	 * @return 200 if change pass success
	 */
	@Override
	public ResponseEntity<?> changePassword(String requestToken,ChangPassRequest requestUser) {

		PasswordResetToken requestReserPassword = passwordTokenRepository.findByToken(requestToken);

		if (requestReserPassword == null) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_PASS_TOKEN_NOT_EXIST));
		}

		if (!jwtUtils.checkDate(requestReserPassword.getExpiryDate())) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_PASS_TOKEN_HAD_EXPIRY));
		}

		if (!jwtUtils.checkPass(requestUser.getNewPassword(), requestUser.getConfirmPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_PASS_NO_MATCHING));
		}

		User updateUser = requestReserPassword.getUser();

		updateUser.setPassword(encoder.encode(requestUser.getNewPassword()));

		userRepository.save(updateUser);
		
		passwordTokenRepository.delete(requestReserPassword);

		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.PASS_CHANGE_SUCCESS));
	}

}
