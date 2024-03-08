package com.example.social.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.social.dto.request.CommentRequest;
import com.example.social.dto.request.LikeRequest;
import com.example.social.dto.request.PostEditRequest;
import com.example.social.dto.request.PostRequest;
import com.example.social.dto.response.MessageResponse;
import com.example.social.entity.Comment;
import com.example.social.entity.Image;
import com.example.social.entity.Like;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.CommentRepository;
import com.example.social.repository.ImageRepository;
import com.example.social.repository.LikeRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.service.PostService;
import com.example.social.utils.CommonContants;
import com.example.social.utils.JwtUtils;

@Service
@Transactional
@Component(value="postService")
public class PostServiceImpl implements PostService {

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ImageRepository imageRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	JwtUtils jwtUtils;

	/**
	 * 
	 * @param authentication
	 * @param postRequest
	 * @return HTTP Ok if create post success else INTERNAL_SERVER_ERROR or throw
	 *         RuntimeException
	 */
	@Override
	public ResponseEntity<Object> newPost(Authentication authentication, PostRequest postRequest, MultipartFile[] image) {

		String username = authentication.getName();
		Post post = new Post();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		Set<Image> listImage = new HashSet<>();
		if (image != null) {
			try {
				listImage = getListImageFromRequest(image, username, post);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new MessageResponse(CommonContants.E_POST_UPLOAD_IMAGE));
			}
		}

		post.setImages(listImage);
		post.setContent(postRequest.getContent());
		post.setUserId(user.getId());
		post.setCreateDate(jwtUtils.getDateTimeCurrent());
		postRepository.save(post);
		return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(CommonContants.POST_CREATE_OK));
	}

	/**
	 * 
	 * @param authentication
	 * @param postEditRequest
	 * @return HTTP Ok if edit success else BadRequest
	 */
	@Override
	public ResponseEntity<?> editPost(Authentication authentication, PostEditRequest postEditRequest, Long postId,
			MultipartFile[] imageNew) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
//		Long postId = postEditRequest.getId();
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_POST_NOT_EXIST));

		if (!post.getUserId().equals(user.getId())) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_POST_EDIT_FAIL));
		}
		if (!checkRemoveImage(postEditRequest.getIdImage(), post)) {
			return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.E_POST_REMOVE_IMAGE));
		}

		Set<Image> listImage = new HashSet<>();
		if (imageNew != null && imageNew.length > 0) {
			try {
				listImage = getListImageFromRequest(imageNew, username, post);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new MessageResponse(CommonContants.E_POST_UPLOAD_IMAGE));
			}
		}
		post.setContent(postEditRequest.getContent());
		
		if(listImage!= null) {
			post.setImages(listImage);
		}
		
		postRepository.save(post);

		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(CommonContants.POST_EDIT_OK));
	}

	/**
	 * 
	 * @param authentication Authentication of user
	 * @param commentRequest
	 * @return
	 */
	@Override
	public ResponseEntity<?> newComment(Authentication authentication, CommentRequest commentRequest) {

		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));

		Long postId = commentRequest.getPostId();
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_POST_NOT_EXIST));

		Comment comment = new Comment();
		comment.setUser(user);
		comment.setPost(post);
		comment.setComment(commentRequest.getComment());
		comment.setCreateDate(jwtUtils.getDateTimeCurrent());
		commentRepository.save(comment);

		return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(CommonContants.POST_COMMENT_OK));
	}

	/**
	 * 
	 * @param authentication
	 * @param likeRequest
	 * @return like or unlike
	 */
	@Override
	public ResponseEntity<?> like(Authentication authentication, LikeRequest likeRequest) {

		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));

		Long postId = likeRequest.getPostId();
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_POST_NOT_EXIST));

		Like like = likeRepository.findByPostIdAndUserId(postId, user.getId());

		if (like == null) {
			like = new Like();
			like.setPostId(post.getId());
			like.setUserId(user.getId());
			like.setCreateDate(jwtUtils.getDateTimeCurrent());
		}
		String mes = "";
		if (likeRequest.isLike()) {
			like.setLikeType(1);
			mes = "like success";
		} else {
			like.setLikeType(0);
			mes = "unlike success";
		}

		likeRepository.save(like);
		return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(mes));
	}

	/**
	 * 
	 * @param baseDirectory
	 * @param subdirectory
	 * @return String Relative Path
	 */
	private static String getRelativePath(File baseDirectory, String subdirectory) {
		File subdirectoryFile = new File(baseDirectory, subdirectory);
		return subdirectoryFile.getPath();
	}

	/**
	 * 
	 * @param listImageMultipart
	 * @param username
	 * @param post
	 * @return Set<Image> after file upload seccess
	 * @throws IOException
	 */
	private Set<Image> getListImageFromRequest(MultipartFile[] listImageMultipart, String username, Post post)
			throws IOException {
		Set<Image> listImage = new HashSet<>();		
		
		String projectPath = System.getProperty(CommonContants.USER_DIR);

		File projectDirectory = new File(projectPath);

		// Lấy đường dẫn tương đối của một thư mục con trong dự án
		String relativePath = getRelativePath(projectDirectory, CommonContants.PATH_IMAGE);

		Path uploadPath = Paths.get(relativePath);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		for (MultipartFile picture : listImageMultipart) {
			if (!picture.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) && !picture.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
		        throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, CommonContants.E_FILE_FAIL);
		    }
			Image image = new Image();
			InputStream inputStream = picture.getInputStream();
			String fileName = picture.getOriginalFilename();
			String currentDate = jwtUtils.getSystemDateCurrnet();
			Path filePath = uploadPath.resolve(username + "-" + currentDate + "-" + fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			image.setImage(username + "-" + currentDate + "-" + fileName);
			image.setPost(post);
			listImage.add(image);
		}
		return listImage;
	}

	/**
	 * 
	 * @param listIdImage
	 * @param post
	 * @return true if idImage from request exist in db else false
	 */
	private boolean checkRemoveImage(List<Long> listIdImage, Post post) {
		
		if (listIdImage == null) {
			return true;
		}
		List<Image> listImage = imageRepository.findByPost(post);		
		
		// check qua 1 lượt nếu có id image ko tồn tại vs id trong db thì return false
		for (Long id : listIdImage) {
			if (!listImage.stream().anyMatch(obj -> obj.getId().equals(id))) {
				return false;
			}
		}
		// xóa image có trong list image dc request
		for (Image image : listImage) {
			if (listIdImage.contains(image.getId())) {
				String filePath = CommonContants.PATH_IMAGE+"/"+image.getImage();
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
			}
		}

		imageRepository.deleteAllById(listIdImage);
		return true;
	}

	@Override
	public ResponseEntity<?> deleteComment(Authentication authentication, Long idComment) {
		User user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		if (commentRepository.existsById(idComment)) {
			commentRepository.deleteByIdAndUserId(idComment, user.getId());
			return ResponseEntity.status(HttpStatus.OK)
					.body(new MessageResponse(CommonContants.DEL_COM_SUCCESS));
		}
		return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.DEL_COM_FAIL));
	}

	@Override
	public ResponseEntity<?> deletePost( Long idPost) {		
		if (postRepository.existsById(idPost)) {
			postRepository.deleteById(idPost);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new MessageResponse(CommonContants.DEL_POST_SUCCESS));
		}
		return ResponseEntity.badRequest().body(new MessageResponse(CommonContants.DEL_POST_FAIL));
	}
	
	@Override
	public boolean isPostOwnedByUser(Long postId,String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(CommonContants.E_USER_NOT_FOUND));
		
		return postRepository.existsByIdAndUserId(postId, user.getId());
	}
}
