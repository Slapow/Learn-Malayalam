const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const { EventEmitter } = require("node:events");

for (const file of ["web/index.html", "app/src/main/assets/parayu_web_companion.html"]) {
  test(`${file}: restored sessions retain learning progress and discard unsupported fields`, () => {
    const html = fs.readFileSync(path.join(__dirname, "..", file), "utf8");
    const initialState = html.match(/window\.appState = \{[\s\S]*?\n    \};/)[0];
    const restore = html.match(/function applySessionState\(data\) \{[\s\S]*?\n    \}/)[0];
    const context = vm.createContext({ window: {} });
    vm.runInContext(`${initialState}\n${restore}`, context);
    const expectedKeys = Object.keys(context.window.appState).sort();
    vm.runInContext(`applySessionState({
      streakDays: 7, learnedPhraseIds: ["n0_01"], activeDeckId: "deck_novice_1_kinship",
      completedQuizCount: 4, obsoleteReward: 500, unrelatedField: true
    })`, context);
    const restored = JSON.parse(JSON.stringify(context.window.appState));
    assert.deepEqual(Object.keys(restored).sort(), expectedKeys);
    assert.equal(restored.streakDays, 7);
    assert.deepEqual(restored.learnedPhraseIds, ["n0_01"]);
    assert.equal(restored.activeDeckId, "deck_novice_1_kinship");
    assert.equal(restored.completedQuizCount, 4);
    vm.runInContext("applySessionState(null); applySessionState([])", context);
    assert.equal(context.window.appState.streakDays, 7);
  });
}

test("session relay only returns supported learning fields", () => {
  let handler;
  const context = vm.createContext({
    require: name => name === "http" ? {
      createServer: callback => { handler = callback; return { listen() {} }; }
    } : require(name),
    __dirname,
    console: { log() {} }
  });
  vm.runInContext(fs.readFileSync(path.join(__dirname, "server.js"), "utf8"), context);
  const response = { setHeader() {}, writeHead() {}, end() {} };
  const request = new EventEmitter();
  request.url = "/api/sync";
  request.method = "POST";
  handler(request, response);
  request.emit("data", JSON.stringify({ streakDays: 8, learnedPhraseIds: ["n0_01"], obsoleteReward: 500 }));
  request.emit("end");
  let snapshot;
  handler({ method: "GET", url: "/api/session" }, {
    ...response, end: body => { snapshot = JSON.parse(body); }
  });
  assert.equal(snapshot.streakDays, 8);
  assert.deepEqual(snapshot.learnedPhraseIds, ["n0_01"]);
  assert.deepEqual(Object.keys(snapshot).sort(), [
    "activeDeckId", "activeScreen", "learnedPhraseIds", "streakDays"
  ]);
});
