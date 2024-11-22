package com.be3c.sysmetic.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString   //@ToString(of = ("id", "username", "age"))
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notice_title", nullable = false, length = 100)
    private String noticeTitle;

    @Column(name = "notice_content", nullable = false, length = 1000)
    private String noticeContent;

//    @Column(name = "writer", nullable = false)
//    private Long writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @Column(name = "write_date", nullable = false)
    private LocalDateTime writeDate;

    @Column(name = "corrector")
    private Long correctorId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member corrector;

    @Column(name = "correct_date")
    private LocalDateTime correctDate;

    @Column(name = "hits", nullable = false)
    private Long hits;

    @Column(name = "is_attachment", nullable = false)
    private Integer isAttatchment; // enum or long

    @Column(name = "is_open", nullable = false)
    private Integer isOpen; // enum or long

    public static Notice createNotice(String noticeTitle,
                                      String noticeContent,
                                      Member writer,
                                      Integer isAttatchment,
                                      Integer isOpen) {
        Notice notice = new Notice();

        notice.setNoticeTitle(noticeTitle);
        notice.setNoticeContent(noticeContent);
        notice.setWriter(writer);
        notice.setWriteDate(LocalDateTime.now());
        notice.setCorrectorId(writer.getId());
        notice.setHits(0L);
        notice.setIsAttatchment(isAttatchment);
        notice.setIsOpen(isOpen);

        return notice;
    }
}
