workspace {

    !identifiers hierarchical

    model {
        customer = person "Customer"

        orderService = softwareSystem "Order Service"
        invoiceService = softwareSystem "Invoice Service"
        customerService = softwareSystem "Customer Service"
    }

    views {
        styles {
            element "Person" {
                shape person
            }
        }
    }
        
}