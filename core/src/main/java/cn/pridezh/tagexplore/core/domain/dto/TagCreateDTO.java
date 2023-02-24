package cn.pridezh.tagexplore.core.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author PrideZH
 */
@Data
public class TagCreateDTO {

    private Long resourceId;

    @NotBlank(message = "名称[name]不能为空")
    private String name;

}
