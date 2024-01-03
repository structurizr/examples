workspace extends ../system-catalog.dsl {

    name "Order service"

    model {
        !extend orderService {
            webapp = container "Orders UI"
            database = container "Orders Database"
            
            customer -> webapp "Makes orders using"
            webapp -> customerService "Manages customer data using" "JSON/HTTPS"
            webapp -> database "Reads from and writes to"
        }
    }
    
    views {
        systemContext orderService "SystemContext" {
            include *
            autolayout lr
        }

        container orderService "Containers" {
            include *
            autolayout lr
        }
    }

    configuration {
        scope softwaresystem
    }

}