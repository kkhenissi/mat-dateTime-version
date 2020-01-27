package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.SecurityLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A OutputFile.
 */
@Entity
@Table(name = "output_file")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OutputFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "relative_path_in_st", nullable = false)
    private String relativePathInST;

    @Column(name = "l_t_insertion_date")
    private LocalDateTime lTInsertionDate;

    @Column(name = "path_in_lt")
    private String pathInLT;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "format")
    private String format;

    @Column(name = "sub_system_at_origin_of_data")
    private String subSystemAtOriginOfData;

    @Enumerated(EnumType.STRING)
    @Column(name = "security_level")
    private SecurityLevel securityLevel;

    @Column(name = "crc")
    private String crc;

    @ManyToOne
    @JsonIgnoreProperties("outputFiles")
    private User owner;

    @ManyToOne
    @JsonIgnoreProperties("outputFiles")
    private Run run;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove

    public String getRelativePathInST() {
        return relativePathInST;
    }

    public void setRelativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
    }

    public OutputFile relativePathInST(String relativePathInST) {
        this.relativePathInST = relativePathInST;
        return this;
    }

    public LocalDateTime getlTInsertionDate() {
        return lTInsertionDate;
    }

    public void setlTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
    }

    public OutputFile lTInsertionDate(LocalDateTime lTInsertionDate) {
        this.lTInsertionDate = lTInsertionDate;
        return this;
    }

    public String getPathInLT() {
        return pathInLT;
    }

    public void setPathInLT(String pathInLT) {
        this.pathInLT = pathInLT;
    }

    public OutputFile pathInLT(String pathInLT) {
        this.pathInLT = pathInLT;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public OutputFile fileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public OutputFile format(String format) {
        this.format = format;
        return this;
    }

    public String getSubSystemAtOriginOfData() {
        return subSystemAtOriginOfData;
    }

    public void setSubSystemAtOriginOfData(String subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
    }

    public OutputFile subSystemAtOriginOfData(String subSystemAtOriginOfData) {
        this.subSystemAtOriginOfData = subSystemAtOriginOfData;
        return this;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public OutputFile securityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
        return this;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public OutputFile crc(String crc) {
        this.crc = crc;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public OutputFile owner(User user) {
        this.owner = user;
        return this;
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }

    public OutputFile run(Run run) {
        this.run = run;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OutputFile)) {
            return false;
        }
        return relativePathInST != null && relativePathInST.equals(((OutputFile) o).relativePathInST);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "OutputFile{" + "relativePathInST='" + getRelativePathInST() + "'" + ", lTInsertionDate='"
            + getlTInsertionDate() + "'" + ", pathInLT='" + getPathInLT() + "'" + ", fileType='" + getFileType()
            + "'" + ", format='" + getFormat() + "'" + ", subSystemAtOriginOfData='" + getSubSystemAtOriginOfData()
            + "'" + ", securityLevel='" + getSecurityLevel() + "'"
            + ", crc='" + getCrc() + "'" + "}";
    }
}
