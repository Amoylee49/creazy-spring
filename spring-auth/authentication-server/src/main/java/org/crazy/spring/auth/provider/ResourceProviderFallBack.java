package org.crazy.spring.auth.provider;

import com.springcloud.common.core.entity.vo.Result;
import com.springcloud.common.web.entity.dto.GroupDTO;
import com.springcloud.common.web.entity.dto.PermissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.crazy.spring.sysadmin.entity.po.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ResourceProviderFallBack implements ResourceProvider{


    @Override
    public Result<Set<Resource>> resources() {
        log.error("认证服务启动时加载资源异常！未加载到资源");
        return Result.fail();
    }

    @Override
    public Result<Set<Resource>> resources(String username) {
        log.error("认证服务查询用户异常！查询用户资源为空！");
        return Result.success(new HashSet<Resource>());
    }

    @Override
    public Result<List<PermissionDTO>> permissions(PermissionDTO permissionDTO) {
        log.error("认证服务查询用户组权限异常！查询用户组权限为空！");
        return Result.success(new HashSet<PermissionDTO>());
    }

    @Override
    public Result<List<GroupDTO>> groups(String username) {
        log.error("认证服务查询用户组异常！查询用户组列表为空！");
        return Result.success(new ArrayList<GroupDTO>());
    }
}
