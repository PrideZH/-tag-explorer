package cn.pridezh.tagexplore.core.service;

import cn.pridezh.tagexplore.core.domain.po.Cover;
import cn.pridezh.tagexplore.core.util.XORUtils;
import cn.pridezh.tagexplore.core.config.properties.AppProperties;
import cn.pridezh.tagexplore.core.mapper.CoverMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author PrideZH
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CoverService {

    private final CoverMapper coverMapper;

    private final AppProperties appProperties;

    private boolean insertFrame(Frame frame, Long resourceId) throws IOException {
        // 无效帧
        if (null == frame || null == frame.image) {
            log.info("无效帧: {}", frame);
            return false;
        }

        String password = appProperties.getAuth().getPassword();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        Cover cover = new Cover();
        cover.setResourceId(resourceId);
        if (StringUtils.isNotBlank(password)) {
            cover.setData(XORUtils.execute(byteArrayOutputStream.toByteArray(), password, false));
        } else {
            cover.setData(byteArrayOutputStream.toByteArray());
        }
        coverMapper.insert(cover);

        byteArrayOutputStream.close();
        return true;
    }

    public void post(MultipartFile file, Long resourceId) throws IOException {
        String password = appProperties.getAuth().getPassword();

        if (MediaType.IMAGE_JPEG_VALUE.equals(file.getContentType())
                || MediaType.IMAGE_PNG_VALUE.equals(file.getContentType())
                || MediaType.IMAGE_GIF_VALUE.equals(file.getContentType())) {
            InputStream inputStream = file.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            long len = inputStream.read(bytes, 0, inputStream.available());

            Cover cover = new Cover();
            cover.setResourceId(resourceId);
            if (StringUtils.isNotBlank(password)) {
                cover.setData(XORUtils.execute(bytes, password, false));
            } else {
                cover.setData(bytes);
            }
            coverMapper.insert(cover);
        } else if ("video/mp4".equals(file.getContentType())) {
            InputStream inputStream = file.getInputStream();
            // 设置maximumSize为0，禁用 seek 回调，从而减少启动时间
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream, 0);

            grabber.start();

            // 截取等分8张图片
            int COUNT = 8;
            long ftp = grabber.getLengthInFrames();
            int step = (int)(ftp / COUNT);
            for (int i = step / 2; i < ftp; i += step) {
                grabber.setFrameNumber(i);
                // 获取无效帧时 继续获取临近帧
                for (int j = 0; j < step / 4; j++) {
                    if (insertFrame(grabber.grabFrame(), resourceId)) {
                        break;
                    }
                    log.info("Retry: {}", j);
                }
            }

            grabber.stop();
            grabber.close();
        }
    }

    public byte[] getCover(Long id, Integer index) {
        String password = appProperties.getAuth().getPassword();

        List<Cover> covers = coverMapper.selectList(new LambdaQueryWrapper<Cover>().eq(Cover::getResourceId, id));

        if (covers.isEmpty()) {
            // 返回纯灰色图片的二进制数据
            return new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0, -112, 119, 83, -34, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61, 0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 0, 12, 73, 68, 65, 84, 24, 87, 99, 120, -3, -6, 53, 0, 5, -122, 2, -62, 5, 107, -49, 24, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
        }

        Cover cover;
        if (index == null) {
            cover = covers.get(0);
        } else {
            cover = covers.get(index % covers.size());
        }

        if (StringUtils.isNotBlank(password)) {
            return XORUtils.execute(cover.getData(), password, false);
        }
        return cover.getData();
    }

}