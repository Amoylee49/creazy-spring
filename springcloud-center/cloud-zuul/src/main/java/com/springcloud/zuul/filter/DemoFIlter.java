package com.springcloud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/**
 * 演示过滤器： 黑名单过滤
 */
@Slf4j
@Component
public class DemoFIlter extends ZuulFilter {

    /**
     * 示例所使用的黑名单：实际使用场景，需要从数据库或者其他来源获取
     */
    private static List<String> blackList = Arrays.asList("foo", "bar", "black");

    @Override
    public String filterType() {
//      pre：路由之前
//		routing：路由之时
//		post： 路由之后
//		error：发送错误调用

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 这里可以写判断逻辑，是否要执行过滤，true为跳过
     */
    @Override
    public boolean shouldFilter() {
        RequestContext cxt = RequestContext.getCurrentContext();
        /***如果请求已经被其他的过滤器终止，则本过滤器也不做处理*/
        if (!cxt.sendZuulResponse()) {
            return false;
        }
        HttpServletRequest request = cxt.getRequest();
        if (request.getRequestURI().startsWith("/blob/demo")) {
            return true;
        }

        /**
         *返回false  表示需要跳过run方法
         */
        return false;
    }

    /**
     * 过滤器的具体逻辑
     * 通过请求判断用户名称
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        /**
         * 对用户名称进行判断：
         * 如果用户名称在黑名单中，则不再转发给后端的服务提供者
         */
        String username = request.getParameter("username");
        if (null != username && blackList.contains(username)) {
            log.info("用户名称{}在黑名单里，链接是：{}", username, request.getRequestURL());
            context.setSendZuulResponse(false);
            try {
                context.getResponse().setContentType("text/html;charset=utf-8");
                context.getResponse().getWriter().write("对不起，您已经进入黑名单");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
