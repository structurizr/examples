workspace extends ../system-catalog.json {

    model {
        !extend podcast {
            webapp = container "Web Application"
            db = container "Database"

            webapp -> db "Reads from"
        }
    }

    views {
        systemContext podcast "SystemContext" {
            include *
            autolayout
        }

        container podcast "Containers" {
            include *
            autolayout
        }

    }

    configuration {
        scope softwareSystem
    }

}