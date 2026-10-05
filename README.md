# ⚡ AI Code Reviewer

A Spring Boot web app that uses **Groq LLaMA** to review code snippets and return structured feedback on bugs, code quality, and improvements — with a dark-themed browser UI.

> Built as a resume project to demonstrate: Spring Boot, REST API design, AI/LLM integration, prompt engineering, and front-end integration.

---

## Demo

Open `http://localhost:8080` in your browser after starting the app:

- Paste any code snippet
- Select the language (Java, Python, JavaScript, and more)
- Click **Review My Code** — get back colour-coded AI feedback in seconds

---

## Features

- 🌐 Dark-themed browser UI — no external frameworks, pure HTML/CSS/JS
- 🤖 AI-powered feedback via Groq's LLaMA model
- 🐛 Structured response: summary, bugs, quality issues, improvements, overall rating
- 🌍 Language-agnostic — works with any programming language
- ✅ Input validation with clear error messages
- 🏥 Health check endpoint

---

## Tech Stack

| Layer       | Technology                              |
|-------------|-----------------------------------------|
| Framework   | Spring Boot 3.2 (Java 17)               |
| AI          | Groq API — LLaMA (`openai/gpt-oss-20b`) |
| HTTP Client | Java built-in `HttpClient`              |
| Front-end   | HTML / CSS / Vanilla JS                 |
| Build       | Maven                                   |
| Testing     | JUnit 5 + MockMvc                       |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- A free [Groq API key](https://console.groq.com/keys) (no credit card required)

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/ai-code-reviewer.git
cd ai-code-reviewer
```

### 2. Set your Groq API key

**Option A — environment variable (recommended):**

PowerShell:
```powershell
$env:GROQ_API_KEY = "gsk_..."
```

Bash/Mac/Linux:
```bash
export GROQ_API_KEY=gsk_...
```

**Option B — edit `application.properties` directly:**
```properties
groq.api.key=gsk_...
```

### 3. Run

```powershell
mvn spring-boot:run
```

Open **[http://localhost:8080](http://localhost:8080)** in your browser.

---

## API Usage

The UI calls the REST API under the hood. You can also call it directly.

### Health Check

```
GET /api/health
```
```
200 OK  →  "AI Code Reviewer is running!"
```

### Review a Code Snippet

```
POST /api/review
Content-Type: application/json
```

**Request:**
```json
{
  "code": "public int add(int a, int b) { return a - b; }",
  "language": "Java"
}
```

**Response:**
```json
{
  "summary": "Method named 'add' incorrectly performs subtraction.",
  "bugs": [
    "The method returns a - b instead of a + b, causing incorrect behavior."
  ],
  "codeQualityIssues": [
    "Method name does not match its implementation.",
    "No Javadoc documentation."
  ],
  "improvements": [
    "Rename the method to 'subtract' or fix the implementation to return a + b.",
    "Add Javadoc comments.",
    "Include unit tests to verify correct behavior."
  ],
  "overallRating": "Needs Work"
}
```

---

## Running Tests

```bash
mvn test
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/codereviewer/
│   │   ├── AiCodeReviewerApplication.java   # Entry point
│   │   ├── controller/
│   │   │   └── CodeReviewController.java    # REST endpoints
│   │   ├── model/
│   │   │   ├── CodeReviewRequest.java       # Input model
│   │   │   └── CodeReviewResponse.java      # Output model
│   │   └── service/
│   │       └── CodeReviewService.java       # Groq API + prompt engineering
│   └── resources/
│       ├── static/
│       │   └── index.html                   # Browser UI
│       └── application.properties
└── test/java/com/codereviewer/
    └── controller/
        └── CodeReviewControllerTest.java
```

---

## Ideas for Extension

- Persist review history with Spring Data JPA + H2
- Support streaming responses via SSE
- Add rate limiting with Resilience4j
- Deploy to Railway, Render, or AWS Elastic Beanstalk

---

## License

MIT
