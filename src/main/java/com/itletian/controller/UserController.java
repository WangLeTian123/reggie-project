package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itletian.dto.LoginDto;
import com.itletian.entity.User;
import com.itletian.service.UserService;
import com.itletian.util.R;
import com.itletian.util.SMSUtils;
import com.itletian.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.SM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);

            // 调用阿里云提供的短信服务API完成短信发送
            SMSUtils.sendMessage("letian123", "SMS_465385761", phone, code);

            //将生成的验证码保存到session中，方便之后登录中验证码的判断
            session.setAttribute(phone, code);
            return R.success("手机短信验证码发送成功！");
        }
        return R.error("手机短信验证码发送失败！");
    }

    /**
     * 用户登录功能
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody LoginDto loginDto, HttpSession session) {
        log.info(loginDto.toString());
        //获取手机号
        String phone = loginDto.getPhone();
        //获取验证码
        String code = loginDto.getCode();

        // 比较手机号和验证码是否正确
        Object codeAttribute = session.getAttribute(phone);
        if (codeAttribute != null && codeAttribute.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            // 判断数据库中是否存在该用户，如果不存在表示新用户（就新建一个，表示新用户自动完成注册）
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            // 将已登录状态保存到域中
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登陆失败！");
    }

}
