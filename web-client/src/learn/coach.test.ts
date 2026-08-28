import { describe, expect, it } from 'vitest'
import { coachTip, wordTip, type CoachView } from './coach'
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

  it('turns the response prompt into an instruction once their spell is on the stack', () => {
    const tip = coachTip({ ...base, isMyTurn: false, canCast: true, stackSize: 1 })
    expect(tip.key).toBe('respond')
    expect(tip.tone).toBe('act')
    expect(tip.body).toMatch(/resolves first/)
    // With nothing to cast, a spell on the stack is only something to watch.
    expect(coachTip({ ...base, isMyTurn: false, stackSize: 1 }).tone).toBe('watch')
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
