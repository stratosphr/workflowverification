package files;

import petrinets.PIPEParser;
import petrinets.model.PetriNet;

import java.io.File;

public class PetriNetFile extends File {

    public PetriNetFile(String path) {
        super(path);
    }

    public PetriNet extractPetriNet() {
        return PIPEParser.parse(this);
    }

    public boolean isValid() {
        System.out.println(getName());
        System.out.println(getParentFile().getName());
        boolean ret = isFile() && getName().equals(getParentFile().getName() + ".xml") && getAbsolutePath().endsWith(".xml");
        System.out.println(ret);
        return ret;
    }

}
