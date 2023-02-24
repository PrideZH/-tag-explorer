package cn.pridezh.tagexplore.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常
 * @author PrideZH
 * @since 2022/8/3 19:06
 */
@Getter
public class ServiceException extends RuntimeException {

    private final int code;
    private final String message;

    public ServiceException(int code, String message) {
        // 不进行栈追踪
        super(message, null, false, false);

        this.code = code;
        this.message = message;
    }

    public ServiceException(HttpStatus status) {
        // 不进行栈追踪
        super(status.name(), null, false, false);

        this.code = status.value();
        this.message = status.name();
    }

    public ServiceException(HttpStatus status, String message) {
        // 不进行栈追踪
        super(status.name(), null, false, false);

        this.code = status.value();
        this.message = message;
    }

}