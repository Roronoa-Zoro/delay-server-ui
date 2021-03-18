package com.illegalaccess.delay.ui.admin.delay.business.controller;

import com.google.common.collect.Iterables;
import com.illegalaccess.delay.toolkit.TimeUtils;
import com.illegalaccess.delay.toolkit.dto.BaseResponse;
import com.illegalaccess.delay.toolkit.json.JsonTool;
import com.illegalaccess.delay.ui.admin.delay.business.constant.BusinessConstant;
import com.illegalaccess.delay.ui.admin.delay.business.dto.LineJsonData;
import com.illegalaccess.delay.ui.admin.delay.business.dto.LineData;
import com.illegalaccess.delay.ui.admin.delay.business.validator.AppTopicStatValid;
import com.illegalaccess.delay.ui.client.DelayUIClient;
import com.illegalaccess.delay.ui.client.dto.TrendInfo;
import com.illegalaccess.delay.ui.client.dto.app.QueryAppTopicStatReq;
import com.illegalaccess.delay.ui.client.dto.app.QueryAppTopicStatResp;
import com.illegalaccess.delay.ui.client.dto.topic.QueryTopicInfo;
import com.illegalaccess.delay.ui.client.dto.topic.QueryTopicReq;
import com.illegalaccess.delay.ui.client.dto.topic.QueryTopicResp;
import com.illegalaccess.delay.ui.common.utils.ResultVoUtil;
import com.illegalaccess.delay.ui.common.vo.ResultVo;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * appkey维度下的监控controller
 * @author Jimmy Li
 */
@Slf4j
@Controller
@RequestMapping("/application/appMonitor")
public class AppTopicMonitorController {

    @DubboReference(interfaceClass = DelayUIClient.class, timeout = 10000)
    private DelayUIClient delayUIClient;

    @GetMapping("/index")
    @RequiresPermissions("application:appMonitor:index")
    public String index(Model model, AppTopic appTopic) {
            Page<Object> list = new PageImpl<>(new ArrayList<>(0));
            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
        // 获取数据列表
//        Page<AppTopic> list = appTopicService.getPageList(example);

        return "/business/appMonitor/AppMonitor";
    }

    @GetMapping("/queryTopics")
    @RequiresPermissions("application:appMonitor:queryTopics")
    @ResponseBody
    public ResultVo queryTopics(@RequestParam("appKey") String appKey) {
        BaseResponse<QueryTopicResp> resp = delayUIClient.queryTopics(QueryTopicReq.builder().appKey(appKey).build());
        List<String> topicList;
        if (resp.OK()) {
            QueryTopicResp response = resp.getContent();
            List<QueryTopicInfo> data = response.getData();
            topicList = new ArrayList<>();
            data.forEach(queryTopicInfo -> topicList.add(queryTopicInfo.getTopic()));

        } else {
            topicList = new ArrayList<>(0);
        }

        return ResultVoUtil.success(topicList);
    }

    @PostMapping("/search")
    @RequiresPermissions("application:appMonitor:search")
    @ResponseBody
    public ResultVo search(@RequestBody AppTopicStatValid appTopicStat) {
        log.info("query topic cnt :{}", JsonTool.toJsonString(appTopicStat));
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (StringUtils.isNotBlank(appTopicStat.getTimeRange())) {
            String[] range = appTopicStat.getTimeRange().split(BusinessConstant.dateRangeSplitter);
            start = TimeUtils.parseLocalDateTime(range[0]);
            end = TimeUtils.parseLocalDateTime(range[1]);
        }

        QueryAppTopicStatReq queryAppTopicStatReq = QueryAppTopicStatReq.builder()
                .appKey(appTopicStat.getAppKey())
                .topics(appTopicStat.getTopics())
                .start(start)
                .end(end)
                .build();

        BaseResponse<QueryAppTopicStatResp> baseResponse = delayUIClient.queryAppTopicStat(queryAppTopicStatReq);
        log.info("queryAppTopicStat resp:{}", baseResponse.getErrorMsg());
        if (baseResponse.OK() && baseResponse.getContent().getTrendInfo() != null) {
            QueryAppTopicStatResp resp = baseResponse.getContent();
            TrendInfo trendInfo = resp.getTrendInfo();
            String[] xcontent = Iterables.toArray(trendInfo.getXContent(), String.class);
            List<LineJsonData> data = new ArrayList<>(trendInfo.getData().size());
            trendInfo.getData().forEach(pair -> {
                data.add(LineJsonData.builder().name(pair.getFirst()).data(Iterables.toArray(pair.getSecond(), Integer.class)).build());
            });

            LineData tld = new LineData();
            tld.setXcontent(xcontent);
            tld.setData(data);
            log.info("data is returned");
            return ResultVoUtil.success(tld);
        }

        log.info("does not get any data, return nothing");

        return ResultVoUtil.success(null);
    }

    private LineData createDummyData(int loop) {
//        int loop = Integer.valueOf(appTopicStat.getAppKey());
//        Integer[] data1 = {120, 132, 101, 134, -90, 230, 210};
//        Integer[] data2 = {20, 232, 11, -144, 90, 30, 90};
//        Integer[] data3 = {180, -12, 161, 84, 60, 230, -10};

        Integer[] data1 = new Integer[loop];
        Integer[] data2 = new Integer[loop];
        Integer[] data3 = new Integer[loop];

        String[] xcontent = new String[loop];
        LocalDateTime now = LocalDateTime.of(2021, 3, 17, 0, 0);

        Random r = new Random();
        for (int i = 0; i < loop; i++) {
            data1[i] = r.nextInt(loop);
            data2[i] = r.nextInt(loop);
            data3[i] = r.nextInt(loop);
            now = now.plusMinutes(2);
            xcontent[i] = DateTimeFormatter.ofPattern("YYYY-mm-dd HH:MM:SS").format(now);
        }

        LineJsonData jd1 = new LineJsonData();
        jd1.setName("测试数据1");
        jd1.setData(data1);

        LineJsonData jd2 = new LineJsonData();
        jd2.setName("测试数据2");
        jd2.setData(data2);

        LineJsonData jd3 = new LineJsonData();
        jd3.setName("测试数据3");
        jd3.setData(data3);

        List<LineJsonData> data = new ArrayList<LineJsonData>();
        data.add(jd1);
        data.add(jd2);
        data.add(jd3);

//        String[] xcontent = {"a","b","c","d","e","f","g"};


        LineData tld = new LineData();
        tld.setXcontent(xcontent);
        tld.setData(data);

        return tld;
    }
}
