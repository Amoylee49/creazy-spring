package org.crazy.spring.auth.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.springcloud.common.web.entity.dto.PermissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.crazy.spring.auth.provider.ResourceProvider;
import org.crazy.spring.auth.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PermissionService implements IPermissionService {

    @Autowired
    @Qualifier(value = "org.crazy.spring.auth.provider.ResourceProvider")
    ResourceProvider resourceProvider;

    @Override
    @Cached(name = "permission4Group::", key = "#permissionDTO.groupCode", cacheType = CacheType.BOTH, expire = 5)
    //在一个JVM中，同一个键只有一个线程加载，其他线程等待结果。
//    @CachePenetrationProtect
    public List<PermissionDTO> queryPermissionsByGroupCode(PermissionDTO permissionDTO) {
        return resourceProvider.permissions(permissionDTO).getData();
    }
}
