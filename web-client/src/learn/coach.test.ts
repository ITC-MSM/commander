import { describe, expect, it } from 'vitest'
import { coachTip, noticeFromLog, noticeFromMove, playerIsActing, wordTip, type CoachView } from './coach'
import type { ClientCard } from '@/types/gameState'
import type { ClientEvent } from '@/types/events'
import type { EntityId } from '@/types'
import { MISSIONS } from './missions'

const base: CoachView = {
  turnNumber: 3,
  step: 'PRECOMBAT_MAIN',
  isMyTurn: true,
  hasPriority: true,
  canPlayLand: false,
  canCast: false,
  canAttack: false,
  canBlock: false,
  hasDecision: false,
  isTargeting: false,
  stackSize: 0,
  attackersIncoming: 0,
  attackersSelected: 0,
  attackersChosen: 0,
  blockersLeft: 0,
  theirCreatures: 0,
  conceded: false,
  passLabel: 'Pass to Attackers',
  hasHover: true,
  isGameOver: false,
  won: null,
}

describe('coachTip', () => {
  it('says to play a land before casting when both are possible', () => {
    expect(coachTip({ ...base, canPlayLand: true, canCast: true }).key).toBe('land-and-cast')
  })

  it('points at the land when that is the only play', () => {
    expect(coachTip({ ...base, canPlayLand: true }).key).toBe('land')
  })

  it('points at castable cards once the land is down', () => {
    expect(coachTip({ ...base, canCast: true }).key).toBe('cast')
  })

  it('tells the player to pass out of an empty main phase', () => {
    expect(coachTip(base).key).toBe('pass-to-combat')
    expect(coachTip({ ...base, step: 'POSTCOMBAT_MAIN' }).key).toBe('pass')
  })

  it('names the flash window once attackers are declared', () => {
    const idle = coachTip({ ...base, isMyTurn: false, attackersIncoming: 1 })
    expect(idle.key).toBe('respond-attack')
    expect(idle.tone).toBe('watch')
    const flash = coachTip({ ...base, isMyTurn: false, attackersIncoming: 2, canCast: true })
    expect(flash.title).toBe('They are attacking with 2 creatures.')
    expect(flash.tone).toBe('act')
    expect(flash.spot).toBe('hand')
    // Once blocks are actually being asked for, the block prompt wins.
    expect(coachTip({ ...base, isMyTurn: false, attackersIncoming: 1, canBlock: true }).key).toBe('block')
  })

  it('warns before an attack that leaves no blocker against a board that can hit back', () => {
    const risky = coachTip({ ...base, canAttack: true, attackersSelected: 1, blockersLeft: 0, theirCreatures: 2 })
    expect(risky.key).toBe('attack-warning')
    expect(risky.tone).toBe('warn')
    expect(risky.body).toMatch(/2 creatures/)
    // Nothing selected yet, a blocker staying home, or an empty board on their side: no warning.
    expect(coachTip({ ...base, canAttack: true, attackersSelected: 0, blockersLeft: 0, theirCreatures: 2 }).key).toBe('attack')
    expect(coachTip({ ...base, canAttack: true, attackersSelected: 1, blockersLeft: 1, theirCreatures: 2 }).key).toBe('attack')
    expect(coachTip({ ...base, canAttack: true, attackersSelected: 1, blockersLeft: 0, theirCreatures: 0 }).key).toBe('attack')
  })

  it('treats a concede as the end of the game, not the mission', () => {
    const tip = coachTip({ ...base, isGameOver: true, won: false, conceded: true })
    expect(tip.key).toBe('conceded')
    expect(tip.tone).toBe('done')
    expect(coachTip({ ...base, isGameOver: true, won: false }).key).toBe('lost')
  })

  it('walks the player through picking a target mid-cast', () => {
    const tip = coachTip({ ...base, isMyTurn: false, canCast: true, stackSize: 1, isTargeting: true })
    expect(tip.key).toBe('target')
    expect(tip.spot).toBe('battlefield')
    expect(tip.body).toMatch(/highlighted — click one/)
  })

  it('ranks the combat prompts above everything but a decision', () => {
    expect(coachTip({ ...base, canAttack: true, canPlayLand: true, canCast: true }).key).toBe('attack')
    expect(coachTip({ ...base, isMyTurn: false, canBlock: true, attackersIncoming: 2 }).title).toBe(
      '2 creatures are attacking you.',
    )
    expect(coachTip({ ...base, canBlock: true, hasDecision: true }).key).toBe('decision')
  })

  it('distinguishes responding on their turn from waiting', () => {
    expect(coachTip({ ...base, isMyTurn: false }).key).toBe('respond-idle')
    const window = coachTip({ ...base, isMyTurn: false, canCast: true })
    expect(window.key).toBe('respond-window')
    expect(window.body).toMatch(/instant right now/)
    expect(coachTip({ ...base, isMyTurn: false, hasPriority: false }).key).toBe('waiting')
  })

  it('explains a spell waiting on the stack during your own turn', () => {
    const idle = coachTip({ ...base, stackSize: 1 })
    expect(idle.key).toBe('stack-mine')
    expect(idle.tone).toBe('watch')
    expect(idle.body).toMatch(/last spell cast happens first/)
    expect(coachTip({ ...base, stackSize: 1, canCast: true }).tone).toBe('act')
    // It outranks the land/cast prompts: a main phase with a stack is not a main phase to play in.
    expect(coachTip({ ...base, stackSize: 1, canPlayLand: true, canCast: true }).key).toBe('stack-mine')
  })

  it('turns the response prompt into an instruction once their spell is on the stack', () => {
    const tip = coachTip({ ...base, isMyTurn: false, canCast: true, stackSize: 1 })
    expect(tip.key).toBe('respond')
    expect(tip.tone).toBe('act')
    expect(tip.body).toMatch(/resolves first/)
    // With nothing to cast, a spell on the stack is only something to watch.
    expect(coachTip({ ...base, isMyTurn: false, stackSize: 1 }).tone).toBe('watch')
  })

  it('keeps "the Tutor is taking a turn" to their turn, and reads the phase on both', () => {
    const mineCombat = coachTip({ ...base, hasPriority: false, step: 'COMBAT_DAMAGE' })
    expect(mineCombat.key).toBe('working')
    expect(mineCombat.title).toBe('Combat is playing out.')
    expect(coachTip({ ...base, hasPriority: false, step: 'END' }).title).toBe('Your turn is ending.')
    expect(coachTip({ ...base, hasPriority: false }).title).toBe('The game is working through your turn.')

    const theirs = (step: string) => coachTip({ ...base, isMyTurn: false, hasPriority: false, step })
    expect(theirs('PRECOMBAT_MAIN').key).toBe('waiting')
    expect(theirs('DECLARE_ATTACKERS').body).toMatch(/in combat/)
    expect(theirs('UPKEEP').body).toMatch(/untap and draw/)
    expect(theirs('CLEANUP').body).toMatch(/turn is ending/)
    // A mission's "they will play a land and pass" override binds to their turn only.
    const override = { waiting: { body: 'They will play a land and pass.' } }
    expect(coachTip({ ...base, isMyTurn: false, hasPriority: false }, override).body).toBe('They will play a land and pass.')
    expect(coachTip({ ...base, hasPriority: false, step: 'COMBAT_DAMAGE' }, override).body).not.toMatch(/play a land/)
  })

  it('has a first-turn tip while the game is still settling', () => {
    expect(coachTip({ ...base, turnNumber: 1, hasPriority: false }).key).toBe('first-turn')
  })

  it('lets a mission re-word a tip by key without changing its tone or spot', () => {
    const tip = coachTip({ ...base, canPlayLand: true }, { land: { title: 'Play the Forest.', body: 'Click it.' } })
    expect(tip).toEqual({ key: 'land', title: 'Play the Forest.', body: 'Click it.', tone: 'act', spot: 'hand' })
    expect(coachTip({ ...base, canCast: true }, { land: { title: 'x', body: 'y' } }).key).toBe('cast')
    expect(coachTip({ ...base, canPlayLand: true }, { land: { spot: 'pass' } }).spot).toBe('pass')
  })

  it('names the real button wherever a tip says to press it', () => {
    expect(coachTip(base).body).toMatch(/Press Pass to Attackers,/)
    expect(coachTip({ ...base, step: 'POSTCOMBAT_MAIN', passLabel: 'End Turn' }).body).toMatch(/^Press End Turn\./)
    expect(coachTip({ ...base, passLabel: 'End Turn' }, { 'pass-to-combat': { title: 'Hit {pass}.', body: 'x' } }).title).toBe('Hit End Turn.')
  })

  it('names the gesture this device has', () => {
    const mouse = coachTip({ ...base, canPlayLand: true })
    const touch = coachTip({ ...base, canPlayLand: true, hasHover: false })
    expect(mouse.body).toMatch(/click it and choose Play/)
    expect(touch.body).toMatch(/tap it and choose Play/)
    expect(wordTip('{read} to read it. {click} it. {pass}!', { passLabel: 'Resolve', hasHover: true })).toBe(
      'Hover a card to read it. Click it. Resolve!',
    )
    expect(wordTip('{read} to read it. {click} it.', { passLabel: 'x', hasHover: false })).toBe(
      'Press and hold a card to read it. Tap it.',
    )
  })

  it('teaches both ways to play a card — dragging and clicking', () => {
    for (const key of ['land', 'land-and-cast', 'cast'] as const) {
      const view = { ...base, canPlayLand: key !== 'cast', canCast: key !== 'land' }
      const tip = coachTip(view)
      expect(tip.key).toBe(key)
      expect(tip.body).toMatch(/[Dd]rag/)
      expect(tip.body).toMatch(/click/)
      expect(tip.spot).toBe('hand')
    }
  })

  it('never leaves a placeholder in any mission’s wording', () => {
    const views = [base, { ...base, hasHover: false }]
    for (const m of MISSIONS) {
      for (const view of views) {
        for (const step of m.tour) {
          expect(wordTip(step.title, view)).not.toMatch(/\{/)
          expect(wordTip(step.body, view)).not.toMatch(/\{/)
        }
        for (const line of m.lessons) expect(wordTip(line, view)).not.toMatch(/\{/)
        for (const [key, hint] of Object.entries(m.hints)) {
          expect(wordTip(hint?.title ?? '', view), `${m.id}/${key}`).not.toMatch(/\{/)
          expect(wordTip(hint?.body ?? '', view), `${m.id}/${key}`).not.toMatch(/\{/)
        }
      }
    }
  })

  it('closes with the result, whatever it was', () => {
    expect(coachTip({ ...base, isGameOver: true, won: true }).tone).toBe('done')
    expect(coachTip({ ...base, isGameOver: true, won: false }).key).toBe('lost')
    expect(coachTip({ ...base, isGameOver: true, won: null }).key).toBe('draw')
    // Game over beats every live prompt.
    expect(coachTip({ ...base, isGameOver: true, won: true, canAttack: true, hasDecision: true }).key).toBe('won')
  })
})

const id = (s: string) => s as EntityId
const me = id('p1')
const them = id('p2')

function cardOf(id: string, types: string[], controllerId: EntityId = me): ClientCard {
  return { id, cardTypes: types, controllerId, ownerId: controllerId } as unknown as ClientCard
}

const cards = Object.fromEntries(
  [cardOf('forest', ['LAND']), cardOf('bear', ['CREATURE']), cardOf('spider', ['CREATURE']), cardOf('cyclops', ['CREATURE'], them)].map(
    (c) => [c.id, c],
  ),
) as Record<EntityId, ClientCard>

describe('noticeFromLog', () => {
  it('says the land is down, with what that buys', () => {
    const log: ClientEvent[] = [
      { type: 'permanentEntered', cardId: id('forest'), cardName: 'Forest', controllerId: me, enteredTapped: false, description: '' },
    ]
    const n = noticeFromLog(log, me, cards, 4)
    expect(n?.key).toBe('permanentEntered-4')
    expect(n?.text).toMatch(/^Forest is on the battlefield/)
  })

  it('answers a creature cast with its arrival, not the cast', () => {
    const log: ClientEvent[] = [
      { type: 'spellCast', spellId: id('bear'), spellName: 'Grizzly Bears', casterId: me, description: '' },
      { type: 'spellResolved', spellId: id('bear'), spellName: 'Grizzly Bears', description: '' },
      { type: 'permanentEntered', cardId: id('bear'), cardName: 'Grizzly Bears', controllerId: me, enteredTapped: false, description: '' },
    ]
    expect(noticeFromLog(log, me, cards, 0)?.text).toMatch(/Grizzly Bears is on the battlefield.*next turn/)
    // The cast alone — an instant, or the first update of two — is the stack line.
    expect(noticeFromLog(log.slice(0, 1), me, cards, 0)?.text).toMatch(/^You cast Grizzly Bears/)
  })

  it('ignores what the other seat did', () => {
    const log: ClientEvent[] = [
      { type: 'permanentEntered', cardId: id('cyclops'), cardName: 'Bloodrock Cyclops', controllerId: them, enteredTapped: false, description: '' },
      { type: 'spellCast', spellId: id('x'), spellName: 'Lightning Bolt', casterId: them, description: '' },
      { type: 'creatureAttacked', creatureId: id('cyclops'), creatureName: 'Bloodrock Cyclops', attackingPlayerId: them, defendingPlayerId: me, description: '' },
      { type: 'creatureBlocked', blockerId: id('cyclops'), blockerName: 'Bloodrock Cyclops', attackerId: id('bear'), attackerName: 'Grizzly Bears', description: '' },
    ]
    expect(noticeFromLog(log, me, cards, 0)).toBeNull()
    expect(noticeFromLog([], me, cards, 0)).toBeNull()
  })

  it('lists the attackers as one line and keys it once', () => {
    const log: ClientEvent[] = [
      { type: 'creatureAttacked', creatureId: id('bear'), creatureName: 'Grizzly Bears', attackingPlayerId: me, defendingPlayerId: them, description: '' },
      { type: 'creatureAttacked', creatureId: id('spider'), creatureName: 'Giant Spider', attackingPlayerId: me, defendingPlayerId: them, description: '' },
    ]
    const n = noticeFromLog(log, me, cards, 7)
    expect(n?.text).toBe('Grizzly Bears and Giant Spider attack.')
    expect(n?.key).toBe('attack-7')
    expect(noticeFromLog(log.slice(0, 1), me, cards, 7)?.text).toBe('Grizzly Bears attacks.')
  })

  it('answers a block by whose creature blocked', () => {
    const log: ClientEvent[] = [
      { type: 'creatureBlocked', blockerId: id('spider'), blockerName: 'Giant Spider', attackerId: id('cyclops'), attackerName: 'Bloodrock Cyclops', description: '' },
    ]
    expect(noticeFromLog(log, me, cards, 0)?.text).toMatch(/^Giant Spider blocks Bloodrock Cyclops/)
  })
})

describe('noticeFromMove', () => {
  it('reads a pass off the step moving while the player held priority', () => {
    expect(noticeFromMove(base, { ...base, step: 'BEGIN_COMBAT' })?.text).toBe('You passed.')
    expect(noticeFromMove(base, { ...base, turnNumber: 4, isMyTurn: false, hasPriority: false })?.text).toBe('You passed.')
    // Without priority the game moves on its own — the Tutor's turn is not the player's doing.
    const theirs = { ...base, isMyTurn: false, hasPriority: false, step: 'UPKEEP' }
    expect(noticeFromMove(theirs, { ...theirs, step: 'PRECOMBAT_MAIN' })).toBeNull()
    expect(noticeFromMove(base, base)).toBeNull()
  })

  it('reads the stack resolving as a pass', () => {
    expect(noticeFromMove({ ...base, stackSize: 1 }, base)?.key).toMatch(/^resolved-/)
    // The stack growing is the other seat responding, not a pass.
    expect(noticeFromMove({ ...base, stackSize: 1 }, { ...base, stackSize: 2, hasPriority: true })).toBeNull()
  })

  it('names a skipped attack and no blocks, and an answered prompt', () => {
    expect(noticeFromMove({ ...base, canAttack: true }, base)?.text).toMatch(/^No attack this turn/)
    expect(noticeFromMove({ ...base, canBlock: true, isMyTurn: false }, { ...base, isMyTurn: false })?.text).toMatch(/^No blocks/)
    expect(noticeFromMove({ ...base, hasDecision: true }, base)?.text).toBe('Answered.')
  })
})

describe('playerIsActing', () => {
  it('is true mid-cast and mid-attack, false at rest', () => {
    expect(playerIsActing(base)).toBe(false)
    expect(playerIsActing({ ...base, isTargeting: true })).toBe(true)
    expect(playerIsActing({ ...base, canAttack: true, attackersSelected: 1, attackersChosen: 1 })).toBe(true)
    // A creature that must attack is selected before the player has done anything.
    expect(playerIsActing({ ...base, canAttack: true, attackersSelected: 1, attackersChosen: 0 })).toBe(false)
  })
})
