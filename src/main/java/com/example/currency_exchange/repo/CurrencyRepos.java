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
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.quoteCurrency = :quote ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByBaseCurrencyAndQuoteCurrencyOrderByBaseCurrency(@Param(
            "base") String baseCurrency, @Param("quote") String quoteCurrency);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.quoteCurrency = :quote AND c.updateTime = :updateTime")
    CurrencyExchangeRate findByBaseCurrencyAndQuoteCurrencyAndUpdateTime(@Param("base") String baseCurrency, @Param(
            "quote") String quoteCurrency, @Param("updateTime") LocalDateTime updateTime);

    // --- methods expected by CurrencyService ---
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByBaseCurrencyOrderByBaseCurrency(@Param("base") String baseCurrency);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByBaseCurrencyAndUpdateTimeOrderByBaseCurrency(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime")
    CurrencyExchangeRate findByBaseCurrencyAndUpdateTime(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);

    // existing methods
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime")
    List<CurrencyExchangeRate> findByBaseCurrency(@Param("base") String baseCurrency, @Param(
            "updateTime") LocalDateTime updateTime);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.quoteCurrency = :quote AND c.updateTime = :updateTime")
    List<CurrencyExchangeRate> findByQuoteCurrency(@Param("quote") String quoteCurrency, @Param(
            "updateTime") LocalDateTime updateTime);

    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.quoteCurrency = :quote AND c.updateTime = :updateTime ORDER BY c.baseCurrency")
    List<CurrencyExchangeRate> findByBaseCurrencyAndQuoteCurrencyAndUpdateTimeOrderByBaseCurrency(@Param(
            "base") String baseCurrency, @Param("quote") String quoteCurrency, @Param(
            "updateTime") LocalDateTime updateTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.quoteCurrency = :quote")
    void deleteByBaseCurrencyAndQuoteCurrency(@Param("base") String baseCurrency, @Param("quote") String quoteCurrency);

    // delete by base only (no time)
    @Modifying
    @Transactional
    @Query("DELETE FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base")
    void deleteByBaseCurrency(@Param("base") String baseCurrency);

    // delete by base and time
    @Modifying
    @Transactional
    @Query("DELETE FROM CurrencyExchangeRate c WHERE c.baseCurrency = :base AND c.updateTime = :updateTime")
    void deleteByBaseCurrencyAndUpdateTime(@Param("base") String baseCurrency, @Param("updateTime") LocalDateTime updateTime);
}
