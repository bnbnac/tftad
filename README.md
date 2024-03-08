# TODO

---

- req res 클래스 validation, weblient npe (oauth, extractor)
- builder private으로 강제

---

- SSL
- ssh 키관리
- channel 이 다른사용자에게 등록된 경우 처리(일단 막아놓음)
- oauth가 아닌 다른 경로로 channel을 추가할수있는지
- 채널이 브랜드채널이면 주기적 확인(브랜딩 권한은 변하는거니까)
- 시작시간(or 대기중) 예상종료시간
- url exception 안내화면
- 무지성 리다이렉션은 안된다. 익셉션컨트롤 안하면 여러번 일어나버림
- 컷 누락 제보기능 - 포인트 차감 환급
- createdAt modifiedAt
- 프록시로 직접 question 접근 못하게
- 근데 원래 list.remove같은걸 쓰나
- external server들한테 jsonnode로 받아오는거 없애기
- oauth 관련객체도 추상화할필요가 있음. api 문서좀 읽고 v3impl 이런식으로
- postservice.validatePostInExtractorCompletion returns enum
- 로그인이 필요한 api 테스트에서 mock login