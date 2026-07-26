import { describe, expect, it } from 'vitest'
import { isLoneTargetRequirement } from './targeting'
import type { ChooseTargetsDecision, TargetRequirementInfo } from '@/types'
import { entityId } from '@/types/entities.ts'

const req = (overrides: Partial<TargetRequirementInfo> = {}): TargetRequirementInfo => ({
  index: 0,
  minTargets: 1,
  maxTargets: 1,
  description: 'target player',
  ...overrides,
})

const decision = (requirements: TargetRequirementInfo[]): ChooseTargetsDecision => ({
  type: 'ChooseTargetsDecision',
  id: 'd1',
  playerId: entityId('p1'),
  prompt: 'Choose targets',
  context: { phase: 'TRIGGER' },
  targetRequirements: requirements,
  legalTargets: {},
})

describe('isLoneTargetRequirement', () => {
  it('is true for a single requirement wanting one target', () => {
    expect(isLoneTargetRequirement(decision([req({ minTargets: 1, maxTargets: 1 })]))).toBe(true)
  })

  it('is true for a single optional up-to-one requirement', () => {
    expect(isLoneTargetRequirement(decision([req({ minTargets: 0, maxTargets: 1 })]))).toBe(true)
  })

  it('is false when the single requirement wants more than one target (Parker Luck: two target players)', () => {
    expect(isLoneTargetRequirement(decision([req({ minTargets: 2, maxTargets: 2 })]))).toBe(false)
  })

  it('is false for multiple requirements', () => {
    expect(isLoneTargetRequirement(decision([req(), req({ index: 1 })]))).toBe(false)
  })

  it('is false for a decision with no requirements', () => {
    expect(isLoneTargetRequirement(decision([]))).toBe(false)
  })
})
