package com.example.social.dto.request;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequest {
	
	@Min(1)
	private Long postId;
	
	@JsonProperty
	private boolean like;	
}
