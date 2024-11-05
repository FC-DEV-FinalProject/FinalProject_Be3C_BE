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
@Table(name = "account_image")
public class AccountImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy", nullable = false)
    private Strategy strategy;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "account_image_created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime accountImageCreatedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false,  columnDefinition = "Timestamp default now() on update now()")
    private LocalDateTime modifiedDate;
}