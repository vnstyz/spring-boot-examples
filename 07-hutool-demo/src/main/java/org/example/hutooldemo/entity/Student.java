package org.example.hutooldemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotBlank(message = "专业不能为空")
    private String major;

    @NotNull(message = "入学年份不能为空")
    @Column(name = "enrollment_year")
    private Integer enrollmentYear;
}
