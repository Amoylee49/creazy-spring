package org.gateway.admin.Service.impl;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gateway.admin.Service.IGatewayRouteService;
import org.gateway.admin.dao.GatewayRouteMapper;
import org.gateway.admin.entity.ov.GatewayRouteVo;
import org.gateway.admin.entity.param.GatewayRouteQueryParam;
import org.gateway.admin.entity.po.GatewayRoute;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GatewayRouteService extends ServiceImpl<GatewayRouteMapper, GatewayRoute> implements IGatewayRouteService {

    public static final String GATEWAY_ROUTES = "gateway_routes::";

    //@CreateCache弃用   https://github.com/alibaba/jetcache/blob/master/docs/CN/CreateCache.md
    @CreateCache(name = GATEWAY_ROUTES, cacheType = CacheType.REMOTE)
    private Cache<String, RouteDefinition> gatewayRouteCache;

//    @Autowired
//    private EventSender eventSender;

    @Override
    public GatewayRoute get(String id) {
        return this.getById(id);
    }

    @Override
    public boolean add(GatewayRoute gatewayRoute) {
        boolean isSuccess = this.save(gatewayRoute);
        //转化为gateway需要的类型，缓存到redis，并事件通知到各网关应用
        RouteDefinition routeDefinition = gatewayRouteToRouteDefinition(gatewayRoute);
        gatewayRouteCache.put(gatewayRoute.getRouteId(), routeDefinition);
//        eventSender.send(BusConfig.ROUTING_KEY, routeDefinition);
        return isSuccess;
    }

    //将数据库中json对象转成网关需要的RouteDefinition对象
    private RouteDefinition gatewayRouteToRouteDefinition(GatewayRoute gatewayRoute) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRoute.getId());
        routeDefinition.setOrder(gatewayRoute.getOrders());
        routeDefinition.setUri(URI.create(gatewayRoute.getUri()));
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            routeDefinition.setFilters(objectMapper.readValue(gatewayRoute.getFilters(), new TypeReference<List<FilterDefinition>>() {
            }));
            routeDefinition.setPredicates(objectMapper.readValue(gatewayRoute.getPredicates(), new TypeReference<List<PredicateDefinition>>() {
            }));
        } catch (IOException e) {
            log.error("网关路由对象转换失败", e);
        }
//
//        try {
//            //设置filters
//            String filters = gatewayRoute.getFilters();
//            List<FilterDefinition> parseObj = fastJsonParseObj(filters, new TypeReference<List<FilterDefinition>>() {
//            });
//            routeDefinition.setFilters(parseObj);
//            //设置predicate
//            routeDefinition.setPredicates(fastJsonParseObj(gatewayRoute.getPredicates(), new TypeReference<List<PredicateDefinition>>() {
//            }));
//        } catch (Exception e) {
//            log.error("序列化网关路由对象失败");
//        }
        return routeDefinition;
    }

    @Override
    public boolean delete(String id) {
        GatewayRoute gatewayRoute = this.getById(id);
        // 删除redis缓存, 并事件通知各网关应用
        gatewayRouteCache.remove(gatewayRoute.getRouteId());
//        eventSender.send(BusConfig.ROUTING_KEY, gatewayRouteToRouteDefinition(gatewayRoute));
        return this.removeById(id);
    }

    @PostConstruct
    @Override
    public boolean overload() {
        List<GatewayRoute> gatewayRoutes = this.list(new QueryWrapper<>());
        gatewayRoutes.forEach(gatewayRoute -> {
            gatewayRouteCache.put(gatewayRoute.getRouteId(), gatewayRouteToRouteDefinition(gatewayRoute));
        });
        log.info("全局初始化网关路由成功");
        return true;
    }

    @Override
    public List<GatewayRouteVo> query(GatewayRouteQueryParam gatewayRouteQueryParam) {
        QueryWrapper<GatewayRoute> queryWrapper = gatewayRouteQueryParam.build();
        queryWrapper.eq(StringUtils.isNotBlank(gatewayRouteQueryParam.getUri()), "uri", gatewayRouteQueryParam.getUri());
        return this.list(queryWrapper).stream().map(GatewayRouteVo::new).collect(Collectors.toList());
    }

    @Override
    public boolean update(GatewayRoute gatewayRoute) {
        boolean isSuccess = this.updateById(gatewayRoute);
        // 转化为gateway需要的类型，缓存到redis, 并事件通知各网关应用
        RouteDefinition routeDefinition = gatewayRouteToRouteDefinition(gatewayRoute);
        gatewayRouteCache.put(gatewayRoute.getRouteId(), routeDefinition);
//        eventSender.send(BusConfig.ROUTING_KEY, routeDefinition);
        return isSuccess;
    }

//    public <T> T fastJsonParseObj(String obj, TypeReference<T> typeReference) {
////        TypeReference<List<FilterDefinition>> listRefer = new TypeReference<>() {
////        };
//        T parseObject = JSONObject.parseObject(obj, typeReference);
//
//        return parseObject;
//    }

}
