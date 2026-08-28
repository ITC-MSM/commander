/**
 * A mission is only as good as its card list: a name the corpus does not know makes the server
 * reject the whole scenario, and the course's card becomes a dead end. So every name is checked
 * against the card definition files in `mtg-sets/` — the same tree `CardDiscovery` scans.
 *
 * Files are named after their card in PascalCase (`GrizzlyBears.kt`, `SerraAngelReprint.kt`),
 * so the check is on file names alone and never reads a file. Basic lands are the one exception:
 * `CardDiscovery` generates them, so they are whitelisted here.
 */
import { readdirSync } from 'node:fs'
import { resolve } from 'node:path'
import { describe, expect, it } from 'vitest'
import type { ClientEvent } from '@/types/events'
import type { ClientCard, ClientGameState } from '@/types/gameState'
import type { EntityId } from '@/types'
import { COURSE_MINUTES, MISSIONS, missionById, missionCardNames, nextMission, type ObjectiveContext } from './missions'
import { hasStarted, nextIncomplete } from './progressStore'
import { ALL_SPOTS } from './spots'

const SETS_ROOT = resolve(__dirname, '../../../mtg-sets')
const BASICS = ['Plains', 'Island', 'Swamp', 'Mountain', 'Forest']

/** `Mons's Goblin Raiders` → `monssgoblinraiders`; `SerraAngelReprint.kt` → `serraangel`. */
function key(name: string): string {
  return name.toLowerCase().replace(/[^a-z0-9]/g, '')
}

function corpusKeys(): Set<string> {
  const keys = new Set<string>(BASICS.map(key))
  for (const entry of readdirSync(SETS_ROOT, { recursive: true, withFileTypes: true })) {
    if (!entry.isFile() || !entry.name.endsWith('.kt')) continue
    const dir = entry.parentPath ?? ''
    if (!dir.includes('/src/main/') || !dir.includes('/definitions/') || !dir.includes('/cards')) continue
    keys.add(key(entry.name.replace(/\.kt$/, '').replace(/Reprint$/, '')))
  }
  return keys
}

describe('the missions', () => {
  it('use only cards the engine has', () => {
    const known = corpusKeys()
    expect(known.size).toBeGreaterThan(1000)
    const unknown = missionCardNames().filter((n) => !known.has(key(n)))
    expect(unknown).toEqual([])
  })

  it('are numbered 1..n in order, with unique ids and a win objective each', () => {
    expect(MISSIONS.map((m) => m.number)).toEqual(MISSIONS.map((_, i) => i + 1))
    expect(new Set(MISSIONS.map((m) => m.id)).size).toBe(MISSIONS.length)
    for (const m of MISSIONS) {
      expect(m.objectives.map((o) => o.id)).toContain('win')
      expect(m.brief.length).toBe(3)
      expect(m.openingCards.length).toBeGreaterThan(0)
    }
  })

  it('are all two-seat AI games inside the public endpoint limits', () => {
    for (const m of MISSIONS) {
      const spec = m.spec('Vincent')
      expect(spec.mode).toBe('AI')
      expect(spec.aiPlayer).toBe(2)
      expect(spec.player1Name).toBe('Vincent')
      for (const seat of [spec.player1, spec.player2]) {
        expect(seat?.library?.length ?? 0).toBeLessThanOrEqual(100)
        expect(seat?.library?.length ?? 0).toBeGreaterThanOrEqual(8)
      }
      // The opening cards the brief shows really are in the opening hand or on the board.
      const opening = new Set([...(spec.player1?.hand ?? []), ...(spec.player1?.battlefield ?? []).map((c) => c.name)])
      expect(m.openingCards.map((c) => c.name).filter((n) => !opening.has(n))).toEqual([])
    }
  })

  it('walk the table before the game, and say what was learned after it', () => {
    for (const m of MISSIONS) {
      expect(m.tour.length, m.id).toBeGreaterThanOrEqual(2)
      expect(m.tour.length, m.id).toBeLessThanOrEqual(4)
      for (const step of m.tour) {
        expect(ALL_SPOTS).toContain(step.spot)
        expect(step.body.length).toBeLessThan(220)
      }
      expect(m.lessons.length, m.id).toBeGreaterThanOrEqual(2)
      expect(m.lessons.length, m.id).toBeLessThanOrEqual(3)
      for (const c of m.openingCards) expect(c.note.length, c.name).toBeGreaterThan(10)
      for (const hint of Object.values(m.hints)) if (hint?.spot) expect(ALL_SPOTS).toContain(hint.spot)
    }
    // Every card the course teaches with is met on the table before it is named: the first
    // mission's tour covers the hand, the battlefield, the turn strip and the pass button.
    expect(missionById('first-steps')?.tour.map((s) => s.spot)).toEqual(['hand', 'battlefield', 'phase-strip', 'pass'])
  })

  it('keeps the promise on the home page honest', () => {
    expect(COURSE_MINUTES).toBeGreaterThanOrEqual(15)
    expect(COURSE_MINUTES).toBeLessThanOrEqual(30)
  })

  it('walks forward', () => {
    expect(nextMission('first-steps')?.id).toBe('blocking')
    expect(nextMission('real-game')).toBeUndefined()
    expect(missionById('nope')).toBeUndefined()
  })
})

