import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGameStore } from '@/store/gameStore.ts'
import type { TournamentFormat } from '@/types'
import { randomBackground } from '@/utils/background.ts'
import { ReplayViewer, type GameSummary } from '../admin/ReplayViewer'
import type { ReplayData } from '@/replay/reconstructSnapshots.ts'
import { labelForFormat } from '@/utils/deckLegality'
import { useAuthStore } from '@/store/authStore'
import { AuthWidget } from '@/components/auth/AuthWidget'
import { LoginModal } from '@/components/auth/LoginModal'
import { DeckMigrationPrompt } from '@/components/auth/DeckMigrationPrompt'
import { AccountBenefitsCallout } from '@/components/auth/AccountBenefitsCallout'
import { FullscreenButton } from './FullscreenButton'
import { LobbyOverlay } from '../lobby/LobbyOverlay'
import { TournamentOverlay } from '../tournament/TournamentOverlay'
import { FreeForAllOverlay } from '../tournament/FreeForAllOverlay'
import styles from './GameUI.module.css'

type GameMode = 'normal' | 'tournament'

interface PublicTournamentSummary {
  lobbyId: string
  state: string
  playerCount: number
  maxPlayers: number
  format: TournamentFormat
  setNames: string[]
  boosterCount: number
  gamesPerMatch: number
  deckFormat?: string | null
}

interface PublicQuickGameSummary {
  lobbyId: string
  playerCount: number
  maxPlayers: number
  setCode: string | null
  hostName: string | null
  format?: string | null
}

type PublicLobbyEntry =
  | ({ kind: 'tournament' } & PublicTournamentSummary)
  | ({ kind: 'quickGame' } & PublicQuickGameSummary)

interface LiveQuickGameSummary {
  gameSessionId: string
  player1Name: string
  player2Name: string
  player1Life: number
  player2Life: number
}

interface LiveTournamentMatchSummary {
  gameSessionId: string
  lobbyId: string
  round: number
  player1Name: string
  player2Name: string
  player1Life: number
  player2Life: number
}

type LiveGameEntry =
  | ({ kind: 'tournament' } & LiveTournamentMatchSummary)
  | ({ kind: 'quickGame' } & LiveQuickGameSummary)


/**
 * Connection overlay shown before game starts.
 */
