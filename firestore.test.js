const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProgressPayload(uid) {
  const now = new Date(Date.now() - 1000);
  return {
    userId: uid,
    displayName: "Adoor Learner",
    streakDays: 4,
    completedQuizCount: 6,
    spokenPhrasesCount: 8,
    activeScreen: "HOME",
    activeDeckId: "deck_contractions",
    activeCardIndex: 0,
    activeQuizIndex: 0,
    activeScenarioId: "scenario_ammachi",
    lastDevice: "Android",
    createdAt: now,
    updatedAt: now,
  };
}

function validLearnedPhrasePayload(uid, phraseId) {
  const now = new Date(Date.now() - 1000);
  return {
    phraseId: phraseId,
    userId: uid,
    manglish: "Njaan ippo varoolla ketto",
    category: "Contractions",
    masteryLevel: 3,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or create user progress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validProgressPayload(ALICE_UID)));
});

test("Authenticated user: can create and read own progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProgressPayload(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user's progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProgressPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).set(validProgressPayload(ALICE_UID)));
});

test("Authenticated user: can update allowed progress fields and rejects ghost fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProgressPayload(ALICE_UID))
  );

  // Valid update
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      activeScreen: "FLASHCARDS",
      lastDevice: "Laptop Web",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Ghost field injection fails
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Negative streak fails
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      streakDays: -10,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Authenticated user: can create, read, and query own learned phrases", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("learned_phrases")
      .doc("ct_01")
      .set(validLearnedPhrasePayload(ALICE_UID, "ct_01"))
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("learned_phrases")
      .where("userId", "==", ALICE_UID)
      .get()
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("learned_phrases")
      .doc("ct_01")
      .get()
  );
});
