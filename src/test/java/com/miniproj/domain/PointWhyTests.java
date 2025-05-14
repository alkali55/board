package com.miniproj.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

@SpringBootTest
@Slf4j
public class PointWhyTests {

    @Test
    public void testPointWhy(){

        // values(), name()
        for (PointWhy reason : PointWhy.values()){
            System.out.println(reason.name() + " : " + reason.getScore());
        }

        // ordinal()
        PointWhy login = PointWhy.valueOf("LOGIN");
        PointWhy reply = PointWhy.valueOf("REPLY");
        System.out.println("ordinal : " + login.ordinal());
        System.out.println("ordinal : " + reply.ordinal());

        // enum 비교 ( == )
        if (login == PointWhy.LOGIN){
            System.out.println("login == PointWhy.LOGIN 일치");
        }

        PointWhy reason = PointWhy.WRITE;
        if (reason.equals(PointWhy.WRITE)){
            System.out.println("reason.equals(PointWhy.WRITE) 일치");
        }

        if(reason.name().equals("WRITE")){
            System.out.println("reason.name().equals('WRITE') 일치");
        }


    }
}
