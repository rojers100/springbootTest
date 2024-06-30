package com.luojie.controImpl;

import com.luojie.dao.mapper1.Mapper1;
import com.luojie.dao.mapper2.Mapper2;
import com.luojie.moudle.LibraryModel;
import com.luojie.moudle.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseTestImpl {

    @Autowired
    private Mapper1 mapper1;

    @Autowired
    private Mapper2 mapper2;

    public void add() {
        UserModel userModel = new UserModel();
        userModel.setUsername("Rojer");
        userModel.setSex("男");
        userModel.setUserid("007");
        userModel.setRoles("admin,member");
        userModel.setMoney("10");

        LibraryModel libraryModel = new LibraryModel();
        libraryModel.setId(1);
        libraryModel.setAmount(1);
        mapper2.addLibrary(libraryModel);
        mapper2.addUserBalance(userModel);
        mapper1.addUser(userModel);
    }
}
