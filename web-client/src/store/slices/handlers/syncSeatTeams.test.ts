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
function store(teamByPlayerId: Record<string, number> = {}, teamSharedLife = false) {
  const setSeatTeams = vi.fn()
  const get = (() => ({ teamByPlayerId, teamSharedLife, setSeatTeams })) as unknown as GetState
  return { get, setSeatTeams }
}

describe('syncSeatTeams', () => {
  it('stamps the map from the state alone — the reconnect path, with no roster ever seen', () => {
    const { get, setSeatTeams } = store()
    syncSeatTeams(
      state(
        player('a', { teamIndex: 0, teamSharedLife: true }),
        player('b', { teamIndex: 0, teamSharedLife: true }),
        player('c', { teamIndex: 1, teamSharedLife: true }),
        player('d', { teamIndex: 1, teamSharedLife: true }),
      ),
      get,
    )
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 0, c: 1, d: 1 }, true)
  })

  it('is a no-op once the map already matches, so it never re-renders the board', () => {
    const { get, setSeatTeams } = store({ a: 0, b: 1 }, true)
    syncSeatTeams(
      state(
        player('a', { teamIndex: 0, teamSharedLife: true }),
        player('b', { teamIndex: 1, teamSharedLife: true }),
      ),
      get,
    )
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
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 1 }, false)
  })

  it('rewrites when a seat changes team, not just when the seat count does', () => {
    const { get, setSeatTeams } = store({ a: 0, b: 0 }, true)
    syncSeatTeams(
      state(
        player('a', { teamIndex: 0, teamSharedLife: true }),
        player('b', { teamIndex: 1, teamSharedLife: true }),
      ),
      get,
    )
    expect(setSeatTeams).toHaveBeenCalledWith({ a: 0, b: 1 }, true)
  })
})
