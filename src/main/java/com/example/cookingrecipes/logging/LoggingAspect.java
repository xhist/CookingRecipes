package com.example.cookingrecipes.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Pointcut that matches all repositories, services and Web REST endpoints
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut
    }

    // Pointcut that matches all Spring beans in the application's main packages
    @Pointcut("within(com.example.cookingrecipes..*)" +
            " || within(com.example.cookingrecipes.service..*)" +
            " || within(com.example.cookingrecipes.controller..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut
    }

    // Log methods throwing exceptions
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL");

        if (log.isDebugEnabled()) {
            log.debug("Exception details:", e);

            // Log method arguments
            if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
                log.debug("Method arguments:");
                Arrays.stream(joinPoint.getArgs())
                        .forEach(arg -> log.debug("Arg: {}", arg));
            }
        }
    }
}