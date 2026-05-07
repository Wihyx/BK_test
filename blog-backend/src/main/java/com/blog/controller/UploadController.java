package com.blog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * File upload controller. Handles generic file uploads (images/covers/bg),
 * MP3 music listing with embedded cover-art extraction (via jaudiotagger),
 * lyric file serving, and image browsing / deletion.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UploadController {

    /** Root upload directory, configurable via {@code blog.upload.dir} property. */
    @Value("${blog.upload.dir:uploads}")
    private String uploadDir;

    /** Create required sub-directories on startup and resolve the absolute path. */
    @PostConstruct
    public void init() {
        Path abs = Paths.get(uploadDir).toAbsolutePath().normalize();
        uploadDir = abs.toString();
        new File(uploadDir, "images").mkdirs();
        new File(uploadDir, "covers").mkdirs();
        new File(uploadDir, "bg").mkdirs();
        new File(uploadDir, "music").mkdirs();
        log.info("Upload dir: {}", uploadDir);
    }

    /** Serve the embedded cover art of an MP3 file directly as binary image data. */
    @GetMapping("/music/cover")
    public void cover(@RequestParam String name, HttpServletResponse response) {
        try {
            File mp3 = new File(uploadDir + File.separator + "music" + File.separator + name);
            if (!mp3.exists()) { response.sendError(404); return; }
            org.jaudiotagger.audio.AudioFile af = org.jaudiotagger.audio.AudioFileIO.read(mp3);
            org.jaudiotagger.tag.Tag tag = af.getTag();
            if (tag == null) { response.sendError(404); return; }
            List<org.jaudiotagger.tag.datatype.Artwork> artworks = tag.getArtworkList();
            if (artworks.isEmpty()) { response.sendError(404); return; }
            byte[] data = artworks.get(0).getBinaryData();
            response.setContentType(artworks.get(0).getMimeType());
            response.getOutputStream().write(data);
        } catch (Exception e) {
            try { response.sendError(404); } catch (Exception ex) {}
        }
    }

    /** List all MP3 files in the music directory with name, path, and cover URL. */
    @GetMapping("/music")
    public Result<List<Map<String, Object>>> listMusic() {
        File dir = new File(uploadDir + File.separator + "music");
        if (!dir.exists()) return Result.success(Collections.emptyList());
        File[] files = dir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".mp3"));
        if (files == null) return Result.success(Collections.emptyList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (File f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", f.getName());
            map.put("path", "/uploads/music/" + f.getName());
            map.put("cover", getCover(f.getName()));
            list.add(map);
        }
        return Result.success(list);
    }

    /** Return the LRC lyrics file content for the given MP3 filename. */
    @GetMapping("/music/lyrics")
    public Result<String> lyrics(@RequestParam String name) {
        String baseName = name.replaceAll("\\.mp3$", "");
        String lrcPath = uploadDir + File.separator + "music" + File.separator + baseName + ".lrc";
        File lrc = new File(lrcPath);
        if (lrc.exists()) {
            try {
                return Result.success(new String(java.nio.file.Files.readAllBytes(lrc.toPath()), "UTF-8"));
            } catch (Exception e) {
                return Result.success("");
            }
        }
        return Result.success("");
    }

    /** Upload a file to the given type sub-directory (images/covers/bg). */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(defaultValue = "images") String type) {
        if (file == null || file.isEmpty()) {
            return Result.error(400, "file is empty");
        }
        try {
            String ext = FileUtil.extName(file.getOriginalFilename());
            if (StrUtil.isBlank(ext)) ext = "jpg";
            String fileName = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS") + "." + ext;
            String subDir = uploadDir + File.separator + type;
            File destDir = new File(subDir);
            if (!destDir.exists()) destDir.mkdirs();
            File dest = new File(destDir, fileName);
            file.transferTo(dest);
            log.info("uploaded: {}", dest.getAbsolutePath());
            return Result.success("/uploads/" + type + "/" + fileName);
        } catch (Exception e) {
            log.error("upload error", e);
            return Result.error(500, "upload failed: " + e.getMessage());
        }
    }

    /** List image files in the given type sub-directory with metadata. */
    @GetMapping("/images")
    public Result<List<Map<String, Object>>> listImages(@RequestParam(defaultValue = "images") String type) {
        File dir = new File(uploadDir + File.separator + type);
        if (!dir.exists()) return Result.success(Collections.emptyList());
        File[] files = dir.listFiles(f -> f.isFile() && isImage(f.getName()));
        if (files == null) return Result.success(Collections.emptyList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (File f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", f.getName());
            map.put("path", "/uploads/" + type + "/" + f.getName());
            map.put("size", f.length());
            map.put("time", DateUtil.format(new Date(f.lastModified()), "yyyy-MM-dd HH:mm:ss"));
            list.add(map);
        }
        list.sort((a, b) -> ((String) b.get("time")).compareTo((String) a.get("time")));
        return Result.success(list);
    }

    /** Delete an uploaded image file by type and filename. */
    @DeleteMapping("/images/{type}/{name}")
    public Result<?> deleteImage(@PathVariable String type, @PathVariable String name) {
        File file = new File(uploadDir + File.separator + type, name);
        if (file.exists() && file.delete()) return Result.success();
        return Result.error(404, "file not found");
    }

    /** Resolve cover art for an MP3: first checks for a sidecar image, falls back to embedded art endpoint. */
    private String getCover(String mp3Name) {
        String base = mp3Name.replaceAll("\\.mp3$", "");
        for (String ext : Arrays.asList(".jpg", ".jpeg", ".png")) {
            File f = new File(uploadDir + File.separator + "music" + File.separator + base + ext);
            if (f.exists()) return "/uploads/music/" + base + ext;
        }
        return "/api/music/cover?name=" + mp3Name;
    }

    /** Check whether a filename has a supported image extension. */
    private boolean isImage(String name) {
        String ext = FileUtil.extName(name).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg").contains(ext);
    }
}
