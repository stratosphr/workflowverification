package tools;

import java.io.File;

public class Settings {

    public enum VerifierType {

        SICSTUS, Z3;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

    }

    public static File getImplementationsFolder(VerifierType verifierType) {
        String subFolder = verifierType.toString();
        return new File("src/main/java/codegeneration/implementations/" + subFolder);
    }

}
