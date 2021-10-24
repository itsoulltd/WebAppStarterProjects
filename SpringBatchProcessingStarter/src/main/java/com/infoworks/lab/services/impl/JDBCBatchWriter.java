package com.infoworks.lab.services.impl;

import com.infoworks.lab.services.definition.ContentWriter;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JDBCBatchWriter<E extends Entity> implements ContentWriter<E> {

    private QueryExecutor executor;
    private Class<E> eClass;
    private static Logger LOG = Logger.getLogger(JDBCBatchWriter.class.getSimpleName());

    public JDBCBatchWriter(QueryExecutor executor, Class<E> type) {
        this.executor = executor;
        this.eClass = type;
    }

    @Override
    public void write(String sheetName, Map<Integer, E> data, boolean skipZeroIndex) {
        if (eClass == null || executor == null) {
            LOG.warning("Class<T> is null or QueryExecutor is null.");
            return;
        }
        //Insert in batch:
        try {
            int rowCount = batchInsert(data.values());
            LOG.info("Successfully Inserted: " + rowCount);
        } catch (SQLException e) {
            LOG.info(e.getMessage());
        }
    }

    public final int batchInsert(Collection<? extends Entity> rows) throws SQLException {
        if (eClass == null || executor == null) return 0;
        if (rows == null || rows.size() <= 0) return 0;
        //
        Row rowDef = Entity.getRowDefinition(eClass);
        SQLInsertQuery insertQuery = new SQLQuery.Builder(QueryType.INSERT)
                .into(Entity.tableName(eClass))
                .values(rowDef.getProperties().toArray(new Property[0]))
                .build();
        //LOG.info(insertQuery.toString());
        List<Row> needToInsert = rows.stream()
                .map(row -> row.getRow())
                .collect(Collectors.toList());
        //
        int batchSize = needToInsert.size();
        boolean isAutoInsert = Entity.isAutoID(eClass);
        Integer[] res = executor.executeInsert(isAutoInsert, batchSize, insertQuery, needToInsert);
        return res != null ? res.length : 0;
    }

    @Override
    public void close() throws Exception {
        if (executor != null){
            executor.close();
        }
    }
}
