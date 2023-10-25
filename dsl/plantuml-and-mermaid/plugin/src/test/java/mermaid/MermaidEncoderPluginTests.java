package mermaid;

import com.structurizr.Workspace;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.Section;
import com.structurizr.dsl.StructurizrDslPluginContext;
import plantuml.PlantUMLEncoderPlugin;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MermaidEncoderPluginTests {

    @Test
    public void test_run() {
        Workspace workspace = new Workspace("Name", "Description");

        Section markdown = new Section(Format.Markdown, "## Context\n" +
                "\n" +
                "```mermaid\n" +
                "flowchart TD\n" +
                "    Start --> Stop\n" +
                "```");
        workspace.getDocumentation().addSection(markdown);

        Section asciidoc = new Section(Format.AsciiDoc, "== Context\n" +
                "\n" +
                "```mermaid\n" +
                "flowchart TD\n" +
                "    Start --> Stop\n" +
                "```");
        workspace.getDocumentation().addSection(asciidoc);

        Map<String,String> parameters = new HashMap<>();
        StructurizrDslPluginContext context = new StructurizrDslPluginContext(null, workspace, parameters);
        new MermaidEncoderPlugin().run(context);

        assertEquals("## Context\n" +
                "\n" +
                "![](https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuICAgIFN0YXJ0IC0tPiBTdG9wXG4iLCAibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQiLCAic2VjdXJpdHlMZXZlbCI6ICJsb29zZSJ9fQ==)\n", markdown.getContent());

        assertEquals("== Context\n" +
                "\n" +
                "image::https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuICAgIFN0YXJ0IC0tPiBTdG9wXG4iLCAibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQiLCAic2VjdXJpdHlMZXZlbCI6ICJsb29zZSJ9fQ==[]\n", asciidoc.getContent());
    }

    @Test
    public void test_asciidoc_diagram_run() {
       Workspace workspace = new Workspace("Name", "Description");

       Section asciidoc = new Section(Format.AsciiDoc, "== Context\n" +
               "\n" +
               "[mermaid]\n" +
               "....\n" +
               "flowchart TD\n" +
               "    Start --> Stop\n" +
               "....");
       workspace.getDocumentation().addSection(asciidoc);

       Map<String,String> parameters = new HashMap<>();
       StructurizrDslPluginContext context = new StructurizrDslPluginContext(null, workspace, parameters);
       new MermaidEncoderPlugin().run(context);

       assertEquals("== Context\n" +
               "\n" +
               "image::https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuICAgIFN0YXJ0IC0tPiBTdG9wXG4iLCAibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQiLCAic2VjdXJpdHlMZXZlbCI6ICJsb29zZSJ9fQ==[]\n", asciidoc.getContent());
   }

   @Test
   public void test_asciidoc_diagram_with_parameters_run() {
       Workspace workspace = new Workspace("Name", "Description");

       Section asciidoc = new Section(Format.AsciiDoc, "== Context\n" +
               "\n" +
               "[mermaid, target=output-file-name, format=output-format]\n" +
               "....\n" +
               "flowchart TD\n" +
               "    Start --> Stop\n" +
               "....");
       workspace.getDocumentation().addSection(asciidoc);

       Map<String,String> parameters = new HashMap<>();
       StructurizrDslPluginContext context = new StructurizrDslPluginContext(null, workspace, parameters);
       new MermaidEncoderPlugin().run(context);

       assertEquals("== Context\n" +
               "\n" +
               "image::https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuICAgIFN0YXJ0IC0tPiBTdG9wXG4iLCAibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQiLCAic2VjdXJpdHlMZXZlbCI6ICJsb29zZSJ9fQ==[]\n", asciidoc.getContent());
   }
}
