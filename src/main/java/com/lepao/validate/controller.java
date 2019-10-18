package com.lepao.validate;

import com.lepao.validate.service.VadidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {
    @Autowired
    private VadidateService vadidateService;

    @RequestMapping("start")
    public void rest()
    {
        vadidateService.validate1("TbAldsfx5445w83zxbewbwprkmmxv8sanrk4343s2pwhebvyp8");
    }
}
