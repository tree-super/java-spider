package net.hneb.jxetyy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhangshuai
 * @create 2021/08/9 22:50
 */
@Controller
@Slf4j
public class IndexController {


    @RequestMapping(value = {"/", "/index" })
    public String index() {
        return "index";
    }

    @RequestMapping(value = {"/welcome" })
    public String welcome() {
        return "welcome";
    }

}
