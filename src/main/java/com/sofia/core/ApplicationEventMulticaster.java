package com.sofia.core;

/**
 * Created by yingbo.gu on 2018-03-25.
 */
public interface ApplicationEventMulticaster <T extends ApplicationEvent>{
    void publishEvent(ApplicationEvent event);
}
