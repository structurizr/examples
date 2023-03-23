## System catalog example

- [catalog.dsl](dsl/catalog.dsl): a catalog of people and software systems
- `order-service/workspace.dsl`: extends `catalog.dsl` to provide detail about the "Order Service"
- `invoice-service/workspace.dsl`: extends `catalog.dsl` to provide detail about the "Invoice Service"
- `customer-service/workspace.dsl`: extends `catalog.dsl` to provide detail about the "Customer Service"
- `system-landscape/workspace.dsl`: extends `catalog.dsl` and uses a plugin to generate a dynamically aggregated system landscape view

![System landscape view](system-landscape.png)