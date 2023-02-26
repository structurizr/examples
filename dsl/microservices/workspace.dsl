workspace {
    
    !identifiers hierarchical
    
    model {
        user = person "User"
    
        softwareSystem = softwareSystem "Software System"{

            webapp = container "Web Application"

            service1 = group "Service 1" {
                service1Api = container "Service 1 API" {
                    tags "Service 1" "Service API"
                }
                container "Service 1 Database" {
                    tags "Service 1" "Database"
                    service1Api -> this "Reads from and writes to"
                }
            }

            service2 = group "Service 2" {
                service2Api = container "Service 2 API" {
                    tags "Service 2" "Service API"
                }
                container "Service 2 Database" {
                    tags "Service 2" "Database"
                    service2Api -> this "Reads from and writes to"
                }
            }

            service3 = group "Service 3" {
                service3Api = container "Service 3 API" {
                    tags "Service 3" "Service API"
                }
                container "Service 3 Database" {
                    tags "Service 3" "Database"
                    service3Api -> this "Reads from and writes to"
                }
                
            }

            service4 = group "Service 4" {
                service4Api = container "Service 4 API" {
                    tags "Service 4" "Service API"
                }
                container "Service 4 Database" {
                    tags "Service 4" "Database"
                    service4Api -> this "Reads from and writes to"
                }
            }

            service5 = group "Service 5" {
                service5Api = container "Service 5 API" {
                    tags "Service 5" "Service API"
                }
                container "Service 5 Database" {
                    tags "Service 5" "Database"
                    service5Api -> this "Reads from and writes to"
                }
            }

            service6 = group "Service 6" {
                service6Api = container "Service 6 API" {
                    tags "Service 6" "Service API"
                }
                container "Service 6 Database" {
                    tags "Service 6" "Database"
                    service6Api -> this "Reads from and writes to"
                }
            }

            service7 = group "Service 7" {
                service7Api = container "Service 7 API" {
                    tags "Service 7" "Service API"
                }
                container "Service 7 Database" {
                    tags "Service 7" "Database"
                    service7Api -> this "Reads from and writes to"
                }
            }

            service8 = group "Service 8" {
                service8Api = container "Service 8 API" {
                    tags "Service 8" "Service API"
                }
                container "Service 8 Database" {
                    tags "Service 8" "Database"
                    service8Api -> this "Reads from and writes to"
                }
            }

            user -> webapp
            webapp -> service1Api
            service1Api -> service2Api
            service1Api -> service3Api
            service2Api -> service4Api
            service2Api -> service5Api
            webapp -> service3Api
            service3Api -> service4Api
            service3Api -> service7Api
            service4Api -> service6Api
            service7Api -> service8Api
        }

    }
    
    views {
        container softwareSystem "Containers_All" {
            include *
            autolayout
        }

        container softwareSystem "Containers_Service1" {
            include ->softwareSystem.service1->
            autolayout
        }

        container softwareSystem "Containers_Service2" {
            include ->softwareSystem.service2->
            autolayout
        }

        container softwareSystem "Containers_Service3" {
            include ->softwareSystem.service3->
            autolayout
        }

        styles {
            element "Person" {
                shape Person
            }
            element "Service API" {
                shape hexagon
            }
            element "Database" {
                shape cylinder
            }
            element "Service 1" {
                background #91F0AE
            }
            element "Service 2" {
                background #EDF08C
            }
            element "Service 3" {
                background #8CD0F0
            }
            element "Service 4" {
                background #F08CA4
            }
            element "Service 5" {
                background #FFAC33
            }
            element "Service 6" {
                background #DD8BFE
            }
            element "Service 7" {
                background #89ACFF
            }
            element "Service 8" {
                background #FDA9F4
            }
            
        }

    }

}