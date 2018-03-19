package com.ebrain.api.monitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ebrain.api.util.JsonUtil;

/**
 * 
 * <p>
 * Description: 监控方法执行时间
 * </p>
 * 时间: 2017年8月23日 上午10:59:52
 *
 * @author peisong
 * @version v1.6
 */
@Aspect
@Component
public class LogAspect {
  private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

  @Pointcut("execution(public * com.ebrain.api..*.*Endpoint.*(..))")
  public void monitorLog() {
  }

  @Around("monitorLog()")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    try {
      return pjp.proceed();
    } finally {
      long time=(System.currentTimeMillis() - start);
      if(time<2000){
        logger.info("api:{},执行时间:{}ms",pjp.getSignature().toShortString(),time);
      }else{
        logger.warn("api:{},参数:{},执行时间:{}ms",pjp.getSignature().toString(),JsonUtil.toJson(pjp.getArgs()),time);
      }
    }
  }
}
