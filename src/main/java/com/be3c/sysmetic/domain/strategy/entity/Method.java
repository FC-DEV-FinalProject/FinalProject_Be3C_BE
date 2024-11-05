package com.be3c.sysmetic.domain.strategy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "method")
public class Method {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @OneToOne(mappedBy = "method")
    // private Strategy strategy;
}