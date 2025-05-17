package ru.hogwarts.school.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students/age")
    public List<Student> getStudentByAge(@RequestParam int minAge, @RequestParam int maxAge) {
        return studentService.getStudentsByAge(minAge, maxAge);
    }

    @GetMapping("/students/{studentId}/faculty")
    public Faculty getFacultyByStudentId(@PathVariable Long studentId) {
        return studentService.getFacultyByStudentId(studentId);
    }

    @GetMapping("/age/{age}")
    public List<Student> findStudentByAge(@PathVariable int age) {

        return studentService.findStudentByAge(age);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.updateStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> findStudents
            (@RequestParam(required = false) int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findStudentByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/count")
    public Integer getStudentsCount() {
        return studentService.getTotalCount();
    }

    @GetMapping("/avg-age")
    public Double getAverageStudentsAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last")
    public List<Student> getLastFiveStudents() {
        return studentService.getLastStudents(5);
    }
}