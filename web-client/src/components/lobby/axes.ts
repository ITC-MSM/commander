/**
 * The three independent axes every game in Argentum is a point in.
 *
 * Before this, the client had two overloaded words: "Format" meant deck legality in the quick
 * lobby and pool type in the tournament lobby, and "Mode" meant quick-vs-tournament on the home
 * screen but table shape inside the lobby. Naming the axes separately is what makes combinations
 * like "4-player free-for-all with my own deck" reachable by reasoning instead of by accident.
 *
 * - **Cards** — where your deck comes from.
 * - **Table** — who is at it.
 * - **Event** — one game, or a series.
 *
 * Sub-options hang off their own axis only (Draft → Booster/Winston/Grid/Commander), never off a
 * different one. A new mode should add a *value* here, not a new axis: if something ever needs a
 * fourth axis or a new top-level home button, the taxonomy was wrong.
 */
import type { DeckFormat, LobbyGameMode, LobbySettings, TournamentFormat } from '@/types'
import { DECK_FORMATS, labelForFormat } from '@/utils/deckLegality'

/** Where the cards come from. */
export type CardsAxis =
  /** Bring a constructed deck. `legality` null = no restriction. */
  | { kind: 'BRING_A_DECK'; legality: DeckFormat | null }
  /** Server rolls a pool for you — the zero-prep on-ramp. */
  | { kind: 'RANDOM' }
  /** Momir Basic: no deckbuilding, 60 basics, flip creatures off the avatar. */
  | { kind: 'MOMIR' }
  | { kind: 'SEALED'; shape: 'STANDARD' | 'COMMANDER' }
  | { kind: 'DRAFT'; shape: 'BOOSTER' | 'WINSTON' | 'GRID' | 'COMMANDER' }

/** The five top-level Cards values, without their sub-options. One button each in the lobby. */
export type CardsKind = CardsAxis['kind']

export const CARDS_KINDS: readonly CardsKind[] = ['BRING_A_DECK', 'RANDOM', 'MOMIR', 'SEALED', 'DRAFT']

/** Who is at the table. */
export type TableAxis = 'ONE_V_ONE' | 'FREE_FOR_ALL' | 'TWO_HEADED_GIANT' | 'TEAM_VS_TEAM'

export const TABLE_VALUES: readonly TableAxis[] =
  ['ONE_V_ONE', 'FREE_FOR_ALL', 'TWO_HEADED_GIANT', 'TEAM_VS_TEAM']

/** One game, or a series. */
export type EventAxis = 'SINGLE_GAME' | 'ROUND_ROBIN'

export interface AxisTriple {
  cards: CardsAxis
  table: TableAxis
  event: EventAxis
}

/**
 * The Cards → "Bring a deck" sub-option: which constructed format submitted decks must be legal
 * in. Derived from the deckbuilder's one list so the lobby dropdowns can never drift from the
 * badges the builder shows.
 */
export const LEGALITY_OPTIONS: ReadonlyArray<{ value: DeckFormat; label: string }> =
  DECK_FORMATS.map((f) => ({ value: f.value.toUpperCase() as DeckFormat, label: f.label }))

export function cardsLabel(cards: CardsAxis): string {
  switch (cards.kind) {
    case 'BRING_A_DECK':
      return cards.legality ? `Bring a deck (${labelForFormat(cards.legality)})` : 'Bring a deck'
    case 'RANDOM':
      return 'Random pool'
    case 'MOMIR':
      return 'Momir Basic'
    case 'SEALED':
      return cards.shape === 'COMMANDER' ? 'Commander Sealed' : 'Sealed'
    case 'DRAFT':
      switch (cards.shape) {
        case 'WINSTON': return 'Winston Draft'
        case 'GRID': return 'Grid Draft'
        case 'COMMANDER': return 'Commander Draft'
        case 'BOOSTER': return 'Booster Draft'
      }
  }
}

/** The bare name of a Cards value, without the sub-option {@link cardsLabel} folds in. */
export function cardsKindLabel(kind: CardsKind): string {
  switch (kind) {
    case 'BRING_A_DECK': return 'Bring a deck'
    case 'RANDOM': return 'Random pool'
    case 'MOMIR': return 'Momir Basic'
    case 'SEALED': return 'Sealed'
    case 'DRAFT': return 'Draft'
  }
}

