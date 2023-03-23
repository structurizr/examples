import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * This example plugin loads a given workspace and clones top-level relationships into this workspace.
 *
 * Possible enhancements to this example include:
 *  - Include relationship technologies.
 *  - Recursively load all .dsl files in a given directory.
 */
public class FindRelationshipsPlugin implements StructurizrDslPlugin {

    private static final String FILENAME_PARAMETER_NAME = "filename";

    @Override
    public void run(StructurizrDslPluginContext context) {
        File directory = context.getDslFile().getParentFile();
        String filename = context.getParameter(FILENAME_PARAMETER_NAME);
        Set<Relationship> relationships = findRelationshipsInWorkspace(new File(directory, filename));

        for (Relationship relationship : relationships) {
            cloneRelationshipIfItDoesNotExist(relationship, context.getWorkspace().getModel());
        }
    }

    Set<Relationship> findRelationshipsInWorkspace(File file) {
        Set<Relationship> relationships = new HashSet<>();

        try {
            StructurizrDslParser dslParser = new StructurizrDslParser();
            dslParser.parse(file);

            Workspace workspace = dslParser.getWorkspace();
            for (Relationship relationship : workspace.getModel().getRelationships()) {
                if (isPersonOrSoftwareSystem(relationship.getSource()) && isPersonOrSoftwareSystem(relationship.getDestination())) {
                    relationships.add(relationship);
                }
            }
        } catch (StructurizrDslParserException e) {
            System.out.println(e);
        }

        return relationships;
    }

    private boolean isPersonOrSoftwareSystem(Element element) {
        return element instanceof Person || element instanceof SoftwareSystem;
    }

    private void cloneRelationshipIfItDoesNotExist(Relationship relationship, Model model) {
        Relationship clonedRelationship = null;

        if (relationship.getSource() instanceof SoftwareSystem && relationship.getDestination() instanceof SoftwareSystem) {
            SoftwareSystem source = model.getSoftwareSystemWithName(relationship.getSource().getName());
            SoftwareSystem destination = model.getSoftwareSystemWithName(relationship.getDestination().getName());

            if (source != null && destination != null && !source.hasEfferentRelationshipWith(destination)) {
                clonedRelationship = source.uses(destination, relationship.getDescription());
            }
        } else if (relationship.getSource() instanceof Person && relationship.getDestination() instanceof SoftwareSystem) {
            Person source = model.getPersonWithName(relationship.getSource().getName());
            SoftwareSystem destination = model.getSoftwareSystemWithName(relationship.getDestination().getName());

            if (source != null && destination != null && !source.hasEfferentRelationshipWith(destination)) {
                clonedRelationship = source.uses(destination, relationship.getDescription());
            }
        } else if (relationship.getSource() instanceof SoftwareSystem && relationship.getDestination() instanceof Person) {
            SoftwareSystem source = model.getSoftwareSystemWithName(relationship.getSource().getName());
            Person destination = model.getPersonWithName(relationship.getDestination().getName());

            if (source != null && destination != null && !source.hasEfferentRelationshipWith(destination)) {
                clonedRelationship = source.delivers(destination, relationship.getDescription());
            }
        } else if (relationship.getSource() instanceof Person && relationship.getDestination() instanceof Person) {
            Person source = model.getPersonWithName(relationship.getSource().getName());
            Person destination = model.getPersonWithName(relationship.getDestination().getName());

            if (source != null && destination != null && !source.hasEfferentRelationshipWith(destination)) {
                clonedRelationship = source.delivers(destination, relationship.getDescription());
            }
        }

        if (clonedRelationship != null) {
            clonedRelationship.addTags(relationship.getTags());
        }
    }

}
