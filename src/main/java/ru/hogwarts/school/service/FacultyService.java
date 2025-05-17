package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        logger.debug("FacultyService initialized with FacultyRepository");
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for adding faculty: {}", faculty);
        logger.debug("Attempting to save faculty with name: {}", faculty.getName());

        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Successfully added faculty with id: {}", savedFaculty.getId());

        return savedFaculty;
    }

    public Faculty findFaculty(Long id) {
        logger.info("Was invoked method for finding faculty by id: {}", id);
        logger.debug("Searching for faculty with id: {}", id);

        return facultyRepository.findById(id)
                .orElseGet(() -> {
                    logger.warn("Faculty not found with id: {}", id);
                    return null;
                });
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for updating faculty with id: {}", faculty.getId());

        if (!facultyRepository.existsById(faculty.getId())) {
            logger.error("Attempt to update non-existent faculty with id: {}", faculty.getId());
            throw new IllegalArgumentException("Faculty not found");
        }

        logger.debug("Updating faculty: {}", faculty);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Successfully updated faculty with id: {}", updatedFaculty.getId());

        return updatedFaculty;
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for deleting faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent faculty with id: {}", id);
            return;
        }

        facultyRepository.deleteById(id);
        logger.info("Successfully deleted faculty with id: {}", id);
    }

    public List<Faculty> findFacultyByColor(String color) {
        logger.info("Was invoked method for finding faculties by color: {}", color);
        logger.debug("Searching faculties with color: {}", color);

        List<Faculty> faculties = facultyRepository.findAllByColor(color);
        logger.info("Found {} faculties with color {}", faculties.size(), color);

        return faculties;
    }

    public Faculty getFacultyByNameOrColor(String query) {
        logger.info("Was invoked method for finding faculty by name or color: {}", query);
        logger.debug("Searching faculty with name or color matching: {}", query);

        Faculty faculty = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query, query);
        if (faculty == null) {
            logger.debug("No faculty found matching query: {}", query);
        } else {
            logger.debug("Found faculty: {} for query: {}", faculty, query);
        }

        return faculty;
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for getting students by faculty id: {}", facultyId);

        return facultyRepository.findById(facultyId)
                .map(faculty -> {
                    logger.debug("Found faculty: {}, students count: {}",
                            faculty.getName(), faculty.getStudents().size());
                    return faculty.getStudents();
                })
                .orElseThrow(() -> {
                    logger.error("Faculty not found with id: {}", facultyId);
                    return new IllegalArgumentException("Faculty not found");
                });
    }
}