export function tableLabel(table: TableAxis): string {
  switch (table) {
    case 'ONE_V_ONE': return '1v1'
    case 'FREE_FOR_ALL': return 'Free-for-All'
    case 'TWO_HEADED_GIANT': return 'Two-Headed Giant'
    case 'TEAM_VS_TEAM': return 'Team vs. Team'
  }
}

export function eventLabel(event: EventAxis): string {
  switch (event) {
    case 'SINGLE_GAME': return 'Single game'
    case 'ROUND_ROBIN': return 'Round-robin bracket'
  }
}

/** "Sealed · 1v1 · Round-robin bracket" — the one-line summary of an axis triple. */
export function axisSummary(axes: AxisTriple): string {
  return [cardsLabel(axes.cards), tableLabel(axes.table), eventLabel(axes.event)].join(' · ')
}

/** The help topic that explains an axis value, so a control can bind its `?` to what is selected. */
export function cardsTopicId(cards: CardsAxis): string {
  return cardsKindTopicId(cards.kind)
}

export function cardsKindTopicId(kind: CardsKind): string {
  switch (kind) {
    case 'BRING_A_DECK': return 'cards-bring-a-deck'
    case 'RANDOM': return 'cards-random'
    case 'MOMIR': return 'cards-momir'
    case 'SEALED': return 'cards-sealed'
    case 'DRAFT': return 'cards-draft'
  }
}

export function tableTopicId(table: TableAxis): string {
  switch (table) {
    case 'ONE_V_ONE': return 'table-1v1'
    case 'FREE_FOR_ALL': return 'table-free-for-all'
    case 'TWO_HEADED_GIANT': return 'table-two-headed-giant'
    case 'TEAM_VS_TEAM': return 'table-team-vs-team'
  }
}

export function eventTopicId(event: EventAxis): string {
  return event === 'ROUND_ROBIN' ? 'event-round-robin' : 'event-single-game'
}

/* ───────────────────────────────────────────────────────────────────────────
 * Mapping onto today's two unrelated server lobby kinds.
 *
 * The axes are the vocabulary the *client* speaks; the server still speaks
 * `TournamentFormat` + `LobbyGameMode` (tournament lobby) or `(format, momirBasic)` (quick lobby).
 * Everything that translates between the two lives here, so exactly one module knows the mapping
 * — the same role `ModePreset.launch` plays for the home screen.
 * ─────────────────────────────────────────────────────────────────────────── */

export function tableFromGameMode(gameMode: LobbyGameMode): TableAxis {
  switch (gameMode) {
    case 'TOURNAMENT': return 'ONE_V_ONE'
    case 'FREE_FOR_ALL': return 'FREE_FOR_ALL'
    case 'TWO_HEADED_GIANT': return 'TWO_HEADED_GIANT'
    case 'TEAM_VS_TEAM': return 'TEAM_VS_TEAM'
  }
}

export function gameModeForTable(table: TableAxis): LobbyGameMode {
  switch (table) {
    case 'ONE_V_ONE': return 'TOURNAMENT'
    case 'FREE_FOR_ALL': return 'FREE_FOR_ALL'
    case 'TWO_HEADED_GIANT': return 'TWO_HEADED_GIANT'
    case 'TEAM_VS_TEAM': return 'TEAM_VS_TEAM'
  }
}

/**
 * Event is not yet independent server-side: `gameMode = TOURNAMENT` *is* the round-robin bracket
 * of 1v1 matches, and every multiplayer table plays exactly one game. Deriving it rather than
 * storing it keeps the client honest about that until Phase 5 of
 * `backlog/menu-lobby-restructure-and-help.md` splits them.
 */
export function eventFromGameMode(gameMode: LobbyGameMode): EventAxis {
  return gameMode === 'TOURNAMENT' ? 'ROUND_ROBIN' : 'SINGLE_GAME'
}

