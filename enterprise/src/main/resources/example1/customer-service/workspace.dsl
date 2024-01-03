workspace extends ../system-catalog.dsl {

    name "Customer service"

    model {
        !extend customerService {
            api = container "Customer API"
            database = container "Customer Database"
            
            api -> database "Reads from and writes to"
        }
    }
    
    views {
        systemContext customerService "SystemContext" {
            include *
            autolayout lr
        }

        container customerService "Containers" {
            include *
            autolayout lr
        }
    }

    configuration {
        scope softwaresystem
    }
    
}