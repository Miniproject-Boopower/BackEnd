# Use an official OpenJDK runtime as a parent image
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# 시스템 패키지 설치 (python3, pip 포함)
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# requirements.txt 복사 및 Python 패키지 설치
COPY requirements.txt .
RUN pip3 install --no-cache-dir -r requirements.txt

# Spring Boot JAR 파일 복사
COPY build/libs/team1-0.0.1-SNAPSHOT.jar .

# 포트 오픈
EXPOSE 8080

# JAR 실행
ENTRYPOINT ["java", "-jar", "team1-0.0.1-SNAPSHOT.jar"]
