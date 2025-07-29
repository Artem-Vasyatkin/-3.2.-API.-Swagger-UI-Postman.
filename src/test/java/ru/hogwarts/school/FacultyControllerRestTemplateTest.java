package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    public void testGetFacultyByNameOrColor() {
        String query = "Gryffindor";
        Faculty faculty = restTemplate.getForObject(
                baseUrl + "/faculties?query=" + query,
                Faculty.class
        );
        assertNotNull(faculty);
        assertEquals(query, faculty.getName());
    }

    @Test
    public void testGetStudentsByFacultyId() {
        Long facultyId = 1L;
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/faculties/" + facultyId + "/students",
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testFindFacultiesByColor() {
        String color = "Red";
        ResponseEntity<List> response = restTemplate.getForEntity(
                baseUrl + "/color/" + color,
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetFacultyInfo() {
        Long id = 1L;
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Faculty.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    public void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("Yellow");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl,
                faculty,
                Faculty.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hufflepuff", response.getBody().getName());
    }

    @Test
    public void testEditFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(faculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                request,
                Faculty.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ravenclaw", response.getBody().getName());
    }

    @Test
    public void testDeleteFaculty() {
        Long id = 1L;
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindFaculties() {
        String color = "Green";
        ResponseEntity<Collection> response = restTemplate.getForEntity(
                baseUrl + "?color=" + color,
                Collection.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetLongestFacultyName() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/longest-name",
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
