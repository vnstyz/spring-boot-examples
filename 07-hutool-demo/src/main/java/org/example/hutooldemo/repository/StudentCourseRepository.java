package org.example.hutooldemo.repository;

import org.example.hutooldemo.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
}
