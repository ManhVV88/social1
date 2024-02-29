package com.example.social.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.social.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
	//new com.example.social.dto.response.CommentTimeLineRespone(c.id,c.comment,c.postId,c.user.id,c.user.username,c.user.avatar)
//	@Query("SELECT new com.example.social.dto.response.CommentTimeLineRespone(c.id,c.comment,c.postId,u.id,u.username,u.avatar) "
//			+ "FROM Comment c JOIN c.user u WHERE c.postId = :postId")
//	List<CommentTimeLineRespone> getListCommentWithsUserByPostId(@Param("postId") Long postId);
	
	@Query("Select COUNT(c) FROM Like c where c.createDate >= :startDate AND c.createDate <= :endDate AND c.userId = :userId")
	Long countNewCommentInWeek(Date startDate, Date endDate,Long userId);
	
	void deleteByIdAndUserId(Long id,Long userId);
	
	Optional<Comment> findFirstByOrderByIdDesc();
}