export function HomeScreen({
  status,
  sessionId,
  error,
}: {
  status: string
  sessionId: string | null
  error: string | undefined
}) {
  const navigate = useNavigate()
  const connect = useGameStore((state) => state.connect)
  const aiEnabled = useGameStore((state) => state.aiEnabled)
  const createTournamentLobby = useGameStore((state) => state.createTournamentLobby)
  const createQuickGameLobby = useGameStore((state) => state.createQuickGameLobby)
  const joinQuickGameLobby = useGameStore((state) => state.joinQuickGameLobby)
  const setPendingTournamentId = useGameStore((state) => state.setPendingTournamentId)
  const lobbyState = useGameStore((state) => state.lobbyState)
  const [joinSessionId, setJoinSessionId] = useState('')
  const [gameMode, setGameMode] = useState<GameMode>('normal')
  const [playerName, setPlayerName] = useState(() => localStorage.getItem('argentum-player-name') || '')

  const [nameConfirmed, setNameConfirmed] = useState(() => !!localStorage.getItem('argentum-player-name'))
  const [loginOpen, setLoginOpen] = useState(false)
  const [showReplays, setShowReplays] = useState(false)
  const [publicLobbies, setPublicLobbies] = useState<PublicLobbyEntry[]>([])
  const [publicLobbiesError, setPublicLobbiesError] = useState<string | null>(null)
  const [liveGames, setLiveGames] = useState<LiveGameEntry[]>([])
  const onlinePlayers = useGameStore((state) => state.onlinePlayers)
  const spectateGame = useGameStore((state) => state.spectateGame)
  const setPendingSpectateGameId = useGameStore((state) => state.setPendingSpectateGameId)
  const authStatus = useAuthStore((state) => state.status)
  const accountsEnabled = useAuthStore((state) => state.accountsEnabled)
  const authInit = useAuthStore((state) => state.init)
  // Bootstrap server config + session on landing so the AuthWidget knows whether to show at all.
  useEffect(() => {
    if (authStatus === 'idle') void authInit()
  }, [authStatus, authInit])

  const confirmName = () => {
    if (playerName.trim()) {
      localStorage.setItem('argentum-player-name', playerName.trim())
      setNameConfirmed(true)
      if (gameMode === 'tournament' && joinSessionId.trim()) {
        setPendingTournamentId(joinSessionId.trim())
      }
      connect(playerName.trim())
    }
  }

  const handleCreate = () => {
    if (gameMode === 'tournament') {
      // Create lobby with default settings - host can change in lobby
      createTournamentLobby(['ECL'], 'SEALED')
    } else {
      // Quick games go through a real lobby; deck/format/set selection (including the Momir Basic
      // custom format) all live inside it.
      createQuickGameLobby(false)
    }
  }

  const handlePlayVsAi = () => {
    createQuickGameLobby(true)
  }

  const handleJoin = () => {
    if (joinSessionId.trim()) {
      // Unified join: send to QuickGameLobbyHandler, which delegates to the tournament
      // handler if the code happens to be a tournament lobby. The home-screen Join field
      // doesn't care which kind of lobby is behind a code.
      joinQuickGameLobby(joinSessionId.trim())
    }
  }

  useEffect(() => {
    if (sessionId || lobbyState) {
      setPublicLobbies([])
      setLiveGames([])
      return
    }

    let cancelled = false
    const loadPublicLobbies = async () => {
      try {
        const [tournamentsRes, quickGamesRes, liveQuickRes, liveTournRes] = await Promise.all([
          fetch('/api/tournaments/public'),
          fetch('/api/quick-games/public'),
          fetch('/api/quick-games/live'),
          fetch('/api/tournaments/live'),
        ])
        if (!tournamentsRes.ok) throw new Error(`Tournaments: ${tournamentsRes.status}`)
        if (!quickGamesRes.ok) throw new Error(`Quick games: ${quickGamesRes.status}`)
        const tournaments = await tournamentsRes.json() as PublicTournamentSummary[]
        const quickGames = await quickGamesRes.json() as PublicQuickGameSummary[]
        const liveQuick = liveQuickRes.ok ? await liveQuickRes.json() as LiveQuickGameSummary[] : []
        const liveTourn = liveTournRes.ok ? await liveTournRes.json() as LiveTournamentMatchSummary[] : []
        if (!cancelled) {
          const merged: PublicLobbyEntry[] = [
            ...quickGames.map((q) => ({ kind: 'quickGame' as const, ...q })),
            ...tournaments.map((t) => ({ kind: 'tournament' as const, ...t })),
          ]
          const live: LiveGameEntry[] = [
            ...liveQuick.map((g) => ({ kind: 'quickGame' as const, ...g })),
            ...liveTourn.map((m) => ({ kind: 'tournament' as const, ...m })),
          ]
          setPublicLobbies(merged)
          setLiveGames(live)
          setPublicLobbiesError(null)
        }
      } catch {
        if (!cancelled) {
          setPublicLobbies([])
          setLiveGames([])
          setPublicLobbiesError('Could not load public lobbies.')
        }
      }
    }

    void loadPublicLobbies()
    const interval = window.setInterval(loadPublicLobbies, 10_000)
    return () => {
      cancelled = true
      window.clearInterval(interval)
    }
  }, [sessionId, lobbyState])

  // Bootstrap the online-players count via REST so the badge appears before the
  // user has a WebSocket session. Once connected, the server pushes
  // OnlinePlayersCount on every connect/disconnect (see ConnectionHandler).
  useEffect(() => {
    if (sessionId || lobbyState || onlinePlayers !== null) return
    let cancelled = false
    fetch('/api/players/online')
      .then((res) => (res.ok ? res.json() as Promise<{ count: number }> : null))
      .then((data) => {
        if (!cancelled && data) useGameStore.setState({ onlinePlayers: data.count })
      })
      .catch(() => { /* ignore — WS push will populate */ })
    return () => { cancelled = true }
  }, [sessionId, lobbyState, onlinePlayers])

  const fetchPlayerGames = useCallback(async (): Promise<GameSummary[]> => {
    const token = localStorage.getItem('argentum-token')
    if (!token) throw new Error('No player token')
    const res = await fetch('/api/replays', {
      headers: { 'X-Player-Token': token },
    })
    if (!res.ok) throw new Error(`Server error: ${res.status}`)
    return await res.json() as GameSummary[]
  }, [])

  const fetchPlayerReplay = useCallback(async (gameId: string): Promise<ReplayData> => {
    const token = localStorage.getItem('argentum-token')
    if (!token) throw new Error('No player token')
    const res = await fetch(`/api/replays/${gameId}`, {
      headers: { 'X-Player-Token': token },
    })
    if (!res.ok) throw new Error(`Failed to load replay: ${res.status}`)
    return await res.json() as ReplayData
  }, [])

  // Show tournament UI if we're in a tournament (even without lobbyState)
  const tournamentState = useGameStore((state) => state.tournamentState)
  const ffaState = useGameStore((state) => state.ffaState)
  if (tournamentState) {
    return <TournamentOverlay tournamentState={tournamentState} />
  }

  // Show Free-for-All standings UI if the pod has started a game
  if (ffaState) {
    return <FreeForAllOverlay ffaState={ffaState} />
  }

  // Show lobby UI if we're in a lobby
  if (lobbyState) {
    return <LobbyOverlay lobbyState={lobbyState} />
  }

  // Show replay viewer overlay
  if (showReplays) {
    return (
      <ReplayViewer
        fetchGames={fetchPlayerGames}
        fetchReplay={fetchPlayerReplay}
        onBack={() => setShowReplays(false)}
      />
    )
  }

  const showPublicLobbies = !sessionId && !lobbyState && (publicLobbies.length > 0 || publicLobbiesError || (onlinePlayers ?? 0) > 0)
  const showLiveGames = !sessionId && !lobbyState && liveGames.length > 0

  const handleSpectate = (gameSessionId: string) => {
    if (status === 'connected') {
      spectateGame(gameSessionId)
      return
    }
    if (!playerName.trim()) return
    localStorage.setItem('argentum-player-name', playerName.trim())
    setPendingSpectateGameId(gameSessionId)
    setNameConfirmed(true)
    connect(playerName.trim())
  }

  return (
    <div className={styles.connectionOverlay} style={{ backgroundImage: `url(${randomBackground})` }}>
      <FullscreenButton />
      <div className={styles.landingLayout}>
        <div className={styles.contentBackdrop}>
          <h1 className={styles.title}>Argentum Engine</h1>
          <span className={styles.commitHash}>{__COMMIT_HASH__}</span>

          {error && (
            <p className={styles.errorMessage}>Error: {error}</p>
          )}

          {!nameConfirmed && (
            <div className={styles.inputGroup}>
              <label className={styles.inputLabel}>{gameMode === 'tournament' && joinSessionId ? 'Enter your name to join' : 'Enter your name'}</label>
              <input
                type="text"
                value={playerName}
                onChange={(e) => setPlayerName(e.target.value)}
                onKeyDown={(e) => { if (e.key === 'Enter') confirmName() }}
                placeholder="Your name"
                autoFocus
                maxLength={20}
                className={styles.textInput}
              />
              <button
                onClick={confirmName}
                disabled={!playerName.trim()}
                className={styles.primaryButton}
              >
                Continue
              </button>
              {accountsEnabled && authStatus !== 'authenticated' && (
                <p className={styles.accountNudge}>
                  Playing as a guest.{' '}
                  <button
                    type="button"
                    onClick={() => setLoginOpen(true)}
                    className={styles.accountNudgeButton}
                  >
                    Create a free account
                  </button>{' '}
                  — one magic link, no password — to save decks across devices, add friends, play
                  ranked, track your stats, and rewatch your games.
                </p>
              )}
            </div>
          )}

          {status === 'connected' && !sessionId && (
            <div className={styles.inputGroup}>
              {/* Game Mode Toggle */}
              <div className={styles.modeToggle}>
                <ModeButton
                  label="Quick Game"
                  active={gameMode === 'normal'}
                  onClick={() => setGameMode('normal')}
                  title="Play with a random deck"
                />
                <ModeButton
                  label="Tournament"
                  active={gameMode === 'tournament'}
                  onClick={() => setGameMode('tournament')}
                  title="Sealed or Draft with up to 8 players"
                />
              </div>

              {/* Game mode description */}
              {gameMode === 'normal' && (
                <p className={styles.modeDescription}>
                  Pick a deck inside the lobby, then play 1v1 with a friend or against the AI.
                </p>
              )}
              {gameMode === 'tournament' && (
                <p className={styles.modeDescription}>
                  Create a lobby for Sealed or Draft. Configure format and set after creating.
                </p>
              )}

              {gameMode !== 'tournament' ? (
                <div className={styles.createButtonRow}>
                  <button
                    onClick={handleCreate}
                    className={styles.primaryButton}
                  >
                    Create Quick Game
                  </button>
                  {aiEnabled && (
                    <button
                      onClick={handlePlayVsAi}
                      className={styles.aiButton}
                    >
                      Play vs AI
                    </button>
                  )}
                </div>
              ) : (
                <button
                  onClick={handleCreate}
                  className={styles.tournamentButton}
                >
                  Create Lobby
                </button>
              )}

              <div className={styles.divider}>
                <div className={styles.dividerLine} />
                <span className={styles.dividerText}>or join existing</span>
                <div className={styles.dividerLine} />
              </div>

              <div className={styles.joinRow}>
                <input
                  type="text"
                  value={joinSessionId}
                  onChange={(e) => setJoinSessionId(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleJoin()}
                  placeholder="Enter Lobby Code"
                  className={styles.sessionInput}
                />
                <button
                  onClick={handleJoin}
                  disabled={!joinSessionId.trim()}
                  className={styles.joinButton}
                >
                  Join
                </button>
              </div>

              <AccountBenefitsCallout onCreateAccount={() => setLoginOpen(true)} />
              <DeckMigrationPrompt />

              <div className={styles.secondaryButtonRow}>
                <button
                  onClick={() => navigate('/deckbuilder')}
                  className={styles.secondaryButton}
                >
                  Deckbuilder
                </button>
                <button
                  onClick={() => navigate('/scenario')}
                  className={styles.secondaryButton}
                >
                  Scenario Builder
                </button>
                <button
                  onClick={() => navigate('/set-completion')}
                  className={styles.secondaryButton}
                >
                  Set Completion
                </button>
                {import.meta.env.DEV && (
                  <button
                    onClick={() => navigate('/llm-tournament')}
                    className={styles.secondaryButton}
                  >
                    LLM Tournament
                  </button>
                )}
                <button
                  onClick={() => setShowReplays(true)}
                  className={styles.secondaryButton}
                >
                  Game Replays
                </button>
              </div>
            </div>
          )}

          {sessionId && (
            <WaitingForOpponent sessionId={sessionId} />
          )}
        </div>

        {(accountsEnabled || showPublicLobbies || showLiveGames) && (
          <div className={styles.sidePanelStack}>
            <AuthWidget />
            {showPublicLobbies && (
              <PublicLobbyList
                lobbies={publicLobbies}
                error={publicLobbiesError}
                onlinePlayers={onlinePlayers}
                onJoin={(entry) => {
                  setJoinSessionId(entry.lobbyId)
                  if (entry.kind === 'tournament') setGameMode('tournament')
                  if (status === 'connected') {
                    // QuickGameLobbyHandler routes by lobby kind — works for both.
                    joinQuickGameLobby(entry.lobbyId)
                  } else if (playerName.trim()) {
                    localStorage.setItem('argentum-player-name', playerName.trim())
                    // pendingTournamentId triggers a JoinLobby on connect — works only for tournaments.
                    if (entry.kind === 'tournament') setPendingTournamentId(entry.lobbyId)
                    setNameConfirmed(true)
                    connect(playerName.trim())
                  }
                }}
              />
            )}
            {showLiveGames && (
              <LiveGameList
                games={liveGames}
                onSpectate={handleSpectate}
                disabled={!playerName.trim() && status !== 'connected'}
              />
            )}
          </div>
        )}
      </div>
      <div className={styles.attribution}>
        <span>
          Card images via <a href="https://scryfall.com" target="_blank" rel="noopener noreferrer" className={styles.attributionLink}>Scryfall</a>
          {' · '}
          Mana symbols by <a href="https://mana.andrewgioia.com" target="_blank" rel="noopener noreferrer" className={styles.attributionLink}>Mana Font</a> (SIL OFL 1.1 / MIT)
        </span>
        <span className={styles.attributionDisclaimer}>
          Fan-made project. Not affiliated with, endorsed, or sponsored by Wizards of the Coast. Magic: The Gathering is © Wizards of the Coast LLC.
        </span>
      </div>
      <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} />
    </div>
  )
}

