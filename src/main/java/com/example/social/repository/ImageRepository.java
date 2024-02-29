package com.example.social.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social.entity.Image;
import com.example.social.entity.Post;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findByPost(Post post);
	Image findFirstByImageStartsWithOrderByIdDesc(String image);
}
