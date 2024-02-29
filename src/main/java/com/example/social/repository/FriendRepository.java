package com.example.social.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.social.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long>{
	boolean existsByUserIdAndUserIdInvited(Long userId , Long userIdInvited );
	Friend findByUserIdAndUserIdInvited(Long userId , Long userIdInvited );
	void deleteByUserIdAndUserIdInvited(Long userId , Long userIdInvited );
	@Query("SELECT f.userId FROM Friend f WHERE f.userIdInvited = :userIdInvited")
	List<Long> getUserIdByUserIdInvited(@Param("userIdInvited") Long userIdInvited);
	
	@Query("SELECT f.userIdInvited FROM Friend f WHERE f.userId = :userId")
	List<Long> getUserIdInvitedByUserId(@Param("userId") Long userId);
	
	@Query("Select COUNT(f) FROM Friend f where f.updateDate >= :startDate AND f.updateDate <= :endDate AND (f.userId = :userId or f.userIdInvited = :userId) and f.statusAccept = 1")
	Long countNewFriendInWeek(Date startDate, Date endDate,Long userId);
}
