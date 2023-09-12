```mermaid
sequenceDiagram
    participant User as User
    participant REST API as REST API
    participant external service as external e-mail REST service
    Note over User, external service: send e-mail
    User ->>+ REST API: POST /emails {"subject": "New ...", <br/>"sender": "...", "recipient": "..."}
    REST API ->>+ external service: POST /external-api-service/emails <br/>{"subject": "New ...", "sender": "...", "recipient": "..."}
    external service -->>- REST API: response
    REST API -->>- User: response
```
