package com.luojie.controImpl;

import com.luojie.dao.mapper2.Mapper2;
import com.luojie.moudle.IdempotenceTestModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdempotenceTestImpl {

    @Autowired
    private Mapper2 mapper2;

    public void setup(String uuid, IdempotenceTestModule module) {
        // 检查是否已经存在
        String getuuid = mapper2.getuuid(uuid);
        if (StringUtils.isNotBlank(getuuid)) {
            throw new IllegalArgumentException("已经处理过了");
        }
        // 不存在则继续
        module.setUuid(uuid);
        mapper2.insertUq(module);
    }

}
