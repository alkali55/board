package com.miniproj.exampletx;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final TransactionExMapper transactionExMapper;

    public void insertWithoutTx(){

        transactionExMapper.insertTableA(1, "a"); // insert됨
        transactionExMapper.insertTableA(1, "aa"); // insert되지 않음.
    }

    @Transactional
    public void insertWithTx(){

        transactionExMapper.insertTableA(1, "a");
        transactionExMapper.insertTableA(1, "aa"); // pk 위반 -> 롤백
    }

    @Transactional
    public void insertWithTxSyntax(){

        transactionExMapper.insertTableA(1, "a");
        transactionExMapper.insertTableB(2, "b"); // pk 위반 -> 롤백
    }

    // 예외 발생
    @Transactional
    public void insertWithTxRuntimeEx(){
        transactionExMapper.insertTableA(1, "a");
        transactionExMapper.insertTableB(2, "aa");
        throw new RuntimeException("런타임예외"); // 런타임예외, error 는 자동 롤백
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void insertWithTxException() throws Exception {
        transactionExMapper.insertTableA(1, "a");
        transactionExMapper.insertTableB(2, "aa");
        throw new Exception("예외발생"); // checked 예외는 자동롤백을 해주지 않으므로, rollbackFor = Exception.class 옵션을 추가해야한다.
    }


    // parent()메서드에 @Transactional이 없는 경우 : child메서드에서 롤백되지 않음.
    @Transactional
    public void parent(){
        transactionExMapper.insertTableA(1, "a");
        child();
    }


    @Transactional
    public void child(){
        transactionExMapper.insertTableB(1, "b");
        throw new RuntimeException("런타임 예외 : child()");
    }



}
