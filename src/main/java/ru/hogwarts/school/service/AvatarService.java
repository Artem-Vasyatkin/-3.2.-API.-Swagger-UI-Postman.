package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private StudentRepository studentRepository;

    private final String UPLOAD_DIR = "uploads";

    public Avatar saveAvatar(MultipartFile file, Long studentId) throws IOException {
        logger.info("Was invoked method for save avatar for student with id = {}", studentId);
        logger.debug("Attempting to save avatar file: {}", file.getOriginalFilename());

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + File.separator + fileName;
        Path fileLocation = Paths.get(filePath);

        try {
            Files.createDirectories(fileLocation.getParent());
            Files.copy(file.getInputStream(), fileLocation);

            logger.debug("Successfully saved avatar to disk: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save avatar file to disk: {}", filePath, e);
            throw e;
        }

        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath);
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        logger.debug("Creating avatar entity for student {}", studentId);
        avatar = avatarRepository.save(avatar);

        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
            logger.error("Student not found with id = {}", studentId);
            return new RuntimeException("Student not found");
        });

        student.setAvatar(avatar);
        studentRepository.save(student);

        logger.info("Successfully saved avatar with id = {} for student {}", avatar.getId(), studentId);
        return avatar;
    }

    public Avatar getAvatarFromDb(Long avatarId) {
        logger.info("Was invoked method for get avatar from DB with id = {}", avatarId);
        logger.debug("Attempting to fetch avatar with id = {}", avatarId);

        return avatarRepository.findById(avatarId).orElseThrow(() -> {
            logger.error("Avatar not found in DB with id = {}", avatarId);
            return new RuntimeException("Avatar not found");
        });
    }

    public byte[] getAvatarFromDisk(Long avatarId) {
        logger.info("Was invoked method for get avatar from disk with id = {}", avatarId);

        Avatar avatar = getAvatarFromDb(avatarId);
        logger.debug("Found avatar in DB, attempting to read from disk: {}", avatar.getFilePath());

        try {
            byte[] data = Files.readAllBytes(Paths.get(avatar.getFilePath()));
            logger.debug("Successfully read avatar file from disk: {}", avatar.getFilePath());
            return data;
        } catch (IOException e) {
            logger.error("Failed to read avatar file from disk: {}", avatar.getFilePath(), e);
            throw new RuntimeException("Не удалось загрузить аватар с диска.", e);
        }
    }

    public Page<Avatar> getAllAvatarsPaginated(int page, int size) {
        logger.info("Was invoked method for get all avatars paginated (page = {}, size = {})", page, size);

        if (size > 100) {
            logger.warn("Requested page size {} is too large, reducing to 100", size);
            size = 100;
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        logger.debug("Fetching avatars with page request: {}", pageRequest);

        Page<Avatar> avatars = avatarRepository.findAll(pageRequest);
        logger.info("Successfully retrieved {} avatars", avatars.getNumberOfElements());

        return avatars;
    }
}
