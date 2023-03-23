workspace extends ../catalog.dsl {

    !plugin FindRelationshipsPlugin {
        filename ../order-service/workspace.dsl
    }
    
    !plugin FindRelationshipsPlugin {
        filename ../invoice-service/workspace.dsl
    }
    
    views {
        systemLandscape "SystemLandscape" {
            include *
            autolayout lr
        }
    }
    
}