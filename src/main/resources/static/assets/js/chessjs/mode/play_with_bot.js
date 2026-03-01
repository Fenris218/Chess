import { createMatchBot } from "../../user/api/match.js";
import { innerStepAvatar } from "../components/message.js";
import { STEPS_CONTAINER } from "../helper/constants.js";
import { reCreateGame, loadPageFunction } from "../game_core.js";
import { renderScore } from "../score.js";

const $ = document.querySelector.bind(document);

async function startNewBotMatch() {
    const data = await createMatchBot();
    localStorage.setItem("MATCH_ID", data.id);

    if (STEPS_CONTAINER) {
        const div = document.createElement("div");
        div.classList.add("step-item");
        div.innerHTML = innerStepAvatar(
            data?.player?.avatar || "/assets/img/icon.jpg",
            "/assets/img/robot.png"
        );
        STEPS_CONTAINER.innerHTML = "";
        STEPS_CONTAINER.appendChild(div);
    }

    reCreateGame();
}

function initBotPage() {
    // 0) Hiển thị tỉ số từ session
    renderScore();

    // 1) Gán alliance classes cho DOM trước
    loadPageFunction("WHITE");

    // 2) Render bàn cờ + gắn sự kiện click
    reCreateGame();

    // 3) Luôn tạo ván mới khi vào trang
    startNewBotMatch().catch(err => {
        console.log("Không tạo được ván mới:", err.message);
    });

    // 4) Nút tạo ván mới
    const btnNewBotMatch = $("#create-new-match");
    if (btnNewBotMatch) {
        btnNewBotMatch.onclick = async () => {
            try {
                await startNewBotMatch();
            } catch (e) {
                console.log("Lỗi tạo ván mới:", e.message);
            }
        };
    }
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initBotPage);
} else {
    initBotPage();
}
