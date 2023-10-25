package mermaid;

import com.structurizr.Workspace;
import com.structurizr.documentation.*;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

import java.util.Arrays;
import java.util.Iterator;
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
        Boolean digramStarts = false;
        Iterator<String> iterator = Arrays.asList(lines).iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.trim().equals("```mermaid")) {
                line = getDiagramFromMDSyntax(iterator, format, url);
            }
            if (line.trim().startsWith("[mermaid") && line.endsWith("]")) {
                line = getDiagramFromAdocSyntax(line, iterator, format, url);
            }
            buf.append(line);
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }

    private String getDiagramFromAdocSyntax(String startLine, Iterator<String> iterator, Format format, String url)
    {
        StringBuilder rawMermaid = new StringBuilder();
        String line = iterator.next();
        if (!line.trim().equals("....")) {
            return startLine + line;
        }
        while (iterator.hasNext()) {
            line = iterator.next();
            if (line.trim().equals("....")) {
                break;
            } else {
                rawMermaid.append(line);
                rawMermaid.append(System.lineSeparator());
            }
        }
        String encodedMermaid = new MermaidEncoder().encode(rawMermaid.toString());

        if (format == Format.AsciiDoc) {
            return String.format(ASCIIDOC_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid);
        }
        return String.format(MARKDOWN_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid);
    }

    private String getDiagramFromMDSyntax(Iterator<String> iterator, Format format, String url)
    {
        StringBuilder rawMermaid = new StringBuilder();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.trim().equals("```")) {
                break;
            } else {
                rawMermaid.append(line);
                rawMermaid.append(System.lineSeparator());
            }
        }
        String encodedMermaid = new MermaidEncoder().encode(rawMermaid.toString());

        if (format == Format.AsciiDoc) {
            return String.format(ASCIIDOC_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid);
        }
        return String.format(MARKDOWN_IMAGE_TEMPLATE, url, MERMAID_FORMAT, encodedMermaid);
    }

}