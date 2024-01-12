workspace extends ../system-catalog.json {

    name "podcast"
    description "A description of the podcast system"

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