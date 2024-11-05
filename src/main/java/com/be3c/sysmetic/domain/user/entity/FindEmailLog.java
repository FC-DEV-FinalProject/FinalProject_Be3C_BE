package com.be3c.sysmetic.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
//@Builder(toBuilder = true)
@NoArgsConstructor
//@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@AllArgsConstructor
@Entity
@Table(name = "find_email_log")
public class FindEmailLog {
    /*
        id : 이메일찾기이력 식별번호
        tryIp : 시도한 ip
        tryName : 시도한 이름
        tryPhoneNumber : 시도한 전화번호
        tryDate : 시도한 일시
        successStatusCode : 성공여부코드
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "try_ip", nullable = false)
    private String tryIp;

    @Column(name = "try_name", nullable = false)
    private String tryName;

    @Column(name = "try_phone_number", nullable = false)
    private String tryPhoneNumber;

    @Column(name = "try_date", nullable = false)
    private LocalDateTime tryDate;

    @Column(name = "success_status_code", nullable = false)
    private String successStatusCode;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}
