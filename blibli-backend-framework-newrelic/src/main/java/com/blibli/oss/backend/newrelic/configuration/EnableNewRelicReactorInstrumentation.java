package com.blibli.oss.backend.newrelic.configuration;

import com.blibli.oss.backend.newrelic.configuration.reporter.NewRelicMongoReporterAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({NewRelicReactorInstrumentationAutoConfiguration.class, NewRelicMongoReporterAutoConfiguration.class})
public @interface EnableNewRelicReactorInstrumentation {
}
