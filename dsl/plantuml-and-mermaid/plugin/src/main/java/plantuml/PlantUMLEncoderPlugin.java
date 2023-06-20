package plantuml;

import com.structurizr.Workspace;
import com.structurizr.documentation.Decision;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.Section;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.SoftwareSystem;

public class PlantUMLEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String PLANTUML_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        try {
            Workspace workspace = context.getWorkspace();
            for (Section section : workspace.getDocumentation().getSections()) {
                section.setContent(encodePlantUML(context, section.getContent(), section.getFormat()));
            }

            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                for (Section section : softwareSystem.getDocumentation().getSections()) {
                    section.setContent(encodePlantUML(context, section.getContent(), section.getFormat()));
                }
            }

            for (Decision decision : workspace.getDocumentation().getDecisions()) {
                decision.setContent(encodePlantUML(context, decision.getContent(), decision.getFormat()));
            }

            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                for (Decision decision : softwareSystem.getDocumentation().getDecisions()) {
                    decision.setContent(encodePlantUML(context, decision.getContent(), decision.getFormat()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encodePlantUML(StructurizrDslPluginContext context, String content, Format format) throws Exception {
        String url = context.getParameter("plantuml.url", "https://www.plantuml.com/plantuml");

        StringBuilder buf = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        StringBuilder rawPlantUML = null;
        for (String line : lines) {
            line = line.trim();

            if (line.equals("```plantuml")) {
                rawPlantUML = new StringBuilder();
            } else if (rawPlantUML != null && line.equals("```")) {
                String encodedPlantUML = new PlantUMLEncoder().encode(rawPlantUML.toString());

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