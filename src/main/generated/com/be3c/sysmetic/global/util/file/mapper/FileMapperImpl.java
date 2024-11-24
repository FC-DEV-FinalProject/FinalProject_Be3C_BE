package com.be3c.sysmetic.global.util.file.mapper;

import com.be3c.sysmetic.global.util.file.dto.FileDto;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.entity.File;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-24T16:19:47+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.5 (Azul Systems, Inc.)"
)
@Component
public class FileMapperImpl implements FileMapper {

    @Override
    public FileDto toDTO(File file) {
        if ( file == null ) {
            return null;
        }

        Long id = null;
        String path = null;
        String type = null;
        long size = 0L;
        String originalName = null;
        String referenceType = null;
        Long referenceId = null;
        LocalDate expireDate = null;

        id = file.getId();
        path = file.getPath();
        type = file.getType();
        size = file.getSize();
        originalName = file.getOriginalName();
        if ( file.getReferenceType() != null ) {
            referenceType = file.getReferenceType().name();
        }
        referenceId = file.getReferenceId();
        expireDate = file.getExpireDate();

        boolean isDeleted = false;

        FileDto fileDto = new FileDto( id, path, type, size, originalName, referenceType, referenceId, isDeleted, expireDate );

        return fileDto;
    }

    @Override
    public File toEntity(FileDto fileDTO) {
        if ( fileDTO == null ) {
            return null;
        }

        File.FileBuilder file = File.builder();

        file.id( fileDTO.id() );
        file.path( fileDTO.path() );
        file.type( fileDTO.type() );
        file.size( fileDTO.size() );
        file.originalName( fileDTO.originalName() );
        if ( fileDTO.referenceType() != null ) {
            file.referenceType( Enum.valueOf( FileReferenceType.class, fileDTO.referenceType() ) );
        }
        file.referenceId( fileDTO.referenceId() );
        file.isDeleted( fileDTO.isDeleted() );
        file.expireDate( fileDTO.expireDate() );

        return file.build();
    }
}
