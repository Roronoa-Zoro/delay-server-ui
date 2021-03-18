package com.illegalaccess.delay.ui.admin.delay.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AppInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    private String appKey;

    private String appDesc;
}
