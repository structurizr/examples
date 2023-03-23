workspace extends ../catalog.dsl {

    model {
        customer -> invoiceService "Downloads invoices from"
        invoiceService -> customerService "Gets customer data from" "JSON/HTTPS"
    }
    
    views {
        systemContext invoiceService "InvoiceService-SystemContext" {
            include *
            autolayout lr
        }
    }
    
}