package com.luojie.moudle;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LibraryModel {

    private String name;

    private BigDecimal price;

    private int amount;

    private int id;
}
