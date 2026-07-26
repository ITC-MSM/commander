# Menu / Lobby Restructure + Player Guidance

Reorganise the client's entry points around a coherent model of what the app actually does, and add
the first real help surface. Argentum grew feature-first — quick games, six limited formats, four
table shapes, tournaments, Momir, ranked, accounts, friends, stats, replays, scenario tooling — and
was never re-organised around a player arriving for the first time.

Two problems, one project:

1. **Mode organisation.** The landing screen's `Quick Game | Tournament` toggle cuts across three
   independent axes at once, which makes several *already-implemented* modes effectively unreachable
   by reasoning.
2. **Guidance.** There is **no help surface of any kind** in the client — no tutorial, no shortcut
   list, no FAQ, no `?` button. Every explanation in the app is a native `title=` tooltip.

This is an `add-feature` project (client capability, plus a tail of server work in Phase 5).

**Audience we optimise for: knows Magic, new to Argentum.** Not a rules tutorial — no teaching what
a phase or the stack is. Explicitly out of scope at the bottom.

## Status: **in progress** (2026-07-26) — Phases 0, 1, 2, 3, 4 landed

| Phase | State |
|---|---|
| 0 — split `GameUI.tsx` | **done** |
| 1 — landing restructure | **done** |
| 2 — axis renaming in both lobbies | **done** |
| 3 — help (`topics.ts`, `/help`, `HelpTip`, `shortcuts.ts`) | **done** |
| 4 — unified lobby over a view model | **done** |
| 5 — server gaps (4c) | not started |
| 6 — `convertLobby`, real URLs | not started |

Phase 1 landed the **Cards / Table / Event** vocabulary as
[`web-client/src/components/lobby/axes.ts`](../web-client/src/components/lobby/axes.ts) and the six
presets as [`modePresets.ts`](../web-client/src/components/ui/modePresets.ts). Phase 2 wired both
lobbies' controls onto that vocabulary and gave `axes.ts` the server-mapping half it was missing.

Sequenced behind [`cube-draft-format.md`](cube-draft-format.md), which is the current next pick.
Phases 0–3 are independent of Cube and can land in any order relative to it; Phase 2's taxonomy is
what gives Cube a place to slot in without a seventh top-level button, so landing Phase 2 *before*
Cube ships is worth a little scheduling effort.

## Confirmed scope decisions

- **Keep the centred glass card.** No persistent nav bar. The landing stays a single
  `styles.contentBackdrop` panel; only its *contents* get restructured into tiers.
- **Presets, not modes.** The home screen shows six named entry points that set lobby defaults. They
  are not six systems — they all land in one lobby.
- **Unify the lobby presentation first, the server second.** A full server-side lobby merge is a
  large project on its own (see § *The honest constraint*); the client unifies over a view model and
  the server gaps get closed behind it, individually.
- **Dev tools stay dev tools.** Scenario Builder, Set Completion and LLM Tournament group under a
  **Lab** heading with an explicit "debugging and content tools, not part of normal play" caption.
  They are *not* reframed as practice modes, and hotseat is not promoted to a real Table value —
  it's a debugging affordance, not a way people are meant to play.
- **Help is one content source, two surfaces.** A typed topic registry feeds both the `/help` page
  and the inline `HelpTip` popovers. This is the constraint that prevents the drift that made the
  existing scattered tooltips near-useless.

---

## Part 1 — Inventory of what the client does today

Captured 2026-07-26. This is reference material for the restructure; it is also the first time the
client's full surface has been written down in one place.

### Routes (`web-client/src/main.tsx:62–79`)

| Route | File | Reached from |
|---|---|---|
| `*` → `/` | `App.tsx` → `components/ui/GameUI.tsx` | default |
| `/deckbuilder`, `/deckbuilder/:deckId` | `components/deckbuilder/DeckbuilderPage.tsx` | home button |
| `/scenario` | `components/scenario/ScenarioBuilderPage.tsx` | home button |
| `/set-completion` | `components/setCompletion/SetCompletionPage.tsx` | home button |
| `/replay/:gameId` | `components/replay/ReplayPage.tsx` | game-over overlay, profile, admin |
| `/profile` | `pages/ProfilePage.tsx` | `AuthWidget` only |
| `/stats` | `pages/StatsPage.tsx` | **`/profile` only — orphaned** |
| `/u/:userId` | `pages/PublicProfilePage.tsx` | **opponent names in tables only — orphaned** |
| `/friends` | `pages/FriendsPage.tsx` | `AuthWidget`, `/profile` |
| `/admin` | `components/admin/AdminPage.tsx` | `AuthWidget` when `user.isAdmin` |
| `/llm-tournament`, `/llm-tournament/:id` | `components/llmTournament/LlmTournamentPage.tsx` | **DEV-only button; route itself ungated** |
| `/tournament/:lobbyId` | `components/tournament/TournamentEntryPage.tsx` | share link; also where a mid-lobby refresh lands |
| `/join/:lobbyId` | `components/lobby/JoinLobbyPage.tsx` | QR / share link (`utils/joinLink.ts`, `JoinQrModal.tsx`) |
| `/login/verify` | `pages/LoginVerifyPage.tsx` | magic-link email |

Query-param sub-modes with no route: `/?spectate=<id>` (`App.tsx:55–100`, only ever emitted by the
LLM tournament page — the normal spectate path uses store state, so **live games have no shareable
URL**), `/?token=<t>` (session assumption, how Scenario Builder hands you a seat),
`/deckbuilder?d=` / `?decks=open` / `?q=&sort=&view=&fmt=`, `/scenario?s=` / `?replay=&frame=`,
`/?profile=1` (render profiler).

### Screens with no URL at all

Everything inside `/` is a Zustand state-machine view selected in `App.tsx:280–299` and
`GameUI.tsx:81–297`: name entry, home, quick-game lobby, tournament lobby, premade deck picker,
booster/Winston/grid draft, limited deck builder, tournament standings, FFA standings, waiting for
opponent, replay browser overlay, match intro, mulligan, game board, game over, spectator board,
session-replaced.

