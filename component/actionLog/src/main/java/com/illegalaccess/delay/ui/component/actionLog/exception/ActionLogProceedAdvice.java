package com.illegalaccess.delay.ui.component.actionLog.exception;

import com.illegalaccess.delay.ui.common.exception.advice.ExceptionAdvice;
import com.illegalaccess.delay.ui.component.actionLog.action.SystemAction;
import com.illegalaccess.delay.ui.component.actionLog.annotation.ActionLog;

/**
 * 运行时抛出的异常进行日志记录
 * @author 小懒虫
 * @date 2019/4/6
 */
public class ActionLogProceedAdvice implements ExceptionAdvice {

    @Override
    @ActionLog(key = SystemAction.RUNTIME_EXCEPTION, action = SystemAction.class)
    public void run(RuntimeException e) {}
}
