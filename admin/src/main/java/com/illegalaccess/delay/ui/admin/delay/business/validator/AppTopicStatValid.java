package com.illegalaccess.delay.ui.admin.delay.business.validator;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppTopicStatValid implements Serializable {

    private String appKey;

    /**
     * 数据间隔方式，1-分钟；2-小时；3-天
     * @See XAxisPeriodEnum
     */
//    private Integer interval;

    /**
     * 查询数据的区间
     * 2021-03-09 00:00:00 - 2021-03-10 00:00:00
     * 分隔符是' - '
     * 不传值则取当前时间前后一小时
     */
    private String timeRange;

    private List<String> topics = new ArrayList<>();
}
