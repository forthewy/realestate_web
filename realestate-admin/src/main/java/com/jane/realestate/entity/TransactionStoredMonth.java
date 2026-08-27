package com.jane.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "transaction_stored_months",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transaction_stored_month",
                        columnNames = "stored_month"
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionStoredMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stored_month", nullable = false)
    private String yearMonth;
}