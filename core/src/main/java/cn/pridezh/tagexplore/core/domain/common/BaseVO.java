package cn.pridezh.tagexplore.core.domain.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author PrideZH
 */
@Data
public class BaseVO implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected Long id;

}
