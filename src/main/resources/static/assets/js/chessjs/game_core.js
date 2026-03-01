import { initGame } from "./data/data.js";
import {
    initGameRender,
    initGameFromFenRender,
    deletePieceForReview,
    createPieceForReview,
    resetPieces
} from "./render/main.js";
import { globalEvent, resetGameState } from "./event/global.js";
import {STEPS_CONTAINER} from "./helper/constants.js";
import {innerStepAvatar} from "./components/message.js";
import {getMatch} from "../user/api/match.js";
import {getPieceAtPosition} from "./helper/commonHelper.js";

const MODE = globalThis.MODE || "PLAY_WITH_BOT";

let globalState = initGame();

let ALLIANCE = "WHITE";
let OPPONENT = "BLACK";
let ROOM;
let ROLE = "PLAYER";
let matchActiveId;
let matchNumber = 0;
let blackPlayer;
let whitePlayer;
let isMatchExecute = false;

function setIsMatchExecute(execute) {
    isMatchExecute = execute;
}

/**
 * Khởi tạo lại bàn cờ mới.
 * Phải gọi sau khi DOM đã sẵn sàng VÀ loadPageFunction() đã chạy.
 */
function reCreateGame() {
    globalState = initGame();
    const root = document.getElementById("game-board");
    if (!root) {
        console.error("reCreateGame: #game-board không tìm thấy!");
        return false;
    }
    root.innerHTML = "";

    // Reset tất cả trạng thái game
    resetPieces();
    resetGameState();

    // Xóa quân bị bắt - dùng querySelectorAll để không crash nếu chưa có class
    document.querySelectorAll(".defeated-pieces").forEach(el => el.innerHTML = "");

    initGameRender(globalState);
    globalEvent();
    return true;
}

/**
 * Thiết lập giao diện cho alliance (WHITE/BLACK) - thêm class, chữ số, ký tự.
 */
function loadPageFunction(alliance) {
    const gbContainer = document.querySelector(".game-board-container");
    if (!gbContainer) return;

    const playerInfo = document.querySelectorAll(".player-info");
    const defeatedPieces = document.querySelectorAll(".defeated-pieces");

    // Xóa class cũ
    playerInfo.forEach(info => {
        info.classList.remove("alliance-black", "alliance-white");
    });
    defeatedPieces.forEach(info => {
        info.classList.remove("alliance-black", "alliance-white");
    });

    // Xóa digit/character cũ
    gbContainer.querySelectorAll(".game-board-container-digit").forEach(div => gbContainer.removeChild(div));
    gbContainer.querySelectorAll(".game-board-container-character").forEach(div => gbContainer.removeChild(div));

    if (alliance === "BLACK") {
        playerInfo[0].classList.add("alliance-black");
        playerInfo[1].classList.add("alliance-white");
        defeatedPieces[0].classList.add("alliance-black");
        defeatedPieces[1].classList.add("alliance-white");

        for (let i = 1; i <= 8; i++) {
            const digit = document.createElement('div');
            digit.innerText = i;
            digit.classList.add('game-board-container-digit');
            gbContainer.appendChild(digit);
        }
        const characters = document.createElement('div');
        characters.classList.add('game-board-container-character');
        for (let i = "h".charCodeAt(0); i >= "a".charCodeAt(0); i--) {
            const character = document.createElement('div');
            character.innerText = String.fromCharCode(i);
            character.classList.add('character');
            characters.appendChild(character);
        }
        gbContainer.appendChild(characters);
    } else {
        // WHITE (mặc định)
        if (playerInfo.length >= 2) {
            playerInfo[1].classList.add("alliance-black");
            playerInfo[0].classList.add("alliance-white");
        }
        if (defeatedPieces.length >= 2) {
            defeatedPieces[1].classList.add("alliance-black");
            defeatedPieces[0].classList.add("alliance-white");
        }

        for (let i = 8; i >= 1; i--) {
            const digit = document.createElement('div');
            digit.innerText = i;
            digit.classList.add('game-board-container-digit');
            gbContainer.appendChild(digit);
        }
        const characters = document.createElement('div');
        characters.classList.add('game-board-container-character');
        for (let i = "a".charCodeAt(0); i <= "h".charCodeAt(0); i++) {
            const character = document.createElement('div');
            character.innerText = String.fromCharCode(i);
            character.classList.add('character');
            characters.appendChild(character);
        }
        gbContainer.appendChild(characters);
    }
    resize();
}