Consequences: nothing here can be bookmarked, shared, or deep-linked, and **browser Back exits the
app** rather than stepping back a screen. `App.tsx:103–113` rewrites the address bar to
`/tournament/<id>` with raw `history.replaceState`, bypassing the router — so React Router's
location is stale relative to the address bar during lobby play.

### Duplicated surfaces

- **Two deckbuilders:** `/deckbuilder` (constructed, 4900 lines) vs
  `components/sealed/DeckBuilderOverlay.tsx` (limited). No cross-link.
- **Two replay viewers:** the `/replay/:gameId` route vs the `components/admin/ReplayViewer.tsx`
  overlay behind the home screen's "Game Replays" button. Same controls, different code.
- **Two lobbies:** `QuickGameLobbyOverlay.tsx` vs `LobbyOverlay` (inside `GameUI.tsx:763`).

### Every way to start a game

| Path | File | Notes |
|---|---|---|
| Quick Game vs human | `QuickGameLobbyOverlay.tsx` | private/public, casual/ranked, legality format, `DeckPicker` |
| Quick Game vs AI | same | one click from home when `aiEnabled` |
| Momir Basic | `QuickGameLobbyOverlay.tsx:293–409` (`CUSTOM_FORMATS`) | no deckbuilding, 60 basics |
| Sealed (Standard / Commander) | `LobbyOverlay` → `sealed/DeckBuilderOverlay.tsx` | 1–16 boosters, per-set distribution, chaos boosters |
| Booster Draft | `draft/DraftPickOverlay.tsx` | 3–8 players, 1–6 packs, pick timer, 1–2 picks/pack |
| Winston Draft | `draft/WinstonDraftOverlay.tsx` | exactly 2 players |
| Grid Draft | `draft/GridDraftOverlay.tsx` | 2–4 players |
| Commander Draft / Sealed | `LobbyOverlay` | 2 players, Brawl or Commander presets |
| Premade Decks event | `PremadeDeckPickerPanel` (`GameUI.tsx:1778`) | everyone brings their own deck |
| Free-for-All | `FreeForAllOverlay` (`GameUI.tsx:1883`) | 2–6, attack-mode any/left/right (CR 802/803) |
| Two-Headed Giant | lobby `gameMode` | exactly 4, shared 30 life (CR 810) |
| Team vs Team | lobby `gameMode` | 4/6/8, own life and turns (CR 808) |
| Scenario / hotseat | `scenario/ScenarioBuilderPage.tsx` | SELF / AI / TWO_PLAYER, 3–4 seat pods |
| Spectate | `LiveGameList`, `/?spectate=`, eliminated "Keep Watching" | `spectating/SpectatorGameBoard.tsx` |
| Replay | `/replay/:gameId`, `ReplayViewer` overlay | scrub, share-as-scenario, snapshot download |

No gauntlet mode exists (grep-verified).

### Existing help content

Zero dedicated surface. The one real help affordance in the whole app is the deckbuilder's
search-syntax popover (`DeckbuilderPage.tsx:2593` → `SearchHelp` at `:2618`).

Good copy that already exists and should be **harvested into topics, not rewritten**:

- `OpponentRail.tsx:211–290` — Overview / Follow camera. The best-written help in the app.
- `GameBoard.tsx:1400–1423` — Auto Tap / Manual Tap, and Auto / Stops / Full Control. The *only*
  place priority modes are explained anywhere.
- `GameCard.tsx:1514–1539` — Plotted ("CR 718 — cast it for free on a later turn"), Prepared, Warped.
- `SpeedGauge.tsx` — how speed rises and what max speed unlocks.
- `TheRingBadge.tsx:14–19` — the four Ring ability texts.
- `GameUI.tsx:989–1600` — ~30 lobby setting tooltips (game modes, ranked, FFA/2HG/team, attack
  left/right, Brawl vs Commander presets, singleton, AI assist, random teams).
- `ReplayPage.tsx:302` — share-as-scenario.
- `StepStrip.tsx:484,492` — "My turn stop" / "Opponent turn stop".
- `QuickGameLobbyOverlay.tsx:184–388` — host-only settings, ranked eligibility.

### Undocumented power features

Nothing in the UI hints at any of these:

- **Right-click / long-press a stack item → yield menu** (`StackZone.tsx:174` →
  `YieldContextMenu.tsx`): yield until end of turn, always yield, always answer Yes/No, revoke.
  `ActiveYieldsPanel.tsx` only appears *after* you've set one, so it can't teach the feature.
- **Stop dots** revealed by hovering a step pip (`StepStrip.tsx:469–497`), persisted to
  `localStorage['argentum-stop-overrides']`.
- `D` toggles the deck browser (`ZonePiles.tsx:149`) — documented only in a `title`.
- `F` flips a DFC while hovering (`CardPreview.tsx:44`, `useDfcHoverFlip.tsx:30`) — undocumented.
- `0` table overview, `1`–`9` opponent boards, `Esc` unpin (`useMultiplayerView.ts:63–73`).
- Draggable decision banners (`DraggableBanner.tsx`).
- Card-stack ungroup (`CardStack.tsx:27`), attachment browser (`Battlefield.tsx:340–390`).
- Swipe left/right on the opponent strip (`GameBoard.tsx:841–858`).
- Drag-to-cast from hand, drag-to-band attackers, drag-to-block (`GameCard.tsx:496–710`).

> **Found while surveying:** `useMultiplayerView.ts:64` has a comment claiming number keys activate
> abilities when a card's action menu is open. The guard is real; **no such handler exists anywhere
> in the codebase.** Either implement it or delete the comment.
>
> **Also found:** `components/spectating/SpectatorView.tsx` is dead — superseded by
> `SpectatorGameBoard.tsx`, imported nowhere.

---

## Part 2 — Mode taxonomy: three axes, not one toggle

Everything implemented today is a point in this space:

