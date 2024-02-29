package com.itletian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.dto.OrdersDto;
import com.itletian.entity.AddressBook;
import com.itletian.entity.OrderDetail;
import com.itletian.entity.Orders;
import com.itletian.entity.User;
import com.itletian.mapper.AddressBookMapper;
import com.itletian.mapper.OrdersMapper;
import com.itletian.service.AddressBookService;
import com.itletian.service.OrdersService;
import com.itletian.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public Page<OrdersDto> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
            LocalDateTime begin = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Orders::getOrderTime, begin)
                    .le(Orders::getOrderTime, end);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);
        pageParam = this.page(pageParam, queryWrapper);

        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(pageParam, ordersDtoPage, "records");
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders orders : pageParam.getRecords()) {
            // 设置基本信息
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);

            // 设置用户信息
            LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(User::getId, orders.getUserId());
            User user = userService.getOne(wrapper1);
            ordersDto.setUserName(user.getName());
            ordersDto.setPhone(user.getPhone());

            // 设置地址信息
            LambdaQueryWrapper<AddressBook> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(AddressBook::getId, orders.getAddressBookId());
            AddressBook addressBook = addressBookService.getOne(wrapper2);
            ordersDto.setAddress(addressBook.getDetail());
            ordersDto.setConsignee(addressBook.getConsignee());

            ordersDtoList.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersDtoPage;
    }

    @Override
    public void submit(Orders orders) {

    }
}
