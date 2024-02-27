package com.itletian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.AddressBook;
import com.itletian.mapper.AddressBookMapper;
import com.itletian.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
