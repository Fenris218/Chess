const ROOT_DIV = null;
const GB_CONTAINER = document.querySelector(".game-board-container");
const OVERLAY = document.querySelector("#overlay");
const SQUARE_SELECTOR = ".square";
const WHITE_DEFEATED_PIECES = ".alliance-black.defeated-pieces";
const BLACK_DEFEATED_PIECES = ".alliance-white.defeated-pieces";
const PROMPT_PIECES = document.querySelector("#prompt-pieces");
const PROMPT_PIECE = document.querySelectorAll(".prompt-piece");
const STEPS_CONTAINER = document.querySelector(".steps-table");
const MOVE_SOUND = document.getElementById("step-sound");

function getRootDiv() {
    return document.getElementById("game-board");
}

export {
    ROOT_DIV,
    getRootDiv,
    GB_CONTAINER,
    OVERLAY,
    SQUARE_SELECTOR,
    WHITE_DEFEATED_PIECES,
    BLACK_DEFEATED_PIECES,
    PROMPT_PIECES,
    PROMPT_PIECE,
    STEPS_CONTAINER,
    MOVE_SOUND
};