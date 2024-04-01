# TFTAD

[tftad.com](https://tftad.com)은
온라인게임 [Team-Fight-Tactics(전략적 팀 전투, 롤토체스)](https://teamfighttactics.leagueoflegends.com/) 요소 중 하나인 증강체 선택 관련 웹사이트입니다.
유튜브 크리에이터는 이곳에 본인의 플레이 영상을 게시할 수 있습니다. 영상에서 증강체 선택 장면이 추출되어 게시됩니다.


## 기술

- Spring boot, JPA

## 특징

- Rest API 서버입니다.
- [OAuth를 이용해 Member에 Channel을 등록합니다.(Blog)](https://velog.io/@bnbnac/Google-OAuth%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EC%9C%A0%ED%8A%9C%EB%B8%8C-%EC%B1%84%EB%84%90-%EB%93%B1%EB%A1%9D%ED%95%98%EA%B8%B0)
- 게시글은 곧 `증강체 선택` 콘텐츠입니다. 따라서 게시글 작성 시 유튜브 영상 주소를 첨부해야 하고, 그 전에 계정에 유튜브 채널을 등록해야 합니다. [**비즈니스 구조도** 보러가기](https://drive.google.com/file/d/10TQxXs86JlJcG9l03tJL9e7Imm5rXgAT/view?usp=drive_link)([다크모드로 보러가기](https://drive.google.com/file/d/1l3K2C0_6eXKJbfeXUAosnXFnEVY0pFtR/view?usp=drive_link))
- 게시글 작성 시 관련 정보를 [추출 서버](https://github.com/bnbnac/augment-extractor)로 전송합니다. 이후 [추출 서버](https://github.com/bnbnac/augment-extractor)로부터 완료 정보를 받습니다.

## 캡처
<img src="https://github.com/bnbnac/tftad/assets/100001092/a6986320-d025-4eee-a8a4-a27e8165a628" />
<img src="https://github.com/bnbnac/tftad/assets/100001092/8f8c95fb-2327-470e-8165-3f2fe83402ca" />
<img src="https://github.com/bnbnac/tftad/assets/100001092/f791521f-9bff-4391-bb37-521349385f42" />
<img src="https://github.com/bnbnac/tftad/assets/100001092/9d102391-9759-4030-8b4b-24cf1b0a1482" />
<video src="https://youtu.be/SJuzTF7wmV0?si=6RUnXBFNHhqbCrin"></video>