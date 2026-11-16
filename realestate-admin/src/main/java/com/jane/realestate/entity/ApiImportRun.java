package com.jane.realestate.entity;

import com.jane.realestate.enums.ApiImportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_import_runs")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiImportRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiImportStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Integer successCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer failureCount = 0;

    public void finish(int successCount, int failureCount) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.finishedAt = LocalDateTime.now();

        if (failureCount == 0) {
            this.status = ApiImportStatus.SUCCESS;
        } else if (successCount == 0) {
            this.status = ApiImportStatus.FAILED;
        } else {
            this.status = ApiImportStatus.PARTIAL_SUCCESS;
        }
    }

    public void fail(int successCount, int failureCount) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.finishedAt = LocalDateTime.now();
        this.status = ApiImportStatus.FAILED;
    }
}