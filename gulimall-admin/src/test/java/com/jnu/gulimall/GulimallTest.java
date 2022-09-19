package com.jnu.gulimall;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * [一句话描述该类的功能]
 *
 * @author : [游成鹤]
 * @version : [v1.0]
 * @date : [2022/9/19 0:21]
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallTest {

    @Resource
    private OSSClient ossClient;

    @Test
    public void testOssClient() {
        System.out.println(ossClient);
    }

}