# TODO

---

## response NPE 처리!!!!
### extractor service좀 어떻게 해봐...

- unpub post edit transaction. edit중에 extractor가 껴들면어캄?
- 리스트조회: 일반조회 unpublish 개수만큼 더 퍼와야하는데 이거처리메소드?
- showPost가 필요한가? published가 필요한가?
- 근데 getPosition은 postRepo를 계속 쓰는데 이걸로 구현하는게 맞나
- post가 channel을 가지면 편한가
- 비번변경용 비번클래스 나누기
- extractor로 나가는 reqdto를 만들자 특히 뭔가 delete할때 서비스에서 컨트롤러로 어떤 dto를 올리고 그걸 변환해서 reqdto를 만들자
- user input에 대한 보안 orm이 해주나?

---

- SSL
- ssh 키관리
- channel 이 다른사용자에게 등록된 경우 처리(일단 막아놓음)
- oauth가 아닌 다른 경로로 channel을 추가할수있는지
- 채널이 브랜드채널이면 주기적 확인(브랜딩 권한은 변하는거니까)
  - 근데 애초에 채널을 등록해놓는게 위험한건가?
- 컷 누락 제보기능 - 포인트 차감 환급
- oauth 관련 api 문서좀 읽고 v3impl 이런식으로
- postservice.validatePostInExtractorCompletion returns enum
- POST /posts 여러 서비스들의 트랜잭션에 대해: 트랜잭션 전파 공부
- validateExtractorResultOrDeletePost UX 고려 - 바로 지워버리지 말고 언제언제 지워질거다 이런식으로
- validateChannelOwner 성능테스트 -> 멤버에서 한번 걸러서 찾는거랑ㅇ비교
- 의존 라이브러리 자동 업데이트?
- comment 기능정도는 넣어줘야 rdb를 쓰는 보람이 있는거같다
- 결국 세션db가 필요하다? logout deleteMember
- member edit delete는 더 강한 인증필요?

 