package net.hneb.jxetyy.common.mapper;

import com.alibaba.druid.util.StringUtils;
import net.hneb.jxetyy.utils.MapBuilder;

import java.util.ArrayList;
import java.util.Map;

public class SearchFilters extends ArrayList<SearchFilter> implements  java.io.Serializable {
    String whereSql;

    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public SearchFilters(){
    }

    public SearchFilters(String whereSql){
        this.setWhereSql(whereSql);
    }

    public SearchFilters(SearchFilter filter){
        this.add(filter);
    }

    public SearchFilters(String field,SearchOp op,Object value){
        this.add(new SearchFilter(field,op,value));
    }

    public SearchFilters add(String field,SearchOp op,Object value){
        this.add(new SearchFilter(field,op,value));
        return this;
    }

    public Map<String,Object> toParamMap(){
        if(!StringUtils.isEmpty(this.whereSql))
            this.add("_whereSql_",SearchOp.EQ, whereSql);
        if(this.size() == 0)
            return MapBuilder.create().getMap();
        return MapBuilder.create("_filters_", this).getMap();
    }

    public static SearchFilters EMPTY = new SearchFilters();
}
