package org.crazy.spring.auth.service;

import com.springcloud.common.web.entity.dto.PermissionDTO;

import javax.servlet.http.HttpServletRequest;

public interface IAuthenticationService {

    /**
     * 校验权限
     * @param authRequest
     * @return 是否有权限
     */
    boolean decide(HttpServletRequest authRequest);
    /**
     * 校验数据权限 数据决定
     * @param permissionDTO 许可dto
     * @return 是否有权限
     */
    boolean dataDecide(PermissionDTO permissionDTO);
}
