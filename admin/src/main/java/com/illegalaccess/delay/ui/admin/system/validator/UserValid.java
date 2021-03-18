package com.illegalaccess.delay.ui.admin.system.validator;

import com.illegalaccess.delay.ui.modules.system.domain.Dept;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Data
public class UserValid implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "用户昵称不能为空")
    @Size(min = 2, message = "用户昵称：请输入至少2个字符")
    private String nickname;
    private String confirm;
    @NotNull(message = "所在部门不能为空")
    private Dept dept;
}
