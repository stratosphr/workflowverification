package files;

import exceptions.InvalidSpecificationException;
import exceptions.InvalidSpecificationFolderException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SpecificationFolder extends File {

    private HashSet<SpecificationFile> specificationFiles;

    public SpecificationFolder(String path) {
        super(path);
        specificationFiles = new HashSet<>();
        if (!isValid()) {
            throw new InvalidSpecificationFolderException();
        }
    }

    public boolean isValid() {
        File[] specifications = listFiles();
        if (specifications == null || specifications.length == 0) {
            return false;
        } else {
            for (File specification : specifications) {
                SpecificationFile specificationFile = new SpecificationFile(specification.getAbsolutePath());
                if (!specificationFile.isValid()) {
                    return false;
                } else {
                    specificationFiles.add(specificationFile);
                }
            }
        }
        return "specifications".equals(getName());
    }

    public ArrayList<SpecificationFile> getSpecificationFiles() {
        /*String[] specificationFileNames = new String[specificationFiles.size()];
        int i = 0;
        for (SpecificationFile specificationFile : this.specificationFiles) {
            specificationFileNames[i] = specificationFile.getName();
            ++i;
        }
        for (String specificationFileName : specificationFileNames) {
            System.out.println(specificationFileName);
        }
        Arrays.sort(specificationFileNames);
        for (String specificationFileName : specificationFileNames) {
            System.out.println(specificationFileName);
        }*/
        SpecificationFile[] specificationFiles = this.specificationFiles.toArray(new SpecificationFile[this.specificationFiles.size()]);
        Arrays.sort(specificationFiles);
        return new ArrayList<>(Arrays.asList(specificationFiles));
    }

}
