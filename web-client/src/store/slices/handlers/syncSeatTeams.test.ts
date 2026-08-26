import { describe, it, expect, vi } from 'vitest'
import { syncSeatTeams } from './gameplayHandlers'
import type { ClientGameState, ClientPlayer } from '@/types'
import { entityId } from '@/types'
import type { GetState } from './types'

/**
 * The seat → team map used to be stamped only from the `gameStarted` roster, a one-shot message a
 * reconnecting client never receives — so every hotseat, scenario and resumed connection rendered
 * a team game as a free-for-all. `syncSeatTeams` re-derives it from the state itself, which every
 * update carries. These tests pin that, and pin that a non-team game never touches the store.
 */

function player(id: string, over: Partial<ClientPlayer> = {}): ClientPlayer {
  return { playerId: entityId(id), name: id, ...over } as unknown as ClientPlayer
}

function state(...players: ClientPlayer[]): ClientGameState {
  return { players } as unknown as ClientGameState
}

/** A stand-in store: whatever the map currently is, plus a spy for the write. */
function store(
  teamByPlayerId: Record<string, number> = {},
  teamSharedLife = false,
  teamSharedTurns = teamSharedLife,
) {
  const setSeatTeams = vi.fn()
  const get = (() =>
    ({ teamByPlayerId, teamSharedLife, teamSharedTurns, setSeatTeams })) as unknown as GetState
  return { get, setSeatTeams }
}

/** A Two-Headed Giant seat: a team, one shared life total, one shared turn (CR 805 / 810). */
function twoHeaded(id: string, teamIndex: number): ClientPlayer {
  return player(id, { teamIndex, teamSharedLife: true, teamSharedTurns: true })
}

describe('syncSeatTeams', () => {
  it('stamps the map from the state alone — the reconnect path, with no roster ever seen', () => {
    const { get, setSeatTeams } = store()
    syncSeatTeams(
      state(twoHeaded('a', 0), twoHeaded('b', 0), twoHeaded('c', 1), twoHeaded('d', 1)),
      get,
    )
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 0, c: 1, d: 1 }, true, true)
  })

  it('is a no-op once the map already matches, so it never re-renders the board', () => {
    const { get, setSeatTeams } = store({ a: 0, b: 1 }, true)
    syncSeatTeams(state(twoHeaded('a', 0), twoHeaded('b', 1)), get)
    expect(setSeatTeams).not.toHaveBeenCalled()
  })

  it('never writes in a non-team game', () => {
    const { get, setSeatTeams } = store()
    syncSeatTeams(state(player('a'), player('b')), get)
    expect(setSeatTeams).not.toHaveBeenCalled()
  })

  it('sets teamIndex but not shared life for Team vs. Team (CR 808.5)', () => {
    const { get, setSeatTeams } = store()
    syncSeatTeams(
      state(player('a', { teamIndex: 0 }), player('b', { teamIndex: 1 })),
      get,
    )
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 1 }, false, false)
  })

  it('separates shared turns from shared life — Team vs. Team has neither, 2HG has both', () => {
    // The two axes are why `teamSharedTurns` exists as its own field: "has a team" and "pools life"
    // both fail to answer whether a teammate may act in your priority window (CR 805.5a).
    const { get, setSeatTeams } = store({ a: 0, b: 1 }, false, false)
    syncSeatTeams(state(twoHeaded('a', 0), twoHeaded('b', 1)), get)
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 1 }, true, true)
  })

  it('rewrites when a seat changes team, not just when the seat count does', () => {
    const { get, setSeatTeams } = store({ a: 0, b: 0 }, true)
    syncSeatTeams(state(twoHeaded('a', 0), twoHeaded('b', 1)), get)
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 1 }, true, true)
  })
})
