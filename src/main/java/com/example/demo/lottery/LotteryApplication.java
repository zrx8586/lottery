package com.example.demo.lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author long_w
 */
@SpringBootApplication()
@EnableScheduling
public class LotteryApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(LotteryApplication.class, args);
//	}

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream is = new ClassPathResource("jvm.properties").getInputStream()) {
            props.load(is);
        }
        // 打印参数
        props.forEach((k, v) -> System.out.println("JVM参数: " + k + "=" + v));
        SpringApplication.run(LotteryApplication.class, args);
    }

}
