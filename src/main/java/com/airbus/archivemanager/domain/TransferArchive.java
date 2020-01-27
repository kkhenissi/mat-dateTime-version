package com.airbus.archivemanager.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.airbus.archivemanager.domain.enumeration.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transfer.
 */
@Entity
@Table(name = "transfer_archive")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransferArchive implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("transferArchives")
    private User owner;

    @Column(name = "files_to_transfer")
    private int filesToTransfer;

    @Column(name = "successfully_transfered")
    private int successfullyTransfered;

    @Column(name = "failed_transfered")
    private int failedTransfered;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not
    // remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TransferArchive name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getArchiveDate() {
        return archiveDate;
    }

    public TransferArchive archiveDate(LocalDateTime archiveDate) {
        this.archiveDate = archiveDate;
        return this;
    }

    public void setArchiveDate(LocalDateTime archiveDate) {
        this.archiveDate = archiveDate;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public TransferArchive status(TransferStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public TransferArchive owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferArchive)) {
            return false;
        }
        return id != null && id.equals(((TransferArchive) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TransferArchive{" + "id=" + getId() + ", name='" + getName() + "'" + ", direction='" + getArchiveDate()
                + "'" + ", status='" + getStatus() + "'" + "}";
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
