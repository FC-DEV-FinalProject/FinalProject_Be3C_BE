package com.be3c.sysmetic.admin;


import com.be3c.sysmetic.domain.admin.entity.Stock;
import com.be3c.sysmetic.domain.admin.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestPropertySource(locations = "/application-test.properties")
@Slf4j
@SpringBootTest
@Transactional // 트랜잭션을 추가해 데이터베이스 격리
public class StockRepositoryTest {
    @Autowired
    StockRepository stockRepository;

    private static String[] STATUS_CODE = {"US001", "US002"};

    @BeforeEach
    public void setUp() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("아이디로_종목_찾기")
    public void findById() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder().name("테스트" + i).statusCode(STATUS_CODE[0]).build());
            stockRepository.save(stocks.get(i));
            Optional<Stock> stock = stockRepository.findByIdAndStatusCode((long) i + 1, STATUS_CODE[0]);
            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + i);
        }

        int test_count = 0;
        while (test_count < 10) {
            long find_rand = (long) (Math.random() * create_rand);
            log.info("찾아본 테스트 종목 번호 : {}번", find_rand);

            Optional<Stock> stock = stockRepository.findByIdAndStatusCode(find_rand + 1, STATUS_CODE[0]);
            assertThat(stock).isPresent();
            assertThat(stock.get().getName()).isEqualTo("테스트" + find_rand);

            test_count++;
        }
    }

    @Test
    @DisplayName("이름으로_종목_찾기")
    public void findByName() {
        // given
        ArrayList<Stock> stocks = new ArrayList<>();
        long create_rand = (long) (Math.random() * 1000);
        log.info("생성된 테스트 종목 수 : {}개", create_rand);

        for (int i = 0; i < create_rand; i++) {
            stocks.add(Stock.builder().name("테스트" + i).statusCode(STATUS_CODE[0]).build());
            stockRepository.save(stocks.get(i));
//            Optional<Stock> stock = stockRepository.findByIdAndStatusCode((long) i + 1, STATUS_CODE[0]);
//            assertThat(stock).isPresent();
//            assertThat(stock.get().getName()).isEqualTo("테스트" + i);
        }

        int test_count = 0;
        while (test_count < 10) {
            long find_rand = (long) (Math.random() * create_rand);
            log.info("찾아본 테스트 종목 번호 : {}번", find_rand);

            Optional<Stock> stock = stockRepository.findByNameAndStatusCode(
                    stocks.get((int) find_rand).getName(), STATUS_CODE[0]
            );
            assertThat(stock).isPresent();

            test_count++;
        }
    }
}

