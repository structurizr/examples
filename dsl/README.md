# Structurizr DSL

This directory contains some examples to demonstrate the various features of Structurizr when using the Structurizr DSL. Each example resides in a separate directory, and can be used in a number of ways. For example, to push the "Big Bank plc - Internet Banking System" example (diagrams, documentation, and ADRs) to a Structurizr cloud service workspace
:

```
./structurizr.sh push -id <id> -key <key> -secret <secret> -workspace big-bank-plc/internet-banking-system/workspace.dsl
```

And to export all of the views to PlantUML format:

```
./structurizr.sh export -workspace big-bank-plc/internet-banking-system/workspace.dsl -format plantuml
```