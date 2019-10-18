package com.lepao.validate.entity;

import lombok.Data;

import java.util.List;
@Data
public class Order {
    private List<ChildenOrder> Orders;
    private String BuyerNick;
    private String Tid;
    private String Payment;
    private String TotalFee;
    private String SellerNick;
}
