# Spring Boot DevOps Pipeline with Complete CI/CD Integration

This project demonstrates a comprehensive enterprise-grade CI/CD pipeline for a Spring Boot application (`Kaddem`) using Jenkins, Docker, SonarQube, Nexus, Prometheus, Grafana, Keycloak, and MySQL.

## Architecture Overview

```
GitHub → Jenkins → SonarQube → Nexus → Docker Registry → Docker Compose → Monitoring
   ↓         ↓         ↓         ↓           ↓              ↓            ↓
Private    Build    Quality   Artifact   Container     Deployment   Observability
 Repo     Pipeline   Gate     Storage     Registry      Platform     & Security
```

## Project Structure

```
├── src/                     # Spring Boot application source code
│   ├── main/java/           # Java source files
│   ├── main/resources/      # Application properties and resources
│   └── test/                # Unit and integration tests
├── jenkins/                 # Jenkins configuration files
│   ├── Dockerfile          # Custom Jenkins Docker image
│   └── plugins.txt         # Jenkins plugins list
├── grafana/                 # Grafana dashboards and configuration
├── nexus/                   # Nexus repository configuration
├── prometheus/              # Prometheus monitoring configuration
├── .env                     # Environment variables
├── Dockerfile              # Spring Boot application Docker image
├── Jenkinsfile             # Jenkins pipeline configuration
├── docker-compose.yml      # Main DevOps stack configuration
├── pom.xml                 # Maven project configuration
├── settings.xml            # Maven settings for Nexus deployment
├── mvnw                    # Maven wrapper (Unix)
├── mvnw.cmd                # Maven wrapper (Windows)
├── kaddem.iml              # IntelliJ IDEA module file
├── HELP.md                 # Spring Boot help documentation
└── README.md               # This file
```

## CI/CD Pipeline Flow

### Pipeline Stages:

1. **Checkout**: Clones code from private GitHub repository (`youssef-2alino1` branch)
2. **Build**: Compiles Spring Boot application with Maven
3. **Unit Tests**: Runs JUnit tests and generates reports
4. **Code Coverage**: Generates JaCoCo coverage reports
5. **SonarQube Analysis**: Performs static code analysis and quality gates
6. **Deploy to Nexus**: Publishes Maven artifacts to Nexus repository
7. **Build Docker Image**: Creates containerized application image
8. **Push to Nexus Docker Registry**: Stores Docker image in private registry
9. **Push to Docker Hub**: Stores Docker image in public registry
10. **Deploy with Docker Compose**: Deploys application with MySQL database

### Key Features:
- **Private GitHub Integration** with Personal Access Token
- **Automated Testing** with JUnit and JaCoCo coverage
- **Code Quality Gates** with SonarQube
- **Artifact Management** with Nexus Repository
- **Container Registry** with Nexus Docker Registry
- **Database Integration** with MySQL
- **Monitoring Stack** with Prometheus & Grafana
- **Authentication** with Keycloak SSO
- **Spring Boot Actuator** metrics exposure

## Technology Stack

### Core Application:
- **Spring Boot** - Java web application framework
- **Maven** - Build and dependency management
- **MySQL** - Database
- **JUnit** - Unit testing
- **JaCoCo** - Code coverage

### DevOps Tools:
- **Jenkins** - CI/CD automation server
- **Docker & Docker Compose** - Containerization
- **SonarQube** - Code quality analysis
- **Nexus Repository** - Artifact and Docker registry
- **Prometheus** - Metrics collection
- **Grafana** - Monitoring dashboards
- **Keycloak** - Identity and access management

## Prerequisites

- Docker and Docker Compose installed
- Git configured with access to private repositories
- GitHub Personal Access Token with `repo` scope
- Minimum 8GB RAM for running all services

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/chyrazz/Projet_Devops.git
cd Projet_Devops
```

### 2. Environment Setup
```bash
# Copy and configure environment variables
cp .env.example .env
# Edit .env with your specific configurations
```

### 3. Start DevOps Stack
```bash
# Start all services except backend-app & DB (will be launched by jenkines stage)
docker-compose up jenkins sonarqube nexus grafana prometheus keycloak -d

