# MermaidEncoderPlugin

This Structurizr DSL plugin looks for inline Mermaid diagram definitions in Markdown/AsciiDoc documentation,
and encodes them as images. For example, this definition in Markdown content:

````
```mermaid
flowchart TD
    Start --> Stop
``` 
````

Will be converted to:

```
![](https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuU3RhcnQgLS0+IFN0b3BcbiIsICJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCIsICJzZWN1cml0eUxldmVsIjogImxvb3NlIn19)
```

Which renders as:

![](https://mermaid.ink/svg/eyAiY29kZSI6ImZsb3djaGFydCBURFxuU3RhcnQgLS0+IFN0b3BcbiIsICJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCIsICJzZWN1cml0eUxldmVsIjogImxvb3NlIn19)

## Usage

Add the plugin, and reference it from your DSL file as follows:

```
!plugin mermaid.MermaidEncoderPlugin
```

This should appear after any `!docs` and/or `!adrs` statements that import documentation into your workspace.

By default, the public Mermaid service (`https://mermaid.ink`) will be used, but you can specify a parameter to change this:

```
!plugin mermaid.MermaidEncoderPlugin {
    "mermaid.url" "http://localhost"
}
```


