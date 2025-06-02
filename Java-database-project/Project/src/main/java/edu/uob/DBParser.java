package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DBParser {

    boolean fileError = false;

    public boolean containsIgnoreCase(ArrayList<String> str, String str2) {
        for (String s : str) {
            if (s.toLowerCase().contains(str2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public int indexOfIgnoreCase(ArrayList<String> str, String str2) {
        int index = -1;
        for(int i = 0; i < str.size(); i++) {
            if(str.get(i).equalsIgnoreCase(str2)) index = i;
        }
        return index;
    }

    public ArrayList<String> getListOfAttributes(ArrayList<String> command){
        ArrayList<String> attributes = new ArrayList<>();
        for(int i = command.indexOf("(") + 1; i < command.indexOf(")"); i++){
            if(!command.get(i).equals(",")){
                attributes.add(command.get(i));
            }
        }
        return attributes;
    }

    public boolean iskeyword(String word){
        String[] keywords = {"create", "use", "table", "database", "drop", "add", "alter", "insert",
        "into", "select", "values", "from", "where", "update", "set", "delete", "join", "and", "or",
        "on", "true", "false", "like"};
        for (String keyword : keywords) {
            if(word.equalsIgnoreCase(keyword)) return true;
        }
        return false;
    }

    public boolean includesSymbol(String word){
        String[] symbols = {"!", "#", "$", "%", "&", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", ">", "=",
        "<", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "}", "~"};
        for (String symbol : symbols) {
            if(word.contains(symbol)) return true;
        }
        return false;
    }

    public boolean checkDuplicateAttributes(ArrayList<String> command){
        ArrayList<String> attributes = getListOfAttributes(command);
        for(int i = 0; i < attributes.size(); i++){
            for(int j = i + 1; j < attributes.size(); j++){
                if(attributes.get(i).equalsIgnoreCase(attributes.get(j))){
                    return true;
                }
            }
        }
        return false;
    }

    public void writeToFile(String toWrite, String filePath, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath, append);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            buffWriter.write(toWrite);
            buffWriter.close();
            writer.close();
        } catch (IOException e) {
            setFileError();
        }
    }


    public void addAttributes(ArrayList<String> command, DBTreeNode tableNode, String filePath)  {
        filePath = filePath + File.separator + tableNode.getData() + ".tab";
        tableNode.addChild("id");
        writeToFile("id\t", filePath, false);
        for (int i = command.indexOf("(") + 1; i < command.size() - 1; i = i + 2) {
            tableNode.addChild(command.get(i));
            writeToFile(command.get(i) + "\t", filePath, true);
        }
        writeToFile("\n", filePath, true);
    }

    public void insertValues(ArrayList<String> command, DBTreeNode tableNode, String filePath) {
        String currentID = String.valueOf(tableNode.getAndIncrementID());
        tableNode.getNext().get(0).addChild(currentID);
        writeToFile(currentID + "\t", filePath, true);
        int attributeCnt = 1;
        for (int i = command.indexOf("(") + 1; i < command.size() - 1; i += 2) {
            tableNode.getNext().get(attributeCnt).addChild(command.get(i));
            writeToFile(command.get(i) + "\t", filePath, true);
            attributeCnt++;
        }
        writeToFile("\n", filePath, true);
    }

    public ArrayList<String> readAllFromFile(String filePath) {
        ArrayList<String> allSelected = new ArrayList<>();
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                if(line == null) break;
                allSelected.add(line);
            }
        } catch (Exception e) {
            setFileError();
        }
        return allSelected;
    }
    
    public void setFileError() {
        fileError = true;
    }
    
    public boolean getFileError() {
        return fileError;
    }

    public ArrayList<String> joinTables(ArrayList<String> command, String filepath1, String filepath2) {
        ArrayList<String> table1 = readAllFromFile(filepath1);
        ArrayList<String> table2 = readAllFromFile(filepath2);
        String joinAttribute1 = command.get(5);
        String joinAttribute2 = command.get(7);
        ArrayList<String> joinedTable = new ArrayList<>();
        String line1 = "id\t";
        String[] table1Line1 = table1.get(0).split("[\t\n]+");
        String[] table2Line1 = table2.get(0).split("[\t\n]+");
        ArrayList<Integer> table1Index = new ArrayList<Integer>();
        ArrayList<Integer> table2Index = new ArrayList<Integer>();
        int joinAttribute1Index = 0;
        int joinAttribute2Index = 0;
        for(int i = 0; i < table1Line1.length; i++) {
            if(!table1Line1[i].equalsIgnoreCase(joinAttribute1) && !table2Line1[i].equalsIgnoreCase("id")) {
                line1 = line1 + command.get(1) + "." + table1Line1[i] + "\t";
                table1Index.add(i);
            }
            else {
                joinAttribute1Index = i;
            }
        }
        for(int i = 0; i < table2Line1.length; i++) {
            if(!table2Line1[i].equalsIgnoreCase(joinAttribute2) && !table2Line1[i].equalsIgnoreCase("id")) {
                line1 = line1 + command.get(3) + "." + table2Line1[i] + "\t";
                table2Index.add(i);
            }
            else{
                joinAttribute2Index = i;
            }
        }
        line1 = line1 + "\n";
        joinedTable.add(line1);
        int currentId = 0;
        for(int i = 1; i < table1.size(); i++) {
            for(int j = 1; j < table2.size(); j++) {
                if(table1.get(i) == null || table2.get(j) == null) break;
                String[] table1Line = table1.get(i).split("[\t\n]+");
                String[] table2Line = table2.get(j).split("[\t\n]+");
                String line = "";
                if(table1Line[joinAttribute1Index].equalsIgnoreCase(table2Line[joinAttribute2Index])) {
                    line = ++currentId + "\t";
                    for (int k = 0; k < table1Index.size(); k++) {
                        line += table1Line[table1Index.get(k)] + "\t";
                    }
                    for (int k = 0; k < table2Index.size(); k++) {
                        line += table2Line[table2Index.get(k)] + "\t";
                    }
                    line += "\n";
                    joinedTable.add(line);
                }
            }
        }
        return joinedTable;
    }

    public ArrayList<String> selectAttributes(ArrayList<String> command, DBTreeNode tableNode, ArrayList<String> currentResponse) {
        ArrayList<String> listOfAttributes = new ArrayList<>();
        for(int i = 1; i < command.indexOf(tableNode.getData()) - 1; i += 2) {
            listOfAttributes.add(command.get(i));
        }
        if(!containsIgnoreCase(listOfAttributes, "id")) listOfAttributes.add(0, "id");
        // if word in string is not in list of attributes or is a child remove from string
        ArrayList<String> selectedAttributes = new ArrayList<>();
        for(String response: currentResponse) {
            String line = "";
            String[] words = response.split("[\t\n]+");
            for(String word: words) {
                for(String attribute: listOfAttributes) {
                    if(word.equalsIgnoreCase(attribute) || searchForNode(tableNode, attribute).isChild(word)){
                        line += word + "\t";
                    }
                }

            }
            selectedAttributes.add(line+ "\n");
       }
       return selectedAttributes;
    }

    public ArrayList<String> getLine(ArrayList<String> currentTable, ArrayList<String> command, String conditionW1, ArrayList<String> selectAllResponse, int indexConditionW1) {
         // go through each separate condition in command until there are no more words left
        // split first line into strings and find index of attribute
        String[] attributes = currentTable.get(0).split("[\t\n]+");
        if(command.get(0).equalsIgnoreCase("select")) {
            attributes = selectAllResponse.get(0).split("[\t\n]+");
        }
        int indexOfAttribute = 0;
        for(int i = 0; i < attributes.length; i++) {
            if(attributes[i].contains(conditionW1)) {
                indexOfAttribute = i;
            }
        }
        String comparator = command.get(indexConditionW1 + 1);
        ArrayList<String> line = new ArrayList<>();
        for(int i = 1; i < currentTable.size(); i++) {
            if(currentTable.get(i) == null) break;
            String[] values = currentTable.get(i).split("[\t\n]+");
            // for value at the same index of attribute find if it fulfills condition
            if(command.get(0).equalsIgnoreCase("select")){
                for(String response: selectAllResponse) {
                    if(response.charAt(0) == currentTable.get(i).charAt(0)) {
                        values = response.split("[\t\n]+");
                    }
                }
            }
            if(fulfillsCondition(comparator, values[indexOfAttribute], command.get(indexConditionW1 + 2))){
                line.add(currentTable.get(i));
            }
        }
        return line;
    }

    public ArrayList<String> getLinesUsingConditions(ArrayList<String> currentTable, ArrayList<String> command, ArrayList<String> selectAllResponse){
        String conditionW1 = command.get(indexOfIgnoreCase(command, "WHERE")+ 1);
        int indexConditionW1 = indexOfIgnoreCase(command, "WHERE") + 1;
        if(conditionW1.equals("(")){
            conditionW1 = command.get(command.indexOf(conditionW1)+ 1);
            indexConditionW1++;
        }
        ArrayList<String> firstLine = getLine(currentTable, command, conditionW1, selectAllResponse, indexConditionW1);
        firstLine.add(0, currentTable.get(0));
        ArrayList<ArrayList<String>> allLines = new ArrayList<>();
        allLines.add(firstLine);
        while(indexConditionW1 + 1 < command.size()){
            if(conditionW1.equalsIgnoreCase("OR")){
                if(command.get(indexConditionW1 + 1).equals("(")){
                    conditionW1 = command.get(indexConditionW1 + 2);
                    indexConditionW1+= 2;
                }
                else{
                    conditionW1 = command.get(indexConditionW1 + 1);
                    indexConditionW1++;
                }
                ArrayList<String> lines = new ArrayList<>();
                ArrayList<String> linesToAdd = new ArrayList<>();
                lines = getLine(currentTable, command, conditionW1, selectAllResponse, indexConditionW1);
                for(ArrayList<String> allLine: allLines) {
                    for (int i = 0; i < lines.size(); i++) {
                        if (!allLine.contains(lines.get(i))) {
                            linesToAdd.add(lines.get(i));
                        }
                    }
                }
                allLines.add(linesToAdd);
                ArrayList<String> newOrderedList = maintainTableOrder(allLines, selectAllResponse);
                newOrderedList.add(0, currentTable.get(0));
                allLines.clear();
                allLines.add(newOrderedList);
            }
            if(conditionW1.equalsIgnoreCase("AND")){
                if(command.get(indexConditionW1 + 1).equals("(")){
                    conditionW1 = command.get(indexConditionW1 + 2);
                    indexConditionW1 += 2;
                }
                else{
                    conditionW1 = command.get(indexConditionW1 + 1);
                    indexConditionW1++;
                }
                ArrayList<String> allLines2 = new ArrayList<>();
                allLines2.add(currentTable.get(0));
                for(ArrayList<String> line: allLines) {
                    allLines2.addAll(getLine(line, command, conditionW1, selectAllResponse, indexConditionW1));
                }
                allLines.clear();
                allLines.add(allLines2);
            }
            conditionW1 = command.get(indexConditionW1 + 1);
            indexConditionW1++;
        }
        ArrayList<String> finalLines = new ArrayList<>();
        for(ArrayList<String> line: allLines){
            finalLines.addAll(line);
        }
        return finalLines;
    }

    public ArrayList<String> maintainTableOrder(ArrayList<ArrayList<String>> currentLines, ArrayList<String> allLines){
        ArrayList<String> orderedList = new ArrayList<>();
        for(int i = 1; i < allLines.size(); i++){
            for(ArrayList<String> lines: currentLines){
                for(String line: lines){
                    String[] words = allLines.get(i).split("[\t\n]+");
                    String[] currentWords = line.split("[\t\n]+");
                    if(words[0].equals(currentWords[0])) orderedList.add(line);
                }
            }
        }
        return orderedList;
    }

    public ArrayList<String> removeIdIfNeeded(ArrayList<String> list, ArrayList<String> command){
        boolean removeId = true;
        for(int i = indexOfIgnoreCase(command, "select"); i < indexOfIgnoreCase(command, "from"); i++){
            if(command.get(i).equalsIgnoreCase("id")) removeId = false;
        }
        if(!removeId) return list;
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String[] splitList = list.get(i).split("[\t\n]+");
            String toAdd = "";
            for(int j = 1; j < splitList.length; j++){
                toAdd += splitList[j] + "\t";
            }
            newList.add(toAdd + "\n");
        }
        return newList;
    }

    public void updateFile(ArrayList<String> command, String filePath, DBTreeNode tableNode, 
                           ArrayList<String> linesToUpdate, ArrayList<String> wholeTable) {
        boolean append = false;
            for(String line : wholeTable){
                if(line == null) break;
                boolean isLineToUpdate = false;
                for(String lineToUpdate: linesToUpdate){
                    if(line == null || lineToUpdate == null) break;
                    if(line.equals(lineToUpdate)){
                        isLineToUpdate = true;
                    }
                }
                if(!isLineToUpdate){
                     writeToFile(line + "\n", filePath, append);
                     append = true;
                }
                else{
                    int index = command.indexOf(command.get(3));
                    while(index < indexOfIgnoreCase(command, "WHERE") ) {
                        String updatedValue = command.get(index+2);
                        int valueToUpdate = tableNode.getChildNumber(tableNode, searchForNode(tableNode, command.get(index)));
                        String[] lineSplit = line.split("[\t\n]+");
                        lineSplit[valueToUpdate] = updatedValue;
                        for(int i = 0; i < lineSplit.length; i++) {
                            writeToFile(lineSplit[i] + "\t", filePath, true);
                        }
                        writeToFile("\n", filePath, true);
                        index += 3;
                    }
                }
            }
    }

    public void removeLineFromFile(String tableName, String filePath, ArrayList<String> linesToDelete, ArrayList<String> fullTable) {
        boolean append = false;
        for(String line: fullTable){
            for(String linetoDelete: linesToDelete){
                if(line == null || linetoDelete == null) break;
                if(!line.contains(linetoDelete)){
                    writeToFile(line + "\n", filePath, append);
                    append = true;
                }
            }
        }
    }

    public boolean fulfillsCondition(String comparator, String valueToCheck, String valueInCommand){
        if(comparator.equals("==")){
            if(valueToCheck.equalsIgnoreCase(valueInCommand)) return true;
        }
        try {
            if (comparator.equals(">")) {
                if (Integer.parseInt(valueToCheck) > Integer.parseInt(valueInCommand)) return true;
            }
            if (comparator.equals("<")) {
                if (Integer.parseInt(valueToCheck) < Integer.parseInt(valueInCommand)) return true;
            }
            if (comparator.equals(">=")) {
                if (Integer.parseInt(valueToCheck) >= Integer.parseInt(valueInCommand)) return true;
            }
            if (comparator.equals("<=")) {
                if (Integer.parseInt(valueToCheck) <= Integer.parseInt(valueInCommand)) return true;
            }
            if (comparator.equals("!=")) {
                if (Integer.parseInt(valueToCheck) != Integer.parseInt(valueInCommand)) return true;
            }
        }catch (Exception e){
            return false;
        }
        if(comparator.equalsIgnoreCase("LIKE")){
            if(valueToCheck.contains(valueInCommand)) return true;
        }
        return false;
    }

    public void addAttributeToFile(String attributeName, String filePath, DBTreeNode tableNode) {
        // select all from table
        ArrayList<String> currentFile = readAllFromFile(filePath);
        // rewrite to file line by line + add new attribute to first line
        String firstLine = currentFile.get(0).replace("\n", "");
        writeToFile(firstLine + attributeName + "\t\n", filePath, false);
        for(int i = 1; i < currentFile.size(); i++) {
            if(currentFile.get(i) == null) break;
            writeToFile(currentFile.get(i), filePath, true);
        }
    }

    public void removeAttributeFromFile(String attributeName, String filePath, DBTreeNode tableNode) {
        ArrayList<String> currentFile = readAllFromFile(filePath);
        String[] line = currentFile.get(0).split("((?=\t)|(?<=\t))");
        int attributeIndex = 0;
        for(int i = 0; i < line.length; i++){
            if(line[i].equals(attributeName)){
                attributeIndex = i;
            }
        }
        boolean append = false;
        for(int i = 0; i < currentFile.size(); i++){
            if(currentFile.get(i) == null) break;
            String[] nextLine = currentFile.get(i).split("((?=\t)|(?<=\t))");
            ArrayList<String> nextLineList = new ArrayList<>(Arrays.asList(nextLine));
            nextLineList.remove(attributeIndex);
            String finalString = String.join("", nextLineList);
            writeToFile(finalString + "\n", filePath, append);
            append = true;
        }
    }

    public DBTreeNode searchForNode(DBTreeNode root, String data){
        if(root.getData().equalsIgnoreCase(data)){
            return root;
        }
        for(int i = 0; i < root.getNext().size(); i++){
            DBTreeNode node = searchForNode(root.getNext().get(i), data);
            if(node != null){
                return node;
           }
        }
        return null;
    }

    public void deleteFolder(File file){
        for(File f : file.listFiles()){
            if(f.isDirectory()){
                deleteFolder(f);
            }
            f.delete();
        }
    }

}