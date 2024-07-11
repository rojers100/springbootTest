package com.luojie.moudle;

import lombok.Data;

@Data
public class IdempotenceTestModule {

    private String key;

    private String value;

    private String uuid;
}
