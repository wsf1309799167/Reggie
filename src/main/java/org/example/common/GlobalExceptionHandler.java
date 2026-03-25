package org.example.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handleException(SQLIntegrityConstraintViolationException e) {
        if(e.getMessage().contains("Duplicate entry")){
           String[] split = e.getMessage().split(" ");
           String msg = split[2] + "已存在";
           return R.error(msg);
        }
        return R.error("数据库操作异常");
    }

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> handleException(CustomException e) {
        return R.error(e.getMessage());
    }
}