function PublicLobbyList({
  lobbies,
  error,
  onlinePlayers,
  onJoin,
}: {
  lobbies: PublicLobbyEntry[]
  error: string | null
  onlinePlayers: number | null
  onJoin: (entry: PublicLobbyEntry) => void
}) {
  if (lobbies.length === 0 && !error && (onlinePlayers ?? 0) === 0) return null

  return (
    <div className={styles.publicTournamentPanel}>
      <div className={styles.publicTournamentHeader}>
        <span className={styles.publicTournamentTitle}>Public Lobbies</span>
        <div className={styles.publicTournamentHeaderRight}>
          {onlinePlayers !== null && onlinePlayers > 0 && (
            <span className={styles.onlinePlayersBadge}>
              <span className={styles.onlinePlayersDot} />
              {onlinePlayers} online
            </span>
          )}
          {lobbies.length > 0 && (
            <span className={styles.publicTournamentCount}>{lobbies.length}</span>
          )}
        </div>
      </div>
      {lobbies.length === 0 && !error ? (
        <p className={styles.publicTournamentEmpty}>No public lobbies right now.</p>
      ) : error && lobbies.length === 0 ? (
        <p className={styles.publicTournamentEmpty}>{error}</p>
      ) : (
        lobbies.map((entry) => (
          <div key={`${entry.kind}-${entry.lobbyId}`} className={styles.publicTournamentRow}>
            <div className={styles.publicTournamentInfo}>
              <span className={styles.publicTournamentName}>{publicLobbyName(entry)}</span>
              <span className={styles.publicTournamentMeta}>{publicLobbyMeta(entry)}</span>
            </div>
            <button onClick={() => onJoin(entry)} className={styles.publicTournamentJoinButton}>
              Join
            </button>
          </div>
        ))
      )}
    </div>
  )
}

