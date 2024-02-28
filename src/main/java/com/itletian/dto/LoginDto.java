package com.itletian.dto;

import lombok.Data;

@Data
public class LoginDto {
    // 电话
    private String phone;

    // 验证码
    private String code;

}
