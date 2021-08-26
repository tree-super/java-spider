package net.hneb.jxetyy.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangshuai on 2020/11/18.
 */
public class MapBuilder {
    Map<String, Object> map;

    private MapBuilder() {
        this(new HashMap<>());
    }

    private MapBuilder(Map<String, Object> map) {
        this.map = map;
    }

    public static MapBuilder create() {
        return new MapBuilder();
    }

    public static MapBuilder create(String key,Object obj) {
        return new MapBuilder().put(key,obj);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public MapBuilder put(String key, Object obj) {
        this.map.put(key, obj);
        return this;
    }

}
