package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.model.Student;

import java.util.Arrays;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {



    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getFacultyByNameOrColor_ShouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.getFacultyByNameOrColor(anyString())).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/faculties")
                        .param("query", "Gryffindor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getStudentsByFacultyId_ShouldReturnStudentsList() throws Exception {
        Student student1 = new Student(1L, "Harry Potter", 11);
        Student student2 = new Student(2L, "Hermione Granger", 11);
        when(facultyService.getStudentsByFacultyId(anyLong())).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/faculties/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Hermione Granger"));
    }

    @Test
    void findFacultiesColor_ShouldReturnFacultiesByColor() throws Exception {
        Faculty faculty1 = new Faculty(1L, "Gryffindor", "Red");
        Faculty faculty2 = new Faculty(2L, "Hufflepuff", "Yellow");
        when(facultyService.findFacultyByColor("Red")).thenReturn(Arrays.asList(faculty1));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/color/Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"));
    }

    @Test
    void getFacultyInfo_ShouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void getFacultyInfo_ShouldReturnNotFound() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFaculty_ShouldCreateAndReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void editFaculty_ShouldUpdateAndReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void editFaculty_ShouldReturnBadRequest() throws Exception {
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Faculty())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFaculty_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void findFaculties_WithColor_ShouldReturnFilteredFaculties() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.findFacultyByColor("Red")).thenReturn(Collections.singletonList(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    void findFaculties_WithoutColor_ShouldReturnEmptyList() throws Exception {
        when(facultyService.findFacultyByColor(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getLongestFacultyName_ShouldReturnLongestName() throws Exception {
        when(facultyService.getLongestFacultyName()).thenReturn("Gryffindor");

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/longest-name"))
                .andExpect(status().isOk())
                .andExpect(content().string("Gryffindor"));
    }

    @Test
    void getLongestFacultyName_WhenNoFaculties_ShouldReturnNoContent() throws Exception {
        when(facultyService.getLongestFacultyName()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/longest-name"))
                .andExpect(status().isNoContent());
    }
}