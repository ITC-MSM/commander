/**
 * The six named entry points on the home screen.
 *
 * These are **presets, not modes**: each one opens a lobby with a particular starting point on the
 * three axes (see `components/lobby/axes.ts`). They are not six systems — a host who entered via
 * "vs AI" can change the axes in the lobby and end up somewhere another preset would have started.
 *
 * One source of truth so a newcomer can *compare* entry points instead of guessing: every card
 * carries the same metadata (players, duration, whether you need a deck), which is exactly what a
 * scattered set of ad-hoc buttons can never give you.
 */
import type { LobbyGameMode, TournamentFormat } from '@/types'
import type { AxisTriple } from '../lobby/axes'

/**
 * How a preset is realised against today's two unrelated server lobby implementations
 * (`QuickGameLobby` vs `TournamentLobby`). This mapping is the seam that Phase 4/5 of
 * `backlog/menu-lobby-restructure-and-help.md` closes; until then it lives here so the home
 * screen stays declarative.
 */
export type PresetLaunch =
  | { kind: 'quickGame'; vsAi: boolean; momirBasic?: boolean }
  | { kind: 'tournamentLobby'; format: TournamentFormat; gameMode: LobbyGameMode }

export interface ModePreset {
  id: string
  title: string
  /** One line, imperative, says what you'll actually be doing. */
  tagline: string
  /** "1v1", "2–8" — seats at the table. */
  players: string
  /** Rough wall-clock, so a newcomer can pick by how much time they have. */
  duration: string
  /** True when you must bring or pick a constructed deck before you can play. */
  needsDeck: boolean
  /** Topic id in `src/help/topics.ts`, bound to the card's help button. */
  helpTopicId: string
  /** The axis triple the lobby opens with. */
  defaults: AxisTriple
  launch: PresetLaunch
  /** Visual accent so the six cards are distinguishable at a glance. */
  accent: 'ai' | 'friend' | 'limited' | 'multiplayer' | 'tournament' | 'variant'
}

export const MODE_PRESETS: readonly ModePreset[] = [
  {
    id: 'vs-ai',
    title: 'vs AI',
    tagline: 'Pick a deck and play the built-in AI right now.',
    players: 'You + AI',
    duration: '~15 min',
    needsDeck: true,
    helpTopicId: 'preset-vs-ai',
    defaults: {
      cards: { kind: 'BRING_A_DECK', legality: null },
      table: 'ONE_V_ONE',
      event: 'SINGLE_GAME',
    },
    launch: { kind: 'quickGame', vsAi: true },
    accent: 'ai',
  },
  {
    id: 'vs-friend',
    title: 'vs Friend',
    tagline: 'Share an invite code, both pick a deck, both ready up.',
    players: '2',
    duration: '~20 min',
    needsDeck: true,
    helpTopicId: 'preset-vs-friend',
    defaults: {
      cards: { kind: 'BRING_A_DECK', legality: null },
      table: 'ONE_V_ONE',
      event: 'SINGLE_GAME',
    },
    launch: { kind: 'quickGame', vsAi: false },
    accent: 'friend',
  },
  {
    id: 'draft-sealed',
    title: 'Draft & Sealed',
    tagline: 'Open packs or draft a pool, then build a 40-card deck.',
    players: '2–8',
    duration: '~60 min',
    needsDeck: false,
    helpTopicId: 'preset-draft-sealed',
    defaults: {
      cards: { kind: 'SEALED', shape: 'STANDARD' },
      table: 'ONE_V_ONE',
      event: 'ROUND_ROBIN',
    },
    launch: { kind: 'tournamentLobby', format: 'SEALED', gameMode: 'TOURNAMENT' },
    accent: 'limited',
  },
  {
    id: 'multiplayer',
    title: 'Multiplayer',
    tagline: 'Free-for-All, Two-Headed Giant or Team vs. Team at one table.',
    players: '3–8',
    duration: '~40 min',
    needsDeck: true,
    helpTopicId: 'preset-multiplayer',
    defaults: {
      cards: { kind: 'BRING_A_DECK', legality: null },
      table: 'FREE_FOR_ALL',
      event: 'SINGLE_GAME',
    },
    launch: { kind: 'tournamentLobby', format: 'PREMADE_DECKS', gameMode: 'FREE_FOR_ALL' },
    accent: 'multiplayer',
  },
  {
    id: 'tournament',
    title: 'Tournament',
    tagline: 'Round-robin bracket with standings — everyone brings a deck.',
    players: '2–8',
    duration: '~60 min+',
    needsDeck: true,
    helpTopicId: 'preset-tournament',
    defaults: {
      cards: { kind: 'BRING_A_DECK', legality: null },
      table: 'ONE_V_ONE',
      event: 'ROUND_ROBIN',
    },
    launch: { kind: 'tournamentLobby', format: 'PREMADE_DECKS', gameMode: 'TOURNAMENT' },
    accent: 'tournament',
  },
  {
    id: 'variants',
    title: 'Variants',
    tagline: 'Momir Basic — no deckbuilding, 60 basics, flip random creatures.',
    players: '2',
    duration: '~20 min',
    needsDeck: false,
    helpTopicId: 'preset-variants',
    defaults: {
      cards: { kind: 'MOMIR' },
      table: 'ONE_V_ONE',
      event: 'SINGLE_GAME',
    },
    launch: { kind: 'quickGame', vsAi: false, momirBasic: true },
    accent: 'variant',
  },
]
