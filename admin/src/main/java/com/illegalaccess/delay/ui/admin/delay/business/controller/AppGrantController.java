package com.illegalaccess.delay.ui.admin.delay.business.controller;

import com.illegalaccess.delay.ui.admin.delay.business.validator.AppGrantValid;
import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.common.utils.EntityBeanUtil;
import com.illegalaccess.delay.ui.common.utils.ResultVoUtil;
import com.illegalaccess.delay.ui.common.utils.StatusUtil;
import com.illegalaccess.delay.ui.common.vo.ResultVo;
import com.illegalaccess.delay.ui.component.shiro.ShiroUtil;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppGrant;
import com.illegalaccess.delay.ui.modules.delay.business.service.AppGrantService;
import com.illegalaccess.delay.ui.modules.system.domain.User;
import com.illegalaccess.delay.ui.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * appkey授权操作controller
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Slf4j
@Controller
@RequestMapping("/application/appGrant")
public class AppGrantController {

    @Autowired
    private AppGrantService appGrantService;

    @Autowired
    private UserService userService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("application:appGrant:index")
    public String index(Model model, AppGrant appGrant) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<AppGrant> example = Example.of(appGrant, matcher);
        Page<AppGrant> list = appGrantService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/business/appGrant/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("application:appGrant:add")
    public String toAdd() {
        return "/business/appGrant/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("application:appGrant:edit")
    public String toEdit(@PathVariable("id") AppGrant appGrant, Model model) {
        model.addAttribute("appGrant", appGrant);
        return "/business/appGrant/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"application:appGrant:add", "application:appGrant:edit"})
    @ResponseBody
    public ResultVo save(@Validated AppGrantValid valid, AppGrant appGrant) {
        // 复制保留无需修改的数据
        if (appGrant.getId() != null) {
            AppGrant beAppGrant = appGrantService.getById(appGrant.getId());
            EntityBeanUtil.copyProperties(beAppGrant, appGrant);
            log.info("will update appGrant with id:{}", appGrant.getId());
        }

        User user = ShiroUtil.getSubject();
        User granteeUser = userService.getByName(appGrant.getGrantee());

        appGrant.setGrantor(user.getDept().getTitle());
        appGrant.setGranteeOrg(granteeUser.getDept().getTitle());

        // 保存数据
        appGrantService.save(appGrant);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("application:appGrant:detail")
    public String toDetail(@PathVariable("id") AppGrant appGrant, Model model) {
        model.addAttribute("appGrant",appGrant);
        return "/business/appGrant/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("application:appGrant:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (appGrantService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}