package cn.pridezh.tagexplore.core.domain.po;

import cn.pridezh.tagexplore.core.domain.common.BasePO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.ByteArrayTypeHandler;

/**
 * @author PrideZH
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(autoResultMap = true)
public class Resource extends BasePO {

    private String name;

    private String remark;

    private String type;

    @TableField(typeHandler = ByteArrayTypeHandler.class)
    private byte[] cover;

}
