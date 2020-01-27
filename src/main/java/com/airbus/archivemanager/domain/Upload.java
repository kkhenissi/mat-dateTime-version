package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.FileType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * An upload.
 */
@Entity
@Table(name = "upload")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Upload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "relative_path_in_st")
    private String relativePathInST;

    @Column(name = "path_in_lt")
    private String lTSPath;

    @Column(name = "l_t_insertion_date")
    private LocalDateTime lTInsertionDate;

    @ManyToOne
    @JsonIgnoreProperties("upload")
    private User owner;

    @ManyToOne
    @JsonIgnoreProperties("upload")
    private Run run;

    @Column(name = "input_type")
    private String inputType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelativePathInST() {
        return relativePathInST;
    }

    public void setRelativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
    }

    public String getlTSPath() {
        return lTSPath;
    }

    public void setlTSPath(String lTSPath) {
        this.lTSPath = lTSPath;
    }

    public LocalDateTime getlTInsertionDate() {
        return lTInsertionDate;
    }

    public void setlTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }
}
