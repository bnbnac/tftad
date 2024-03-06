# TODO

---

- post는 question 작업에 따른 공개여부를 boolean field로 가져야함
- 실패테스트 해야할것
    - yt-dlp 다운로드 실패
    - ffmpeg 컷 실패
    - storage 저장경로 조회 실패
    - storage 컷 전송 실패
- 로그아웃, 채널삭제
- query... method의 NPE
- OAuthService와 GoogleService 분리?
- external별 webclient bean으로 세분화?
- extractor용 session을 만든다?
- extractor에서 시간이 지나도 callback 오지 않는다면? post 삭제?
- 매소드를 묶어서 줄이려고 하지말고 객체를 만들어야할듯? 그게그건가,,,?
- post content에 간략한 상황설명
- 혹은 question마다 선택 이유설명 << visible
- video resource 봐서 영상이 너무 길면 거부
- generateQuestions를 예쁘게 하기 위한 dto 손질
- Member 객체 없이 memberId만 가지고 Post를 생성할수잇는지
- req res 클래스 validation


---

- SSL
- ssh 키관리
- channel 이 다른사용자에게 등록된 경우 처리(일단 막아놓음)
- property 상수 중구난방 authproperty를 jwtproperty에서 빼자
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