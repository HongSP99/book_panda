package com.booksajo.bookPanda.config.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final LoggingTracer loggingTracer;

    public LoggingAspect(LoggingTracer loggingTracer) {
        this.loggingTracer = loggingTracer;
    }

    @Pointcut("execution(* com.booksajo.bookPanda..controller..*(..)) "
            + "|| execution(* com.booksajo.bookPanda..service..*(..)) "
            + "|| execution(* com.booksajo.bookPanda..repository..*(..))")
    public void allComponents() {}

    @Around("allComponents()")
    public Object doLogTrace(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String message = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        try {
            loggingTracer.begin(message, args);
            final Object result = joinPoint.proceed();
            loggingTracer.end(message);
            return result;
        } catch (final Exception e) {
            loggingTracer.exception(message, e);
            throw e;
        }
    }
}
