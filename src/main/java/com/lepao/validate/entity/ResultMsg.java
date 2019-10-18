package com.lepao.validate.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResultMsg {
    private String tid;
    private String resultMsg;
    private String userName;
    private String sellerName;
}
