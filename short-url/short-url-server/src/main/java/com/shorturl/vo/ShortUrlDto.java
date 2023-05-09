package com.shorturl.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlDto {
    //要生成的短链接地址

    @NonNull
    private String url;
}
