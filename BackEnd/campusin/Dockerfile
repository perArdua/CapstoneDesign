# base-image
FROM openjdk:11
# 빌드 파일 경로
ARG JAR_FILE=build/libs/*.jar
# 빌드 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar
# 컨테이너 실행시 실행될 명령어
ENTRYPOINT ["java","-jar","/app.jar"]
