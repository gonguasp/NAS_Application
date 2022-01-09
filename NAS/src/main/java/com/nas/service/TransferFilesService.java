package com.nas.service;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.FolderDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.UserView;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Data
@RequiredArgsConstructor
public class TransferFilesService {

    @NonNull
    @Autowired
    private LoadBalancerService loadBalancerService;

    @NonNull
    @Autowired
    private DiscoveryClient discoveryClient;

    @NonNull
    @Autowired
    private JwtService jwtService;

    @NonNull
    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Value("${upload.path}")
    private String uploadPath;

    public List<FolderDTO> getFolderList(long id, String path) {
        List<FolderDTO> foldersList = new ArrayList<>();
        String prePath = uploadPath + "/" + id + "/";
        File[] folders = new File(prePath + path).listFiles(pathname -> pathname.isDirectory()); // File::isDirectory

        if (folders != null) {
            for (File folder : folders) {
                foldersList.add(new FolderDTO(folder.getName(), hasSubFolders(prePath + path + "/" + folder.getName()), path + "/" + folder.getName()));
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

    public void loadVariablesToView(ModelMap model, Boolean createFile, Map<String, String> welcomeData)
            throws ExpiredTokenException, InvalidCredentialsException {
        Object principal = securityConfiguration.getLoggedUser();
        final String jwt = jwtService.generateToken((UserDetailsDTO) principal);
        addFieldsToModal(model, createFile, welcomeData, jwt, ((UserDetailsDTO) principal).getId());
    }

    public void loadVariablesToView(ModelMap model, Boolean createFile, Map<String, String> welcomeData, String jwt)
            throws ExpiredTokenException, InvalidCredentialsException {
        addFieldsToModal(model, createFile, welcomeData, jwt, jwtService.validateToken(jwt).getId());
    }

    private void addFieldsToModal(ModelMap model, Boolean createFile, Map<String, String> welcomeData, String jwt, Long userId)
            throws ExpiredTokenException, InvalidCredentialsException {
        if (createFile.booleanValue()) {
            String id = Long.toString(userId);
            File folder = new File(uploadPath + "/" + id);
            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        model.put("jwt", jwt);
        UserView userView = jwtService.validateToken(jwt);
        model.put("isDarkTheme", userView.isDarkTheme());
        discoveryClient.getServices().forEach(service ->
            model.put(service, loadBalancerService.getInstanceUrl(discoveryClient.getInstances(service)))
        );

        model.put("folders", getFolderList(userId, ""));
        model.put("language", LocaleContextHolder.getLocale().toString().equals("en") ? "english" : LocaleContextHolder.getLocale().toString());
        model.addAllAttributes(welcomeData);
    }
}