```mermaid
sequenceDiagram
    participant User as User
    participant REST API as REST API
    participant external service as message broker
    Note over User, external service: send e-mail
    User ->>+ REST API: POST /emails {"subject": "New ...", "sender": "...", "recipient": "..."}
    REST API ->>+ external service: POST /topics/pl.allegro.[...].email {"subject": "New ...", "sender": "...", "recipient": "..."}
    external service -->>- REST API: response
    REST API -->>- User: response
```
