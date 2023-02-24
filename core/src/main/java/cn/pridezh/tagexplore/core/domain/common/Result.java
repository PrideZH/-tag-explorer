package cn.pridezh.tagexplore.core.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author PrideZH
 */
@Accessors(chain = true)
@AllArgsConstructor
@Data
public class Result<T> implements Serializable {

    private Integer code;

    private String message;

    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.name(), data);
    }

    public static Result<Void> fail() {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), null);
    }

    public static Result<Void> fail(HttpStatus status) {
        return new Result<>(status.value(), status.name(), null);
    }

    public static <T> Result<T> fail(HttpStatus status, T data) {
        return new Result<>(status.value(), status.name(), data);
    }

    public static Result<Void> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

}