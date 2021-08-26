package net.hneb.jxetyy.service;

import com.alibaba.fastjson.JSONObject;
import net.hneb.jxetyy.entity.LbpcNo;
import net.hneb.jxetyy.entity.LbpcQuest;

/**
 * @author zhangshuai
 */
public interface LbNoService {
    /**
     * @return
     */
    LbpcNo selectByLbId(String lbId);

    JSONObject submit(LbpcQuest quest, JSONObject basicMap, JSONObject custData);
}
