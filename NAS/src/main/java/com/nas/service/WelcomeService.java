package com.nas.service;

import com.nas.persistence.model.User;
import com.nas.persistence.repository.FileRepository;
import com.nas.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WelcomeService {

    @NonNull
    @Autowired
    private UserRepository userRepository;

    @NonNull
    @Autowired
    private FileRepository fileRepository;

    public Map<String, String> getWelcomeData(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        Map<String, String> welcomeData = new HashMap<>();
        Double GB = 1024 * 1024 * 1024.0;
        Long freeSpace = new File(".").getFreeSpace();
        Long totalSpace = new File(".").getTotalSpace();
        Long usedSpaceByUser = fileRepository.sumSizesByUser(user);
        Long usedSpaceByOthersUsers = fileRepository.sumSizesExcludingUser(user);
        Long system = totalSpace - freeSpace - usedSpaceByUser - usedSpaceByOthersUsers;

        welcomeData.put("filesByUser", Long.toString(fileRepository.countFilesByUser(user)));
        welcomeData.put("foldersByUser", Long.toString(fileRepository.countFilesByUserAndIsFolder(user, true)));
        welcomeData.put("sumSizesByUser", Double.toString(Math.floor(usedSpaceByUser * 100 / GB) / 100.0));
        welcomeData.put("sumSizesExcludingUser", Double.toString(Math.floor(usedSpaceByOthersUsers * 100 / GB) / 100.0));
        welcomeData.put("system", Double.toString(Math.floor(system * 100 / GB) / 100.0));
        welcomeData.put("totalSize", Double.toString(Math.floor(freeSpace * 100 / GB) / 100.0));
        welcomeData.put("activeSince", user.getActiveSince().toString());

        StringBuilder roles = new StringBuilder();
        user.getRoles().forEach(role -> {
            if(!roles.toString().isEmpty()) {
                roles.append(", ");
            }
            if("ROLE_USER".equals(role.getRole())) {
                roles.append("USER");
            } else if("ROLE_ADMIN".equals(role.getRole())) {
                roles.append("ADMIN");
            }
        });

        welcomeData.put("roles", roles.toString());
        return welcomeData;
    }
}
