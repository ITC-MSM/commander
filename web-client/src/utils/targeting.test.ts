import { describe, expect, it } from 'vitest'
import {
  derivePileAction,
  describePileZones,
  isLoneTargetRequirement,
  partitionTargetsByZone,
  routeTargetsByZone,
} from './targeting'
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

describe('routeTargetsByZone', () => {
  const bears = card('bears', ZoneType.BATTLEFIELD)
  const courser = card('courser', ZoneType.GRAVEYARD)
  const exiled = card('exiled', ZoneType.EXILE)
  const cards = cardMap(bears, courser, exiled)

  it('routes a battlefield-only requirement to the board', () => {
    expect(routeTargetsByZone([bears.id], cards).mode).toBe('board')
  })

  it('routes a graveyard-only requirement to the pile picker', () => {
    const route = routeTargetsByZone([courser.id], cards)
    expect(route.mode).toBe('pile')
    expect(route.pileCards).toEqual([courser])
    expect(route.pileZoneLabel).toBe('Graveyard')
  })

  it('routes a battlefield ∪ graveyard union to BOTH (Taskmaster, Mercenary Mimic)', () => {
    const route = routeTargetsByZone([bears.id, courser.id], cards)
    expect(route.mode).toBe('mixed')
    expect(route.pileCards).toEqual([courser])
    expect(route.hasBoardTargets).toBe(true)
  })

  it('routes an empty target set to the board (nothing to open a picker for)', () => {
    expect(routeTargetsByZone([], cards).mode).toBe('board')
  })

  it('routes per requirement: The Spot, Living Portal exiles a permanent AND a graveyard card', () => {
    // "exile up to one target nonland permanent and up to one target nonland permanent card
    // from a graveyard" — slot 0 is a board click, slot 1 needs the pile picker. Neither slot is
    // mixed, so neither shows the cross-over button.
    const spot = decision(
      [
        req({ index: 0, minTargets: 0, description: 'up to one target nonland permanent' }),
        req({ index: 1, minTargets: 0, description: 'up to one target nonland permanent card from a graveyard' }),
      ],
      { 0: [bears.id], 1: [courser.id] },
    )

    expect(routeTargetsByZone(spot.legalTargets[0] ?? [], cards).mode).toBe('board')
    expect(routeTargetsByZone(spot.legalTargets[1] ?? [], cards).mode).toBe('pile')
  })

  it('labels the pile from the cards actually there', () => {
    expect(describePileZones([courser])).toBe('Graveyard')
    expect(describePileZones([exiled])).toBe('Exile')
    expect(describePileZones([courser, exiled])).toBe('Graveyard / Exile')
    expect(routeTargetsByZone([bears.id, exiled.id], cards).pileZoneLabel).toBe('Exile')
  })
})

describe('partitionTargetsByZone', () => {
  const bears = card('bears', ZoneType.BATTLEFIELD)
  const courser = card('courser', ZoneType.GRAVEYARD)
  const exiled = card('exiled', ZoneType.EXILE)
  const cards = cardMap(bears, courser, exiled)

  it('sends a battlefield-only requirement to the board', () => {
    expect(partitionTargetsByZone([bears.id], cards)).toEqual({
      pileCards: [],
      hasBoardTargets: true,
    })
  })

  it('sends a graveyard-only requirement to the pile picker', () => {
    expect(partitionTargetsByZone([courser.id], cards)).toEqual({
      pileCards: [courser],
      hasBoardTargets: false,
    })
  })

  it('keeps a graveyard ∪ exile union pile-only (Sorceress\'s Schemes)', () => {
    expect(partitionTargetsByZone([courser.id, exiled.id], cards)).toEqual({
      pileCards: [courser, exiled],
      hasBoardTargets: false,
    })
  })

  it('reports BOTH routes for a battlefield ∪ graveyard union (Taskmaster, Mercenary Mimic)', () => {
    // Playtest regression: "becomes a copy of up to one target creature on the battlefield or
    // creature card in a graveyard". The old all-or-nothing flag collapsed this to board-only and
    // the graveyard cards became unselectable. Both halves must be reported.
    expect(partitionTargetsByZone([bears.id, courser.id], cards)).toEqual({
      pileCards: [courser],
      hasBoardTargets: true,
    })
  })

  it('keeps the pile cards in valid-target order regardless of where the board target sits', () => {
    expect(partitionTargetsByZone([courser.id, bears.id, exiled.id], cards).pileCards).toEqual([
      courser,
      exiled,
    ])
  })

  it('treats a target missing from the client card map as a board target', () => {
    expect(partitionTargetsByZone([entityId('unknown')], cards)).toEqual({
      pileCards: [],
      hasBoardTargets: true,
    })
  })

  it('falls back to the server zone hint for a card carrying no zone', () => {
    const zoneless = card('zoneless', null)
    const map = cardMap(zoneless)
    expect(partitionTargetsByZone([zoneless.id], map, ZoneType.GRAVEYARD)).toEqual({
      pileCards: [zoneless],
      hasBoardTargets: false,
    })
    expect(partitionTargetsByZone([zoneless.id], map, ZoneType.EXILE).pileCards).toEqual([zoneless])
    expect(partitionTargetsByZone([zoneless.id], map)).toEqual({
      pileCards: [],
      hasBoardTargets: true,
    })
  })

  it('reports neither route for an empty valid-target set', () => {
    expect(partitionTargetsByZone([], cards)).toEqual({ pileCards: [], hasBoardTargets: false })
  })

  it('reports a board target when the client has no card map at all', () => {
    expect(partitionTargetsByZone([courser.id], undefined)).toEqual({
      pileCards: [],
      hasBoardTargets: true,
    })
  })
})

describe('derivePileAction', () => {
  it('labels an exile effect', () => {
    expect(derivePileAction('Exile target card from a graveyard')).toEqual({
      confirmText: 'Exile',
      verb: 'exile',
    })
  })

  it('labels a reanimation effect', () => {
    expect(derivePileAction('Put target creature card from a graveyard onto the battlefield')).toEqual({
      confirmText: 'Put onto Battlefield',
      verb: 'put onto the battlefield',
    })
  })

  it('labels a shuffle-into-library effect', () => {
    expect(derivePileAction('Shuffle target card from a graveyard into its owner\'s library')).toEqual({
      confirmText: 'Shuffle into Library',
      verb: 'shuffle into your library',
    })
  })

  it('falls back to return-to-hand', () => {
    expect(derivePileAction('Return target creature card from your graveyard to your hand')).toEqual({
      confirmText: 'Return to Hand',
      verb: 'return to your hand',
    })
  })

  it('falls back to return-to-hand for a missing hint', () => {
    expect(derivePileAction(undefined).confirmText).toBe('Return to Hand')
  })

  it('reads The Spot, Living Portal as exile, not reanimation', () => {
    // ExileUntilLeavesEffect renders "…until this permanent leaves the battlefield", and The Spot
    // composes two of them (CompositeEffect joins with ". "). A bare "battlefield" test would
    // offer "Put onto Battlefield" for a card the effect exiles.
    const hint =
      'Exile up to one target nonland permanent until this permanent leaves the battlefield. ' +
      'Exile up to one target nonland permanent card from a graveyard until this permanent ' +
      'leaves the battlefield'

    expect(derivePileAction(hint)).toEqual({ confirmText: 'Exile', verb: 'exile' })
  })

  it('still reads a blink as reanimation despite the leaves-the-battlefield clause', () => {
    const hint = 'Exile target creature, then return it to the battlefield when this leaves the battlefield'

    expect(derivePileAction(hint).confirmText).toBe('Put onto Battlefield')
  })
})
