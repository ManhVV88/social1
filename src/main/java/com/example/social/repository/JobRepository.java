package com.example.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.social.entity.Job;


@Repository
public interface JobRepository extends JpaRepository<Job, Integer>{	 
}
