/**
 * What you can actually play, before any lobby exists.
 *
 * The pre-lobby twin of `axisChoices.ts`. Both answer "can I have this combination?"; they differ in
 * where they start. `axisChoices` is asked from inside a lobby that is already backed by one of the
 * two server implementations, so its third answer is `RECREATE` — tear this one down and stand the
 * other one up. Here nothing exists yet, so every reachable combination is simply created correctly
 * the first time, and the only answers are *yes* and *not implemented*.
 *
 * That is the whole reason the landing screen asks its three questions before creating anything: the
 * six preset cards it replaces (see `backlog/menu-lobby-restructure-and-help.md` § 3a) committed to a
 * lobby kind on the first click, so "vs Friend, actually let's do Free-for-All" cost a recreate.
 *
 * ## The three questions
 *
 * 1. **Roster** — who fills the seats. Not one of the three axes and deliberately so: seats are per
 *    seat, and any Cards × Table × Event point could in principle be played by any roster. It leads
 *    because it prunes hardest and because it is the one thing a player has already decided before
 *    opening the app.
 * 2. **Cards** — where the deck comes from (`axes.ts`).
 * 3. **Shape** — Table × Event as one list of named shapes rather than two rows. Only five of the
 *    eight combinations exist, so two rows would mean three dead cells; the lobby keeps the axes
 *    separate because there its job is editing, not choosing.
 *
 * ## Disabled versus absent
 *
 * The distinction is load-bearing, and it is the same one the lobby draws:
 *
 * - **Disabled with a reason** — nothing implements this yet. Every one of these is a gap in § 4c of
 *   the plan, and this is the surface where the player meets it while asking the question rather
 *   than after committing to a lobby.
 * - **Absent** — contradicts an answer already given. A group of five is not shown a disabled "1v1,
 *   one game"; they said there were five of them. Rendering that as a limitation would teach
 *   something false about the system.
 */
import type { LobbyGameMode, TournamentFormat } from '@/types'
import {
  COMMANDER_LIMITED_NEEDS_A_1V1_TABLE,
  cardsKindLabel,
  cardsLabel,
  cardsSeatCap,
  isCommanderLimited,
  tournamentFormatForCards,
  gameModeForTable,
  type CardsAxis,
  type CardsKind,
  type EventAxis,
  type TableAxis,
} from './axes'
import type { DeckPickerTab } from '../ui/DeckPicker'

/* ── Vocabulary ─────────────────────────────────────────────────────────── */

/** Who fills the seats. */
export type Roster =
  /** You and the built-in AI. */
  | 'SOLO'
  /** One human opponent, reached with an invite code. */
  | 'FRIEND'
  /** Three or more players. */
  | 'GROUP'

export const ROSTERS: readonly Roster[] = ['SOLO', 'FRIEND', 'GROUP']

/** A reachable Table × Event pair, named the way a player would describe it. */
export type ShapeId = 'ONE_GAME' | 'BRACKET' | 'FREE_FOR_ALL' | 'TWO_HEADED_GIANT' | 'TEAM_VS_TEAM'

const SHAPE_AXES: Record<ShapeId, { table: TableAxis; event: EventAxis }> = {
  ONE_GAME: { table: 'ONE_V_ONE', event: 'SINGLE_GAME' },
  BRACKET: { table: 'ONE_V_ONE', event: 'ROUND_ROBIN' },
  FREE_FOR_ALL: { table: 'FREE_FOR_ALL', event: 'SINGLE_GAME' },
  TWO_HEADED_GIANT: { table: 'TWO_HEADED_GIANT', event: 'SINGLE_GAME' },
  TEAM_VS_TEAM: { table: 'TEAM_VS_TEAM', event: 'SINGLE_GAME' },
}

/** The closed domain, for the same reason `CARDS_KINDS` / `TABLE_VALUES` / `ROSTERS` exist: so a
 *  test can walk every value rather than restate the list and drift from it. */
export const SHAPE_IDS: readonly ShapeId[] = Object.keys(SHAPE_AXES) as ShapeId[]

export function shapeAxes(shape: ShapeId): { table: TableAxis; event: EventAxis } {
  return SHAPE_AXES[shape]
}

export function rosterLabel(roster: Roster): string {
  switch (roster) {
    case 'SOLO': return 'Just me'
    case 'FRIEND': return 'A friend'
    case 'GROUP': return 'A group'
  }
}

export function rosterCaption(roster: Roster): string {
  switch (roster) {
    case 'SOLO': return 'You and the built-in AI. Nobody else has to show up.'
    case 'FRIEND': return 'One opponent. You get an invite code to share.'
    case 'GROUP': return 'Three to eight players, at one table or in a bracket.'
  }
}

