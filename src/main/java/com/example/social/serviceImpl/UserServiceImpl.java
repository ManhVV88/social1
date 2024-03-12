package com.example.social.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.social.dto.request.AcceptAddFriendRequest;
import com.example.social.dto.request.UpdateRequest;
import com.example.social.dto.response.CommentTimeLineRespone;
import com.example.social.dto.response.ImageTimeLineRespone;
import com.example.social.dto.response.MessageResponse;
import com.example.social.dto.response.PostTimeLineRespone;
import com.example.social.entity.Comment;
import com.example.social.entity.Friend;
import com.example.social.entity.Image;
import com.example.social.entity.Job;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.CommentRepository;
import com.example.social.repository.FriendRepository;
import com.example.social.repository.JobRepository;
import com.example.social.repository.LikeRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.service.FileService;
import com.example.social.service.UserService;
import com.example.social.utils.CommonContants;
import com.example.social.utils.JwtUtils;

import net.coobird.thumbnailator.Thumbnails;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	FileService fileService;

//	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	/**
	 * 
	 * @param authentication
	 * @return info user
	 */
	@Override
	public ResponseEntity<?> infoUser(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));

		return ResponseEntity.ok(user);
	}

	/**
	 * 
	 * @param authentication
	 * @return list post of friend
	 */
	@Override
	public ResponseEntity<?> home(Authentication authentication, int page, int size) {
		User user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		Long userId = user.getId();
		List<Long> listFriendId = friendRepository.getUserIdByUserIdInvited(userId);
		listFriendId.addAll(friendRepository.getUserIdInvitedByUserId(userId));
		listFriendId.add(userId);

		if (listFriendId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.NO_POST_YOUR_FRIEND));
		}

		List<User> listFriend = userRepository.findAllById(listFriendId);
		PageRequest pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
		List<Post> listPost = postRepository.findByUserIdInOrderByCreateDateDesc(listFriendId, pageable);

		if (listPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.NO_POST_YOUR_FRIEND));
		}

		List<PostTimeLineRespone> listPostTimeLine = getListPostTimeLineRespone(listPost, listFriend);
