package com.example.social.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "app_role")
@Getter
@Setter
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROLE_ID", nullable = false)
	private Long roleId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE_NAME",length = 20)
	private ERole roleName;

	@Column(name = "ROLE_LEVEL")
	private int level;
}
