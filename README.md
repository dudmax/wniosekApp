# Aplikacja do zarządzania wnioskami
Aplikacja realizuje API odpowiedzialne za obsługę żądań HTTP związanych z aplikacjami. Zapewnia różne punkty końcowe do zarządzania aplikacjami.

- Aplikacja realizuje API do zarzadzania wnioskami zgodnie z zadaniem i diagramem stanow
- Podczas dodawania nowego wniosku konieczne jest podanie jego nazwy oraz treści
- Treść wniosku może się zmieniać tylko przy stanach 'CREATED' oraz 'VERIFIED'
- Podczas usuwania/odrzucania wniosku konieczne jest podanie przyczyny
- Konieczne jest przechowywanie pełnej historii zmian stanu wniosku
- Jest możliwość przeglądania wniosków (domyślna paginacja 10 wierszy) wraz z
- Jest możliwość filtrowania po nazwie i stanie
- W momencie publikacji dokumentu zostaje mu nadany unikalny numer (numeryczny)
- Utworzony REST'owe endpoint'y
- Wnioski przechowywane w bazie danych (H2), struktura bazy znajduie sie w plike schema.sql
- Dodany testy dla ApplicationController i ApplicationService
## Technologii
Java 17, Spring Boot, Spring MVC, Spring Data JPA, REST API, jUnit 5, Lombok, H2

# API Reference
## Get Applications

- **URL:** /applications
- **Method:** GET
- **Description:** Retrieve a list of applications with optional filtering by name and state.
- **Parameters:**
    - `page` (optional): The page number (default is 0).
    - `size` (optional): The number of items per page (default is 10).
    - `name` (optional): Filter applications by name.
    - `state` (optional): Filter applications by state.
- **Responses:**
    - 200 OK: Returns a list of applications as a pageable response.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Get Application by ID

- **URL:** /applications/{id}
- **Method:** GET
- **Description:** Retrieve an application by its ID.
- **Parameters:**
    - `id`: The ID of the application to retrieve.
- **Responses:**
    - 200 OK: Returns the application details.
    - 404 Not Found: If the application with the given ID is not found.

## Add Application

- **URL:** /applications
- **Method:** POST
- **Description:** Create a new application.
- **Request Body:** An ApplicationDTO representing the application to be created. This request must include the following required fields:
    - `name` (string): The name of the application (required).
    - `description` (string): A description of the application (required).
- **Responses:**
    - 200 OK: Returns the created application.
    - 400 Bad Request: If application title or description is not provide.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Update Application

- **URL:** /applications/{id}
- **Method:** PUT
- **Description:** Update the description of an application.
- **Parameters:**
    - `id`: The ID of the application to update.
- **Request Body:** A string representing the new description.
- **Responses:**
    - 200 OK: Returns the updated application.
    - 400 Bad Request: If the application is not in the correct state for updating the description.
    - 404 Not Found: If the application with the given ID is not found.

## Delete Application

- **URL:** /applications/{id}
- **Method:** DELETE
- **Description:** Delete an application with a reason.
- **Parameters:**
    - `id`: The ID of the application to delete.
- **Request Parameters:**
    - `reason`: The reason for deleting the application.
- **Responses:**
    - 204 No Content: Indicates a successful deletion.
    - 400 Bad Request: If the application is not in the correct state or if the reason is missing.
    - 404 Not Found: If the application with the given ID is not found.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Verify Application

- **URL:** /applications/{id}/verify
- **Method:** POST
- **Description:** Verify an application.
- **Parameters:**
    - `id`: The ID of the application to verify.
- **Responses:**
    - 200 OK: Returns the verified application.
    - 400 Bad Request: If the application is not in the correct state for verification.
    - 404 Not Found: If the application with the given ID is not found.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Accept Application

- **URL:** /applications/{id}/accept
- **Method:** POST
- **Description:** Accept an application.
- **Parameters:**
    - `id`: The ID of the application to accept.
- **Responses:**
    - 200 OK: Returns the accepted application.
    - 400 Bad Request: If the application is not in the correct state for acceptance.
    - 404 Not Found: If the application with the given ID is not found.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Publish Application

- **URL:** /applications/{id}/publish
- **Method:** POST
- **Description:** Publish an application.
- **Parameters:**
    - `id`: The ID of the application to publish.
- **Responses:**
    - 200 OK: Returns the published application.
    - 400 Bad Request: If the application is not in the correct state for publishing.
    - 404 Not Found: If the application with the given ID is not found.
    - 500 Internal Server Error: If have some problem (a state is not found).

## Reject Application

- **URL:** /applications/{id}/reject
- **Method:** POST
- **Description:** Reject an application with a reason.
- **Parameters:**
    - `id`: The ID of the application to reject.
- **Request Parameters:**
    - `reason`: The reason for rejecting the application.
- **Responses:**
    - 200 OK: Returns the rejected application.
    - 400 Bad Request: If the application is not in the correct state or if the reason is missing.
    - 404 Not Found: If the application with the given ID is not found.
    - 500 Internal Server Error: If have some problem (a state is not found).
