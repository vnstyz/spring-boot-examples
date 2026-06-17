package org.example.hutooldemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "student_course")
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "请选择学生")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "请选择教师")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @NotBlank(message = "课程名称不能为空")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @NotBlank(message = "学期不能为空")
    private String semester;

    @Column
    private Integer score;
}
