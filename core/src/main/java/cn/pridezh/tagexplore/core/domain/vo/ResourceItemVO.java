package cn.pridezh.tagexplore.core.domain.vo;

import cn.pridezh.tagexplore.core.domain.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PrideZH
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceItemVO extends BaseVO {

    private String name;

    private String type;

    /**
     * 封面数量
     */
    private Long coverCount;

}
