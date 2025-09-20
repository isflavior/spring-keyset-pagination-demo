# Spring Keyset/Cursor Pagination Demo

This project demonstrates **cursor-based (keyset) pagination** with **Spring Data JPA Scroll API**, plus:
- user-selected sorting (whitelisted, stable + unique order),
- filters via `Specification`
- forward & backward navigation with **opaque cursors** that include sort + filters.

## Build & Run

```bash
./mvnw spring-boot:run
# or with Maven installed
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2  (JDBC URL: `jdbc:h2:mem:demo`)

## Example requests

First window (newest first by default):

```
GET http://localhost:8080/api/products/scroll?limit=10
```

Filter + sort by price ascending:

```
GET http://localhost:8080/api/products/scroll?name=mouse&sort=price&direction=asc&limit=10
```

Using cursors (from the response):

```
GET /api/products/scroll?cursor=<response.cursors.next>&navigate=forward&limit=10
GET /api/products/scroll?cursor=<response.cursors.prev>&navigate=backward&limit=10
```

## Notes

- Sortable fields are marked with the @Sortable annotation.
- Sorting is stable and unique, i.e. the same sort order is always returned for the same query.
- Cursors are **opaque** Base64URL tokens containing `{ sortField, filters, keys }`.
- When filters or sort change, the backend discards any incoming cursor and starts a fresh window.
- The same sort is used for both directions; “previous” uses a **backward** keyset position built from the **first row**.
- Keyset columns (`createdAt`, `name`, `price`, `id`) are **NOT NULL** to avoid surprises.
