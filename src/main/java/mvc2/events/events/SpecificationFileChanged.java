package mvc2.events.events;

import files.SpecificationFile;
import mvc2.models.ConfigurationModel;

public class SpecificationFileChanged extends AbstractEvent {

    private final SpecificationFile specificationFile;

    public SpecificationFileChanged(ConfigurationModel source, SpecificationFile specificationFile){
        super(source);
        this.specificationFile = specificationFile;
    }

    public SpecificationFile getSpecificationFile() {
        return specificationFile;
    }

}
