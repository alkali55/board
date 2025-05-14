package com.miniproj.util;

import com.miniproj.domain.BoardUpFilesVODTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadUtil {

    @Value("${file.upload-base-dir}")
    private String baseDir;

    @Value("${file.upload-url-path}")
    private String uploadUrlPath;

    public List<BoardUpFilesVODTO> saveFiles(List<MultipartFile> multipartFileList) throws IOException {

        List<BoardUpFilesVODTO> resultList = new ArrayList<>();

        if(multipartFileList == null || multipartFileList.isEmpty()) return resultList;

        String datePath = getDatePath();
        log.info("datePath : {}", datePath);

        // C:/upload    /     2025/04/29
        String targetDir = baseDir + File.separator + datePath;

        // 디렉토리 생성
        Files.createDirectories(Paths.get(targetDir)); // throws IOException

        // 원래파일이름에 uuid를 붙여서 파일이름 중복 방지 -> 저장
        for (MultipartFile file: multipartFileList){

            if(file.isEmpty()) continue;
            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();

            String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            String newName = uuid + "_" + originalFileName;
            String fullPath = targetDir + File.separator + newName;

            // 실제 파일 저장
            file.transferTo(new File(fullPath));

            // 이미지이면 썸네일 & base64 생성
//            boolean isImage = file.getContentType() != null && file.getContentType().startsWith("image/");
            boolean isImage = ImageMimeType.isImage(ext);
            String base64 = null;

            if(isImage){
                // 썸네일 이미지 생성
                String thumbName = "thumb_" + newName;
                String thumbPath = targetDir + File.separator + thumbName;

                BufferedImage thumbnail = Thumbnails.of(new File(fullPath)).size(200, 200).asBufferedImage();
                ImageIO.write(thumbnail, ext, new File(thumbPath));

                // base64 변환
                byte[] imageBytes = Files.readAllBytes(Paths.get(thumbPath));
                base64 = Base64.getEncoder().encodeToString(imageBytes);
                log.info("base64 : {}", base64);
            }

            // /upload/yyyy/MM/dd/새파일명
            String relativePath = uploadUrlPath + datePath + File.separator + newName;
            String thumbRelativePath = isImage? uploadUrlPath + datePath + File.separator + "thumb_" + newName : null;

            BoardUpFilesVODTO dto = new BoardUpFilesVODTO();
            dto.setOriginalFileName(originalFileName);
            dto.setNewFileName(newName);
            dto.setImage(isImage);
            dto.setExt(ext);
            dto.setSize(file.getSize());
            dto.setBase64(base64);
            dto.setFilePath(relativePath);
            dto.setThumbFileName(thumbRelativePath);

            resultList.add(dto);

        }

//        throw new IOException("예외발생");

        return resultList;
    }

    private String getDatePath() {
        LocalDate today = LocalDate.now();
        // yyyy/MM/dd
        return today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public void deleteFile(String relativePath){

        log.info("삭제할 파일 : {}", relativePath);
        String fullPath = (baseDir + relativePath.replace(uploadUrlPath, "")).replace("/", File.separator);

        log.info("fullPath = {}", fullPath);

        File file = new File(fullPath);

        if (file.exists()) {
            file.delete();
        }
    }
}
