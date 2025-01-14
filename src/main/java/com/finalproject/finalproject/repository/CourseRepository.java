package com.finalproject.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finalproject.finalproject.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
}