function LiveGameList({
  games,
  onSpectate,
  disabled,
}: {
  games: LiveGameEntry[]
  onSpectate: (gameSessionId: string) => void
  disabled: boolean
}) {
  return (
    <div className={styles.publicTournamentPanel}>
      <div className={styles.publicTournamentHeader}>
        <span className={styles.publicTournamentTitle}>Live Games</span>
        <div className={styles.publicTournamentHeaderRight}>
          <span className={styles.liveBadge}>
            <span className={styles.liveDot} />
            Live
          </span>
          <span className={styles.publicTournamentCount}>{games.length}</span>
        </div>
      </div>
      {games.map((game) => (
        <div key={`${game.kind}-${game.gameSessionId}`} className={styles.publicTournamentRow}>
          <div className={styles.publicTournamentInfo}>
            <span className={styles.publicTournamentName}>
              {game.player1Name} vs {game.player2Name}
            </span>
            <span className={styles.publicTournamentMeta}>{liveGameMeta(game)}</span>
          </div>
          <button
            onClick={() => onSpectate(game.gameSessionId)}
            disabled={disabled}
            className={styles.spectateButton}
          >
            Spectate
          </button>
        </div>
      ))}
    </div>
  )
}

function liveGameMeta(game: LiveGameEntry): string {
  const lifeSummary = `${game.player1Life} / ${game.player2Life} life`
  if (game.kind === 'tournament') {
    return `Tournament · Round ${game.round} · ${lifeSummary}`
  }
  return `Quick Game · ${lifeSummary}`
}

