package com.example.library;

import com.example.library.dto.request.AuthorRequest;
import com.example.library.dto.request.BookRequest;
import com.example.library.dto.request.MemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────
    // AUTHOR TESTS
    // ─────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("POST /api/authors - Create author successfully")
    void createAuthor_success() throws Exception {
        AuthorRequest req = AuthorRequest.builder()
                .firstName("George").lastName("Orwell").nationality("British").build();

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(jsonPath("$.lastName").value("Orwell"))
                .andExpect(jsonPath("$.nationality").value("British"));
    }

    @Test @Order(2)
    @DisplayName("POST /api/authors - Fail when firstName missing")
    void createAuthor_missingFirstName() throws Exception {
        AuthorRequest req = AuthorRequest.builder().lastName("Orwell").build();

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test @Order(3)
    @DisplayName("GET /api/authors - Get all authors with pagination")
    void getAllAuthors() throws Exception {
        // Create one first
        AuthorRequest req = AuthorRequest.builder()
                .firstName("Jane").lastName("Austen").build();
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/authors").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test @Order(4)
    @DisplayName("GET /api/authors/{id} - Get author by ID")
    void getAuthorById() throws Exception {
        AuthorRequest req = AuthorRequest.builder()
                .firstName("Mark").lastName("Twain").build();
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test @Order(5)
    @DisplayName("GET /api/authors/{id} - 404 when not found")
    void getAuthorById_notFound() throws Exception {
        mockMvc.perform(get("/api/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test @Order(6)
    @DisplayName("PUT /api/authors/{id} - Update author")
    void updateAuthor() throws Exception {
        AuthorRequest create = AuthorRequest.builder()
                .firstName("Old").lastName("Name").build();
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        AuthorRequest update = AuthorRequest.builder()
                .firstName("New").lastName("Name").nationality("Egyptian").build();
        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.nationality").value("Egyptian"));
    }

    @Test @Order(7)
    @DisplayName("DELETE /api/authors/{id} - Delete author")
    void deleteAuthor() throws Exception {
        AuthorRequest req = AuthorRequest.builder()
                .firstName("Delete").lastName("Me").build();
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // BOOK TESTS
    // ─────────────────────────────────────────────

    private void createTestAuthor() throws Exception {
        AuthorRequest req = AuthorRequest.builder()
                .firstName("George").lastName("Orwell").build();
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }

    @Test @Order(8)
    @DisplayName("POST /api/books - Create book successfully")
    void createBook_success() throws Exception {
        createTestAuthor();
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("978-0451524935").genre("Dystopian")
                .publishedYear(1949).authorId(1L).build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.isbn").value("978-0451524935"))
                .andExpect(jsonPath("$.author.firstName").value("George"));
    }

    @Test @Order(9)
    @DisplayName("POST /api/books - Fail on duplicate ISBN")
    void createBook_duplicateIsbn() throws Exception {
        createTestAuthor();
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("DUPLICATE-ISBN").genre("Dystopian")
                .publishedYear(1949).authorId(1L).build();
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // Try same ISBN again
        BookRequest req2 = BookRequest.builder()
                .title("Animal Farm").isbn("DUPLICATE-ISBN").genre("Satire")
                .publishedYear(1945).authorId(1L).build();
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test @Order(10)
    @DisplayName("POST /api/books - Fail when author not found")
    void createBook_authorNotFound() throws Exception {
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("978-0451524935").authorId(999L).build();
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test @Order(11)
    @DisplayName("GET /api/books - Get all books paginated")
    void getAllBooks() throws Exception {
        createTestAuthor();
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("978-0451524935").authorId(1L).build();
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/books").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].author").isNotEmpty());
    }

    @Test @Order(12)
    @DisplayName("GET /api/books/search - Search by title and genre")
    void searchBooks() throws Exception {
        createTestAuthor();
        BookRequest r1 = BookRequest.builder().title("1984").isbn("ISBN-001").genre("Dystopian").publishedYear(1949).authorId(1L).build();
        BookRequest r2 = BookRequest.builder().title("Animal Farm").isbn("ISBN-002").genre("Satire").publishedYear(1945).authorId(1L).build();
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)));
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)));

        mockMvc.perform(get("/api/books/search").param("genre", "Dystopian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("1984"));

        mockMvc.perform(get("/api/books/search").param("title", "farm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Animal Farm"));

        mockMvc.perform(get("/api/books/search").param("publishedYear", "1949"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test @Order(13)
    @DisplayName("GET /api/authors/{id}/books - Get books by author")
    void getBooksByAuthor() throws Exception {
        createTestAuthor();
        BookRequest r1 = BookRequest.builder().title("1984").isbn("ISBN-A").genre("Dystopian").authorId(1L).build();
        BookRequest r2 = BookRequest.builder().title("Animal Farm").isbn("ISBN-B").genre("Satire").authorId(1L).build();
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)));
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)));

        mockMvc.perform(get("/api/authors/1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test @Order(14)
    @DisplayName("PUT /api/books/{id} - Update book")
    void updateBook() throws Exception {
        createTestAuthor();
        BookRequest create = BookRequest.builder().title("Old Title").isbn("ISBN-OLD").authorId(1L).build();
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(create)));

        BookRequest update = BookRequest.builder().title("New Title").isbn("ISBN-NEW").genre("Fiction").authorId(1L).build();
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.genre").value("Fiction"));
    }

    @Test @Order(15)
    @DisplayName("DELETE /api/books/{id} - Delete book")
    void deleteBook() throws Exception {
        createTestAuthor();
        BookRequest req = BookRequest.builder().title("Delete Me").isbn("ISBN-DEL").authorId(1L).build();
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(delete("/api/books/1")).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/1")).andExpect(status().isNotFound());
    }
    @Test @Order(16)
    @DisplayName("POST /api/members - Register member successfully")
    void createMember_success() throws Exception {
        MemberRequest req = MemberRequest.builder()
                .firstName("Jane").lastName("Doe").email("jane@example.com").phoneNumber("01012345678").build();

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.membershipDate").isNotEmpty());
    }

    @Test @Order(17)
    @DisplayName("POST /api/members - Fail on duplicate email")
    void createMember_duplicateEmail() throws Exception {
        MemberRequest req = MemberRequest.builder()
                .firstName("Jane").lastName("Doe").email("duplicate@example.com").build();
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)));

        MemberRequest req2 = MemberRequest.builder()
                .firstName("John").lastName("Smith").email("duplicate@example.com").build();
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }

    @Test @Order(18)
    @DisplayName("POST /api/members - Fail on invalid email format")
    void createMember_invalidEmail() throws Exception {
        MemberRequest req = MemberRequest.builder()
                .firstName("Jane").lastName("Doe").email("not-an-email").build();
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test @Order(19)
    @DisplayName("GET /api/members/search - Search by name")
    void searchMembersByName() throws Exception {
        MemberRequest r1 = MemberRequest.builder().firstName("Alice").lastName("Smith").email("alice@test.com").build();
        MemberRequest r2 = MemberRequest.builder().firstName("Bob").lastName("Jones").email("bob@test.com").build();
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)));
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)));

        mockMvc.perform(get("/api/members/search").param("name", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test @Order(20)
    @DisplayName("PUT /api/members/{id} - Update member")
    void updateMember() throws Exception {
        MemberRequest create = MemberRequest.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(create)));

        MemberRequest update = MemberRequest.builder().firstName("New").lastName("Name").email("new@test.com").build();
        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test @Order(21)
    @DisplayName("DELETE /api/members/{id} - Delete member")
    void deleteMember() throws Exception {
        MemberRequest req = MemberRequest.builder().firstName("Del").lastName("User").email("del@test.com").build();
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(delete("/api/members/1")).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/members/1")).andExpect(status().isNotFound());
    }


}
