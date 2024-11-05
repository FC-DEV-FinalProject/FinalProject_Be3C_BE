package com.be3c.sysmetic.domain.admin.repository;

import com.be3c.sysmetic.domain.admin.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findById(Long id);
    Optional<Stock> findByIdAndStatusCode(Long id, String statusCode);
    Optional<Stock> findByName(String name);
    Optional<Stock> findByNameAndStatusCode(String name, String statusCode);

    @Query("SELECT s FROM Stock s WHERE s.statusCode = 'US001' ORDER BY s.createdDate")
    Page<List<Stock>> findUsingItemPage(Pageable pageable);
}