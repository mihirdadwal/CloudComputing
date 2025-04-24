package com.sarveshsawant.webdev.accountauth.configuration;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Track API endpoint execution time and count
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object trackApiEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        // Increment counter for API calls
        meterRegistry.counter("api.calls",
                "method", methodName,
                "class", className
        ).increment();

        // Time the API execution
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("userauth.api.endpoint.time")
                    .description("Time taken for API endpoint execution")
                    .tag("method", methodName)
                    .tag("class", className)
                    .register(meterRegistry));
        }
    }

    // Track database query execution time
    @Around("execution(* com.sarveshsawant.webdev.accountauth.repositories.*.*(..))")
    public Object trackDatabaseQueryTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("userauth.database.query.time")
                    .description("Time taken for database queries")
                    .tag("method", joinPoint.getSignature().getName())
                    .tag("class", joinPoint.getSignature().getDeclaringType().getSimpleName())
                    .register(meterRegistry));
        }
    }

    // Track S3 operations time
    @Around("execution(* com.sarveshsawant.webdev.accountauth.service.StorageService.*(..))")
    public Object trackS3OperationTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("userauth.s3.operation.time")
                    .description("Time taken for S3 operations")
                    .tag("operation", joinPoint.getSignature().getName())
                    .register(meterRegistry));
        }
    }
}