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
import type { DeckFormat } from '@/types'

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

/** Who is at the table. */
export type TableAxis = 'ONE_V_ONE' | 'FREE_FOR_ALL' | 'TWO_HEADED_GIANT' | 'TEAM_VS_TEAM'

/** One game, or a series. */
export type EventAxis = 'SINGLE_GAME' | 'ROUND_ROBIN'

export interface AxisTriple {
  cards: CardsAxis
  table: TableAxis
  event: EventAxis
}

export function cardsLabel(cards: CardsAxis): string {
  switch (cards.kind) {
    case 'BRING_A_DECK':
      return 'Bring a deck'
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
