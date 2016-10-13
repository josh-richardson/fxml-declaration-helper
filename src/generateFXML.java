import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Joshua on 17/06/2015.
 * Edited by Tom on 13/10/2016
 */
public class generateFXML extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        presentation.setVisible(file != null && file.getExtension() != null && file.getExtension().equals("fxml"));
    }


    public void actionPerformed(AnActionEvent e) {
        VirtualFile virtualFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (virtualFile == null) {
            return;
        }
        final String contents;
        try {
            BufferedReader br = new BufferedReader(new FileReader(virtualFile.getPath()));
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }
            contents = stringBuilder.toString();
        } catch (IOException e1) {
            return;
        }

        HashMap<String, ArrayList<String>> toCopy = new HashMap<String, ArrayList<String>>();
        String newLine = System.getProperty("line.separator");

        for (String ln: contents.split("<")) {
            if (ln.contains("fx:id")) {
                int startIndex = ln.indexOf("fx:id=") + 7;
                String type = ln.substring(0, ln.indexOf(" ")).replace(newLine, "");
                String name = ln.substring(startIndex, ln.indexOf(" ", startIndex) - 1).replaceAll("\\p{Punct}", "");
                ArrayList<String> names;

                //If an entry already exists for this type
                if((names = toCopy.get(type)) != null){
                    //Add another variable of this type.
                    names.add(name);
                    System.out.println("ADDED " + name);
                }else{
                    //Add a new entry for this type
                    ArrayList<String> newList = new ArrayList();
                    newList.add(name);
                    System.out.println("CREATED " + type);
                    toCopy.put(type, newList);
                }
            }
        }

        StringBuilder output = new StringBuilder();

        //Build an output string from the hashmap
        for(String type: toCopy.keySet()){

            output.append("@FXML private ").append(type);
            System.out.println("NEW VAR");
            for(String name: toCopy.get(type)){
                output.append(" ").append(name).append(",");
            }

            output.replace(output.length()-1,output.length(), ";"+newLine);
        }

        StringSelection selection = new StringSelection(output.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}



