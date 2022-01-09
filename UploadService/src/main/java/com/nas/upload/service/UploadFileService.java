package com.nas.upload.service;

import com.nas.persistence.dto.FolderDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.repository.FileRepository;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Data
public class UploadFileService {
    @Value("${upload.path}")
    private String uploadPath;

    @NonNull
    @Autowired
    private final FileRepository fileRepository;

    public void uploadFile(User user, String path, MultipartFile multipartFile) {
        try {
            path = uploadPath + "/" + user.getId() + path + multipartFile.getOriginalFilename();
            File file = new File(path);
            file.mkdirs();
            multipartFile.transferTo(file);
            registerFileInDB(user, path, multipartFile);
        } catch (IOException e) {
            e.printStackTrace(); //NOSONAR
        }
    }

    private void registerFileInDB(User user, String filePath, MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename() != null ? multipartFile.getOriginalFilename() : "";
        com.nas.persistence.model.File file = fileRepository.findByPathAndUser(filePath, user);

        if (file != null) {
            file.setSize(multipartFile.getSize());
            file.setModified(Instant.now());
        } else {
            file = new com.nas.persistence.model.File(originalFileName.split("/")[originalFileName.split("/").length - 1], //NOSONAR
                    filePath, multipartFile.getSize(), new File(filePath).isDirectory(),
                    user, Instant.now(), Instant.now());
        }

        saveFolder(filePath.replace(originalFileName, ""), originalFileName, user); //NOSONAR
        fileRepository.save(file);
    }

    private void saveFolder(String userPath, String originalFileName, User user) {
        StringBuilder userPathBuilder = new StringBuilder(userPath);
        String[] folders = originalFileName.split("/");
        if (folders.length > 1) {
            List<String> folderList = new LinkedList<>(Arrays.asList(folders));
            folderList.remove(folderList.size() - 1);
            for (String folder : folderList) {
                userPathBuilder.append(folder + "/");
                if (fileRepository.findByPathAndUser(userPathBuilder.toString(), user) == null) {
                    com.nas.persistence.model.File file = new com.nas.persistence.model.File(folder,
                            userPathBuilder.toString(), 0, new File(userPathBuilder.toString()).isDirectory(),
                            user, Instant.now(), Instant.now());
                    fileRepository.save(file);
                }
            }
        }
    }

    public List<FolderDTO> getFoldersList(Long id, String path) {
        List<FolderDTO> foldersList = new ArrayList<>();
        String prePath = uploadPath + "/" + id + "/";
        File[] folders = new File(prePath + path).listFiles(pathname -> pathname.isDirectory());
        path = path.endsWith("/") ? path : path + "/";

        if (folders != null) {
            for (File folder : folders) {
                foldersList.add(new FolderDTO(folder.getName(), hasSubFolders(prePath + path + folder.getName()), path + folder.getName()));
            }
        }

        return foldersList;
    }

    public boolean hasSubFolders(String path) {
        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean createFolder(User user, String path) {
        com.nas.persistence.model.File file = new com.nas.persistence.model.File(new File(path).getName(),
                uploadPath + "/" + user.getId() + path, 0, true,
                user, Instant.now(), Instant.now());
        fileRepository.save(file);
        return new File(uploadPath + "/" + user.getId() + path).mkdirs();
    }

    public boolean deleteFolder(User user, String path) throws IOException {
        path = uploadPath + "/" + user.getId() + path;
        fileRepository.deleteByUserAndPathContaining(user, path);
        FileUtils.deleteDirectory(new File(path));
        return true;
    }
}