import { describe, it, expect } from 'vitest'
import type { ClientCard, EntityId } from '@/types'
import { entityId } from '@/types'
import { defendingPlayerOf, isBattle, tableSideOf } from './combatTargets'

const ME = entityId('me')
const OPP = entityId('opp')

function permanent(id: string, cardTypes: string[], controllerId: EntityId, protectorId?: EntityId): ClientCard {
  return { id: entityId(id), cardTypes, controllerId, protectorId } as unknown as ClientCard
}

const siege = permanent('siege', ['BATTLE'], ME, OPP)
const walker = permanent('walker', ['PLANESWALKER'], OPP)
const cards = { [siege.id]: siege, [walker.id]: walker }

describe('defendingPlayerOf', () => {
  it('is the protector for a battle, not its controller', () => {
    expect(defendingPlayerOf(siege.id, cards)).toBe(OPP)
  })

  it('is the controller for a planeswalker', () => {
    expect(defendingPlayerOf(walker.id, cards)).toBe(OPP)
  })

  it('is the player themself when the target is a player', () => {
    expect(defendingPlayerOf(OPP, cards)).toBe(OPP)
  })
})

describe('tableSideOf', () => {
  it("puts a Siege on its protector's side", () => {
    expect(tableSideOf(siege)).toBe(OPP)
  })

  it('leaves every other permanent with its controller', () => {
    expect(tableSideOf(permanent('bear', ['CREATURE'], ME))).toBe(ME)
  })

  it('falls back to the controller for a battle with no protector yet', () => {
    expect(tableSideOf(permanent('b', ['BATTLE'], ME))).toBe(ME)
  })
})

describe('isBattle', () => {
  it('reads the BATTLE card type', () => {
    expect(isBattle(siege)).toBe(true)
    expect(isBattle(walker)).toBe(false)
  })
})
