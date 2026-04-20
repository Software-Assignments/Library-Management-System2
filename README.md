# Library Management System

A RESTful API backend for managing a library — books, authors, members, and borrowing records — built with Spring Boot, Spring Data JPA, H2, MapStruct, and Lombok.

## Group

See [GROUPS.md](GROUPS.md) for group number and member names.

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- JDK 21

## Build & Run

```bash
# Clone the repo
git clone https://github.com/Software-Assignments/Library-Management-System2.git
cd Library-Management-System2

# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

The API will start on **http://localhost:8080**

## H2 Console

Once running, access the embedded database at:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: *(leave blank)*

## Testing

A Postman collection is available at `library_api.postman_collection.json` in the root of the repository.
Alternatively, see [CURL_COMMANDS.md](CURL_COMMANDS.md) for sample curl commands.

## API Endpoints

### Authors — `/api/authors`
| Method | Path | Description |
|--------|------|-------------|
| GET | /api/authors | Get all authors (paginated & sortable) |
| GET | /api/authors/{id} | Get author by ID |
| POST | /api/authors | Create new author |
| PUT | /api/authors/{id} | Update author |
| DELETE | /api/authors/{id} | Delete author |
| GET | /api/authors/{id}/books | Get all books by author |

### Books — `/api/books`
| Method | Path | Description |
|--------|------|-------------|
| GET | /api/books | Get all books (paginated & sortable) |
| GET | /api/books/{id} | Get book by ID (includes author) |
| POST | /api/books | Create new book |
| PUT | /api/books/{id} | Update book |
| DELETE | /api/books/{id} | Delete book |
| GET | /api/books/search | Search by title, genre, publishedYear |

### Members — `/api/members`
| Method | Path | Description |
|--------|------|-------------|
| GET | /api/members | Get all members (paginated) |
| GET | /api/members/{id} | Get member by ID |
| GET | /api/members/search | Search by name |
| POST | /api/members | Register member |
| PUT | /api/members/{id} | Update member |
| DELETE | /api/members/{id} | Delete member |

### Borrow Records — `/api/borrow-records`
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/borrow-records | Borrow a book |
| PUT | /api/borrow-records/{id}/return | Return a book |
| GET | /api/borrowrecords/member/{memberId} | Get records for a member |
| GET | /api/borrow-records/active | Get all currently borrowed books |


## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (delete) |
| 400 | Bad Request (validation failure) |
| 404 | Not Found |
| 409 | Conflict (duplicate ISBN/email, book already borrowed) |
| 500 | Internal Server Error |

## N+1 Analysis

**Affected endpoint:** `GET /api/books` — with `FetchType.LAZY` on the `Book.author` relationship, naively iterating over all books to return their author details would trigger one extra SQL query *per book* (N books = N+1 total queries).

**Resolution:** `BookRepository.findAllWithAuthor(Pageable pageable)` uses a JPQL `JOIN FETCH b.author` clause, which causes Hibernate to load all books *and* their authors in a single SQL JOIN query, completely eliminating the N+1 problem. The same pattern is applied in `findByIdWithAuthor`, `findByAuthorIdWithAuthor`, and `searchBooks`.

