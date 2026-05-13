Let's make a fake jira platform

### Entities

* User
* Organization
* Project
* Board
* Sprint
* Ticket
* Comment
* Attachment
* Label
* ActivityLog

### Relationships

```
Organization 1---* Project
Project 1---* Sprint
Sprint 1---* Ticket
Ticket *---* Label
Ticket 1---* Comment
Ticket *---1 User (assignee)
Ticket *---1 User (reporter)
```

### Features

* complex jpa mappings
* Audits with Envers `GET /tickets/{id}/history`
  * ticket status
  * assignee
  * priority
* pagination, filtering, criteria query
* soft deletion
* optimistic locking
* async events
* caching
* security rules
* maybe natural language search using elasticsearch
* @EntityGraph, fetch joins, batch fetching
