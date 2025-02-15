package com.project.elice2.comment.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestDto {

//    @NotBlank(message = "문의를 남겨주세요.")
//    @Size(min = 1,max = 500,message = "문의는 500자까지 남길 수 있습니다.")
    private String detail;


    private boolean isSecret;


    private Long productId;

    public String getDetail() {
        return detail;
    }

    public boolean getIsSecret() {
        return isSecret;
    }

    public Long getProductId() {
        return productId;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setIsSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
