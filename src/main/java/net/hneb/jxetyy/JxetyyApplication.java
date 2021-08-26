package net.hneb.jxetyy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("net.hneb.jxetyy.dao")
@EnableTransactionManagement
@Slf4j
public class JxetyyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JxetyyApplication.class, args);
        log.info("启动成功");
    }

}
