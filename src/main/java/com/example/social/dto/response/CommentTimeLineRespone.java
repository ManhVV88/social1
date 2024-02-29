package com.example.social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentTimeLineRespone {

	private Long id;
	private String comment;
	private Long postId;
	private Long userId;
	private String username;
	private byte[] avatar;
	
}