| Axis | Values implemented | Where it lives now |
|---|---|---|
| **Cards** — where your deck comes from | Bring a deck · Random pool · Momir Basic · Sealed (Standard / Commander) · Draft (Booster / Winston / Grid / Commander) | split across **two unrelated controls**: the legality dropdown at `QuickGameLobbyOverlay.tsx:23` and the `Format` row at `GameUI.tsx:971` |
| **Table** — who is at it | 1v1 · Free-for-All (2–6) · Two-Headed Giant (4) · Team vs Team (4/6/8) | `GameUI.tsx:1056` — only visible *after* picking "Multiplayer" |
| **Event** — one game or a series | Single game · Round-robin bracket + standings | **not its own axis** — jammed onto Table as `Mode: Tournament \| Multiplayer` (`GameUI.tsx:998`) |

### The core diagnosis

**"Tournament" is not a peer of "Sealed" — it's a peer of "single game."**

Because Event was never separated out as its own axis, the word got promoted to the home screen,
where it now labels *all limited play*. Everything else follows from that one mistake:

- A **1v1 sealed game with one friend** requires clicking a button labelled **Tournament**.
- **"4-player free-for-all with my own deck"** = Tournament → Create Lobby → Format **Premade** →
  Mode **Multiplayer** → Variant **Free-for-All**. Four steps, the first of which is misleading.
  This combination is *fully supported server-side* — verified at `LobbyHandler.kt:1304–1335`
  (the `PREMADE_DECKS` start branch dispatches to `FreeForAllHandler.maybeStartGame`) and
  `FreeForAllHandler.kt:47–111` (premade + commander-shape + team stamping all handled). It is a
  discoverability failure, not a missing feature.
- **Momir Basic** is a card inside a dropdown inside the quick lobby — invisible from home.
- **"Format"** means deck legality in one lobby and pool type in the other. **"Mode"** means
  quick-vs-tournament on home but table shape in the lobby. Two words, four meanings.

### Renaming (worth doing even if nothing else ships)

| Old | New | Values |
|---|---|---|
| Format (quick) / `deckFormat` | folded into **Cards** → "Bring a deck" sub-option | Standard, Pioneer, Modern, Pauper, Legacy, Vintage, Commander, Brawl, Standard Brawl, Premodern |
| Format (tournament) | **Cards** | Bring a deck · Random · Sealed · Draft · Momir |
| Mode / Variant | **Table** | 1v1 · Free-for-All · Two-Headed Giant · Team vs Team |
| *(implicit in `gameMode`)* | **Event** | Single game · Round-robin bracket |

Sub-options hang off their own axis only: Draft → Booster / Winston / Grid / Commander; Sealed →
Standard / Commander; Bring a deck → the legality dropdown.

**Cube is the test that the taxonomy is right.** [`cube-draft-format.md`](cube-draft-format.md)
adds one new **Cards** value (with Pool Play as a sub-option), not a seventh top-level button. If a
new mode ever needs a new home-screen button, the taxonomy was wrong.

Naming the axes also makes the real holes *visible* instead of hiding them behind impossible click
paths: ranked is 1v1-bracket-only, there is no 2HG bracket, AI can't join premade or FFA. Those are
Phase 5.

---

## Part 3 — Landing screen

Keep the centred glass card. Restructure its contents into three labelled tiers.

```
                    Argentum Engine

  ── PLAY ────────────────────────────────────────────
  ┌────────────┬────────────┬────────────┬───────────┐
  │ vs AI      │ vs Friend  │ Draft/Seal │ Multiplyr │
  │ 1v1 · now  │ 1v1 · code │ 2–8 · packs│ 3–8 · FFA │
  └────────────┴────────────┴────────────┴───────────┘
  ┌────────────┬────────────┐
  │ Tournament │ Variants   │
  │ bracket    │ Momir      │
  └────────────┴────────────┘
  join code: [________] (Join)
  Continue → [Sealed lobby ABC12]        ← only if one is live

  ── BUILD & BROWSE ──────────────────────────────────
  Deckbuilder · Replays · Stats · Friends · Profile

  ── LAB (advanced) ──────────────────────────────────
  Scenario Builder · Set Completion · LLM Tournament[dev]
  "Debugging and content tools, not part of normal play."
```

Implementation rules:

- **One source of truth for the cards.** `src/components/ui/modePresets.ts`:
  ```ts
  interface ModePreset {
    id: string
    title: string
    tagline: string          // "Open packs and build a 40-card deck"
    players: string          // "2–8"
    duration: string         // "~40 min"
    needsDeck: boolean
    helpTopicId: string
    defaults: { cards: CardsAxis; table: TableAxis; event: EventAxis }
  }
  ```
  Consistent metadata on every card is what lets a newcomer *compare* modes instead of guessing.
  `defaults` is the axis triple the lobby opens with.
- Every card carries a `⃝?` bound to its `helpTopicId` (Part 5).
- **BUILD & BROWSE finally gives `/stats` and `/profile` a home-screen entry.** They are orphaned
  today — `/stats` is two clicks deep behind a non-obvious affordance.
- **Continue chip** — if a lobby or game is live, surface it. Today a refresh mid-lobby dumps you on
  `/tournament/:lobbyId` with no indication that's what happened.
- Unchanged and staying where they are: guest name entry, `AccountBenefitsCallout`,
  `DeckMigrationPrompt`, `AuthWidget`, `PublicLobbyList`, `LiveGameList`, the Scryfall / Mana Font
  attribution and the WotC fan-project disclaimer.

---

## Part 4 — One lobby, three axes

**Goal:** every preset lands in the same lobby, which always shows Cards / Table / Event. Someone who
entered via "vs AI" can add a human and switch to Free-for-All without backing out to the menu.

### The honest constraint

The server has **two unrelated lobby implementations with no shared interface**:

