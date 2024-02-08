## OPM RestAPI Documentation

- Adopted from: https://stubby4j.com/docs/admin_portal.html
- Inspired by Swagger API docs style & structure: https://petstore.swagger.io/#/pet

------------------------------------------------------------------------------------------

### Authentication

All requests to the RestAPI must include valid authentication credentials. Please include a valid JWT token within
the Bearer scheme included in the Authorization HTTP header.

`'Authorization': 'Bearer <Signed JSON Web Token>'`

------------------------------------------------------------------------------------------

### Optional Values

Optional values must still include the given key, but should be set to null if not looking
to include them in the payload.

For example, for the following optional username JSON body:

```json
{
    "username": "username-here"
}
```

Setting username to null, as follows, would indicate desire to not include it in the payload body.

```json
{
    "username": null
}
```

------------------------------------------------------------------------------------------

### ID Values

Values that must include an ID cannot contain decimal or negative values.

They must contain a positive, non-zero integer value associated with the given entity.

------------------------------------------------------------------------------------------

### Versioning

#### Current Version: 1

Versioning of the API will occur in a prefix of the URL routes used. For example, version 1 would be:

<code><b>/api/v1</b></code>

All endpoints should use this format as a prefix in their requests. For example, user authentication would be:

<code><b>/api/v1/user/auth</b></code>

------------------------------------------------------------------------------------------

### Endpoints

#### User Authentication

