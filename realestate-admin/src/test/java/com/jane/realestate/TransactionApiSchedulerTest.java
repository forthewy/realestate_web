package com.jane.realestate;

import com.jane.realestate.scheduler.TransactionApiScheduler;
import com.jane.realestate.service.TransactionApiImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class TransactionApiSchedulerTest {


    @Autowired
    private TransactionApiScheduler scheduler;

    @MockitoBean
    private TransactionApiImportService transactionApiImportService;

    @Test
    void schedulerTest() {
        scheduler.collectTransactions();

        verify(transactionApiImportService, times(1))
                .collectAllRegions();
    }


}
