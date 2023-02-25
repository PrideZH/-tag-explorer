package cn.pridezh.tagexplore.core.service;

import cn.pridezh.tagexplore.core.domain.common.PageDTO;
import cn.pridezh.tagexplore.core.domain.dto.ResourceUpdateDTO;
import cn.pridezh.tagexplore.core.domain.po.Cover;
import cn.pridezh.tagexplore.core.domain.po.Resource;
import cn.pridezh.tagexplore.core.domain.po.ResourceTag;
import cn.pridezh.tagexplore.core.domain.po.Tag;
import cn.pridezh.tagexplore.core.domain.vo.ResourceItemVO;
import cn.pridezh.tagexplore.core.domain.vo.ResourceVO;
import cn.pridezh.tagexplore.core.domain.vo.TagVO;
import cn.pridezh.tagexplore.core.mapper.ResourceMapper;
import cn.pridezh.tagexplore.core.mapper.ResourceTagMapper;
import cn.pridezh.tagexplore.core.mapper.TagMapper;
import cn.pridezh.tagexplore.core.util.AESUtils;
import cn.pridezh.tagexplore.core.util.XORUtils;
import cn.pridezh.tagexplore.core.config.properties.AppProperties;
import cn.pridezh.tagexplore.core.exception.ServiceException;
import cn.pridezh.tagexplore.core.mapper.CoverMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author PrideZH
 */
@RequiredArgsConstructor
@Service
public class ResourceService extends ServiceImpl<ResourceMapper, Resource> {

    private final CoverService coverService;

    private final ResourceMapper resourceMapper;
    private final CoverMapper coverMapper;
    private final TagMapper tagMapper;
    private final ResourceTagMapper resourceTagMapper;

//    private final ResourceConvert resourceConvert;

    private final AppProperties appProperties;

