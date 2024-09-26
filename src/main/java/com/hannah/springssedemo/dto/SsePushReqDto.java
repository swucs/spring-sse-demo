package com.hannah.springssedemo.dto;

import lombok.Data;

import java.util.List;

@Data
public class SsePushReqDto {
    private List<String> userIds;
    private String message;
}
