# CampusIn 백엔드

캠퍼스 생활 전반(커뮤니티, 스터디, 학습 루틴)을 모바일에서 지원하는 iOS 앱을 위한 백엔드 서비스입니다. 게시판과 스터디 모집, 실시간 메시지, 학습 타이머·할 일 관리, 랭킹·배지 등으로 학생들의 참여와 동기 부여를 높이는 것을 목표로 합니다.

## 프로젝트 개요
- **목적**: 학생들이 교내 정보를 공유하고 함께 공부하며 자신의 학습 루틴을 관리할 수 있는 통합 플랫폼 제공.
- **클라이언트**: iOS 앱(Alamofire 기반 네트워킹, SnapKit UI 등)과 연동.
- **백엔드 역할**: 인증·인가, 도메인 데이터 관리, 검색/통계/랭킹 계산, 실시간성 보조 인프라 연동(캐시·검색엔진).

## 핵심 기능
- **인증/회원 관리**: OAuth2 소셜 로그인과 JWT 기반 세션, 닉네임 설정 및 관리자 승격 지원.
- **커뮤니티**: 게시판/게시글/댓글 CRUD, 태그, 사진 업로드 도메인, OpenSearch를 활용한 게시글 검색.
- **스터디/모집**: 스터디 그룹 생성·수정·삭제, 가입/초대 흐름, 그룹별 게시/관리 로직.
- **메시지**: 사용자 간 쪽지/메시지 스레드 관리로 개별 커뮤니케이션 제공.
- **학습 루틴 관리**: 타이머로 공부 시간을 기록/수정/초기화, 투두 리스트 등록 및 상태 관리.
- **동기 부여 요소**: 학습/활동 통계를 기반으로 랭킹 산정과 배지 부여, 주기적 랭킹 스케줄러 포함.
- **운영/관리**: 관리자 전용 엔드포인트, 액추에이터 기반 헬스 체크 및 모니터링.

## 기술 스택
- **언어/런타임**: Java 17, Gradle 기반(Spring Boot 3).
- **프레임워크**: Spring Boot.
- **데이터**: MySQL(영속 저장소), Redis(캐시), OpenSearch(게시글 검색), QueryDSL로 타입 안전 쿼리.
- **보안**: JWT 토큰 발급/검증, Spring Security 필터 체인, OAuth2 소셜 로그인.
- **관측/로깅**: Actuator + Micrometer Prometheus, Log4j2, P6Spy SQL 로깅.
- **API 문서화**: springdoc-openapi(Swagger UI).
- **프론트엔드**: iOS(Swift) with Alamofire, FSCalendar, MessageKit, Lottie, SideMenu, SnapKit, BSImagePicker.
- **운영 도구**: Dockerfile + docker-compose로 DB/Redis/OpenSearch 등 컨테이너 구동.

## 아키텍처 & 디렉터리
- **레이어드 아키텍처**: `api`(Controller) → `application`(도메인 서비스/비즈니스 규칙) → `domain`(엔티티·DTO) → `infra`(JPA/외부 연동)로 역할 분리, `common`에서 보안·설정·예외/응답 포맷을 공통 관리.
- **검색/캐시 파이프라인**: 게시글은 JPA로 저장된 뒤 OpenSearch 인덱싱, 인기/랭킹·세션 데이터는 Redis 기반 캐시로 가속.
- **배포/실행 흐름**: GitHub Actions를 통해 Docker file을 Docker Hub에 upload 후 AWS EC2에서 self-hosted runner를 통해 실행

![아키텍처](https://github.com/user-attachments/assets/687e7bf3-cf05-42d1-8954-36e2cf8cc4a1)