| | Quick game | Tournament |
|---|---|---|
| Model | `lobby/QuickGameLobby.kt` (125 lines) | `lobby/TournamentLobby.kt` (1884 lines) |
| Storage | in-memory `ConcurrentHashMap`, not persisted | Redis via `PersistentTournamentLobby` + `persistence/LobbyConverter.kt` |
| Players | hard `MAX_PLAYERS = 2` (4 for its unused 2HG path) | 2–8, host field, spectators |
| Shape | no state machine; flat DTO + per-field setters | `LobbyState` machine; nested `settings` + one 21-field `updateLobbySettings` |
| Handler | `handler/QuickGameLobbyHandler.kt` (678) | `handler/LobbyHandler.kt` (2412) + 5 sub-handlers |

There is no `kind` discriminator anywhere. So a *fully* unified lobby is **not** a client-only
change. This plan unifies the presentation first and closes server gaps behind it, individually.

One bridge already exists: `QuickGameLobbyHandler.handleJoin:241–248` delegates to the tournament
join handler when the code belongs to a tournament lobby, so the home Join field is already
kind-agnostic.

### 4a. Client: one lobby screen over a view model

- `src/components/lobby/lobbyViewModel.ts` — a `UnifiedLobbyView` type plus
  `fromQuickGameLobby(state)` and `fromTournamentLobby(state)`. Both produce
  `{ lobbyId, isHost, players[], axes: { cards, table, event }, capabilities, ready, canStart }`.
- `src/components/lobby/LobbyScreen.tsx` renders the view model. `GameUI.tsx:95`'s hard either/or
  switch becomes: build the view model from whichever slice is populated, render one screen.
- `src/components/lobby/LobbyAxes.tsx` renders the three axes from a declarative descriptor
  (`lobbyAxes.ts`) that also encodes which values the *current backing kind* supports. Unsupported
  values render **disabled with a HelpTip explaining why** — not hidden. Visible constraints beat
  invisible ones.
- **The CSS is already shared.** `QuickGameLobbyOverlay.tsx` imports `GameUI.module.css` and
  deliberately reuses `lobbyOverlay` / `lobbyContent` / `lobbyHeader` / `inviteBox` /
  `playerListPanel` / `actionsRow` / `settingsRow` / `variantGroup` / `variantCaption` — its header
  comment says so. The merge is structural, not visual.
- Reused untouched: `DeckPicker.tsx`, `SetPickerModal`, `BanListEditor.tsx`, `JoinQrModal.tsx`,
  `utils/joinLink.ts`, `PremadeDeckPickerPanel`.

### 4b. Switching axes across lobby kinds

When the host picks a value the current backing kind can't express (quick game → Free-for-All), the
client tears down and recreates on the other kind.

- **v1, no server work:** confirm first — *"Switching to Free-for-All creates a new lobby. Your
  invite code will change."* Recreate, re-copy the link. Acceptable because it only bites the host
  before anyone has joined, which is the common case.
- **v2, needs server:** a `convertLobby` message preserving `lobbyId` and joined players. Scope
  separately; **do not block v1 on it.**

### 4c. Server gaps that keep holes in the matrix

Each is independent. Ordered by value.

| # | Gap | Where | Why it matters |
|---|---|---|---|
| 1 | **AI rejected in `PREMADE_DECKS` and in all FFA/team modes** | `LobbyHandler.kt:1386–1393`, `:1395–1403`, `:2080–2088` | "Bring a deck + vs AI" and "FFA with AI seats" both read as obvious once the axes are visible, and both currently fail. Highest-value fix in the list. |
| 2 | **Momir exists only on quick lobbies** | `lobby/MomirBasicSetup.kt`, `QuickGameLobbyHandler`; `grep -i momir` over `TournamentLobby.kt` / `LobbyHandler.kt` / `FreeForAllHandler.kt` / `PersistentLobby.kt` = **zero hits** | Momir can't be a Cards value on the unified lobby until it exists tournament-side. Needs the flag, the `MomirBasicSetup` wiring in `TournamentMatchHandler` / `FreeForAllHandler`, and a `Ranked.modeForQuickGame` equivalent. |
| 3 | **No per-player "random pool" in premade** | `QuickGameLobbyPlayer.setCode` (empty `deckList` = server picks); tournament SEALED forces a `DECK_BUILDING` phase | "Random" is the zero-prep on-ramp — the fastest path from cold open to playing. It must survive the merge. |
| 4 | **No per-player ready in tournament `WAITING_FOR_PLAYERS`** | host presses `startTournamentLobby`; there is no per-player ready toggle | The 2-player "both ready → go" flow is what makes a quick game *feel* quick. |
| 5 | **Ranked gated to `gameMode == TOURNAMENT`** | `TournamentLobby.rankedEligible` (`:335–358`) vs `QuickGameLobby.rankedEligible` + `Ranked.modeForQuickGame` | Two different ranked paths need reconciling before ranked can appear on one axis panel. Both already silently downgrade to unranked at start, so the failure mode is safe but confusing. |
| 6 | **Quick-lobby 2HG is phantom capability** | `QuickGameLobby.twoHeadedGiant` + `TWO_HEADED_GIANT_PLAYERS = 4` are implemented; `grep -rn twoHeadedGiant web-client/` = **0 hits**, and it's missing from `QuickGameLobbyStateMessage` (`types/messages.ts:2711–2727`) along with `maxPlayers` and `QuickGameLobbyPlayerView.teamIndex` | Either wire it up or delete it. Right now it's server capability no client can reach. |
| 7 | **`PersistentTournamentLobby` missing fields** | `persistence/LobbyConverter.kt` — lacks `deckFormat`, `ranked`, `bannedCardNames`, `deckSizeMin`, `allowDuplicates`, `commanderPreset`, `ffaLastStandings` | Any new unified setting needs a converter pass or it won't survive a restart. Pre-existing bug, worth fixing while in here. |

---

## Part 5 — Guidance

Two surfaces, **one content source**. This is the whole design.

### 5a. Content model — `src/help/topics.ts`

