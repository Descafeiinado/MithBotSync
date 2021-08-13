package net.heavenus.mith.database.data;

import net.heavenus.mith.database.HikariDatabase;
import net.heavenus.mith.database.data.interfaces.DataTableInfo;
import net.heavenus.mith.database.table.SyncedUserTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class DataTable {

    public abstract void init(HikariDatabase database);

    public abstract Map<String, DataContainer> getDefaultValues();

    public DataTableInfo getInfo() {
        return this.getClass().getAnnotation(DataTableInfo.class);
    }

    private static final List<DataTable> TABLES = new ArrayList<>();

    static {
        TABLES.add(new SyncedUserTable());
    }

    public static void registerTable(DataTable table) {
        TABLES.add(table);
    }

    public static Collection<DataTable> listTables() {
        return TABLES;
    }
}
