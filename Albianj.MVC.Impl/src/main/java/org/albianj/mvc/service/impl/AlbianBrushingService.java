package org.albianj.mvc.service.impl;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.BrushingConfigurtion;
import org.albianj.mvc.config.RequestCounter;
import org.albianj.mvc.lang.ServerHelper;
import org.albianj.mvc.service.IAlbianBrushingService;
import org.albianj.service.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by xuhaifeng on 17/1/19.
 */
@AlbianServiceRant(Id = IAlbianBrushingService.Name, Interface = IAlbianBrushingService.class)
public class AlbianBrushingService extends FreeAlbianService implements IAlbianBrushingService {
    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianMvcConfigurtionService.HttpConfigurtion",SetterLifetime = AlbianServiceFieldSetterLifetime.AfterNew)
    private AlbianHttpConfigurtion c;

    public String getServiceName() {
        return Name;
    }

    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.c = c;
    }


    public boolean consume(HttpServletRequest request) {
        String ip = null;
        try {
            ip = ServerHelper.getIpAddress(request);
        } catch (IOException e) {
            ip = "unknown";
        }
        BrushingConfigurtion brushing = c.getBrushing();
        if (null == brushing) {
            brushing = new BrushingConfigurtion();
        }

        long ts = (AlbianDateTime.getCurrentSeconds() / brushing.getUnitTime()) * brushing.getUnitTime();
        RequestCounter counter = getRequestCounter(request, ip);
        if (null == counter) {
            counter = new RequestCounter();
            counter.setRequestCount(brushing.getRequestCount() - 1);
            counter.setUnitTime(ts);
            storeRequestCounter(request, ip, counter);
            return true;
        }

        if (ts == counter.getUnitTime()) {
            counter.subCounter();
            storeRequestCounter(request, ip, counter);
            return 0 <= counter.getRequestCount();
        }

        counter.setRequestCount(brushing.getRequestCount() - 1);
        counter.setUnitTime(ts);
        storeRequestCounter(request, ip, counter);
        return true;

    }

    public RequestCounter getRequestCounter(HttpServletRequest request, String ip) {
        RequestCounter ct = (RequestCounter) request.getSession().getAttribute(ip);
        return ct;
    }

    public void storeRequestCounter(HttpServletRequest request, String ip, RequestCounter counter) {
        request.getSession().setAttribute(ip, counter);
    }
}
