package com.illegalaccess.delay.ui.modules.delay.business.service;

import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppGrant;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
public interface AppGrantService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<AppGrant> getPageList(Example<AppGrant> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    AppGrant getById(Long id);

    /**
     * 保存数据
     * @param appGrant 实体对象
     */
    AppGrant save(AppGrant appGrant);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    List<AppGrant> getAppKeyList(String grantee);

    /**
     * appkey是否被授权给grantee
     * @param appKey
     * @param grantee
     * @return
     */
    boolean appGranted(String appKey, String grantee);
}