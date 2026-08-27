package com.jane.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_imports")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer transactionCount;

    @Column(nullable = false)
    private Integer skippedCount;

    @Column(nullable = false)
    private LocalDateTime importedAt;

    @PrePersist
    public void prePersist() {
        importedAt = LocalDateTime.now();
    }
}