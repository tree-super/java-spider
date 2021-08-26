/** 日期工具 */

// 计算年龄
// 参数，出生日期的年，月，日正整数值
var calculateDate = function(year, month, date) {

    // 当前日期
    var today = new Date();
    // 当前年月日
    var t_year  = today.getFullYear();
    var t_month = today.getMonth() + 1;
    var t_date  = today.getDate();

    // 出生日期，月份值从0开始
    // var birthday = new Date();
    // birthday.setFullYear(year,month-1,date);

    var largeMonths = [1,3,5,7,8,10,12], // 大月， 用于计算天，只在年月都为零时，天数有效
        lastMonth = (t_month - 1) > 0?t_month-1:12,  // 当前日期上月月份
        isLeapYear = false, // 闰年标识
        daysOFMonth = 0;    // 当前日期上月天数

    // 是否闰年，用于计算天，只在年月都为零时，天数有效
    if((t_year%4===0 && t_year%100!==0) || t_year%400===0){  
        isLeapYear = true;
    }

    // 当前日期的上一个月多少天
    if(largeMonths.indexOf(lastMonth)>-1){
        daysOFMonth = 31;
    }else if(lastMonth===2){
        if(isLeapYear){
            daysOFMonth = 29;
        }else{
            daysOFMonth = 28;
        }
    }else{
        daysOFMonth = 30;
    }

    // 当前日期和出生日期的年月日差值，当前日期大于等于出生日期
    var Y = t_year - parseInt(year);
    var M = t_month - parseInt(month);
    var D = t_date - parseInt(date);

    if(D < 0){ // 出生日小于本日
        M--;
        D = D + daysOFMonth; // 借一个月，上月天数
    }
    if(M < 0){ // 出生月小于本月，或因出生日小于本日，借走一月的情况
        Y--;
        M = M + 12; // 借一年 12个月
    }

    if(Y < 0){ // 出生年不可大于当前年，当年出生不存在借年情况
        return;
    }

    return " " + Y + " 岁 " + M + " 个月 " + D + " 天";
};

/**
// 计算年龄，几岁几个月几天
var calcAge = function(year,month,date) {

    // 当前日期
    var today = new Date();
    // 当前年月日
    var t_year  = today.getFullYear();
    var t_month = today.getMonth() + 1;
    var t_date  = today.getDate();

    // 出生日期，月份值从0开始
    // var birthday = new Date();
    // birthday.setFullYear(year,month-1,date);

    var largeMonths = [1,3,5,7,8,10,12], // 大月， 用于计算天，只在年月都为零时，天数有效
        lastMonth = (t_month - 1) > 0?t_month-1:12,  // 当前日期上月月份
        isLeapYear = false, // 闰年标识
        daysOFMonth = 0;    // 当前日期上月天数

    // 是否闰年，用于计算天，只在年月都为零时，天数有效
    if((t_year%4===0 && t_year%100!==0) || t_year%400===0){  
        isLeapYear = true;
    }

    // 当前日期的上一个月多少天
    if(largeMonths.indexOf(lastMonth)>-1){
        daysOFMonth = 31;
    }else if(lastMonth===2){
        if(isLeapYear){
            daysOFMonth = 29;
        }else{
            daysOFMonth = 28;
        }
    }else{
        daysOFMonth = 30;
    }

    // 当前日期和出生日期的年月日差值，当前日期大于等于出生日期
    var Y = t_year - parseInt(year);
    var M = t_month - parseInt(month);
    var D = t_date - parseInt(date + 1); // 0天补齐1天

    if(D < 0){
        M--;
        D = D + daysOFMonth; // 借一个月，上月天数
    }
    if(M < 0){
        Y--;
        M = M + 12; // 借一年 12个月
    }

    if(Y < 0){
        returnStr = "出生日期有误！";
    }
    else if(Y===0){
        if(M===0){
            returnStr = D+"天";
        }else{
            returnStr = M+"个月";
        }
    }else{
        if(M===0){
            returnStr = Y+"岁";
        }else{
            returnStr = Y+"岁"+M+"个月";
        }
    }

    return returnStr;
};
*/