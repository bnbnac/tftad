# TODO

---

## response NPE 처리!!!!
### extractor service좀 어떻게 해봐...

- unpub post edit transaction. edit중에 extractor가 껴들면어캄?
- req res 클래스 validation, weblient npe (oauth, extractor)
- 리스트조회: 일반조회 unpublish 개수만큼 더 퍼와야하는데 이거처리메소드?
- extractor cutting 후 downloads 삭제 
- showPost가 필요한가? published가 필요한가?
- 근데 getPosition은 postRepo를 계속 쓰는데 이걸로 구현하는게 맞나
- downloads, cutter 찌꺼기 지우는작업, force quit 처리 끝내고 testmethod
- post가 channel을 가지면 편한가
- delete member, update member, update question
- 비번변경용 비번클래스 나누기
- extractor로 나가는 reqdto를 만들자 특히 뭔가 delete할때 서비스에서 컨트롤러로 어떤 dto를 올리고 그걸 변환해서 reqdto를 만들자

---

- SSL
- ssh 키관리
- channel 이 다른사용자에게 등록된 경우 처리(일단 막아놓음)
- oauth가 아닌 다른 경로로 channel을 추가할수있는지
- 채널이 브랜드채널이면 주기적 확인(브랜딩 권한은 변하는거니까)
  - 근데 애초에 채널을 등록해놓는게 위험한건가?
- 컷 누락 제보기능 - 포인트 차감 환급
- createdAt modifiedAt
- 프록시로 직접 question 접근 못하게
- external server들한테 jsonnode로 받아오는거 없애기
- oauth 관련객체도 추상화할필요가 있음. api 문서좀 읽고 v3impl 이런식으로
- postservice.validatePostInExtractorCompletion returns enum
- 로그인이 필요한 api 테스트에서 mock login
- POST /posts 여러 서비스들의 트랜잭션에 대해: 트랜잭션 전파 공부, 혹은 새로운 상위 서비스를 만들어 그 여러 서비스를 주입받는다?
- 지금 post title에만 길이 제한이 있는데 이거외에도 할거 많지 않나
- validateExtractorResultOrDeletePost UX 고려 - 바로 지워버리지 말고 언제언제 지워질거다 이런식으로
- validateChannelOwner 성능테스트 -> 멤버에서 한번 걸러서 찾는거랑ㅇ비교
- yt-dlp를 알아서 업데이트하는법?
- IP 주소나 기기 정보를 토큰에 포함 (추가 보안 요소) 추가적인 보안 요소를 도입할 수 있다.
  - 예를 들어, IP 주소나 기기 정보를 토큰에 포함시켜 토큰을 사용하는 클라이언트의 신원을 확인할 수 있다.
  - 이를 통해 탈취된 토큰이 다른 기기나 IP 주소에서 사용되는 것을 방지할 수 있다.
- comment 기능정도는 넣어줘야 rdb를 쓰는 보람이 있는거같다
- 결국 세션db가 필요하다? logout deleteMember

 