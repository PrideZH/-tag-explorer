package cn.pridezh.tagexplore.core.controller;

import cn.pridezh.tagexplore.core.domain.common.Result;
import cn.pridezh.tagexplore.core.domain.dto.TagCreateDTO;
import cn.pridezh.tagexplore.core.domain.vo.TagVO;
import cn.pridezh.tagexplore.core.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author PrideZH
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @PostMapping("")
    public Result<Void> post(@Validated @RequestBody TagCreateDTO tagCreateDTO) {
        tagService.add(tagCreateDTO);
        return Result.success(null);
    }

    @GetMapping("")
    public Result<List<TagVO>> list(@RequestParam(required = false) List<String> tags) {
        return Result.success(tagService.list(tags));
    }

    @DeleteMapping("/{resourceId:\\d+}/{tagId:\\d+}")
    public Result<Void> delete(@PathVariable("resourceId") String resourceId, @PathVariable("tagId") String tagId) {
        tagService.delete(Long.valueOf(resourceId), Long.valueOf(tagId));
        return Result.success(null);
    }

}