export function rosterTopicId(roster: Roster): string {
  switch (roster) {
    case 'SOLO': return 'roster-solo'
    case 'FRIEND': return 'roster-friend'
    case 'GROUP': return 'roster-group'
  }
}

export function shapeLabel(shape: ShapeId): string {
  switch (shape) {
    case 'ONE_GAME': return 'One game'
    case 'BRACKET': return 'Round-robin bracket'
    case 'FREE_FOR_ALL': return 'Free-for-All'
    case 'TWO_HEADED_GIANT': return 'Two-Headed Giant'
    case 'TEAM_VS_TEAM': return 'Team vs. Team'
  }
}

export function shapeCaption(shape: ShapeId): string {
  switch (shape) {
    case 'ONE_GAME': return 'A single 1v1 game. Play again afterwards if you want another.'
    case 'BRACKET': return 'Everyone plays everyone, with standings. 1v1 matches.'
    case 'FREE_FOR_ALL': return 'One shared game, every player for themselves (CR 802/803).'
    case 'TWO_HEADED_GIANT': return 'Two teams of two sharing 30 life, turns and combat (CR 810).'
    case 'TEAM_VS_TEAM': return 'Teams with their own life totals and turns (CR 808).'
  }
}

export function shapeTopicId(shape: ShapeId): string {
  switch (shape) {
    case 'ONE_GAME': return 'event-single-game'
    case 'BRACKET': return 'event-round-robin'
    case 'FREE_FOR_ALL': return 'table-free-for-all'
    case 'TWO_HEADED_GIANT': return 'table-two-headed-giant'
    case 'TEAM_VS_TEAM': return 'table-team-vs-team'
  }
}

/**
 * A tile's badge: how much of a *commitment* this option is.
 *
 * The thing a newcomer most wants to know before clicking is whether they are about to play a game
 * or start an event — open packs, build a deck, then several rounds and a standings table. That is
 * not derivable from a name like "Sealed", so every Cards and Shape tile says which it is.
 */
export type ChoiceWeight =
  /** Straight into a game. */
  | 'QUICK'
  /** There is a step before you play, or more than one game after. */
  | 'EVENT'

/** One option in a wizard step. `disabledReason` present ⇒ nothing implements it yet. */
export interface Choice<V> {
  value: V
  label: string
  caption: string
  topicId: string
  /** Short badge on the tile. Omitted where the distinction doesn't apply (the roster step). */
  badge?: { text: string; weight: ChoiceWeight }
  disabledReason?: string
}

/* ── The reasons ────────────────────────────────────────────────────────────
 * Each of these is a Phase 5 gap (§ 4c), phrased for someone who has not yet created anything —
 * which is why they say "go back and pick X" rather than the lobby's "switch the Table first".
 * ─────────────────────────────────────────────────────────────────────────── */

const AI_NEEDS_A_QUICK_GAME =
  'The AI can only bring a deck to a single 1v1 game — premade-deck brackets reject AI seats.'

const AI_NOT_AT_A_MULTIPLAYER_TABLE =
  'The AI can’t take a seat at a multiplayer table yet. Pick “A group” and invite people, or stay at 1v1.'

const MOMIR_IS_A_1V1_SINGLE_GAME =
  'Momir Basic only exists as a 1v1 single game — it has no bracket or multiplayer implementation.'

const RANDOM_IS_A_1V1_SINGLE_GAME =
  'A rolled random pool only exists on the two-seat lobby that plays one game.'

const LIMITED_ALWAYS_RUNS_AS_A_BRACKET =
  'A limited pool always runs as a bracket at a 1v1 table — the pool is meant to be played more than once. With two players and one game per matchup that is a single game anyway.'

/** The AI is off on this server entirely. */
export const AI_DISABLED_ON_SERVER = 'The AI player is disabled on this server.'

/* ── Step 1: roster ─────────────────────────────────────────────────────── */

export function rosterChoices(aiEnabled: boolean): Choice<Roster>[] {
  return ROSTERS.map((roster) => ({
    value: roster,
    label: rosterLabel(roster),
    caption: rosterCaption(roster),
    topicId: rosterTopicId(roster),
    ...(roster === 'SOLO' && !aiEnabled ? { disabledReason: AI_DISABLED_ON_SERVER } : {}),
  }))
}

/* ── Step 2: cards ──────────────────────────────────────────────────────── */

/** The order the five Cards values are offered in — cheapest on-ramp first. */
const CARDS_ORDER: readonly CardsKind[] = ['BRING_A_DECK', 'RANDOM', 'MOMIR', 'SEALED', 'DRAFT']

