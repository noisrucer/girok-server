package com.girok.girokserver;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomService {

    private final CustomProperties customProperties;

    @PostConstruct
    public void init() {
        test();
    }

    public void test() {
        System.out.println("customProperties = " + customProperties.getName());
        System.out.println("customProperties.getAge() = " + customProperties.getAge());
    }
}
