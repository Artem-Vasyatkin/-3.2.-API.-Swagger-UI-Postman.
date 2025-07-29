package ru.hogwarts.school.controller;


import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hibernate.internal.CoreLogging.logger;

@RestController
@RequestMapping("/student")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final Object lock = new Object();

    @Autowired
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }



    @GetMapping("/names-starting-with-a")
    public List<String> getNamesStartingWithA() {
        logger.info("Was invoked GET endpoint for names starting with 'A'");
        return studentService.getAllNamesStartingWithAUppercase();
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

    @GetMapping("/average-age")
    public Double getAverageAge() {
        logger.info("Was invoked GET endpoint for average student age");
        return studentService.getAverageAge();
    }

    @GetMapping("/last")
    public List<Student> getLastFiveStudents() {
        return studentService.getLastStudents(5);
    }

    @GetMapping("/print-parallel")
    public String printStudentsParallel() throws ExecutionException, InterruptedException {
        List<String> studentNames = studentService.getFirstSixStudentNames();

        if (studentNames.size() < 6) {
            return "Need at least 6 students in the database";
        }

        // Основной поток - первые два имени
        System.out.println("Main thread: " + studentNames.get(0));
        System.out.println("Main thread: " + studentNames.get(1));

        // Параллельный поток 1 - третий и четвертый студент
        CompletableFuture<Void> thread1 = CompletableFuture.runAsync(() -> {
            System.out.println("Parallel thread 1: " + studentNames.get(2));
            System.out.println("Parallel thread 1: " + studentNames.get(3));
        });

        // Параллельный поток 2 - пятый и шестой студент
        CompletableFuture<Void> thread2 = CompletableFuture.runAsync(() -> {
            System.out.println("Parallel thread 2: " + studentNames.get(4));
            System.out.println("Parallel thread 2: " + studentNames.get(5));
        });

        // Ожидаем завершения всех потоков
        CompletableFuture.allOf(thread1, thread2).get();

        return "Check console for parallel output";
    }

    @GetMapping("/print-synchronized")
    public String printStudentsSynchronized() throws InterruptedException {
        List<String> studentNames = studentService.getFirstSixStudentNames();

        if (studentNames.size() < 6) {
            return "Need at least 6 students in the database";
        }

        // Основной поток - первые два имени
        printSynchronized("Main thread", studentNames.get(0));
        printSynchronized("Main thread", studentNames.get(1));

        // Параллельный поток 1 - третий и четвертый студент
        Thread thread1 = new Thread(() -> {
            printSynchronized("Parallel thread 1", studentNames.get(2));
            printSynchronized("Parallel thread 1", studentNames.get(3));
        });

        // Параллельный поток 2 - пятый и шестой студент
        Thread thread2 = new Thread(() -> {
            printSynchronized("Parallel thread 2", studentNames.get(4));
            printSynchronized("Parallel thread 2", studentNames.get(5));
        });

        thread1.start();
        thread2.start();

        // Ожидаем завершения потоков
        thread1.join();
        thread2.join();

        return "Check console for synchronized output";
    }

    private void printSynchronized(String threadName, String studentName) {
        synchronized (lock) {
            System.out.println(threadName + ": " + studentName);
        }
    }
}