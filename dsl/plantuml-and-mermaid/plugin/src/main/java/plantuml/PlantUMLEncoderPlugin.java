package plantuml;

import com.structurizr.Workspace;
import com.structurizr.documentation.*;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;

import java.util.Set;
import java.util.stream.Collectors;

public class PlantUMLEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String PLANTUML_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        try {
            Workspace workspace = context.getWorkspace();

            Set<Documentable> documentables = workspace.getModel().getElements().stream().filter(e -> e instanceof Documentable).map(e -> (Documentable)e).collect(Collectors.toSet());
            documentables.add(workspace);
            documentables.forEach(e -> encodePlantUML(context, e));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encodePlantUML(StructurizrDslPluginContext context, Documentable documentable) {
        for (Section section : documentable.getDocumentation().getSections()) {
            section.setContent(encodePlantUML(context, section));
        }

        for (Decision decision : documentable.getDocumentation().getDecisions()) {
            decision.setContent(encodePlantUML(context, decision));
        }
    }

    private String encodePlantUML(StructurizrDslPluginContext context, DocumentationContent documentationContent) {
        String url = context.getParameter("plantuml.url", "https://www.plantuml.com/plantuml");

        String content = documentationContent.getContent();
        Format format = documentationContent.getFormat();

        StringBuilder buf = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        StringBuilder rawPlantUML = null;
        for (String line : lines) {
            line = line.trim();

            if (line.equals("```plantuml")) {
                rawPlantUML = new StringBuilder();
            } else if (rawPlantUML != null && line.equals("```")) {
                String encodedPlantUML = rawPlantUML.toString();

                try {
                    encodedPlantUML = new PlantUMLEncoder().encode(rawPlantUML.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (format == Format.AsciiDoc) {
                    buf.append(String.format(ASCIIDOC_IMAGE_TEMPLATE, url, PLANTUML_FORMAT, encodedPlantUML));
                } else {
                    buf.append(String.format(MARKDOWN_IMAGE_TEMPLATE, url, PLANTUML_FORMAT, encodedPlantUML));
                }

                buf.append(System.lineSeparator());
                rawPlantUML = null;
            } else if (rawPlantUML != null) {
                rawPlantUML.append(line);
                rawPlantUML.append(System.lineSeparator());
            } else {
                buf.append(line);
                buf.append(System.lineSeparator());
            }
        }

        return buf.toString();
    }

}