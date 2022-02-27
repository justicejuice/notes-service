# Notes Service

Thist Tutorial shows how to secure a small Rest API with Spring Security and JWT.
The Rest API consists of a very simple datamodel:

1. User     -> stores user information.
2. notes    -> stores notes created by an user.

# API Definition
We have some unprotected REST Endoints to allow an user to login or register:

|Path         |Method|Parameters             |Result|Description      |
|-------------|------|-----------------------|------|-----------------|
|api/v1/users/|POST  |email, name, password  |User  |Registers an user|
|api/v1/users/|GET   |email, password        |User  |Logs in an user  |

We also have some resources we want to protect. So just an authenticated
AND authorized user can access these resources:

|Path                            |Method|Parameters              |Result        |Description           |
|--------------------------------|------|------------------------|--------------|----------------------|
|api/v1/users/{id}               |DELETE|id                      |empty         |Registers an user     |
|api/v1/users/{id}/notes         |GET   |id of user              |List of notes |All notes of an user  |
|api/v1/users/{id}/notes/{noteId}|GET   |id of user, noteId      |note          |A notes of an user    |
|api/v1/users/{id}/notes         |POST  |id of user, note        |created note  |Creates a new note    |
|api/v1/users/{id}/notes/{noteId}|PUT   |id of user, noteId, note|updated note  |Updates a given note  |
|api/v1/users/{id}/notes/{noteId}|DELETE|id of user, noteId      |empty         |Deletes a note        |
