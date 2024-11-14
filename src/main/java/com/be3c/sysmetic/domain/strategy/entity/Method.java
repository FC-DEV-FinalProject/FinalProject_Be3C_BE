package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "method")
public class Method extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "statusCode", nullable = false)
    private String statusCode;

    @Column(name = "explanation", nullable = false)
    private String explanation;

    @CreationTimestamp
    @Column(name = "method_created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime methodCreatedDate;
}