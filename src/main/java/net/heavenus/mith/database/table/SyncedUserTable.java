package net.heavenus.mith.database.table;

import net.heavenus.mith.database.HikariDatabase;
import net.heavenus.mith.database.data.DataContainer;
import net.heavenus.mith.database.data.DataTable;
import net.heavenus.mith.database.data.interfaces.DataTableInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@DataTableInfo(
        name = "MithBotSync",
        create = "CREATE TABLE IF NOT EXISTS `MithBotSync` (`name` VARCHAR(64), `discord_id` VARCHAR(64), `uuid` VARCHAR(64), `time` LONG, PRIMARY KEY(`name`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin",
        select = "SELECT ALL * FROM `MithBotSync` WHERE `name` = ?",
        insert = "INSERT INTO `MithBotSync` VALUES (?, ?, ?, ?;)",
        update = "UPDATE `MithBotSync` SET `discord_id` = ?, `uuid` = ?, `time` = ?")
public class SyncedUserTable extends DataTable {
    @Override
    public void init(HikariDatabase database) {

    }

    public Map<String, DataContainer> getDefaultValues() {
        Map<String, DataContainer> defaultValues = new LinkedHashMap<>();
        defaultValues.put("discord_id", new DataContainer("0"));
        defaultValues.put("uuid", new DataContainer("none"));
        defaultValues.put("time", new DataContainer(System.currentTimeMillis()));
        return defaultValues;
    }
}