    public List<ResourceItemVO> post(MultipartFile[] files) throws Exception {
        if (files == null || files.length == 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST);
        }
        List<ResourceItemVO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(post0(file));
        }
        return result;
    }

    private ResourceItemVO post0(MultipartFile file) throws Exception {
        String password = appProperties.getAuth().getPassword();

        if (file.getOriginalFilename() == null) {
            throw new ServiceException(1001, "文件名为空");
        }

        File f = createDistinctFile(file.getOriginalFilename());
        OutputStream os = new FileOutputStream(f);
        byte[] buf = new byte[1024];
        InputStream is = file.getInputStream();
        while (-1 != is.read(buf)) {
            byte[] res = XORUtils.execute(buf, password);
            os.write(res, 0, res.length);
        }
        os.flush();
        is.close();
        os.close();

        Resource resource = new Resource();
        resource.setName(f.getName());
        resource.setType(file.getContentType());
        resourceMapper.insert(resource);

        coverService.post(file, resource.getId());

        ResourceItemVO resourceVO = new ResourceItemVO();

        resourceVO.setId(resource.getId());
        resourceVO.setType(resource.getType());
        resourceVO.setCoverCount(coverMapper.selectCount(new LambdaQueryWrapper<Cover>()
                .eq(Cover::getResourceId, resource.getId())));

        String name = resource.getName().substring(0, resource.getName().lastIndexOf('.'));
        if (StringUtils.isNotBlank(password)) {
            resourceVO.setName(AESUtils.decrypt(name, password));
        } else {
            resourceVO.setName(name);
        }

        return resourceVO;
    }

    public void uploadCover(MultipartFile file, Long id) throws IOException {
        Resource resource = Optional
                .ofNullable(resourceMapper.selectById(id))
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND));

        String password = appProperties.getAuth().getPassword();

        if (StringUtils.isNotBlank(password)) {
            resource.setCover(XORUtils.execute(file.getBytes(), password, false));
        } else {
            resource.setCover(file.getBytes());
        }
        resourceMapper.updateById(resource);
    }

    private String getExtension(String type) {
        return switch (type) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/bmp" -> ".bmp";
            case "image/gif" -> ".gif";
            case "video/mp4" -> ".mp4";
            default -> "";
        };
    }

    public IPage<ResourceItemVO> page(List<Long> tags, PageDTO pageDTO) {
        String password = appProperties.getAuth().getPassword();

        IPage<Resource> search = resourceMapper.search(new Page<>(pageDTO.getPage(), pageDTO.getSize()), tags);

        return search.convert(resource -> {
            ResourceItemVO resourceVO = new ResourceItemVO();

            resourceVO.setId(resource.getId());

            resourceVO.setType(resource.getType());
            resourceVO.setCover(resource.getCover() != null && resource.getCover().length != 8);
            resourceVO.setCoverCount(coverMapper.selectCount(new LambdaQueryWrapper<Cover>()
                    .eq(Cover::getResourceId, resource.getId())));

            if (StringUtils.isNotBlank(password)) {
                try {
                    String name = resource.getName().substring(0, resource.getName().lastIndexOf('.'));
                    resourceVO.setName(AESUtils.decrypt(name, password));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                resourceVO.setName(resource.getName());
            }

            return resourceVO;
        });
    }

    public ResourceVO get(Long id) {
        Resource resource = Optional
                .ofNullable(resourceMapper.selectById(id))
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND));

        String password = appProperties.getAuth().getPassword();

        ResourceVO resourceVO = new ResourceVO();

        resourceVO.setId(resource.getId());
        resourceVO.setType(resource.getType());
        resourceVO.setCover(resource.getCover() != null && resource.getCover().length != 8);

        resourceVO.setCoverCount(coverMapper.selectCount(new LambdaQueryWrapper<Cover>()
                .eq(Cover::getResourceId, resource.getId())));

        if (StringUtils.isNotBlank(password)) {
            try {
                String name = resource.getName().substring(0, resource.getName().lastIndexOf('.'));
                resourceVO.setName(AESUtils.decrypt(name, password));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resourceVO.setName(resource.getName());
        }

        List<Long> tagIds = resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                        .eq(ResourceTag::getResourceId, id)).stream()
                .map(ResourceTag::getTagId)
                .toList();
        resourceVO.setTags(tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                        .in(Tag::getId, tagIds)).stream()
                .map(tag -> {
                    TagVO tagVO = new TagVO();
                    tagVO.setId(tag.getId());
                    tagVO.setName(tag.getName());
                    return tagVO;
                }).toList());

        return resourceVO;
    }

    public void put(ResourceUpdateDTO resourceUpdateDTO) throws Exception {
        Resource resource = Optional
                .ofNullable(resourceMapper.selectById(resourceUpdateDTO.getId()))
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND));

        Resource newResource = new Resource();

        if (StringUtils.isNotBlank(resourceUpdateDTO.getName())) {
            File newFile = createDistinctFile(resourceUpdateDTO.getName());
            newResource.setName(newFile.getName());

            File file = new File(appProperties.getRepository() + "/" + resource.getName());
            if(!file.renameTo(newFile)) {
                throw new ServiceException(1001, "文件名称修改失败");
            }
        }

        newResource.setId(resource.getId());
        resourceMapper.updateById(newResource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Resource resource = Optional
                .ofNullable(resourceMapper.selectById(id))
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND));

        File file = new File(appProperties.getRepository() + "/" + resource.getName());
        if (!file.delete()) {
            throw new ServiceException(1001, "Failed to delete the file");
        }

        resourceMapper.deleteById(id);
        coverMapper.delete(new LambdaQueryWrapper<Cover>().eq(Cover::getResourceId, id));
    }

    /**
     * 防重名 重名时文件名添加` (id)` id从0开始计数
     */
    private File createDistinctFile(String fileName) throws Exception {
        String password = appProperties.getAuth().getPassword();

        File file;

        String name, suffix;
        if (fileName.lastIndexOf(".") != -1) {
            name = fileName.substring(0, fileName.lastIndexOf("."));
            suffix = fileName.substring(fileName.lastIndexOf("."));
        } else {
            name = fileName;
            suffix = "";
        }

        int id = 0;
        do {
            if (id++ != 0) {
                name += " (" + id + ")";
            }
            String result;
            if (StringUtils.isNotBlank(password)) {
                result = AESUtils.encrypt(name, appProperties.getAuth().getPassword());
                result += ".res";
            } else {
                result = name;
                result += suffix;
            }
            file = new File(appProperties.getRepository() + "/" + result);
        } while (file.exists());
        return file;
    }

    public void delCover(Long id) {
        if (resourceMapper.exists(new LambdaUpdateWrapper<Resource>()
                .eq(Resource::getId, id))) {
            throw new ServiceException(HttpStatus.NOT_FOUND);
        }

        resourceMapper.update(null, new LambdaUpdateWrapper<Resource>()
                .eq(Resource::getId, id)
                .set(Resource::getCover, null));
    }

}
