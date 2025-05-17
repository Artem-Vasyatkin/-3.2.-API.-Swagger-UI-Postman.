package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.debug("StudentService initialized with StudentRepository");
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for adding student: {}", student);
        logger.debug("Attempting to save student with name: {}", student.getName());

        Student savedStudent = studentRepository.save(student);
        logger.info("Successfully added student with id: {}", savedStudent.getId());

        return savedStudent;
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method for finding student by id: {}", id);
        logger.debug("Searching for student with id: {}", id);

        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Student not found with id: {}", id);
                    return new IllegalArgumentException("Student not found");
                });
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for updating student with id: {}", student.getId());

        if (!studentRepository.existsById(student.getId())) {
            logger.error("Attempt to update non-existent student with id: {}", student.getId());
            throw new IllegalArgumentException("Student not found");
        }

        logger.debug("Updating student: {}", student);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Successfully updated student with id: {}", updatedStudent.getId());

        return updatedStudent;
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for deleting student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent student with id: {}", id);
            return;
        }

        studentRepository.deleteById(id);
        logger.info("Successfully deleted student with id: {}", id);
    }

    public List<Student> findStudentByAge(int age) {
        logger.info("Was invoked method for finding students by age: {}", age);
        logger.debug("Searching students with age: {}", age);

        List<Student> students = studentRepository.findAllByAge(age);
        logger.info("Found {} students with age {}", students.size(), age);

        return students;
    }

    public List<Student> getStudentsByAge(int minAge, int maxAge) {
        logger.info("Was invoked method for finding students between ages {} and {}", minAge, maxAge);
        logger.debug("Searching students with age range: {} - {}", minAge, maxAge);

        if (minAge > maxAge) {
            logger.warn("Invalid age range: minAge {} is greater than maxAge {}", minAge, maxAge);
            throw new IllegalArgumentException("Invalid age range");
        }

        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.info("Found {} students in age range {} - {}", students.size(), minAge, maxAge);

        return students;
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for getting faculty by student id: {}", studentId);

        return studentRepository.findById(studentId)
                .map(student -> {
                    Faculty faculty = student.getFaculty();
                    if (faculty == null) {
                        logger.debug("Student with id {} has no faculty assigned", studentId);
                    } else {
                        logger.debug("Found faculty: {} for student id: {}", faculty.getName(), studentId);
                    }
                    return faculty;
                })
                .orElseThrow(() -> {
                    logger.error("Student not found with id: {}", studentId);
                    return new IllegalArgumentException("Student not found");
                });
    }

    public Integer getTotalCount() {
        logger.info("Was invoked method for getting total student count");

        Integer count = studentRepository.getTotalCount();
        logger.info("Total student count: {}", count);

        return count;
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for getting average age of all students");

        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students for age calculation", students.size());

        if (students.isEmpty()) {
            logger.warn("No students found for average age calculation");
            return 0.0;
        }

        double averageAge = students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);

        logger.info("Calculated average age: {}", averageAge);
        return averageAge;
    }

    public List<Student> getLastStudents(int count) {
        logger.info("Was invoked method for getting last {} students", count);

        if (count <= 0) {
            logger.warn("Invalid count parameter: {}", count);
            throw new IllegalArgumentException("Count must be positive");
        }

        List<Student> students = studentRepository.getLastStudents(count);
        logger.info("Retrieved {} last students", students.size());

        return students;
    }

    public List<String> getAllNamesStartingWithAUppercase() {
        logger.info("Was invoked method for getting all names starting with 'A'");

        long startTime = System.currentTimeMillis();

        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} total students", students.size());

        List<String> result = students.stream()
                .map(Student::getName)
                .filter(name -> {
                    boolean startsWithA = name.toUpperCase().startsWith("A");
                    if (!startsWithA) {
                        logger.trace("Filtered out name: {}", name);
                    }
                    return startsWithA;
                })
                .map(String::toUpperCase)
                .sorted()
                .peek(name -> logger.debug("Processing name: {}", name))
                .collect(Collectors.toList());

        logger.info("Method completed in {} ms. Found {} matching names",
                System.currentTimeMillis() - startTime, result.size());

        return result;
    }

    public List<String> getFirstSixStudentNames() {
        return studentRepository.findAll().stream()
                .limit(6)
                .map(Student::getName)
                .collect(Collectors.toList());
    }
}