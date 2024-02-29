package com.example.social.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	
	@NotBlank(message = "username haven't blank")
	@Size(max = 36,message = "username lesthan 36 character")
	@Pattern(regexp = "^\\S+@\\S+\\.\\S+$",message = "username must be email format")
	String username;
	
	@NotBlank(message = "password haven't blank")
	@Size(min = 3 , max = 72, message = "password length must be in 3~72 character")
	String password;	
}
