package com.illegalaccess.delay.ui.admin.delay.business.validator;

import lombok.Data;

import java.io.Serializable;

@Data
public class GrantAppReq implements Serializable {

    /**
     * 接入方appkey
     */
    private String appKey;

    /**
     * 授权人
     */
    private String grantor;

    /**
     * 被授权人
     */
    private String grantee;

    /**
     * 被授权人组织机构
     */
    private String granteeOrg;
}
