# Sample curl Commands

## Authors

```bash
# Create an author
curl -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"George","lastName":"Orwell","nationality":"British"}'

# Get all authors
curl "http://localhost:8080/api/authors?page=0&size=10"

# Get author by ID
curl http://localhost:8080/api/authors/1

# Update author
curl -X PUT http://localhost:8080/api/authors/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"George","lastName":"Orwell","nationality":"British","birthDate":"1903-06-25"}'

# Delete author
curl -X DELETE http://localhost:8080/api/authors/1

# Get books by author
curl http://localhost:8080/api/authors/1/books
```

## Books

```bash
# Create a book (authorId must exist)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"1984","isbn":"978-0451524935","genre":"Dystopian","publishedYear":1949,"authorId":1}'

# Get all books
curl "http://localhost:8080/api/books?page=0&size=10&sort=title&direction=asc"

# Get book by ID
curl http://localhost:8080/api/books/1

# Search books
curl "http://localhost:8080/api/books/search?genre=Dystopian"
curl "http://localhost:8080/api/books/search?title=1984"
curl "http://localhost:8080/api/books/search?publishedYear=1949"

# Update book
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Nineteen Eighty-Four","isbn":"978-0451524935","genre":"Dystopian","publishedYear":1949,"authorId":1}'

# Delete book
curl -X DELETE http://localhost:8080/api/books/1
```

## Members

```bash
# Register a member
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com","phoneNumber":"01012345678"}'

# Get all members
curl "http://localhost:8080/api/members?page=0&size=10"

# Get member by ID
curl http://localhost:8080/api/members/1

# Search members by name
curl "http://localhost:8080/api/members/search?name=jane"

# Update member
curl -X PUT http://localhost:8080/api/members/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","email":"janesmith@example.com"}'

# Delete member
curl -X DELETE http://localhost:8080/api/members/1
```

## Borrow Records

```bash
# Borrow a book
curl -X POST http://localhost:8080/api/borrow-records \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"memberId":1}'

# Return a book
curl -X PUT http://localhost:8080/api/borrow-records/1/return

# Get borrow records for a member
curl http://localhost:8080/api/borrowrecords/member/1

# Get all currently borrowed books
curl http://localhost:8080/api/borrow-records/active
```