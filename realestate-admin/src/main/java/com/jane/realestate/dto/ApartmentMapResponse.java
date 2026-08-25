package com.jane.realestate.dto;

public record ApartmentMapResponse(
        Long id,
        String name,
        Double lat,
        Double lng,
        Long price,
        Long transactionCount,
        Double area,
        Integer floor
) {}