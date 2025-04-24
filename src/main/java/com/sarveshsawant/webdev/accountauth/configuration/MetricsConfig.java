package com.sarveshsawant.webdev.accountauth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;

@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    @Value("${statsd.host}")
    private String host;

    @Value("${statsd.port}")
    private int port;

    @Value("${statsd.enabled}")
    private boolean enabled;

    @Bean
    public StatsdMeterRegistry statsdMeterRegistry() {
        StatsdConfig config = new StatsdConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public StatsdFlavor flavor() {
                return StatsdFlavor.ETSY;
            }

            @Override
            public String host() {
                return host;
            }

            @Override
            public int port() {
                return port;
            }

            @Override
            public boolean enabled() {
                return enabled;
            }
        };

        return new StatsdMeterRegistry(config, Clock.SYSTEM);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}