<details>
 <summary><code>POST</code> <code><b>/users/register</b></code> <code>(registers user information if they don't have an account)</code>:white_check_mark:</summary>

##### Request Payload

> ```json
> {
>   "username": "username-here"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `{"username":"username-here","message":"Success"}` | Successfully registered user. |
> | `400`         | `application/json`                | `{"code":400,"message":"User already exists with those details"}` | User details already exist in database. |
> | `400`         | `application/json`                | `{"code":400,"message":"Username is required"}` | Username field required. |
> | `400`         | `application/json`                | `{"code":400,"message":"Username must be 3 to 100 characters"}` | Username length requirement. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/users/register \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"username":"my_username"}' 
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/users/auth</b></code> <code>(authenticates user)</code>:white_check_mark:</summary>

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"username":"username-here","message":"Success"}` | **Includes a URI to the user resource in the Location Header** |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | No user found using those credentials. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/users/auth \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Project Management

<details>
 <summary><code>GET</code> <code><b>/projects</b></code> <code>(gets all of a user's projects)</code>:white_check_mark:</summary>

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Returns all of a user's projects. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projects": [
>       {
>           "projectName": "project1",
>           "projectID": 1,
>           "lastUpdated": "2023-10-31T15:45:00Z",
>           "projectLocation": "/api/v1/projects/1",
>           "team": {
>               "teamName": "team1",
>               "teamID": 1, 
>               "teamLocation": "/api/v1/teams/1"
>           }
>       },
>       {
>           "projectName": "project2",
>           "projectID": 2,
>           "lastUpdated": "2023-10-31T15:45:00Z",
>           "projectLocation": "/api/v1/projects/2",
>           "team": {
>               "teamName": "team2",
>               "teamID": 2, 
>               "teamLocation": "/api/v1/teams/2"
>           }
>       } 
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/projects</b></code> <code>(creates a new project)</code>:white_check_mark:</summary>

##### Request Payload

> ```json
> {
>   "projectName": "New Project Name",
>   "projectID": 1,
>   "teamName": "team1",
>   "teamID": 1,
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `See below.` | **Includes a URI to the project resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"Project name for that team already exists"}` | Project name for team already exists. Teams must have unique project names. |
> | `404`         | `application/json`                | `{"code":"404","message":"User not in team, or team does not exist"}` | User not in team, or chosen team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 201 HTTP Code Response Body

> ```json
> {
>     "projectName": "New Project Name",
>     "projectID": 1,
>     "projectLocation": "/api/v1/projects/1"
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/projects \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{
>   "projectName": "New Project Name",
>   "teamName": "team1",
>   "teamID": 1 }' 
> ```

</details>
<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}</b></code> <code>(gets details for a specific project)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Returns details regarding a specific project. |
> | `403`         | `application/json`                | `{"code":"403","message":"User not in project, or project does not exist"}` | User not in this project, or the project does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projectName": "project1",
>     "projectID": 1,
>     "projectLocation": "/api/v1/projects/1",
>     "team": {
>         "teamName": "Team1",
>         "teamID": 1,
>         "teamLocation": "/api/v1/teams/1"
>      },
>     "columns": [
>      {
>           "columnTitle": "Todo",
>           "columnID": 1,
>           "columnIndex": 0,       # Indicates location on board
>           "columnLocation": "/api/v1/projects/1/columns/1",
>           "tasks": [
>            {
>                 "title": "task1",
>                 "taskID": 1, 
>                 "priority": "High",
                  "dueDate": "2023-11-01", # Or null  
>                 "comments": 1, # Number of comments on task
                  "taskIndex": 0, # Used for sorting eventually, default to -1
                  "assignedTo": {
                        "username": "username-of-assignee",
                        "userID": 1
                   }, # Or null
                  "sprint": {
                        "sprintID": 1,
                        "sprintName": "Sprint Name",
                        "endDate": "2023-11-01",
                        "sprintLocation": "/api/v1/projects/1/sprints/1"
                   }, # Or null
>                 "taskLocation": "/api/v1/projects/1/tasks/1"
>            },
>           ]
>      },
>      {
>           "columnTitle": "In progress",
>           "columnID": 2,
>           "columnIndex": 1,       # Indicates location on board
>           "columnLocation": "/api/v1/projects/1/columns/2",
>           "tasks": []
>      },
>      {
>           "columnTitle": "Done",
>           "columnID": 3,
>           "columnIndex": 2,       # Indicates location on board
>           "columnLocation": "/api/v1/projects/1/columns/3",
>           "tasks": []
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}</b></code> <code>(modifies specific project details)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>   "projectName": "new-name",      # Cannot be deleted, only modified
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Modify the project name. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projectName": "new-name",
>     "projectID": 1,
>     "projectLocation": "/api/v1/projects/1",
>     "lastUpdated": "2023-10-31T15:45:00Z",
> }
> ```

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1 \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"projectName":"new-name"}' 
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/projects/{projectID}</b></code> <code>(deletes a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Project deleted."}` | Successful deletion. |
> | `403`         | `application/json`                | `{"code":"403","message":"Cannot delete if tasks remain"}` | Tasks must be removed to delete a project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Users and Project Management

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/users</b></code> <code>(gets all users associated with a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Returns all users associated with a project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projectName": "project1",
>     "projectID": 1,
>     "lastUpdated": "2023-10-31T15:45:00Z",
>     "projectLocation": "/api/v1/projects/1",
>     "team": {
>         "teamName": "Team1",
>         "teamID": 1,
>         "teamLocation": "/api/v1/teams/1"
>      },
>     "users": [
>       {
>           "username": "username1",
>           "userID": 1,
>           "userInProjectTeam": true,       
>       },
>       {
>           "username": "username2",
>           "userID": 2,
>           "userInProjectTeam": false,       
>       },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/users \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/projects/{projectID}/user</b></code> <code>(add user to project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>   "username": "username-here"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"{username} added to project."}` | Successfully added user to project. |
> | `400`         | `application/json`                | `{"code":"400","message":"User already in project"}` | User to add already in in this project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | Adding user not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | Username not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/projects/1/user \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"username":"another_username"}' 
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/projects/{projectID}/user</b></code> <code>(remove user from project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>   "username": "username-here"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"{username} removed from project."}` | Successfully removed user from project. |
> | `400`         | `application/json`                | `{"code":"403","message":"User not in project"}` | User to delete not in this project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | Deleting user not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | Username not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1/user \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"username":"another_username"}' 
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/users/{userID}/projects</b></code> <code>(gets all projects associated with a user)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `userID` |  required  | int ($int64) | The unique ID of the user |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Returns all projects associated with a user. |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | User not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "username": "my-username",
>     "userID": 1,
>     "projects": [
>       {
>           "projectName": "project1",
>           "projectID": 1,
>           "projectLocation": "/api/v1/projects/1",
>           "team": {
>               "teamName": "Team1",
>               "teamID": 1,
>               "teamLocation": "/api/v1/teams/1"
>           }
>       },
>       {
>           "projectName": "project2",
>           "projectID": 2,
>           "projectLocation": "/api/v1/projects/2",
>           "team": {
>               "teamName": "Team2",
>               "teamID": 2,
>               "teamLocation": "/api/v1/teams/2"
>           }
>       },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/users/1/projects \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Columns Management

<details>
 <summary><code>POST</code> <code><b>/projects/{projectID}/columns</b></code> <code>(adds a column to a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>   "columnTitle": "New Column Here"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `See below.` | **Includes a URI to the column resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"Column exists"}` | Column already exists. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 201 HTTP Code Response Body

> ```json
> {
>     "columnTitle": "New Column Here",
>     "columnIndex": 1,         # New column always placed at end
>     "columnID": 1
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/project/1/columns \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"columnTitle":"New Column Here"}' 
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}/columns/{columnID}/name</b></code> <code>(modifies column name)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `columnID` |  required  | int ($int64) | The unique ID of the column |

##### Request Payload

> ```json
> {
>   "columnTitle": "New Column Name Here"       # Cannot be deleted, only modified
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully modified column name in project. |
> | `400`         | `application/json`                | `{"code":"400","message":"Column exists in project"}` | Column already exists in project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Column does not exist"}` | Column not found in project. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "columnTitle": "New Column Name Here",
>     "columnIndex": 0,      # Keeps previous column index
>     "columnID": 1,
>     "columnLocation": "/api/v1/projects/1/columns/1
> }
> ```

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/columns/1/name \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"columnTitle":"New Column Name Here"}' 
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}/columns/{columnID}/order</b></code> <code>(modifies column order)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `columnID` |  required  | int ($int64) | The unique ID of the column |

##### Request Payload

> ```json
> {
>   "columnIndex": 1
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully modified column index in project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Column does not exist"}` | Column not found in project. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "columnTitle": "project1",
>     "columnIndex": 0,      # Keeps previous column index
>     "columnID": 1,
>     "columnLocation": "/api/v1/projects/1/columns/1
> }
> ```

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/columns/1/name \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"columnTitle":"New Column Name Here"}' 
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/projects/{projectID}/columns/{columnID}</b></code> <code>(deletes a column from a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `columnID` |  required  | int ($int64) | The unique ID of the column |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Column removed from project"}` | Successfully deleted column from project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Cannot remove if tasks remain in column"}` | Tasks still in column. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Column does not exist"}` | Column not found in project. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |


##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1/columns/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Task Management

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/tasks</b></code> <code>(gets all tasks associated with a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved all tasks for a project. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projectName": "project1",
>     "projectID": 1,
>     "lastUpdated": "2023-10-31T15:45:00Z",
>     "projectLocation": "/api/v1/projects/1",
>     "tasks": [
>      {
>           "title": "Task 1",
>           "taskID": 1,
>           "taskColumnIndex": 0,       # Indicates location on board
>           "description": "This is a task!",
>           "assignedTo": {
>                 "username": "username-of-assignee",
>                 "userID": 1
>            } or null,       
>           "priority": "High",
>           "sprint": {
>                 "startDate": "2023-10-31",
>                 "endDate": "2023-11-01",
>                 "sprintName": "Sprint Name",
>                 "sprintID": 1,
>                 "sprintLocation": "api/v1/projects/1/sprints/1"
>            } or null,
>           "comments": [
>            {
>                 "commentID": 1,
>                 "commentBody": "This is a comment",
>                 "commenter": "username-here",
>                 "commentLocation": "/api/v1/projects/1/tasks/1/comments/1"
>            },
>           ],
>           "customFields": [ ... ],
>           "taskLocation": "/api/v1/projects/1/tasks/1",
>      },
>      {
>           "title": "Task 2",
>           "taskID": 2,
>           "taskColumnIndex": 1,       # Indicates location on board
>           "description": "This is another task!",
>           "assignedTo": {
>                 "username": "username-of-assignee",
>                 "userID": 1
>            } or null,       
>           "priority": "High",
>           "sprint": {
>                 "startDate": "2023-10-31",
>                 "endDate": "2023-11-01",
>                 "sprintName": "Sprint Name",
>                 "sprintID": 1,
>                 "sprintLocation": "api/v1/projects/1/sprints/1"
>            } or null,
>           "comments": [
>            {
>                 "commentID": 2,
>                 "commentBody": "This is another comment",
>                 "commenter": "username-here",
>                 "commentLocation": "/api/v1/projects/1/tasks/2/comments/2"
>            },
>           ],
>           "customFields": [ ... ],
>           "taskLocation": "/api/v1/projects/1/tasks/2",
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/projects/{projectID}/tasks</b></code> <code>(adds task to column in project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>     "title": "Task 1",
>     "description": "This is another task!", # Optional - a description of only spaces is considered null
>     "columnID": 1,                          # Optional, defaults to first in-order column if not included
>     "assignedTo": 1,                        # Optional, ID of the user who it is being assigned to, or null
>     "dueDate": "2024-11-03",                # Optional, in format "yyyy-MM-dd"
>     "priority": "High",                     # Optional, must be one of: 'High', 'Medium', 'Low', 'None', defaults to 'None' 
>     "sprintID": 1,                          # Optional
>     "customFields": [ ... ]                 # Optional
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `See below.` | **Includes a URI to the task resource in the Location Header** |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Column does not exist"}` | Column not found in project. Project must have at least one column. |
> | `404`         | `application/json`                | `{"code":"404","message":"Sprint not found"}` | Sprint not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Assignee not found"}` | Assignee not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 201 HTTP Code Response Body

