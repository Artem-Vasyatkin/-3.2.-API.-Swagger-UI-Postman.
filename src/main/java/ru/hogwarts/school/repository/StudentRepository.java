package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findAllByAge(int age);

    @Query("SELECT COUNT(s) FROM Student s")
    Integer getTotalCount();

    @Query("SELECT AVG(s.age) FROM Student s")
    Double getAverageAge();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC LIMIT :count")
    List<Student> getLastStudents(@Param("count") int count);
}

