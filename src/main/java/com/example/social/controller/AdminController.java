package com.example.social.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.social.service.UserService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	
	@Autowired
	UserService userService;
	
	
	@PutMapping("/disable/{username}")
	ResponseEntity<?> disableUser(@PathVariable String username)
	{
		return userService.disableUser(username);
	}
	
	@PutMapping("/enable/{username}")
	ResponseEntity<?> enableUser(@PathVariable String username)
	{
		return userService.enableUser(username);
	}

	@PutMapping("/roles/{userId}")
	ResponseEntity<?> updateRole(@PathVariable(value="userId") @Valid @Min(1) Long userId,@RequestParam(value="roleId") @Valid @Min(1) Long roleId)
	{
		return userService.updateRole(userId,roleId,SecurityContextHolder.getContext().getAuthentication());
	}
	
}
