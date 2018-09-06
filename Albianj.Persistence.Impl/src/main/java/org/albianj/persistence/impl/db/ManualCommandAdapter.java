package org.albianj.persistence.impl.db;

import org.albianj.persistence.context.IInternalManualCommand;
import org.albianj.persistence.context.IManualCommand;
import org.albianj.persistence.context.IManualContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualCommandAdapter implements IManualCommandAdapter {
    @Override
    public IManualContext createManualCommands(IManualContext mctx) {
        List<IManualCommand> cmds = mctx.getCommands();
        List<IInternalManualCommand> imcs = new LinkedList<>();
        for(IManualCommand cmd : cmds){
            IInternalManualCommand imc = PersistenceNamedParameter.parseSql(cmd);
            imc.setCmdType(cmd.getCmdType());
            imc.setCommandParameters(cmd.getCommandParameters());
            imcs.add(imc);
        }
        mctx.setInternelCommands(imcs);
        return mctx;
    }
}
