package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock")
public class Stock extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "code")
    private String code;

    @Column(name = "country")
    private String country;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "listed_date")
    private LocalDateTime listedDate;

    @Column(name = "delisted_date")
    private LocalDateTime delistedDate;

    @CreatedDate
    @Column(name = "stock_created_date", nullable = false)
    private LocalDateTime stockCreatedDate;

}