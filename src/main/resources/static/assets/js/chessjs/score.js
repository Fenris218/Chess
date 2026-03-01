/**
 * Quản lý tỉ số trong session.
 * Khi F5 hoặc tắt/mở lại tab → sessionStorage bị xóa → tỉ số reset về 0-0.
 */

const KEY_PLAYER = "SCORE_PLAYER";
const KEY_BOT    = "SCORE_BOT";
const KEY_DRAW   = "SCORE_DRAW";

function getScore() {
    return {
        player: parseInt(sessionStorage.getItem(KEY_PLAYER) || "0", 10),
        bot:    parseInt(sessionStorage.getItem(KEY_BOT)    || "0", 10),
        draw:   parseInt(sessionStorage.getItem(KEY_DRAW)   || "0", 10),
    };
}

function saveScore(score) {
    sessionStorage.setItem(KEY_PLAYER, score.player);
    sessionStorage.setItem(KEY_BOT,    score.bot);
    sessionStorage.setItem(KEY_DRAW,   score.draw);
}

function addPlayerWin() {
    const s = getScore();
    s.player++;
    saveScore(s);
    renderScore();
}

function addBotWin() {
    const s = getScore();
    s.bot++;
    saveScore(s);
    renderScore();
}

function addDraw() {
    const s = getScore();
    s.draw++;
    saveScore(s);
    renderScore();
}

function renderScore() {
    const s = getScore();
    const elPlayer = document.getElementById("score-player");
    const elBot    = document.getElementById("score-bot");
    const elDraw   = document.getElementById("score-draw");
    if (elPlayer) elPlayer.textContent = s.player;
    if (elBot)    elBot.textContent    = s.bot;
    if (elDraw)   elDraw.textContent   = s.draw;
}

export { getScore, addPlayerWin, addBotWin, addDraw, renderScore };

