package cn.pridezh.tagexplore.core.domain.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author PrideZH
 */
@Data
public class BasePO implements Serializable {

    /** 主键 (雪花算法) */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

}