function cardsCaption(kind: CardsKind): string {
  switch (kind) {
    case 'BRING_A_DECK': return 'Play one of your own constructed decks.'
    case 'RANDOM': return 'The server rolls you a deck. Zero preparation.'
    case 'MOMIR': return '60 basics; flip a random creature each turn. No deckbuilding.'
    case 'SEALED': return 'Open boosters and build a deck from what you get.'
    case 'DRAFT': return 'Pick cards one at a time from packs, then build.'
  }
}

/** Sealed and draft put a pool-building step in front of the game; the other three do not. */
function cardsBadge(kind: CardsKind): { text: string; weight: ChoiceWeight } {
  return kind === 'SEALED' || kind === 'DRAFT'
    ? { text: 'Build a deck first', weight: 'EVENT' }
    : { text: 'Play right away', weight: 'QUICK' }
}

export function cardsChoices(roster: Roster): Choice<CardsKind>[] {
  return CARDS_ORDER.map((kind) => ({
    value: kind,
    label: cardsKindLabel(kind),
    caption: cardsCaption(kind),
    topicId: `cards-${kind.toLowerCase().replace(/_/g, '-')}`,
    badge: cardsBadge(kind),
    ...(roster === 'GROUP' && kind === 'RANDOM' ? { disabledReason: RANDOM_IS_A_1V1_SINGLE_GAME } : {}),
    ...(roster === 'GROUP' && kind === 'MOMIR' ? { disabledReason: MOMIR_IS_A_1V1_SINGLE_GAME } : {}),
  }))
}

/** The default sub-shape when a Cards value is first selected. */
export function defaultCardsAxis(kind: CardsKind): CardsAxis {
  switch (kind) {
    case 'BRING_A_DECK': return { kind: 'BRING_A_DECK', legality: null }
    case 'RANDOM': return { kind: 'RANDOM' }
    case 'MOMIR': return { kind: 'MOMIR' }
    case 'SEALED': return { kind: 'SEALED', shape: 'STANDARD' }
    case 'DRAFT': return { kind: 'DRAFT', shape: 'BOOSTER' }
  }
}

/* ── Step 3: shape ──────────────────────────────────────────────────────── */

/** True for the Cards values that only the two-seat quick-game lobby implements. */
function isQuickOnly(kind: CardsKind): boolean {
  return kind === 'RANDOM' || kind === 'MOMIR'
}

function quickOnlyReason(kind: CardsKind): string {
  return kind === 'MOMIR' ? MOMIR_IS_A_1V1_SINGLE_GAME : RANDOM_IS_A_1V1_SINGLE_GAME
}

const MULTIPLAYER_SHAPES: readonly ShapeId[] = ['FREE_FOR_ALL', 'TWO_HEADED_GIANT', 'TEAM_VS_TEAM']

/**
 * Which shapes this roster and Cards value can be played in.
 *
 * Read the three branches as the answer to "what stops the rest?": for a solo player it is the AI
 * seat rules, for a pair it is the arithmetic of two seats, and for a group it is only ever the
 * Cards value.
 */
