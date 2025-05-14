package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceA {

    private final TransactionExMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public void parentServiceA(){
        log.info("parentServiceA() 시작....................");

        mapper.insertTableA(1, "a");
        saveBServiceA();
        mapper.insertTableA(1, "aa");

        log.info("parentServiceA() 종료....................");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBServiceA(){
        log.info("saveBServiceA() 시작.........................");
        mapper.insertTableB(1, "b");
        mapper.insertTableB(2, "bb");


        log.info("saveBServiceA() 종료.........................");
    }
}
