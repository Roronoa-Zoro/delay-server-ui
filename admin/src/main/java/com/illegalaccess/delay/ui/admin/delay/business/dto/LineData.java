package com.illegalaccess.delay.ui.admin.delay.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 折线图数据模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineData implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * x轴 数据点
     */
    private String[] xcontent;

    /**
     * 折线数据
     */
    private List<LineJsonData> data;
}
