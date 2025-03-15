package com.luojie.moudle;

import lombok.Data;

@Data
public class UserModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L; // 版本号

    private String username;

    private String money;

    private String sex;

    private String roles;

    private String userid;
}
