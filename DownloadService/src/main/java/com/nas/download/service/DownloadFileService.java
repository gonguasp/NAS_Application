package com.nas.download.service;

import com.nas.download.utils.FolderComparator;
import com.nas.download.utils.NameComparator;
import com.nas.persistence.dto.FileDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.repository.FileRepository;
import lombok.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
@NoArgsConstructor
public class DownloadFileService {
    @NonNull
    @Value("${upload.path}")
    private String uploadPath;

    @NonNull
    @Autowired
    private FileRepository fileRepository;

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<FileDTO> getFileList(long id, String relativePath) {
        List<FileDTO> fileList = new ArrayList<>();
        String filePath = (uploadPath + File.separator + id + relativePath).replace("\\\\", "/");
        File[] filesArray = new File(filePath).listFiles();
        if(filesArray == null) {
            filesArray = new File[0];
        }
        Arrays.sort(filesArray, new NameComparator());
        Arrays.sort(filesArray, new FolderComparator());

        Arrays.stream(filesArray).collect(Collectors.toList()).forEach(file ->
            composeFileList(file, filePath, fileList)
        );

        return fileList;
    }

    public void composeFileList(File file, String filePath, List<FileDTO> fileList) {
        Instant modificationDate = Instant.ofEpochMilli(file.lastModified());
        Instant creationDate = Instant.EPOCH;
        try {
            creationDate = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toInstant();
        } catch (IOException e) {
            e.printStackTrace(); //NOSONAR
        }

        FileDTO fileDTO;
        if (!file.isDirectory()) {
            com.nas.persistence.model.File modelFile = fileRepository.findByPath(filePath + file.getName());
            fileDTO = new FileDTO(modelFile.getId(), file.getName(), creationDate, modificationDate, file.isDirectory(), file.length());
        } else {
            fileDTO = new FileDTO(0L, file.getName(), creationDate, modificationDate, file.isDirectory(), file.length());
        }
        fileList.add(fileDTO);
    }

    public StreamingResponseBody downloadZipFile(final HttpServletResponse response, long userId, List<String> files, String parentFolder) {
        response.setContentType("application/zip");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename=sample.zip");

        return out -> {
            StringBuilder path = new StringBuilder(uploadPath + File.separator + userId + File.separator);
            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                for (String fileName : files) {
                    path.append(parentFolder.substring(1));
                    fileName = fileName.replace(parentFolder, "");
                    zipFile(new File(path + fileName), fileName, zipOut);
                }
            } catch (final IOException e) {
                e.printStackTrace(); //NOSONAR
            }
        };
    }

    public StreamingResponseBody downloadFile(final HttpServletResponse response, Long fileId) throws FileNotFoundException {
        Optional<com.nas.persistence.model.File> optionalFile = fileRepository.findById(fileId);
        com.nas.persistence.model.File file;
        if (optionalFile.isPresent()) {
            file = optionalFile.get();
        } else {
            throw new FileNotFoundException("Fichero no encontrado");
        }

        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename=" + file.getName());

        return outputStream -> writeFile(new File(file.getPath()), outputStream);
    }

    public void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }

        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        writeFile(fileToZip, zipOut);
    }

    private void writeFile(File file, OutputStream outputStream) {
        byte[] bytes = new byte[1024];
        int length;
        try (InputStream is = new FileInputStream(file)) {
            while ((length = is.read(bytes)) >= 0) {
                outputStream.write(bytes, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace(); //NOSONAR
        }
    }

    public void deleteFiles(User user, List<String> pathFiles) throws IOException {
        String path = (uploadPath + File.separator + user.getId() + File.separator).replace("\\\\", "/");

        for (String fileName : pathFiles) {
            if (fileName.startsWith("/") || fileName.startsWith("\\")) {
                fileName = fileName.substring(1);
            }
            String filePath = path + fileName;
            if(!new File(filePath).delete()) {
                FileUtils.deleteDirectory(new File(filePath));
            }
            fileRepository.deleteByUserAndPathContaining(user, filePath);
        }
    }

    public boolean changeName(User user, String path, String oldName, String newName) {
        path = uploadPath + "/" + user.getId() + path;

        if(!newName.matches("\\w+(\\.+\\w+)*+") || new File(path + newName).exists()) {
            return false;
        }

        com.nas.persistence.model.File specificFile = fileRepository.findByPathAndUser(path + oldName, user);
        if(specificFile == null) {
            specificFile = fileRepository.findByPathAndUser(path + oldName + "/", user);
        }
        specificFile.setName(newName);
        specificFile.setPath(specificFile.getPath().replace(path + oldName, path + newName));
        specificFile.setModified(Instant.now());
        fileRepository.save(specificFile);

        List<com.nas.persistence.model.File> fileList = fileRepository.findAllByUserAndPathContaining(user, path + oldName);
        for (com.nas.persistence.model.File file : fileList) {
            if(file.getPath().equals(path + newName) || file.getPath().equals(path + newName + "/")) {
                continue;
            }
            file.setPath(file.getPath().replace(path + oldName, path + newName));
            file.setModified(Instant.now());
            fileRepository.save(file);
        }

        try {
            Path oldPath = Paths.get(path + oldName);
            Files.move(oldPath, oldPath.resolveSibling(path + newName));
        } catch (IOException | InvalidPathException e) {
            return false;
        }

        return true;
    }
}
