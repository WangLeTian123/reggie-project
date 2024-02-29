package com.itletian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.config.mybaitPlus.BaseContext;
import com.itletian.dto.OrdersDto;
import com.itletian.entity.*;
import com.itletian.mapper.AddressBookMapper;
import com.itletian.mapper.OrdersMapper;
import com.itletian.service.*;
import com.itletian.util.CustomException;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;
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
        // 1、查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户地址是否有误
        User user = userService.getById(BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        // 2、向订单表中插入一条数据
        // 3、向订单明细表中插入数据（多条）
        long orderId = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            details.add(orderDetail);
        }
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);
        orderDetailService.saveBatch(details);
        // 4、清空购物车
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page<OrdersDto> getUserOrderPage(Integer page, Integer pageSize) {
        User user = userService.getById(BaseContext.getCurrentId());
        Page<Orders> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        List<Orders> orders = this.list(queryWrapper);
        pageParam = this.page(pageParam, queryWrapper);

        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(pageParam, ordersDtoPage, "records");
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders order : orders) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            ordersDto.setUserName(user.getName());

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, order.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtoList.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersDtoPage;
    }
}
