package travel.journal.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.journal.api.entities.File;
import travel.journal.api.service.FileService;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel-journal/")
public class FileController {

    private final FileService fileService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("image/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable int imageId) {
        File image = fileService.getImageById(imageId);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        String imageName = image.getFileName();
        headers.setContentType(MediaTypeFactory.getMediaType(imageName).orElse(MediaType.APPLICATION_OCTET_STREAM));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(imageName)
                        .build());
        headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS)
                .noTransform()
                .mustRevalidate());
        headers.setAccessControlExposeHeaders(Collections.singletonList(HttpHeaders.CONTENT_DISPOSITION));
        return new ResponseEntity<>(image.getFileContent(), headers, HttpStatus.OK);
    }

}
