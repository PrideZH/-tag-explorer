package cn.pridezh.tagexplore.core.service;

import cn.pridezh.tagexplore.core.domain.dto.TagCreateDTO;
import cn.pridezh.tagexplore.core.domain.po.ResourceTag;
import cn.pridezh.tagexplore.core.domain.po.Tag;
import cn.pridezh.tagexplore.core.domain.vo.TagVO;
import cn.pridezh.tagexplore.core.mapper.ResourceTagMapper;
import cn.pridezh.tagexplore.core.mapper.TagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author PrideZH
 */
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagMapper tagMapper;
    private final ResourceTagMapper resourceTagMapper;

    @Transactional(rollbackFor = Exception.class)
    public void add(TagCreateDTO tagCreateDTO) {
        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, tagCreateDTO.getName()));

        if (tag == null) {
            tag = new Tag();
            tag.setName(tagCreateDTO.getName());
            tagMapper.insert(tag);
        }

        ResourceTag resourceTag = new ResourceTag();
        resourceTag.setResourceId(tagCreateDTO.getResourceId());
        resourceTag.setTagId(tag.getId());
        resourceTagMapper.insert(resourceTag);
    }

    public List<TagVO> list(List<String> tagIds) {
        List<Tag> result;

        if (tagIds == null || tagIds.size() == 0) {
            result = tagMapper.selectList(null);
        } else {
            // 获取可查询到的所有资源的ID
            List<Long> resourceIds = resourceTagMapper.selectList(new QueryWrapper<ResourceTag>()
                            .select("DISTINCT resource_id")
                            .lambda()
                            .in(ResourceTag::getTagId, tagIds))
                    .stream().map(ResourceTag::getResourceId).toList();
            // 获取这些资源的所有标签
            List<Long> resTagIds = resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                            .select(ResourceTag::getTagId)
                            .in(ResourceTag::getResourceId, resourceIds))
                    .stream().map(ResourceTag::getTagId).toList();
            result = tagMapper.selectList(new LambdaQueryWrapper<Tag>().in(Tag::getId, resTagIds));
        }

        return result.stream().map(tag -> {
            TagVO tagVO = new TagVO();

            tagVO.setId(tag.getId());
            tagVO.setName(tag.getName());
            tagVO.setCount(resourceTagMapper.selectCount(new LambdaQueryWrapper<ResourceTag>()
                    .eq(ResourceTag::getTagId, tag.getId())));

            return tagVO;
        }).toList();
    }

    public void delete(Long resourceId, Long tagId) {
        resourceTagMapper.delete(new LambdaQueryWrapper<ResourceTag>()
                .eq(ResourceTag::getResourceId, resourceId)
                .eq(ResourceTag::getTagId, tagId));
    }

}