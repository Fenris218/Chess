const timeWhite = {
    color: "WHITE",
    clock: null,
    endTime: 0,
    countDownInterval: null,
    isPaused: true
}

const timeBlack = {
    color: "BLACK",
    clock: null,
    endTime: 0,
    countDownInterval: null,
    isPaused: true
}

function getTimeRemaining(timeColor) {
    return 0;
}

function togglePause(timeColor) {
    // No-op in bot mode
}

function resetClock(whiteRemainTime = 0, blackRemainTime = 0) {
    // No-op in bot mode
}

export { timeWhite, timeBlack, getTimeRemaining, togglePause, resetClock }