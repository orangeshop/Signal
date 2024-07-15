# 모바일 프로젝트(시그널-Signal)

<!-- 필수 항목 -->

## 카테고리

| Application | Domain | Language | Framework |
| ---- | ---- | ---- | ---- |
| :black_square_button: Desktop Web | :white_check_mark: AI | :black_square_button: JavaScript |:black_square_button: Vue.js |
| :white_check_mark: Mobile Web | :black_square_button: Big Data | :black_square_button: TypeScript | :black_square_button: React |
| :black_square_button: Responsive Web | :black_square_button: Blockchain | :black_square_button: C/C++ | :black_square_button: Angular |
| :white_check_mark: Android App | :black_square_button: IoT | :black_square_button: C# | :black_square_button: Node.js |
| :black_square_button: iOS App | :black_square_button: AR/VR/Metaverse | :white_check_mark: Python | :white_check_mark: MariaDB |
| :black_square_button: Desktop App | :black_square_button: Game | :white_check_mark: Java | :white_check_mark: Spring/Springboot |
| | | :white_check_mark: Kotlin | :white_check_mark: AndroidStudio  |

<!-- 필수 항목 -->

## 프로젝트 소개

* 프로젝트명: 시그널(Signal)
* 팀명 : 온고지신溫故知新(옛 것을 먼저 충분히 익히고 그것을 바탕으로 새로운 것을 익힌다 )
* 서비스 특징: [세대 간 정보 공유 서비스]
* 주요 기능
  - 주니어와 시니어세대들의 고민거리 공유 기능 제공
  - 실시간 채팅 기능 제공
  - 화상 커피챗 기능 제공
* 주요 기술
  - WebRTC
  - WebSocket
  - STT, TTS
  - REST API
* 참조 리소스
  * Vuetify: 디자인 전반 적용
  * Vue Argon Design System: 디자인 전반 적용
  * Vue Black Dashboard Pro(유료): 캘린더 컴포넌트 사용
  * AR Core: 구글에서 제공하는 AR 지원 라이브러리. 이미지 인식 및 오버레이 영상에 활용
  * Color Thief: 이미지 색상 추출 라이브러리. 커버 사진 색상 추출 및 배경 변경에 활용
  * Animation.css: CSS 애니메이션 지원 라이브러리. 메인 페이지 진입 애니메이션에 활용
* 배포 환경
  - URL: // 웹 서비스, 랜딩 페이지, 프로젝트 소개 등의 배포 URL 기입
  - 테스트 계정: // 로그인이 필요한 경우, 사용 가능한 테스트 계정(ID/PW) 기입

<!-- 자유 양식 -->

## 팀 소개
* 김민수: 팀장, 백엔드 개발
* 김민조: 부팀장, 프론트엔드 테크리더
* 강인수: 백엔드 개발, 백엔드 테크리더
* 황병현: 백엔드 개발, 아이디어 기획
* 최지훈: 프론트앤드 개발, QA
* 최영호: 프론트앤드 개발, 서기

## 팀 Ground Rule
* 🍎 사소한 이슈 편하게 얘기하기
* 🍪 고민거리가 1시간 넘어가면 얘기하기
* 🗣 정기회의 일주일에 2번, 한달 8번 중 지각 2번은 OK but 그 이후에는 커피 사기
* ⚠️ 최소 수면 시간 보장하기(1~7시는 대답 없어도 인정) + 공지 확인 후에 ✅ 체크 필수
* 📝 폭력과 욕설 자제하기
* 🤑 BE와 FE 각자의 포지션 내에서 코드 리뷰 진득하게 하기(진행 시 정말 잘했는데! 로 미사어구 붙여서 시작하기)
<!-- 자유 양식 -->

## 회의록
https://www.notion.so/8b707095e0874ad989de871aba68ebca?v=d518879cd26442699a8e9d1aea71fe0a&pvs=4

## 프로젝트 상세 설명

// 개발 환경, 기술 스택, 시스템 구성도, ERD, 기능 상세 설명 등
## 프로젝트 ERD 이미지
<img src='./시그널 DB.png'>

## 프로젝트 Figma 이미지
![image](figma.PNG)

## commit convention
- ⭐feat : 기능 (새로운 기능)
- 🛠fix : 버그 (버그 수정)
- ⚒refactor : 리팩토링
- 🎨design : CSS 등 사용자 UI 디자인 변경
- 💬comment : 필요한 주석 추가 및 변경
- 🖍️style : 스타일 (코드 형식, 세미콜론 추가: 비즈니스 로직에 변경 없음)
- ✏docs : 문서 수정 (문서 추가, 수정, 삭제, README)
- 📋test : 테스트 (테스트 코드 추가, 수정, 삭제: 비즈니스 로직에 변경 없음)
- 👏chore : 기타 변경사항 (빌드 스크립트 수정, assets, 패키지 매니저 등)
- 📃init : 초기 생성
- 🔙rename : 파일 혹은 폴더명을 수정하거나 옮기는 작업만 한 경우
- ✂remove : 파일을 삭제하는 작업만 수행한 경우

## 7.08(월) ~ 7.12(금) 1주차 기여도 정리

| 날짜 | 전체 | 김민수 | 강인수 | 황병현 | 김민조 | 최영호 | 최지훈 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 07/08 | 프로젝트 주제 회의(시니어의 재취업 지원 서비스) |  Notion 페이지 설정 및 Gitlab 연결, 전체 일정 관리 |  Jira 설정 및 Gitlab 연결, 백엔드 기술 상의 | Jira 설정 및 Gitlab 연결, 백엔드 기술 상의 | 프로젝트 개발 배경 조사, 프론트 기술 상의 | Figma로 구현 가능한지 테스트, 프론트 기술 상의 | Figma로 구현 가능한지 테스트, 프론트 기술 상의 |
| 07/09 | 프로젝트 주제 회의 + 컨설턴트, 코치님 1차 사전 미팅 | 일정관리, 기술 조사 및 기획 세부사항 정리, PM특강 | ERD 1차 설계, DB 기술 스택 논의 | 스프링부트 기술 학습, DB 기술 스택 논의 | 페르소나 및 고객 여정 지도 구상 | 회의록 작성 및 디자인 | 앱 로고 구상 및 디자인 |
| 07/10 | 프로젝트 주제 회의 + 컨설턴트, 코치님 2차 사전 미팅(최종 컨펌) | 스프링부트 기술 학습, 일정 관리 | ERD 2차 설계,  와이어 프레임 논의 | 기능 명세서 우선 순위 설정, ERD 2차설계 |  Figma 컴포넌트 제작 | 노션 구조 변경, 기능 명세서 우선 순위 설정 | FE프로젝트 폴더 구조 설계 |
| 07/11 | 와이어프레임 논의 완료 + 피그마 제작 + DB설계 | 일정관리, PM특강, 스프링부트 MariaDB연결 | 스프링부트 MariaDB연결, 백엔드 프로젝트 구조 설계 |  스프링부트 MariaDB연결, 기능 명세서 추가 사항 정리 | Figma 컴포넌트 제작, 앱으로 인한 기대효과 고려 | Figma 컴포넌트 제작, 서비스명(signal)구상 및 확정 | : Figma 컴포넌트 제작, 앱 로고 구상 및 확정 |
