package cn.pridezh.tagexplore.core.mapper;

import cn.pridezh.tagexplore.core.domain.po.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author PrideZH
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    IPage<Resource> search(IPage<?> page, @Param("tags") List<Long> tags);

}
