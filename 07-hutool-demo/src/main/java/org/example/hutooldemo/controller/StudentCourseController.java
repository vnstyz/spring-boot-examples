package org.example.hutooldemo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hutooldemo.entity.StudentCourse;
import org.example.hutooldemo.repository.StudentCourseRepository;
import org.example.hutooldemo.repository.StudentRepository;
import org.example.hutooldemo.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class StudentCourseController {

    private final StudentCourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "course/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("course", new StudentCourse());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("action", "add");
        return "course/form";
    }

    @PostMapping
    public String save(@Valid StudentCourse course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("teachers", teacherRepository.findAll());
            model.addAttribute("action", "add");
            return "course/form";
        }
        courseRepository.save(course);
        return "redirect:/course";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("选课记录不存在: " + id)));
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("action", "edit");
        return "course/form";
    }

    @PostMapping("/update")
    public String update(@Valid StudentCourse course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("teachers", teacherRepository.findAll());
            model.addAttribute("action", "edit");
            return "course/form";
        }
        courseRepository.save(course);
        return "redirect:/course";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/course";
    }
}