# Check services status
docker-compose ps
```

### 4. Access Services
- **Jenkins**: http://localhost:8080
- **SonarQube**: http://localhost:9000
- **Nexus**: http://localhost:8081
- **Grafana**: http://localhost:3000
- **Keycloak**: http://localhost:8090
- **Application**: http://localhost:8089/kaddem

## Configuration

### Jenkins Setup

1. **Access Jenkins** at http://localhost:8080
2. **Install Required Plugins**:
   - Git Plugin
   - Maven Integration Plugin
   - SonarQube Scanner Plugin
   - Nexus Artifact Uploader
   - Docker Pipeline Plugin
   - JaCoCo Plugin

3. **Configure Global Tools**:
   - **JDK**: Name `JDK8`, Auto-install OpenJDK 8
   - **Maven**: Name `Maven`, Auto-install Maven 3.8.x

4. **Add Credentials**:
   ```
   ID: github-credentials
   Type: Username with password
   Username: [your-github-username]
   Password: [your-github-personal-access-token]
   
   ID: nexus-credentials
   Type: Username with password
   Username: admin
   Password: admin123
   
   ID: nexus-docker-credentials
   Type: Username with password
   Username: admin
   Password: admin123
   
   ID: sonar-credentials
   Type: Secret text
   Secret: [your-sonarqube-token]
   ```

### GitHub Personal Access Token

1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Generate new token with scopes:
   - **repo** (Full control of private repositories)
   - **read:org** (Read org and team membership)

### SonarQube Configuration

1. Access SonarQube at http://localhost:9000
2. Default credentials: `admin/admin`
3. Create new project: `kaddem`
4. Generate authentication token
5. Configure quality gates and rules

### Nexus Repository Setup

1. Access Nexus at http://localhost:8081
2. Default credentials: `admin/admin123`
3. Repositories are auto-configured:
   - **Maven**: `maven-snapshots`
   - **Docker**: `docker-registry` (port 8082)

### Keycloak Authentication

1. Access Keycloak at http://localhost:8090
2. Admin credentials: `admin/admin123`
3. Realm: `devops`
4. Pre-configured users:
   - **admin** (admin role)
   - **editor** (editor role)

## Pipeline Configuration

### Environment Variables
```groovy
NEXUS_URL = "nexus:8081"
SONARQUBE_URL = "http://sonarqube:9000"
PROJECT_KEY = "kaddem"
DOCKER_IMAGE_NAME = "kaddem"
SOURCE_CODE_PATH = "${WORKSPACE}/"
```

### Branch Configuration
- **Target Branch**: `youssef-2alino1`
- **Repository**: `https://github.com/chyrazz/Projet_Devops.git`

## Monitoring & Observability

### Prometheus Metrics
- Application metrics: http://localhost:8089/kaddem/actuator/prometheus
- JVM metrics, database connections, custom business metrics

### Grafana Dashboards
- **Spring Boot Dashboard**: Application performance metrics
- **Infrastructure Dashboard**: Docker containers, system resources
- **Jenkins Dashboard**: Build metrics and pipeline performance

### Keycloak SSO Integration
- Single Sign-On for Grafana
- Role-based access control
- Centralized user management

## Docker Services

| Service | Port | Purpose |
|---------|------|---------|
| Jenkins | 8080 | CI/CD Pipeline |
| SonarQube | 9000 | Code Quality |
| Nexus | 8081 | Artifact Repository |
| Nexus Docker | 8082 | Docker Registry |
| Grafana | 3000 | Monitoring Dashboards |
| Prometheus | 9090 | Metrics Collection |
| Keycloak | 8090 | Authentication |
| MySQL | 3306 | Database |
| Application | 8089 | Spring Boot App |

## Troubleshooting

### Common Issues

1. **GitHub Authentication Failed**:
   - Verify Personal Access Token has `repo` scope
   - Check credentials ID matches `github-credentials`

2. **SonarQube Analysis Failed**:
   - Verify SonarQube token is valid
   - Check project key configuration

3. **Docker Build Failed**:
   - Ensure Docker daemon is running
   - Check Dockerfile syntax in Spring Boot project

4. **Nexus Deployment Failed**:
   - Verify Nexus credentials
   - Check Maven settings.xml configuration

### Logs and Debugging
```bash
# View Jenkins logs
docker-compose logs jenkins

# View application logs
docker-compose logs backend-app

# View all services
docker-compose logs -f
```

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Next Steps

- [ ] Add integration tests
- [ ] Implement blue-green deployment
- [ ] Add security scanning with OWASP
- [ ] Configure backup strategies
- [ ] Add performance testing with JMeter
- [ ] Implement GitOps with ArgoCD

---

**Built with ❤️ by YHbibi for DevOps Excellence**
