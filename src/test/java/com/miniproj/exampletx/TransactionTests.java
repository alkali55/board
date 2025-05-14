package com.miniproj.exampletx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TransactionTests {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private ServiceA serviceA;

    @Autowired
    private ServiceB serviceB;

    @Test
    void testInsertWithoutTx(){

        sampleService.insertWithoutTx();
    }

    @Test
    void testInsertWithTx(){
        sampleService.insertWithTx();
    }

    @Test
    void testInsertWithTxSystax(){
        sampleService.insertWithTxSyntax();
    }

    @Test
    void testInsertWithTxRuntimeEx(){
        sampleService.insertWithTxRuntimeEx();
    }

    @Test
    void testInsertWithTxException() {
        try {
            sampleService.insertWithTxException();
        } catch (Exception e) {
            log.info("예외 발생.......");
        }
    }

    @Test
    void testParent(){
        sampleService.parent();
    }

    @Test
    void testInsertA(){
        serviceA.parentServiceA();;
    }

    @Test
    void testServiceBInsertA(){
        serviceB.parentServiceB();
    }

}
