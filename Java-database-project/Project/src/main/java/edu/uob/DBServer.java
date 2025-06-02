package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private static DBTreeNode parentNode = new DBTreeNode("");
    private DBParser DBParser = new DBParser();
    private DBTreeNode currentDatabase;
    private String currentDatabaseName;
    String response = "";

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }


    /**
     * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
     * able to mark your submission correctly.
     *
     * <p>This method handles all incoming DB commands and carries out the required actions.
     */
    public String handleCommand(String command) {
        DBTokeniser tokenizer = new DBTokeniser();
        String responseString = "";
        ArrayList<String> tokenisedCommand = DBTokeniser.setup(command);
        // depending on keywords used return correct response
        String w1 = tokenisedCommand.get(0);
        if(w1.equalsIgnoreCase("create")) response = this.handleCreateQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("use")) response = this.handleUseQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("insert")) response = this.handleInsertQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("select")) response = this.handleSelectQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("drop")) response = this.handleDropQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("alter")) response = this.handleAlterQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("delete")) response = this.handleDeleteQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("update")) response = this.handleUpdateQuery(tokenisedCommand);
        else if(w1.equalsIgnoreCase("join")) response = this.handleJoinquery(tokenisedCommand);
        else{
            return "[ERROR] Unknown command: " + w1;
        }
        if(DBParser.getFileError()) {
            return "[ERROR]";
        }
        return response;
    }

    public String handleUseQuery(ArrayList<String> command) {
        String response = "[OK]";
        currentDatabase = DBParser.searchForNode(parentNode, command.get(1));
        if(currentDatabase == null){
            response = "[ERROR] invalid database: " + command.get(1);
        }
        else {
            currentDatabaseName = currentDatabase.getData();
        }
        return response;
    }

    public String handleCreateQuery(ArrayList<String> command) {
        if(DBParser.iskeyword(command.get(2)) || DBParser.includesSymbol(command.get(2))){
            return "[ERROR] invalid database name";
        }
        if(command.get(1).equalsIgnoreCase("database")){
            if(DBParser.searchForNode(parentNode, command.get(2)) != null){
                return "[ERROR] database already exists: " + command.get(2);
            }
            String databaseName = command.get(2).toLowerCase();
            DBTreeNode database = parentNode.addChild(databaseName);
            File newFolder = new File(storageFolderPath, databaseName);
            newFolder.mkdir();
        }
        else if(command.get(1).equalsIgnoreCase("table")){
            String tableResponse = createTable(command);
            if(tableResponse.contains("[ERROR]")) return tableResponse;
        }
        else{
            return "[ERROR] unknown command: " + command.get(1);
        }
        return "[OK]";
    }

    public String createTable(ArrayList<String> command) {
        if(DBParser.iskeyword(command.get(2)) || DBParser.includesSymbol(command.get(2))){
            return "[ERROR] invalid table name";
        }
        if(currentDatabase == null){
            return "[ERROR] no current database, set with use command";
        }
        if(command.get(3).equals("(") && DBParser.checkDuplicateAttributes(command)){
            return "[ERROR] duplicate attributes";
        }
        if(currentDatabaseName.equalsIgnoreCase(command.get(2))){
            return "[ERROR] same name as database";
        }
        if(DBParser.searchForNode(currentDatabase, command.get(2)) != null){
            return "[ERROR] table already exists: " + command.get(2);
        }
        DBTreeNode tableNode = currentDatabase.addChild(command.get(2).toLowerCase());
        tableNode.setIDCount();
        String tableFileName = command.get(2).toLowerCase();
        String tableFilePath = storageFolderPath + File.separator + currentDatabaseName;
        File tableFile = new File(tableFilePath, tableFileName + ".tab");
        try {
            tableFile.createNewFile();
        } catch (IOException e) {
            return "[ERROR] unable to create file";
        }
        if(command.get(3).equals("(")){
            ArrayList<String> attributes = DBParser.getListOfAttributes(command);
            for(int i = 0; i < attributes.size(); i++){
                if(DBParser.iskeyword(attributes.get(i)) || DBParser.includesSymbol(attributes.get(i))){
                    return "[ERROR] invalid attribute name";
                }
            }
            DBParser.addAttributes(command, tableNode, tableFilePath);
        }
        return "";
    }

    public String handleInsertQuery(ArrayList<String> command){
        if(!command.get(DBParser.indexOfIgnoreCase(command, "values") + 1).equals("(")
        && !command.get(command.size() - 2) .equals(")")){
            return "[ERROR] invalid syntax";
        }
        if(!command.get(1).equalsIgnoreCase("into") ||
        !command.get(3).equalsIgnoreCase("values")){
            return "[ERROR] invalid syntax";
        }
        String tableFilePath = getFilePath(currentDatabaseName, command.get(2));
        DBTreeNode databaseNode = DBParser.searchForNode(parentNode, currentDatabaseName);
        DBTreeNode tableNode = DBParser.searchForNode(databaseNode, command.get(2));
        if(tableNode == null){
            return "[ERROR] invalid table: " + command.get(2);
        }
        // count number of values
        int numOfValues = (command.indexOf(")")) - (command.indexOf("(") + 1);
        for(int i = command.indexOf("("); i <= command.indexOf(")"); i++){
            if(command.get(i).equals(",")) numOfValues--;
        }
        if(numOfValues + 1 != tableNode.getNext().size()){
            return "[ERROR] invalid number of values";
        }
        DBParser.insertValues(command, tableNode, tableFilePath);
        return "[OK]";
    }


     public String handleSelectQuery(ArrayList<String> command){
         String tableName = command.get(DBParser.indexOfIgnoreCase(command, "FROM") +  1);
         String tableFilePath = getFilePath(currentDatabaseName, tableName);
         DBTreeNode tableNode = DBParser.searchForNode(parentNode, tableName);
         if(tableNode == null){
             return "[ERROR] invalid table";
         }
         ArrayList<String> selectAllResponse = new ArrayList<>();
         selectAllResponse = DBParser.readAllFromFile(tableFilePath);
         ArrayList<String> selectResponse = new ArrayList<>();
         if(!command.contains("*")){
             selectResponse = DBParser.selectAttributes(command, tableNode, selectAllResponse);
         }
         else{
             if(!DBParser.containsIgnoreCase(command, "WHERE")){
                 for(int i = 0; i < selectAllResponse.size(); i++){
                     selectAllResponse.set(i, selectAllResponse.get(i) + "\n");
                 }
                 String result = String.join(" ", selectAllResponse);
                 return "[OK]\n" + result;
             }
         }
         if(DBParser.containsIgnoreCase(command, "WHERE")){
             if(selectAllResponse.isEmpty()) return "[ERROR] empty table";
             if(command.contains("*")){
                 selectResponse = DBParser.getLinesUsingConditions(selectAllResponse, command, selectAllResponse);
             }
             else{
                 selectResponse = DBParser.getLinesUsingConditions(selectResponse, command, selectAllResponse);
             }
         }
         selectResponse = DBParser.removeIdIfNeeded(selectResponse, command);
         String result = String.join(" ", selectResponse);
         return "[OK]\n" + result;
     }

     public String handleDropQuery(ArrayList<String> command){
         if(command.get(1).equalsIgnoreCase("database")){
             File databaseFolder = new File(storageFolderPath + File.separator + command.get(2));
             DBParser.deleteFolder(databaseFolder);
             databaseFolder.delete();
             // delete tree node and children
             DBTreeNode databaseNode = DBParser.searchForNode(parentNode, command.get(2));
             if(databaseNode == null) return "[ERROR] invalid database: " + command.get(2);
             databaseNode.deleteAllChildren(databaseNode);
         }
         if(command.get(1).equalsIgnoreCase("table")){
             File tableFile = new File(getFilePath(currentDatabaseName, command.get(2)));
             tableFile.delete();
             DBTreeNode tableNode = DBParser.searchForNode(currentDatabase, command.get(2));
             if(tableNode == null) return "[ERROR] invalid table: " + command.get(2);
             tableNode.deleteAllChildren(tableNode);
         }
         else{
             return "[ERROR] unknown command";
         }
         return "[OK]";
     }

     public String handleAlterQuery(ArrayList<String> command){
         if(!command.get(1).equalsIgnoreCase("table")){
             return "[ERROR] invalid command";
         }
         String tableName = command.get(2);
         DBTreeNode tableNode = DBParser.searchForNode(parentNode, tableName);
         if(tableNode == null) return "[ERROR] invalid table: " + command.get(2);
         String attributeName = command.get(4);
         String tableFilePath = getFilePath(currentDatabaseName, tableName);
         if(command.get(3).equalsIgnoreCase("drop")){
             if(attributeName.equalsIgnoreCase("id")){
                 return "[ERROR] cannot drop id";
             }
             DBTreeNode attributeNode = DBParser.searchForNode(tableNode, attributeName);
             if(attributeNode == null) return "[ERROR] invalid attribute: " + command.get(4);
             attributeNode.deleteAllChildren(attributeNode);
             DBParser.removeAttributeFromFile(attributeName, tableFilePath, tableNode);
         }
         if(command.get(3).equalsIgnoreCase("add")){
             if(DBParser.searchForNode(tableNode, attributeName) != null) {
                 return "[ERROR] duplicate attribute: " + command.get(4);
             }
             if(DBParser.iskeyword(attributeName) || DBParser.includesSymbol(attributeName)){
                 return "[ERROR] invalid attribute name";
             }
             tableNode.addChild(attributeName);
             DBParser.addAttributeToFile(attributeName, tableFilePath, tableNode);
             DBParser.addAttributes(command, tableNode, tableFilePath);
         }
         return "[OK]";
     }

     public String handleDeleteQuery(ArrayList<String> command){
         if(!command.get(1).equalsIgnoreCase("from") || !DBParser.containsIgnoreCase(command, "WHERE")){
             return "[ERROR] invalid syntax";
         }
         String tableName = command.get(2);
         DBTreeNode tableNode = DBParser.searchForNode(parentNode, tableName);
         if(tableNode == null) return "[ERROR] invalid table: " + command.get(2);
         String filePath = getFilePath(currentDatabaseName, tableName);
         ArrayList<String> wholeTable = DBParser.readAllFromFile(filePath);
         ArrayList<String> linesToDelete = DBParser.getLinesUsingConditions(wholeTable, command, wholeTable);
         linesToDelete.remove(0);
         DBParser.removeLineFromFile(tableName, filePath, linesToDelete, wholeTable);
         return "[OK]";
     }

     public String handleUpdateQuery(ArrayList<String> command){
        if(!command.get(2).equalsIgnoreCase("set") || !DBParser.containsIgnoreCase(command, "where")){
            return "[ERROR] invalid syntax";
        }
        DBTreeNode tableNode = DBParser.searchForNode(parentNode, command.get(1));
        for(int i = DBParser.indexOfIgnoreCase(command, "set") + 1;
            i < DBParser.indexOfIgnoreCase(command, "where"); i += 4){
            if(DBParser.searchForNode(tableNode, command.get(i)) == null){
                return "[ERROR] invalid attribute: " + command.get(i);
            }
            if(command.get(i).equalsIgnoreCase("id")){
                return "[ERROR] cannot update id";
            }
        }
        if(tableNode == null) return "[ERROR] invalid table: " + command.get(1);
        String filePath = getFilePath(currentDatabaseName, command.get(1));
        ArrayList<String> wholeTable = DBParser.readAllFromFile(filePath);
        ArrayList<String> linesToUpdate = DBParser.getLinesUsingConditions(wholeTable, command , wholeTable);
        linesToUpdate.remove(0);
        DBParser.updateFile(command, filePath, tableNode, linesToUpdate, wholeTable);
        return "[OK]";
     }

     public String handleJoinquery(ArrayList<String> command){
        if(!command.get(2).equalsIgnoreCase("and") || !command.get(4).equalsIgnoreCase("on")
        || !command.get(6).equalsIgnoreCase("and")){
            return "[ERROR] invalid syntax";
        }
        DBTreeNode tableNode1 = DBParser.searchForNode(parentNode, command.get(1));
        DBTreeNode tableNode2 = DBParser.searchForNode(parentNode, command.get(3));
        if(tableNode1 == null || tableNode2 == null){
            return "[ERROR] invalid table(s)";
        }
        if(DBParser.searchForNode(tableNode1, command.get(5)) == null ||
        DBParser.searchForNode(tableNode2, command.get(7)) == null){
            return "[ERROR] invalid attribute(s)";
        }
        String filePath1 = getFilePath(currentDatabaseName, command.get(1));
        String filePath2 = getFilePath(currentDatabaseName, command.get(3));
        ArrayList<String> response = DBParser.joinTables(command, filePath1, filePath2);
        String result = String.join(" ", response);
        return result;
     }

     public String getFilePath(String databaseName, String tableName){
         return storageFolderPath + File.separator + databaseName + File.separator + tableName + ".tab";
     }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
