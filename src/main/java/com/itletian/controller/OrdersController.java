package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.config.mybaitPlus.BaseContext;
import com.itletian.dto.OrdersDto;
import com.itletian.entity.Orders;
import com.itletian.service.OrdersService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 分页查询订单信息
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        Page<OrdersDto> ordersDtoPage = ordersService.getPage(page, pageSize, number, beginTime, endTime);
        return R.success(ordersDtoPage);
    }

    /**
     * 下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("{}", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 查看用户的订单
     */
    @GetMapping("/userPage")
    public R<Page> queryUserOrderPage(Integer page, Integer pageSize) {
        Page<OrdersDto> ordersDtoPage = ordersService.getUserOrderPage(page, pageSize);
        return R.success(ordersDtoPage);
    }
}
