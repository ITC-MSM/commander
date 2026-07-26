/**
 * The one content source behind both help surfaces: the `/help` page and the inline
 * {@link HelpTip} popovers.
 *
 * Everything explained anywhere in the client should live here, and the call site should
 * reference a topic id rather than hold its own string. That single constraint is what stops the
 * drift that made ~40 scattered `title=` tooltips near-useless: each explanation now has exactly
 * one home, and the popover and the page can never disagree.
 *
 * **Audience: knows Magic, new to Argentum.** No rules teaching — nothing here explains what a
 * phase, the stack or a mulligan *is*. It explains what *this app* does with them.
 *
 * Typed TS rather than markdown on purpose: there is no markdown pipeline in the client, `public/`
 * ships no docs, and the Dockerfile copies only `dist/` + `nginx.conf` — the repo's `docs/` is not
 * reachable from the browser and never will be without new build machinery.
 *
 * `body` is a small block union rather than `ReactNode` so this stays a plain data module that
 * both surfaces can render (and that a lint/test can walk).
 */

export type HelpSection = 'getting-started' | 'modes' | 'playing' | 'decks' | 'advanced'

export type HelpBlock =
  | { kind: 'p'; text: string }
  | { kind: 'ul'; items: readonly string[] }
  /** Renders the full `shortcuts.ts` table. */
  | { kind: 'shortcuts' }

export interface HelpTopic {
  id: string
  section: HelpSection
  title: string
  /** One or two sentences — this is what the inline popover shows. */
  summary: string
  /** Longer prose, only rendered on `/help`. */
  body?: readonly HelpBlock[]
  /** Other topic ids, rendered as links. */
  related?: readonly string[]
  /** Ids from `shortcuts.ts`, rendered as chips under the topic. */
  shortcuts?: readonly string[]
}

export const HELP_SECTIONS: readonly { id: HelpSection; title: string; blurb: string }[] = [
  {
    id: 'getting-started',
    title: 'Getting started',
    blurb: 'Your first five minutes: a name, a game, and where your decks live.',
  },
  {
    id: 'modes',
    title: 'Game modes',
    blurb: 'Three independent choices — Cards, Table, Event — and the six presets that set them.',
  },
  {
    id: 'playing',
    title: 'Playing a game',
    blurb: 'Priority, stops, tapping, yields and the board controls.',
  },
  {
    id: 'decks',
    title: 'Decks',
    blurb: 'Building, importing, sharing, and building from a limited pool.',
  },
  {
    id: 'advanced',
    title: 'Advanced',
    blurb: 'Shortcuts, replays, spectating, the multiplayer camera and the Lab tools.',
  },
]

