import { describe, expect, it } from 'vitest'
import { coachTip, type CoachView } from './coach'

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
  attackersIncoming: 0,
  passLabel: 'Pass to Attackers',
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

  it('ranks the combat prompts above everything but a decision', () => {
    expect(coachTip({ ...base, canAttack: true, canPlayLand: true, canCast: true }).key).toBe('attack')
    expect(coachTip({ ...base, isMyTurn: false, canBlock: true, attackersIncoming: 2 }).title).toBe(
      '2 creatures are attacking you.',
    )
    expect(coachTip({ ...base, canBlock: true, hasDecision: true }).key).toBe('decision')
  })

  it('distinguishes responding on their turn from waiting', () => {
    expect(coachTip({ ...base, isMyTurn: false }).key).toBe('respond')
    expect(coachTip({ ...base, isMyTurn: false, canCast: true }).body).toMatch(/instant right now/)
    expect(coachTip({ ...base, isMyTurn: false, hasPriority: false }).key).toBe('waiting')
  })

  it('has a first-turn tip while the game is still settling', () => {
    expect(coachTip({ ...base, turnNumber: 1, hasPriority: false }).key).toBe('first-turn')
  })

  it('lets a mission re-word a tip by key without changing its tone', () => {
    const tip = coachTip({ ...base, canPlayLand: true }, { land: { title: 'Play the Forest.', body: 'Click it.' } })
    expect(tip).toEqual({ key: 'land', title: 'Play the Forest.', body: 'Click it.', tone: 'act' })
    expect(coachTip({ ...base, canCast: true }, { land: { title: 'x', body: 'y' } }).key).toBe('cast')
  })

  it('names the real button wherever a tip says to press it', () => {
    expect(coachTip(base).body).toMatch(/Press Pass to Attackers,/)
    expect(coachTip({ ...base, step: 'POSTCOMBAT_MAIN', passLabel: 'End Turn' }).body).toMatch(/^Press End Turn\./)
    expect(coachTip({ ...base, passLabel: 'End Turn' }, { 'pass-to-combat': { title: 'Hit {pass}.', body: 'x' } }).title).toBe('Hit End Turn.')
  })

  it('closes with the result, whatever it was', () => {
    expect(coachTip({ ...base, isGameOver: true, won: true }).tone).toBe('done')
    expect(coachTip({ ...base, isGameOver: true, won: false }).key).toBe('lost')
    expect(coachTip({ ...base, isGameOver: true, won: null }).key).toBe('draw')
    // Game over beats every live prompt.
    expect(coachTip({ ...base, isGameOver: true, won: true, canAttack: true, hasDecision: true }).key).toBe('won')
  })
})
