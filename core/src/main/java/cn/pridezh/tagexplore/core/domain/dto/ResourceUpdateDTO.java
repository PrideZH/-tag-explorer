package cn.pridezh.tagexplore.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author PrideZH
 */
@Data
public class ResourceUpdateDTO {

    @JsonIgnore
    private Long id;

    private String name;

    private List<String> tags;

}
