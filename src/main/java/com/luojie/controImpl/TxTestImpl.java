package com.luojie.controImpl;

import com.luojie.controImpl.interfaceImpl.TxTestInterface;
import com.luojie.dao.mapper2.Mapper2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TxTestImpl implements TxTestInterface {

    @Autowired
    private Mapper2 mapper2;

    @Transactional(value = "myTransactionManager", rollbackFor = Exception.class, propagation= Propagation.MANDATORY)
    @Override
    public void testTs(String userid, String bookName, Integer amount) {
        // 获取图书的价格
        Integer bookPrice = Integer.valueOf(mapper2.getBookPrice(bookName));
        // 扣去图书库存
        mapper2.buyBook(bookName, amount);
        // 扣去用户余额
        mapper2.userDeductMoney(userid, BigDecimal.valueOf((long) amount * bookPrice));
    }
}
