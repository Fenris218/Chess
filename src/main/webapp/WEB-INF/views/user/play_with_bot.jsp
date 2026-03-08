<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
    <link rel="stylesheet" href="/assets/css/user/base.css?v=2">
    <link rel="stylesheet" href="/assets/css/chess.css?v=1">
    <link rel="stylesheet" href="/assets/css/play_bot.css?v=2">
    <title>Chơi Cờ Vua Với Máy</title>
</head>
<body>
    <div id="background"></div>
    <div id="coating"></div>
    <div id="overlay"></div>
    <div class="container">
        <div class="play-area">
            <div class="player-info-container">
                <div class="player-info">
                    <div class="avatar" style="background: url('/assets/img/icon.jpg')
                    no-repeat center center / cover;">
                    </div>
                    <span class="content">
                        <span class="name">Người chơi</span>

                    </span>
                </div>
                <div class="player-info">
                    <span class="content">
                        <span class="name">Máy tính</span>

                    </span>
                    <div class="avatar" style="background: url('/assets/img/robot.png')
                    no-repeat center center / cover;">
                    </div>
                </div>
            </div>

            <div class="game-board-container">
                <div id="game-board"></div>
                <div id="prompt-pieces">
                    <div class="rook prompt-piece" name="PAWN"></div>
                    <div class="knight prompt-piece" name="KNIGHT"></div>
                    <div class="bishop prompt-piece" name="BISHOP"></div>
                    <div class="queen prompt-piece" name="QUEEN"></div>
                </div>
            </div>

            <div class="defeated-pieces-container">
                <span class="defeated-pieces"></span>
                <span class="defeated-pieces "></span>
            </div>
        </div>

        <div class="right-panel">
            <div class="closure introduce-container right-item">
                <div class="right-item__header">
                    <div class="header-img" style="background: url('/assets/img/robot.png')
                    no-repeat center center / cover;"></div>
                    <div class="right-item__title">Chơi với máy</div>
                </div>
                <div class="score-board">
                    <div class="score-row">
                        <span class="score-label">Người chơi</span>
                        <span class="score-colon">:</span>
                        <span class="score-value" id="score-player">0</span>
                    </div>
                    <div class="score-row">
                        <span class="score-label">Máy tính</span>
                        <span class="score-colon">:</span>
                        <span class="score-value" id="score-bot">0</span>
                    </div>
                    <div class="score-row">
                        <span class="score-label">Hòa</span>
                        <span class="score-colon">:</span>
                        <span class="score-value" id="score-draw">0</span>
                    </div>
                </div>
            </div>
            <div class="closure steps-container right-item">
                <div class="right-item__header">
                    <i class="fa-solid fa-arrow-right-arrow-left header-img"></i>
                    <div class="right-item__title">Nước đi</div>
                </div>
                <div class="steps scrollable-element-x">
                    <div class="steps-table">
                        <div class="step-item">
                            <div class="step-index">Trò chơi</div>
                            <div class="step-container">
                                <div class="step-avatar" style="background: url('/assets/img/icon.jpg')
                                    no-repeat center center / cover;"></div>
                            </div>
                            <div class="step-container">
                                <div class="step-avatar" style="background: url('/assets/img/robot.png')
                                    no-repeat center center / cover;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="step__footer">
                    <div class="btn btn--green step__function" id="create-new-match">
                        <i class="fa-solid fa-plus"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
        globalThis.MODE = "PLAY_WITH_BOT";
    </script>
    <script type="module" src="/assets/js/chessjs/mode/play_with_bot.js?v=21"></script>
</body>
</html>