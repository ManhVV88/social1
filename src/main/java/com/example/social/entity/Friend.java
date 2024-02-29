package com.example.social.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "friend")
@Getter
@Setter
public class Friend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "user_id_invited")
	private Long userIdInvited;
	
	@Column(name = "status_accept")
	private int statusAccept;
	
	@Column(name = "update_date")
	private Date updateDate;
}
