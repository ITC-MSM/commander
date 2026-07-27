import { describe, it, expect } from 'vitest'
import { applyStateDelta } from './deltaApplicator'
import type { ClientDeckCard, ClientGameState, StateDelta } from '@/types'
import { entityId } from '@/types'

/**
 * `applyStateDelta` rebuilds the whole state object rather than patching it, so any field the
 * server omits from a delta has to be carried forward explicitly — forgetting one silently blanks
 * that part of the UI on the very next update. These cover the two "sent only when changed" fields
 * (`deck`, `activeYields`), which are exactly the ones a rebuild is prone to dropping.
 */

const bolt: ClientDeckCard = {
  cardName: 'Lightning Bolt',
  copies: 4,
  remaining: 3,
  cmc: 1,
  cardTypes: ['INSTANT'],
  colors: ['RED'],
  imageUri: null,
}

function baseState(over: Partial<ClientGameState> = {}): ClientGameState {
  return {
    viewingPlayerId: entityId('player-1'),
    cards: {},
    zones: [],
    players: [],
    currentPhase: 'MAIN_1' as ClientGameState['currentPhase'],
    currentStep: 'MAIN' as ClientGameState['currentStep'],
    activePlayerId: entityId('player-1'),
    priorityPlayerId: entityId('player-1'),
    turnNumber: 1,
    isGameOver: false,
    winnerId: null,
    combat: null,
    ...over,
  }
}

const emptyDelta: StateDelta = { players: [] }

describe('applyStateDelta', () => {
  it('keeps the deck tracker when the delta omits it (nothing was drawn)', () => {
    const next = applyStateDelta(baseState({ deck: [bolt] }), emptyDelta)
    expect(next.deck).toEqual([bolt])
  })

  it('replaces the deck tracker when the delta carries a new one', () => {
    const drawn = { ...bolt, remaining: 2 }
    const next = applyStateDelta(baseState({ deck: [bolt] }), { ...emptyDelta, deck: [drawn] })
    expect(next.deck).toEqual([drawn])
  })

  it('keeps active yields, which the server never puts in a delta', () => {
    const yields = [{ cardDefinitionId: 'Soul Warden#ALA-25', abilityId: 'ability_42', displayName: 'Soul Warden' }]
    const next = applyStateDelta(baseState({ activeYields: yields }), emptyDelta)
    expect(next.activeYields).toEqual(yields)
  })
})
