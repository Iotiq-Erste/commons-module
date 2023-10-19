package com.iotiq.commons.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base abstract class for entities with getEntityName method
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class NamedAbstractPersistable<T extends Serializable> extends AbstractPersistable<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public abstract String getEntityName();

}
