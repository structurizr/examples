# Enterprise-wide modelling with Structurizr

This example shows one approach to enterprise-wide modelling with the Structurizr tooling;
using a combination of the Structurizr on-premises installation, Structurizr DSL, and the Structurizr for Java library.

In this example there are 3 software system scoped workspaces, each defined using the Structurizr DSL,
which are perhaps owned and maintained by 3 separate teams:

- [customer service](src/main/dsl/customer-service/workspace.dsl)
- [invoice service](src/main/dsl/invoice-service/workspace.dsl)
- [order service](src/main/dsl/order-service/workspace.dsl)

All people and software systems used across the enterprise are defined in a [system catalog workspace](src/main/dsl/system-catalog.dsl),
again defined using the Structurizr DSL.
Note that this system catalog doesn't define any relationships.
Each of the software system scoped workspaces `extends` this system catalog, and adds detail for that one specific
software system via the `!extend` keyword.

The code in the [Main class](src/main/java/org/example/Main.java):

1. Starts up a Structurizr on-premises installation (via Docker)
2. Loads the 3 example workspaces (above).
3. Automatically generates a system landscape diagram by extracting relationships from the 3 example workspaces.

Steps 1 and 2 are just for bootstrapping this example. Step 3 is the interesting part of the process.

To run this (you will need Java 17 and Docker installed):

```
./gradlew run
```

Here are the resulting diagrams:

| Customer service system context                                                 | Invoice service system context                                               | Order service system context                                           | System landscape (generated)                               |
|---------------------------------------------------------------------------------|------------------------------------------------------------------------------|------------------------------------------------------------------------|------------------------------------------------------------|
| [![Customer service](images/customer-service.png)](images/customer-service.png) | [![Invoice service](images/invoice-service.png)](images/invoice-service.png) | [![Order service](images/order-service.png)](images/order-service.png) | [![Landscape](images/landscape.png)](images/landscape.png) |