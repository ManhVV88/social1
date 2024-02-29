package com.example.social.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
	
	@Min(1)
	private Long postId;
	
	@NotNull(message = "comment is not null")
	private String comment;
}
