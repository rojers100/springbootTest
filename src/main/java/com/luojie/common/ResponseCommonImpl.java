package com.luojie.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommonImpl implements Serializable {

    int code;

    String msg;

    Object entity;
}
