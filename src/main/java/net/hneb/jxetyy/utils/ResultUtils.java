package net.hneb.jxetyy.utils;

/**
 * @author zhangshuai
 */
public class ResultUtils {

    public static <T> Result<T> getResult(Integer code, String msg){
        return new Result<T>(code,msg);
    }

    public static <T> Result<T> getSuccessResult() {
        return new Result<T>(StatusCodeEnum.SUCCESS.getCode());
    }

    public static <T> Result<T> getSuccessResult(String msg) {
        return new Result<T>(StatusCodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> getFailResult(){
        return new Result<T>(StatusCodeEnum.FAIL.getCode());
    }

    public static <T> Result<T> getFailResult(String msg){
        return new Result<T>(StatusCodeEnum.FAIL.getCode(), msg);
    }

    public static <T> Result<T> getMsg(String msg){
        return new Result<T>(msg);
    }

    public static <T> Result<T> getSuccessResultAndData(T data){
        return new Result<T>(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMsg(),data);
    }
}
