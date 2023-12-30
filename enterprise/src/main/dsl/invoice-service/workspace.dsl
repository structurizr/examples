workspace extends ../system-catalog.dsl {

    name "Invoice service"

    model {
        !extend invoiceService {
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
        systemContext invoiceService "SystemContext" {
            include *
            autolayout lr
        }

        container invoiceService "Containers" {
            include *
            autolayout lr
        }
    }

    configuration {
        scope softwaresystem
    }

}