export function shapeChoices(roster: Roster, cards: CardsAxis): Choice<ShapeId>[] {
  const kind = cards.kind
  const choice = (value: ShapeId, disabledReason?: string): Choice<ShapeId> => ({
    value,
    label: shapeLabel(value),
    caption: shapeCaption(value),
    topicId: shapeTopicId(value),
    // A bracket is the only shape that plays more than one game; the multiplayer tables are each one
    // shared game, which is the distinction the old "Multiplayer vs Tournament" pair blurred.
    badge: value === 'BRACKET'
      ? { text: 'Several rounds · standings', weight: 'EVENT' }
      : { text: 'One game', weight: 'QUICK' },
    ...(disabledReason ? { disabledReason } : {}),
  })

  if (roster === 'GROUP') {
    // ONE_GAME is absent, not disabled: a 1v1 single game contradicts "a group".
    // Every multiplayer table is one shared game, and both limited and premade lobbies can seat one
    // — Free-for-All with your own deck is the combination Part 2 called out as
    // supported-but-unreachable. The quick-only values never get here; step 2 disabled them.
    // Commander is the exception, and only at the *table*: its bracket is 1v1 matches all the way
    // down, so eight people can share a Commander pool even though they can't share one game.
    const multiplayerReason = isQuickOnly(kind)
      ? quickOnlyReason(kind)
      : isCommanderLimited(cards)
        ? COMMANDER_LIMITED_NEEDS_A_1V1_TABLE
        : undefined
    return [
      choice('BRACKET', isQuickOnly(kind) ? quickOnlyReason(kind) : undefined),
      ...MULTIPLAYER_SHAPES.map((s) => choice(s, multiplayerReason)),
    ]
  }

  if (roster === 'SOLO') {
    if (isQuickOnly(kind)) {
      return [choice('ONE_GAME'), choice('BRACKET', quickOnlyReason(kind))]
    }
    if (kind === 'BRING_A_DECK') {
      return [
        choice('ONE_GAME'),
        choice('BRACKET', AI_NEEDS_A_QUICK_GAME),
        ...MULTIPLAYER_SHAPES.map((s) => choice(s, AI_NOT_AT_A_MULTIPLAYER_TABLE)),
      ]
    }
    // Limited vs the AI: a pod of AI drafters playing the bracket out. Fully supported and, before
    // this screen, reachable only by creating a lobby and pressing "+ Add AI Player" repeatedly.
    return [
      choice('ONE_GAME', LIMITED_ALWAYS_RUNS_AS_A_BRACKET),
      choice('BRACKET'),
      ...MULTIPLAYER_SHAPES.map((s) => choice(s, AI_NOT_AT_A_MULTIPLAYER_TABLE)),
    ]
  }

  // FRIEND — two seats. The multiplayer shapes are absent rather than disabled: they need a third
  // player, which is a previous answer, not a missing feature.
  if (isQuickOnly(kind)) {
    return [choice('ONE_GAME'), choice('BRACKET', quickOnlyReason(kind))]
  }
  if (kind === 'BRING_A_DECK') {
    return [choice('ONE_GAME'), choice('BRACKET')]
  }
  return [choice('ONE_GAME', LIMITED_ALWAYS_RUNS_AS_A_BRACKET), choice('BRACKET')]
}

/* ── Seats ──────────────────────────────────────────────────────────────── */

/**
 * How many seats the lobby opens with, and whether the player gets to choose.
 *
 * `values` is the exact allowed list rather than a min/max pair because Team vs. Team needs an even
 * count and Two-Headed Giant needs exactly four — a range with holes in it is a control that lets you
 * pick something the start button then refuses.
 */
export interface SeatRule {
  values: number[]
  /** Rendered next to the control; empty when there is nothing to choose. */
  label: string
  /** True when the count is forced, so the wizard shows it as a fact instead of a control. */
  fixed: boolean
  /**
   * One line under the control. Says whether the number has to be right *now*.
   *
   * For a group it does not: `maxPlayers` is a cap, not a quorum — `startBlockReason` only ever
   * checks how many players are actually present — so the wizard defaults to the maximum and the
   * host starts whenever everyone has arrived. For a solo pod it decides how many AI seats to fill,
   * so it is a real choice, but the lobby can still add and remove them afterwards.
   */
  caption: string
}

export function seatRule(roster: Roster, cards: CardsAxis, shape: ShapeId): SeatRule {
  const twoSeats = (caption: string): SeatRule => ({ values: [2], label: '', fixed: true, caption })

  if (roster === 'FRIEND') return twoSeats('Two seats. Share the invite code and they take the other one.')
  if (roster === 'SOLO' && (isQuickOnly(cards.kind) || cards.kind === 'BRING_A_DECK')) {
    return twoSeats('You and one AI opponent.')
  }
  const cap = Math.min(cardsSeatCap(cards), shape === 'FREE_FOR_ALL' ? 6 : 8)

  if (roster === 'SOLO') {
    // A limited pod against AI drafters. This one really is a choice — it decides how many AI seats
    // get filled — but the lobby can add and remove them afterwards.
    const values = range(2, cap)
    return {
      values,
      label: 'Pod size',
      fixed: values.length === 1,
      caption: 'You plus AI opponents. Add or remove them in the lobby.',
    }
  }

  const openCaption =
    'A limit, not a requirement — start whenever everyone is in. Changeable in the lobby.'

  switch (shape) {
    case 'TWO_HEADED_GIANT':
      return { values: [4], label: '', fixed: true, caption: 'Two-Headed Giant is exactly four seats: two teams of two.' }
    case 'TEAM_VS_TEAM':
      return {
        values: [4, 6, 8].filter((n) => n <= cap),
        label: 'Up to',
        fixed: false,
        caption: `Teams need an even pod. ${openCaption}`,
      }
    default: {
      const values = range(3, cap)
      return { values, label: 'Up to', fixed: values.length === 1, caption: openCaption }
    }
  }
}

function range(from: number, to: number): number[] {
  const out: number[] = []
  for (let n = from; n <= to; n += 1) out.push(n)
  return out.length > 0 ? out : [from]
}

