## Tutorial Spring Data-Rest API:

#### Accessing the REST API:
##### If we run the application and go to http://localhost:8080/api/data in a browser, we will receive the following JSON:
    ~>$ curl --location 'http://localhost:8080/api/data'
    == Result:
    {
      "_links" : {
        "users" : {
          "href" : "http://localhost:8080/api/data/users{?page,size,sort}",
          "templated" : true
        },
        "users" : {
          "href" : "http://localhost:8080/api/data/users{?page,size,sort}",
          "templated" : true
        },
        "profile" : {
          "href" : "http://localhost:8080/api/data/profile"
        }
      }
    }
##### Accessing http://localhost:8080/api/data/users/ will show the list of users with page=0 & size=20
    ~>$ curl --location 'http://localhost:8080/api/data/users?page=0&size=20'
    == Result:
    {
      "_embedded" : {
        "users" : [ {
          "name" : "towhid",
          "sex" : "MALE",
          "age" : 20,
          "dob" : "2023-03-23T12:36:53.193+0000",
          "active" : true,
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/api/data/users/1"
            },
            "user" : {
              "href" : "http://localhost:8080/api/data/users/1"
            }
          }
        }, ... ]
      },
      "_links" : {
        "first" : {
          "href" : "http://localhost:8080/api/data/users?page=0&size=20"
        },
        "self" : {
          "href" : "http://localhost:8080/api/data/users{?page,size,sort}",
          "templated" : true
        },
        "next" : {
          "href" : "http://localhost:8080/api/data/users?page=1&size=20"
        },
        "last" : {
          "href" : "http://localhost:8080/api/data/users?page=2&size=20"
        },
        "profile" : {
          "href" : "http://localhost:8080/api/data/profile/users"
        },
        "search" : {
          "href" : "http://localhost:8080/api/data/users/search"
        }
      },
      "page" : {
        "size" : 20,
        "totalElements" : 49,
        "totalPages" : 3,
        "number" : 0
      }
    }
##### We can also do Post/Put/Delete using this api:
    Let’s use curl to add a user using POST:
    ~>$ curl --location 'http://localhost:8080/api/data/users' \
        --header 'Content-Type: application/json' \
        --data '{
            "name": "Test",
            "sex": "MALE",
            "age": 20,
            "active": true
        }'
    == Result:
    {
        "name": "Test",
        "sex": "MALE",
        "age": 20,
        "dob": "2023-12-10T13:56:01.058+0000",
        "active": true,
        "_links": {
            "self": {
                "href": "http://localhost:8080/api/data/users/1"
            },
            "user": {
                "href": "http://localhost:8080/api/data/users/1"
            }
        }
    }
##### We can now access this user by ID at http://localhost:8080/api/data/users/1
    Lets browse users using id=1
    ~>$ curl --location 'http://localhost:8080/api/data/users/1'
    == Result:
    {
        "name": "Test",
        "sex": "MALE",
        "age": 20,
        "dob": "2023-12-10T13:56:01.058+0000",
        "active": true,
        "_links": {
            "self": {
                "href": "http://localhost:8080/api/data/users/1"
            },
            "user": {
                "href": "http://localhost:8080/api/data/users/1"
            }
        }
    }
##### We can search user by Name (since there is findByName method in @Repository interface).
##### e.g. http://localhost:8080/api/data/users/search/findByName?name=Test
    Lets search Test user using curl:
    ~>$ curl --location 'http://localhost:8080/api/data/users/search/findByName?name=Test'
    == Result:
    {
      "_embedded" : {
        "users" : [ {
          "name" : "Test",
          "sex" : "MALE",
          "age" : 20,
          "dob" : "2023-12-10T13:56:01.058+0000",
          "active" : true,
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/api/data/users/1"
            },
            "user" : {
              "href" : "http://localhost:8080/api/data/users/1"
            }
          }
        } ]
      },
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/data/users/search/findByName?name=Test"
        }
      }
    }
##### Now we like to update Test user using PUT:
    Lets update Test user with id=1
    ~>$ curl --location --request PUT 'http://localhost:8080/api/data/users/1' \
        --header 'Content-Type: application/json' \
        --data '{
            "name": "Test Updated",
            "age": 29
        }'
    == Result:
    {
      "name" : "Test Updated",
      "sex" : "NONE",
      "age" : 29,
      "dob" : "2023-12-10",
      "active" : false,
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/data/users/1"
        },
        "user" : {
          "href" : "http://localhost:8080/api/data/users/1"
        }
      }
    }
##### Now Lets delete Test user with DELETE:
    Lets Delete User using curl where id=1
    ~>$ curl --location --request DELETE 'http://localhost:8080/api/data/users/1'
    == Result:
    http status: 204 No Content [Server successfully process the request, but no content returns.] 
##### We can checkout the search api exist in User data-rest api:
    ~>$ curl --location 'http://localhost:8080/api/data/users/search'
    == Result: 
    {
        "_links": {
            "findByAgeLimit": {
                "href": "http://localhost:8080/api/data/users/search/findByAgeLimit{?min,max}",
                "templated": true
            },
            "findByName": {
                "href": "http://localhost:8080/api/data/users/search/findByName{?name}",
                "templated": true
            },
            "self": {
                "href": "http://localhost:8080/api/data/users/search"
            }
        }
    }
##### Let's search using: findByAgeLimit?min=20&max=30
    ~>$ curl --location 'http://localhost:8080/api/data/users/search/findByAgeLimit?min=27&max=29'
    == Result:
    {
        "_embedded": {
            "users": [
                {
                    "name": "Test Updated",
                    "sex": "NONE",
                    "age": 29,
                    "dob": "2023-12-10T14:49:19.156+0000",
                    "active": false,
                    "_links": {
                        "self": {
                            "href": "http://localhost:8080/api/data/users/323"
                        },
                        "user": {
                            "href": "http://localhost:8080/api/data/users/323"
                        }
                    }
                }
            ]
        },
        "_links": {
            "self": {
                "href": "http://localhost:8080/api/data/users/search/findByAgeLimit?min=27&max=29"
            }
        }
    }

#### Reference: (Thanks to all)
##### [Spring Data-Rest Intro By baeldung.com](https://www.baeldung.com/spring-data-rest-intro)
##### [Data-Rest Intro By springboottutorial.com](https://www.springboottutorial.com/introduction-to-spring-data-rest-using-spring-boot)
##### [Spring Data Jpa@Query By baeldung.com](https://www.baeldung.com/spring-data-jpa-query)
##### [@path Vs @collectionResourceRel](https://stackoverflow.com/questions/50411838/spring-data-rest-collectionresourcerel-vs-path)