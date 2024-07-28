package com.openpayd.forex.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class ConversionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false)
    private String transactionId;
    @Column
    private String sourceCurrency;
    @Column
    private String targetCurrency;
    @Column
    private BigDecimal amount;
    @Column
    private BigDecimal convertedAmount;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
