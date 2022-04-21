# Notes Service

Thist Tutorial shows how to secure a small Rest API with Spring Security and JWT.
The Rest API consists of a very simple datamodel:

1. User     -> stores user information.
2. notes    -> stores notes created by an user.

# API Definition
We have some unprotected REST Endoints to allow an user to login or register:

|Path                 |Method|Parameters             |Result  |Description      |
|---------------------|------|-----------------------|--------|-----------------|
|api/v1/users/register|POST  |email, name, password  |UserView|Registers an user|
|api/v1/users/login   |POST  |email, password        |UserView|Logs in an user  |

We also have some resources we want to protect. So just an authenticated
AND authorized user can access these resources:

| Path                  |Method| Parameters   |Result        | Description                                 |
|-----------------------|------|--------------|--------------|---------------------------------------------|
| api/v1/users/{id}     |DELETE| id           |empty         | Deletes an user                             |
| api/v1/notes          |GET   |              |List of notes | All notes of authenticated user             |
| api/v1/notes/{noteId} |GET   | noteId       |note          | Specific note of authenticated user         |
| api/v1/notes          |POST  | note         |created note  | Creates a new note for authenticated user   |
| api/v1/notes/{noteId} |PUT   | noteId, note |updated note  | Updates a given note for authenticated user |
| api/v1/notes/{noteId} |DELETE| noteId       |empty         | Deletes a note of authenticated user        |