//		List<PostTimeLineRespone> listPostTimeLine2 = postRepository.findAllByUserIdInOrderByCreateDateDesc(listFriendId, pageable);
//		System.out.println("listPostTimeLine2"+listPostTimeLine2);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listPostTimeLine);
	}

	/**
	 * 
	 * @param image
	 * @return ImageTimeLineRespone
	 */
	private ImageTimeLineRespone setImageTimeLine(Image image) {
		ImageTimeLineRespone imageTLRespone = new ImageTimeLineRespone();
		imageTLRespone.setId(image.getId());

//		byte[] imageTL = getImageFromFolder(image.getImage(), "image");

		imageTLRespone.setImage(image.getImage());
		imageTLRespone.setPostId(image.getPost().getId());
		return imageTLRespone;
	}

	/**
	 * 
	 * @param post
	 * @param listImageTLRespone
	 * @param user
	 * @return PostTimeLineRespone
	 */
	private PostTimeLineRespone setPostTimeLineRespone(Post post, List<ImageTimeLineRespone> listImageTLRespone,
			User user, List<CommentTimeLineRespone> listComment) {
		PostTimeLineRespone postTLRes = new PostTimeLineRespone();
//		byte[] avatarUser = getImageFromFolder(user.getAvatar(), "Avatar");

		postTLRes.setAvatar(user.getAvatar());
		postTLRes.setContent(post.getContent());
		postTLRes.setCreateDate(post.getCreateDate());
		postTLRes.setImages(listImageTLRespone);
		postTLRes.setPostId(post.getId());
		postTLRes.setTitle(post.getTitle());
		postTLRes.setUserId(post.getUserId());
		postTLRes.setUsername(user.getUsername());

//		List<CommentTimeLineRespone> listComment = new ArrayList<>();
//		listComment = commentRepository.getListCommentWithsUserByPostId(post.getId());	

		postTLRes.setComments(listComment);
		return postTLRes;
	}

	/**
	 * 
	 * @param listPost
	 * @param listFriend
	 * @return List<PostTimeLineRespone>
	 */
	private List<PostTimeLineRespone> getListPostTimeLineRespone(List<Post> listPost, List<User> listFriend) {
		List<PostTimeLineRespone> listPostTimeLine = new ArrayList<>();

		for (Post post : listPost) {
			List<ImageTimeLineRespone> listImageTLRespone = new ArrayList<>();
			for (Image images : post.getImages()) {
				listImageTLRespone.add(setImageTimeLine(images));
			}

			List<CommentTimeLineRespone> listCommentTLResponse = new ArrayList<>();
			for (Comment comment : post.getComment()) {
				listCommentTLResponse.add(setCommentTL(comment));
			}
			// lấy user trong listFriend theo userId của post
			User userTL = (User) listFriend.stream().filter(obj -> obj.getId().equals(post.getUserId())).findFirst()
					.orElse(null);
			listPostTimeLine.add(setPostTimeLineRespone(post, listImageTLRespone, userTL, listCommentTLResponse));

		}
		return listPostTimeLine;
	}

	private CommentTimeLineRespone setCommentTL(Comment comment) {
		CommentTimeLineRespone cmTLResponse = new CommentTimeLineRespone();
		//byte[] avatarUser = getImageFromFolder(comment.getUser().getAvatar(), "Avatar");

		cmTLResponse.setAvatar(comment.getUser().getAvatar());
		cmTLResponse.setComment(comment.getComment());
		cmTLResponse.setId(comment.getId());
		cmTLResponse.setPostId(comment.getPost().getId());
		cmTLResponse.setUserId(comment.getUser().getId());
		cmTLResponse.setUsername(comment.getUser().getUsername());
		return cmTLResponse;
	}

	/**
	 * 
	 * @param authentication
	 * @param multipartFile
	 * @return OK if update success
	 */
	@Override
	public ResponseEntity<?> updateUser(Authentication authentication, MultipartFile multipartFile,
			UpdateRequest userRequestUpdate) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		String mesValidate = validateUpdateRequest(userRequestUpdate, multipartFile);
		if (!mesValidate.equals("")) {
			return ResponseEntity.badRequest().body(new MessageResponse(mesValidate));
		}

		Date dateBirthDay = null;
		if (!userRequestUpdate.getDateOfBirth().isBlank()) {
			dateBirthDay = convertStringDateToDate(userRequestUpdate.getDateOfBirth());
			if (!checkDateIsPast(dateBirthDay)) {
				return ResponseEntity.badRequest().body(new MessageResponse("dateOfBirth must be a past date"));
			}
		}

		if (multipartFile != null && !multipartFile.isEmpty()) {
			if (!isImage(multipartFile)) {
				return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only JPEG images are allowed");
			}

			String fileName = multipartFile.getOriginalFilename();
			String fileExtension = FilenameUtils.getExtension(fileName);
			String avatarName = username + "." + fileExtension;

			if (uploadImage(multipartFile, avatarName)) {
				user.setAvatar(avatarName);
			}
		}
		setUser(user, userRequestUpdate, dateBirthDay);

		userRepository.save(user);
		return ResponseEntity.ok().body(new MessageResponse(CommonContants.UPDATE_INFO_OK));
	}

	private boolean uploadImage(MultipartFile multipartFile, String fileName) {

		try {
			String projectPath = System.getProperty(CommonContants.USER_DIR);

			File projectDirectory = new File(projectPath);

			// Lấy đường dẫn tương đối của một thư mục con trong dự án
			String relativePath = getRelativePath(projectDirectory, CommonContants.PATH_AVATAR);

			Path uploadPath = Paths.get(relativePath);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			InputStream inputStream = multipartFile.getInputStream();
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean isImage(MultipartFile multipartFile) {

		if (!multipartFile.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)
				&& !multipartFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
			return false;
		}
		try {
			InputStream input = multipartFile.getInputStream();
			ImageIO.read(input).toString();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private boolean checkDateIsPast(Date date) {
		Date dateCurrent = new Date();
		if (date.after(dateCurrent)) {
			return false;
		}
		return true;
	}

	private String validateUpdateRequest(UpdateRequest updateRequest, MultipartFile multipartFile) {
		String message = "";
		if (checkAllNull(updateRequest, multipartFile)) {
			message = "There must be at least one field entered or file selected";
		} else {
			if (!updateRequest.getDateOfBirth().isBlank() && !checkDateFormat(updateRequest.getDateOfBirth())) {
				message = "DateOfBirth invalid , DateOfBirth must be format yyyy-mm-dd";
			} else if (updateRequest.getJob() != null && !jobRepository.existsById(updateRequest.getJob())) {
				message = "job not exist";
			}
		}
		return message;
	}

	private boolean checkAllNull(UpdateRequest updateRequest, MultipartFile multipartFile) {
		return updateRequest.getAddress().isBlank() && updateRequest.getDateOfBirth().isBlank()
				&& updateRequest.getFirstName().isBlank() && updateRequest.getJob() == null
				&& updateRequest.getLastName().isBlank() && (multipartFile == null || multipartFile.getSize() == 0);
	}

	private boolean checkDateFormat(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);

		try {
			dateFormat.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public void removeFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 
	 * @param authentication
	 * @param userRequestUpdate
	 * @return OK if update success
	 */
//	@Override
//	public ResponseEntity<?> updateInfoUser(Authentication authentication, UpdateRequest userRequestUpdate) {
//		try {
//			User user = userRepository.findByUsername(authentication.getName())
//					.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
//			setUser(user, userRequestUpdate);
//			userRepository.save(user);
//			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.UPDATE_INFO_OK));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.UPDATE_INFO_OK));
//		}
//	}

	/**
	 * 
	 * @param authentication
	 * @param toUser
	 * @return
	 */
	@Override
	public ResponseEntity<?> addFriend(Authentication authentication, Long toUser) {
		User user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		Long userId = user.getId();
		
		User userInvited = userRepository.findById(toUser)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		Long userInvitedId = userInvited.getId();
		if(userId.equals(userInvitedId)) {
			return ResponseEntity.badRequest().body(new MessageResponse("You cann't send invitations to yourself"));
		}
		// nếu đã tồn tại lời mời trc đó thì không thể gửi tiếp
		Friend checkAddFriend = friendRepository.findByUserIdAndUserIdInvited(userId, userInvitedId);
//		if (friendRepository.existsByUserIdAndUserIdInvited(userId, userInvitedId)) {
//			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.SEND_FAIL_BY_ISEXIST));
//		}
		if(checkAddFriend != null ) {
			if(checkAddFriend.getStatusAccept() != CommonContants.HAD_ACCEPT) {
				return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.SEND_FAIL_BY_ISEXIST));
			} else {
				return ResponseEntity.badRequest().body(new MessageResponse(String.format(CommonContants.HAD_FRIEND_MES,
						userInvited.getUsername())));
			}
		}

		// nếu UserA đã gửi lời mời tới UserB, sau đó UserB gửi lời mời tới UserA
		Friend addFriend = friendRepository.findByUserIdAndUserIdInvited(userInvitedId, userId);
		if (addFriend != null) {
			if (addFriend.getStatusAccept() != CommonContants.HAD_ACCEPT) {
				addFriend.setStatusAccept(CommonContants.ACCEPT);
				addFriend.setUpdateDate(jwtUtils.getDateTimeCurrent());
				friendRepository.save(addFriend);
				return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.ACCEPT_MES));
			} else {
				return ResponseEntity.badRequest().body(new MessageResponse(String.format(CommonContants.HAD_FRIEND_MES,
						userInvited.getUsername())));
			}
		} else {
			addFriend = new Friend();
			addFriend.setUserId(userId);
			addFriend.setUserIdInvited(userInvitedId);
			addFriend.setUpdateDate(jwtUtils.getDateTimeCurrent());
			friendRepository.save(addFriend);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new MessageResponse(String.format(CommonContants.SEND_SUCCESS, userInvited.getUsername())));
		}

	}

	/**
	 * 
	 * @param authentication
	 * @param acceptRequest
	 * @return
	 */
	@Override
	public ResponseEntity<?> acceptAddFriend(Authentication authentication, AcceptAddFriendRequest acceptRequest,
			Long userIdSendInvite) {

		Long userId = userRepository.findByUsername(authentication.getName()).get().getId();

		User userSendInvite = userRepository.findById(userIdSendInvite)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));

		Friend addFriend = friendRepository.findByUserIdAndUserIdInvited(userSendInvite.getId(), userId);
		if (addFriend != null) {
			String mes = Accept(addFriend, acceptRequest, userSendInvite, userId);
			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(mes));
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.INVITE_NOT_EXIST));
		}
	}

	/**
	 * 
	 * @param authentication
	 * @return file
	 */
	@Override
	public ResponseEntity<?> getStatistical(Authentication authentication) {
		User user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		Long userId = user.getId();
		String currentDate = jwtUtils.getSystemDateCurrnet();
		String fileName = user.getUsername() + "_" + currentDate + ".xlsx";
		String filePath = getAbsolutePath(userId, fileName);
		try {
			Resource resource = new UrlResource("file:" + filePath);

			// Kiểm tra xem file có tồn tại không
			if (resource.exists()) {
				// Thiết lập các header và thuộc tính cho phản hồi
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

				// Trả về phản hồi (ResponseEntity) chứa file để tải xuống
				return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
						.contentType(MediaType
								.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
						.body(resource);
			} else {
				// Xử lý khi file không tồn tại
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}

	}

	/**
	 * 
	 * @param userId
	 * @param fileName
	 * @return absolutepath
	 */
	private String getAbsolutePath(Long userId, String fileName) {
		Calendar calendar = Calendar.getInstance();
		Date endDate = calendar.getTime();
		calendar.add(Calendar.WEEK_OF_YEAR, -2);
		Date startDate = calendar.getTime();
		Long countPostInWeek = postRepository.countNewPostInWeek(startDate, endDate, userId);
		Long countLikeInWeek = likeRepository.countNewLikeInWeek(startDate, endDate, userId);
		Long countFriendInWeek = friendRepository.countNewFriendInWeek(startDate, endDate, userId);
		Long countCommentInWeek = commentRepository.countNewCommentInWeek(startDate, endDate, userId);
		// Tạo một workbook mới
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Tạo một trang trong workbook
		XSSFSheet sheet = workbook.createSheet("Report");

		// Tạo dữ liệu cho biểu đồ
		Object[][] data = { { "Report In Week", "Number" }, { "Post", countPostInWeek }, { "Like", countLikeInWeek },
				{ "Friend", countFriendInWeek }, { "Comment", countCommentInWeek } };

		// Thêm dữ liệu vào sheet
		int rowCount = 0;
		for (Object[] rowData : data) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			for (Object cellData : rowData) {
				Cell cell = row.createCell(columnCount++);
				if (cellData instanceof String) {
					cell.setCellValue((String) cellData);
				} else if (cellData instanceof Long) {
					cell.setCellValue((Long) cellData);
				}
			}
		}

		String filePath = "";
		// Lưu workbook vào file
		File file = new File(fileName);
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			workbook.write(outputStream);
			filePath = file.getAbsolutePath();
			workbook.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 
	 * @param addFriend
	 * @param acceptRequest
	 * @param userSendInvite
	 * @param userId
	 * @return message for accept invite or ignore invite
	 */
	private String Accept(Friend addFriend, AcceptAddFriendRequest acceptRequest, User userSendInvite, Long userId) {
		String mes = "";
		if (acceptRequest.isAccept()) {
			if (addFriend.getStatusAccept() == CommonContants.HAD_ACCEPT) {
				mes = String.format(CommonContants.HAD_FRIEND_MES, userSendInvite.getUsername());
			} else {
				addFriend.setStatusAccept(CommonContants.ACCEPT);
				friendRepository.save(addFriend);
				mes = CommonContants.ACCEPT_MES;
			}
		} else {
			if (addFriend.getStatusAccept() == CommonContants.HAD_ACCEPT) {
				mes = String.format(CommonContants.HAD_FRIEND_MES, userSendInvite.getUsername());
			} else {
				mes = CommonContants.IGNORE_MES;
				friendRepository.deleteByUserIdAndUserIdInvited(userSendInvite.getId(), userId);
			}
		}
		return mes;
	}

	/**
	 * 
	 * @param baseDirectory
	 * @param subdirectory
	 * @return relative path
	 */
	private static String getRelativePath(File baseDirectory, String subdirectory) {
		File subdirectoryFile = new File(baseDirectory, subdirectory);
		return subdirectoryFile.getPath();
	}

	/**
	 * SET fields for user
	 * 
	 * @param user
	 * @param userRequest
	 */
	private void setUser(User user, UpdateRequest userRequest, Date dateBirthDay) {

		if (userRequest.getJob() != null) {
			Job jobUser = jobRepository.findById(userRequest.getJob())
					.orElseThrow(() -> new RuntimeException(CommonContants.E_JOB));
			user.setJobId(jobUser.getJobId());
		}
		if (!userRequest.getFirstName().isBlank()) {
			user.setFirtName(userRequest.getFirstName());
		}
		if (!userRequest.getLastName().isBlank()) {
			user.setLastName(userRequest.getLastName());
		}
		if (!userRequest.getAddress().isBlank()) {
			user.setAddress(userRequest.getAddress());
		}
		if (dateBirthDay != null) {
			user.setBirthDay(dateBirthDay);
		}

	}

	private Date convertStringDateToDate(String dateString) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = inputFormat.parse(dateString);
			return date;
		} catch (ParseException e) {
			return date;
		}
	}

	@Override
	public ResponseEntity<?> getImage(Authentication authentication,String image,String path) {
	
		String imageUsername = "";
		if(image!= null ) {
			imageUsername =image;
		}else {
			imageUsername = userRepository.findByUsername(authentication.getName()).get().getAvatar();
		}
		byte[] avatar = getImageFromFolder(imageUsername, path);
		if (avatar == null) {
			return ResponseEntity.notFound().build();
		}
		byte[] resizedAvatar = null;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(avatar);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			Thumbnails.of(inputStream).size(120, 120).outputFormat("jpg") // Định dạng đầu ra là JPEG
					.toOutputStream(outputStream);
			resizedAvatar = outputStream.toByteArray();
		} catch (IOException e) {
			resizedAvatar = avatar;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<>(resizedAvatar, headers, HttpStatus.OK);
	}

	private byte[] getImageFromFolder(String imageUsername, String folderName) {
		byte[] avatarData = null;
		String projectPath = System.getProperty(CommonContants.USER_DIR);

		File projectDirectory = new File(projectPath);
		String relativePath = getRelativePath(projectDirectory, CommonContants.PATH + folderName + "/" + imageUsername);

		Path filePath = Paths.get(relativePath);

		try {
			avatarData = Files.readAllBytes(filePath);
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
		return avatarData;
	}

	@Override
	public ResponseEntity<?> disableUser(String username) {
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		
		user.setEnable(0);
		
		userRepository.save(user);
		
		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("disable user succes"));
	}
	
	@Override
	public ResponseEntity<?> enableUser(String username) {
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		
		user.setEnable(1);
		
		userRepository.save(user);
		
		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("enable user succes"));
	}
}
