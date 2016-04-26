package org.albianj.persistence.impl.toolkit;

import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.RelationalOperator;
import org.albianj.persistence.object.SortStyle;

public class EnumMapping {
	public static String toRelationalOperators(RelationalOperator opt) {
		switch (opt) {
		case And: {
			return "AND";
		}
		case OR: {
			return "OR";
		}
		case Normal : {
			return "";
		}
		default: {
			return "AND";
		}
		}
	}

	public static String toLogicalOperation(LogicalOperation opt) {
		switch (opt) {
		case Equal: {
			return "=";
		}
		case Greater: {
			return ">";
		}
		case GreaterOrEqual: {
			return ">=";
		}
		case Is: {
			return "IS";
		}
		case Less: {
			return "<";
		}
		case LessOrEqual: {
			return "<=";
		}
		case NotEqual: {
			return "<>";
		}
		default: {
			return "=";
		}
		}
	}

	public static String toSortOperation(SortStyle sort) {
		switch (sort) {
		case Asc: {
			return "ASC";
		}
		case Desc: {
			return "DESC";
		}
		default: {
			return "ASC";
		}
		}
	}
}
