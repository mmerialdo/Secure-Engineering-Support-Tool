package org.crmf.proxy.authnauthz;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/************************************************************************
 * Created: 08/09/2020                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/

@Aspect
@Component
public class PermissionEvaluatorCustom {

  @Autowired
  private ShiroSecurityProcessorCustom processor;

  @Pointcut("@annotation(perm)")
  public void permission(Permission perm) {
  }

  @Before("permission(perm)")
  public Object intercept(JoinPoint joinPoint, Permission perm) throws Throwable {
    String shiroToken = null;
    String projectIdentifier = null;

    Object[] args = joinPoint.getArgs();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
    Method method = methodSignature.getMethod();
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    assert args.length == parameterAnnotations.length;
    for (int argIndex = 0; argIndex < args.length; argIndex++) {
      for (Annotation annotation : parameterAnnotations[argIndex]) {
        if (!(annotation instanceof RequestParam))
          continue;
        RequestParam requestParam = (RequestParam) annotation;
        if ("SHIRO_SECURITY_TOKEN".equals(requestParam.name())) {
          shiroToken = args[argIndex].toString();
        }
      }
    }
    this.processor.applySecurityPolicy(perm.value(), shiroToken);

    return null;
  }
}
