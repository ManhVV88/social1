package com.example.social.dto.request;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendAddFriendRequest {
	//@Pattern(regexp = "^[1-9][0-9]*$",message = "toUser must be a valid integer")
	@Min(value = 1 , message = "toUser must be number and value min is 1")
	private Long toUser;
}
