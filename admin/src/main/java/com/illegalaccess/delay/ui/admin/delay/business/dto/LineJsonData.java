package com.illegalaccess.delay.ui.admin.delay.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineJsonData implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 折线名称
     */
    private String name;

    /**
     * 折线数据点
     */
    private Integer[] data;
}
