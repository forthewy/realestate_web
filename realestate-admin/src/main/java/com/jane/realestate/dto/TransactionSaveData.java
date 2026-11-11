package com.jane.realestate.dto;
import com.jane.realestate.entity.Transaction;

public record TransactionSaveData(
        Transaction transaction,
        String fullAddress
) {}