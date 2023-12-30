package org.example;

import com.structurizr.Workspace;
import com.structurizr.api.AdminApiClient;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.configuration.WorkspaceScope;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.model.*;
import com.structurizr.view.SystemLandscapeView;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {

    private static String STRUCTURIZR_ONPREMISES_URL = "";
    private static final String ADMIN_API_KEY_PLAINTEXT = "password";
    private static final String ADMIN_API_KEY_BCRYPT = "$2a$10$ekjju1h3fC1y2YAln7wqxuJ.q0gBjQoFPX/Wvmzr.L5aIdoqvUIwa";
    private static WorkspaceMetadata SYSTEM_LANDSCAPE_WORKSPACE_METADATA;

    public static void main(String[] args) throws Exception {
        startOnPremisesInstallation();
        loadExampleWorkspaces();
        generateSystemLandscapeWorkspace();

        System.out.println("Structurizr on-premises installation: " + STRUCTURIZR_ONPREMISES_URL);
        System.out.println("System landscape workspace: " + STRUCTURIZR_ONPREMISES_URL + "/share/1/diagrams#Landscape");
    }

    private static void startOnPremisesInstallation() throws Exception {
        // this method starts the Docker version of the Structurizr on-premises installation:
        // - via Testcontainers
        // - volume mounted to a temporary directory
        // - with the "admin API key" enabled

        File structurizrDataDirectory = Files.createTempDirectory("structurizr").toFile();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(structurizrDataDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println(structurizrDataDirectory.getAbsolutePath());

        Files.writeString(new File(structurizrDataDirectory, "structurizr.properties").toPath(), "structurizr.apiKey=" + ADMIN_API_KEY_BCRYPT);

        GenericContainer container = new GenericContainer(DockerImageName.parse("structurizr/onpremises:preview"))
                .withExposedPorts(8080)
                .withFileSystemBind(structurizrDataDirectory.getAbsolutePath(), "/usr/local/structurizr", BindMode.READ_WRITE)
                .waitingFor(Wait.forHttp("/"));

        container.start();

        Logger logger = LoggerFactory.getLogger(Main.class);
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);

        // just a thread to keep the JVM alive
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
        thread.setDaemon(false);
        thread.start();

        int port = container.getMappedPort(8080);
        STRUCTURIZR_ONPREMISES_URL = "http://localhost:" + port;
    }

    private static void loadExampleWorkspaces() throws Exception {
        // create workspace 1 (landscape ... this is empty for now)
        SYSTEM_LANDSCAPE_WORKSPACE_METADATA = createAdminApiClient().createWorkspace();

        // create workspace 2 (customer service)
        WorkspaceMetadata workspaceMetadata1 = createAdminApiClient().createWorkspace();
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/dsl/customer-service/workspace.dsl"));
        Workspace workspace1 = parser.getWorkspace();
        WorkspaceApiClient workspaceApiClient = createWorkspaceApiClient(workspaceMetadata1.getApiKey(), workspaceMetadata1.getApiSecret());
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata1.getId(), workspace1);

        // create workspace 3 (invoice service)
        WorkspaceMetadata workspaceMetadata2 = createAdminApiClient().createWorkspace();
        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/dsl/invoice-service/workspace.dsl"));
        Workspace workspace2 = parser.getWorkspace();
        workspaceApiClient = createWorkspaceApiClient(workspaceMetadata2.getApiKey(), workspaceMetadata2.getApiSecret());
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata2.getId(), workspace2);

        // create workspace 4 (order service)
        WorkspaceMetadata workspaceMetadata3 = createAdminApiClient().createWorkspace();
        parser = new StructurizrDslParser();
        parser.parse(new File("src/main/dsl/order-service/workspace.dsl"));
        Workspace workspace3 = parser.getWorkspace();
        workspaceApiClient = createWorkspaceApiClient(workspaceMetadata3.getApiKey(), workspaceMetadata3.getApiSecret());
        workspaceApiClient.setWorkspaceArchiveLocation(null);
        workspaceApiClient.putWorkspace(workspaceMetadata3.getId(), workspace3);
    }

    private static void generateSystemLandscapeWorkspace() throws Exception {
        // create a workspace based upon the system catalog ... this has people and software systems, but no relationships
        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(new File("src/main/dsl/system-catalog.dsl"));

        Workspace systemLandscapeWorkspace = parser.getWorkspace();
        systemLandscapeWorkspace.setName("Landscape");

        // now extract all relationships between people/software systems from all software system scoped workspaces
        // so they can be added to the system landscape workspace
        List<WorkspaceMetadata> workspaces = createAdminApiClient().getWorkspaces();
        for (WorkspaceMetadata workspaceMetadata : workspaces) {
            WorkspaceApiClient workspaceApiClient = createWorkspaceApiClient(workspaceMetadata.getApiKey(), workspaceMetadata.getApiSecret());
            workspaceApiClient.setWorkspaceArchiveLocation(null);
            Workspace workspace = workspaceApiClient.getWorkspace(workspaceMetadata.getId());
            if (workspace.getConfiguration().getScope() == WorkspaceScope.SoftwareSystem) {
                SoftwareSystem softwareSystem = findScopedSoftwareSystem(workspace);
                if (softwareSystem != null) {
                    systemLandscapeWorkspace.getModel().getSoftwareSystemWithName(softwareSystem.getName()).setUrl("{workspace:" + workspaceMetadata.getId() + "}/diagrams#SystemContext");
                }

                findAndCloneRelationships(workspace, systemLandscapeWorkspace);
            }
        }

        // create a system landscape view
        SystemLandscapeView view = systemLandscapeWorkspace.getViews().createSystemLandscapeView("Landscape", "An automatically generated system landscape view.");
        view.addAllElements();
        view.enableAutomaticLayout();

        // and push the landscape workspace to the on-premises installation
        WorkspaceApiClient workspaceApiClient = createWorkspaceApiClient(SYSTEM_LANDSCAPE_WORKSPACE_METADATA.getApiKey(), SYSTEM_LANDSCAPE_WORKSPACE_METADATA.getApiSecret());
        workspaceApiClient.putWorkspace(SYSTEM_LANDSCAPE_WORKSPACE_METADATA.getId(), systemLandscapeWorkspace);
    }

    private static AdminApiClient createAdminApiClient() {
        return new AdminApiClient(STRUCTURIZR_ONPREMISES_URL + "/api", null, ADMIN_API_KEY_PLAINTEXT);
    }

    private static WorkspaceApiClient createWorkspaceApiClient(String apiKey, String apiSecret) {
        WorkspaceApiClient workspaceApiClient = new WorkspaceApiClient(STRUCTURIZR_ONPREMISES_URL + "/api", apiKey, apiSecret);
        workspaceApiClient.setWorkspaceArchiveLocation(null); // this prevents the local file system from being cluttered with JSON files

        return workspaceApiClient;
    }

    private static SoftwareSystem findScopedSoftwareSystem(Workspace workspace) {
        return workspace.getModel().getSoftwareSystems().stream().filter(ss -> !ss.getContainers().isEmpty()).findFirst().orElse(null);
    }

    private static void findAndCloneRelationships(Workspace source, Workspace destination) {
        for (Relationship relationship : source.getModel().getRelationships()) {
            if (isPersonOrSoftwareSystem(relationship.getSource()) && isPersonOrSoftwareSystem(relationship.getDestination())) {
                cloneRelationshipIfItDoesNotExist(relationship, destination.getModel());
            }
        }
    }

    private static boolean isPersonOrSoftwareSystem(Element element) {
        return element instanceof Person || element instanceof SoftwareSystem;
    }

    private static void cloneRelationshipIfItDoesNotExist(Relationship relationship, Model model) {
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