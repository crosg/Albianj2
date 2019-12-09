package org.albianj.l5bridge.impl;

import com.qq.l5.L5sys;
import org.albianj.argument.RefArg;
import org.albianj.l5bridge.IL5BridgeService;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;


@AlbianServiceRant(Id =IL5BridgeService.Name,Interface = IL5BridgeService.class)
public class L5BridgeService extends FreeAlbianService implements IL5BridgeService {
    @Override
    public void exchange(String l5tag,RefArg<String> ip,RefArg<Integer> port) {
        RefArg<Integer> modId = new RefArg<>();
        RefArg<Integer> cmdId = new RefArg<>();
        RefArg<Float> totS = new RefArg<>();
        parserL5Tag(l5tag,modId,cmdId,totS);

        L5sys.QosRequest req = new L5sys.QosRequest();
        req.modId = modId.getValue();
        req.cmdId = cmdId.getValue();

        long l5ReqStart = System.currentTimeMillis();
        L5sys l5sys = new L5sys();
        int code = l5sys.ApiGetRoute(req, totS.getValue());
        if (code < 0) {
        }
        ip.setValue(req.hostIp);
        port.setValue(req.hostPort);

    }

    public void parserL5Tag(String l5tag, RefArg<Integer> modId, RefArg<Integer> cmdId, RefArg<Float> totS){
        String[] l5ts = l5tag.split(":");
        if(l5ts.length < 3){

        }
        if(!l5ts[0].toUpperCase().equals("L5")) {

        }
        modId.setValue(Integer.valueOf(l5ts[1]));
        cmdId.setValue(Integer.valueOf(l5ts[2]));
        if(l5ts.length == 4){
            totS.setValue(Float.parseFloat(l5ts[3]));
        } else {
            totS.setValue(new Float(0.3));
        }

    }
}
