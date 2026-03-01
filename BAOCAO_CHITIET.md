# 📋 BÁO CÁO CHI TIẾT: CÁCH HOẠT ĐỘNG CỦA CHƯƠNG TRÌNH CỜ VUA ONLINE

## Đại học Bách Khoa Đà Nẵng — PBL4

---

## Mục lục

1. [Tổng quan hệ thống](#1-tổng-quan-hệ-thống)
2. [Kiến trúc tổng thể](#2-kiến-trúc-tổng-thể)
3. [Công nghệ sử dụng](#3-công-nghệ-sử-dụng)
4. [Cấu trúc mã nguồn](#4-cấu-trúc-mã-nguồn)
5. [Luồng hoạt động chính](#5-luồng-hoạt-động-chính)
6. [Chi tiết Backend — Chess Engine (AI)](#6-chi-tiết-backend--chess-engine-ai)
7. [Chi tiết Backend — Tầng Web (Spring Boot)](#7-chi-tiết-backend--tầng-web-spring-boot)
8. [Chi tiết Frontend — Giao diện & Logic phía Client](#8-chi-tiết-frontend--giao-diện--logic-phía-client)
9. [Cơ sở dữ liệu](#9-cơ-sở-dữ-liệu)
10. [Sơ đồ tuần tự (Sequence Diagram)](#10-sơ-đồ-tuần-tự-sequence-diagram)
11. [Đánh giá thuật toán AI](#11-đánh-giá-thuật-toán-ai)
12. [Tổng kết](#12-tổng-kết)

---

## 1. Tổng quan hệ thống

Chương trình là một **ứng dụng web chơi cờ vua (Chess)** cho phép người chơi **đấu với máy tính (Bot AI)**. Hệ thống được xây dựng trên nền tảng **Spring Boot** (Java 17) phía backend, sử dụng **JSP** để render giao diện phía server, và **JavaScript thuần (Vanilla JS)** để xử lý logic bàn cờ và tương tác người dùng phía client.

### Tính năng chính:
- ♟️ Chơi cờ vua với AI Bot (thuật toán MiniMax)
- 📊 Lưu lịch sử nước đi theo trận đấu
- 🏆 Hiển thị tỉ số thắng/thua/hòa trong phiên
- 🔄 Tạo ván mới bất kỳ lúc nào

---

## 2. Kiến trúc tổng thể

```
┌─────────────────────────────────────────────────────────────────┐
│                        TRÌNH DUYỆT (Browser)                   │
│  ┌───────────────┐  ┌──────────────┐  ┌──────────────────────┐ │
│  │  JSP Pages    │  │  JavaScript  │  │  CSS Stylesheets     │ │
│  │  (HTML View)  │  │  (Game Logic │  │  (chess.css,         │ │
│  │               │  │   + API call)│  │   play_bot.css)      │ │
│  └───────────────┘  └──────┬───────┘  └──────────────────────┘ │
│                             │ HTTP REST API (JSON)              │
└─────────────────────────────┼───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT SERVER (Backend)                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │
│  │  Controller   │→│   Service    │→│   Chess Engine (AI)   │ │
│  │  (REST API)   │  │  (Business)  │  │  MiniMax / AlphaBeta │ │
│  └──────────────┘  └──────┬───────┘  └──────────────────────┘ │
│                            │                                    │
│                    ┌───────▼───────┐                            │
│                    │  Repository   │                            │
│                    │  (JPA/ORM)    │                            │
│                    └───────┬───────┘                            │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │   MySQL 8.0    │
                    │   (chessdb)    │
                    └────────────────┘
```

### Mô hình kiến trúc: **MVC + Layered Architecture**
- **Model**: Entity classes (Match, MatchWithBot, Step, BotStats)
- **View**: JSP pages + JavaScript rendering
- **Controller**: REST Controllers (Spring MVC)
- **Service Layer**: Business logic + AI engine integration
- **Repository Layer**: Data access qua Spring Data JPA

---

## 3. Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản | Mục đích |
|---|---|---|---|
| **Ngôn ngữ** | Java | 17 | Backend chính |
| **Framework** | Spring Boot | 3.3.4 | Application framework |
| **ORM** | Spring Data JPA + Hibernate | 6.5.3 | Tương tác database |
| **View Engine** | JSP + JSTL | 1.2 | Server-side rendering |
| **Database** | MySQL | 8.0 | Lưu trữ dữ liệu |
| **Frontend** | Vanilla JavaScript (ES Modules) | ES6+ | Game logic phía client |
| **Build Tool** | Maven | 3.x | Quản lý dependency, build |
| **Utility** | Lombok | 1.18.36 | Giảm boilerplate code |
| **Utility** | Google Guava | 31.1-jre | Immutable collections |
| **Code Format** | Spotless | 2.43.0 | Tự động format code |
| **Coverage** | JaCoCo | 0.8.12 | Code coverage report |

---

## 4. Cấu trúc mã nguồn

```
src/main/java/com/example/pbl4Version1/
│
├── Pbl4Version1Application.java          ← Entry point (Spring Boot main)
├── ServletInitializer.java               ← WAR deployment support
│
├── chessEngine/                          ← ★ CHESS ENGINE (AI CORE)
│   ├── Alliance.java                     ← Enum: WHITE/BLACK + bonus tables
│   ├── ai/                               ← AI algorithms
│   │   ├── MoveStrategy.java             ← Interface chiến lược tìm nước đi
│   │   ├── MiniMax.java                  ← ★ Thuật toán MiniMax (đang dùng)
│   │   ├── AlphaBetaWithMoveOrdering.java← Alpha-Beta pruning + sắp xếp
│   │   ├── AlphaBetaThreeBest.java       ← Alpha-Beta trả 3 nước tốt nhất
│   │   ├── BoardEvaluator.java           ← Interface đánh giá bàn cờ
│   │   ├── StandardBoardEvaluator.java   ← ★ Hàm đánh giá chính
│   │   ├── PawnStructureAnalyzer.java    ← Phân tích cấu trúc tốt
│   │   ├── KingSafetyAnalyzer.java       ← Phân tích an toàn vua
│   │   ├── MoveEvaluation.java           ← Pair<Move, Score>
│   │   └── Pair.java                     ← Utility pair
│   ├── board/                            ← Board representation
│   │   ├── Board.java                    ← ★ Bàn cờ + FEN parser/generator
│   │   ├── BoardUtils.java              ← Constants + helper methods
│   │   ├── Tile.java                     ← Ô cờ (Empty/Occupied)
│   │   └── Move.java                     ← Các loại nước đi
│   ├── piece/                            ← Quân cờ
│   │   ├── Piece.java                    ← Abstract base class
│   │   ├── King.java                     ← Vua
│   │   ├── Queen.java                    ← Hậu
│   │   ├── Rook.java                     ← Xe
│   │   ├── Bishop.java                   ← Tượng
│   │   ├── Knight.java                   ← Mã
│   │   └── Pawn.java                     ← Tốt (+ phong cấp, bắt qua đường)
│   └── player/                           ← Player logic
│       ├── Player.java                   ← Abstract player
│       ├── WhitePlayer.java              ← Người chơi trắng (+ nhập thành)
│       ├── BlackPlayer.java              ← Người chơi đen (+ nhập thành)
│       ├── MoveTransition.java           ← Kết quả thực hiện nước đi
│       └── MoveStatus.java              ← DONE/ILLEGAL/LEAVES_IN_CHECK
│
├── controller/                           ← REST API Controllers
│   ├── RootController.java              ← GET / → redirect /home
│   ├── HomeController.java              ← GET /home → play_with_bot.jsp
│   ├── MatchWithBotController.java      ← API tạo/lấy trận đấu bot
│   └── BotStepController.java          ← ★ API gửi nước đi → nhận nước bot
│
├── service/                              ← Business Logic
│   ├── BotStepService.java             ← ★ Xử lý nước đi + gọi AI engine
│   ├── MatchWithBotService.java        ← Tạo/lấy trận đấu
│   └── BotStatsService.java           ← Thống kê thắng/thua/hòa
│
├── entity/                               ← JPA Entities (Database models)
│   ├── Match.java                       ← Trận đấu (base class, TABLE_PER_CLASS)
│   ├── MatchWithBot.java               ← Trận đấu với bot (extends Match)
│   ├── Step.java                        ← Nước đi (lưu FEN, from, to)
│   └── BotStats.java                   ← Thống kê bot (singleton row)
│
├── repository/                           ← Data Access Layer
│   ├── MatchWithBotRepository.java     ← JPA Repository cho MatchWithBot
│   ├── StepRepisitory.java             ← JPA Repository cho Step
│   └── BotStatsRepository.java         ← JPA Repository cho BotStats
│
├── dto/                                  ← Data Transfer Objects
│   ├── request/
│   │   └── StepToBotRequest.java       ← Request: matchId, fen, from, to, name
│   └── response/
│       ├── ApiResponse.java            ← Wrapper response: code, message, result
│       ├── StepResponse.java           ← Response nước đi bot
│       └── MatchWithBotResponse.java   ← Response trận đấu
│
├── enums/                                ← Enum constants
│   ├── GameStatus.java                 ← ONGOING, CHECK_MATE, STALE_MATE,...
│   └── PlayerType.java                 ← WHITE, BLACK, COMPUTER, HUMAN,...
│
└── exception/                            ← Exception handling
    ├── ErrorCode.java                   ← Mã lỗi (MATCH_NOT_EXISTED,...)
    └── AppException.java               ← Custom runtime exception
```

### Frontend:
```
src/main/resources/static/assets/
├── js/
│   ├── chessjs/
│   │   ├── game_core.js          ← ★ Core: khởi tạo game, quản lý state
│   │   ├── score.js              ← Quản lý tỉ số (sessionStorage)
│   │   ├── data/
│   │   │   ├── data.js           ← Khởi tạo bàn cờ 8x8
│   │   │   └── piece.js          ← Factory tạo quân cờ (hình ảnh, tên)
│   │   ├── event/
│   │   │   └── global.js         ← ★ Xử lý click, tính nước đi hợp lệ,
│   │   │                            gửi nước đi lên server, nhận nước bot
│   │   ├── render/
│   │   │   └── main.js           ← Render bàn cờ, quân cờ, highlight, FEN
│   │   ├── helper/
│   │   │   ├── constants.js      ← DOM selectors constants
│   │   │   └── commonHelper.js   ← Check ô, kiểm tra chiếu
│   │   ├── components/
│   │   │   └── message.js        ← Alert, promotion dialog
│   │   └── mode/
│   │       └── play_with_bot.js  ← ★ Entry: khởi tạo trang chơi với bot
│   └── user/api/
│       └── match.js              ← Fetch API: tạo match, lấy match
├── css/
│   ├── chess.css                 ← Style bàn cờ
│   └── play_bot.css              ← Style trang chơi bot
└── img/                          ← Hình quân cờ, avatar,...

src/main/webapp/WEB-INF/views/
└── user/
    └── play_with_bot.jsp         ← Trang JSP chính để chơi với bot
```

---

## 5. Luồng hoạt động chính

### 5.1. Khi người dùng truy cập trang web

```
Trình duyệt                    Spring Boot Server                   MySQL
    │                                  │                               │
    │  GET /                           │                               │
    │─────────────────────────────────►│                               │
    │  redirect → /home                │                               │
    │◄─────────────────────────────────│                               │
    │                                  │                               │
    │  GET /home                       │                               │
    │─────────────────────────────────►│                               │
    │  return: user/play_with_bot.jsp  │                               │
    │◄─────────────────────────────────│                               │
    │                                  │                               │
    │  [Browser load JSP + JS + CSS]   │                               │
    │                                  │                               │
```

**Chi tiết:**

1. `RootController.java` nhận `GET /` → redirect sang `/home`
2. `HomeController.java` nhận `GET /home` → trả view `user/play_with_bot`
3. Spring MVC resolve thành file `/WEB-INF/views/user/play_with_bot.jsp`
4. JSP render HTML, load CSS + JavaScript (ES Module)
5. JavaScript module `play_with_bot.js` tự động chạy `initBotPage()`

### 5.2. Khi trang khởi tạo (initBotPage)

```javascript
// play_with_bot.js
function initBotPage() {
    renderScore();           // 0) Hiển thị tỉ số từ sessionStorage
    loadPageFunction("WHITE"); // 1) Setup giao diện cho phe TRẮNG
    reCreateGame();          // 2) Render bàn cờ 8x8 + gắn sự kiện click
    startNewBotMatch();      // 3) Gọi API tạo ván mới
}
```

**`startNewBotMatch()` gọi API:**
```
Browser                         Server                          MySQL
   │  GET /api/matches/bot        │                               │
   │─────────────────────────────►│ MatchWithBotController         │
   │                              │     .createBoard()             │
   │                              │─► MatchWithBotService          │
   │                              │     .create()                  │
   │                              │        │  new MatchWithBot()   │
   │                              │        │  save to DB ─────────►│
   │                              │        │  ◄── return ID ───────│
   │                              │◄───────┘                       │
   │  JSON: {id, winner, steps}   │                               │
   │◄─────────────────────────────│                               │
   │                              │                               │
   │  localStorage["MATCH_ID"]    │                               │
   │  = data.id                   │                               │
```

### 5.3. Khi người chơi thực hiện nước đi

Đây là **luồng quan trọng nhất** của chương trình:

```
Browser (JS)                    Server (Java)                    MySQL
   │                              │                               │
   │ [1] Người chơi click quân    │                               │
   │     → tính nước đi hợp lệ   │                               │
   │     → highlight các ô        │                               │
   │                              │                               │
   │ [2] Click ô đích             │                               │
   │     → di chuyển quân (DOM)   │                               │
   │     → tạo FEN string         │                               │
   │                              │                               │
   │ [3] POST /api/steps/bot      │                               │
   │     {matchId, fen,           │                               │
   │      from, to, name}         │                               │
   │─────────────────────────────►│                               │
   │                              │ BotStepController              │
   │                              │   .stepToBot(request)          │
   │                              │─► BotStepService               │
   │                              │   .applyHumanStepAndReplyBotMove()
   │                              │     │                          │
   │                              │     │ [4] Lưu nước người chơi │
   │                              │     │     Step(human) ────────►│
   │                              │     │                          │
   │                              │     │ [5] Parse FEN → Board    │
   │                              │     │     Board.createByFEN()  │
   │                              │     │                          │
   │                              │     │ [6] AI tính nước đi      │
   │                              │     │     MiniMax(depth=2)     │
   │                              │     │       .execute(board)    │
   │                              │     │     ┌────────────────┐   │
   │                              │     │     │  Duyệt tất cả  │   │
   │                              │     │     │  nước hợp lệ   │   │
   │                              │     │     │  → min/max      │   │
   │                              │     │     │  → chọn best    │   │
   │                              │     │     └────────────────┘   │
   │                              │     │                          │
   │                              │     │ [7] Thực hiện nước bot   │
   │                              │     │     board.makeMove(bot)  │
   │                              │     │     → new Board state    │
   │                              │     │     → generateFen()      │
   │                              │     │                          │
   │                              │     │ [8] Lưu nước bot         │
   │                              │     │     Step(bot) ──────────►│
   │                              │     │                          │
   │                              │◄────┘                          │
   │  JSON: {matchId, fen,        │                               │
   │         from, to, name}      │                               │
   │◄─────────────────────────────│                               │
   │                              │                               │
   │ [9] Nhận nước bot            │                               │
   │     → di chuyển quân bot     │                               │
   │     → kiểm tra chiếu/hết    │                               │
   │     → cập nhật UI            │                               │
```

---

## 6. Chi tiết Backend — Chess Engine (AI)

### 6.1. Biểu diễn bàn cờ (Board Representation)

Bàn cờ được biểu diễn dưới dạng **mảng 64 ô (Tile)**, đánh số từ 0 đến 63:

```
Ô   0  1  2  3  4  5  6  7     (hàng 8: a8 b8 c8 d8 e8 f8 g8 h8)
    8  9  10 11 12 13 14 15     (hàng 7)
    16 17 18 19 20 21 22 23     (hàng 6)
    24 25 26 27 28 29 30 31     (hàng 5)
    32 33 34 35 36 37 38 39     (hàng 4)
    40 41 42 43 44 45 46 47     (hàng 3)
    48 49 50 51 52 53 54 55     (hàng 2: a2 b2 ... h2)
    56 57 58 59 60 61 62 63     (hàng 1: a1 b1 ... e1=King ... h1)
```

**Class `Tile`** (Flyweight Pattern):
- `EmptyTile`: ô trống, được cache sẵn 64 instances (không tạo mới)
- `OccupiedTile`: ô có quân, chứa reference đến `Piece`

**Class `Board`** (Immutable, Builder Pattern):
- Mỗi khi thực hiện nước đi → tạo Board MỚI hoàn toàn (immutable)
- Sử dụng `Board.Builder` để build
- Phương thức quan trọng:
  - `createByFEN(String fen)`: parse chuỗi FEN thành Board
  - `generateFen()`: tạo chuỗi FEN từ Board hiện tại
  - `createStandardBoard()`: tạo bàn cờ ban đầu
  - `calculateLegalMoves()`: tính tất cả nước đi hợp lệ

### 6.2. Hệ thống quân cờ (Piece Hierarchy)

```
Piece (abstract)
├── King    (giá trị: 10000)  — di chuyển 1 ô mọi hướng
├── Queen   (giá trị:   900)  — kết hợp Xe + Tượng
├── Rook    (giá trị:   500)  — ngang/dọc
├── Bishop  (giá trị:   330)  — chéo
├── Knight  (giá trị:   300)  — hình chữ L
└── Pawn    (giá trị:   100)  — đi thẳng, ăn chéo
                                 + bắt qua đường (en passant)
                                 + nhảy đôi (nước đầu)
                                 + phong cấp (promotion)
```

Mỗi quân cờ có:
- `piecePosition`: vị trí (0-63)
- `pieceAlliance`: WHITE hoặc BLACK
- `isFirstMove`: để xử lý nhập thành, nhảy đôi tốt
- `calculateLegalMoves(Board)`: tính các nước đi hợp lệ
- `locationBonus()`: điểm thưởng vị trí (Piece-Square Table)
- `movePiece(Move)`: tạo quân mới ở vị trí mới

### 6.3. Hệ thống nước đi (Move Hierarchy)

```
Move (abstract)
├── MajorMove              — di chuyển thường (không ăn)
├── AttackMove             — di chuyển + ăn quân
│   └── MajorAttackMove    — quân lớn ăn quân
├── PawnMove               — tốt đi thẳng
├── PawnAttackMove         — tốt ăn chéo
│   └── PawnEnPassantAttackMove  — bắt qua đường
├── PawnJump               — tốt nhảy đôi
├── PawnPromotion          — phong cấp (Decorator Pattern)
├── KingSideCastleMove     — nhập thành gần (O-O)
├── QueenSideCastleMove    — nhập thành xa (O-O-O)
└── NullMove               — nước đi null (sentinel)
```

Khi thực hiện nước đi (`Move.execute()`):
1. Tạo `Board.Builder` mới
2. Copy tất cả quân (trừ quân đang di chuyển)
3. Đặt quân đã di chuyển vào vị trí mới (`movePiece()`)
4. Chuyển lượt (`setMoveMaker`)
5. `builder.build()` → Board mới

### 6.4. Thuật toán AI — MiniMax

**Thuật toán MiniMax** là thuật toán tìm kiếm trong cây trò chơi (Game Tree Search), giả sử cả hai bên chơi tối ưu:
- **MAX** (Trắng): cố gắng **tối đa hóa** điểm đánh giá
- **MIN** (Đen): cố gắng **tối thiểu hóa** điểm đánh giá

```
                    ROOT (Đen đi - MIN)
                   /        |         \
              Move A      Move B     Move C
             (MAX)        (MAX)      (MAX)
            /    \       /    \     /    \
          MA1   MA2   MB1   MB2  MC1   MC2
         (MIN) (MIN) (MIN) (MIN)(MIN) (MIN)
          │     │     │     │    │     │
        eval  eval  eval  eval eval  eval
         5    -3     2     8   -1     4

    MIN chọn:  -3         2         -1    ← Mỗi node MIN chọn min
    MAX chọn:         2                   ← Node gốc MAX... nhưng đây Đen
    Kết quả: Đen chọn Move C (-1) → giá trị nhỏ nhất
```

**Cài đặt trong `MiniMax.java`:**

```java
public Move execute(Board board) {
    // Sử dụng multi-threading (ExecutorService) để song song hóa
    // đánh giá các nước đi ở tầng đầu tiên
    ExecutorService executorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    for (Move move : board.getCurrentPlayer().getLegalMoves()) {
        // Với mỗi nước đi hợp lệ:
        //   - Nếu Trắng đang đi → gọi min() cho bàn cờ sau khi đi
        //   - Nếu Đen đang đi → gọi max() cho bàn cờ sau khi đi
        // Chọn nước có giá trị tốt nhất cho bên đang đi
    }
}

public int min(Board board, int depth) {
    if (depth == 0 || isEndGame) return evaluate(board);
    int lowest = MAX_VALUE;
    for (Move move : legalMoves) {
        lowest = Math.min(lowest, max(executeMove, depth - 1));
    }
    return lowest;
}

public int max(Board board, int depth) {
    if (depth == 0 || isEndGame) return evaluate(board);
    int highest = MIN_VALUE;
    for (Move move : legalMoves) {
        highest = Math.max(highest, min(executeMove, depth - 1));
    }
    return highest;
}
```

**Depth (Độ sâu)**: Hiện tại sử dụng `depth = 2`, nghĩa là AI nhìn trước **2 nước** (1 nước của mỗi bên).

**Tối ưu hóa:**
- Sử dụng `ExecutorService` (multi-threading) để đánh giá song song các nước đi ở tầng gốc
- Có sẵn `AlphaBetaWithMoveOrdering` (Alpha-Beta pruning + sắp xếp nước đi) nhưng chưa kích hoạt trong code hiện tại

### 6.5. Hàm đánh giá bàn cờ (Board Evaluation)

`StandardBoardEvaluator` tính điểm = **Điểm Trắng − Điểm Đen**

Mỗi bên được tính theo công thức:

```
score = mobility + kingThreats + attacks + castle + pieceEvaluations + pawnStructure
```

| Yếu tố | Công thức | Mô tả |
|---|---|---|
| **pieceEvaluations** | Σ(giá trị quân + bonus vị trí) | Tổng giá trị quân + Piece-Square Table |
| **mobility** | 5 × (số nước hợp lệ × 10 / số nước đối thủ) | Tính di động |
| **kingThreats** | Chiếu hết: 10000 × depth; Chiếu: +45 | Đe dọa vua |
| **attacks** | +1 cho mỗi nước ăn quân có giá trị cao hơn | Ưu tiên đổi quân có lợi |
| **castle** | +25 nếu đã nhập thành | Thưởng nhập thành |
| **pawnStructure** | -10 cho tốt đôi, -10 cho tốt cô lập | Phạt cấu trúc tốt xấu |

**Piece-Square Tables** (bảng bonus vị trí):
- Mỗi quân có bảng 64 giá trị tương ứng 64 ô
- Ví dụ: Mã ở trung tâm có bonus cao, Vua ở trung tâm bị phạt
- Được định nghĩa trong `Alliance.java` (mảng `WHITE_PAWN_PREFERRED_COORDINATES`, `BLACK_KNIGHT_PREFERRED_COORDINATES`,...)

### 6.6. Phân tích cấu trúc tốt (PawnStructureAnalyzer)

- **Tốt đôi (Doubled Pawn)**: 2 tốt cùng cột → phạt `-10` mỗi tốt thừa
- **Tốt cô lập (Isolated Pawn)**: không có tốt ở cột kề → phạt `-10` mỗi tốt

### 6.7. Phân tích an toàn Vua (KingSafetyAnalyzer)

- Tính **Chebyshev distance** giữa Vua và quân đối phương gần nhất
- Quân có giá trị cao ở gần Vua → nguy hiểm hơn
- *(Đã implement nhưng chưa tích hợp vào hàm evaluate chính)*

---

## 7. Chi tiết Backend — Tầng Web (Spring Boot)

### 7.1. Khởi động ứng dụng

```java
@SpringBootApplication
public class Pbl4Version1Application {
    public static void main(String[] args) {
        SpringApplication.run(Pbl4Version1Application.class, args);
    }
}
```

- Spring Boot auto-configure: JPA, Web MVC, Tomcat embedded
- `ServletInitializer` hỗ trợ deploy WAR lên external Tomcat
- `application.properties` cấu hình DB, view resolver (JSP), port 8080

### 7.2. REST API Endpoints

| Method | URL | Controller | Mô tả |
|---|---|---|---|
| `GET` | `/` | `RootController` | Redirect → `/home` |
| `GET` | `/home` | `HomeController` | Trả view `play_with_bot.jsp` |
| `GET` | `/api/matches/bot` | `MatchWithBotController` | Tạo trận đấu mới → trả ID |
| `GET` | `/api/matches/bot/{id}` | `MatchWithBotController` | Lấy thông tin trận + lịch sử nước đi |
| `POST` | `/api/steps/bot` | `BotStepController` | ★ **Gửi nước người → nhận nước bot** |

### 7.3. Service: BotStepService (Logic chính)

```java
@Transactional
public StepResponse applyHumanStepAndReplyBotMove(StepToBotRequest request) {
    // 1. Tìm trận đấu trong DB
    MatchWithBot match = matchRepository.findById(request.getMatchId());

    // 2. Lưu nước đi của người chơi
    Step human = new Step();
    human.setMatch(match);
    human.setFrom(request.getFrom());       // e.g. "e2"
    human.setTo(request.getTo());           // e.g. "e4"
    human.setName(request.getName());       // e.g. "e4"
    human.setBoardState(request.getFen());  // FEN sau khi người đi
    stepRepository.save(human);

    // 3. Tạo Board từ FEN (trạng thái sau nước người chơi)
    Board board = Board.createByFEN(request.getFen());

    // 4. AI tính nước đi tốt nhất
    Move botMove = new MiniMax(2).execute(board);

    // 5. Thực hiện nước đi trên Board
    MoveTransition transition = board.getCurrentPlayer().makeMove(botMove);
    Board nextBoard = transition.getTransitionBoard();
    String nextFen = nextBoard.generateFen();

    // 6. Lưu nước đi bot vào DB
    Step bot = new Step();
    bot.setMatch(match);
    bot.setFrom(from);
    bot.setTo(to);
    bot.setBoardState(nextFen);
    stepRepository.save(bot);

    // 7. Trả response cho client
    return new StepResponse(matchId, nextFen, from, to, name, null, null);
}
```

### 7.4. FEN (Forsyth-Edwards Notation)

FEN là chuẩn biểu diễn trạng thái bàn cờ dạng chuỗi:

```
rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
│         │         │ │    │  │
│         │         │ │    │  └─ Số nước đầy đủ
│         │         │ │    └──── Ô bắt qua đường
│         │         │ └───────── Quyền nhập thành
│         │         └─────────── Bên đi tiếp (w/b)
│         └───────────────────── Vị trí quân (/ phân hàng)
└─────────────────────────────── Hàng 8 (đen) đến hàng 1 (trắng)
```

**`Board.createByFEN()`**: Parse chuỗi FEN, tạo từng quân cờ, xử lý nhập thành + en passant
**`Board.generateFen()`**: Duyệt 64 ô, tạo chuỗi FEN, thêm thông tin lượt đi + nhập thành

### 7.5. API Response Format

```json
{
    "code": 1000,
    "message": null,
    "result": {
        "matchId": 42,
        "fen": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3",
        "from": "e7",
        "to": "e5",
        "name": "e7e5",
        "gameStatus": null,
        "winner": null
    }
}
```

---

## 8. Chi tiết Frontend — Giao diện & Logic phía Client

### 8.1. Kiến trúc JavaScript (ES Modules)

```
play_with_bot.js          ← Entry point
    │
    ├── game_core.js      ← Core: initGame, reCreateGame, loadPageFunction
    │       │
    │       ├── data.js   ← Tạo mảng 8x8 Square objects
    │       ├── render/main.js  ← Render DOM bàn cờ + quân cờ
    │       └── event/global.js ← ★ Toàn bộ logic game phía client
    │               │
    │               ├── helper/commonHelper.js  ← Kiểm tra ô, chiếu
    │               └── components/message.js   ← Alert, promotion UI
    │
    ├── score.js          ← Tỉ số (sessionStorage)
    └── user/api/match.js ← Fetch API calls
```

### 8.2. Trạng thái bàn cờ phía Client

```javascript
// data.js - Mỗi ô là 1 object
{
    color: "white"|"black",   // Màu ô (cho CSS)
    id: "e4",                 // Tọa độ đại số
    piece: {                  // null nếu trống
        piece_name: "WHITE_PAWN",
        current_position: "e4",
        img: "/assets/img/white_pawn.png",
        piece_signal: "P",
        moved: false
    },
    highlight: false,         // Có đang highlight không
    highlightCaptured: false  // Highlight ô có thể ăn
}
```

`globalState` = mảng 8 hàng × 8 cột chứa các Square objects.

### 8.3. Luồng xử lý click quân cờ

```
[1] Người chơi click vào quân cờ
        │
        ▼
[2] globalEvent() → root.onclick
        │
        ▼
[3] Xác định quân được click (event.target → square.piece.piece_name)
        │
        ▼
[4] Gọi hàm tương ứng: whitePawnClick(), whiteRookClick(),...
        │
        ▼
[5] Kiểm tra: đúng lượt? đúng phe? không phải viewer?
        │
        ▼
[6] Tính nước đi hợp lệ: calculatePawnLegalMove(),...
        │  - normal: các ô có thể đi (không ăn)
        │  - attack: các ô có thể ăn
        │  - Xử lý đặc biệt: en passant, nhập thành
        │
        ▼
[7] Highlight các ô hợp lệ trên DOM (span.highlight)
        │
        ▼
[8] Chờ click lần 2...
```

### 8.4. Luồng xử lý di chuyển quân

```
[1] Click ô đích (có highlight)
        │
        ▼
[2] moveOrCancelMove(square)
        │
        ▼
[3] Kiểm tra willBeInCheck() — nước đi không được để vua bị chiếu
        │
        ▼
[4] Xử lý đặc biệt: prepareForMoving()
        │  - Nhập thành → di chuyển xe
        │  - Tốt nhảy đôi → ghi nhớ en passant
        │  - Bắt qua đường → xóa tốt bị bắt
        │
        ▼
[5] moveElement(piece, destination)
        │  - Cập nhật DOM (di chuyển img)
        │  - Cập nhật globalState
        │
        ▼
[6] Kiểm tra phong cấp (tốt đến hàng 8/1)
        │  → Hiện dialog chọn quân → finishPromotionPawn()
        │
        ▼
[7] Kiểm tra trạng thái: moveStatus()
        │  - CHECK_MATE → kết thúc, hiện thông báo
        │  - STALE_MATE → hòa
        │  - IN_CHECK → thông báo chiếu
        │
        ▼
[8] Thêm nước đi vào bảng: handleNameMove()
        │
        ▼
[9] Gửi lên server: sendStepToServer(from, to)
        │  → POST /api/steps/bot
        │  → Gửi FEN hiện tại (generateFen())
        │
        ▼
[10] Nhận response từ server (nước đi của bot)
        │
        ▼
[11] Di chuyển quân bot trên bàn cờ (moveElement)
        │
        ▼
[12] Kiểm tra trạng thái sau nước bot
        │  - Nếu bị chiếu hết → "Bạn thua!"
        │  - Nếu hòa → "Hòa cờ!"
        │
        ▼
[13] Chuyển lượt → chờ người chơi đi tiếp
```

### 8.5. Tính nước đi hợp lệ phía Client

Mỗi loại quân có hàm tính riêng:

| Quân | Hàm | Logic |
|---|---|---|
| Tốt | `calculatePawnLegalMove()` | Đi 1/2 ô, ăn chéo, en passant |
| Xe | `calculateRookLegalMove()` | Duyệt 4 hướng (lên/xuống/trái/phải) đến khi gặp quân |
| Mã | `calculateKnightLegalMove()` | 8 vị trí hình chữ L |
| Tượng | `calculateBishopLegalMove()` | Duyệt 4 đường chéo |
| Hậu | `calculateQueenLegalMove()` | = Xe + Tượng (8 hướng) |
| Vua | `calculateKingLegalMove()` | 8 ô xung quanh + nhập thành |

Sau khi tính, phải lọc: nước đi nào khiến vua bị chiếu → loại bỏ (`willBeInCheck()`).

### 8.6. Tạo chuỗi FEN phía Client

`generateFen()` trong `render/main.js`:
- Duyệt `globalState` từ hàng 8 → hàng 1
- Quân trắng: chữ IN HOA (K, Q, R, B, N, P)
- Quân đen: chữ thường (k, q, r, b, n, p)
- Ô trống: đếm số liên tiếp
- Thêm: lượt đi (w/b), quyền nhập thành, ô en passant

### 8.7. Quản lý tỉ số

```javascript
// score.js - Sử dụng sessionStorage (mất khi đóng tab)
sessionStorage.setItem("SCORE_PLAYER", playerWins);
sessionStorage.setItem("SCORE_BOT", botWins);
sessionStorage.setItem("SCORE_DRAW", draws);
```

---

## 9. Cơ sở dữ liệu

### 9.1. ERD (Entity Relationship)

```
┌──────────────────────────────┐
│          game_match          │
│ (Inheritance: TABLE_PER_CLASS)│
├──────────────────────────────┤
│ id            : BIGINT (PK)  │
│ game_status   : VARCHAR      │ ← ONGOING, CHECK_MATE,...
│ turn          : VARCHAR      │ ← WHITE, BLACK
│ winner        : VARCHAR      │ ← WHITE, BLACK, null
│ created_at    : TIMESTAMP    │
└──────────────┬───────────────┘
               │
               │ 1:N
               ▼
┌──────────────────────────────┐
│          match_with_bot      │
│ (extends game_match)         │
├──────────────────────────────┤
│ (kế thừa tất cả từ Match)   │
└──────────────┬───────────────┘
               │
               │ 1:N
               ▼
┌──────────────────────────────┐
│          game_step           │
├──────────────────────────────┤
│ id            : BIGINT (PK)  │
│ match_id      : BIGINT (FK)  │ → game_match
│ name          : VARCHAR      │ ← "e2e4", "O-O",...
│ from_position : VARCHAR      │ ← "e2"
│ to_position   : VARCHAR      │ ← "e4"
│ board_state   : TEXT         │ ← FEN string
└──────────────────────────────┘

┌──────────────────────────────┐
│          bot_stats           │
├──────────────────────────────┤
│ id            : BIGINT (PK)  │ ← luôn = 1 (singleton)
│ wins          : BIGINT       │
│ losses        : BIGINT       │
│ draws         : BIGINT       │
└──────────────────────────────┘
```

### 9.2. Chiến lược kế thừa

Sử dụng `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`:
- Mỗi subclass (`MatchWithBot`) có bảng riêng
- Chứa tất cả cột của parent (`Match`)
- Ưu điểm: query nhanh cho từng loại match

### 9.3. Hibernate DDL Auto

```properties
spring.jpa.hibernate.ddl-auto=update
```
→ Hibernate tự động tạo/cập nhật schema khi khởi động.

---

## 10. Sơ đồ tuần tự (Sequence Diagram)

### 10.1. Tạo ván mới

```
Browser              JS (play_with_bot)     Server (Controller)      Service               MySQL
   │                       │                      │                    │                      │
   │  DOMContentLoaded     │                      │                    │                      │
   │──────────────────────►│                      │                    │                      │
   │                       │  initBotPage()       │                    │                      │
   │                       │  renderScore()       │                    │                      │
   │                       │  loadPageFunction()  │                    │                      │
   │                       │  reCreateGame()      │                    │                      │
   │                       │                      │                    │                      │
   │                       │  GET /api/matches/bot│                    │                      │
   │                       │─────────────────────►│                    │                      │
   │                       │                      │  create()          │                      │
   │                       │                      │───────────────────►│                      │
   │                       │                      │                    │  save(MatchWithBot)   │
   │                       │                      │                    │─────────────────────►│
   │                       │                      │                    │◄─────────────────────│
   │                       │                      │◄───────────────────│                      │
   │                       │  {id:42, steps:[]}   │                    │                      │
   │                       │◄─────────────────────│                    │                      │
   │                       │                      │                    │                      │
   │                       │  localStorage[       │                    │                      │
   │                       │  "MATCH_ID"] = 42    │                    │                      │
   │  Bàn cờ hiển thị     │                      │                    │                      │
   │◄──────────────────────│                      │                    │                      │
```

### 10.2. Đi một nước (Human → Bot)

```
Browser           JS (global.js)         Server (BotStepCtrl)    BotStepService         MiniMax AI          MySQL
   │                    │                      │                      │                     │                 │
   │ click "e2"         │                      │                      │                     │                 │
   │───────────────────►│                      │                      │                     │                 │
   │                    │ whitePawnClick()      │                      │                     │                 │
   │                    │ calculateLegalMoves() │                      │                     │                 │
   │ highlight e3,e4    │                      │                      │                     │                 │
   │◄───────────────────│                      │                      │                     │                 │
   │                    │                      │                      │                     │                 │
   │ click "e4"         │                      │                      │                     │                 │
   │───────────────────►│                      │                      │                     │                 │
   │                    │ moveOrCancelMove()   │                      │                     │                 │
   │                    │ moveElement(P,e4)    │                      │                     │                 │
   │                    │ generateFen()        │                      │                     │                 │
   │                    │                      │                      │                     │                 │
   │                    │ POST /api/steps/bot  │                      │                     │                 │
   │                    │ {matchId:42,         │                      │                     │                 │
   │                    │  fen:"rnb.../4P3...",│                      │                     │                 │
   │                    │  from:"e2",to:"e4"}  │                      │                     │                 │
   │                    │─────────────────────►│                      │                     │                 │
   │                    │                      │ applyHuman...()      │                     │                 │
   │                    │                      │─────────────────────►│                     │                 │
   │                    │                      │                      │ save(humanStep)     │                 │
   │                    │                      │                      │────────────────────────────────────── ►│
   │                    │                      │                      │                     │                 │
   │                    │                      │                      │ Board.createByFEN() │                 │
   │                    │                      │                      │──► parse FEN        │                 │
   │                    │                      │                      │                     │                 │
   │                    │                      │                      │ MiniMax(2).execute() │                │
   │                    │                      │                      │────────────────────►│                 │
   │                    │                      │                      │                     │ min/max search  │
   │                    │                      │                      │                     │ evaluate boards │
   │                    │                      │                      │◄────────────────────│                 │
   │                    │                      │                      │ bestMove = d7→d5    │                 │
   │                    │                      │                      │                     │                 │
   │                    │                      │                      │ makeMove(d7→d5)     │                 │
   │                    │                      │                      │ generateFen()       │                 │
   │                    │                      │                      │ save(botStep) ─────────────────────── ►│
   │                    │                      │                      │                     │                 │
   │                    │                      │◄─────────────────────│                     │                 │
   │                    │ {fen:"...",           │                      │                     │                 │
   │                    │  from:"d7",to:"d5"}  │                      │                     │                 │
   │                    │◄─────────────────────│                      │                     │                 │
   │                    │                      │                      │                     │                 │
   │                    │ moveElement(p,d5)    │                      │                     │                 │
   │                    │ moveStatus() check   │                      │                     │                 │
   │ Quân đen di chuyển │                      │                      │                     │                 │
   │◄───────────────────│                      │                      │                     │                 │
```

---

## 11. Đánh giá thuật toán AI

### 11.1. Điểm mạnh

| Điểm mạnh | Mô tả |
|---|---|
| **Đa luồng** | Tầng gốc của MiniMax sử dụng `ExecutorService` song song hóa |
| **Hàm đánh giá đầy đủ** | Xét: giá trị quân, vị trí, di động, cấu trúc tốt, nhập thành, đe dọa |
| **Piece-Square Tables** | Khuyến khích quân chiếm trung tâm, phát triển đúng |
| **Hỗ trợ đặc biệt** | En passant, phong cấp, nhập thành đều được xử lý |
| **Immutable Board** | Mỗi nước tạo Board mới → thread-safe, dễ undo |

### 11.2. Hạn chế & Hướng cải tiến

| Hạn chế | Giải pháp đề xuất |
|---|---|
| `depth = 2` khá nông | Tăng depth, dùng Alpha-Beta pruning (đã code sẵn) |
| Chưa bật Alpha-Beta | Thay `new MiniMax(2)` bằng `new AlphaBetaWithMoveOrdering(4)` |
| Chưa có Opening Book | Thêm thư viện khai cuộc |
| Chưa có Endgame Tables | Thêm bảng tàn cuộc |
| Chưa có Quiescence Search | Thêm tìm kiếm bổ sung khi có giao tranh |
| Chưa có Iterative Deepening | Tìm kiếm sâu dần với giới hạn thời gian |
| Chưa có Transposition Table | Cache các vị trí đã đánh giá bằng Zobrist hashing |

### 11.3. Độ phức tạp

- **MiniMax thuần**: O(b^d) với b ≈ 35 (branching factor), d = 2 → ~1,225 vị trí
- **Alpha-Beta pruning**: O(b^(d/2)) trong trường hợp tốt nhất → ~35 vị trí với d=2
- **Thực tế**: Nhanh (< 1 giây) nhờ depth nhỏ + multi-threading

---

## 12. Tổng kết

### Tóm tắt cách hoạt động

```
1. Người dùng truy cập web → Spring Boot trả trang JSP
2. JavaScript tạo bàn cờ 8x8 trên DOM, gán sự kiện click
3. Gọi API tạo trận mới → server lưu vào MySQL → trả matchId
4. Người chơi click quân → JS tính nước hợp lệ → highlight
5. Người chơi click ô đích → JS di chuyển quân + tạo FEN
6. JS gửi FEN lên server qua REST API (POST /api/steps/bot)
7. Server parse FEN → tạo Board → chạy MiniMax AI (depth=2)
8. AI đánh giá tất cả nước có thể → chọn nước tốt nhất
9. Server lưu cả nước người + nước bot vào DB
10. Server trả nước bot (from, to, FEN mới) cho client
11. JS di chuyển quân bot trên bàn cờ, kiểm tra chiếu/hết
12. Lặp lại từ bước 4 cho đến khi kết thúc ván
```

### Phân công xử lý

| Thành phần | Xử lý phía Client (JS) | Xử lý phía Server (Java) |
|---|---|---|
| **Render bàn cờ** | ✅ DOM manipulation | ❌ |
| **Tính nước hợp lệ (người)** | ✅ Cho mỗi quân | ❌ |
| **Di chuyển quân (người)** | ✅ Cập nhật DOM + state | Lưu DB |
| **Tính nước AI (bot)** | ❌ | ✅ MiniMax engine |
| **Di chuyển quân (bot)** | ✅ Nhận response → update DOM | ✅ Tính toán + lưu DB |
| **Kiểm tra kết thúc** | ✅ Check/Checkmate/Stalemate | ❌ (chỉ client kiểm tra) |
| **Lưu lịch sử** | ❌ | ✅ MySQL |
| **Tỉ số phiên** | ✅ sessionStorage | ❌ |

### Tổng quan Design Patterns được sử dụng

| Pattern | Nơi sử dụng |
|---|---|
| **Builder** | `Board.Builder` - xây dựng bàn cờ |
| **Immutable Object** | `Board`, `Tile` - không thay đổi sau khi tạo |
| **Flyweight** | `EmptyTile` cache 64 ô trống |
| **Strategy** | `MoveStrategy` interface cho các thuật toán AI |
| **Singleton** | `StandardBoardEvaluator`, `PawnStructureAnalyzer`, `KingSafetyAnalyzer` |
| **Decorator** | `PawnPromotion` wrap nước đi gốc |
| **Template Method** | `Player.calculateKingCastles()` abstract → White/BlackPlayer |
| **Factory Method** | `Tile.createTile()`, `Move.MoveFactory` |
| **Repository** | Spring Data JPA repositories |
| **DTO** | Request/Response objects tách biệt entity |
| **Layered Architecture** | Controller → Service → Repository → Database |

---

> **Báo cáo được tạo tự động từ mã nguồn dự án PBL4 — Chess Online Game**
> **Đại học Bách Khoa Đà Nẵng**

