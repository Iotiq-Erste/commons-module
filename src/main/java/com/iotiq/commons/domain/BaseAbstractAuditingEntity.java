package com.iotiq.commons.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class BaseAbstractAuditingEntity<T extends Serializable> extends NamedAbstractPersistable<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();

}
