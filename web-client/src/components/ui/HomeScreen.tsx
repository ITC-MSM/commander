/**
 * Landing screen — the centred glass card you see before any game exists.
 *
 * Three labelled tiers instead of one `Quick Game | Tournament` toggle:
 *
 * - **PLAY** — the six {@link MODE_PRESETS} cards, a join-code row, and a Continue chip when a
 *   lobby is still live from a previous page load.
 * - **BUILD & BROWSE** — deckbuilder, replays and the account pages (`/stats`, `/friends`,
 *   `/profile`), which had no home-screen entry at all before.
 * - **LAB** — debugging and content tools, explicitly captioned as not part of normal play.
 *
 * The presets are declarative (`modePresets.ts`); this file only knows how to *launch* one.
 */
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
import { HelpTip } from '@/components/help/HelpTip'
import { MODE_PRESETS, type ModePreset } from './modePresets'
import { axisSummary } from '../lobby/axes'
import { loadLobbyId, clearLobbyId } from '@/store/slices/shared'
import styles from './GameUI.module.css'

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
 * Home screen shown before a game starts — and the router into the lobby / tournament overlays.
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
  const lobbyState = useGameStore((state) => state.lobbyState)
  const [joinSessionId, setJoinSessionId] = useState('')
  const [playerName, setPlayerName] = useState(() => localStorage.getItem('argentum-player-name') || '')

  const [nameConfirmed, setNameConfirmed] = useState(() => !!localStorage.getItem('argentum-player-name'))
  const [loginOpen, setLoginOpen] = useState(false)
  const [showReplays, setShowReplays] = useState(false)
  const [publicLobbies, setPublicLobbies] = useState<PublicLobbyEntry[]>([])
  const [publicLobbiesError, setPublicLobbiesError] = useState<string | null>(null)
  const [liveGames, setLiveGames] = useState<LiveGameEntry[]>([])
  // A join requested before the socket was up (name entry, or a public-lobby row clicked while
  // disconnected), replayed once we're connected. Kind-agnostic: the quick-game join handler
  // delegates to the tournament handler when the code belongs to one.
  const [pendingJoinCode, setPendingJoinCode] = useState<string | null>(null)
  // Read once at first render, before `connect` gets a chance to clear it: the lobby this browser
  // was in before the page reloaded. Surfaced as the Continue chip — a mid-lobby refresh used to
  // land you back here with no indication that a lobby was still waiting for you.
  const [resumableLobbyId, setResumableLobbyId] = useState<string | null>(() => loadLobbyId())
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
      if (joinSessionId.trim()) setPendingJoinCode(joinSessionId.trim())
      connect(playerName.trim())
    }
  }

  const handleJoin = () => {
    if (joinSessionId.trim()) {
      // Unified join: send to QuickGameLobbyHandler, which delegates to the tournament
      // handler if the code happens to be a tournament lobby. The home-screen Join field
      // doesn't care which kind of lobby is behind a code.
      joinQuickGameLobby(joinSessionId.trim())
    }
  }

  /** Open the lobby a preset describes. See {@link ModePreset.launch}. */
  const launchPreset = (preset: ModePreset) => {
    const launch = preset.launch
    if (launch.kind === 'quickGame') {
      createQuickGameLobby(launch.vsAi, undefined, false, undefined, launch.momirBasic ?? false)
    } else {
      // Sets are configured inside the lobby; ECL is only the starting selection, and is ignored
      // entirely by PREMADE_DECKS (which generates no boosters).
      createTournamentLobby(['ECL'], launch.format, 6, 8, 45, false, launch.gameMode)
    }
  }

  // Replay a join that was queued while disconnected.
  useEffect(() => {
    if (!pendingJoinCode || status !== 'connected') return
    setPendingJoinCode(null)
    joinQuickGameLobby(pendingJoinCode)
  }, [pendingJoinCode, status, joinQuickGameLobby])

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
  const signedIn = authStatus === 'authenticated'

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
      <div className={styles.cornerControls}>
        <FullscreenButton />
        <button
          type="button"
          onClick={() => navigate('/help')}
          className={styles.fullscreenButton}
          title="How Argentum works — modes, priority, shortcuts"
        >
          ? Help
        </button>
      </div>
      <div className={styles.landingLayout}>
        <div className={styles.contentBackdrop}>
          <h1 className={styles.title}>Argentum Engine</h1>
          <span className={styles.commitHash}>{__COMMIT_HASH__}</span>

          {error && (
            <p className={styles.errorMessage}>Error: {error}</p>
          )}

          {!nameConfirmed && (
            <div className={styles.inputGroup}>
              <label className={styles.inputLabel}>{joinSessionId ? 'Enter your name to join' : 'Enter your name'}</label>
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
            <div className={styles.homeTiers}>
              {/* ── PLAY ─────────────────────────────────────────────── */}
              <section className={styles.homeTier}>
                <SectionHeading label="Play" />
                <div className={styles.presetGrid}>
                  {MODE_PRESETS.map((preset) => (
                    <ModePresetCard
                      key={preset.id}
                      preset={preset}
                      disabled={preset.launch.kind === 'quickGame' && preset.launch.vsAi && !aiEnabled}
                      onSelect={() => launchPreset(preset)}
                    />
                  ))}
                </div>

                <div className={styles.joinRow}>
                  <input
                    type="text"
                    value={joinSessionId}
                    onChange={(e) => setJoinSessionId(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleJoin()}
                    placeholder="Have an invite code? Paste it here"
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

                {resumableLobbyId && (
                  <div className={styles.continueChip}>
                    <button
                      type="button"
                      className={styles.continueChipButton}
                      onClick={() => joinQuickGameLobby(resumableLobbyId)}
                    >
                      Continue → lobby <span className={styles.continueChipCode}>{resumableLobbyId}</span>
                    </button>
                    <button
                      type="button"
                      className={styles.continueChipDismiss}
                      aria-label="Dismiss"
                      title="I'm done with that lobby"
                      onClick={() => { clearLobbyId(); setResumableLobbyId(null) }}
                    >
                      ×
                    </button>
                  </div>
                )}

                <AccountBenefitsCallout onCreateAccount={() => setLoginOpen(true)} />
                <DeckMigrationPrompt />
              </section>

              {/* ── BUILD & BROWSE ───────────────────────────────────── */}
              <section className={styles.homeTier}>
                <SectionHeading label="Build & Browse" />
                <div className={styles.secondaryButtonRow}>
                  <button onClick={() => navigate('/deckbuilder')} className={styles.secondaryButton}>
                    Deckbuilder
                  </button>
                  <button onClick={() => setShowReplays(true)} className={styles.secondaryButton}>
                    Replays
                  </button>
                  {signedIn && (
                    <>
                      <button onClick={() => navigate('/stats')} className={styles.secondaryButton}>
                        Stats
                      </button>
                      <button onClick={() => navigate('/friends')} className={styles.secondaryButton}>
                        Friends
                      </button>
                      <button onClick={() => navigate('/profile')} className={styles.secondaryButton}>
                        Profile
                      </button>
                    </>
                  )}
                </div>
              </section>

              {/* ── LAB ──────────────────────────────────────────────── */}
              <section className={styles.homeTier}>
                <SectionHeading label="Lab" hint="advanced" />
                <div className={styles.secondaryButtonRow}>
                  <button onClick={() => navigate('/scenario')} className={styles.secondaryButton}>
                    Scenario Builder
                  </button>
                  <button onClick={() => navigate('/set-completion')} className={styles.secondaryButton}>
                    Set Completion
                  </button>
                  {import.meta.env.DEV && (
                    <button onClick={() => navigate('/llm-tournament')} className={styles.secondaryButton}>
                      LLM Tournament
                    </button>
                  )}
                </div>
                <p className={styles.tierCaption}>
                  Debugging and content tools, not part of normal play.
                </p>
              </section>
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
                  if (status === 'connected') {
                    // QuickGameLobbyHandler routes by lobby kind — works for both.
                    joinQuickGameLobby(entry.lobbyId)
                  } else if (playerName.trim()) {
                    localStorage.setItem('argentum-player-name', playerName.trim())
                    setPendingJoinCode(entry.lobbyId)
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

/** Rule-and-label heading separating the landing screen's tiers. */
function SectionHeading({ label, hint }: { label: string; hint?: string }) {
  return (
    <div className={styles.tierHeading}>
      <span className={styles.tierHeadingLabel}>
        {label}
        {hint && <span className={styles.tierHeadingHint}>{hint}</span>}
      </span>
      <span className={styles.tierHeadingRule} />
    </div>
  )
}

/**
 * One entry point in the PLAY tier. Every card carries the same metadata — seats, rough duration,
 * whether you need a deck, and the axis triple the lobby will open with — so the six can be
 * compared rather than guessed at.
 */
function ModePresetCard({
  preset,
  disabled,
  onSelect,
}: {
  preset: ModePreset
  disabled: boolean
  onSelect: () => void
}) {
  // A wrapper div rather than one big button: the HelpTip is itself a button, and nesting
  // interactive elements is invalid HTML (and unreachable by keyboard).
  return (
    <div className={`${styles.presetCard} ${styles[`presetCard_${preset.accent}`] ?? ''}`}>
      <span className={styles.presetCardHelp}>
        <HelpTip topicId={preset.helpTopicId} label={`What is ${preset.title}?`} size="sm" />
      </span>
      <button
        type="button"
        onClick={onSelect}
        disabled={disabled}
        data-testid={`mode-preset-${preset.id}`}
        className={styles.presetCardButton}
        {...(disabled ? { title: 'The AI player is disabled on this server' } : {})}
      >
        <span className={styles.presetCardTitle}>{preset.title}</span>
        <span className={styles.presetCardTagline}>{preset.tagline}</span>
        <span className={styles.presetCardMeta}>
          <span>{preset.players}</span>
          <span className={styles.presetCardMetaDot}>·</span>
          <span>{preset.duration}</span>
          <span className={styles.presetCardMetaDot}>·</span>
          <span>{preset.needsDeck ? 'Bring a deck' : 'No deck needed'}</span>
        </span>
        <span className={styles.presetCardAxes}>{axisSummary(preset.defaults)}</span>
      </button>
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
