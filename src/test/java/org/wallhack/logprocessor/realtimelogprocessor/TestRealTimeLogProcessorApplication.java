package org.wallhack.logprocessor.realtimelogprocessor;

import org.springframework.boot.SpringApplication;

public class TestRealTimeLogProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.from(RealTimeLogProcessorApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
