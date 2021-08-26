package net.hneb.jxetyy.common.mapper;

public class SearchFilter implements  java.io.Serializable {

    private String field;
    private SearchOp op;
    private Object value;

    public SearchFilter() {
    }

    public SearchFilter(String field,SearchOp op, Object value){
        this.field = field;
        this.op = op;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SearchOp getOp() {
        return op;
    }

    public void setOp(SearchOp op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
