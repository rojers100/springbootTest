package com.luojie.moudle;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RedisEventModule {

    @NonNull
    private String key;

    private String value;

    private String field;

    private int timeout;

    private TimeUnit timeoutUnit;
}
