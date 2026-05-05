# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Learnify is a learning platform built with Spring Boot backend and React frontend.

**Tech Stack:**
- Backend: Java 21, Spring Boot 3.5.13, Maven, Spring Security, Spring Data JPA, H2 Database
- Authentication: JWT (jjwt 0.11.5), BCrypt password hashing
- Frontend: React 19, Vite, React Router, Axios
- Testing: JUnit 5, Mockito, MockMvc
- CI/CD: GitHub Actions

**Project Status:** ✅ COMPLETED - Full chat functionality with AI integration

## Development Commands

### Backend (Spring Boot)
```bash
# Build and run tests
mvn clean install

# Run tests only
mvn test

# Run specific test
mvn test -Dtest=AuthControllerTest

# Run application
mvn spring-boot:run

# Skip tests during build
mvn clean install -DskipTests
```

### Frontend (React)
```bash
cd frontend

# Install dependencies
npm install

# Start development server (http://localhost:5173)
npm run dev

# Build for production
npm run build
```

## Architecture

### Backend Structure
```
com.example.learning_platform/
├── config/           # Security, CORS, JWT filter, HTTP client
├── controller/       # REST API endpoints (Auth, Chat)
├── dto/             # Data Transfer Objects
├── exception/       # Global error handling
├── model/           # JPA entities
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic
└── util/           # JWT utilities
```

### Frontend Structure
```
frontend/src/
├── components/      # React components (Login, Register, Chat, ProtectedRoute)
├── contexts/        # AuthContext for authentication state
├── hooks/           # useAuth hook
├── services/        # API client with JWT interceptor
├── App.jsx          # Main app with routing
└── .env             # Environment variables
```

## Important Configuration

### Database
- H2 in-memory database: `jdbc:h2:mem:learnifydb`
- Console: http://localhost:8080/h2-console
- Username: `sa`, Password: empty
- Auto DDL: `create-drop`

### API Keys
- `GEMINI_API_KEY` - Google AI API key (set via environment variable)
- **Never commit actual API keys**

### CORS
- Backend accepts requests from `http://localhost:5173`

### JWT Configuration
- Secret key in `application.properties` as `jwt.secret`
- Expiration: 24 hours (86400000 ms)
- Tokens via `Authorization: Bearer <token>` header
- Minimum key length: 256 bits

## Testing

### Backend Tests
- Unit tests with Mockito
- Controller tests with MockMvc
- Integration tests with `@SpringBootTest`
- Test naming: `ClassNameTest.java` in `src/test/java`

### Running Tests
```bash
# All backend tests
mvn test

# All frontend tests
cd frontend
npm test

# Specific backend test
mvn test -Dtest=JwtUtilTest

# Specific frontend test
npm test -- Chat.test.jsx
```

### Test Coverage
**Backend Tests (57 tests):**
- UserServiceImplTest: 4 tests ✅
- AuthControllerTest: 6 tests ✅
- JwtUtilTest: 5 tests ✅
- AuthControllerIntegrationTest: 3 tests ✅
- ChatServiceTest: 11 tests ✅
- ChatControllerTest: 12 tests ✅
- ChatIntegrationTest: 16 tests ✅
- LearningPlatformApplicationTests: 1 test ✅

**Frontend Tests (20 tests):**
- Chat.test.jsx: 20 tests ✅
  - Rendering tests: 7 tests
  - User interaction tests: 8 tests
  - API integration tests: 5 tests

**Total Tests:** 78 tests passing (57 backend + 20 frontend + 1 application)

## Development Notes

- Package name: `com.example.learning_platform` (underscore, not hyphen)
- Password hashing: BCryptPasswordEncoder
- All API endpoints return JSON
- Validation errors: HTTP 400
- Frontend: port 5173, Backend: port 8080
- H2 data lost on restart (in-memory)
- JWT tokens expire after 24 hours

## Known Issues and Limitations

**Current Limitations:**
- H2 database loses data on restart
- No token refresh mechanism
- No chat history persistence
- No file upload support

**Security Considerations:**
- JWT secret key in application.properties (use env vars in production)
- CSRF disabled for development
- H2 console enabled (disable for production)
- No rate limiting
- Tokens in localStorage (XSS vulnerable)

## Quick Start

```bash
# Navigate to project
cd /home/nick/Git/Internship/project/learning-platform

# Verify state
git status
mvn test

# Start backend
mvn spring-boot:run

# Start frontend (new terminal)
cd frontend
npm run dev
```

**Access:**
- Backend: http://localhost:8080
- Frontend: http://localhost:5173
- H2 Console: http://localhost:8080/h2-console

## Key Files

