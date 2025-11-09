package com.example.currency_exchange.repo;

import com.example.currency_exchange.entity.CurrencyExchangeRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

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
        e.setAverageBid(avgBid);
        e.setAverageAsk(avgBid + 0.1f);
        e.setHighBid(avgBid + 0.2f);
        e.setHighAsk(avgBid + 0.3f);
        e.setLowBid(avgBid - 0.2f);
        e.setLowAsk(avgBid - 0.1f);
        return e;
    }

    @Test
    void save_and_findByBaseAndQuoteOrderByBaseCurrency() {
        LocalDateTime t = LocalDateTime.now().withNano(0);
        CurrencyExchangeRate a = repo.save(create("USD", "EUR", t, 1.0f));
        CurrencyExchangeRate b = repo.save(create("USD", "EUR", t.plusDays(1), 1.1f));

        List<CurrencyExchangeRate> list = repo.findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency("USD", "EUR");
        assertThat(list).isNotEmpty();
        assertThat(list).extracting(CurrencyExchangeRate::getBaseCurrency).allMatch(s -> s.equals("USD"));
    }

    @Test
    void findByBaseCurrencyAndUpdateTime_and_findByBaseCurrency() {
        LocalDateTime t = LocalDateTime.now().withNano(0);
        CurrencyExchangeRate saved = repo.save(create("GBP", "USD", t, 1.5f));

        CurrencyExchangeRate one = repo.findByBaseCurrencyAndQuoteCurrencyAndUpdateTime("GBP", "USD", t);
        assertThat(one).isNotNull();
        assertThat(one.getId()).isEqualTo(saved.getId());

        List<CurrencyExchangeRate> byBaseAndTime = repo.findByBaseCurrency("GBP", t);
        assertThat(byBaseAndTime).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findByQuoteCurrency() {
        LocalDateTime t = LocalDateTime.now().withNano(0);
        repo.save(create("AUD", "JPY", t, 0.7f));

        List<CurrencyExchangeRate> byQuote = repo.findByQuoteCurrency("JPY", t);
        assertThat(byQuote).isNotEmpty();
        assertThat(byQuote.get(0).getQuoteCurrency()).isEqualTo("JPY");
    }

    @Test
    void findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency() {
        LocalDateTime t = LocalDateTime.now().withNano(0);
        repo.save(create("CAD", "CHF", t, 0.9f));

        List<CurrencyExchangeRate> res = repo.findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency("CAD",
                "CHF", t);
        assertThat(res).isNotEmpty();
    }

    @Test
    void delete_methods_work() {
        LocalDateTime t = LocalDateTime.now().withNano(0);
        CurrencyExchangeRate e1 = repo.save(create("DEL", "X1", t, 1.0f));
        CurrencyExchangeRate e2 = repo.save(create("DEL", "X2", t.plusMinutes(1), 1.1f));

        // delete by base+quote
        repo.deleteByBaseCurrencyAndQuoteCurrency("DEL", "X1");
        assertThat(repo.findByBaseCurrencyAndQuoteCurrencyAndUpdateTime("DEL", "X1", t)).isNull();

        // delete by base+quote+time on e2
        repo.deleteByBaseCurrencyAndQuoteCurrencyAndUpdateTime("DEL", "X2", t.plusMinutes(1));
        List<CurrencyExchangeRate> remaining = repo.findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency("DEL", "X2");
        assertThat(remaining).isEmpty();

        // save again and delete by base+time
        CurrencyExchangeRate e3 = repo.save(create("BAS", "Q", t, 2.0f));
        repo.deleteByBaseCurrencyAndUpdateTime("BAS", t);
        assertThat(repo.findByBaseCurrency("BAS", t)).isEmpty();

        // delete by quote
        CurrencyExchangeRate e4 = repo.save(create("SOM", "QQ", t, 3.0f));
        repo.deleteByQuoteCurrency("QQ", t);
        assertThat(repo.findByQuoteCurrency("QQ", t)).isEmpty();
    }
}
