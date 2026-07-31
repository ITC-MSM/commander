/**
 * The lobby's settings, grouped under the words the wizard already taught.
 *
 * A host configuring a booster draft with Commander rules and two sets used to face **twenty stacked
 * rows, around 1500px, some sixty interactive elements** in a 756px column — with the player list and
 * the Start button below all of it. Every row was individually justified; the total was not.
 *
 * ## Why these five groups and not some other five
 *
 * Four of them are the axes (`axes.ts`), which the player has just answered in the wizard and which
 * `LobbyAxisSummary` already shows as chips at the top of this very screen. Introducing a second
 * vocabulary here — "Pool", "Timing", "Advanced" — would be the exact mistake this project spent
 * Phase 2 undoing, when "Format" meant two things and "Mode" meant three.
 *
 * The grouping is also what the code already believes: `TournamentLobbySettings` gates the Commander
 * deckbuild knobs on `axes.rules === 'COMMANDER'`, the matchup count on `axes.event === 'ROUND_ROBIN'`,
 * and the attack rule on a Free-for-All *table*. Each of those rows is already a refinement of one
 * axis; the group is just where it was always pointing.
 *
 * The fifth, **This lobby**, is the honest remainder: visibility, AI assistance and the AI seat's
 * deck are facts about *this room*, not about the game being played, and they are the rows a saved
 * setup is least likely to be about.
 *
 * ## The collapse does not hide anything you could act on
 *
 * The project's standing rule is *disabled-with-reason over hiding* — every greyed option is a
 * tracked server gap, and rendering it teaches the shape of the system. That rule is about **values
 * within a control**, and it is untouched here: each group header carries its axis's full button
 * strip, so every value stays one click away with its reason and its `⇄` intact. What collapses is
 * the *refinements* below it, and each header states their live values.
 *
 * Two guards keep the two from drifting apart:
 *
 * - a group holding the reason Start is disabled **opens itself** and shows a `!` — see
 *   {@link blockingGroupFor}, which reads the same `startBlockReason` that writes the button's
 *   tooltip rather than a second hand-maintained mapping;
 * - a group is never a scroll container. `.lobbyOverlay` remains the single one, which is the lesson
 *   written into `GameUI.module.css` after an earlier attempt at a scrolling settings panel produced
 *   three competing scrollbars.
 */
import type { LobbyState } from '@/store/slices/types'
import {
  COMMANDER_PRESETS,
  cardsKindTopicId,
  cardsLabel,
  effectiveCommanderPreset,
  eventLabel,
  eventTopicId,
  rulesLabel,
  rulesTopicId,
  tableLabel,
  tableTopicId,
} from './axes'
import type { UnifiedLobbyView } from './lobbyViewModel'

export type GroupId = 'CARDS' | 'RULES' | 'TABLE' | 'EVENT' | 'LOBBY'

/** Reading order, and the order they render in: what deck → under what rules → at what table →
 *  over how many games → and how this particular room is set up. */
export const GROUP_IDS: readonly GroupId[] = ['CARDS', 'RULES', 'TABLE', 'EVENT', 'LOBBY']

export function groupLabel(id: GroupId): string {
  switch (id) {
    case 'CARDS': return 'Cards'
    case 'RULES': return 'Rules'
    case 'TABLE': return 'Table'
    case 'EVENT': return 'Event'
    case 'LOBBY': return 'This lobby'
  }
}

/** The help topic bound to the *value in effect*, so a group's `?` explains what is selected. */
export function groupTopicId(id: GroupId, view: UnifiedLobbyView): string | null {
  switch (id) {
    case 'CARDS': return cardsKindTopicId(view.axes.cards.kind)
    case 'RULES': return rulesTopicId(view.axes.rules)
    case 'TABLE': return tableTopicId(view.axes.table)
    case 'EVENT': return eventTopicId(view.axes.event)
    case 'LOBBY': return null
  }
}

/**
 * The live values a collapsed group is holding — "ECL + BLB · 6 packs · 45s".
 *
 * This is what makes collapsing honest rather than merely tidier: a host scanning five headers can
 * see what every group currently *is* without opening any of them, and only opens the one that is
 * wrong. It deliberately names values, never row labels: "45s" is useful, "Pick timer" is not.
 */
