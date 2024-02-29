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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user", uniqueConstraints = { @UniqueConstraint(columnNames = "USER_NAME"),
		@UniqueConstraint(columnNames = "USER_EMAIL") })
@Getter
@Setter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID", nullable = false)
	private Long id;

	@NotBlank
	@Size(max = 36)
	@Pattern(regexp = "^\\S+@\\S+\\.\\S+$",message = "Invalid email")
	@Column(name = "USER_NAME")
	private String username;

	
	@Size(max = 50)
	@Email
	@Column(name = "USER_EMAIL")
	private String email;

	@NotBlank
	@Size(min = 3 , max = 72, message = "length must be in 3~72 character")
	@Column(name = "ENCRYTED_PASSWORD")
	@JsonIgnore
	private String password;

//  @Pattern(regexp = "^[01]{1}$", message = "value must be 0 or 1")
	@Min(value = 0, message = "value must be 0 or 1")
	@Max(value = 1, message = "value must be 0 or 1")
	@Column(name = "ENABLED")
	private int enable;

	@Size(max = 120)
	@Column(name = "FIRT_NAME")
	private String firtName;

	@Size(max = 120)
	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "BIRTH_DAY")
	private Date birthDay;

	@Size(max = 120)
	@Column(name = "AVATAR")
	private String avatar;

//  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//  @JoinTable(  name = "job", 
//  joinColumns = @JoinColumn(name = "JOB",referencedColumnName = "JOB_ID"))
	@Column(name = "JOB")
	private Integer jobId;

	@Size(max = 120)
	@Column(name = "ADDRESS")
	private String address;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"))
	private Set<Role> roles = new HashSet<>();

	public User(@NotBlank @Size(max = 36) @Email String username, @NotBlank @Size(max = 120) String password) {
		super();
		this.username = username;
		this.password = password;
	}

	// getters and setters
}