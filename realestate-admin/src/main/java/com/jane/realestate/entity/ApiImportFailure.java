package com.jane.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_import_failures")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiImportFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ApiImportRun importRun;

    @Column(nullable = false)
    private String sggCd;

    @Column(nullable = false)
    private String dealMonth;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    @Column(nullable = true)
    private LocalDateTime resolvedAt;

    @Column(length = 1000)
    private String errorMessage;

    // 실패 복구 메소드
    public void resolve() {
        this.resolvedAt = LocalDateTime.now();
    }
}