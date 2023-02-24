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
@TableName("resource__tag")
public class ResourceTag extends BasePO {

    private Long resourceId;

    private Long tagId;

}