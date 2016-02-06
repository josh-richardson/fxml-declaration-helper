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

/**
 * Created by Joshua on 17/06/2015.
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

        String toCopy = "";
        String newLine = System.getProperty("line.separator");

        for (String ln: contents.split("<")) {
            if (ln.contains("fx:id")) {
                toCopy += "@FXML" + newLine + "private ";
                toCopy += ln.substring(0, ln.indexOf(" ")).replace(newLine, "") + " ";
                int startIndex = ln.indexOf("fx:id=") + 7;
                int endIndex = ln.indexOf(" ", startIndex) - 1;
                toCopy += ln.substring(startIndex, endIndex) + ";" + newLine;
            }
        }
        StringSelection selection = new StringSelection(toCopy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}



