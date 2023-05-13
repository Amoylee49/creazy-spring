package org.crazy.spring.auth.provider;


import com.springcloud.common.core.entity.vo.Result;
import com.springcloud.common.web.entity.dto.GroupDTO;
import com.springcloud.common.web.entity.dto.PermissionDTO;
import org.crazy.spring.sysadmin.entity.po.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

//必须开启 FeignClient，而且开启 hystrix 熔断限流，fallback 方法才能被调用。
@FeignClient(name = "authentication", fallback = ResourceProviderFallBack.class)
public interface ResourceProvider {

    @GetMapping("/resource/all")
    Result<Set<Resource>> resources();

    @GetMapping("/resource/user/{username}")
    Result<Set<Resource>> resources(@PathVariable("username") String username);

    @GetMapping("/permission/group")
    Result<List<PermissionDTO>> permissions(@RequestBody PermissionDTO permissionDTO);

    @GetMapping("/group/user/{username}")
    Result<List<GroupDTO>> groups(@PathVariable("username") String username);


}
