package org.crazy.spring.auth.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.springcloud.common.core.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.crazy.spring.auth.provider.ResourceProvider;
import org.crazy.spring.auth.service.IResourceService;
import org.crazy.spring.auth.service.NewMvcRequestMatcher;
import org.crazy.spring.sysadmin.entity.po.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourceService implements IResourceService {

    @Autowired
    private HandlerMappingIntrospector mvcMappingIntrospect;
    @Autowired
    private ResourceProvider resourceProvider;

    //系统中所有权限集合
    private static final Map<RequestMatcher,ConfigAttribute> resourceConfigAttr = new HashMap<>();
    @Override
    public synchronized void saveResource(Resource resource) {

        resourceConfigAttr.put(
                this.newMvcRequestMatcher(resource.getUrl(),resource.getMethod()),
                new SecurityConfig(resource.getCode())
        );

        log.info("resourceConfigAttributes size:{}", resourceConfigAttr.size());
    }
    /**
     * 创建RequestMatcher
     *
     * @param url
     * @param method
     * @return
     */
    private MvcRequestMatcher newMvcRequestMatcher(String url, String method) {
        return new NewMvcRequestMatcher(mvcMappingIntrospect, url, method);
    }

    @Override
    public synchronized void removeResource(Resource resource) {
        resourceConfigAttr.remove(this.newMvcRequestMatcher(resource.getUrl(), resource.getMethod()));
        log.info("resourceConfigAttributes size:{}", resourceConfigAttr.size());
    }

    @Override
    public synchronized void loadResource() {
        Result<Set<Resource>> resourcesResult = resourceProvider.resources();
        if (resourcesResult.isFail())
            System.exit(1);
        Set<Resource> resultData = resourcesResult.getData();
        Map<MvcRequestMatcher, SecurityConfig> tempResourceAttr = resultData.stream().collect(Collectors.toMap(
                resource -> this.newMvcRequestMatcher(resource.getUrl(), resource.getMethod()),
                resource -> new SecurityConfig(resource.getCode())
        ));
        resourceConfigAttr.putAll(tempResourceAttr);
        log.debug("init resourcesConfigAttr:{}", resourceConfigAttr);
    }

    @Override
    public ConfigAttribute findConfigAttributesByUrl(HttpServletRequest authRequest) {
        return resourceConfigAttr.keySet().stream()
                .filter(matcher-> matcher.matches(authRequest))
                .map(matcher -> resourceConfigAttr.get(matcher))
                .peek(urlConfig-> log.debug("url在资源池中的配置{}",urlConfig.getAttribute()))
                .findFirst()
                .orElse(new SecurityConfig("NONEXISTENT_URL"))
                ;
    }

    @Override
    @Cached(name = "resource4user::", key = "#username",cacheType = CacheType.BOTH,expire = 5)
    public Set<Resource> queryByUsername(String username) {
        return resourceProvider.resources(username).getData();
    }










}
