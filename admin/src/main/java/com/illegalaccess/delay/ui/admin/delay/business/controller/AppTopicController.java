package com.illegalaccess.delay.ui.admin.delay.business.controller;

import com.illegalaccess.delay.toolkit.dto.BaseResponse;
import com.illegalaccess.delay.toolkit.enums.MQTypeEnum;
import com.illegalaccess.delay.toolkit.json.JsonTool;
import com.illegalaccess.delay.ui.admin.ObjectConverter;
import com.illegalaccess.delay.ui.admin.delay.business.dto.AppInfo;
import com.illegalaccess.delay.ui.admin.delay.business.validator.AppTopicValid;
import com.illegalaccess.delay.ui.client.DelayUIClient;
import com.illegalaccess.delay.ui.client.dto.app.QueryAppKeyReq;
import com.illegalaccess.delay.ui.client.dto.app.QueryAppKeyResp;
import com.illegalaccess.delay.ui.client.dto.topic.*;
import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.common.utils.EntityBeanUtil;
import com.illegalaccess.delay.ui.common.utils.ResultVoUtil;
import com.illegalaccess.delay.ui.common.utils.StatusUtil;
import com.illegalaccess.delay.ui.common.vo.ResultVo;
import com.illegalaccess.delay.ui.component.shiro.ShiroUtil;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppGrant;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppTopic;
import com.illegalaccess.delay.ui.modules.delay.business.service.AppGrantService;
import com.illegalaccess.delay.ui.modules.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 给appkey下分配topic
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Slf4j
@Controller
@RequestMapping("/application/topic")
public class AppTopicController {

    @DubboReference(interfaceClass = DelayUIClient.class)
    private DelayUIClient delayUIClient;
    @Autowired
    private ObjectConverter objectConverter;

    @Autowired
    private AppGrantService appGrantService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("application:topic:index")
    public String index(Model model, AppTopic appTopic) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();


        BaseResponse<QueryTopicResp> resp = delayUIClient.queryTopics(QueryTopicReq.builder().appKey(appTopic.getAppKey()).build());
        if (resp.OK()) {
            QueryTopicResp response = resp.getContent();
            List<QueryTopicInfo> data = response.getData();
            List<AppTopic> list = objectConverter.toAppTopicList(data);
            Page<AppTopic> page = new PageImpl<>(list);
            model.addAttribute("list", page.getContent());
            model.addAttribute("page", page);
        } else {
            Page<AppTopic> list = new PageImpl<>(new ArrayList<>(0));
            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
        }

        // 获取数据列表
//        Page<AppTopic> list = appTopicService.getPageList(example);

        return "/business/appTopic/index";
    }

    @GetMapping("/queryAppKey4User")
    @RequiresPermissions("application:topic:index")
    @ResponseBody
    public ResultVo queryAppKey4User() {
        User user = ShiroUtil.getSubject();
        BaseResponse<QueryAppKeyResp> baseResponse = delayUIClient.queryAppKey(QueryAppKeyReq.builder().creator(user.getUsername()).build());
        List<AppGrant> granteeApp = appGrantService.getAppKeyList(user.getUsername());

        Set<AppInfo> appInfos = new HashSet<>();
        if (baseResponse.OK() && !CollectionUtils.isEmpty(baseResponse.getContent().getData())) {
            baseResponse.getContent().getData().forEach(res -> appInfos.add(new AppInfo(res.getAppKey(), res.getAppDesc())));
        }

        if (!CollectionUtils.isEmpty(granteeApp)) {
            log.info("user has granted app key list");
            granteeApp.forEach(ga -> appInfos.add(new AppInfo(ga.getAppKey(), ga.getAppDesc())));
        }

        return ResultVoUtil.success(appInfos);
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("application:topic:add")
    public String toAdd() {
        return "/business/appTopic/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("application:topic:edit")
    public String toEdit(@PathVariable("id") AppTopic appTopic, Model model) {
        model.addAttribute("appTopic", appTopic);
        return "/business/appTopic/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"application:topic:add", "application:topic:edit"})
    @ResponseBody
    public ResultVo save(@Validated AppTopicValid valid, AppTopic appTopic) {
        // 复制保留无需修改的数据
        if (appTopic.getId() != null) {
//            AppTopic beAppTopic = appTopicService.getById(appTopic.getId()); todo
            AppTopic beAppTopic = new AppTopic();
            EntityBeanUtil.copyProperties(beAppTopic, appTopic);
        }
        log.info(JsonTool.toJsonString(appTopic));

        BaseResponse<Boolean> baseResp = delayUIClient.queryTopicForApp(QueryApp4TopicReq.builder().appKey(appTopic.getAppKey()).topic(appTopic.getTopic()).build());
        if (baseResp.OK() && baseResp.getContent()) {
            return ResultVoUtil.success("topic已经存在");
        }


        User user = ShiroUtil.getSubject();
        SaveTopicReq saveTopicReq = SaveTopicReq.builder().appKey(appTopic.getAppKey())
                        .creator(user.getUsername())
                        .mqType(MQTypeEnum.getByType(appTopic.getMqType()))
                        .topic(appTopic.getTopic())
                        .creatorOrg(user.getDept().getTitle())
                        .build();
        BaseResponse<SaveTopicResp> baseResponse = delayUIClient.saveTopic(saveTopicReq);
        log.info("save topic for app resp:{}", baseResponse.toJsonString());
        if (baseResponse.OK() && baseResponse.getContent() != null) {
            return ResultVoUtil.SAVE_SUCCESS;
        }
        // 保存数据 todo
//        appTopicService.save(appTopic);
        return ResultVoUtil.error(baseResponse.getErrorMsg());
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("application:topic:detail")
    public String toDetail(@PathVariable("id") AppTopic appTopic, Model model) {
        model.addAttribute("appTopic",appTopic);
        return "/business/appTopic/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("application:topic:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        return ResultVoUtil.success(statusEnum.getMessage() + "成功");

        // todo
//        if (appTopicService.updateStatus(statusEnum, ids)) {
//            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
//        } else {
//            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
//        }
    }
}