function resize() {
    const gbContainer = document.querySelector(".game-board-container");
    if (!gbContainer) return;
    const playerInfoContainer = document.querySelector(".player-info-container");
    const defeatedPiecesContainer = document.querySelector(".defeated-pieces-container");
    if (playerInfoContainer) playerInfoContainer.style.width = gbContainer.offsetHeight + "px";
    if (defeatedPiecesContainer) defeatedPiecesContainer.style.width = gbContainer.offsetHeight + "px";
    gbContainer.style.width = gbContainer.offsetHeight + 'px';
}

window.onresize = resize;

function addSteps(steps) {
    steps.forEach((step, index) => {
        if (index % 2 === 0) {
            const div = document.createElement("div");
            div.classList.add("step-item");
            div.innerHTML = `
                <div class="step-index">${Math.floor(index / 2) + 1}</div>
                <div class="step-container alliance-white">
                    <div class="step">${step.name}</div>
                </div>
                <div class="step-container alliance-black">
                    <div class="step"></div>
                </div>
            `;
            if (STEPS_CONTAINER) {
                STEPS_CONTAINER.appendChild(div);
                div.scrollIntoView({ behavior: "smooth" });
            }
        } else {
            const allSteps = document.querySelectorAll('.step');
            const lastStep = allSteps[allSteps.length - 1];
            if (lastStep) lastStep.innerHTML = step.name;
        }
    });
}

function getRole(username) {
    if (ROOM && ROOM.host && ROOM.host.username === username) {
        return "HOST";
    } else if (ROOM && ROOM.player && ROOM.player.username === username) {
        return "PLAYER";
    }
    return "VIEWER";
}

function initComponent(player, color) {
    const alliance = document.querySelector(".player-info.alliance-" + color);
    if (!alliance) return;
    const name = alliance.querySelector(".name");
    const elo = alliance.querySelector(".elo");
    const avatar = alliance.querySelector(".avatar");
    if (name) name.innerText = player.username || "???";
    if (elo) elo.innerText = player.elo || "---";
    if (avatar) avatar.style.background = `url('${player.avatar}') no-repeat center center / cover`;
}

function initStepsContainer(white, black) {
    if (!STEPS_CONTAINER) return;
    const div = document.createElement("div");
    div.classList.add("step-item");
    div.innerHTML = innerStepAvatar(
        white ? white.avatar : "/assets/img/icon.jpg",
        black ? black.avatar : "/assets/img/robot.png"
    );
    STEPS_CONTAINER.innerHTML = "";
    STEPS_CONTAINER.appendChild(div);
}

function setMatchNumber(number) {
    if (ROOM) ROOM.matchNumber = number;
}

function setMatchActiveId(id) {
    matchActiveId = id;
}

function setRoomData(data) {
    ROOM = data;
    ROLE = getRole(sessionStorage.getItem("USERNAME"));
}

function setRoom(data) {
    setRoomData(data);
    if (ROOM.matchNumber % 2 === 0) {
        whitePlayer = ROOM.host;
        blackPlayer = ROOM.player;
        if (ROLE === "PLAYER") {
            ALLIANCE = "BLACK";
            OPPONENT = "WHITE";
        } else {
            ALLIANCE = "WHITE";
            OPPONENT = "BLACK";
        }
    } else {
        whitePlayer = ROOM.player;
        blackPlayer = ROOM.host;
        if (ROLE === "HOST" || ROLE === "VIEWER") {
            ALLIANCE = "BLACK";
            OPPONENT = "WHITE"
        } else {
            ALLIANCE = "WHITE";
            OPPONENT = "BLACK";
        }
    }
    matchActiveId = ROOM.matchActiveId;
}

function setRoomAndComponents(data) {
    setRoom(data);
    loadPageFunction(ALLIANCE);
    if (whitePlayer) initComponent(whitePlayer, "white");
    if (blackPlayer) initComponent(blackPlayer, "black");
}

export {
    MODE,
    ROOM,
    ROLE,
    ALLIANCE,
    OPPONENT,
    matchNumber,
    whitePlayer,
    blackPlayer,
    globalState,
    matchActiveId,
    isMatchExecute,
    setRoom,
    addSteps,
    setRoomData,
    reCreateGame,
    loadPageFunction,
    initComponent,
    initStepsContainer,
    setMatchNumber,
    setMatchActiveId,
    setIsMatchExecute,
    setRoomAndComponents
};