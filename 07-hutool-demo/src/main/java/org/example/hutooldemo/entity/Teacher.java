package org.example.hutooldemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "职称不能为空")
    private String title;

    @NotBlank(message = "院系不能为空")
    private String department;
}
