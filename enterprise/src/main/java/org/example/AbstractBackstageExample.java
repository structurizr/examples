package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.Workspace;
import com.structurizr.configuration.WorkspaceScope;
import com.structurizr.model.SoftwareSystem;
import org.example.backstage.Entity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class AbstractBackstageExample extends AbstractExample {

    protected static final String BACKSTAGE_ENTITY_KIND_SYSTEM = "System";
    protected static final String BACKSTAGE_ENTITY_KIND_COMPONENT = "Component";
    protected static final String BACKSTAGE_ENTITY_KIND_RESOURCE = "Resource";
    protected static final String BACKSTAGE_RELATION_TYPE_DEPENDS_ON = "dependsOn";
    protected static final String BACKSTAGE_RELATION_TYPE_CONSUMES_API = "consumesApi";
    protected static final String STRUCTURIZR_DSL_IDENTIFIER_PROPERTY_NAME = "structurizr.dsl.identifier";
    protected static final String BACKSTAGE_REF_PROPERTY_NAME = "backstage.ref";
    protected static final String OWNER_PERSPECTIVE_NAME = "Owner";

    protected static Workspace createSystemLandscapeWorkspace(Entity[] entities) {
        // create a landscape workspace (this ignores namespaces and assumes that all names are unique)
        Workspace workspace = new Workspace("Landscape", "The system landscape, imported from the Backstage demo at https://demo.backstage.io");
        workspace.getConfiguration().setScope(WorkspaceScope.Landscape);

        for (Entity entity : entities) {
            if (BACKSTAGE_ENTITY_KIND_SYSTEM.equals(entity.kind)) {
                String name = entity.metadata.name;
                String description = entity.metadata.description;
                String dslIdentifier = entity.metadata.name.replaceAll("\\W", "");

                SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem(name);
                softwareSystem.setDescription(description);
                softwareSystem.addProperty(STRUCTURIZR_DSL_IDENTIFIER_PROPERTY_NAME, dslIdentifier);
                softwareSystem.addProperty(BACKSTAGE_REF_PROPERTY_NAME, toBackstageRef(entity));
                softwareSystem.addPerspective(OWNER_PERSPECTIVE_NAME, entity.spec.owner);
                softwareSystem.setGroup("Domain: " + entity.spec.domain);
                softwareSystem.addTags(entity.metadata.tags);
            }
        }

        return workspace;
    }

    protected static Entity[] getEntitiesFromBackstage() throws Exception {
        String json = "";
        try {
            // loads the data from the Backstage demo instance
            String url = "https://demo.backstage.io/api/catalog/entities";
            HttpRequest request = HttpRequest.newBuilder(new URI(url)).build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            json = response.body();
        } catch (Exception e) {
            // if this doesn't work, we'll use a snapshot of the demo data instead
            json = Files.readString(new File("src/main/resources/backstage-demo.json").toPath());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(json, Entity[].class);
    }

    protected static String toBackstageRef(Entity entity) {
        return entity.kind.toLowerCase() + ":" + entity.metadata.namespace + "/" + entity.metadata.name;
    }

}