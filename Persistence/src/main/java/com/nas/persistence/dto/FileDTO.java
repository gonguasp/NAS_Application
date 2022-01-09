package com.nas.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    private Long id;
    private String name;
    private Instant creationDate;
    private Instant modificationDate;
    private Boolean isDirectory;
    private Long size;
}
