package com.springcloud.common.web.entity.po.entity.vo;


import com.springcloud.common.web.entity.po.entity.po.BasePo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseVo<T extends BasePo> implements Serializable {
    private String id;
}
