package org.example.hutooldemo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hutooldemo.entity.Student;
import org.example.hutooldemo.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "student/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("action", "add");
        return "student/form";
    }

    @PostMapping
    public String save(@Valid Student student, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "add");
            return "student/form";
        }
        studentRepository.save(student);
        return "redirect:/student";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在: " + id)));
        model.addAttribute("action", "edit");
        return "student/form";
    }

    @PostMapping("/update")
    public String update(@Valid Student student, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "student/form";
        }
        studentRepository.save(student);
        return "redirect:/student";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/student";
    }
}
