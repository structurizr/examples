package mermaid;

import com.structurizr.Workspace;
import com.structurizr.documentation.*;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

import java.util.Set;
import java.util.stream.Collectors;

public class MermaidEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String MERMAID_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        try {
            Workspace workspace = context.getWorkspace();

            Set<Documentable> documentables = workspace.getModel().getElements().stream().filter(e -> e instanceof Documentable).map(e -> (Documentable)e).collect(Collectors.toSet());
            documentables.add(workspace);
            documentables.forEach(e -> encodeMermaid(context, e));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encodeMermaid(StructurizrDslPluginContext context, Documentable documentable) {
        for (Section section : documentable.getDocumentation().getSections()) {
            section.setContent(encodeMermaid(context, section));
        }

        for (Decision decision : documentable.getDocumentation().getDecisions()) {
            decision.setContent(encodeMermaid(context, decision));
        }
    }

    private String encodeMermaid(StructurizrDslPluginContext context, DocumentationContent documentationContent) {
        String url = context.getParameter("mermaid.url", "https://mermaid.ink");

        String content = documentationContent.getContent();
        Format format = documentationContent.getFormat();

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