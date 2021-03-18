package com.illegalaccess.delay.ui.modules.delay.business.domain;

import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.common.utils.StatusUtil;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Jimmy Li
 * @date 2021/03/10
 */
@Data
@Entity
@Table(name="delay_app_topic")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class AppTopic implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
    // appkey
    private String appKey;
    // mq类型
    private Integer mqType;
    // mq topic
    private String topic;
    // 创建者
    private String creator;
    // 创建者组织
    private String creatorOrg;
}