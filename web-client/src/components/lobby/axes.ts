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
import type { CommanderPreset, DeckFormat, LobbyGameMode, LobbySettings, TournamentFormat } from '@/types'
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

export function isCommanderDeckFormat(format: DeckFormat | null): boolean {
  return format === 'COMMANDER' || format === 'BRAWL' || format === 'STANDARD_BRAWL'
}

export function legalityOptionsForTable(table: TableAxis): typeof LEGALITY_OPTIONS {
  return table === 'TWO_HEADED_GIANT'
    ? LEGALITY_OPTIONS.filter((option) => !isCommanderDeckFormat(option.value))
    : LEGALITY_OPTIONS
}

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

/**
 * Upper seat bound for a Cards value, which is really a property of its *sub-shape*: Winston passes
 * three piles between exactly two players and Grid deals a 3×3 grid to at most four. Booster draft,
 * sealed and both Commander shapes take a full eight.
 *
 * One function rather than a number repeated at each call site, because two surfaces need the same
 * fact and phrase it differently: the lobby says "this lobby has 5 players", the landing wizard says
 * "pick 'A friend' instead".
 *
 * The Commander shapes read 8 rather than 2 deliberately. The client capped them at two from the day
 * they shipped, with the comment "multiplayer commander is a separate project", but that conflated
 * how many people share a *pool* with how many share a *game*: an eight-player Commander Draft is a
 * bracket of 1v1 Commander matches, which is exactly what has always been supported. The server
 * never had the restriction — `LobbyHandler.kt:605-616` caps Winston, Grid, 2HG, Teams and FFA and
 * puts everything else at 2–8, and its start guard only asks for two players. What genuinely is
 * missing is Commander at a Two-Headed Giant table and Commander with AI seats; both are stated where
 * they apply — see {@link COMMANDER_NEEDS_ITS_OWN_LIFE_TOTAL} and {@link COMMANDER_LIMITED_HAS_NO_AI}.
 */
export function cardsSeatCap(cards: CardsAxis): number {
  switch (cards.kind) {
    case 'BRING_A_DECK': return 8
    // Both live only on the two-seat quick-game lobby.
    case 'RANDOM':
    case 'MOMIR': return 2
    case 'SEALED': return 8
    case 'DRAFT':
      switch (cards.shape) {
        case 'BOOSTER': return 8
        case 'WINSTON': return 2
        case 'GRID': return 4
        case 'COMMANDER': return 8
      }
  }
}

/** The drafted / sealed Commander formats — `TournamentFormat.isCommanderFormat` on the server. */
export function isCommanderLimited(cards: CardsAxis): boolean {
  return (cards.kind === 'SEALED' || cards.kind === 'DRAFT') && cards.shape === 'COMMANDER'
}

/**
 * Commander at a Two-Headed Giant table — the one table shape it can't have.
 *
 * Commander pods themselves work: nothing in the engine's commander code is per-seat-count (damage
 * is tallied per *(commander, defending player)* pair, the command zone is per player, and the CR
 * 903.9a zone choice loops the turn order), so Free-for-All and Team vs. Team just play it. Two-Headed
 * Giant can't, because CR 810.4 gives the *team* one shared life total while Commander gives each
 * player their own 40 — `Format.TwoHeadedGiant` deliberately exposes no commander configuration, and
 * `LobbyHandler.handleStartTournamentLobby` refuses the combination server-side. Blocking it here
 * turns that rejection into a reason the host can read before pressing Start.
 */
export const COMMANDER_NEEDS_ITS_OWN_LIFE_TOTAL =
  'Commander can’t be played as Two-Headed Giant — a 2HG team shares one life total, and Commander gives every player their own 40. Free-for-All and Team vs. Team pods work.'

/**
 * AI seats in a Commander limited lobby.
 *
 * `LobbyHandler.buildAiSealedDeck` builds a 40-card sealed deck and calls `submitDeck` without a
 * commander, and `TournamentLobby.validateDeck` doesn't check for one — so the AI would not be
 * rejected, it would sit down in a Commander game with no commander and a 40-card deck. Silently
 * wrong is worse than blocked.
 */
export const COMMANDER_LIMITED_HAS_NO_AI =
  'The AI can’t build a Commander deck — its automatic deckbuilding never picks a commander, so it would sit down without one. Play these with people.'

/**
 * The Commander life / commander-damage presets, mirroring `CommanderPreset` in
 * `mtg-sdk/.../core/Format.kt`. The numbers live there; this is the display copy for them, in one
 * place because the settings panel and the lobby subtitle both name them.
 */
export const COMMANDER_PRESETS: Record<CommanderPreset, { life: number; damage: number; label: string; hint: string }> = {
  BRAWL: {
    life: 25, damage: 16, label: 'Brawl (25/16)',
    hint: 'Paper Brawl shape — 25 starting life, 16 commander damage',
  },
  COMMANDER: {
    life: 30, damage: 21, label: 'Commander (30/21)',
    hint: 'Closer to Commander Legends — 30 life, 21 commander damage',
  },
  POD: {
    life: 40, damage: 21, label: 'Pod (40/21)',
    hint: 'Paper multiplayer Commander — 40 life, 21 commander damage. Every pod plays at this.',
  },
}

/**
 * The preset the game will actually run at, mirroring `TournamentLobby.effectiveCommanderPreset`.
 *
 * The host's Brawl-vs-Commander choice is a 1v1 pacing knob; a pod always plays paper Commander's 40
 * life, so the server overrides it there. Deriving it here rather than reading a second server field
 * keeps the settings panel showing what the host picked while the summary shows what they'll get.
 */
export function effectiveCommanderPreset(preset: CommanderPreset, gameMode: LobbyGameMode): CommanderPreset {
  return gameMode === 'TOURNAMENT' ? preset : 'POD'
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
