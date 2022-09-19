package com.jnu.gulimall.exception;

import com.jnu.common.exception.BizCodeEnume;
import com.jnu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * [集中处理所有异常]
 *
 * @author : [游成鹤]
 * @version : [v1.0]
 * @createTime : [2022/8/11 11:30]
 */

@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "")
@RestControllerAdvice(basePackages = "com.jnu.gulimall")
public class GulimallExceptionControllerAdvice {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors()
                .forEach((fieldError)-> errorMap.put(fieldError.getField(),fieldError.getDefaultMessage()));

        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data",errorMap);
    }

    /**
     * 其他所有异常
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误：",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }

}