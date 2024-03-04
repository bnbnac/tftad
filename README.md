# TODO

---

- SSL
- channel 이 다른사용자에게 등록된 경우 처리(일단 막아놓음)
- property 상수 중구난방 authproperty를 jwtproperty에서 빼자
- oauth가 아닌 다른 경로로 channel을 추가할수있는지
- 시작시간(or 대기중) 예상종료시간
- url exception 안내화면
- 무지성 리다이렉션은 안된다. 익셉션컨트롤 안하면 여러번 일어나버림
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