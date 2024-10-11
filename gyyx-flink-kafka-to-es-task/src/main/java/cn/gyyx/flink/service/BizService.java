package cn.gyyx.flink.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: yel
 * @Date: 2024/10/8 11:23
 */
@Slf4j
@Service
public class BizService {


    public void testBiz(){
        log.info("hello biz");
        System.out.println("hello biz");

    }


}
