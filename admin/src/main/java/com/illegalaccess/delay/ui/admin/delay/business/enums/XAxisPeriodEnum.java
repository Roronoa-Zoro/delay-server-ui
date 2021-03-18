package com.illegalaccess.delay.ui.admin.delay.business.enums;


/**
 * 趋势图 x-轴 数据间隔
 */
public enum XAxisPeriodEnum {
    X_MIN(1, "1分钟"),
    X_HOUR(2, "1小时"),
    X_DAY(3, "1天"),
    ;

    /**
     * 数据间隔方式，1-分钟；2-小时；3-天
     */
    private Integer type;
    private String desc;

    XAxisPeriodEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
