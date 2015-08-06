package codegeneration.writers;

import codegeneration.implementations.ImplementationFactory;
import codegeneration.implementations.sicstus.SicstusImplementation;
import mvc.model.ConfigurationModel;
import petrinets.model.Workflow;

public class SicstusCodeWriter extends CodeWriter {

    public SicstusCodeWriter(ConfigurationModel configurationModel) {
        super(configurationModel);
    }

    @Override
    public void writeStateEquation() {
        SicstusImplementation sicstusImplementation = ImplementationFactory.getImplementation(configurationModel.getSicstusImplementation(), (Workflow) configurationModel.getVerificationFolder().getPetriNetFile().extractPetriNet(), configurationModel.getSpecificationFile().extractSpecification());
        super.write(sicstusImplementation.getStateEquation());
    }

}
