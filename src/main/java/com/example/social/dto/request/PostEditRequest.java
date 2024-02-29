package com.example.social.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEditRequest {
	
	private String content;	
	private List<Long> idImage;
}
