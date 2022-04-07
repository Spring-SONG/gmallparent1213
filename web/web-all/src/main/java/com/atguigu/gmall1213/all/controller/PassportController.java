package com.atguigu.gmall1213.all.controller;

import com.atguigu.gmall1213.common.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: songshiqi
 * @Date: 2022/4/7 16:24
 */
@Controller
public class PassportController {

    @GetMapping("login.html")
    public String login(HttpServletRequest request) {
// http://passport.gmall.com/login.html?originUrl=http://www.gmall.com/
        String originUrl=request.getParameter("originUrl");
        request.setAttribute("originUrl", originUrl);
        return "login";

    }
}
