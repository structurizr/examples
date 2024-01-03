package org.example;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.util.WorkspaceUtils;
import org.example.backstage.Entity;

import java.io.File;
import java.io.IOException;

public class Example2 extends AbstractBackstageExample {

    public static void main(String[] args) throws Exception {
        startOnPremisesInstallation();

        // load entities from demo.backstage.io
        Entity[] entities = getEntitiesFromBackstage();

        // create a system landscape workspace from the systems in Backstage, and export it to a JSON file
        Workspace systemLandscapeWorkspace = createSystemLandscapeWorkspace(entities);
        WorkspaceUtils.saveWorkspaceToJson(systemLandscapeWorkspace, new File("src/main/resources/example2/system-catalog.json"));

        // an example to show how a JSON file can be extended via the DSL
        StructurizrDslParser dslParser = new StructurizrDslParser();
        dslParser.parse(new File("src/main/resources/example2/podcast/workspace.dsl"));
        Workspace podcastWorkspace = dslParser.getWorkspace();
        WorkspaceMetadata podcastWorkspaceMetadata = createAdminApiClient().createWorkspace();
        createWorkspaceApiClient(podcastWorkspaceMetadata).putWorkspace(podcastWorkspaceMetadata.getId(), podcastWorkspace);

        enrichSystemLandscape(systemLandscapeWorkspace);

        System.out.println("Structurizr on-premises installation: " + STRUCTURIZR_ONPREMISES_URL);
        System.out.println("System landscape workspace: " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");

        try {
            Runtime.getRuntime().exec("open " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");
        } catch (IOException e) {
            // ignore
        }
    }

}