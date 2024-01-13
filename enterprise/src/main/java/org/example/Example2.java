package org.example;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.configuration.WorkspaceScope;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.SystemLandscapeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Example2 extends AbstractExample {

    private final static Collection<Workspace> workspaces = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        loadExampleWorkspaces();
        generateSystemLandscapeWorkspace();
    }

    private static void loadExampleWorkspaces() throws Exception {
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/customer-service/workspace.dsl"));
        workspaces.add(parser.getWorkspace());

        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/invoice-service/workspace.dsl"));
        workspaces.add(parser.getWorkspace());

        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/order-service/workspace.dsl"));
        workspaces.add(parser.getWorkspace());
    }

    private static void generateSystemLandscapeWorkspace() throws Exception {
        // create a workspace based upon the system catalog ... this has people and software systems, but no relationships
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/system-catalog.dsl"));

        Workspace systemLandscapeWorkspace = parser.getWorkspace();
        systemLandscapeWorkspace.setName("Landscape");

        // extract all relationships between people/software systems from all software system scoped workspaces
        // so they can be added to the system landscape workspace
        for (Workspace workspace : workspaces) {
            if (workspace.getConfiguration().getScope() == WorkspaceScope.SoftwareSystem) {
                findAndCloneRelationships(workspace, systemLandscapeWorkspace);
            }
        }

        // create a system landscape view
        SystemLandscapeView view = systemLandscapeWorkspace.getViews().createSystemLandscapeView("Landscape", "An automatically generated system landscape view.");
        view.addAllElements();
        view.enableAutomaticLayout();

        // and save the resulting workspace as a JSON file, for use with other tools
        WorkspaceUtils.saveWorkspaceToJson(systemLandscapeWorkspace, new File("src/main/resources/example2/workspace.json"));
    }


}