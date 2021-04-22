# LuckyDrawGamingService

API ENDPOINT: https://lucky-draw-gaming-service.herokuapp.com/

The backend API service which is built using Spring Boot in Java provides the following method calls:

| Method  | API URL | Operation |
| ------------- | ------------- | ------------ |
| POST   | /register | Registers user and returns User ID | 
| GET    | /raffle-ticket/{userId}     | Generates a raffle-ticket for a user for participation in a lucky draw event |
| POST | /participate | Allows user with raffle-ticket to participate in an event |
| GET | /winners | Gets winners of all events in the last one week |
| POST | /winner | Computes Winners for an event [Access only to admin through ADMIN_KEYS] |
| GET | /events | Gets event details for past and upcoming week |

### Example