function publicLobbyName(entry: PublicLobbyEntry): string {
  if (entry.kind === 'tournament') {
    if (entry.format === 'PREMADE_DECKS') return 'Premade Decks Tournament'
    return entry.setNames.join(' + ') || 'Tournament'
  }
  return entry.hostName ? `${entry.hostName}'s Quick Game` : 'Quick Game'
}

function publicLobbyMeta(entry: PublicLobbyEntry): string {
  if (entry.kind === 'tournament') {
    if (entry.format === 'PREMADE_DECKS') {
      const parts = ['Premade Decks']
      if (entry.deckFormat) parts.push(labelForFormat(entry.deckFormat))
      parts.push(`${entry.playerCount}/${entry.maxPlayers} players`)
      if (entry.gamesPerMatch > 1) parts.push(`${entry.gamesPerMatch} games per matchup`)
      return parts.join(' · ')
    }
    const base = `${formatTournamentFormat(entry.format)} · ${entry.boosterCount} ${entry.format === 'DRAFT' ? 'packs' : 'boosters'} · ${entry.playerCount}/${entry.maxPlayers} players`
    return entry.gamesPerMatch > 1 ? `${base} · ${entry.gamesPerMatch} games per matchup` : base
  }
  const parts = ['Quick Game']
  if (entry.setCode) parts.push(entry.setCode)
  if (entry.format) parts.push(labelForFormat(entry.format))
  parts.push(`${entry.playerCount}/${entry.maxPlayers} players`)
  return parts.join(' · ')
}