> ```json
> {
>     "title": "Task 1",
>     "taskID": 1,
>     "columnID": 1,                          # ID of column to be placed under
>     "priority": "None",                     # Other possible values: 'High', 'Medium', 'Low'
>     "description": "None",                  # Nullable
>     "dueDate": "None",                      # Nullable
>     "sprintID": "None",                     # Nullable
>     "assignedTo": "None",                   # Nullable
>     "taskLocation": "/api/v1/projects/1/tasks/1",
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"title":"Task title"}' 
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/tasks/{taskID}</b></code> <code>(gets specific task details in a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved the task details. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "title": "Task 1",
>     "taskID": 1,
>     "taskColumnIndex": 0,       # Indicates location on board
>     "column": {
>           "columnTitle": "Column",
>           "columnIndex": 0,
>           "columnID": 1,
>           "columnLocation": "api/v1/projects/1/columns/1"
>      },
>     "description": "This is a task!",
>     "assignedTo": {
>           "username": "username-of-assignee",
>           "userID": 1
>      } or null,       
>     "priority": "High" or null,
>     "sprint": {
>           "startDate": "2023-10-31",
>           "endDate": "2023-11-01",
>           "sprintName": "Sprint Name",
>           "sprintID": 1,
>           "sprintLocation": "api/v1/projects/1/sprints/1"
>      } or null,
>     "comments": [
>      {
>           "commentID": 1,
>           "commentBody": "This is a comment",
>           "commenter": "username-here",
>           "commentLocation": "/api/v1/projects/1/tasks/1/comments/1"
>      },
>     ],
>     "customFields": [ ... ],
>     "taskLocation": "/api/v1/projects/1/tasks/1",
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/users/{userID}/tasks</b></code> <code>(gets all tasks for a user in a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `userID` |  required  | int ($int64) | The unique ID of the user |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved the task details. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | Searching user not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | User not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "title": "Task 1",
>     "taskID": 1,
>     "taskColumnIndex": 0,       # Indicates location on board
>     "column": {
>           "columnTitle": "Column",
>           "columnIndex": 0,
>           "columnID": 1,
>           "columnLocation": "api/v1/projects/1/columns/1"
>      },
>     "description": "This is a task!",
>     "assignedTo": {
>           "username": "username-of-assignee",
>           "userID": 1
>      } or null,       
>     "priority": "High" or null,
>     "sprint": {
>           "startDate": "2023-10-31",
>           "endDate": "2023-11-01",
>           "sprintName": "Sprint Name",
>           "sprintID": 1,
>           "sprintLocation": "api/v1/projects/1/sprints/1"
>      } or null,
>     "comments": [
>      {
>           "commentID": 1,
>           "commentBody": "This is a comment",
>           "commenter": "username-here",
>           "commentLocation": "/api/v1/projects/1/tasks/1/comments/1"
>      },
>     ],
>     "customFields": [ ... ],
>     "taskLocation": "/api/v1/projects/1/tasks/1",
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/users/1/tasks \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}/tasks/{taskID}</b></code> <code>(modifies task in column in project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Request Payload

To keep the attribute the same, do not include the task attribute in the request payload.

> ```json
> {
>     "title": "New Title",                     # Optional - note that a title is mandatory for a task, so no possibility of deleting a title
>     "description": "This is another task!",   # Optional, set as empty string to delete
>     "assignedTo": 1,                          # Optional, ID of the user who it is being assigned to, or -1 to delete
>     "priority": "High",                       # Optional, must be one of 'High', 'Medium', 'Low', 'None'
>     "sprintID": 1,                            # Optional, ID of sprint to change to, or -1 to delete
>     "customFields": [ ... ],                  # Optional, for future implementation
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Task modified"}` | Successfully modified task. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Invalid task"}` | Task attribute not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Invalid sprint"}` | Sprint not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Assignee"}` | Assignee not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1 \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"title":"New title."}' 
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/project/{projectID}/tasks/{taskID}/columns/{columnID}</b></code> <code>(moves task to other column in project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |
> | `columnID` |  required  | int ($int64) | The unique ID of the column |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Task moved"}` | Successfully moved task. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found in project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Column does not exist"}` | Column not found in project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1/columns/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/project/{projectID}/tasks/{taskID}</b></code> <code>(removes task from project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Task moved"}` | Successfully deleted task. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found in project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Comment Management

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/tasks/{taskID}/comments</b></code> <code>(gets all comments associated with a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved all comments for the task. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "taskID": 1,
>     "taskLocation": "/api/v1/projects/1/tasks/1",
>     "comments": [
>      {
>           "commentID": 1,
>           "commentBody": "This is another comment!",
>           "commenter": {
>                 "username": "commenter",
>                 "userID": 1
>            },       
>           "commentLocation": "/api/v1/projects/1/tasks/1/comments/1",
>      },
>      {
>           "commentID": 2,
>           "commentBody": "This is a second comment!",
>           "commenter": {
>                 "username": "commenter2",
>                 "userID": 2
>            },       
>           "commentLocation": "/api/v1/projects/1/tasks/1/comments/2",
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1/comments \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/projects/{projectID}/tasks/{taskID}/comments</b></code> <code>(add a comment to a task)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |

##### Request Payload

> ```json
> {
>     "commentBody": "New comment body."    # Cannot be empty or just spaces
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | **Includes a URI to the comment resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"Comment cannot be empty"}` | Comment cannot be empty. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 201 HTTP Code Response Body

> ```json
> {
>     "commentID": 1,
>     "commentBody": "This is a comment.",
>     "commenter":{
>           "username": "commenter",
>           "userID": 1
>      }
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1/comments \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"commentBody":"New comment stuff."}' 
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}/tasks/{taskID}/comments/{commentID}</b></code> <code>(modify a comment on a task)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |
> | `commentID` |  required  | int ($int64) | The unique ID of the comment |

##### Request Payload

> ```json
> {
>     "commentBody": "New comment body."    # Cannot be empty or just spaces
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Comment modified."}` | Successfully edited the comment. |
> | `400`         | `application/json`                | `{"code":"400","message":"Comment cannot be empty"}` | Comment cannot be empty. Delete instead. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Comment does not exist"}` | Comment not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1/comments/1 \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"commentBody":"New comment stuff."}' 
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/projects/{projectID}/tasks/{taskID}/comments/{commentID}</b></code> <code>(delete a comment on a task)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `taskID` |  required  | int ($int64) | The unique ID of the task |
> | `commentID` |  required  | int ($int64) | The unique ID of the comment |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Comment deleted."}` | Successfully deleted the comment. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Comment does not exist"}` | Comment not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Task does not exist"}` | Task not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1/tasks/1/comments/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------

#### Sprint Management

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/sprints</b></code> <code>(gets all sprints associated with a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved all sprints for the task. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "projectID": 1,
>     "projectLocation": "/api/v1/projects/1",
>     "sprints": [
>      {
>           "sprintID": 1,
>           "sprintName": "Sprint Name1",
>           "startDate": "2023-10-31",
>           "endDate": "2023-11-15",
>           "sprintLocation": "/api/v1/projects/1/sprints/1"
>      },
>      {
>           "sprintID": 2,
>           "sprintName": "Sprint Name2",
>           "startDate": "2023-10-31",
>           "endDate": "2023-11-16",
>           "sprintLocation": "/api/v1/projects/1/sprints/2"
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/sprints \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/projects/{projectID}/sprints</b></code> <code>(adds a sprint to a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |

##### Request Payload

> ```json
> {
>     "startDate": "2023-11-15",
>     "endDate": "2023-11-30",
>     "sprintName": "Sprint Name"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `See below.` | **Includes a URI to the sprint resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"Sprint name must be unique for project"}` | Sprint name must be unique for this project. |
> | `400`         | `application/json`                | `{"code":"400","message":"Invalid date range"}` | Invalid date range for sprint. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 201 HTTP Code Response Body

> ```json
> {
>     "sprintID": 1,
>     "startDate": "2023-11-15",
>     "endDate": "2023-11-30",
>     "sprintName": "Sprint Name"
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/projects/1/sprints \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{
>   "sprintName": "Sprint name",
>   "startDate": "2023-11-15",
>   "endDate": "2023-11-30",
>      }' 
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/projects/{projectID}/sprints/{sprintID}</b></code> <code>(get all tasks associated with a sprint in a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `sprintID` |  required  | int ($int64) | The unique ID of the sprint |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved all tasks for the sprint. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Sprint does not exist"}` | Sprint not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "sprintID": 1,
>     "sprintName": "Sprint Name",
>     "startDate": "2023-10-31",
>     "endDate": "2023-11-15",
>     "tasks": [
>      {
>           "title": "Task 1",
>           "taskID": 1,
>           "taskColumnIndex": 0,       # Indicates location on board
>           "description": "This is a task!",
>           "assignedTo": {
>                 "username": "username-of-assignee",
>                 "userID": 1
>            } or null,       
>           "priority": "High",
>           "comments": [
>            {
>                 "commentID": 1,
>                 "commentBody": "This is a comment",
>                 "commenter": "username-here",
>                 "commentLocation": "/api/v1/projects/1/tasks/1/comments/1"
>            },
>           ],
>           "customFields": [ ... ],
>           "taskLocation": "/api/v1/projects/1/tasks/1",
>      },
>      {
>           "title": "Task 2",
>           "taskID": 2,
>           "taskColumnIndex": 1,       # Indicates location on board
>           "description": "This is another task!",
>           "assignedTo": {
>                 "username": "username-of-assignee",
>                 "userID": 1
>            } or null,       
>           "priority": "High",
>           "comments": [
>            {
>                 "commentID": 2,
>                 "commentBody": "This is another comment",
>                 "commenter": "username-here",
>                 "commentLocation": "/api/v1/projects/1/tasks/2/comments/2"
>            },
>           ],
>           "customFields": [ ... ],
>           "taskLocation": "/api/v1/projects/1/tasks/2",
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/projects/1/sprints/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>PUT</code> <code><b>/projects/{projectID}/sprints/{sprintID}</b></code> <code>(modifies a sprint in a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `sprintID` |  required  | int ($int64) | The unique ID of the sprint |

##### Request Payload

If a field is included, it is assumed that user is trying to edit that field.
Leaving the field out of the payload will keep the field's original value.
No fields can be deleted, or have just empty spaces.

> ```json
> {
>     "startDate": "2023-11-15",
>     "endDate": "2023-11-30",
>     "sprintName": "Sprint Name"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully modified the sprint. |
> | `400`         | `application/json`                | `{"code":"400","message":"Sprint name must be unique for project"}` | Sprint name must be unique for this project. |
> | `400`         | `application/json`                | `{"code":"400","message":"Invalid date range"}` | Invalid date range for sprint. |
> | `400`         | `application/json`                | `{"code":"400","message":"Invalid sprint attributes"}` | Invalid sprint attributes. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Sprint does not exist"}` | Sprint not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "sprintID": 1,
>     "startDate": "2023-11-15",
>     "endDate": "2023-11-30",
>     "sprintName": "Sprint Name"
> }
> ```

##### Example cURL

> ```bash
> curl -X PUT \
>  https://opm-api.propersi.me/api/v1/projects/1/sprints/1 \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{
>   "sprintName": "Sprint name",
>   "startDate": "2023-11-15",
>   "endDate": "2023-11-30",
>      }' 
> ```
</details>

<details>
 <summary><code>DELETE</code> <code><b>/projects/{projectID}/sprints/{sprintID}</b></code> <code>(deletes a sprint in a project)</code></summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `projectID` |  required  | int ($int64) | The unique ID of the project |
> | `sprintID` |  required  | int ($int64) | The unique ID of the sprint |


##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Not authorized"}` | Sprint successfully deleted. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in this project. |
> | `404`         | `application/json`                | `{"code":"404","message":"Project does not exist"}` | Project not found. |
> | `404`         | `application/json`                | `{"code":"404","message":"Sprint does not exist"}` | Sprint not found. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |


##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/projects/1/sprints/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```
</details>

------------------------------------------------------------------------------------------

#### Team Management

<details>
 <summary><code>GET</code> <code><b>/teams</b></code> <code>(gets all teams associated with a user)</code>:white_check_mark:</summary>

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Successfully retrieved all teams for the user. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "teams": [
>      {
>           "teamID": 1,
>           "teamName": "Team Name 1",
>           "teamLocation": "/api/v1/teams/1",
            "isTeamCreator": false
>      },
>      {
>           "teamID": 2,
>           "teamName": "Team Name 2",
>           "teamLocation": "/api/v1/teams/2",
            "isTeamCreator": true
>      },
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/teams \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/teams</b></code> <code>(make a new team)</code>:white_check_mark:</summary>

##### Request Payload

> ```json
> {
>     "teamName": "My new team",
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `201`         | `application/json`                | `See below.` | **Includes a URI to the team resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"You have already made a team with this name"}` | Users must make unique teams. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "teamName": "Team Name 1",
>     "teamID": 1,
>     "teamCreator": 1,
>     "teamLocation": "/api/v1/teams/1",
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/teams \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"teamName":"my_username"}' 
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/teams/{teamID}</b></code> <code>(get all projects associated with a team)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `teamID` |  required  | int ($int64) | The unique ID of the team |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Gets all projects associated with a team. |
> | `404`         | `application/json`                | `{"code":"404","message":"User not in team, or does not exist"}` | User not in team, or team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "teamID": 1,
>     "teamName": "Team Name 1",
      "teamLocation": "/api/v1/teams/1
>     "projects": [
>       {
>           "projectName": "project1",
>           "projectID": 1,
>           "lastUpdated": "2023-10-31T15:45:00Z",
>           "projectLocation": "/api/v1/projects/1",
>       },
>       {
>           "projectName": "project2",
>           "projectID": 2,
>           "lastUpdated": "2023-10-31T15:45:00Z",
>           "projectLocation": "/api/v1/projects/2",
>       } 
>     ]
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/teams/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/teams/{teamID}</b></code> <code>(delete a team)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `teamID` |  required  | int ($int64) | The unique ID of the team |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"Team deleted"}` | Team deleted. |
> | `403`         | `application/json`                | `{"code":"403","message":"Team still has associated projects - remove them before deleting the team"}` | Teams must have no associated projects. |
> | `403`         | `application/json`                | `{"code":"403","message":"Team still contains other members - remove them before deleting the team"}` | Teams must have no other members. |
> | `403`         | `application/json`                | `{"code":"403", "message":"User not creator of this team"}` | User not creator - only creator can delete team. |
> | `404`         | `application/json`                | `{"code":"404", "message":"Team does not exist"}` | Team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/teams/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>GET</code> <code><b>/teams/{teamID}/members</b></code> <code>(get all members associated with a team)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `teamID` |  required  | int ($int64) | The unique ID of the team |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | Gets all members associated with a team. |
> | `404`         | `application/json`                | `{"code":"404","message":"User not in team, or team does not exist"}` | User not in team, or team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "teamID": 1,
>     "teamName": "Team Name 1",
>     "members": [
>       {
>           "username": "user1",
>           "userID": 1,
            "isTeamCreator": true
>       },
>       {
>           "username": "user2",
>           "userID": 2,
            "isTeamCreator": false 
>       } 
>     ],
      "teamLocation": "/api/v1/teams/1"
> }
> ```

##### Example cURL

> ```bash
> curl -X GET \
>  https://opm-api.propersi.me/api/v1/teams/1/members \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

<details>
 <summary><code>POST</code> <code><b>/teams/{teamID}/members</b></code> <code>(add team member to a team)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `teamID` |  required  | int ($int64) | The unique ID of the team |

##### Request Payload

> ```json
> {
>   "username": "username-here"
> }
> ```

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `See below.` | **Includes a URI to the team resource in the Location Header** |
> | `400`         | `application/json`                | `{"code":"400","message":"User already exists in this team"}` | User already in team. |
> | `404`         | `application/json`                | `{"code":"404","message":"User does not exist"}` | User to add does not exist. |
> | `404`         | `application/json`                | `{"code":"404","message":"User not in team, or team does not exist"}` | User not in team, or team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

###### 200 HTTP Code Response Body

> ```json
> {
>     "code": 200,
>     "message": "User added",
> }
> ```

##### Example cURL

> ```bash
> curl -X POST \
>  https://opm-api.propersi.me/api/v1/teams/1/members \
>  -H 'Content-Type: application/json' \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
>  -d '{"username":"my_username"}' 
> ```

</details>

<details>
 <summary><code>DELETE</code> <code><b>/teams/{teamID}/members/{memberID}</b></code> <code>(remove team member from team)</code>:white_check_mark:</summary>

##### Parameters

> | name   |  type      | data type      | description                                          |
> |--------|------------|----------------|------------------------------------------------------|
> | `teamID` |  required  | int ($int64) | The unique ID of the team |
> | `memberID` |  required  | int ($int64) | The unique ID of the user |

##### Responses

> | http code     | content-type                      | response  | details |
> |---------------|-----------------------------------|-----------|---------------------------------------------------------|
> | `200`         | `application/json`                | `{"code":"200","message":"User removed"}` | Successfully removed user from team. |
> | `403`         | `application/json`                | `{"code":"403","message":"Not authorized"}` | User not in team. |
> | `404`         | `application/json`                | `{"code":"404","message":"User to add does not exist, or is not in team"}` | User to remove does not exist, or not in team. |
> | `404`         | `application/json`                | `{"code":"404","message":"Team does not exist"}` | Team does not exist. |
> | `405`         | `text/html;charset=utf-8`         | None | Invalid HTTP method. |

##### Example cURL

> ```bash
> curl -X DELETE \
>  https://opm-api.propersi.me/api/v1/teams/1/members/1 \
>  -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
> ```

</details>

------------------------------------------------------------------------------------------
