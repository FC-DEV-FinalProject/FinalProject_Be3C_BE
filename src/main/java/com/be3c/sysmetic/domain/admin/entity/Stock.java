package com.be3c.sysmetic.domain.admin.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Stock")
public class Stock {
    /*
        id : 종목 식별 번호 (Primary Key)
        name : 종목명
        statusCode : 종목 상태 코드
        code : 종목 코드
        country : 국가명
        exchange : 거래소
        listedDate : 상장일
        delistedDate : 폐지일
        stockCreatedDate : 종목 생성일
        createdBy : 생성자 ID
        createdDate : 생성일
        modifiedBy : 수정자 ID
        modifiedDate : 수정일
     */

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "country")
    private String country;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "listed_date")
    private LocalDateTime listedDate;

    @Column(name = "delisted_date")
    private LocalDateTime delistedDate;

    @Column(name = "stock_created_date")
    private LocalDateTime stockCreatedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}
