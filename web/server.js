const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 3000;
const INDEX_PATH = path.join(__dirname, "index.html");

let latestSession = {
  streakDays: 0,
  learnedPhraseIds: [],
  activeScreen: "QUIZ",
  activeDeckId: "deck_level0_words"
};

const server = http.createServer((req, res) => {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    res.writeHead(204);
    res.end();
    return;
  }

  if (req.url === "/api/session" && req.method === "GET") {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify(latestSession));
    return;
  }

  if (req.url === "/api/sync" && req.method === "POST") {
    let body = "";
    req.on("data", chunk => { body += chunk; });
    req.on("end", () => {
      try {
        const parsed = JSON.parse(body);
        const allowedFields = [
          "streakDays", "learnedPhraseIds", "activeScreen", "activeDeckId",
          "activeCardIndex", "activeQuizIndex", "activeScenarioId",
          "completedQuizCount", "spokenPhrasesCount", "lastDevice"
        ];
        for (const key of allowedFields) {
          if (Object.prototype.hasOwnProperty.call(parsed, key)) {
            latestSession[key] = parsed[key];
          }
        }
      } catch (_) {}
      res.writeHead(200, { "Content-Type": "application/json" });
      res.end(JSON.stringify({ status: "ok" }));
    });
    return;
  }

  fs.readFile(INDEX_PATH, "utf8", (err, html) => {
    if (err) {
      res.writeHead(500, { "Content-Type": "text/plain" });
      res.end("Error loading web app");
      return;
    }
    res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
    res.end(html);
  });
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`Learn Malayalam Web Companion Server running on http://0.0.0.0:${PORT}`);
});
