# Spring Boot Authentication Service

A comprehensive **Authentication & Authorization Service** built with **Spring Boot 3.5.9** that provides secure, stateless authentication for modern applications.

## 🚀 Features

### Core Authentication
- ✅ **JWT-based authentication** with access & refresh tokens
- ✅ **User registration & login** with email/password
- ✅ **Refresh token rotation** for enhanced security
- ✅ **Secure logout** with token revocation
- ✅ **Password encryption** using BCrypt
- ✅ **Role-based access control** (USER/ADMIN roles)

### OAuth2 Integration
- ✅ **Google OAuth2 login** with automatic user creation
- ✅ **Seamless OAuth2 to JWT conversion** for unified authorization
- 🔧 **GitHub OAuth2** (code ready, requires configuration)

### Security Features
- ✅ **Stateless authentication** (no server-side sessions)
- ✅ **JWT token validation** with custom authentication filter
- ✅ **CORS configuration** for cross-origin requests
- ✅ **Request validation** with Bean Validation
- ✅ **Exception handling** with custom authentication entry point

## 🛠 Tech Stack

- **Java 17** - Modern Java LTS version
- **Spring Boot 3.5.9** - Latest Spring Boot framework
- **Spring Security 6** - Comprehensive security framework
- **Spring Data JPA** - Database abstraction layer
- **JWT (JJWT 0.11.5)** - JSON Web Token implementation
- **OAuth2 Client** - OAuth2 integration for Google/GitHub
- **H2 Database** - In-memory database for development
- **Lombok** - Boilerplate code reduction
- **Bean Validation** - Request/Response validation
- **Maven** - Dependency management and build tool

## 🏗 Architecture Overview

### Database Design
- **Users Table**: Stores user credentials and profile information
- **Roles Table**: Defines user roles (USER, ADMIN)
- **User_Roles Table**: Many-to-many relationship between users and roles
- **Refresh_Tokens Table**: Stores refresh tokens with expiration tracking

### Security Architecture
- **Stateless JWT authentication** - No server-side session storage
- **Short-lived access tokens** (configurable expiration)
- **Long-lived refresh tokens** stored in database with automatic cleanup
- **Role-based authorization** with method-level security
- **OAuth2 integration** for seamless third-party authentication
- **Password hashing** using BCrypt with configurable strength

### Key Components
- **JwtProvider**: Handles JWT token generation and validation
- **JwtAuthenticationFilter**: Intercepts requests and validates JWT tokens
- **CustomUserDetailsService**: Loads user details for authentication
- **CustomOAuth2UserService**: Handles OAuth2 user information processing
- **RefreshTokenService**: Manages refresh token lifecycle

## 🔐 Authentication Flows

### Username & Password Authentication
1. **Registration**: `POST /api/auth/register`
   - User provides email, password, and fullName
   - Password is hashed using BCrypt
   - Default USER role is assigned
   - Returns 201 Created on success

2. **Login**: `POST /api/auth/login`
   - User provides email and password
   - Server validates credentials
   - Returns JWT access token and refresh token
   ```json
   {
     "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
     "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
     "tokenType": "Bearer"
   }
   ```

3. **Token Usage**:
   - Include access token in Authorization header: `Bearer <token>`
   - When access token expires, use refresh token to get new tokens

4. **Logout**: `POST /api/auth/logout`
   - Invalidates the refresh token
   - Client should discard access token

### OAuth2 Authentication Flow
1. **Initiate OAuth2**: `GET /oauth2/authorization/google`
   - Redirects to Google OAuth2 consent screen
   - User authorizes the application

2. **Callback Processing**:
   - Google redirects back with authorization code
   - Server exchanges code for user information
   - User account is created automatically if doesn't exist
   - Server generates internal JWT tokens

3. **Token Response**:
   - Same JWT token structure as username/password flow
   - OAuth2 is used only for initial authentication
   - All subsequent API calls use JWT tokens

### Token Refresh Flow
1. **Token Refresh**: `POST /api/auth/refresh`
   ```json
   {
     "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
   }
   ```
   - Validates refresh token
   - Generates new access token and refresh token
   - Invalidates old refresh token (token rotation)

## 📋 API Endpoints

