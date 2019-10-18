package com.lepao.validate;


import com.alibaba.fastjson.JSONObject;
import com.lepao.validate.entity.Form;
import com.lepao.validate.service.VadidateService;
import com.lepao.validate.util.AsigoUtil;
import com.lepao.validate.util.HttpRequestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidateApplicationTests {
    @Autowired
    private VadidateService vadidateService;

    @Test
    public void contextLoads() {
//       Form formSheBao = vadidateService.getAllForm("2019年公积金处理订单");
//       vadidateService.getFormContent(formSheBao.getId(),null);
       // vadidateService.validate();
//        vadidateService.getFormContent("5b9244cb75a03c126dff4c42",null);
//        vadidateService.getFormContent("5b9244d875a03c126dff4c68",null);
//        vadidateService.getFormContent("5b9244d275a03c126dff4c4e",null);
        //vadidateService.select(1);
        //vadidateService.validate1();

    }
}
