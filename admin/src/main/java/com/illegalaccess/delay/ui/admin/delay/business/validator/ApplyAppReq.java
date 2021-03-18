package com.illegalaccess.delay.ui.admin.delay.business.validator;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 *
 * @author Jimmy Li
 * @date 2021-03-09 17:36
 */
@Data
public class ApplyAppReq implements Serializable {

    @NotEmpty(message = "应用描述不能为空")
    private String appDesc;

//    @NotEmpty(message = "创建者组织")
    private String creatorOrg;
}
