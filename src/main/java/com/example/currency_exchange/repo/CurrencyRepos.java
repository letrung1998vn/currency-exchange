package com.example.currency_exchange.repo;

import com.example.currency_exchange.entity.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CurrencyRepos extends JpaRepository<CurrencyExchangeRate, Long> {

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByCurrencyCode(@Param("base") String baseCurrency);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByCurrencyCodeAndUpdateTimeOrderByCurrencyCode(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime")
    CurrencyExchangeRate findByCurrencyCodeAndUpdateTime(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base")
    void deleteByBaseCurrency(@Param("base") String baseCurrency);

    @Modifying
    @Transactional
    @Query("DELETE FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime")
    void deleteByBaseCurrencyAndUpdateTime(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);
}
