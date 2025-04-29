# 💻개발자 멘토링 서비스 Dementor

[![DeMentor.png](https://i.postimg.cc/2SXdHJGt/DeMentor.png)](https://postimg.cc/SnCzRgwL)

### 배포 URL

> Admin : https://admin.dementor.site  <br>
Service : https://www.dementor.site
> 

<br>

## 프로젝트 소개

- 디멘터 프로젝트는 B2C 기반의 개발자 멘토링 플랫폼으로, 취업 준비생, 신입 개발자, 그리고 개발을 희망하는 사람들이 현업 개발자와 직접 소통하며 성장할 수 있도록 돕는 것을 목표로 하는 프로젝트입니다.

<br>

## 팀원 구성

<div align="center">

| 최대욱 | 김호남 | 권보경 | 백민진 | 최다빈 |
| --- | --- | --- | --- | --- |
| [@daewook123](https://github.com/daewook123) | [@tigerpoint123](https://github.com/tigerpoint123) |  [@pingu0118](https://github.com/pingu0118) | [@baekminjin](https://github.com/baekminjin) | [@davinyakma](https://github.com/davinyakma) |

</div>

<br>

## 1. 개발 환경

- 백엔드: Spring Boot 3.4.4, Java 17
- ORM: JPA (Hibernate)
- 데이터베이스: MySQL8
- 실시간/비동기 처리: Firebase, RabbitMQ
- 캐시 & 세션 관리: Redis
- CI/CD: Github Actions
- 인증 및 보안: Spring Security, JWT

<br>

<br>

## 2. 실행 컨테이너
- mysql-team03
- redis-server
- rabbitMQ ( == web3_4_tried-it_begit)
- angry_bartik
- web3_4_tried-it_begit (rabbitMQ, prometheus, grafana)

### 2-1. K6 도커 명령어 (powershell)
> docker run --rm -v "${PWD}:/scripts" grafana/k6 run --out experimental-prometheus-rw=http://host.docker.internal:9090/api/v1/write /scripts/script.js

## 4. 역할 분담

### 🍊 최대욱

- 회원, 관리자 로그인, 관리자 기능 공동개발

<br>

### 👻 김호남

- 멘토링 수업 도메인 개발, 멘토 기능 공동개발

<br>

### 😎 권보경

- 채팅 도메인 개발

<br>

### 🐬 백민진

- 멘토링 신청 도메인 개발, 멘토 기능 공동 개발

<br>

### 😃 최다빈

- 멘토 도메인 개발, 파일 첨부 도메인 개발

<br>

## 5. 개발 기간 및 작업 관리

### 개발 기간

- 전체 개발 기간 : 2025-03-21 ~ 2025-04-16

<br>

## 6. 프로젝트 구조

<img width="5589" alt="frame (9)" src="https://github.com/user-attachments/assets/682a6486-74d9-475c-94e9-8f6403629d87" />

<br>

