package cn.pridezh.tagexplore.core.domain.po;

import cn.pridezh.tagexplore.core.domain.common.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author PrideZH
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName
public class Resource extends BasePO {

    private String name;

    private String type;

}
