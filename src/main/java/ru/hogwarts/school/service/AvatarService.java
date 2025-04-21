package ru.hogwarts.school.service;

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

    @Autowired
    private AvatarRepository avatarRepository;

    private StudentRepository studentRepository;

    private final String UPLOAD_DIR = "uploads";

    public Avatar saveAvatar(MultipartFile file, Long studentId)
            throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + File.separator + fileName;

        Path fileLocation = Paths.get(filePath);

        Files.createDirectories(fileLocation.getParent());
        Files.copy(file.getInputStream(), fileLocation);

        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath);
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());
        avatar = avatarRepository.save(avatar);

        Student student = new Student();

        student.setAvatar(avatar);

        return avatar;
    }

    public Avatar getAvatarFromDb(Long avatarId) {
        return avatarRepository.findById(avatarId).orElseThrow();
    }

    public byte[] getAvatarFromDisk(Long avatarId) {
        Avatar avatar = getAvatarFromDb(avatarId);
        try {
            return Files.readAllBytes(Paths.get(avatar.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить аватар с диска.", e);
        }
    }

    public Page<Avatar> getAllAvatarsPaginated(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return avatarRepository.findAll(pageRequest);
    }
}
