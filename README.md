## Tiny Bank

## Versions
* Maven 3.9.3
* Java 21
* Spring Boot 3.4.1

## Development
The application uses Spring Boot to ease REST API development and testing processes. 
Swagger UI is available at http://localhost:8080/swagger-ui/index.html#/, making manual testing of the REST API straightforward.

To run the service, launch the `TinyBankApplication`.

## Assumptions
* Multiple Accounts: A user can have multiple accounts.
* Internal Transfers: Users can transfer money between their own accounts.
* Account Creation: When a user is created, they have no accounts by default. Accounts can be added individually or in bulk after the user is created.
* User Identification: Users are identified by their NIN (National Identification Number). While each user also has a UUID, the NIN is used for indexing to simplify operations.
* Repository Design: The repository consists of a map of users. For simplicity, no separate map was created for accounts. This avoids the added complexity of handling atomic writes across both user and account repositories.

## Testing
* Unit Tests: Unit tests are implemented for both the domain and repository packages.
* API Package Unit Testing: Due to time constraints, the API layer does not include unit tests.
* Integration/Feature Tests: `TinyBankApplicationTests` provides a test suite with basic test scenarios for each endpoint created.