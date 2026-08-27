import { describe, expect, it, vi } from 'vitest'

// gameStore.ts reads localStorage at module-init time (autoTapEnabled, the follow-action
// preference); the plain Node vitest environment has no Storage API. Stub the minimum and
// pull the real store in afterwards, so the slice under test is the one the app runs.
vi.stubGlobal('localStorage', {
  getItem: () => null,
  setItem: () => {},
  removeItem: () => {},
})
vi.stubGlobal('window', { addEventListener: () => {}, removeEventListener: () => {} })

const { useGameStore } = await import('../../gameStore')
const { isFollowingAction } = await import('./boardViewSlice')
const { entityId } = await import('../../../types')

const ME = entityId('me')
const A = entityId('a')
const B = entityId('b')

function seatTable() {
  const gameState = {
    players: [{ playerId: ME }, { playerId: A }, { playerId: B }],
  } as unknown as NonNullable<ReturnType<typeof useGameStore.getState>['gameState']>
  useGameStore.setState({ gameState, playerId: ME, spectatingState: null })
  useGameStore.getState().resetBoardView()
  useGameStore.setState({ followAction: true })
}

describe('boardView pin / follow-the-action', () => {
  it('a pin suspends follow without flipping the persisted setting', () => {
    seatTable()
    useGameStore.getState().viewOpponent(A)
    const s = useGameStore.getState()
    expect(s.viewedOpponentId).toBe(A)
    expect(s.viewPinned).toBe(true)
    expect(s.followAction).toBe(true)
    expect(isFollowingAction(s)).toBe(false)
  })

  it('unpinning (Esc / re-click) hands the camera straight back to the follow rules', () => {
    seatTable()
    useGameStore.getState().viewOpponent(A)
    useGameStore.getState().unpinView()
    expect(isFollowingAction(useGameStore.getState())).toBe(true)
    // …and follow moves the view again.
    useGameStore.getState().followViewTo(B)
    expect(useGameStore.getState().viewedOpponentId).toBe(B)
  })

  it('a pinned camera refuses follow writes', () => {
    seatTable()
    useGameStore.getState().viewOpponent(A)
    useGameStore.getState().followViewTo(B)
    expect(useGameStore.getState().viewedOpponentId).toBe(A)
  })

  it('clicking Follow while pinned releases the pin instead of turning the setting off', () => {
    seatTable()
    useGameStore.getState().viewOpponent(A)
    useGameStore.getState().toggleFollowAction()
    const s = useGameStore.getState()
    expect(s.viewPinned).toBe(false)
    expect(s.followAction).toBe(true)
    expect(isFollowingAction(s)).toBe(true)
  })

  it('clicking Follow when unpinned toggles the setting', () => {
    seatTable()
    useGameStore.getState().toggleFollowAction()
    expect(useGameStore.getState().followAction).toBe(false)
    expect(isFollowingAction(useGameStore.getState())).toBe(false)
    useGameStore.getState().toggleFollowAction()
    expect(useGameStore.getState().followAction).toBe(true)
  })
})
