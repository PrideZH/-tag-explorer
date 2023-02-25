package cn.pridezh.tagexplore.core.domain.vo;

import cn.pridezh.tagexplore.core.domain.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author PrideZH
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceVO extends BaseVO {

    private String name;

    private String type;

    private List<TagVO> tags;

    private Long coverCount;

    private Boolean cover;

}
