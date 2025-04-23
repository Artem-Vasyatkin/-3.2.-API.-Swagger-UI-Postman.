package ru.hogwarts.school.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(null);
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> findStudentByAge(int age) {
        return studentRepository.findAllByAge(age);
    }

    public List<Student> getStudentsByAge(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow()
                .getFaculty();
    }

    public Integer getTotalCount() {
        return studentRepository.getTotalCount();
    }

    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastStudents(int count) {
        return studentRepository.getLastStudents(count);
    }
}