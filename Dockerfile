# Use an official OpenJDK runtime as a parent image
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# 필수 시스템 패키지 설치 (Python + Chrome + 기타 의존성)
RUN apt-get update && apt-get install -y \
    python3 python3-pip python3-venv \
    wget unzip curl gnupg \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 \
    libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 \
    libgbm1 libgtk-3-0 libxshmfence1 xdg-utils && \
    rm -rf /var/lib/apt/lists/*

# Chrome 설치
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-linux.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# requirements.txt 복사 및 Python 패키지 설치
# Spring Boot JAR 파일 복사
COPY build/libs/team1-0.0.1-SNAPSHOT.jar .
COPY requirements.txt .
COPY crawl/ ./crawl/

# 가상환경 생성 및 패키지 설치
RUN python3 -m venv venv && \
    . venv/bin/activate && \
    pip install --upgrade pip && \
    pip install -r requirements.txt

# 포트 오픈
EXPOSE 8080

# JAR 실행
ENTRYPOINT ["java", "-jar", "team1-0.0.1-SNAPSHOT.jar"]