package codegeneration.implementations.sicstus;

import java.util.ArrayList;
import java.util.Arrays;

public enum ESicstusImplementations {

    DEFAULT, ONE_TRANSITION_PER_SEGMENT;

    @Override
    public String toString() {
        String className = "";
        ArrayList<String> words = new ArrayList<>(Arrays.asList(super.toString().split("_")));
        for (String word : words) {
            className += word.substring(0, 1) + word.substring(1).toLowerCase();
        }
        return className;
    }

}
