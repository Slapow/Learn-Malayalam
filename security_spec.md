# Security Specification — Parayu Cloud Progress & Session Continuity

## 1. Data Invariants
1. **Strict Ownership Isolation**: Every `/users/{userId}` document and `/users/{userId}/learned_phrases/{phraseId}` subcollection document belongs exclusively to `request.auth.uid == userId`. No user can read, list, create, update, or delete another user's progress or learned phrases.
2. **Schema & Key Completeness**: All writes must satisfy strict key allowlists (`hasAll` and `hasOnly` on create; `affectedKeys().hasOnly` on update) and field-level type/bound checks via `isValidUserProgress` and `isValidLearnedPhrase`.
3. **Immutable Identity & Creation Timestamps**: `userId`, `phraseId`, and `createdAt` cannot be modified after creation.
4. **Server Timestamp Bounds**: `createdAt` and `updatedAt` must be Firestore `timestamp` objects `<= request.time`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read**: `unauthDb.collection("users").doc("alice").get()` -> Reject (`request.auth != null`).
2. **Cross-User Profile Read**: `bobDb.collection("users").doc("alice").get()` -> Reject (`request.auth.uid == userId`).
3. **Spoofed Owner ID on Create**: `aliceDb.collection("users").doc("alice").set({ userId: "bob", ... })` -> Reject (`data.userId == request.auth.uid`).
4. **Shadow / Ghost Field Injection**: `aliceDb.collection("users").doc("alice").update({ isAdmin: true })` -> Reject (`affectedKeys().hasOnly(...)`).
5. **Negative Streak Poisoning**: `aliceDb.collection("users").doc("alice").update({ streakDays: -500 })` -> Reject (`data.streakDays >= 0`).
6. **Oversized String Exhaustion**: `displayName` > 100 chars -> Reject (`data.displayName.size() <= 100`).
7. **Future Timestamp Spoofing**: `createdAt` set in the future -> Reject (`data.createdAt <= request.time`).
8. **Mutating Immutable `createdAt`**: Updating `createdAt` on an existing profile -> Reject (`incoming().createdAt == existing().createdAt`).
9. **Mutating Immutable `userId`**: Updating `userId` on an existing document -> Reject (`incoming().userId == existing().userId`).
10. **Cross-User Subcollection Write**: `bobDb.collection("users/alice/learned_phrases").doc("ct_01").set(...)` -> Reject.
11. **Unfiltered Collection List**: `aliceDb.collection("users").get()` without `where("userId", "==", "alice")` -> Reject.
12. **Invalid Mastery Level**: `masteryLevel: 99` in `learned_phrases` -> Reject (`data.masteryLevel >= 1 && data.masteryLevel <= 5`).
