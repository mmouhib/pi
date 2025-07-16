# Project Setup Instructions

**DO NOT CHANGE THE ROOT FOLDER NAME (`pi`)**

---

## Default Credentials

- **Grafana**
  - **User:** `admin`
  - **Password:** `admin@123`
- **Nexus**
  - **User:** `admin`
  - **Password:** `admin`

---

## Requirements

- WSL or any Linux machine
- Docker and Docker Compose available in the CLI

---

## 1. Clone and Prepare Your Own Repository

1. Clone this repository:
   ```sh
   git clone https://github.com/mmouhib/pi.git
   ```
2. Delete the `.git` folder:
   ```sh
   cd pi
   rm -rf .git
   ```
3. Create a new repository on your own GitHub account.
4. Initialize a new git repo and push:
   ```sh
   git init
   git remote add origin https://github.com/{your-username}/{your-repo}.git
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git push -u origin main
   ```
5. Edit the `Jenkinsfile` and change the `git` URL to your new repository and branch (`main` by default).

---

## 2. Run the Project

```sh
docker compose up jenkins sonarqube nexus grafana prometheus -d
```

---

## 3. Add Docker Hub Credentials in Jenkins

- Go to: [Jenkins Credentials](http://localhost:8080/manage/credentials/store/system/domain/_/)
- Generate a token from [Docker Hub Security Settings](https://hub.docker.com/settings/security) ⇒ *Personal access token*
- Add credentials:
  - **ID:** `nexus-docker-credentials`
  - **Username:** `{your dockerhub username}`
  - **Password:** `{the generated token}`
- Example:  
  ![Docker Hub Credentials](docs/img_1.png)

---

## 4. Add SonarQube Credentials in Jenkins

- Go to: [Jenkins Credentials](http://localhost:8080/manage/credentials/store/system/domain/_/)
- Use the following:
  - **ID:** `sonar-credentials`
  - **Username:** `admin`
  - **Password:** `+ANJ#KKrtN4S$n_`

---

## 5. Generate a SonarQube Token

- Log in to SonarQube (port 9000).
- On first login, change the password:
  - **Old password:** `admin`
  - **New password:** `+ANJ#KKrtN4S$n_`
- Visit: [SonarQube Account Security](http://localhost:9000/account/security)
- Add your token to `pom.xml`:  
  ![Sonar Token in pom.xml](docs/img_6.png)

---

## 6. Add JDK Dependency in Jenkins

- Go to: [Jenkins Global Tool Configuration](http://localhost:8080/manage/configureTools/)
- Add JDK (e.g., JDK 8 and JDK 17)
- ![Add JDK](docs/img_3.png)
- **DO NOT FORGET TO SAVE**

---

## 7. Add Maven Dependency in Jenkins

- Go to: [Jenkins Global Tool Configuration](http://localhost:8080/manage/configureTools/)
- Add Maven
- ![Add Maven](docs/img_4.png)
- **DO NOT FORGET TO SAVE**

---

## 8. Update Docker Compose for Backend App

- In `docker-compose.yml`, change the image tag for the `backend-app` service:
  - Replace `mmouhib` with your Docker Hub username.
- ![Update Docker Compose](docs/img_9.png)

---

## 9. Add SonarQube URL in Jenkins

- Go to: [Jenkins Configure System](http://localhost:8080/manage/configure)
- Add the SonarQube URL.
- ![Add Sonar URL](docs/img_5.png)
- **DO NOT FORGET TO SAVE**

---

## 10. Create a Jenkins Pipeline and Add the Jenkinsfile

1. In Jenkins, click **"New Item"**.
2. Enter a name for your pipeline and select **"Pipeline"**.
3. Click **OK**.
4. In the pipeline configuration, scroll to the **Pipeline** section.
5. Copy the content of your `Jenkinsfile` and paste it into the script box.
6. Save the pipeline.  
   ![Jenkins Pipeline](docs/img22.png)

---

## 11. Run the Jenkins Pipeline

- Go to your pipeline in Jenkins.
- Click **"Build Now"**.
- Monitor the build process.  
  ![Build Now](docs/img_1343.png)

---

## 12. Import Grafana Dashboards

After the pipeline succeeds:

- Visit [Grafana Import](http://localhost:3000/dashboard/import)
- Import the following dashboard IDs one by one:
  ```
  9964
  4701
  11378
  16459
  1860
  8321
  17642
  ```
- ![Grafana Dashboards](docs/img_7.png)