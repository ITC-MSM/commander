import { useGameStore } from '@/store/gameStore.ts'
import { QuickGameLobbyOverlay } from './QuickGameLobbyOverlay'
import { HomeScreen } from './HomeScreen'

/**
 * Connection/lobby UI - shown when not in a game.
 * Combat mode and game UI are handled in App.tsx and GameBoard.tsx.
 *
 * This file is only the router between the pre-game screens. The screens themselves live in
 * `HomeScreen.tsx`, `components/lobby/` and `components/tournament/`.
 */
export function GameUI() {
  const connectionStatus = useGameStore((state) => state.connectionStatus)
  const sessionId = useGameStore((state) => state.sessionId)
  const lastError = useGameStore((state) => state.lastError)
  const deckBuildingState = useGameStore((state) => state.deckBuildingState)
  const tournamentState = useGameStore((state) => state.tournamentState)
  const ffaState = useGameStore((state) => state.ffaState)
  const quickGameLobbyState = useGameStore((state) => state.quickGameLobbyState)

  // Don't show connection overlay if actively building deck (but show during 'waiting' phase)
  // Exception: always show if tournamentState/ffaState exists (for the standings overlays)
  if (deckBuildingState && deckBuildingState.phase !== 'waiting' && !tournamentState && !ffaState) return null

  // Quick-game lobby is its own dedicated overlay (deck picker lives inside it).
  if (quickGameLobbyState && !sessionId) return <QuickGameLobbyOverlay />

  return (
    <HomeScreen
      status={connectionStatus}
      sessionId={sessionId}
      error={lastError?.message}
    />
  )
}
