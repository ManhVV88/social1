package com.example.social.dto.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostTimeLineRespone {
	private Long postId;
	private String title;
	private List<ImageTimeLineRespone> images = new ArrayList<>();
	private Long userId;
	private String content;
	private Date createDate;
	private byte[] avatar;
	private String username;
	private List<CommentTimeLineRespone> comments = new ArrayList<>();	
	
	
	
}
