package org.auth.client.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSON;
import com.springcloud.common.core.entity.vo.Result;
import com.springcloud.common.web.entity.dto.PermissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.auth.client.provider.AuthProvider;
import org.auth.client.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthService implements IAuthService {

    //Authorization 认证开头是 bearer
    private static final String BEAREA = "Bearer";

    @Autowired
    private AuthProvider authProvider;

    @Value("${spring.security.oauth2.jwt.signingKey}")
    private String signKey = "123456";

    //不需要网关鉴权的url配置(/oauth /open)
    @Value("${gate.ignore.authentication.startWith:/oauth}")
    private String ignoreUrl;

    @Override
    public Result dataAuthenticate(String authentication, PermissionDTO dto) {
        return authProvider.dataAuth(authentication, dto);
    }

    @Override
    public Result authenticate(String authentication, String url, String method) {
        return authProvider.auth(authentication, url, method);
    }

    @Override
    public boolean ignoreAuthentication(String url) {
        boolean match = Stream.of(this.ignoreUrl.split(",")).anyMatch(
                ignoreUrl -> url.startsWith(ignoreUrl.trim())
        );
        return match;
    }

    @Override
    public boolean hasPermission(Result authResult) {
        log.info("鉴权结果：{}", authResult.getData());
        return authResult.isSuccess() && (Boolean) authResult.getData();
    }

    @Override
    public boolean hasPermission(String authentication, String url, String method) {
        //如果未带token信息，直接权限
        if (StringUtils.isBlank(authentication) || !authentication.startsWith(BEAREA))
            return Boolean.FALSE;
        //token有效，在网关进行校验 无效或过期
        if (invalidJwtAccessToken(authentication))
            return Boolean.FALSE;
        //从认证服务获取是否有权限，远程调用
        return hasPermission(authenticate(authentication, url, method));
    }

    @Override
    public boolean invalidJwtAccessToken(String jwtToken) {
        return JWTUtil.verify(jwtToken, signKey.getBytes(StandardCharsets.UTF_8));
    }

    //https://blog.csdn.net/weixin_51876109/article/details/127667045
    @Override
    public JWT JwtParseToken(String jwtToken) {
        if (jwtToken.startsWith(BEAREA)) {
            jwtToken = StringUtils.substring(jwtToken, BEAREA.length());
        }

        return JWTUtil.parseToken(jwtToken);
    }

    public static void main(String[] args) {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiJ0b20iLCJleHBpcmVfdGltZSI6MTY0MTk1NjgxNjYyMX0.RqgWLmgxquZB6OFtnGbb1bjdbQcyTKEenaoGq1f3VEw";

        final JWT jwt = JWTUtil.parseToken(token);

//        System.out.println(jwt.getHeader(JWTHeader.TYPE));
//        System.out.println(jwt.getPayload("uid"));
//        System.out.println(new Date((long) jwt.getPayload("expire_time")).toLocaleString());


        boolean flag = JWTUtil.verify(token, "123456".getBytes());
        System.out.println(flag);
        System.out.println("=============================================");
        HashMap<String,Object> payload = new HashMap<>();
        String now = DateUtil.now();

        //签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        //生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        //过期时间 最大有效期7天强制过期
        payload.put(JWTPayload.EXPIRES_AT, DateUtil.offsetDay(DateUtil.date(),7));

        payload.put("UserId",234);
        payload.put("UserId4","234");

        String token1 = JWTUtil.createToken(payload, "signKey".getBytes());
        log.info("token,{}",token1);
        JWT jwt1 = JWTUtil.parseToken(token1);

        Object jsonToken = JSON.toJSON(jwt1.getPayload());
        log.info("解析jwt payLoad{}",jsonToken);


        System.out.println("===================================");
        String sign = JWT.create()
                .addHeaders(null)
                .addPayloads(payload)
                .setKey("signKey".getBytes())
                .sign();

        boolean verify = JWTUtil.verify(sign, "123456".getBytes());
        log.info("{} \n -{}", sign, verify);
    }

}
