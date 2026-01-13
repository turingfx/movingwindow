package com.alipay.flink;

/**
 * @author sansi.xy
 * @date 1/13/26
 */

public class ResourceWithTime {
    private Resource resource;
    private Long time;

    public ResourceWithTime(Resource resource, Long time) {
        this.resource = resource;
        this.time = time;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
