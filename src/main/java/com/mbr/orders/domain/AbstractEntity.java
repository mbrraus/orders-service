package com.mbr.orders.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractEntity implements Serializable {
    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

//    @Column(name = "udate")
//    @LastModifiedDate
//    private LocalDateTime modifiedDate;

}
