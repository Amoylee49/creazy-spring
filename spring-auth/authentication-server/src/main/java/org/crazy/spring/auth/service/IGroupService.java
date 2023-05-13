package org.crazy.spring.auth.service;


import com.springcloud.common.web.entity.dto.GroupDTO;

import java.util.List;

public interface IGroupService {

    /**
     * 根据用户名查找组列表
     *
     * @param username 用户名
     * @return {@link List<GroupDTO>}
     */
    List<GroupDTO> queryGroupsByUsername(String username);
}
