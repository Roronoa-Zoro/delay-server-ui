package com.illegalaccess.delay.ui.modules.delay.business.repository;

import com.illegalaccess.delay.ui.modules.delay.business.domain.AppGrant;
import com.illegalaccess.delay.ui.modules.system.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Repository
public interface AppGrantRepository extends BaseRepository<AppGrant, Long> {

    List<AppGrant> findAllByGranteeAndStatus(String grantee, Byte status);

    AppGrant findAppGrantByGranteeAndAppKey(String grantee, String appKey);
}