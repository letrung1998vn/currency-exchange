package com.example.currency_exchange.repo;

import com.example.currency_exchange.entity.CurrencyExchangeRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CurrencyReposTest {

    @Autowired
    private CurrencyRepos repo;

    private CurrencyExchangeRate create(String base, String quote, LocalDateTime t, float avgBid) {
        CurrencyExchangeRate e = new CurrencyExchangeRate();
        e.setBaseCurrency(base);
        e.setQuoteCurrency(quote);
        e.setUpdateTime(t);
        e.setAverageBid(new BigDecimal(avgBid));
        e.setAverageAsk(new BigDecimal(avgBid + 0.1f));
        e.setHighBid(new BigDecimal(avgBid + 0.2f));
        e.setHighAsk(new BigDecimal(avgBid + 0.3f));
        e.setLowBid(new BigDecimal(avgBid - 0.2f));
        e.setLowAsk(new BigDecimal(avgBid - 0.1f));
        return e;
    }

    @Test
    void saveAndFindByCurrencyCodeAndUpdateTimeOrderbyCurrencyCodeOrder() {
        LocalDateTime now = LocalDateTime.now();
        CurrencyExchangeRate a = create("USD", "EUR", now, 1.1f);
        CurrencyExchangeRate b = create("USD", "GBP", now.plusMinutes(1), 1.2f);

        repo.save(a);
        repo.save(b);

        List<CurrencyExchangeRate> list = repo.findByCurrencyCode("USD");
        assertThat(list).isNotEmpty().hasSize(2);
        assertThat(list).allMatch(e -> "USD".equals(e.getBaseCurrency()));
    }

    @Test
    void findByCurrencyAdnUpdateTimeOrderbyCurrencyCodeAndUpdateTimeVariants() {
        LocalDateTime t = LocalDateTime.of(2025, 11, 11, 0, 0);
        CurrencyExchangeRate a = create("JPY", "USD", t, 0.009f);
        repo.save(a);

        List<CurrencyExchangeRate> list = repo.findByCurrencyCodeAndUpdateTimeOrderByCurrencyCode("JPY", t);
        assertThat(list).isNotEmpty().hasSize(1);

        CurrencyExchangeRate single = repo.findByCurrencyCodeAndUpdateTime("JPY", t);
        assertThat(single).isNotNull();
        assertThat(single.getBaseCurrency()).isEqualTo("JPY");
    }

    @Test
    void deleteByBaseCurrencyRemovesAllWithThatBase() {
        LocalDateTime now = LocalDateTime.now();
        repo.save(create("AUD", "USD", now, 0.7f));
        repo.save(create("AUD", "EUR", now.plusHours(1), 0.71f));

        List<CurrencyExchangeRate> before = repo.findByCurrencyCode("AUD");
        assertThat(before).hasSize(2);

        repo.deleteByBaseCurrency("AUD");

        List<CurrencyExchangeRate> after = repo.findByCurrencyCode("AUD");
        assertThat(after).isEmpty();
    }

    @Test
    void deleteByBaseCurrencyAndUpdateTimeRemovesOnlyMatching() {
        LocalDateTime t1 = LocalDateTime.of(2025, 11, 10, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 11, 10, 11, 0);
        repo.save(create("CAD", "USD", t1, 0.8f));
        repo.save(create("CAD", "EUR", t2, 0.81f));

        List<CurrencyExchangeRate> before = repo.findByCurrencyCode("CAD");
        assertThat(before).hasSize(2);

        repo.deleteByBaseCurrencyAndUpdateTime("CAD", t1);

        List<CurrencyExchangeRate> after = repo.findByCurrencyCode("CAD");
        assertThat(after).hasSize(1);
        assertThat(after.get(0).getUpdateTime()).isEqualTo(t2);
    }
}
