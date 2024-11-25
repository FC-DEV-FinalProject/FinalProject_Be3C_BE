package com.be3c.sysmetic.global.util.file.mapper;

import com.be3c.sysmetic.global.util.file.dto.FileDto;
import com.be3c.sysmetic.global.util.file.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileDto toDTO(File file);
    File toEntity(FileDto fileDTO);
}
