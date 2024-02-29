package com.itletian.dto;

import com.itletian.entity.OrderDetail;
import com.itletian.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
	
}
