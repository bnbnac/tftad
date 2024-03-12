# TODO

---

## response NPE 처리!!!!

- unpub post edit transaction. edit중에 extractor가 껴들면어캄?
- req res 클래스 validation, weblient npe (oauth, extractor)
- 짜바리 crud - channel, question,
- 리스트조회: 일반조회 unpublish 개수만큼 더 퍼와야하는데 이거처리메소드?
- 본인수정 & 본인삭제 : extractor가 껴들면?
- post RUD 테스트 다시보기
- validateExtractorResultOrDeletePost 는 repo코드가 있으니까 방어를 해야한다?

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
- business적 validation:
- POST /posts 여러 서비스들의 트랜잭션에 대해: 트랜잭션 전파 공부, 혹은 새로운 하위 서비스를 만들어 그 여러 서비스를 주입받는다?
- 지금 post title에만 길이 제한이 있는데 이거외에도 할거 많지 않나
- validateExtractorResultOrDeletePost UX 고려
- validateChannelOwner 성능테스트 -> 멤버에서 한번 걸러서 찾는거랑ㅇ비교