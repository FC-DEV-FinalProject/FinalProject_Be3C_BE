package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findById(Long id);
    Optional<Stock> findByIdAndStatusCode(Long id, String statusCode);
    Optional<Stock> findByName(String name);
    Optional<Stock> findByNameAndStatusCode(String name, String statusCode);

    Page<Stock> findAllByStatusCode(Pageable pageable, String statusCode);
}