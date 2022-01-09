package com.nas.persistence.repository;

import com.nas.persistence.model.File;
import com.nas.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByPath(String path);

    File findByPathAndUser(String path, User user);

    void deleteByPath(String path);

    void deleteByUserAndPathContaining(User user, String path);

    List<File> findAllByUserAndPathContaining(User user, String path);

    Integer countFilesByUser(User user);

    Integer countFilesByUserAndIsFolder(User user, Boolean isFolder);

    @Query(value ="select coalesce(sum(fl.size), 0) from File fl where fl.user = ?1")
    Long sumSizesByUser(User user);

    @Query(value ="select coalesce(sum(fl.size), 0) from File fl where fl.user <> ?1")
    Long sumSizesExcludingUser(User user);

    void deleteByUser(User user);

}