```ts
export type HelpSection = 'getting-started' | 'modes' | 'playing' | 'decks' | 'advanced'

export interface HelpTopic {
  id: string                 // 'priority-modes', 'table-free-for-all', 'yields'
  section: HelpSection
  title: string
  summary: string            // 1–2 sentences — what the popover shows
  body?: ReactNode           // longer prose, only rendered on /help
  related?: string[]         // other topic ids
  shortcuts?: string[]       // ids from shortcuts.ts
}
```

Typed TS, not markdown: there is no markdown pipeline in the client, `public/` ships no docs, and
the Dockerfile copies only `dist/` + `nginx.conf` — so the repo's `docs/` is not reachable from the
browser and never will be without new build machinery.

**Seed it by moving the good `title=` copy listed in Part 1 into topics**, then having those call
sites reference the topic id instead of holding the string. Net new prose is small; the win is that
there is now exactly one place each explanation lives.

### 5b. `/help` route — `src/pages/HelpPage.tsx`

Registered in `main.tsx`. Deep-linkable as `/help/playing#priority-modes`.

1. **Getting started** — pick a name, guest vs account, start your first game, where decks live.
2. **Game modes** — one entry per Part 2 axis value plus the six presets. Documents the three-axis
   model using the exact words the lobby uses.
3. **Playing a game** — phase bar and stops, priority modes (Auto / Stops / Full Control), passing
   and resolving, Auto vs Manual Tap, targeting and combat drag, yields, undo, the log, zone
   browsers, the deck tracker.
4. **Decks** — constructed deckbuilder, search syntax (link the existing `SearchHelp` popover rather
   than duplicating it), Arena import/export, share links, sealed/draft building, sideboard.
5. **Advanced** — keyboard shortcut table, replays and replay-to-scenario, spectating, multiplayer
   camera (Overview / Follow / pin), Lab tools.

### 5c. `HelpTip` — `src/components/help/HelpTip.tsx`

A `⃝?` button taking a `topicId`. Popover shows the topic's `summary` plus "Read more →" linking to
`/help/<section>#<id>`.

**Must be portal-rendered.** The app is `overflow: hidden` and the multiplayer strip uses a CSS
transform, which breaks `position: fixed` — see the existing portal overlays in `ZonePiles.tsx` for
the established pattern.

Minimum placement: every home mode card; every lobby axis row; the in-game priority-mode button,
Auto Tap button and step-strip stop dots; the stack yield menu; the ranked toggle; the Overview /
Follow buttons.

### 5d. Persistent `?` entry

No nav bar, so: a small fixed help button beside `FullscreenButton` / `AuthWidget` on the home
screen, and in the in-game top chrome. Home opens `/help`; in-game opens a **drawer** rather than
navigating, so it doesn't drop the WebSocket.

### 5e. Keyboard shortcut registry — `src/help/shortcuts.ts`

One declarative list. Shortcuts are currently scattered across `useMultiplayerView.ts`,
`ZonePiles.tsx`, `CardPreview.tsx`, `useDfcHoverFlip.tsx`, `ReplayPage.tsx`, `ActionMenu.tsx`,
`DeckbuilderPage.tsx`, with no index anywhere. The `/help` Advanced section renders it as a table.

Complete list as of 2026-07-26: `1`–`9` opponent boards · `0` overview · `Esc` unpin / close modal /
close zone browser / exit replay · `D` deck browser · `F` flip DFC on hover · `←`/`→` replay frame ·
`Space` replay play-pause · `Enter` submit · Shift-click / right-click removes a deckbuilder copy.

Resolve the phantom number-key comment (`useMultiplayerView.ts:64`) while doing this.

---

## Phasing

`GameUI.tsx` is 2992 lines holding five screens. Phase 0 is a prerequisite for everything else.

| Phase | Work | Ships value alone? |
|---|---|---|
| ~~**0**~~ | ~~Split `GameUI.tsx`~~ — **done.** `HomeScreen.tsx` (709), `components/lobby/LobbyOverlay.tsx` (1123), `components/tournament/{TournamentOverlay,FreeForAllOverlay}.tsx`, shared `FullscreenButton.tsx`. `GameUI.tsx` is now a 34-line router. | no — enabler |
| ~~**1**~~ | ~~Landing restructure~~ — **done.** `axes.ts` + `modePresets.ts`, three tiers, Lab caption, Continue chip, `/stats` `/friends` `/profile` surfaced. | **yes** |
| ~~**2**~~ | ~~Axis renaming across both lobbies~~ — **done.** `axes.ts` gained the server-mapping half; both lobbies show a `LobbyAxisSummary`; the tournament lobby's Format/Mode/Variant rows became Cards (+ sub-options) / Table / Event. | **yes** — kills the Format/Mode overloading |
| ~~**3**~~ | ~~Help~~ — **done.** `src/help/{topics,shortcuts,helpStore}.ts`, `/help/:section`, portal `HelpTip`, in-game drawer, `?` on home. | **yes** |
| ~~**4**~~ | ~~Unified lobby~~ — **done.** `lobbyViewModel.ts` + `axisChoices.ts` + `useLobbyCommands.ts` behind one `LobbyScreen`; both old overlays deleted; v1 recreate-on-switch confirm. | **yes** |
| **5** | Server gaps from 4c, in the numbered order. | yes, each |
| **6** | Optional: `convertLobby` preserving the invite code; real URLs for in-`/` screens so Back works. | yes |

### What Phases 0/1/3 actually shipped

- **New files.** `components/lobby/axes.ts`, `components/ui/modePresets.ts`,
  `components/ui/SettingsLabel.tsx`, `components/ui/FullscreenButton.tsx`,
  `components/help/{HelpTip,HelpDrawer,HelpTopicView}.tsx` + `help.module.css`,
  `help/{topics,shortcuts,helpStore}.ts`, `pages/HelpPage.tsx` + module CSS.
- **Verified against the running stack.** All six presets walked from a cold home screen;
  "Multiplayer" lands on `Premade Decks Free-for-All` (the combination the plan calls out as
  supported-but-unreachable), "Variants" on a Momir Basic lobby, "Draft & Sealed" on a sealed lobby.
  The in-game drawer was opened from a real vs-AI game.
