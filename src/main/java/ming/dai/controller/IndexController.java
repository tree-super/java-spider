package ming.dai.controller;

import lombok.extern.slf4j.Slf4j;
import ming.dai.service.PageDownLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhangshuai
 * @create 2021/09/16 22:50
 */
@Controller
@Slf4j
public class IndexController {

//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private PageDownLoad pageDownLoad;

    @RequestMapping(value = {"/", "/index" })
    public String index(Model model) {
        HashMap<String, String> whResult = pageDownLoad.downWH();
        model.addAttribute("wh", whResult);
        HashMap<String, String> gjzsResult = pageDownLoad.downGJZS();
        model.addAttribute("gjzs", gjzsResult);
        HashMap<String, String> wgjsResult = pageDownLoad.downWGJS();
        model.addAttribute("wgjs", wgjsResult);
        List<String> kxResult = pageDownLoad.downKX();
        model.addAttribute("kx", kxResult);
        return "index";
    }

    @RequestMapping(value = {"/welcome" })
    public String welcome() {
        return "welcome";
    }

}
