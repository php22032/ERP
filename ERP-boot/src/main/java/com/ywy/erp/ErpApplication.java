package com.ywy.erp;

import com.ywy.erp.utils.ComputerInfo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@MapperScan("com.ywy.erp.mappers")
@ServletComponentScan
@EnableScheduling
public class ErpApplication{
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(ErpApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        System.out.println("启动成功，后端服务API地址：http://" + ComputerInfo.getIpAddr() + ":"
                + environment.getProperty("server.port") + "/ERP-boot/doc.html");
        System.out.println("您还需启动前端服务，启动命令：yarn run serve 或 npm run serve，测试用户：ywy，密码：123456");
    }
}
