package org.example;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.configuration.WorkspaceScope;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import org.example.backstage.Entity;
import org.example.backstage.Relation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Example4 extends AbstractBackstageExample {

    public static void main(String[] args) throws Exception {
        startOnPremisesInstallation();

        // load entities from demo.backstage.io
        Entity[] entities = getEntitiesFromBackstage();

        // create a system landscape workspace from the systems in Backstage
        Workspace systemLandscapeWorkspace = createSystemLandscapeWorkspace(entities);

        // now create a workspace per software system for the detail (i.e. containers ... aka Backstage "components" and "resources")
        // these workspaces will "extend" the landscape workspace so that system-system links can be added where necessary
        // (although there are none of these in the demo data)
        createSoftwareSystemWorkspaces(systemLandscapeWorkspace, entities);

        enrichSystemLandscape(systemLandscapeWorkspace);

        System.out.println("Structurizr on-premises installation: " + STRUCTURIZR_ONPREMISES_URL);
        System.out.println("System landscape workspace: " + STRUCTURIZR_ONPREMISES_URL + "/share/" + SYSTEM_LANDSCAPE_WORKSPACE_METADATA.getId() + "/diagrams#Landscape");

        try {
            Runtime.getRuntime().exec("open " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");
        } catch (IOException e) {
            // ignore
        }
    }

    private static void createSoftwareSystemWorkspaces(Workspace systemLandscapeWorkspace, Entity[] entities) throws Exception {
        Map<String,Workspace> workspacesByName = new HashMap<>();
        Map<String, WorkspaceMetadata> workspaceMetadataByName = new HashMap<>();

        for (Entity entity : entities) {
            if (BACKSTAGE_ENTITY_KIND_SYSTEM.equals(entity.kind)) {
                String name = entity.metadata.name;
                String description = entity.metadata.description;

                Workspace workspace = workspacesByName.get(name);
                WorkspaceMetadata workspaceMetadata = workspaceMetadataByName.get(name);
                if (workspace == null) {
                    workspace = WorkspaceUtils.fromJson(WorkspaceUtils.toJson(systemLandscapeWorkspace, false)); // clones the workspace
                    workspace.setName(name);
                    workspace.setDescription(description);
                    workspace.getConfiguration().setScope(WorkspaceScope.SoftwareSystem);
                    workspacesByName.put(name, workspace);

                    workspaceMetadata = createAdminApiClient().createWorkspace();
                    workspaceMetadataByName.put(name, workspaceMetadata);
                }
            }
        }

        // find components
        for (Entity entity : entities) {
            if (BACKSTAGE_ENTITY_KIND_COMPONENT.equals(entity.kind) || BACKSTAGE_ENTITY_KIND_RESOURCE.equals(entity.kind)) {
                if (!StringUtils.isNullOrEmpty(entity.spec.system)) {
                    String softwareSystemName = entity.spec.system;
                    Workspace workspace = workspacesByName.get(softwareSystemName);
                    SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName(softwareSystemName);
                    if (softwareSystem != null) {
                        String dslIdentifier = softwareSystem.getProperties().get(STRUCTURIZR_DSL_IDENTIFIER_PROPERTY_NAME) + "." + entity.metadata.name.replaceAll("\\W", "");
                        Container container = softwareSystem.addContainer(entity.metadata.name);
                        container.setDescription(entity.metadata.description);
                        container.addProperty(STRUCTURIZR_DSL_IDENTIFIER_PROPERTY_NAME, dslIdentifier);
                        container.addProperty(BACKSTAGE_REF_PROPERTY_NAME, toBackstageRef(entity));
                        container.addTags(entity.metadata.tags);
                    }
                }
            }
        }

        for (Workspace workspace : workspacesByName.values()) {
            // find relationships from containers
            for (Entity entity : entities) {
                if (BACKSTAGE_ENTITY_KIND_COMPONENT.equals(entity.kind)) {
                    for (Relation relation : entity.relations) {
                        if (BACKSTAGE_RELATION_TYPE_DEPENDS_ON.equals(relation.type) || BACKSTAGE_RELATION_TYPE_CONSUMES_API.equals(relation.type)) {
                            String sourceRef = toBackstageRef(entity);
                            String targetRef = relation.targetRef;
                            Container source = (Container) workspace.getModel().getElements().stream().filter(e -> e instanceof Container && sourceRef.equals(e.getProperties().get(BACKSTAGE_REF_PROPERTY_NAME))).findFirst().orElse(null);
                            Element destination = workspace.getModel().getElements().stream().filter(e -> targetRef.equals(e.getProperties().get(BACKSTAGE_REF_PROPERTY_NAME))).findFirst().orElse(null);

                            if (source != null && destination != null) {
                                if (destination instanceof SoftwareSystem) {
                                    source.uses((SoftwareSystem) destination, relation.type);
                                } else {
                                    source.uses((Container) destination, relation.type);
                                }
                            }
                        }
                    }
                }
            }

            // find relationships from software systems
            for (Entity entity : entities) {
                if (BACKSTAGE_ENTITY_KIND_SYSTEM.equals(entity.kind)) {
                    for (Relation relation : entity.relations) {
                        if (BACKSTAGE_RELATION_TYPE_DEPENDS_ON.equals(relation.type) || BACKSTAGE_RELATION_TYPE_CONSUMES_API.equals(relation.type)) {
                            String sourceRef = toBackstageRef(entity);
                            String targetRef = relation.targetRef;
                            System.out.println(sourceRef + " -> " + targetRef);
                            SoftwareSystem source = (SoftwareSystem) workspace.getModel().getElements().stream().filter(e -> e instanceof SoftwareSystem && sourceRef.equals(e.getProperties().get(BACKSTAGE_REF_PROPERTY_NAME))).findFirst().orElse(null);
                            Element destination = workspace.getModel().getElements().stream().filter(e -> targetRef.equals(e.getProperties().get(BACKSTAGE_REF_PROPERTY_NAME))).findFirst().orElse(null);

                            if (source != null && destination != null) {
                                if (destination instanceof SoftwareSystem) {
                                    source.uses((SoftwareSystem) destination, relation.type);
                                } else {
                                    source.uses((Container) destination, relation.type);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String name : workspacesByName.keySet()) {
            Workspace workspace = workspacesByName.get(name);
            WorkspaceMetadata workspaceMetadata = workspaceMetadataByName.get(name);

            SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName(name);

            SystemContextView systemContextView = workspace.getViews().createSystemContextView(softwareSystem, "SystemContext", "");
            systemContextView.addDefaultElements();
            systemContextView.enableAutomaticLayout();

            ContainerView containerView = workspace.getViews().createContainerView(softwareSystem, "Containers", "");
            containerView.addDefaultElements();
            containerView.enableAutomaticLayout();

            workspace.trim();
            createWorkspaceApiClient(workspaceMetadata).putWorkspace(workspaceMetadata.getId(), workspace);
        }
    }

}