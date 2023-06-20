package mermaid;

import com.structurizr.Workspace;
import com.structurizr.documentation.Decision;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.Section;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.SoftwareSystem;

public class MermaidEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String MERMAID_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        try {
            Workspace workspace = context.getWorkspace();
            for (Section section : workspace.getDocumentation().getSections()) {
                section.setContent(encodeMermaid(context, section.getContent(), section.getFormat()));
            }

            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                for (Section section : softwareSystem.getDocumentation().getSections()) {
                    section.setContent(encodeMermaid(context, section.getContent(), section.getFormat()));
                }
            }

            for (Decision decision : workspace.getDocumentation().getDecisions()) {
                decision.setContent(encodeMermaid(context, decision.getContent(), decision.getFormat()));
            }

            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                for (Decision decision : softwareSystem.getDocumentation().getDecisions()) {
                    decision.setContent(encodeMermaid(context, decision.getContent(), decision.getFormat()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encodeMermaid(StructurizrDslPluginContext context, String content, Format format) throws Exception {
        String url = context.getParameter("mermaid.url", "https://mermaid.ink");

        StringBuilder buf = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        StringBuilder rawMermaid = null;
        for (String line : lines) {
            line = line.trim();

            if (line.equals("```mermaid")) {
                rawMermaid = new StringBuilder();
            } else if (rawMermaid != null && line.equals("```")) {
                String encodedMermaid = new MermaidEncoder().encode(rawMermaid.toString());

                if (format == Format.AsciiDoc) {
                    buf.append(String.format(ASCIIDOC_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid));
                } else {
                    buf.append(String.format(MARKDOWN_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid));
                }

                buf.append(System.lineSeparator());
                rawMermaid = null;
            } else if (rawMermaid != null) {
                rawMermaid.append(line);
                rawMermaid.append(System.lineSeparator());
            } else {
                buf.append(line);
                buf.append(System.lineSeparator());
            }
        }

        return buf.toString();
    }

}