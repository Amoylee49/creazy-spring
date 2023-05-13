package org.gateway.web.service;

public interface IPermissionService {


    boolean permission(String authentication, String url, String method);
}
