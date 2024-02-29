package com.example.social.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.social.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

	@Query("Select COUNT(l) FROM Like l where l.createDate >= :startDate AND l.createDate <= :endDate AND l.userId = :userId")
	Long countNewLikeInWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") Long userId);
	
	Like findByPostIdAndUserId(Long postId, Long userId);
}
