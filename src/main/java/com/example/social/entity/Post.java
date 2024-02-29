package com.example.social.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonFormat
@Table(name = "post")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title")
	private String title;

	@OneToMany(mappedBy = "post",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Image> images = new HashSet<>();

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "content")
	private String content;
	
	@Column(name = "create_date")
	private Date createDate;
	
	@OneToMany(mappedBy = "post",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Comment> comment = new HashSet<>();

}
