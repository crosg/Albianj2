package org.albianj.persistence.impl.db;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.verify.Validate;

public class PersistenceNamedParameter {
	/**
	
	 */
	public static void parseSql(IPersistenceCommand cmd) {
		String regex = "#\\w+#";// insert into tablename(col1,col2) values(
								// #col1#,#col2#)
		String cmdText = cmd.getCommandText();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(cmdText);
		Map<Integer, String> paramsMap = new HashMap<Integer, String>();

		int idx = 1;
		while (m.find()) {
			paramsMap.put(new Integer(idx++), m.group());
		}
		cmdText = cmdText.replaceAll(regex, "?");
		cmd.setCommandText(cmdText);
		cmd.setParameterMapper(paramsMap);

		if (!Validate.isNullOrEmptyOrAllSpace(cmd.getRollbackCommandText())) {
			String rollbackText = cmd.getRollbackCommandText();
			Matcher rm = p.matcher(rollbackText);
			Map<Integer, String> rollbackParamsMap = new HashMap<Integer, String>();

			idx = 1;
			while (rm.find()) {
				rollbackParamsMap.put(new Integer(idx++), rm.group());
			}
			rollbackText = rollbackText.replaceAll(regex, "?");
			cmd.setRollbackCommandText(rollbackText);
			cmd.setRollbackParameterMapper(rollbackParamsMap);
		}
		return;
	}
}
