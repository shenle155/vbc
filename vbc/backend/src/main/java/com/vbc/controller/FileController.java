package com.vbc.controller;

import com.vbc.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/{type}/{filename}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String type,
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        Resource resource = fileService.loadFile(type, filename);
        long fileLength = resource.contentLength();

        String contentType = type.equals("videos") ? "video/mp4"
                : (type.equals("frames") || type.equals("thumbnails") || type.equals("snapshots"))
                ? "image/jpeg" : "application/octet-stream";

        // No range requested - return full file
        if (rangeHeader == null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Accept-Ranges", "bytes")
                    .contentLength(fileLength)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        }

        // Range request - return partial content
        String rangeValue = rangeHeader.replace("bytes=", "").split(",")[0];
        String[] range = rangeValue.split("-");
        long start = range[0].isEmpty() ? 0 : Long.parseLong(range[0]);
        long end = (range.length > 1 && !range[1].isEmpty())
                ? Long.parseLong(range[1]) : fileLength - 1;
        if (end >= fileLength) end = fileLength - 1;
        long contentLength = end - start + 1;

        InputStream inputStream = resource.getInputStream();
        inputStream.skip(start);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(contentType))
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", "bytes " + start + "-" + end + "/" + fileLength)
                .contentLength(contentLength)
                .body(new InputStreamResource(inputStream));
    }
}
