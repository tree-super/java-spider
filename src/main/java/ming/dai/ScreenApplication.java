package ming.dai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class ScreenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScreenApplication.class, args);
        try {
            Runtime.getRuntime().exec("cmd /c start http://localhost:8088");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("启动成功");
    }

}