/**
 * The tournament format that expresses a Cards value, or null when no tournament lobby can.
 *
 * The inverse of {@link cardsFromTournamentFormat}. Random pool folds onto `PREMADE_DECKS`: it is
 * a per-player choice inside the deck picker, not a lobby setting, so it doesn't change the
 * lobby's shape. Momir Basic has no tournament-side implementation at all — `grep -i momir` over
 * `TournamentLobby.kt` / `LobbyHandler.kt` / `FreeForAllHandler.kt` is zero hits — which is gap #2
 * in the plan's Phase 5 list, and why this returns null rather than guessing.
 */
export function tournamentFormatForCards(cards: CardsAxis): TournamentFormat | null {
  switch (cards.kind) {
    case 'BRING_A_DECK':
    case 'RANDOM':
      return 'PREMADE_DECKS'
    case 'MOMIR':
      return null
    case 'SEALED':
      return cards.shape === 'COMMANDER' ? 'COMMANDER_SEALED' : 'SEALED'
    case 'DRAFT':
      switch (cards.shape) {
        case 'WINSTON': return 'WINSTON_DRAFT'
        case 'GRID': return 'GRID_DRAFT'
        case 'COMMANDER': return 'COMMANDER_DRAFT'
        case 'BOOSTER': return 'DRAFT'
      }
  }
}

export function cardsFromTournamentFormat(
  format: TournamentFormat,
  deckFormat: DeckFormat | null | undefined,
): CardsAxis {
  switch (format) {
    case 'SEALED': return { kind: 'SEALED', shape: 'STANDARD' }
    case 'COMMANDER_SEALED': return { kind: 'SEALED', shape: 'COMMANDER' }
    case 'DRAFT': return { kind: 'DRAFT', shape: 'BOOSTER' }
    case 'WINSTON_DRAFT': return { kind: 'DRAFT', shape: 'WINSTON' }
    case 'GRID_DRAFT': return { kind: 'DRAFT', shape: 'GRID' }
    case 'COMMANDER_DRAFT': return { kind: 'DRAFT', shape: 'COMMANDER' }
    case 'PREMADE_DECKS': return { kind: 'BRING_A_DECK', legality: deckFormat ?? null }
  }
}

export function axesFromLobbySettings(settings: LobbySettings): AxisTriple {
  return {
    cards: cardsFromTournamentFormat(settings.format, settings.deckFormat),
    table: tableFromGameMode(settings.gameMode),
    event: eventFromGameMode(settings.gameMode),
  }
}

/**
 * Quick-game lobbies are hard-capped at two seats and always play one game, so Table and Event are
 * constants. Cards is the `(format, momirBasic)` pair — mutually exclusive server-side.
 *
 * Random pool is *per player*, not a lobby setting: it is the deck picker's Random tab, so one
 * player can roll a pool while the other brings a deck. It is still reported as this viewer's
 * Cards value, because a chip reading "Bring a deck" over a picker sitting on Random is exactly
 * the drift this vocabulary exists to remove.
 *
 * @param deckTab the deck picker's live tab, when the lobby is hoisting it (the unified lobby
 *   does, so its Cards axis and the picker are one control). When it isn't known yet — the first
 *   render, or a reconnect before the picker has mounted — we fall back to the server's own
 *   "roll me one" signal, an empty submitted deck (`QuickGameLobbyHandler.toView` labels it
 *   "Random Pool").
 */
export function axesFromQuickGameLobby(
  lobby: { readonly momirBasic?: boolean | null; readonly format?: DeckFormat | null },
  you?: { readonly deckSelected: boolean; readonly deckCardCount: number } | undefined,
  deckTab?: 'saved' | 'examples' | 'paste' | 'random' | undefined,
): AxisTriple {
  const rollsAPool = !lobby.momirBasic && (
    deckTab !== undefined
      ? deckTab === 'random'
      : you?.deckSelected === true && you.deckCardCount === 0
  )
  return {
    cards: lobby.momirBasic
      ? { kind: 'MOMIR' }
      : rollsAPool
        ? { kind: 'RANDOM' }
        : { kind: 'BRING_A_DECK', legality: lobby.format ?? null },
    table: 'ONE_V_ONE',
    event: 'SINGLE_GAME',
  }
}
