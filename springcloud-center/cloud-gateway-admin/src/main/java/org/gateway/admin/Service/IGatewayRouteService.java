package org.gateway.admin.Service;

import org.gateway.admin.entity.ov.GatewayRouteVo;
import org.gateway.admin.entity.param.GatewayRouteQueryParam;
import org.gateway.admin.entity.po.GatewayRoute;

import java.util.List;

public interface IGatewayRouteService {

    GatewayRoute get(String id);
    boolean add(GatewayRoute gatewayRoute);
    boolean delete(String id);
    boolean update(GatewayRoute gatewayRoute);
    List<GatewayRouteVo> query(GatewayRouteQueryParam gatewayRouteQueryParam);
    public boolean overload();
}
