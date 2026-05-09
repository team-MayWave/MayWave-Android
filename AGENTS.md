# Additional Agent Rules

## Code Change Summary Rule

After modifying code, always write a short summary in about 3 lines.

The summary must include:

```txt
Created code: ...
Deleted code: ...
Changed code: ...
```

If nothing was created, write `Created code: None`.

If nothing was deleted, write `Deleted code: None`.

If nothing was changed, write `Changed code: None`.

Example:

```txt
Created code: Added `ChatBubbleElement` composable to separate chat bubble UI.
Deleted code: Removed duplicated bubble UI code from `ChatScreen`.
Changed code: Replaced hardcoded UI with reusable parameters for message text and alignment.
```

<!-- 한글 설명:
코드를 수정한 뒤에는 AI가 어떤 코드를 만들었고, 어떤 코드를 삭제했고, 어떤 코드를 변경했는지 3줄 정도로 간단히 정리해야 한다.
-->

---

## Folder and File Creation Rule

Before creating a new folder or file, always check the related skill folder and `SKILL.md` file first.

Use the rules from the relevant `SKILL.md` file when deciding:

- folder name
- file name
- package path
- class name
- composable name
- responsibility of the file
- whether the file belongs to UI, ViewModel, Repository, DTO, Retrofit, Theme, or Navigation layer

Example skill structure:

```txt
.codex/
└── skills/
    ├── android-compose-ui-rules/
    │   └── SKILL.md
    ├── android-compose-navigation/
    │   └── SKILL.md
    ├── android-compose-retrofit/
    │   └── SKILL.md
    ├── android-compose-viewmodel/
    │   └── SKILL.md
    └── android-compose-theme/
        └── SKILL.md
```

If a relevant `SKILL.md` exists, follow it first.

If no relevant `SKILL.md` exists, follow this priority:

```txt
1. AGENTS.md project rules
2. Existing project folder structure
3. Android official architecture guidance
4. Jetpack Compose best practices
```

Do not create a new folder just because the file is new.

Create a new folder only when:

- the feature has multiple related files
- the responsibility is clearly different from existing folders
- the folder improves separation of concerns
- the folder matches an existing skill rule

Bad example:

```txt
chat_element/
chat_component/
chat_ui/
chat_view/
```

Do not create many similar folders with unclear responsibility.

Good example:

```txt
ui/
└── chat/
    ├── ChatScreen.kt
    ├── ChatBubble.kt
    └── ChatChoiceButton.kt
```

<!-- 한글 설명:
새 폴더나 파일을 만들기 전에 관련된 skills 폴더 안의 SKILL.md를 먼저 확인해야 한다.
폴더 이름, 파일 이름, 패키지 경로, 파일 역할은 SKILL.md 규칙을 기준으로 결정한다.
-->

---

## Korean Explanation and Vocabulary Rule

This project may include short Korean comments inside Markdown rule files to help the developer understand the rule.

Use Korean comments only for explanation.

Do not use Korean comments to replace the main rule.

The main rule should be written clearly in English so AI agents can understand and follow it.

Example:

```md
- Use `Route` classes instead of raw string routes.

<!-- 한글:
화면 이동 경로를 `"welcome"` 같은 문자열로 직접 쓰지 말고,
Route 클래스로 관리하라는 뜻이다.
-->
```

When a rule introduces an important technical word, add a short vocabulary section.

Example:

```md
<!-- Vocabulary:
- route: 화면 이동 경로
- destination: 이동할 화면
- navigate: 화면을 이동하다
- composable: Compose에서 UI를 만드는 함수
- repository: 데이터 요청 로직을 숨기는 계층
- DTO: 서버와 주고받는 데이터 형식
- domain model: 앱 내부에서 쓰기 좋은 데이터 모델
-->
```

Keep Korean explanations short and practical.

Do not write long theory inside `AGENTS.md`.

Detailed explanations should be placed inside the related `SKILL.md` file.

<!-- 한글 설명:
AGENTS.md는 프로젝트 전체 규칙을 짧고 명확하게 적는 곳이다.
자세한 설명은 각 skills 폴더의 SKILL.md에 적는 것이 좋다.
-->

---

## Agent Work Order Rule

When creating or modifying code, follow this order:

```txt
1. Read AGENTS.md
2. Check the related skills folder
3. Read the relevant SKILL.md file
4. Check the existing project folder structure
5. Create or modify only the necessary files
6. Keep each file responsible for one clear role
7. After modification, write a 3-line code change summary
```

Do not skip the related `SKILL.md` file when it exists.

Do not create folders or files without checking whether an existing folder already has the correct responsibility.

<!-- 한글 설명:
에이전트는 바로 코드를 만들지 말고 AGENTS.md → skills/SKILL.md → 기존 폴더 구조 순서로 확인한 뒤 코드를 만들어야 한다.
-->

---

## Final Output Rule

After completing a code task, always finish with this format:

```txt
Created code: ...
Deleted code: ...
Changed code: ...
```

Keep the final explanation short.

Do not write a long theory unless the developer asks for it.

Example:

```txt
Created code: Added `ChatChoiceButton.kt` for reusable chat choice UI.
Deleted code: None.
Changed code: Updated `ChatScreen.kt` to use `ChatChoiceButton` instead of duplicated button code.
```

<!-- 한글 설명:
코드 작업이 끝나면 마지막에 만든 코드, 삭제한 코드, 변경한 코드를 짧게 정리해야 한다.
-->