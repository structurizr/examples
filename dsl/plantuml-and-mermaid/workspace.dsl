workspace {

    model {
        softwareSystem = softwareSystem "Software System" {
            !docs docs
        }
    }

    // see https://github.com/structurizr/dsl-plugins/tree/main/src/main/java/com/structurizr/dsl/plugins/plantuml for plugin source
    !plugin com.structurizr.dsl.plugins.plantuml.PlantUMLEncoderPlugin {
        "plantuml.url" "https://www.plantuml.com/plantuml"
    }

    // see https://github.com/structurizr/dsl-plugins/tree/main/src/main/java/com/structurizr/dsl/plugins/mermaid for plugin source
    !plugin com.structurizr.dsl.plugins.mermaid.MermaidEncoderPlugin {
        "mermaid.url" "https://mermaid.ink"
    }

}