package com.airbus.archivemanager.domain;

import com.airbus.archivemanager.domain.enumeration.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A Transfer.
 */
@Entity
@Table(name = "transfer_retrieve")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransferRetrieve implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "retrieve_date")
    private LocalDateTime retrieveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("transferRetrieves")
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

    public TransferRetrieve name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getRetrieveDate() {
        return retrieveDate;
    }

    public TransferRetrieve retrieveDate(LocalDateTime retrieveDate) {
        this.retrieveDate = retrieveDate;
        return this;
    }

    public void setRetrieveDate(LocalDateTime retrieveDate) {
        this.retrieveDate = retrieveDate;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public TransferRetrieve status(TransferStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }


    public User getOwner() {
        return owner;
    }

    public TransferRetrieve owner(User user) {
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
        if (!(o instanceof TransferRetrieve)) {
            return false;
        }
        return id != null && id.equals(((TransferRetrieve) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TransferRetrieve{" + "id=" + getId() + ", name='" + getName() + "'" + ", direction='" + getRetrieveDate() + "'"
            + ", status='" + getStatus() + "'" + "}";
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
