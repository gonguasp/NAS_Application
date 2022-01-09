package com.nas.persistence.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private String name;
    private boolean subFolder;
    private String relativePath;
}