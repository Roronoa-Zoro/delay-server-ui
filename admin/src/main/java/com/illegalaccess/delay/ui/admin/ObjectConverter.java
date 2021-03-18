package com.illegalaccess.delay.ui.admin;

import com.illegalaccess.delay.ui.client.dto.topic.QueryTopicInfo;
import com.illegalaccess.delay.ui.modules.delay.business.domain.AppTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectConverter {

    @Mappings(value = {
            @Mapping(target = "createDate", ignore = true),
            @Mapping(target = "updateDate", ignore = true)
    })
    AppTopic toAppTopic(QueryTopicInfo info);

    List<AppTopic> toAppTopicList(List<QueryTopicInfo> info);
}
