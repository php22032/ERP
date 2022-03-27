package com.ywy.erp.config;

import com.gitee.starblues.extension.mybatis.SpringBootMybatisExtension;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginBeanConfig {
    @Bean
    public PluginApplication pluginApplication(){
        PluginApplication pluginApplication = new AutoPluginApplication();
        pluginApplication.addExtension(new SpringBootMybatisExtension());
        return pluginApplication;
    }
}
