package ru.hogwarts.school;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void createStudent_shouldReturnStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry Potter");
        student.setAge(17);

        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Harry Potter\",\"age\":17}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"));
    }

    @Test
    void getStudentInfo_shouldReturnStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Hermione Granger");
        student.setAge(18);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hermione Granger"));
    }

    @Test
    void getStudentsByAge_shouldReturnStudents() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Ron Weasley");
        student.setAge(17);

        when(studentService.findStudentByAge(17)).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student?age=17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ron Weasley"));
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        when(studentService.getFacultyByStudentId(1L)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/students/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void deleteStudent_shouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(status().isOk());
    }
}
