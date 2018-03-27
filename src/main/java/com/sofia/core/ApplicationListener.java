package com.sofia.core;

/**
 * Created by yingbo.gu on 2018-03-25.
 */
public interface ApplicationListener <T extends ApplicationEvent>{

    void onApplicationEvent(ApplicationEvent event);
}
