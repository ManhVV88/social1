package com.example.social.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.social.dto.response.PostTimeLineRespone;
import com.example.social.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByUserIdInOrderByCreateDateDesc(List<Long> listUserId,Pageable  pageable);
	 //  + "(SELECT new com.example.social.dto.response.ImageTimeLineRespone(i.id, i.image,ip.id) FROM Image i JOIN i.post ip WHERE ip.id = p.id), "
	   // + "(SELECT new com.example.social.dto.response.CommentTimeLineRespone(c.id, c.comment, c.postId, cu.id, cu.username, cu.avatar) FROM Comment c JOIN c.user cu WHERE c.postId = p.id)"

//	@Query(
//			"SELECT new com.example.social.dto.response.PostTimeLineRespone("
//			+ "p.id, "
//			+ "p.title, "
//			+ "(SELECT new com.example.social.dto.response.ImageTimeLineRespone(i.id, i.image,ip.id) FROM Image i JOIN i.post ip WHERE ip.id = p.id), "
//		 	+ "p.userId, "
//		    + "p.content, "
//		    + "p.createDate, "
//		    + "u.avatar, "
//		    + "u.username, "
//		    + "(SELECT new com.example.social.dto.response.CommentTimeLineRespone(c.id, c.comment, c.postId, cu.id, cu.username, cu.avatar) FROM Comment c JOIN c.user cu WHERE c.postId = p.id)"
//			    + ") "
//		     + "FROM Post p JOIN User u on p.userId = u.id WHERE p.userId IN :listUserId"
//		     )

	List<PostTimeLineRespone> findAllByUserIdInOrderByCreateDateDesc(@Param("listUserId") List<Long> listUserId,Pageable  pageable);
	
	
	@Query("Select COUNT(p) FROM Post p where p.createDate >= :startDate AND p.createDate <= :endDate AND p.userId = :userId")
	Long countNewPostInWeek(Date startDate, Date endDate,Long userId);
	
	Long countAllByUserIdIn(List<Long> listUserId);
	
	Optional<Post> findFirstByOrderByIdDesc();
	
	void deleteByIdAndUserId(Long id, Long userId);
}