export function groupSummary(id: GroupId, view: UnifiedLobbyView, lobbyState: LobbyState | null): string {
  const s = lobbyState?.settings
  const parts: string[] = []

  switch (id) {
    case 'CARDS': {
      parts.push(cardsLabel(view.axes.cards))
      if (s) {
        if (s.cubeName) parts.push(`${s.cubeName} (${s.cubeCardCount ?? 0})`)
        else if (s.setCodes.length > 0) parts.push(s.setNames.join(' + ') || s.setCodes.join(' + '))
        else if (s.format !== 'PREMADE_DECKS') parts.push('no sets yet')
        if (usesBoosters(s.format)) parts.push(`${s.boosterCount} ${countsPacks(s.format) ? 'packs' : 'boosters'}`)
        if (isAnyDraft(s.format)) parts.push(`${s.pickTimeSeconds}s`)
        if (s.picksPerRound === 2) parts.push('pick 2')
        if (s.bannedCardNames.length > 0) parts.push(`${s.bannedCardNames.length} banned`)
      }
      break
    }
    case 'RULES': {
      parts.push(rulesLabel(view.axes.rules))
      if (s && view.axes.rules === 'COMMANDER' && s.format !== 'PREMADE_DECKS') {
        parts.push(`${COMMANDER_PRESETS[effectiveCommanderPreset(s.commanderPreset, s.gameMode)].life} life`)
        parts.push(`min ${s.deckSizeMin}`)
        parts.push(s.allowDuplicates ? 'duplicates OK' : 'singleton')
      }
      break
    }
    case 'TABLE': {
      parts.push(tableLabel(view.axes.table))
      if (s?.gameMode === 'FREE_FOR_ALL') {
        parts.push(attackLabel(s.attackMode))
      }
      if (view.teams.mode !== 'NONE') {
        parts.push(view.teams.mode === 'RANDOM' ? 'random teams' : 'chosen teams')
      }
      break
    }
    case 'EVENT': {
      parts.push(eventLabel(view.axes.event))
      const games = s?.gamesPerMatch ?? 1
      if (view.axes.event === 'ROUND_ROBIN' && games > 1) parts.push(`${games} games per matchup`)
      if (view.ranked.available) parts.push(view.ranked.on ? 'ranked' : 'casual')
      break
    }
    case 'LOBBY': {
      if (view.invitable) parts.push(view.isPublic ? 'public' : 'private')
      if (s) parts.push(`AI assist ${s.aiAssistEnabled ? 'on' : 'off'}`)
      break
    }
  }
  return parts.join(' · ')
}

function attackLabel(mode: LobbyState['settings']['attackMode'] | undefined): string {
  switch (mode ?? 'MULTIPLE') {
    case 'LEFT': return 'attack left'
    case 'RIGHT': return 'attack right'
    default: return 'attack any'
  }
}

function isAnyDraft(format: string): boolean {
  return format === 'DRAFT' || format === 'WINSTON_DRAFT' || format === 'GRID_DRAFT' ||
    format === 'COMMANDER_DRAFT'
}

function countsPacks(format: string): boolean {
  return format === 'DRAFT' || format === 'COMMANDER_DRAFT'
}

function usesBoosters(format: string): boolean {
  return format !== 'PREMADE_DECKS' && format !== 'GRID_DRAFT'
}

/**
 * Which group holds the reason the host can't press Start, if any.
 *
 * Derived from `view.blockReason`, which is the same value that becomes the Start button's tooltip —
 * so the group that opens itself and the sentence the host reads are guaranteed to be about the same
 * thing. A second, hand-maintained "which row fixes this" table is exactly what would rot.
 *
 * Null means there is nothing in the settings to open: "Need at least 2 players" and "all connected
 * players must submit a deck" are answered by the player list, not by a knob.
 */
export function blockingGroupFor(view: UnifiedLobbyView): GroupId | null {
  return view.blockGroup
}