/**
 * The seat count a fresh selection starts on: always the **maximum**.
 *
 * For a group that means the lobby opens as wide as it can — the count is a cap and the host starts
 * once everyone has arrived, so a default of "the smallest legal pod" only ever meant the host had
 * to notice and raise it. For a solo pod it means a full table of AI, which is the interesting case.
 */
export function defaultSeats(rule: SeatRule): number {
  return rule.values[rule.values.length - 1] ?? 2
}

/* ── What will actually happen ──────────────────────────────────────────── */

/**
 * The selection as a sequence of stages: `Open boosters → Build a deck → Everyone plays everyone →
 * Standings`.
 *
 * The single most useful thing to show before committing, because the question a newcomer cannot
 * answer from any of the names involved is "how long is this and how many steps does it have?".
 * "Sealed" does not say that a deckbuilding phase and a standings table are coming.
 */
export function flowStages(selection: Selection): string[] {
  const { cards, shape, seats } = selection
  const stages: string[] = []

  switch (cards.kind) {
    case 'BRING_A_DECK': stages.push('Pick one of your decks'); break
    case 'RANDOM': stages.push('The server rolls you a deck'); break
    case 'MOMIR': stages.push('60 basics — no deckbuilding'); break
    case 'SEALED': stages.push('Open boosters', 'Build a deck'); break
    case 'DRAFT': stages.push(`${cardsLabel(cards)}`, 'Build a deck'); break
  }

  switch (shape) {
    case 'ONE_GAME': stages.push('One game'); break
    case 'BRACKET':
      stages.push(seats > 2 ? `Everyone plays everyone (${seats} players)` : 'Play the matchup', 'Standings')
      break
    case 'FREE_FOR_ALL':
    case 'TWO_HEADED_GIANT':
    case 'TEAM_VS_TEAM':
      stages.push(`One shared game, ${seats} seats`)
      break
  }
  return stages
}

/* ── The launch ─────────────────────────────────────────────────────────── */

/**
 * How a completed selection is realised against the two server lobby implementations.
 *
 * This is the seam `ModePreset.launch` used to be, with the six hand-written cases replaced by a
 * derivation — which is why a new Cards or Table value no longer needs a home-screen change.
 */
export type LaunchSpec =
  | { kind: 'QUICK'; vsAi: boolean; momirBasic: boolean; deckTab: DeckPickerTab }
  | {
      kind: 'TOURNAMENT'
      format: TournamentFormat
      gameMode: LobbyGameMode
      maxPlayers: number
      /** AI seats to fill after the lobby exists. Only ever non-zero for a solo limited pod. */
      aiSeats: number
    }

export interface Selection {
  roster: Roster
  cards: CardsAxis
  shape: ShapeId
  seats: number
}

/**
 * The quick-game lobby is the only thing that implements a 1v1 single game, and the only thing that
 * implements Momir or a rolled pool at all. Everything else is the tournament lobby.
 */
export function lobbyKindFor(selection: Selection): 'QUICK' | 'TOURNAMENT' {
  const { cards, shape } = selection
  if (isQuickOnly(cards.kind)) return 'QUICK'
  return shape === 'ONE_GAME' && cards.kind === 'BRING_A_DECK' ? 'QUICK' : 'TOURNAMENT'
}

export function resolveLaunch(selection: Selection): LaunchSpec {
  const { roster, cards, shape, seats } = selection

  if (lobbyKindFor(selection) === 'QUICK') {
    return {
      kind: 'QUICK',
      vsAi: roster === 'SOLO',
      momirBasic: cards.kind === 'MOMIR',
      deckTab: cards.kind === 'RANDOM' ? 'random' : 'saved',
    }
  }

  // `tournamentFormatForCards` only returns null for Momir, which never reaches here.
  const format = tournamentFormatForCards(cards) ?? 'PREMADE_DECKS'
  return {
    kind: 'TOURNAMENT',
    format,
    gameMode: gameModeForTable(shapeAxes(shape).table),
    maxPlayers: seats,
    aiSeats: roster === 'SOLO' ? Math.max(0, seats - 1) : 0,
  }
}

/** "Just me · Booster Draft · Round-robin bracket · 8 seats" — the recap, and the Play again chip. */
export function selectionSummary(selection: Selection): string {
  const parts = [
    rosterLabel(selection.roster),
    // `cardsLabel` already folds the sub-shape in ("Commander Sealed", "Winston Draft").
    cardsLabel(selection.cards),
    shapeLabel(selection.shape),
  ]
  if (selection.seats > 2) parts.push(`${selection.seats} seats`)
  return parts.join(' · ')
}
