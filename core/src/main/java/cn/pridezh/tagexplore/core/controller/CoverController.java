package cn.pridezh.tagexplore.core.controller;

import cn.pridezh.tagexplore.core.service.CoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author PrideZH
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/cover")
public class CoverController {

    private final CoverService coverService;

    @GetMapping(value = "/{id:\\d+}",
            produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getCover(@PathVariable("id") String id, @RequestParam(required = false) Integer index) {
        return coverService.getCover(Long.valueOf(id), index);
    }

}
