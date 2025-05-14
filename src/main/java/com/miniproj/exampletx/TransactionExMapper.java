package com.miniproj.exampletx;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionExMapper {

    @Insert("insert into table_a values(#{id}, #{name})")
    int insertTableA(@Param("id") int id, @Param("name") String name);

    @Insert("insert into table_b values(#{id}, #{name})")
    int insertTableB(@Param("id") int id, @Param("name") String name);
}
