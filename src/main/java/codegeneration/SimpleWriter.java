package codegeneration;

import exceptions.AttemptToWriteNullPointerToFileException;
import exceptions.UnableToCreateSubFoldersForFileWritingException;

import java.io.*;

public class SimpleWriter {

    private File outputFile;

    public SimpleWriter(File outputFile) {
        this.outputFile = outputFile;
    }

    protected void write(Object... objects) {
        if (objects == null) {
            try {
                throw new AttemptToWriteNullPointerToFileException();
            } catch (AttemptToWriteNullPointerToFileException e) {
                e.printStackTrace();
            }
        } else {
            if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
                throw new UnableToCreateSubFoldersForFileWritingException();
            }
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getAbsolutePath()), "utf-8"))) {
                for (Object object : objects) {
                    if (object == null) {
                        throw new AttemptToWriteNullPointerToFileException();
                    } else {
                        writer.write(object.toString());
                    }
                }
            } catch (IOException | AttemptToWriteNullPointerToFileException e) {
                e.printStackTrace();
            }
        }
    }

}