- **`ModePreset.launch`** is the seam onto today's two unrelated server lobby kinds. Phase 4/5 close
  it; until then the home screen stays declarative and only one function knows the mapping.
- **e2e specs updated.** The lobby/draft specs were already stale (they typed into a placeholder
  renamed some time ago); they now click the `mode-preset-draft-sealed` test id.
- **Resolved from Part 1's findings:** the phantom number-key comment at `useMultiplayerView.ts:64`
  (no such handler exists — comment corrected, feature not invented). Still open: dead
  `components/spectating/SpectatorView.tsx`, and the two duplicated deckbuilders / replay viewers.

### What Phase 2 actually shipped

- **`axes.ts` gained the half it was missing.** Phase 1 gave it the vocabulary; Phase 2 gave it the
  translation onto the two server lobby kinds — `axesFromLobbySettings`, `axesFromQuickGameLobby`,
  `tableFromGameMode` / `gameModeForTable`, `cardsFromTournamentFormat`, `eventFromGameMode`,
  `eventUnavailableReason`, the `*TopicId` helpers and a shared `LEGALITY_OPTIONS` derived from the
  deckbuilder's `DECK_FORMATS`. One module now knows the mapping, which is what Phase 4's view model
  will be built on.
- **`components/lobby/LobbyAxisSummary.tsx`** — Cards/Table/Event chips in *both* lobby headers,
  each with a `HelpTip` bound to the value in effect. It replaced the one-word `lobbyFormat` chip.
  Notably this is the first time a **non-host** can see what they joined: the settings panel is
  host-only.
- **Tournament lobby rows restructured**, not just relabelled:
  - `Format: Sealed|Draft|Premade` → `Cards: Bring a deck|Sealed|Draft`, with its sub-options
    (deck legality, sealed shape, draft shape) as indented rows directly beneath it. The deck-format
    dropdown moved up from the bottom of the panel to sit under the value it belongs to.
  - `Mode: Tournament|Multiplayer` + `Variant: FFA|2HG|Team` → one flat
    `Table: 1v1|Free-for-All|Two-Headed Giant|Team vs. Team`. The old pair made 1v1 a peer of
    "multiplayer" rather than of the three shapes, and hid the shapes behind a click.
  - New `Event: Single game|Round-robin bracket` row. Derived from Table today, so the unreachable
    value renders **disabled with the reason attached** (`eventUnavailableReason`) — that is the
    Phase 5 hole, made visible instead of hidden. `.settingsButton:disabled` got a style, which the
    already-disabled Winston/Grid/Commander buttons had been silently missing.
  - Draft "Normal" → **"Booster"**, matching `cardsLabel()` and the plan's taxonomy.
- **Quick lobby**: `FormatSelector` → `CardsSelector`; "Format" → "Cards"; the dropdown reads
  "Bring a deck — no restriction"; "or pick a custom format" → "or pick a variant".
- **`Games per matchup` is now gated on `event === ROUND_ROBIN`** (was `!isFfa`), so it stops
  appearing on 2HG and Team vs. Team tables where a single shared game made it a no-op.
- **New topic `axis-limits`** documents every combination that isn't wired up yet, cross-linked from
  `axes`, `ranked` and both event topics.

Verified against the running stack by walking the tournament lobby through Sealed → Bring a deck
(+ Modern) → Free-for-All → 2HG → Draft and the quick lobby through Bring a deck → Pauper → Momir,
asserting the header chips against the control state at each step. One honest bug surfaced and was
fixed doing this: the quick lobby's Cards chip read "Bring a deck" while the deck picker sat on its
Random tab. Random pool is per-player, not a lobby setting, so `axesFromQuickGameLobby` now takes
the viewer's seat and reports `RANDOM` when they have submitted an empty deck (the server's own
"roll me one" signal). A chip contradicting the control under it is precisely the drift this
vocabulary exists to remove.

Two things fixed on the way past, both reported by Vincent:

- **`/help` could not be scrolled.** `HelpPage.module.css` used `min-height: 100vh` with
  `overflow-y: auto` — with `min-height` the box grows to its content, so there is nothing to
  scroll, and `#root { height:100%; overflow:hidden }` just clips it. Now `height: 100vh`, the same
  fix `SetCompletionPage` and `adminUi` already document. The in-game drawer was never affected
  (fixed positioning + `flex:1; overflow-y:auto`).
- **`the-ring` and `speed` topics removed.** They explained MTG mechanics rather than what Argentum
  does with them, which is out of this project's stated scope. Neither had a call site — both were
  `/help`-only. The in-game `title=` tooltips on `TheRingBadge` and `SpeedGauge` are untouched, which
  is the right home for a mechanic explainer. `card-badges` stays: it answers "what is this label the
  client is drawing on my card", which is unanswerable from the card text.
- **Scenario Builder is now dev-only** on the home screen, alongside LLM Tournament — it drives
  `/api/dev/scenarios/*`, which a production server does not expose. The *route* stays open in both
  builds, because a replay's "share as scenario" link is a real `/scenario?s=` deep link.

### What Phase 4 actually shipped

**One screen.** `QuickGameLobbyOverlay` (454) and `LobbyOverlay` (1131) are deleted; every lobby is
[`components/lobby/LobbyScreen.tsx`](../web-client/src/components/lobby/LobbyScreen.tsx). They
already shared a stylesheet — `QuickGameLobbyOverlay`'s header comment said so — but not a line of
structure, and the behaviour had quietly diverged: only one had a fullscreen button, only one showed
a QR code, they disagreed about who could see the settings, and the axes were editable on one of
them. All of that is now one answer.

Three new modules, one job each:

