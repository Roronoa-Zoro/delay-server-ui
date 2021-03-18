package com.illegalaccess.delay.ui.admin.delay.business.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Data
public class AppTopicValid implements Serializable {
    @NotEmpty(message = "appkey不能为空")
    private String appKey;
    @NotNull(message = "mq类型不能为空")
    private Integer mqType;
    @NotEmpty(message = "mq topic不能为空")
    private String topic;
    @NotEmpty(message = "创建者不能为空")
    private String creator;
    @NotEmpty(message = "创建者组织不能为空")
    private String creatorOrg;
}