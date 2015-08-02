package files;

import petrinets.model.PetriNet;

import java.io.File;

public class PetriNetFile extends File {

    public PetriNetFile(String path) {
        super(path);
    }

    public PetriNet extractPetriNet() {
        return new PetriNet();
    }

    public boolean isValid() {
        return getAbsolutePath().endsWith(".xml");
    }

}
