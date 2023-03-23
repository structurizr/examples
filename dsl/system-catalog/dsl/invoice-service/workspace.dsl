workspace extends ../catalog.dsl {

    model {
        !ref invoiceService {
            ui = container "Invoice UI"
            s3 = container "Invoice Store" {
                technology "Amazon Web Services S3 Bucket"
            }

            ui -> s3 "Stores and retrieves invoices from" "HTTPS"
        }

        customer -> ui "Downloads invoices from"
        ui -> customerService "Gets customer data from" "JSON/HTTPS"
    }
    
    views {
        systemContext invoiceService "InvoiceService-SystemContext" {
            include *
            autolayout lr
        }

        container invoiceService "InvoiceService-Containers" {
            include *
            autolayout lr
        }
    }
    
}