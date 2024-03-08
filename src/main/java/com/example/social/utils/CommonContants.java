package com.example.social.utils;

public class CommonContants {
	public static final String PATH_AVATAR = "src/main/resources/Avatar";
	public static final String PATH = "src/main/resources/";
	
	public static final String E_USER_NOT_FOUND = "Error: User is not found.";
	public static final String E_USER_IS_EXIST = "Error: Username is already taken!"; 
	public static final String E_JOB = "Error: Job haven't exist";
	public static final String USER_DIR = "user.dir";
	public static final String USER_SIGNUP_SUCCESS = "User registered successfully!";
	//update user
	public static final String UPDATE_AVATAR_OK = "update avatar ok";
	public static final String UPDATE_AVATAR_FAIL = "can't update avatar";
	public static final String UPDATE_INFO_OK = "update info user ok";
	public static final int IS_ENABLE = 1 ;
	public static final int PAGE_SIZE = 10 ;
	
	// accept addfriend
	public static final int HAD_ACCEPT = 1 ;
	public static final int ACCEPT = 1 ;
	public static final String ACCEPT_MES = "u are did accept invite";
	public static final String IGNORE_MES = "u are did ignore invite";
	public static final String HAD_FRIEND_MES = "You and %s are already friends";
	public static final String INVITE_NOT_EXIST = "invite not exist";
	
	
	// send invite addfriend
	public static final String SEND_FAIL_BY_USER = "can't send invite because user doesn't exist";
	public static final String SEND_FAIL_BY_ISEXIST = "can't send invite because you has sent before";
	public static final String SEND_SUCCESS = "u had sent invite to %s";
	
	//post
	public static final String E_POST_NOT_EXIST = "Error: post not exist";
	public static final String E_POST_EDIT_FAIL = "Can't edit post";
	public static final String E_POST_REMOVE_IMAGE = "Can't remove image";
	public static final String E_POST_UPLOAD_IMAGE = "can't upload image post";
	public static final String POST_EDIT_OK = "Edit post ok ";
	public static final String POST_CREATE_OK = "Create new post ok ";
	public static final String POST_COMMENT_OK = "Comment success ";
	public static final String NO_POST_YOUR_FRIEND = "no post your friend";
	public static final String PATH_IMAGE = "src/main/resources/image";
	public static final String DEL_COM_SUCCESS = "delete comment success";
	public static final String DEL_COM_FAIL = "delete comment fail , id not exist";
	
	public static final String DEL_POST_SUCCESS = "delete post success";
	public static final String DEL_POST_FAIL = "delete post fail , id not exist";
	//otp
	public static final String E_OTP_IS_VALID = "Error: otp isvalid!";
	
	//role 
	public static final String E_ROLE_NOT_FOUND = "Error: Role is not found.";
	
	//password 
	public static final String E_PASS_NO_ENTER_EMAIL ="Error: you haven't enter email";
	public static final String E_PASS_TOKEN_NOT_EXIST ="Error: token dosen't exist";
	public static final String E_PASS_TOKEN_HAD_EXPIRY ="Error: token has expiry date";
	public static final String E_PASS_NO_MATCHING ="Error: password no matching";
	public static final String PASS_CHANGE_SUCCESS ="password change success";
	
	//file 
	public static final String E_FILE_FAIL = "Only JPEG images are allowed.";
	public static final String CALLL_FROM_CLIENT = "CALL_BY_CLIENT";
	public static final String CALLL_FROM_CTL = "CALL_BY_CONTROLLER";
}
