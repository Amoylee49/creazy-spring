package org.auth.client.provider;

import com.springcloud.common.core.entity.vo.Result;
import com.springcloud.common.web.entity.dto.PermissionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "authentication-server",
        fallback = AuthProvider.AuthProviderFallback.class)
public interface AuthProvider {
    /**
     * 调用鉴权服务，判断用户是否有权限
     */
    @PostMapping(value = "/auth/permission")
    Result auth(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication, @RequestParam(value = "url") String url, @RequestParam(value = "method") String method);

    @PostMapping(value = "/auth/data/permission")
    Result dataAuth(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication, @RequestBody PermissionDTO permissionDTO);

    @Component
    class AuthProviderFallback implements AuthProvider {
        //降级统一返回无权限

        /**
         * {
         * code:"-1"
         * mesg:"系统异常"
         * }
         *
         * @param authentication
         * @param url
         * @param method
         * @return
         */
        @Override
        public Result auth(String authentication, String url, String method) {
            return Result.fail();
        }

        @Override
        public Result dataAuth(String authentication, PermissionDTO permissionDTO) {
            return Result.fail();
        }


    }
}
