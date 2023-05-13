package org.crazy.spring.auth.service.impl;

import com.springcloud.common.web.entity.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.crazy.spring.auth.provider.ResourceProvider;
import org.crazy.spring.auth.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupService implements IGroupService {

//    @Qualifier("o.springboot.cloud.auth.authentication.provider.ResourceProvider")
    @Autowired
    ResourceProvider resourceProvider;
    @Override
    public List<GroupDTO> queryGroupsByUsername(String username) {
        List<GroupDTO> groups = resourceProvider.groups(username).getData();
        log.info("username:{},groups:{}", username, groups);
        return groups;
    }
}
