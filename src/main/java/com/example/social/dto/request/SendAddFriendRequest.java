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
//	@Pattern(regexp = "^[1-9][0-9]*$",message = "value not matching")
	@Min(1)
	private Long toUser;
}
