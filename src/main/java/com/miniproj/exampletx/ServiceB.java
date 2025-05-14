package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceB {

    private final TransactionExMapper mapper;

    private final ServiceA serviceA;

    @Transactional(rollbackFor = Exception.class)
    public void parentServiceB(){
        log.info("parentServiceB() 시작....................");

        mapper.insertTableA(1, "a");

        serviceA.saveBServiceA();

        mapper.insertTableA(1, "aa");

        log.info("parentServiceB() 종료....................");
    }

}
