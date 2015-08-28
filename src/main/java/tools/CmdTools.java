package tools;

import exceptions.UnableToExecuteCommandException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CmdTools {

    private static Process process;
    private static BufferedReader reader;

    public static ArrayList<String> executeCommand(String command) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            process.waitFor();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }
            process.destroy();
            return lines;
        } catch (Exception e) {
            throw new UnableToExecuteCommandException(e.getMessage());
        }
    }

}