export const HELP_TOPICS: readonly HelpTopic[] = [
  // ── Getting started ────────────────────────────────────────────────────
  {
    id: 'pick-a-name',
    section: 'getting-started',
    title: 'Picking a name',
    summary:
      'Type any name to start playing straight away — no sign-up. The name is remembered in this browser and is what opponents see.',
    body: [
      { kind: 'p', text: 'Nothing is gated behind an account. A name is enough to create a lobby, join one with a code, or play the AI.' },
    ],
    related: ['guest-vs-account'],
  },
  {
    id: 'guest-vs-account',
    section: 'getting-started',
    title: 'Guest vs. account',
    summary:
      'Guests can play everything. An account (one magic link, no password) adds decks that follow you between devices, friends, ranked play, stats and saved replays.',
    body: [
      { kind: 'p', text: 'Signing in later keeps the decks you built as a guest — you are offered a one-click migration.' },
      { kind: 'ul', items: [
        'Decks saved to the account instead of this browser',
        'Friends list and online presence',
        'Ranked games (every player in the game must be signed in, otherwise it silently plays unranked)',
        'Full stats dashboard and permanent replays',
      ] },
    ],
    related: ['ranked', 'replays'],
  },
  {
    id: 'first-game',
    section: 'getting-started',
    title: 'Starting your first game',
    summary:
      'Pick one of the six cards under PLAY. Each one opens the same lobby with different defaults — you can change them once you are inside.',
    body: [
      { kind: 'p', text: '“vs AI” is the shortest path: pick a deck, ready up, play. “vs Friend” is the same thing with an invite code to share.' },
      { kind: 'p', text: 'Everything you start ends in a lobby, and every lobby shows the same three settings. Nothing is a dead end — a lobby you opened as a 1v1 can become a four-player game without going back to the menu.' },
    ],
    related: ['axes', 'invite-codes'],
  },
  {
    id: 'invite-codes',
    section: 'getting-started',
    title: 'Invite codes and links',
    summary:
      'Every lobby has a short code. Paste it into the Join field on the home screen, scan its QR code, or open the share link — all three land in the same lobby.',
    body: [
      { kind: 'p', text: 'The Join field does not care what kind of lobby a code belongs to; it routes for you.' },
    ],
  },
  {
    id: 'where-decks-live',
    section: 'getting-started',
    title: 'Where your decks live',
    summary:
      'Saved decks live in this browser until you sign in, and in your account afterwards. The deckbuilder’s My Decks list is the same list every lobby deck picker reads from.',
    related: ['deckbuilder', 'deck-sharing'],
  },

  // ── Game modes ─────────────────────────────────────────────────────────
  {
    id: 'axes',
    section: 'modes',
    title: 'The three choices: Cards, Table, Event',
    summary:
      'Every game here is three independent picks: where your cards come from, who is at the table, and whether it is one game or a series.',
    body: [
      { kind: 'ul', items: [
        'Cards — bring a deck, a random pool, Momir Basic, Sealed, or one of the four drafts.',
        'Table — 1v1, Free-for-All, Two-Headed Giant, or Team vs. Team.',
        'Event — a single game, or a round-robin bracket with standings.',
      ] },
      { kind: 'p', text: 'They are independent: “Sealed” is not an alternative to “Tournament”, it is an alternative to “Draft”. A tournament is an alternative to a single game. That is why a 1v1 sealed game with one friend and an eight-player bracket are the same screen with different settings.' },
      { kind: 'p', text: 'The six cards on the home screen are named starting points, not six separate systems. Each sets the three values; the lobby lets you change any of them.' },
    ],
    related: ['cards-sealed', 'table-free-for-all', 'event-round-robin'],
  },
  {
    id: 'cards-bring-a-deck',
    section: 'modes',
    title: 'Cards: Bring a deck',
    summary:
      'Play a deck you built or pasted. Optionally restrict everyone to a constructed format — Standard, Pioneer, Modern, Pauper, Legacy, Vintage, Commander, Brawl, Standard Brawl or Premodern.',
    body: [
      { kind: 'p', text: '“No restriction” lets any legal-in-the-engine card in. The restriction is checked when a deck is submitted, not when it is built.' },
    ],
    related: ['deckbuilder'],
  },
  {
    id: 'cards-random',
    section: 'modes',
    title: 'Cards: Random pool',
    summary:
      'The server picks a deck for you. The fastest route from a cold start to actually playing — no deckbuilding, no pool to sort.',
  },
  {
    id: 'cards-momir',
    section: 'modes',
    title: 'Cards: Momir Basic',
    summary:
      'No deckbuilding at all. Everyone runs 60 basic lands; discard a card and pay {X} to put a random creature with mana value X onto the battlefield.',
    body: [
      { kind: 'p', text: 'The Momir Vig avatar sits in the command zone. Creatures are rolled from every implemented set, so games look nothing alike.' },
    ],
  },
  {
    id: 'cards-sealed',
    section: 'modes',
    title: 'Cards: Sealed',
    summary:
      'Open boosters and build a 40-card deck from what you got. Standard sealed uses 6 boosters by default; Commander Sealed opens Commander-shaped packs and builds a 60-card deck around a commander from your pool.',
    body: [
      { kind: 'p', text: 'The host picks the sets (mix several, or add a deferred “Random Set” that stays hidden until the game starts), how many boosters each player opens, and whether boosters are per-set or “chaos” — each pack mixing every selected set.' },
    ],
    related: ['limited-deckbuilding', 'cards-draft'],
  },
  {
    id: 'cards-draft',
    section: 'modes',
    title: 'Cards: Draft',
    summary:
      'Four shapes: Booster (pass packs, 3–8 players), Winston (three face-down piles, exactly 2), Grid (pick a row or column from a 3×3 grid, 2–4) and Commander (Commander-shaped packs, 1v1).',
    body: [
      { kind: 'p', text: 'The host sets a pick timer and, for Booster and Commander drafts, whether each pick takes one card or two.' },
    ],
    related: ['limited-deckbuilding', 'cards-sealed'],
  },
  {
    id: 'table-1v1',
    section: 'modes',
    title: 'Table: 1v1',
    summary: 'Two players, 20 life each. The default for every preset except Multiplayer.',
  },
  {
    id: 'table-free-for-all',
    section: 'modes',
    title: 'Table: Free-for-All',
    summary:
      'One game, everyone at the same table (2–6 players). Last player standing wins.',
    body: [
      { kind: 'p', text: 'The host chooses who each creature may attack: any opponent (CR 802), or only the player to your left or right (CR 803). “Left” and “right” follow the seating order shown in the lobby.' },
    ],
    related: ['table-team-vs-team', 'multiplayer-camera'],
  },
  {
    id: 'table-two-headed-giant',
    section: 'modes',
    title: 'Table: Two-Headed Giant',
    summary:
      'Exactly four players in two teams of two (CR 810). Each team shares one 30-life total, takes its turns together, and attacks and blocks as one unit.',
    body: [
      { kind: 'p', text: 'Teams are randomised at game start by default, re-rolled every game. Switch to “Choose teams” and the host can click each player’s team chip to assign them by hand.' },
    ],
    related: ['table-team-vs-team'],
  },
  {
    id: 'table-team-vs-team',
    section: 'modes',
    title: 'Table: Team vs. Team',
    summary:
      'An even pod (4, 6 or 8) split into two teams — 2v2, 3v3 or 4v4 (CR 808). Unlike Two-Headed Giant nothing is shared: each player keeps their own life and their own turn, and is knocked out individually. The last team with anyone standing wins.',
    related: ['table-two-headed-giant'],
  },
  {
    id: 'event-single-game',
    section: 'modes',
    title: 'Event: Single game',
    summary: 'One game, then everyone is back at the lobby. Multiplayer tables offer a “Play Again” ready loop.',
  },
  {
    id: 'event-round-robin',
    section: 'modes',
    title: 'Event: Round-robin bracket',
    summary:
      'Everyone plays everyone in a series of 1v1 matches; standings update after each round and most match wins takes it.',
    body: [
      { kind: 'p', text: 'Standings show wins–losses–draws, points and game win rate; hovering a row spells out the tiebreakers actually used (opponents’ match win %, game win %, opponents’ game win %, life differential).' },
      { kind: 'p', text: 'Odd player counts give someone a bye each round. When a round ends, everyone readies up for the next one; the host can add an extra round after the bracket completes.' },
    ],
    related: ['ranked'],
  },
  {
    id: 'ranked',
    section: 'modes',
    title: 'Ranked play',
    summary:
      'Ranked games adjust each player’s ELO. Every player must be signed in — otherwise the game still runs, but silently counts as unranked.',
    body: [
      { kind: 'p', text: 'Ranked is currently available on 1v1 games only. Multiplayer tables are always casual.' },
    ],
    related: ['guest-vs-account'],
  },
  {
    id: 'preset-vs-ai',
    section: 'modes',
    title: 'Preset: vs AI',
    summary:
      'A 1v1 game against the built-in AI. Pick a deck, ready up, and it starts immediately — nobody else has to show up.',
    related: ['axes', 'cards-bring-a-deck'],
  },
  {
    id: 'preset-vs-friend',
    section: 'modes',
    title: 'Preset: vs Friend',
    summary:
      'A 1v1 lobby with an invite code. Share the code or QR, both players pick a deck, both ready up.',
    related: ['invite-codes', 'ranked'],
  },
  {
    id: 'preset-draft-sealed',
    section: 'modes',
    title: 'Preset: Draft & Sealed',
    summary:
      'Limited play. Opens a lobby set to Sealed; switch it to any of the four draft shapes in the same screen. 2–8 players, and by default a round-robin bracket afterwards.',
    related: ['cards-sealed', 'cards-draft', 'event-round-robin'],
  },
  {
    id: 'preset-multiplayer',
    section: 'modes',
    title: 'Preset: Multiplayer',
    summary:
      'One shared game with 3–8 players, everyone bringing their own deck. Opens as Free-for-All; switch the table to Two-Headed Giant or Team vs. Team in the lobby.',
    related: ['table-free-for-all', 'table-two-headed-giant', 'table-team-vs-team'],
  },
  {
    id: 'preset-tournament',
    section: 'modes',
    title: 'Preset: Tournament',
    summary:
      'A round-robin bracket of 1v1 matches where everyone brings a constructed deck. The host can restrict the field to a format and set how many games each matchup plays.',
    related: ['event-round-robin', 'cards-bring-a-deck'],
  },
  {
    id: 'preset-variants',
    section: 'modes',
    title: 'Preset: Variants',
    summary:
      'The modes that do not involve deckbuilding. Currently Momir Basic — 60 basics and a random creature per turn.',
    related: ['cards-momir'],
  },

  // ── Playing a game ─────────────────────────────────────────────────────
  {
    id: 'priority-modes',
    section: 'playing',
    title: 'Priority modes: Auto, Stops, Full Control',
    summary:
      'Auto passes for you whenever you have nothing worth doing. Stops pauses on opponent spells and abilities and on combat damage. Full Control gives you priority at every single step.',
    body: [
      { kind: 'p', text: 'The button cycles Auto → Stops → Full Control. Auto is right for most games; switch to Full Control when you need a specific window, such as responding in your own upkeep.' },
      { kind: 'p', text: 'Auto never passes when you have a decision that matters — it is a convenience, not a rules shortcut.' },
    ],
    related: ['stops', 'yields'],
  },
  {
    id: 'stops',
    section: 'playing',
    title: 'Stops on the phase bar',
    summary:
      'Hover a step on the phase bar to reveal two dots: a blue “my turn” stop and an amber “opponent turn” stop. Click one and you will always get priority at that step.',
    body: [
      { kind: 'p', text: 'Stops are saved in this browser and apply to every game you play.' },
    ],
    related: ['priority-modes', 'phase-bar'],
  },
  {
    id: 'phase-bar',
    section: 'playing',
    title: 'The phase bar',
    summary:
      'The strip of pips across the top is the turn. The lit pip is the current step; the colour tells you whose turn it is.',
    related: ['stops'],
  },
  {
    id: 'auto-tap',
    section: 'playing',
    title: 'Auto Tap vs. Manual Tap',
    summary:
      'Auto Tap picks lands for you when you cast something. Manual Tap hands you the choice — useful when the lands you spend now decide what you can cast later.',
    related: ['priority-modes'],
  },
  {
    id: 'yields',
    section: 'playing',
    title: 'Yields — stop being asked',
    summary:
      'Right-click (or long-press) an ability on the stack to open its yield menu: yield until end of turn, always yield, always answer Yes, always answer No, or revoke.',
    body: [
      { kind: 'p', text: 'This is the fix for a repeating optional trigger asking you the same question every turn. Active yields are listed in a panel while they are in force, so you can revoke one at any time.' },
    ],
    shortcuts: ['stack-yield-menu'],
    related: ['priority-modes'],
  },
  {
    id: 'targeting-and-combat',
    section: 'playing',
    title: 'Targeting, attacking and blocking',
    summary:
      'Drag a card from your hand onto the battlefield to cast it, drag an attacker onto a defender to attack, and drag a blocker onto an attacker to block. Clicking works everywhere dragging does.',
    body: [
      { kind: 'p', text: 'Dragging one attacker onto another bands them (CR 702.22). On a phone, swipe left and right on the opponent strip to move between boards.' },
    ],
    related: ['multiplayer-camera'],
  },
  {
    id: 'zone-browsers',
    section: 'playing',
    title: 'Browsing zones',
    summary:
      'Click a graveyard, exile or library pile to open a full browser of its contents. Press D to open the deck browser, which tracks what is left in your library.',
    shortcuts: ['deck-browser', 'escape'],
  },
  {
    id: 'card-badges',
    section: 'playing',
    title: 'Card badges',
    summary:
      'Small labels on a card mark a state the card text alone will not tell you: Plotted, Prepared, Warped, Band N, and counters.',
    body: [
      { kind: 'ul', items: [
        'Plotted (CR 718) — sitting face-up in exile; cast it for free on a later turn.',
        'Prepared (Secrets of Strixhaven) — a copy of its spell waits castable in exile; casting the copy unprepares the creature.',
        'Warped (CR 702.185) — exiled at the beginning of the next end step, then castable again from exile.',
        'Band N (CR 702.22) — which attacking band this creature belongs to.',
      ] },
    ],
  },
  {
    id: 'the-ring',
    section: 'playing',
    title: 'The Ring',
    summary:
      'The gilded badge shows how many times the Ring has tempted you. Hover it to see exactly which abilities your Ring-bearer currently has — one per tempt, kept for the rest of the game (CR 701.54c).',
  },
  {
    id: 'speed',
    section: 'playing',
    title: 'Speed',
    summary:
      'The four-bar gauge is your speed (CR 702.179). It only appears once you actually have speed; hover it for how speed rises and what max speed unlocks.',
  },
  {
    id: 'undo',
    section: 'playing',
    title: 'Undo',
    summary:
      'The undo button takes back your most recent action when the server can still safely rewind — typically a tap or a cast that has not resolved.',
  },
  {
    id: 'game-log',
    section: 'playing',
    title: 'The game log',
    summary: 'A running record of everything that happened, in rules order. Useful when an interaction resolved differently than you expected.',
  },

  // ── Decks ──────────────────────────────────────────────────────────────
  {
    id: 'deckbuilder',
    section: 'decks',
    title: 'The deckbuilder',
    summary:
      'Search the full implemented card pool, click to add a copy, right-click or shift-click to remove one. Decks save to this browser, or to your account when signed in.',
    shortcuts: ['deckbuilder-remove', 'flip-dfc'],
    related: ['search-syntax', 'deck-sharing'],
  },
  {
    id: 'search-syntax',
    section: 'decks',
    title: 'Search syntax',
    summary:
      'The deckbuilder search speaks a Scryfall-style query language — `t:creature`, `c<=rw`, `cmc>=4`, `o:flying`, `f:standard`, `is:legendary`. The `?` button beside the search box lists every operator with examples.',
    related: ['deckbuilder'],
  },
  {
    id: 'deck-import-export',
    section: 'decks',
    title: 'Import and export',
    summary:
      'Paste an Arena-style decklist (`4 Lightning Bolt`) straight into the deckbuilder or a lobby deck picker, and export the same way.',
    related: ['deckbuilder'],
  },
  {
    id: 'deck-sharing',
    section: 'decks',
    title: 'Share links',
    summary:
      'A deck can be shared as a single URL that carries the whole list — no account needed on either end. Opening it drops the deck into the recipient’s deckbuilder.',
    related: ['deckbuilder'],
  },
  {
    id: 'limited-deckbuilding',
    section: 'decks',
    title: 'Building from a sealed or drafted pool',
    summary:
      'After a draft or sealed opening you get a dedicated builder over just your pool: add cards, set basic-land counts, and submit. Standard limited wants at least 40 cards including lands.',
    body: [
      { kind: 'p', text: 'Anything you leave out of the deck stays available as your sideboard between games in a match. You can save a drafted deck to My Decks from the standings screen — the printings you actually drafted are preserved.' },
    ],
    related: ['cards-sealed', 'cards-draft'],
  },

  // ── Advanced ───────────────────────────────────────────────────────────
  {
    id: 'keyboard-shortcuts',
    section: 'advanced',
    title: 'Keyboard shortcuts',
    summary: 'The complete list of keys the client listens for.',
    body: [{ kind: 'shortcuts' }],
  },
  {
    id: 'replays',
    section: 'advanced',
    title: 'Replays',
    summary:
      'Finished games can be replayed frame by frame. Scrub with the timeline, step with the arrow keys, play/pause with space.',
    body: [
      { kind: 'p', text: 'A replay can also be turned into a scenario: pick a frame, hand it to the Scenario Builder, and start a fresh game from exactly that board state.' },
    ],
    shortcuts: ['replay-frame', 'replay-play', 'escape'],
    related: ['lab-tools'],
  },
  {
    id: 'spectating',
    section: 'advanced',
    title: 'Spectating',
    summary:
      'Live games are listed on the home screen and inside tournaments. A spectator sees both boards and steers the same camera a player does — but never sees a hidden zone.',
    related: ['multiplayer-camera'],
  },
  {
    id: 'multiplayer-camera',
    section: 'advanced',
    title: 'Multiplayer camera: Overview, Follow, pin',
    summary:
      'Overview shows every opponent board side by side; turn it off to focus one board at a time. Follow slides the view to whoever is acting; turn it off for a manual camera.',
    body: [
      { kind: 'p', text: 'Number keys 1–9 jump to an opponent’s board and 0 toggles the overview. Clicking an opponent chip pins that board until you press Esc.' },
      { kind: 'p', text: 'Overview is desktop and landscape-tablet only — three boards side by side are unusable on a portrait phone.' },
    ],
    shortcuts: ['opponent-boards', 'overview', 'escape'],
    related: ['table-free-for-all'],
  },
  {
    id: 'lab-tools',
    section: 'advanced',
    title: 'Lab tools',
    summary:
      'Debugging and content tools, not part of normal play: the Scenario Builder (start a game from a hand-authored board state), Set Completion (which cards of a set are implemented), and, in dev builds, the LLM Tournament runner.',
    related: ['replays'],
  },
]

export function topicById(id: string): HelpTopic | undefined {
  return HELP_TOPICS.find((t) => t.id === id)
}

export function topicsInSection(section: HelpSection): readonly HelpTopic[] {
  return HELP_TOPICS.filter((t) => t.section === section)
}

/** Deep link to a topic on the help page. */
export function helpHref(topic: HelpTopic): string {
  return `/help/${topic.section}#${topic.id}`
}
