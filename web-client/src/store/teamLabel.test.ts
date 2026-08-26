import { describe, it, expect } from 'vitest'
import { teamLabel } from './teamLabel'

/**
 * The center HUD's two orbs are labelled by team in a shared-life game, and they borrow the
 * opponent rail's own vocabulary so the two never disagree about what to call a team.
 */
describe('teamLabel', () => {
  it('names the viewer’s own team and the other one relative to them', () => {
    expect(teamLabel(1, 1)).toBe('Your Team')
    expect(teamLabel(0, 1)).toBe('Opponents')
  })

  it('falls back to a neutral number when the viewer has no team (spectator, replay)', () => {
    // 1-based for display: team index 0 is "Team 1".
    expect(teamLabel(0, null)).toBe('Team 1')
    expect(teamLabel(1, null)).toBe('Team 2')
  })

  it('is empty for a player with no team, so a non-team orb keeps its player name', () => {
    expect(teamLabel(null, 0)).toBe('')
    expect(teamLabel(null, null)).toBe('')
  })
})
