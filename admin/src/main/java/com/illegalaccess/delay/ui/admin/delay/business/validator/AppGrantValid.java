package com.illegalaccess.delay.ui.admin.delay.business.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Data
public class AppGrantValid implements Serializable {
    @NotEmpty(message = "授权人不能为空")
    private String grantor;
    @NotEmpty(message = "被授权人不能为空")
    private String grantee;
    @NotEmpty(message = "被授权人组织不能为空")
    private String granteeOrg;
}