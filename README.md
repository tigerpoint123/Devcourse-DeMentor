# 💻개발자 멘토링 서비스 Dementor

<img width="1285" height="674" alt="스크린샷 2025-08-02 174633" src="https://github.com/user-attachments/assets/e3fb97a8-0eb7-42e0-890a-d59162937c05" />

### 배포 URL

> Admin : https:// <br>
Service : https:// <br>
> 배포 예정

## 프로젝트 소개

- 디멘터 프로젝트는 B2C 기반의 개발자 멘토링 플랫폼으로, 취업 준비생, 신입 개발자, 그리고 개발을 희망하는 사람들이 현업 개발자와 직접 소통하며 성장할 수 있도록 돕는 것을 목표로 하는 프로젝트입니다.

## 팀원 구성

<div align="center">
  
| 최대욱 | 김호남 | 권보경 | 백민진 | 최다빈 |
| --- | --- | --- | --- | --- |
| [@daewook123](https://github.com/daewook123) | [@tigerpoint123](https://github.com/tigerpoint123) |  [@pingu0118](https://github.com/pingu0118) | [@baekminjin](https://github.com/baekminjin) | [@davinyakma](https://github.com/davinyakma) |

</div>

## 1. 개발 환경

- 백엔드: Spring Boot 3.4.4, Java 17
- ORM: JPA (Hibernate)
- 데이터베이스: MySQL8
- 실시간/비동기 처리: Firebase, RabbitMQ
- 캐시 & 세션 관리: Redis
- CI/CD: Github Actions
- 인증 및 보안: Spring Security, JWT


## 2. 실행 컨테이너
- mysql-team03
- redis-server
- rabbitMQ ( == web3_4_tried-it_begit)

### 2-1. K6 도커 명령어 (powershell)
> redis 버전 : docker run --rm --network web3_4_tried-it_begit_monitoring -v "C:\workplace\WEB3_4_Tried-IT_BE.git:/scripts" grafana/k6:latest run --out influxdb=http://influxdb:8086/k6 /scripts/k6_script/redis_script.js
> 
> DB 버전 : docker run --rm --network web3_4_tried-it_begit_monitoring -v "C:\workplace\WEB3_4_Tried-IT_BE.git:/scripts" grafana/k6:latest run --out influxdb=http://influxdb:8086/k6 /scripts/k6_script/db_script.js
> 
> -u 10 : 10명의 가상 사용자가
> 
> -i 100 : 각 100번의 http 요청
> 
### 2-1-1. Node.js로 테스트 실행
> 명령어 : artillery run k6_script/redis_load_test.yml --output k6_script/result.json
> --output 이후는 결과를 json 형태로 저장
> artillery 버전 1.x로 실행해야 함

### 2-2. 테스트 방법
> web3_4_tried-it_begit(grafana, influxdb) 실행
> 
> 스크립트 실행 (powershell) : docker run --rm --network web3_4_tried-it_begit_monitoring -v "//c/workplace/WEB3_4_Tried-IT_BE.git:/scripts" grafana/k6:latest run -u 10 -i 100 --out influxdb=http://influxdb:8086/k6 /scripts/script.js
> 
> grafana (localhost:3000) 에서 대시보드로 실시간 모니터링 (admin / admin or 1234)

## 3. 역할 분담

### 👻 김호남

- 멘토링 수업 도메인 CRUD, 멘토 기능 공동개발
- 인기 멘토링 수업 조회 성능 개선 - Redis 캐시 활용
- 비동기 알림 시스템 도입 - RabbitMQ 메시지 큐 도입

## 4. 개발 기간 및 작업 관리

### 개발 기간

- 전체 개발 기간 : 2025-03-21 ~ 2025-04-16


## 5. 프로젝트 구조
### 5-1. ERD
<img width="1671" height="1022" alt="image" src="https://github.com/user-attachments/assets/73eede5f-b549-430c-9ad7-7a827b8d59ff" />

### 5-2. 전체 구조
<img width="1307" height="855" alt="image" src="https://github.com/user-attachments/assets/ab3a5929-fa04-4f1c-abab-8b9c7f615a8f" />

<br>