function formatTournamentFormat(format: PublicTournamentSummary['format']): string {
  switch (format) {
    case 'WINSTON_DRAFT':
      return 'Winston Draft'
    case 'GRID_DRAFT':
      return 'Grid Draft'
    case 'DRAFT':
      return 'Draft'
    case 'COMMANDER_DRAFT':
      return 'Commander Draft'
    case 'SEALED':
      return 'Sealed'
    case 'COMMANDER_SEALED':
      return 'Commander Sealed'
    case 'PREMADE_DECKS':
      return 'Premade Decks'
  }
}

/**
 * Mode toggle button.
 */
function ModeButton({
  label,
  active,
  onClick,
  title,
}: {
  label: string
  active: boolean
  onClick: () => void
  title?: string
}) {
  return (
    <button
      onClick={onClick}
      title={title}
      className={`${styles.modeButton} ${active ? styles.modeButtonActive : ''}`}
    >
      {label}
    </button>
  )
}

/**
 * Waiting for opponent display.
 */
function WaitingForOpponent({
  sessionId,
}: {
  sessionId: string
}) {
  const cancelGame = useGameStore((state) => state.cancelGame)
  const [copied, setCopied] = useState(false)

  const copySessionId = () => {
    navigator.clipboard.writeText(sessionId)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className={styles.waitingSection}>
      <p className={styles.waitingTitle}>Game Created!</p>
      <div
        onClick={copySessionId}
        className={`${styles.inviteBox} ${copied ? styles.inviteBoxCopied : ''}`}
      >
        <div className={styles.inviteCode}>
          {sessionId}
        </div>
        <span className={`${styles.inviteCopyLabel} ${copied ? styles.inviteCopyLabelCopied : ''}`}>
          {copied ? 'Copied!' : 'Copy'}
        </span>
      </div>
      <p className={styles.waitingSubtitle}>
        Waiting for opponent to join...
      </p>
      <button onClick={cancelGame} className={styles.cancelButton}>
        Cancel Game
      </button>
    </div>
  )
}
