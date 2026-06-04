package org.example.hutooldemo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hutooldemo.entity.Teacher;
import org.example.hutooldemo.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("teachers", teacherRepository.findAll());
        return "teacher/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("action", "add");
        return "teacher/form";
    }

    @PostMapping
    public String save(@Valid Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "add");
            return "teacher/form";
        }
        teacherRepository.save(teacher);
        return "redirect:/teacher";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("教师不存在: " + id)));
        model.addAttribute("action", "edit");
        return "teacher/form";
    }

    @PostMapping("/update")
    public String update(@Valid Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "teacher/form";
        }
        teacherRepository.save(teacher);
        return "redirect:/teacher";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        teacherRepository.deleteById(id);
        return "redirect:/teacher";
    }
}
