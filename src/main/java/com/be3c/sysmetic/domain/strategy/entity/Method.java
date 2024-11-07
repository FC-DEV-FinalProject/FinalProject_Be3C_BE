package com.be3c.sysmetic.domain.strategy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "method")
public class Method {

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        modifiedDate = now;
        methodCreatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        modifiedDate = now;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "method_created_date", nullable = false)
    private LocalDateTime methodCreatedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
    
}