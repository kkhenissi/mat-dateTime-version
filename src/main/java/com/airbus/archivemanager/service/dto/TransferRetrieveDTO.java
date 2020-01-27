package com.airbus.archivemanager.service.dto;

import com.airbus.archivemanager.domain.enumeration.TransferStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.airbus.archivemanager.domain.TransferRetrieve} entity.
 */
public class TransferRetrieveDTO implements Serializable, Cloneable {

    private Long id;

    @NotNull
    private String name;

    private LocalDateTime retrieveDate;

    private TransferStatus status;

    private Set<OutputFileDTO> outputFiles = new HashSet<>();
    private Set<ScenarioFileDTO> scenarioFiles = new HashSet<>();

    private int filesToTransfer = 0;
    private int successfullyTransfered = 0;
    private int failedTransfered = 0;
    
    private Long ownerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getRetrieveDate() {
        return retrieveDate;
    }

    public void setRetrieveDate(LocalDateTime retrieveDate) {
        this.retrieveDate = retrieveDate;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long userId) {
        this.ownerId = userId;
    }

    public Set<OutputFileDTO> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(Set<OutputFileDTO> outputFiles) {
        this.outputFiles = outputFiles;
    }

    public Set<ScenarioFileDTO> getScenarioFiles() {
        return scenarioFiles;
    }

    public void setScenarioFiles(Set<ScenarioFileDTO> scenarioFiles) {
        this.scenarioFiles = scenarioFiles;
    }

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
            ((TransferRetrieveDTO) o).outputFiles = new HashSet<>();
            ((TransferRetrieveDTO) o).scenarioFiles = new HashSet<>();
        } catch (CloneNotSupportedException cnse) {
            cnse.printStackTrace(System.err);
        }
        return o;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransferRetrieveDTO transferRetrieveDTO = (TransferRetrieveDTO) o;
        if (transferRetrieveDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transferRetrieveDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", direction='" + getRetrieveDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", owner=" + getOwnerId() +
            "}";
    }

    public int getFilesToTransfer() {
        return filesToTransfer;
    }

    public void setFilesToTransfer(int filesToTransfer) {
        this.filesToTransfer = filesToTransfer;
    }

    public int getSuccessfullyTransfered() {
        return successfullyTransfered;
    }

    public void setSuccessfullyTransfered(int successfullyTransfered) {
        this.successfullyTransfered = successfullyTransfered;
    }

    public int getFailedTransfered() {
        return failedTransfered;
    }

    public void setFailedTransfered(int failedTransfered) {
        this.failedTransfered = failedTransfered;
    }
}
