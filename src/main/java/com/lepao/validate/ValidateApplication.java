package com.lepao.validate;

import com.lepao.validate.service.VadidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableCaching  //开启定时任务
@SpringBootApplication
@EnableAsync
public class ValidateApplication {

    @Autowired
    public static void main(String[] args) {


        SpringApplication.run(ValidateApplication.class, args);
        VadidateService vadidateService = new VadidateService();
        while(true)
        {
            //获取当前时间戳
            long currentTime = System.currentTimeMillis()/1000;
            if((currentTime%86400)==28800)
            {
                //调用方法
                vadidateService.validate1("TbAldsfx5445w83zxbewbwprkmmxv8sanrk4343s2pwhebvyp8");
                try {
                    //睡一秒防止重复启动
                    Thread.sleep(10000);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