- **`lobbyViewModel.ts`** — `UnifiedLobbyView` plus `fromQuickGameLobby` / `fromTournamentLobby`.
  Pure. Everything the two kinds genuinely disagree about is a *field* (`startModel`,
  `primaryAction`, `invitable`, `canAddAi`, `teams`, `ranked.available`) rather than a branch at
  every call site — which is what lets Phase 5 close the server gaps without touching the screen.
- **`axisChoices.ts`** — for every Cards / Table / Event value on *this* lobby: `DIRECT`,
  `RECREATE` onto the other backing kind, or `BLOCKED` with the reason. This supersedes `axes.ts`'s
  kind-blind `eventUnavailableReason`, which was removed.
- **`useLobbyCommands.ts`** — the write side of the same seam, including the recreate.

**The axes are now the lobby's primary control, on both kinds.** Before, a quick lobby had a
`Cards` dropdown and nothing else; the tournament lobby had all three rows. Now both show all three
rows and all five Cards values.

**Cross-kind switching (4b v1).** Values the current implementation can't express are marked `⇄`
and, on click, confirm before tearing the lobby down and recreating it on the other kind. The
confirm counts what is actually lost — the invite code always, joined humans and AI seats and
submitted decks only when there are any — rather than warning in the abstract. Two combinations
become reachable for the first time:

- **quick → tournament**: turn a vs-Friend game into a draft, a Free-for-All or a bracket without
  backing out to the home screen.
- **tournament → quick**: `Event: Single game` at a 1v1 bring-a-deck table, and `Cards: Momir Basic`
  / `Random pool`. Previously the only route to a 1v1 single game was knowing that the *home screen*
  button, not the lobby, was the way to get one.

Everything else stays visible and disabled with its reason attached, which is where the Phase 5
holes now show up: Momir and random pools are 1v1-single-game only, bracket play is 1v1 only, a
limited pool always runs as a bracket.

**`Random pool` is a real Cards value now, not just a chip.** It is the deck picker's Random tab —
per player, not a lobby setting — so `DeckPicker` grew an optional controlled `tab` / `onTabChange`
pair and the lobby hoists it. Selecting Random on the Cards row moves the picker; the picker moving
updates the row and the header chip. Phase 2 had to infer this from the server echoing back an empty
deck; now there is one source of truth. `RecreateSpec` carries a `deckTab` for the same reason —
without it, "Random pool ⇄" would land you on a lobby reading "Bring a deck".

**Also folded in:**

- `TournamentLobbySettings.tsx` holds what is genuinely tournament-only (sets, boosters, timers,
  commander preset, ban list, teams, attack mode, AI assist), ordered by what it belongs to rather
  than by when it was added.
- **The disabled start button explains itself.** The old reason string fell through to a bare "Need
  at least 2 players" for the exact-count shapes, so a Two-Headed Giant lobby holding three players
  offered a dead button and no explanation. Every branch of the seat rule now has a sentence.
- **Winston's booster cap was about to break.** Winston is a draft everywhere else but counts
  *boosters* capped at 16, not *packs* capped at 6; the extraction nearly folded it in with the
  drafts. Called out in a comment at the one place that cares.
- **The routing either/or is gone.** `GameUI.tsx` has one lobby branch (`quickGameLobbyState ||
  lobbyState`), and `HomeScreen` no longer routes to any overlay.
- Two new help topics' worth of change: `lobby-switching` explains `⇄`, and `axis-limits` was
  rewritten — "Event follows Table" stopped being true the moment 1v1 single game became reachable.
- `DEFAULT_LOBBY_SET_CODE` replaces the `'ECL'` that was hardcoded in `HomeScreen.launchPreset` and
  would have been hardcoded a second time in the recreate path.

**One honest bug fixed on the way past.** The server broadcasts a lobby closure to everyone still
listed in it, *including the host who closed it by leaving* — so a recreate landed you in a working
new lobby underneath a red "Host left the lobby" banner. `onQuickGameLobbyClosed` now ignores a
closure for a lobby the client has already cleared. This was always wrong (pressing Leave did it
too); the recreate just made it impossible to miss.

**Verified against the running stack**, walking: vs Friend → Cards Random↔Bring a deck (picker
follows) → Table Free-for-All (confirm → premade FFA lobby) → Table 1v1 (direct; Ranked and Games
per matchup appear, Attack disappears) → Event Single game (confirm → quick lobby) → Cards Sealed
(confirm → sealed lobby, shape sub-row, set chips, ban list) → Cards Random pool (confirm → quick
lobby *on the Random tab*) → vs AI → Cards Momir Basic → started a real Momir game. Reload mid-lobby
rejoins into the unified screen on both kinds. `npm run typecheck` and `npm run build` clean; no
console errors.

**Deviation from the plan's naming:** the descriptor module is `axisChoices.ts`, not `lobbyAxes.ts`
— sitting next to the existing `axes.ts`, a name differing only by a prefix would have been a
coin-flip every time you went looking for one of them.

### Part 1 duplication, closed

Both of Part 1's remaining findings are resolved.

- **`components/spectating/SpectatorView.tsx` deleted** — 1025 dead lines, superseded by
  `SpectatorGameBoard.tsx`. Its only mention anywhere was its own `export`.
- **The two replay viewers now share one playback surface**,
  [`components/replay/ReplayPlayer.tsx`](../web-client/src/components/replay/ReplayPlayer.tsx):
  transport, scrubber, share/scenario/snapshot actions, `SpectatorContext` and the board.

  The two *entry points* deliberately stay separate, because they are different things: the route is
  a shareable URL that loads a public replay by id; the overlay is an in-app screen that lists games
  and **must not navigate**, since routing away drops the WebSocket. Everything after "here are the
  frames" is now shared. `ReplayPage` is 494 → 143 lines (route, fetch, loading/error, team
  stamping); `ReplayViewer` is 733 → 354 (list + fetch plumbing), and its dead style keys went with
  it. 1227 lines → 920, with the surface existing once.

  The copies had already drifted, and merging onto the route's better version means **the overlay
  gains** replay metadata, the archived-frames badge, `stateReproducible` gating, multiplayer seat
  labels instead of "Alice vs Bob", and the "Share as scenario" / "Save snapshot" buttons it never
  had. `SpectatorStateUpdate` also moved out of `components/admin/` into
  `replay/reconstructSnapshots.ts` — the public route was importing its core wire type from the
  admin overlay.

  > **Found while doing this:** `GameBoard` calls a **different number of hooks** depending on
  > whether the store holds spectating state, so mounting it before frame 0 lands crashes React with
  > *"Rendered more hooks than during the previous render"*. Both old call sites avoided it by
  > accident, writing frame 0 in the same batch that revealed the board. `ReplayPlayer` now gates the
  > mount explicitly (`primed`), but **the conditional hook in `GameBoard` is still there** and will
  > bite the next thing that mounts it against an empty store. Worth fixing at source.