### Authentication Endpoints

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "John Doe"
}
```
**Response**: `201 Created` - User created successfully

#### User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```
**Response**: `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer"
}
```

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```
**Response**: `200 OK` - Returns new access and refresh tokens

#### Logout
```http
POST /api/auth/logout
Content-Type: application/json
Authorization: Bearer <access_token>

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```
**Response**: `200 OK` - Refresh token invalidated

### OAuth2 Endpoints
- **Google OAuth2**: `GET /oauth2/authorization/google`
- **GitHub OAuth2**: `GET /oauth2/authorization/github` *(requires configuration)*

### Protected User Endpoints

#### Get Current User Info
```http
GET /api/users/me
Authorization: Bearer <access_token>
```
**Response**: `200 OK`
```json
"Hello user@example.com"
```

## 🚀 Getting Started

### Prerequisites
- **Java 17** or later
- **Maven 3.6+**
- **Git**

### Environment Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/zanuartri/spring-auth-service.git
   cd auth-service
   ```

2. **Configure OAuth2 (Optional)**:
   
   **Google OAuth2** (Pre-configured):
   - Copy `src/main/resources/application.env` to your environment
   - Update Google OAuth2 credentials in `application.env`:
     ```env
     GOOGLE_CLIENT_ID=your-google-client-id
     GOOGLE_CLIENT_SECRET=your-google-client-secret
     ```
   
   **GitHub OAuth2** (Code ready, needs setup):
   - Add GitHub OAuth2 configuration to `application.yaml`:
     ```yaml
     spring:
       security:
         oauth2:
           client:
             registration:
               google:
                 # existing google config...
               github:
                 client-id: ${GITHUB_CLIENT_ID}
                 client-secret: ${GITHUB_CLIENT_SECRET}
                 scope:
                   - user:email
     ```
   - Add GitHub credentials to `application.env`:
     ```env
     GITHUB_CLIENT_ID=your-github-client-id
     GITHUB_CLIENT_SECRET=your-github-client-secret
     ```

3. **Run the application**:
   ```bash
   # Windows
   .\mvnw spring-boot:run
   
   # Unix/Linux/MacOS
   ./mvnw spring-boot:run
   ```

4. **Access the application**:
   - **API Base URL**: `http://localhost:8080`

### Testing the API

#### Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

#### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### Access protected endpoint:
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🔒 Security Considerations

### Authentication & Authorization
- **BCrypt Password Hashing**: Passwords are securely hashed using BCrypt with configurable strength
- **JWT Token Security**: 
  - Short-lived access tokens (default: 15 minutes)
  - Secure token signing with HMAC SHA-256
  - Automatic token expiration validation
- **Refresh Token Security**:
  - UUID-based refresh tokens stored securely in database
  - Token rotation on refresh (old tokens are invalidated)
  - Automatic cleanup of expired tokens

### API Security
- **Stateless Authentication**: No server-side session storage, fully JWT-based
- **CORS Configuration**: Properly configured for cross-origin requests
- **Input Validation**: Bean validation on all request DTOs
- **Exception Handling**: Secure error responses without sensitive information leakage
- **OAuth2 Integration**: Secure OAuth2 flow with Google (GitHub ready)

### Data Protection
- **Database Security**: User roles and permissions properly enforced
- **Environment Variables**: Sensitive OAuth2 credentials externalized
- **No Credential Hardcoding**: All secrets configurable via environment variables

### Production Considerations
- **Database Migration**: Ready for PostgreSQL/MySQL in production
- **Monitoring Ready**: Structured logging and error handling
- **Scalability**: Stateless design supports horizontal scaling
- **Rate Limiting**: Ready for integration with API Gateway

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/auth_service/
│   │   ├── AuthServiceApplication.java         # Main application class
│   │   ├── config/
│   │   │   └── PasswordConfig.java            # BCrypt password encoder configuration
│   │   ├── controller/
│   │   │   ├── AuthController.java            # Authentication endpoints
│   │   │   └── UserController.java            # User management endpoints
│   │   ├── dto/
│   │   │   ├── LoginRequest.java              # Login request DTO
│   │   │   ├── LoginResponse.java             # Login response DTO
│   │   │   ├── LogoutRequest.java             # Logout request DTO
│   │   │   ├── RefreshTokenRequest.java       # Token refresh DTO
│   │   │   └── RegisterRequest.java           # Registration request DTO
│   │   ├── model/
│   │   │   ├── User.java                      # User entity
│   │   │   ├── Role.java                      # Role entity
│   │   │   └── RefreshToken.java              # Refresh token entity
│   │   ├── repository/
│   │   │   ├── UserRepository.java            # User data access
│   │   │   ├── RoleRepository.java            # Role data access
│   │   │   └── RefreshTokenRepository.java    # Refresh token data access
│   │   ├── security/
│   │   │   ├── JwtProvider.java               # JWT token generation/validation
│   │   │   ├── JwtAuthenticationFilter.java   # JWT authentication filter
│   │   │   ├── JwtAuthenticationEntryPoint.java # Authentication error handler
│   │   │   ├── SecurityConfig.java            # Security configuration
│   │   │   ├── CustomUserDetailsService.java # User details service
│   │   │   ├── CustomOAuth2UserService.java   # OAuth2 user service
│   │   │   └── OAuth2AuthenticationSuccessHandler.java # OAuth2 success handler
│   │   └── service/
│   │       ├── AuthService.java               # Authentication business logic
│   │       └── RefreshTokenService.java       # Refresh token management
│   └── resources/
│       ├── application.yaml                   # Application configuration
│       └── application.env                    # Environment variables
└── test/
    └── java/com/example/auth_service/
        └── AuthServiceApplicationTests.java   # Basic application tests
