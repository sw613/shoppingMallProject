package com.project.elice2.orders.pay;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Amount {
    private int total;
    private int tax_free;
    private int tax;
    private int point;
    private int discount;
    private int green_deposit;
}
