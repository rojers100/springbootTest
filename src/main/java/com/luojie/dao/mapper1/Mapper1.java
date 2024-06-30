package com.luojie.dao.mapper1;

import com.luojie.moudle.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface Mapper1 {
    void addUser(UserModel userModel);

    UserModel getUser(String userid);
}
