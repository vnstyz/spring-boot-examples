package org.example.kuangstudythymeleaf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.kuangstudythymeleaf.entity.Employee;
import org.example.kuangstudythymeleaf.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping({"/", "/employee/list"})
    public String list(Model model,
                       @RequestParam(defaultValue = "1") Long pageNo,
                       @RequestParam(defaultValue = "10") Long pageSize) {
        // [MOD-PAGINATION] Unified pagination query entry.
        IPage<Employee> employeePage = employeeService.queryPage(pageNo, pageSize);
        model.addAttribute("page", employeePage);
        model.addAttribute("employees", employeePage.getRecords());

        // [MOD-PAGINATION] Provide page number list for Thymeleaf.
        long totalPages = employeePage.getPages();
        List<Integer> pageNumbers = totalPages > 0
                ? IntStream.rangeClosed(1, (int) totalPages).boxed().toList()
                : new ArrayList<>();
        model.addAttribute("pageNumbers", pageNumbers);
        return "index";
    }

    @PostMapping("/employee/delete/{id}")
    public String deleteEmployeeById(@PathVariable("id") Integer id,
                                     @RequestParam(defaultValue = "1") Long pageNo,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        if (id != null) {
            employeeService.removeById(id);
        }
        return "redirect:/?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    @PostMapping("/employee/batchDelete")
    public String batchDelete(@RequestParam(value = "ids", required = false) List<Integer> ids,
                              @RequestParam(defaultValue = "1") Long pageNo,
                              @RequestParam(defaultValue = "10") Long pageSize) {
        // [MOD-ROBUST] Guard empty batch delete request.
        if (ids != null && !ids.isEmpty()) {
            employeeService.removeBatchByIds(ids);
        }
        return "redirect:/?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    @GetMapping("/employee/add")
    public String toAddPage(Model model) {
        model.addAttribute("employee", new Employee());
        return "input";
    }

    @GetMapping("/employee/edit/{id}")
    public String toUpdatePage(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return "redirect:/";
        }
        model.addAttribute("employee", employee);
        return "input";
    }

    @PostMapping("/employee/save")
    public String saveOrUpdate(@ModelAttribute Employee employee) {
        employeeService.saveOrUpdate(employee);
        return "redirect:/";
    }
}