// ── Objective detection ─────────────────────────────────────────────────────

const ME = 'p1' as unknown as EntityId
const THEM = 'p2' as unknown as EntityId

function cardOf(id: string, owner: EntityId, types: string[]): ClientCard {
  return { id, name: id, cardTypes: types, ownerId: owner, controllerId: owner } as unknown as ClientCard
}

/** Event literals use plain-string ids; the brand is a compile-time fiction the log never sees. */
function ctx(events: readonly object[], cards: ClientCard[], extra: Partial<ClientGameState> = {}): ObjectiveContext {
  const state = {
    viewingPlayerId: ME,
    cards: Object.fromEntries(cards.map((c) => [c.id, c])),
    gameLog: events as unknown as ClientEvent[],
    isGameOver: false,
    winnerId: null,
    ...extra,
  } as unknown as ClientGameState
  return { state, me: ME, won: null }
}

const objective = (missionId: string, id: string) => {
  const o = missionById(missionId)?.objectives.find((x) => x.id === id)
  if (!o) throw new Error(`no objective ${id} in ${missionId}`)
  return o
}

describe('objectives', () => {
  it('sees a land you played, not one the opponent played', () => {
    const land = objective('first-steps', 'land')
    const mine = { type: 'permanentEntered', cardId: 'f1', cardName: 'Forest', controllerId: ME, enteredTapped: false, description: '' } as const
    expect(land.done(ctx([mine], [cardOf('f1', ME, ['LAND'])]))).toBe(true)
    expect(land.done(ctx([{ ...mine, controllerId: THEM }], [cardOf('f1', THEM, ['LAND'])]))).toBe(false)
    // A creature entering is not a land; the server's casing is upper, but either casing must read.
    expect(land.done(ctx([mine], [cardOf('f1', ME, ['CREATURE'])]))).toBe(false)
    expect(land.done(ctx([mine], [cardOf('f1', ME, ['Land'])]))).toBe(true)
  })

  it('tells a creature spell from an instant', () => {
    const cast = { type: 'spellCast', spellId: 's1', spellName: 'x', casterId: ME, description: '' } as const
    expect(objective('first-steps', 'creature').done(ctx([cast], [cardOf('s1', ME, ['CREATURE'])]))).toBe(true)
    expect(objective('first-steps', 'creature').done(ctx([cast], [cardOf('s1', ME, ['INSTANT'])]))).toBe(false)
    expect(objective('instants', 'instant').done(ctx([cast], [cardOf('s1', ME, ['INSTANT'])]))).toBe(true)
  })

  it('credits attacks and blocks to the right seat', () => {
    const attack = { type: 'creatureAttacked', creatureId: 'c1', creatureName: 'x', attackingPlayerId: ME, defendingPlayerId: THEM, description: '' } as const
    expect(objective('first-steps', 'attack').done(ctx([attack], []))).toBe(true)
    expect(objective('first-steps', 'attack').done(ctx([{ ...attack, attackingPlayerId: THEM }], []))).toBe(false)

    const block = { type: 'creatureBlocked', blockerId: 'b1', blockerName: 'x', attackerId: 'a1', attackerName: 'y', description: '' } as const
    expect(objective('blocking', 'block').done(ctx([block], [cardOf('b1', ME, ['CREATURE'])]))).toBe(true)
    expect(objective('blocking', 'block').done(ctx([block], [cardOf('b1', THEM, ['CREATURE'])]))).toBe(false)
  })

  it('counts only their creatures dying', () => {
    const died = { type: 'creatureDied', creatureId: 'c1', creatureName: 'x', description: '' } as const
    expect(objective('blocking', 'kill').done(ctx([died], [cardOf('c1', THEM, ['CREATURE'])]))).toBe(true)
    expect(objective('blocking', 'kill').done(ctx([died], [cardOf('c1', ME, ['CREATURE'])]))).toBe(false)
  })

  it('wins from the store result or the state', () => {
    const win = objective('real-game', 'win')
    expect(win.done(ctx([], []))).toBe(false)
    expect(win.done({ ...ctx([], []), won: true })).toBe(true)
    expect(win.done(ctx([], [], { isGameOver: true, winnerId: ME }))).toBe(true)
    expect(win.done(ctx([], [], { isGameOver: true, winnerId: THEM }))).toBe(false)
  })
})

describe('progress helpers', () => {
  it('continues at the first unfinished mission, in course order', () => {
    expect(nextIncomplete([])).toBe('first-steps')
    expect(nextIncomplete(['first-steps', 'instants'])).toBe('blocking')
    expect(nextIncomplete(MISSIONS.map((m) => m.id))).toBeUndefined()
    expect(hasStarted({ completed: [] })).toBe(false)
    expect(hasStarted({ completed: ['first-steps'] })).toBe(true)
  })
})
