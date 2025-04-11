package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {


    @Autowired
    private AvatarService avatarService;

    @PostMapping("/upload/{studentId}")
    public Avatar uploadAvatar(@PathVariable Long studentId,
                               @RequestParam("file") MultipartFile file)
            throws IOException {
        return avatarService.saveAvatar(file, studentId);
    }

    @GetMapping("/{avatarId}/db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long avatarId) {
        Avatar avatar = avatarService.getAvatarFromDb(avatarId);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.valueOf(avatar.getMediaType()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping("/{avatarId}/disk")
    public ResponseEntity<byte[]> getAvatarFromDisk(@PathVariable Long avatarId) {
        byte[] data = avatarService.getAvatarFromDisk(avatarId);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
