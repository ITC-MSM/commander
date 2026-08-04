import { describe, expect, it } from 'vitest'
import { getPileTargetCards, isLoneTargetRequirement } from './targeting'
import type { ChooseTargetsDecision, ClientCard, EntityId, TargetRequirementInfo } from '@/types'
import { ZoneType } from '@/types'
import { entityId } from '@/types/entities.ts'

const req = (overrides: Partial<TargetRequirementInfo> = {}): TargetRequirementInfo => ({
  index: 0,
  minTargets: 1,
  maxTargets: 1,
  description: 'target player',
  ...overrides,
})

const decision = (
  requirements: TargetRequirementInfo[],
  legalTargets: Record<number, readonly EntityId[]> = {},
): ChooseTargetsDecision => ({
  type: 'ChooseTargetsDecision',
  id: 'd1',
  playerId: entityId('p1'),
  prompt: 'Choose targets',
  context: { phase: 'TRIGGER' },
  targetRequirements: requirements,
  legalTargets,
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

const owner = entityId('p2')

const card = (id: string, zoneType: ZoneType | null): ClientCard =>
  ({
    id: entityId(id),
    name: id,
    ownerId: owner,
    zone: zoneType === null ? null : { zoneType, ownerId: owner },
  }) as unknown as ClientCard

const cardMap = (...cards: ClientCard[]) =>
  Object.fromEntries(cards.map((c) => [c.id, c])) as Record<string, ClientCard>

describe('getPileTargetCards', () => {
  const bears = card('bears', ZoneType.BATTLEFIELD)
  const courser = card('courser', ZoneType.GRAVEYARD)
  const exiled = card('exiled', ZoneType.EXILE)
  const cards = cardMap(bears, courser, exiled)

  it('returns the cards when every legal target sits in a graveyard', () => {
    expect(getPileTargetCards([courser.id], cards)).toEqual([courser])
  })

  it('returns the cards when every legal target sits in exile', () => {
    expect(getPileTargetCards([exiled.id], cards)).toEqual([exiled])
  })

  it('treats graveyard and exile as one pile set', () => {
    expect(getPileTargetCards([courser.id, exiled.id], cards)).toEqual([courser, exiled])
  })

  it('returns null when any legal target is on the battlefield', () => {
    expect(getPileTargetCards([bears.id, courser.id], cards)).toBeNull()
  })

  it('returns null for an empty legal-target set', () => {
    expect(getPileTargetCards([], cards)).toBeNull()
  })

  it('returns null when a target is missing from the client card map', () => {
    expect(getPileTargetCards([entityId('unknown')], cards)).toBeNull()
  })

  it('returns null when a target card carries no zone', () => {
    const zoneless = card('zoneless', null)
    expect(getPileTargetCards([zoneless.id], cardMap(zoneless))).toBeNull()
  })

  it('routes per requirement: The Spot, Living Portal exiles a permanent AND a graveyard card', () => {
    // "exile up to one target nonland permanent and up to one target nonland permanent card
    // from a graveyard" — slot 0 is a board click, slot 1 needs the pile picker.
    const spot = decision(
      [
        req({ index: 0, minTargets: 0, description: 'up to one target nonland permanent' }),
        req({ index: 1, minTargets: 0, description: 'up to one target nonland permanent card from a graveyard' }),
      ],
      { 0: [bears.id], 1: [courser.id] },
    )
    const legalTargets = spot.legalTargets

    expect(spot.targetRequirements).toHaveLength(2)
    expect(getPileTargetCards(legalTargets[0] ?? [], cards)).toBeNull()
    expect(getPileTargetCards(legalTargets[1] ?? [], cards)).toEqual([courser])
  })
})
