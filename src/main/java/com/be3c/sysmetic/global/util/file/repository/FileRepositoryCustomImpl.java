package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.entity.File;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.be3c.sysmetic.global.util.file.entity.QFile.file;

@Repository
@RequiredArgsConstructor
public class FileRepositoryCustomImpl implements FileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<File> findFilesByFileReference(FileRequest fileRequest) {

        return queryFactory
                .select(file)
                .from(file)
                .where(file.referenceId.eq(fileRequest.referenceId())
                        .and(file.referenceType.eq(fileRequest.referenceType())))
                .orderBy(file.id.asc())
                .fetch();
    }
}
