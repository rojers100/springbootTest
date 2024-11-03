package com.luojie.dao.mapper2;

import com.luojie.moudle.IdempotenceTestModule;
import com.luojie.moudle.LibraryModel;
import com.luojie.moudle.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface Mapper2 {

    void addUserBalance(UserModel model);

    void addLibrary(LibraryModel model);

    void buyBook(@Param("name") String name, @Param("amount")Integer amount);

    void userDeductMoney(@Param("userid")String userid, @Param("money") BigDecimal money);

    Integer getBookPrice(String name);

    String getuuid(String uuid);

    String getOne(@Param("uuid")String uuid, @Param("key") String key, @Param("value") String value);

    void insertUq(IdempotenceTestModule module);
}
