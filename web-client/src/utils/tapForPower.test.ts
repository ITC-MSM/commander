import { describe, it, expect } from 'vitest'
import { autoSelectForPower, totalPowerOf, type PowerCandidate } from './tapForPower'
import { entityId } from '@/types'

function creature(id: string, power: number, canAttack?: boolean): PowerCandidate {
  return { entityId: entityId(id), power, ...(canAttack === undefined ? {} : { canAttack }) }
}

/** Total contribution of a pick, so assertions read in the units the cost is paid in. */
function powerOf(pool: readonly PowerCandidate[], picked: readonly ReturnType<typeof entityId>[]) {
  return totalPowerOf(pool, picked)
}

describe('autoSelectForPower', () => {
  it('takes the single creature that covers the cost with the least overshoot', () => {
    const pool = [creature('a', 5), creature('b', 3), creature('c', 8)]
    const picked = autoSelectForPower(pool, 3)
    expect(picked).toEqual([entityId('b')])
  })

  it('combines creatures when none covers the cost alone, largest first', () => {
    const pool = [creature('a', 2), creature('b', 1), creature('c', 2)]
    const picked = autoSelectForPower(pool, 4)
    expect(powerOf(pool, picked)).toBeGreaterThanOrEqual(4)
    expect(picked).toHaveLength(2)
  })

  it('spends creatures that cannot attack before ones that can', () => {
    const pool = [creature('attacker', 3, true), creature('sick', 3, false)]
    expect(autoSelectForPower(pool, 3)).toEqual([entityId('sick')])
  })

  it('reaches into attackers only for the shortfall the spare creatures leave', () => {
    const pool = [
      creature('sick', 2, false),
      creature('smallAttacker', 1, true),
      creature('bigAttacker', 6, true),
    ]
    const picked = autoSelectForPower(pool, 3)
    expect(picked).toEqual([entityId('sick'), entityId('smallAttacker')])
  })

  it('treats a missing canAttack as "could attack" so nothing is spent silently', () => {
    const pool = [creature('unknown', 3), creature('sick', 3, false)]
    expect(autoSelectForPower(pool, 3)).toEqual([entityId('sick')])
  })

  it('never picks a creature contributing nothing, and terminates on an all-zero board', () => {
    const pool = [creature('a', 0), creature('b', 0)]
    expect(autoSelectForPower(pool, 2)).toEqual([])
  })

  it('returns everything that helps when the board cannot cover the cost', () => {
    const pool = [creature('a', 1, false), creature('b', 1, true)]
    const picked = autoSelectForPower(pool, 5)
    expect(picked).toHaveLength(2)
    expect(powerOf(pool, picked)).toBe(2)
  })

  it('picks nothing for a zero requirement', () => {
    expect(autoSelectForPower([creature('a', 3)], 0)).toEqual([])
  })
})

describe('totalPowerOf', () => {
  it('sums only the selected creatures and ignores ids that are no longer candidates', () => {
    const pool = [creature('a', 2), creature('b', 3)]
    expect(totalPowerOf(pool, [entityId('a'), entityId('b'), entityId('gone')])).toBe(5)
  })
})
