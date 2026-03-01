function createMatchOnline(whitePlayerId, blackPlayerId, roomId) {
    // Disabled in bot-only mode
    throw new Error("Online mode is disabled");
}

function createMatchBot() {
    return fetch("/api/matches/bot", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        redirect: "follow"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Something is wrong!");
            }
            return response.json();
        })
        .then(data => data.result);
}

function getMatch(type, matchId) {
    return fetch(`/api/matches/${type}/${matchId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(data => data.result);
}

function getPageMatch(page) {
    // Disabled in bot-only mode
    throw new Error("Human match history is disabled");
}

export { createMatchOnline, getMatch, createMatchBot, getPageMatch }