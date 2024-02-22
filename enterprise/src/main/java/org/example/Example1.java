package org.example;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.dsl.StructurizrDslParser;

import java.io.File;
import java.io.IOException;

public class Example1 extends AbstractExample {

    public static void main(String[] args) throws Exception {
        startOnPremisesInstallation();
        loadExampleWorkspaces();
        generateSystemLandscapeWorkspace();

        System.out.println("Structurizr on-premises installation: " + STRUCTURIZR_ONPREMISES_URL);
        System.out.println("System landscape workspace: " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");

        try {
            Runtime.getRuntime().exec("open " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");
        } catch (IOException e) {
            // ignore
        }
    }

    private static void loadExampleWorkspaces() throws Exception {
        // create workspace 2 (customer service)
        WorkspaceMetadata workspaceMetadata1 = createAdminApiClient().createWorkspace();
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/customer-service/workspace.dsl"));
        Workspace workspace1 = parser.getWorkspace();
        workspace1.trim();
        WorkspaceApiClient workspaceApiClient = createWorkspaceApiClient(workspaceMetadata1);
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata1.getId(), workspace1);

        // create workspace 3 (invoice service)
        WorkspaceMetadata workspaceMetadata2 = createAdminApiClient().createWorkspace();
        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/invoice-service/workspace.dsl"));
        Workspace workspace2 = parser.getWorkspace();
        workspace2.trim();
        workspaceApiClient = createWorkspaceApiClient(workspaceMetadata2);
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata2.getId(), workspace2);

        // create workspace 4 (order service)
        WorkspaceMetadata workspaceMetadata3 = createAdminApiClient().createWorkspace();
        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/order-service/workspace.dsl"));
        Workspace workspace3 = parser.getWorkspace();
        workspace3.trim();
        workspaceApiClient = createWorkspaceApiClient(workspaceMetadata3);
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata3.getId(), workspace3);
    }

    private static void generateSystemLandscapeWorkspace() throws Exception {
        // create a workspace based upon the system catalog ... this has people and software systems, but no relationships
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/resources/example1/system-catalog.dsl"));

        Workspace systemLandscapeWorkspace = parser.getWorkspace();
        systemLandscapeWorkspace.setName("Landscape");
        enrichSystemLandscape(systemLandscapeWorkspace);
    }

}