**Deliberately not done:** merging the two deckbuilders. `/deckbuilder` (constructed) and
`sealed/DeckBuilderOverlay` (limited pool) solve genuinely different problems, and Vincent's call is
that the duplication is not worth paying down.

### Deliberate deviations from the plan above

- `HelpTopic.body` is a small `HelpBlock` union (`p` / `ul` / `shortcuts`) rather than `ReactNode`,
  so `topics.ts` stays a plain data module both surfaces render and a test can walk.
- Mode preset cards are a `div` wrapping a `button`, not one big `button` — `HelpTip` is itself a
  button and nesting interactive elements is invalid HTML.
- The "Variants" preset opens **Momir vs a friend**. Momir vs AI stays reachable via the vs-AI
  lobby's format selector, exactly as before; it is not a second card.

---

## Critical files

**Client**

- `web-client/src/components/ui/GameUI.tsx` — 2992 lines. `ConnectionOverlay` 109–533,
  `LobbyOverlay` 763–1777, `PremadeDeckPickerPanel` 1778–1882, `FreeForAllOverlay` 1883–2069,
  `TournamentOverlay` 2080–2603.
- `web-client/src/components/ui/QuickGameLobbyOverlay.tsx` — 456 lines.
- `web-client/src/components/ui/GameUI.module.css` — 68 KB; the landing + lobby + tournament design
  language, already shared by both lobbies.
- `web-client/src/main.tsx` — route registration.
- `web-client/src/store/slices/quickGameLobbySlice.ts` (90), `lobbySlice.ts` (203), `types.ts:581–593`.
- `web-client/src/store/slices/handlers/quickGameLobbyHandlers.ts` (27), `lobbyHandlers.ts` (299).
- `web-client/src/types/messages.ts` — `LobbySettings` 1259–1297, `QuickGameLobbyStateMessage` 2711–2727.
- Tokens and primitives to build on: `styles/variables.css`, `styles/responsive.css`,
  `components/shared/Button.tsx`.

**Server** (Phase 5 only)

- `game-server/.../lobby/QuickGameLobby.kt`, `TournamentLobby.kt`, `MomirBasicSetup.kt`,
  `QuickGameLobbyRepository.kt`
- `game-server/.../handler/QuickGameLobbyHandler.kt`, `LobbyHandler.kt` (AI guards 1386–1403,
  2080–2088; premade start 1304–1335; settings update 2072–2118), `FreeForAllHandler.kt` (47–111)
- `game-server/.../protocol/ClientMessage.kt` (tournament 152–344, quick 468–575), `ServerMessage.kt`
  (`LobbySettings` 446, `LobbyUpdate` 510, `QuickGameLobbyState` 1092)
- `game-server/.../persistence/LobbyConverter.kt`

---

## Verification

Per `web-client/AGENTS.md`, UI changes need real data — run the stack, don't mock.

1. `GAME_DEV_ENDPOINTS_ENABLED=true ./gradlew :game-server:bootRun --args='--spring.profiles.active=local'`
   (~90 s cold) + `npm run dev`. The dev server falls back to :5174+ if 5173 is taken — read the
   actual port from its log.
2. `npm run typecheck` and `npm run build` in `web-client/` after each phase.
3. **Mode matrix walk** — from a cold home screen, reach each of: vs AI · vs Friend (code and QR) ·
   Sealed 1v1 · Booster Draft 8-player bracket · FFA 4-player with own decks · 2HG · Team vs Team ·
   Momir · Tournament with standings. Confirm the axis labels shown match what was clicked. Record
   which combinations remain blocked by the Phase 5 gaps.
4. **Back-button walk** — home → lobby → game → game over. Document current behaviour as the
   baseline for Phase 6.
5. **Screenshots** (required for UI PRs): Playwright via system Chrome
   (`chromium.launch({ channel: 'chrome' })`), reuse the `playwright` already in `node_modules`
   (CommonJS import), `deviceScaleFactor: 2`, `waitForTimeout(~800)` so webfonts paint. Capture:
   home, unified lobby in three axis states, `/help` index, a `HelpTip` popover open. Host on a
   throwaway flat `<feature>-screenshots` branch per the AGENTS.md git-plumbing recipe — do not
   commit PNGs to the feature branch.
6. **E2E** — `e2e-scenarios/`; the existing lobby and draft specs are the regression net for phases
   0, 2 and 4. Set `E2E_BASE_URL` when running against a worktree dev server.
7. **Server phases** — `just` gates only, never raw `./gradlew` (root `AGENTS.md`); use the `verify`
   skill to pick the suite.

## Out of scope

- Rules teaching for players new to Magic (what a phase is, the stack, mulligans as a concept).
- A first-run guided tour / step-through overlay.
- A persistent nav bar — the glass card stays.
- Promoting hotseat to a real Table value; it stays a debugging affordance.
- Cube — it slots into the Cards axis when [`cube-draft-format.md`](cube-draft-format.md) lands.
- Merging the two deckbuilders. Noted in Part 1 as duplication, but the constructed builder and the
  limited-pool builder solve different problems — decided 2026-07-26 not to pay it down. (The two
  *replay viewers* were merged; see § *Part 1 duplication, closed*.)
