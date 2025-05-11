workspace {
    
    !identifiers hierarchical
    
    model {
        archetypes {
            externalSoftwareSystem = softwareSystem {
                tag "External"
            }
            microservice = group
            api = container {
                technology "Java and Spring Boot"
                tag "API"
            }
            datastore = container {
                technology "MySQL"                
                tag "Datastore"
            }
        }
        
        user = person "User"
        
        softwareSystemB = externalSoftwareSystem "Software System B"
        softwareSystemC = externalSoftwareSystem "Software System C"

        softwareSystemA = softwareSystem "Software System A" {

            nginx = container "Web Server" {
                description "Serves HTML, CSS, JavaScript, etc"
                technology "Nginx"
                tag "Web Server"
            }
            spa = container "UI" {
                technology "JavaScript and React"
                tag "Single Page Application"
            }

            service1 = microservice "Service 1" {
                service1Api = api "Service 1 API"
                datastore "Service 1 Database Schema" {
                    service1Api -> this
                }
            }

            service2 = microservice "Service 2" {
                service2Api = api "Service 2 API"
                datastore "Service 2 Database Schema" {
                    service2Api -> this
                }
            }

            service3 = microservice "Service 3" {
                service3Api = api "Service 3 API"
                datastore "Service 3 Database" {
                    service3Api -> this
                }
                
            }

            service4 = microservice "Service 4" {
                service4Api = api "Service 4 API"
                datastore "Service 4 Database Schema" {
                    service4Api -> this
                }
            }

            service5 = microservice "Service 5" {
                service5Api = api "Service 5 API" {
                    -> softwareSystemB
                }
                datastore "Service 5 Database Schema" {
                    service5Api -> this
                }
            }

            service6 = microservice "Service 6" {
                service6Api = api "Service 6 API" {
                    -> softwareSystemC
                }
                datastore "Service 6 Database Schema" {
                    service6Api -> this
                }
            }

            service7 = microservice "Service 7" {
                service7Api = api "Service 7 API"
                datastore "Service 7 Database Schema" {
                    service7Api -> this
                }
            }

            service8 = microservice "Service 8" {
                service8Api = api "Service 8 API"
                datastore "Service 8 Database Schema" {
                    service8Api -> this
                }
            }

            user -> nginx "Requests UI from (using web browser)"
            nginx -> spa "Delivers"
            user -> spa "Uses"
            spa -> service1Api
            spa -> service2Api
            spa -> service3Api
            spa -> service4Api
            spa -> service5Api
            service1Api -> service2Api
            service1Api -> service3Api
            service2Api -> service4Api
            service2Api -> service5Api
            service3Api -> service4Api
            service3Api -> service7Api
            service4Api -> service6Api
            service7Api -> service8Api
        }

    }
    
    views {
        container softwareSystemA "Containers_All" {
            include *
            autolayout
        }

        container softwareSystemA "Containers_Service1" {
            include ->softwareSystemA.service1->
            autolayout
        }

        container softwareSystemA "Containers_Service2" {
            include ->softwareSystemA.service2->
            autolayout
        }

        container softwareSystemA "Containers_Service3" {
            include ->softwareSystemA.service3->
            autolayout
        }

        container softwareSystemA "Containers_Service4" {
            include ->softwareSystemA.service4->
            autolayout
        }

        container softwareSystemA "Containers_Service5" {
            include ->softwareSystemA.service5->
            autolayout
        }

        container softwareSystemA "Containers_Service6" {
            include ->softwareSystemA.service6->
            autolayout
        }

        container softwareSystemA "Containers_Service7" {
            include ->softwareSystemA.service7->
            autolayout
        }

        container softwareSystemA "Containers_Service8" {
            include ->softwareSystemA.service8->
            autolayout
        }

        styles {
            element "Person" {
                shape Person
                background #08427b
                colour #ffffff
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "External" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438dd5
                colour #ffffff
            }
            element "Web Server" {
                shape folder
            }
            element "Single Page Application" {
                shape webbrowser
            }
            element "API" {
                shape hexagon
            }
            element "Datastore" {
                shape cylinder
            }
            
        }

    }

}