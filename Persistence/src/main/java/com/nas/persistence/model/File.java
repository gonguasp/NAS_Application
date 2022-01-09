package com.nas.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String path;
    private long size;
    private boolean isFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @Column(updatable = false, insertable = true)
    private Instant created;
    private Instant modified;


    public File(String name, String path, long size, boolean isFolder, User user, Instant created, Instant modified) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isFolder = isFolder;
        this.user = user;
        this.created = created;
        this.modified = modified;
    }
}