```

## ⚙️ Configuration

### Application Properties (application.yaml)
- **Database Configuration**: H2 in-memory database settings
- **JPA Settings**: Hibernate DDL auto-update
- **OAuth2 Configuration**: Google OAuth2 client settings
- **Logging Configuration**: Spring framework logging levels

### Environment Variables (application.env)
- `GOOGLE_CLIENT_ID`: Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth2 client secret

### JWT Configuration
JWT settings are configured in the `JwtProvider` class:
- **Access Token Expiration**: 15 minutes (900 seconds)
- **Refresh Token Duration**: 7 days (604,800 seconds)
- **Secret Key**: Auto-generated HMAC SHA-256 key
- **Token Claims**: Email (subject) + user roles

### Input Validation Rules
The API enforces the following validation constraints:

**Registration (`RegisterRequest`)**:
- `email`: Must be a valid email format and not blank
- `password`: Minimum 6 characters, not blank
- `fullName`: Not blank

**Login (`LoginRequest`)**:
- `email`: Not blank
- `password`: Not blank

**Token Refresh (`RefreshTokenRequest`)**:
- `refreshToken`: Not blank (UUID format)

**Logout (`LogoutRequest`)**:
- `refreshToken`: Not blank (UUID format)

## 🚀 Future Improvements

- **Email Verification**: User email confirmation workflow
- **Password Reset**: Forgot password functionality with email recovery
- **Account Management**: Profile updates, password changes
- **Multi-Factor Authentication (MFA)**: SMS/TOTP-based 2FA
- **Token Reuse Detection**: Enhanced security monitoring
- **Rate Limiting**: Request throttling and DDoS protection
- **Admin Dashboard**: User management interface
- **Audit Logging**: Comprehensive security event logging
- **Database Migration**: Production-ready database setup (PostgreSQL/MySQL)
- **Docker Support**: Containerization for easy deployment
- **API Documentation**: Swagger/OpenAPI integration
- **Health Checks**: Application monitoring endpoints
- **Metrics**: Performance monitoring and analytics

## 🛡️ Technologies Used

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Framework** | Spring Boot | 3.5.9 | Main application framework |
| **Security** | Spring Security | 6.x | Authentication & authorization |
| **Database** | Spring Data JPA | - | Data persistence layer |
| **Database** | H2 Database | - | In-memory database (development) |
| **JWT** | JJWT | 0.11.5 | JSON Web Token implementation |
| **OAuth2** | Spring OAuth2 Client | - | OAuth2 integration |
| **Validation** | Bean Validation | - | Request/response validation |
| **Code Gen** | Lombok | - | Boilerplate code reduction |
| **Build Tool** | Maven | 3.6+ | Dependency management |
| **Java** | OpenJDK | 17 | Runtime environment |

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Zanuar Tri**  
Backend Engineer specializing in Spring Boot ecosystem

- 🔧 **Expertise**: Spring Boot, Spring Security, JWT, OAuth2, Microservices
- 🌐 **Focus**: Authentication services, API security, scalable backend solutions
- 📧 **Contact**: triromadon@gmail.com
- 🔗 **GitHub**: https://github.com/zanuartri

---

*Built with ❤️ using Spring Boot and modern security best practices*

