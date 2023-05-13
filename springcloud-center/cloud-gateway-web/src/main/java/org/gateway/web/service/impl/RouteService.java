package org.gateway.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gateway.web.service.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteService implements IRouteService {

    private static final String GATEWAY_ROUTES = "gateway_routes::";

    @Value("${test.val:0000000}")
    private String testNum;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @CreateCache(name = GATEWAY_ROUTES, cacheType = CacheType.REMOTE)
    private Cache<String, RouteDefinition> gatewayRouteCache;
    private Map<String, RouteDefinition> routeDefinitionMap = new HashMap<>();

    @PostConstruct
    private void loadRouteDefinition(){
        log.info("loadRouteDefinition,开始初始化路由");
        Set<String> keys = redisTemplate.keys(GATEWAY_ROUTES + "*");
        if (CollectionUtil.isEmpty(keys)){
            log.info("testNum-----------{}",testNum);
            log.info("remote gatewayRouteCache size()-----------{}",gatewayRouteCache);
            log.info("redisTemplate 为空，return");
            return;
        }
        //去掉key前缀
        Set<String> keySet = keys.stream().map(key -> key.replace(GATEWAY_ROUTES, StringUtils.EMPTY))
                .collect(Collectors.toSet());
        Map<String, RouteDefinition> allRoutes = gatewayRouteCache.getAll(keySet);
        for (String ke: keySet){
            RouteDefinition routeDefinition = gatewayRouteCache.get(ke);
            log.info("{}",routeDefinition);
        }
        log.info("gatewayRoutes:{}-------" ,allRoutes.values());

        // 以下代码原因是，jetcache将RouteDefinition返序列化后，uri发生变化，未初使化，导致路由异常，以下代码是重新初使化uri
        allRoutes.values().forEach(routeDefinition -> {
            try {
                routeDefinition.setUri(new URI(routeDefinition.getUri().toASCIIString()));
            } catch (URISyntaxException e) {
                log.error("网关加载RouteDefinition异常：", e);
            }
        });
        routeDefinitionMap.putAll(allRoutes);
        log.info("共初使化路由信息：{}", routeDefinitionMap.size());

    }
    @Override
    public Collection<RouteDefinition> getRouteDefinitions() {
        return routeDefinitionMap.values();
    }

    @Override
    public boolean save(RouteDefinition routeDefinition) {
        routeDefinitionMap.put(routeDefinition.getId(),routeDefinition);
        log.info("新增路由1条：{},目前路由共{}条", routeDefinition, routeDefinitionMap.size());
        return true;
    }

    @Override
    public boolean delete(String routeId) {
        routeDefinitionMap.remove(routeId);
        log.info("删除路由1条：{},目前路由共{}条", routeId, routeDefinitionMap.size());
        return true;
    }
}
