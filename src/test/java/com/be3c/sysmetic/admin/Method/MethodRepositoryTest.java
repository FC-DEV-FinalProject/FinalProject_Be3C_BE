package com.be3c.sysmetic.admin.Method;


import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodRepositoryTest {
    @Autowired
    MethodRepository methodRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        methodRepository.deleteAll();
        entityManager.createNativeQuery("ALTER TABLE Method AUTO_INCREMENT = 1")
                .executeUpdate();
    }

    @Test
    @DisplayName("테스트 데이터 입력 메서드")
    @Commit
    public void insertToDB() {
        List<Method> methodList = new ArrayList<>();

        for(int i = 0; i < 1000; i++) {
            methodList.add(Method.builder()
                    .name("테스트" + i)
                    .statusCode(Code.USING_STATE.getCode())
                            .explanation("테스트")
                    .build());
        }

        methodRepository.saveAll(methodList);
    }

    @Test
    @DisplayName("메서드 입력 성공 테스트")
    @Order(1)
    public void insertSuccessTest() {
        Method method = Method.builder()
                .name("테스트")
                .statusCode(Code.USING_STATE.getCode())
                .explanation("테스트")
                .build();

        methodRepository.save(method);

        assertTrue(methodRepository.findById(method.getId()).isPresent());
    }

    @Test
    @DisplayName("메서드 입력 실패 테스트 - 어느 한 값이 들어오지 않았을 때")
    @Order(2)
    public void insertFailTest() {
        assertThrows(DataIntegrityViolationException.class, ()-> {
            Method method = Method.builder()
                    .name("이름")
//                    .statusCode(Code.USING_STATE.getCode())
                    .build();
            methodRepository.save(method);
        });

        assertThrows(DataIntegrityViolationException.class, ()-> {
            Method method = Method.builder()
//                    .name("이름")
                    .statusCode(Code.USING_STATE.getCode())
                    .build();
            methodRepository.save(method);
        });
    }

    @Test
    @DisplayName("없는 데이터 찾기")
    @Order(3)
    public void findFailTest() {
        assertThrows(NoSuchElementException.class, ()-> {
            methodRepository.findByIdAndStatusCode(1L, Code.USING_STATE.getCode()).get();
        });
    }

    @Test
    @DisplayName("매매 방식 찾기 성공")
    @Order(4)
    public void findMethod() {
        // given
        List<Method> methodList = new ArrayList<>();

        int total_count = 100;

        for(int i = 1; i <= total_count; i++) {
            methodList.add(Method.builder()
                    .name("테스트" + i)
                    .statusCode(Code.USING_STATE.getCode())
                    .explanation("테스트")
                    .build()
            );
        }

        methodRepository.saveAll(methodList);

        // when
        int find_random = (int) (Math.random() * total_count);
//        log.info("찾는 위치 : {}", find_random);

        // then
        assertEquals(
                methodList.get(find_random - 1),
                methodRepository.findByIdAndStatusCode((long) find_random, Code.USING_STATE.getCode()).get()
        );
    }

    @Test
    @DisplayName("매매 방식 페이지 찾기")
    @Order(5)
    public void findPageMethod() {
        // given
        List<Method> methodList = new ArrayList<>();

        int total_count = ((int) (Math.random() * 1000)) + 100;
        int page_size = 10;

        for(int i = 1; i <= total_count; i++) {
            methodList.add(Method.builder()
                    .name("테스트" + i)
                    .statusCode(Code.USING_STATE.getCode())
                    .explanation("테스트")
                    .build()
            );
        }

        methodRepository.saveAll(methodList);

        // when
        int find_page = (int) ((Math.random() * total_count) / page_size);
        Pageable pageable = PageRequest.of(find_page, page_size, Sort.by("createdAt"));

        Page<MethodGetResponseDto> methodPage = methodRepository.findAllByStatusCode(pageable, Code.USING_STATE.getCode());
        int for_count = 0;
        // then
        assertTrue(methodPage.hasContent());
        for(MethodGetResponseDto methodGetResponseDto : methodPage.getContent()) {
            log.info(methodGetResponseDto.toString());

            assertEquals(methodList.get(find_page * page_size + for_count).getId(), methodGetResponseDto.getId());
            assertEquals(methodList.get(find_page * page_size + for_count).getName(), methodGetResponseDto.getName());

            for_count++;
        }
    }
}

