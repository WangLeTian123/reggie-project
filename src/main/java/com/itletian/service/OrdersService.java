package com.itletian.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itletian.dto.OrdersDto;
import com.itletian.entity.Orders;

import java.time.LocalDateTime;

public interface OrdersService extends IService<Orders> {
    Page<OrdersDto> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime);

    void submit(Orders orders);
}
