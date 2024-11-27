package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.entity.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;


    private List<File> fileEntityBuilderAndSaver(int howManyRows){

        List<File> list = new ArrayList<>();
        for(int i=0; i<howManyRows; i++) {
            File fileEntity = File.builder()
                    .path("UUID" + i)
                    .type("contentType"+i)
                    .size(111L)
                    .originalName("filename"+i)

                    .referenceType(FileReferenceType.STRATEGY)
                    .referenceId(1L)

                    .build();
            fileRepository.save(fileEntity);
            list.add(fileEntity);
        }
        return list;
    }

    @Test
    public void  findTest() {
        List<File> saveList = fileEntityBuilderAndSaver(3);
        FileRequest fileRequest = new FileRequest(FileReferenceType.STRATEGY, 1L);
        List<File> findList = fileRepository.findFilesByFileReference(fileRequest);

        System.out.println("saveList = " + saveList);
        System.out.println("findList = " + findList);

        assertEquals(saveList.size(), findList.size(), "파일 개수가 일치하지 않습니다.");

        for (int i = 0; i < saveList.size(); i++) {
            File savedFile = saveList.get(i);
            File foundFile = findList.get(i);

            assertEquals(savedFile.getReferenceId(), foundFile.getReferenceId(), "referenceId가 일치하지 않습니다.");
            assertEquals(savedFile.getReferenceType(), foundFile.getReferenceType(), "referenceType이 일치하지 않습니다.");
            assertEquals(savedFile.getOriginalName(), foundFile.getOriginalName(), "originalName이 일치하지 않습니다.");
            assertEquals(savedFile.getSize(), foundFile.getSize(), "파일 크기가 일치하지 않습니다.");
        }
    }

}

