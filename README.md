# sunsuntripBackend
Fukushima Trip Recommendation API Server

---
## Git Convention

### 1. 이슈 생성 및 관리

- **이슈 생성**  
  - 기능 추가나 수정이 필요한 경우 Issues 탭에서 이슈를 생성.
  - **이슈 제목**: `[기능 이름] [수정 또는 추가]` 형식으로 작성.  
    - 예: `로그인 기능 추가`
  - **이슈 내용**: 변경 내용과 변경 이유를 간단히 설명.

- **In Progress 라벨**  
  - 작업을 시작할 때 생성된 이슈에 **In Progress** 라벨을 붙여 작업 중임을 팀원에게 알림.  
  - 동일한 작업을 중복해서 수행하는 것을 방지.


### 2. 로컬 환경에서 Git 설정

1. **기본 브랜치명 설정**
```
git config --global init.defaultBranch main
```

2. **git init**  
   - 로컬 프로젝트에서 Git 초기화를 진행.
   
3. **원격 저장소 연결**  
   - `git remote add origin <GitHub 주소>` 명령어로 원격 저장소와 연결.
   
4. **기본 브랜치 가져오기**  
   - `git pull origin main`으로 원격 저장소의 `main` 브랜치를 로컬에 동기화하여 최신 상태 유지.


### 3. 작업 브랜치 생성 및 이슈 해결

1. **작업 브랜치 생성**  
   - `git checkout -b 브랜치이름` 명령어로 새로운 브랜치를 생성하고, 작업 중인 이슈에 맞는 이름을 브랜치명으로 지정.  
     - 예시

```
feature/api-routing → API 연동 및 경로 처리 로직
fix/login-validation → 로그인 입력값 검증 수정
chore/ci-setup → GitHub Actions 등 CI 설정
refactor/path-algorithm → 경로 탐색 알고리즘 리팩토링
```


2. **코드 수정 또는 기능 추가**  
   - 이슈에서 요구하는 내용을 구현하거나 수정.
   - 코드 작성 후 아래 Spring Convention에 어긋나지 않게 작성했는지 확인
     - https://chatgpt.com/g/g-2DQzU5UZl-code-copilot
       - 컨벤션에 맞춰 코드 수정 요청, 컨벤션 보내기, 코드 보내기

3. **변경 사항 스테이징 및 커밋**
   - `git add .` 명령어로 모든 변경 사항을 스테이징.
   - `git commit -m "변경 사항 설명"`으로 커밋.
   - **커밋 메시지 형식**: `[이슈제목] - [세부적인 수정 또는 추가사항]` 사용.  
     - 예: `로그인 기능 - 로그인 버튼 디자인 수정`

5. **원격 저장소로 푸시**  
   - `git push origin 브랜치명` 명령어로 작업 브랜치를 원격 저장소에 푸시.


### 4. GitHub에서 Pull Request 생성 및 병합

1. **Pull Request 생성**  
   - GitHub에서 **Pull Requests** 탭으로 이동하여 새로운 Pull Request 생성.
   - **PR 제목**:  
     - 단일 이슈 해결일 경우 커밋 메시지를 그대로 사용.
     - 여러 이슈를 해결한 경우 해결된 이슈 제목을 연달아 작성.
   - **PR 내용**: 추가 설명이 필요한 경우 자유롭게 작성.

2. **코드 리뷰 및 병합**  
   - PR을 리뷰할 수 있도록 하며, 리뷰가 완료되면 PR을 `main` 브랜치에 병합.
   - 간단한 작업의 경우 리뷰 없이 자동 병합 사용.

3. **브랜치 삭제**  
   - 병합 후 불필요한 브랜치를 삭제하여 브랜치 관리 간소화. 보통 GitHub에서 **Delete branch** 버튼을 통해 삭제.

4. **Done 라벨**  
   - In Progress 라벨을 떼고 Done 라벨을 붙여 이슈가 해결되었음을 알림.


### 전체 프로세스 요약

- **이슈 생성 및 In Progress 라벨 부착**
- **로컬에서 Git 초기 설정**
- **작업 브랜치 생성 후 이슈 해결**
- **커밋 및 원격 저장소에 푸시**
- **GitHub에서 Pull Request 생성 및 병합 후 Done 라벨 부착**
