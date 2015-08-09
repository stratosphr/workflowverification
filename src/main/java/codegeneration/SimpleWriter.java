package codegeneration;

import exceptions.AttemptToWriteNullPointerToFileException;
import exceptions.UnableToCreateSubFoldersForFileWritingException;

import java.io.*;

public class SimpleWriter {

    private File outputFile;

    public SimpleWriter(File outputFile) {
        this.outputFile = outputFile;
        empty();
    }

    protected void write(Object... objects) {
        if (objects == null) {
            try {
                throw new AttemptToWriteNullPointerToFileException();
            } catch (AttemptToWriteNullPointerToFileException e) {
                e.printStackTrace();
            }
        } else {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
                for (Object object : objects) {
                    if (object == null) {
                        throw new AttemptToWriteNullPointerToFileException();
                    } else if (object instanceof Object[]) {
                        for(Object subObject : (Object[]) object){
                            writer.write(subObject.toString() + "\n");
                        }
                    } else {
                        writer.write(object.toString() + "\n");
                    }
                }
                writer.write("\n");
            } catch (IOException | AttemptToWriteNullPointerToFileException e) {
                e.printStackTrace();
            }
        }

    }

    private void empty() {
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            throw new UnableToCreateSubFoldersForFileWritingException();
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)))) {
            // JUST EMPTY THE FILE
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
