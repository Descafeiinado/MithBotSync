package net.heavenus.mith.logger;

import net.heavenus.mith.BotSync;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class LoggerExecutor {

    public Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    public String name;
    public LoggerExecutor() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        this.name = "plugins/MithBotSync/log-" + format.format(this.timestamp) + ".txt";
    }

    public void createFile(){
        try {
            File myObj = new File(this.name);
            myObj.createNewFile();
            FileWriter fileWriter = new FileWriter(myObj);
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : BotSync.debugLogs){
                stringBuilder.append(string).append("\n");
            }
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeToFile(){
        try {
            FileWriter myWriter = new FileWriter(this.name);
            for(String line : BotSync.debugLogs){
                myWriter.write(line);
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
