package com.project.elice2.orders.pay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kakaopay")
public class KakaoPayProperties {
    private String secretKey;
    private String cid;

}
