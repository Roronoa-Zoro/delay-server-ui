package com.illegalaccess.delay.ui.modules.delay.business.service.impl;

import com.illegalaccess.delay.ui.common.data.PageSort;
import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppGrant;
import com.illegalaccess.delay.ui.modules.delay.business.repository.AppGrantRepository;
import com.illegalaccess.delay.ui.modules.delay.business.service.AppGrantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Slf4j
@Service
public class AppGrantServiceImpl implements AppGrantService {

    @Autowired
    private AppGrantRepository appGrantRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public AppGrant getById(Long id) {
        return appGrantRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<AppGrant> getPageList(Example<AppGrant> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return appGrantRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param appGrant 实体对象
     */
    @Override
    public AppGrant save(AppGrant appGrant) {
        return appGrantRepository.save(appGrant);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return appGrantRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public List<AppGrant> getAppKeyList(String grantee) {
        log.info("find all appkey for grantee:{}", grantee);
        List<AppGrant> data = appGrantRepository.findAllByGranteeAndStatus(grantee, StatusEnum.OK.getCode());
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>(0);
        }

        return data;
    }

    @Override
    public boolean appGranted(String appKey, String grantee) {
        AppGrant appGrant = appGrantRepository.findAppGrantByGranteeAndAppKey(grantee, appKey);
        return appGrant != null ? true : false;
    }
}