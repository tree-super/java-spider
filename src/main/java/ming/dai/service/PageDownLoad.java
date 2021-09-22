package ming.dai.service;

import java.util.HashMap;
import java.util.List;

/**
 * 页面下载接口
 */
public interface PageDownLoad {
    HashMap<String, String> downWH();
    HashMap<String, String> downGJZS();
    HashMap<String, String> downWGJS();
    List<String> downKX();
}