**Backend:**
- `src/main/resources/application.properties` - Spring Boot config
- `src/main/java/com/example/learning_platform/config/SecurityConfig.java` - Security setup
- `src/main/java/com/example/learning_platform/config/JwtAuthenticationFilter.java` - JWT filter
- `src/main/java/com/example/learning_platform/util/JwtUtil.java` - JWT utilities
- `src/main/java/com/example/learning_platform/controller/AuthController.java` - Authentication
- `src/main/java/com/example/learning_platform/controller/ChatController.java` - Chat
- `src/main/java/com/example/learning_platform/service/ChatServiceImpl.java` - Chat service

**Frontend:**
- `frontend/src/App.jsx` - Main app with routing
- `frontend/src/contexts/AuthContext.jsx` - Authentication context
- `frontend/src/services/api.js` - API client with JWT interceptor
- `frontend/src/components/Chat.jsx` - Chat component
- `frontend/src/components/ProtectedRoute.jsx` - Route protection

**Tests:**
- `src/test/java/com/example/learning_platform/util/JwtUtilTest.java` - JWT tests
- `src/test/java/com/example/learning_platform/controller/AuthControllerIntegrationTest.java` - Integration tests
- `src/test/java/com/example/learning_platform/service/ChatServiceTest.java` - Chat service tests
- `src/test/java/com/example/learning_platform/controller/ChatControllerTest.java` - Chat controller tests
- `src/test/java/com/example/learning_platform/controller/ChatIntegrationTest.java` - Chat integration tests
- `frontend/src/components/Chat.test.jsx` - Chat component tests

## Environment Variables

**Backend:**
- `GEMINI_API_KEY` - Google AI API key

**Frontend:**
- `VITE_API_URL` - Backend API URL (default: http://localhost:8080)

## API Endpoints

**Public:**
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login (returns JWT token)

**Protected (requires JWT):**
- POST `/api/chat` - Chat with AI

## Current Work Plan

### AI Provider Architecture Refactoring

**Goal:** Implement flexible AI provider architecture using Strategy pattern to easily switch between different AI models (Gemini, Groq, etc.)

**Status:** 📋 Planning phase

**Implementation Steps:**

1. **Create AI Provider Interface**
   - Create `src/main/java/com/example/learning_platform/service/ai/AiProvider.java`
   - Define common interface: `sendMessage(String message, String username)` and `getProviderName()`

2. **Refactor Gemini Provider**
   - Extract current Gemini logic into `GeminiProvider` class implementing `AiProvider`
   - Make model and API URL configurable via properties
   - Keep existing DTOs (`GeminiRequest`, `GeminiResponse`)

3. **Implement Groq Provider**
   - Create `src/main/java/com/example/learning_platform/service/ai/GroqProvider.java`
   - Create Groq-specific DTOs (`GroqRequest`, `GroqResponse`)
   - Implement Groq API integration using OpenAI-compatible format
   - Make model and API URL configurable

4. **Create Provider Configuration**
   - Create `src/main/java/com/example/learning_platform/config/AiProviderConfig.java`
   - Implement provider selection logic based on configuration
   - Use `@Primary` annotation for selected provider

5. **Update Chat Service**
   - Refactor `ChatServiceImpl` to use `AiProvider` interface
   - Remove direct Gemini dependencies
   - Add logging for selected provider

6. **Update Configuration**
   - Add provider selection property: `ai.provider=groq` (or `gemini`)
   - Add Groq-specific properties: `groq.api.key`, `ai.groq.model`, `ai.groq.api-url`
   - Make Gemini properties configurable: `ai.gemini.model`, `ai.gemini.api-url`

7. **Update Tests**
   - Refactor existing tests to use `AiProvider` interface
   - Add tests for Groq provider
   - Add integration tests for provider switching

8. **Documentation**
   - Update CLAUDE.md with new architecture
   - Add examples of adding new providers
   - Document configuration options

**Benefits:**
- Easy provider switching via configuration
- Extensible architecture for new AI providers
- Better testability with interface abstraction
- Separation of concerns

**Configuration Example:**
```properties
# Provider selection
ai.provider=groq

# Gemini config
google.ai.api.key=${GEMINI_API_KEY}
ai.gemini.model=gemini-2.5-flash

# Groq config
groq.api.key=${GROQ_API_KEY}
ai.groq.model=llama-3.3-70b-versatile
```

## Future Enhancements

**Backend:**
- Chat history persistence in database
- Message search functionality
- File upload support for chat attachments
- Rate limiting implementation
- Token refresh mechanism

**Frontend:**
- WebSocket support for real-time updates
- Chat export/import functionality
- User profile management
- Additional component tests (Login, Register, ProtectedRoute)

**Security:**
- Password reset functionality
- Email verification
- Secure token storage (httpOnly cookies)
- Account lockout mechanism
