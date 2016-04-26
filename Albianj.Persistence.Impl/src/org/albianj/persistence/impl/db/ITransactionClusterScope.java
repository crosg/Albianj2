package org.albianj.persistence.impl.db;

import org.albianj.persistence.context.IWriterJob;

public interface ITransactionClusterScope {
	public boolean execute(IWriterJob writerJob);
}
