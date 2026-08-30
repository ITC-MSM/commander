import { describe, it, expect } from 'vitest'
import { keepAttackerPreview, keepBlockerPreview } from './combatPreview'
import { Step } from '@/types'

/**
 * The bug these pin: an attacker selection streamed to the table, then *not* declared — the
 * attacker cancelled, or declared no attackers — used to be cleared only by "the server's combat
 * state now exists". No attackers means no combat state, so nothing ever cleared it, and its
 * arrows stayed painted over the board for the rest of the game (free-for-all worst of all, where
 * the preview carries a defender per attacker and so draws full arrows).
 */
describe('keepAttackerPreview', () => {
  it('keeps the preview while attackers are being declared', () => {
    expect(keepAttackerPreview(Step.DECLARE_ATTACKERS, false)).toBe(true)
  })

  it('drops it once the declaration landed and combat carries the real attackers', () => {
    expect(keepAttackerPreview(Step.DECLARE_ATTACKERS, true)).toBe(false)
  })

  it('drops it when the declaration never landed — no attackers, so no combat state', () => {
    // The step has moved on with `combat` still null: exactly the case that used to stick.
    expect(keepAttackerPreview(Step.DECLARE_BLOCKERS, false)).toBe(false)
    expect(keepAttackerPreview(Step.END_COMBAT, false)).toBe(false)
    expect(keepAttackerPreview(Step.POSTCOMBAT_MAIN, false)).toBe(false)
    expect(keepAttackerPreview(Step.END, false)).toBe(false)
  })

  it('drops it when the step is unknown, rather than painting on a guess', () => {
    expect(keepAttackerPreview(null, false)).toBe(false)
    expect(keepAttackerPreview(undefined, false)).toBe(false)
  })
})

describe('keepBlockerPreview', () => {
  it('keeps the preview while blockers are being declared and none are in combat yet', () => {
    expect(keepBlockerPreview(Step.DECLARE_BLOCKERS, true, false)).toBe(true)
  })

  it('drops it once the declaration landed — combat carries the blockers', () => {
    expect(keepBlockerPreview(Step.DECLARE_BLOCKERS, true, true)).toBe(false)
  })

  it('drops it past the declare blockers step, even with combat still running', () => {
    expect(keepBlockerPreview(Step.COMBAT_DAMAGE, true, false)).toBe(false)
    expect(keepBlockerPreview(Step.END_COMBAT, true, false)).toBe(false)
  })

  it('drops it when there is no combat at all', () => {
    expect(keepBlockerPreview(Step.DECLARE_BLOCKERS, false, false)).toBe(false)
  })
})
