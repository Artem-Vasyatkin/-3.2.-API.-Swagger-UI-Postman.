package ru.hogwarts.school;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
        studentRepository.deleteAll();
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(17);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl, student, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Harry Potter", response.getBody().getName());
    }

    @Test
    void getStudentInfo_shouldReturnStudent() {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(18);
        Student saved = studentRepository.save(student);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + saved.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hermione Granger", response.getBody().getName());
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() {
        Student student1 = new Student();
        student1.setName("Ron Weasley");
        student1.setAge(17);
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("Draco Malfoy");
        student2.setAge(18);
        studentRepository.save(student2);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                baseUrl + "?age=17", Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        Student student = new Student();
        student.setName("Neville Longbottom");
        student.setAge(17);
        student.setFaculty(faculty);
        Student saved = studentRepository.save(student);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/students/" + saved.getId() + "/faculty", Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gryffindor", response.getBody().getName());
    }

    @Test
    void deleteStudent_shouldRemoveStudent() {
        Student student = new Student();
        student.setName("Luna Lovegood");
        student.setAge(16);
        Student saved = studentRepository.save(student);

        restTemplate.delete(baseUrl + "/" + saved.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + saved.getId(), Student.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
