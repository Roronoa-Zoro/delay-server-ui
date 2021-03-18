package com.illegalaccess.delay.ui.admin.delay.business.controller;

import com.illegalaccess.delay.toolkit.dto.BaseResponse;
import com.illegalaccess.delay.ui.admin.delay.business.validator.ApplyAppReq;
import com.illegalaccess.delay.ui.client.DelayUIClient;
import com.illegalaccess.delay.ui.client.dto.app.*;
import com.illegalaccess.delay.ui.common.utils.HttpServletUtil;
import com.illegalaccess.delay.ui.common.utils.ResultVoUtil;
import com.illegalaccess.delay.ui.common.vo.ResultVo;
import com.illegalaccess.delay.ui.component.shiro.ShiroUtil;
import com.illegalaccess.delay.ui.modules.system.domain.Dept;
import com.illegalaccess.delay.ui.modules.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

/**
 * @author Jimmy Li
 * app管理 controller
 */
@Slf4j
@Controller
@RequestMapping("/application/app")
public class AppManagementController {

    @DubboReference(interfaceClass = DelayUIClient.class)
    private DelayUIClient delayUIClient;

    /**
     * 跳转到列表页面
     */
    @GetMapping("/management")
    @RequiresPermissions("application:app:management")
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        log.info("search========{}", search);
        User user = ShiroUtil.getSubject();
        BaseResponse<QueryAppKeyResp> baseResponse = delayUIClient.queryAppKey(QueryAppKeyReq.builder().creator(user.getUsername()).build());
        if (baseResponse.OK()) {

            Page<QueryAppKeyInfo> list = new PageImpl<>(baseResponse.getContent().getData());
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
        } else {
            Page<Object> list = new PageImpl<Object>(new ArrayList<>());
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
        }


        return "/business/appManagement/AppManagement";
    }

    @RequiresPermissions("application:app:applyApp")
    @PostMapping("/applyAppKey")
//    @ActionLog(key = "申请appkey", action = SaveAction.class)
    public ResultVo applyAppKey(@Validated ApplyAppReq valid, ApplyAppReq req) {
//        storeApi.
        // todo
        User user = ShiroUtil.getSubject();
        Dept dept = user.getDept();
        System.out.println(req);
        System.out.println(dept.getTitle());

        BaseResponse<SaveAppKeyResp> baseResponse = delayUIClient.saveAppKey(SaveAppKeyReq.builder().appDesc(req.getAppDesc()).creator(user.getUsername()).creatorOrg(dept.getTitle()).build());
        log.info("apply appkey resp:{}", baseResponse.toJsonString());
        if (baseResponse.OK() && baseResponse.getContent() != null) {
            return ResultVoUtil.SAVE_SUCCESS;
        }

        return ResultVoUtil.error("申请appkey失败");
    }

    @RequiresPermissions("application:app:applyApp")
    @GetMapping("/toApplyAppKey")
    public String toApplyAppKey() {
        return "/business/appManagement/add";
    }

}
