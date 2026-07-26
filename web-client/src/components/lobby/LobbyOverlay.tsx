import { useState, useCallback } from 'react'
import { useGameStore, type LobbyState } from '@/store/gameStore.ts'
import { teamColor } from '@/styles/seatColors'
import { SetIcon } from '../ui/SetIcon'
import { randomBackground } from '@/utils/background.ts'
import { DeckPicker } from '../ui/DeckPicker'
import { BanListEditor } from '../ui/BanListEditor'
import { SetPickerModal } from '../ui/SetPickerModal'
import { JoinQrModal } from '../ui/JoinQrModal'
import { FullscreenButton } from '../ui/FullscreenButton'
import { SettingsLabel } from '../ui/SettingsLabel'
import { buildJoinUrl } from '@/utils/joinLink'
import { labelForFormat } from '@/utils/deckLegality'
import { TournamentOverlay } from '../tournament/TournamentOverlay'
import { FreeForAllOverlay } from '../tournament/FreeForAllOverlay'
import { LobbyAxisSummary } from './LobbyAxisSummary'
import {
  axesFromLobbySettings,
  cardsTopicId,
  eventLabel,
  eventTopicId,
  eventUnavailableReason,
  gameModeForTable,
  tableLabel,
  tableTopicId,
  LEGALITY_OPTIONS,
  type EventAxis,
  type TableAxis,
} from './axes'
import styles from '../ui/GameUI.module.css'

/**
 * Sentinel set code for a deferred "Random Set" pick in a tournament lobby. The concrete set stays
 * hidden (shown as "Random Set") until the server rolls it at game start — mirrors the server's
 * TournamentLobby.RANDOM_SET_CODE. Multiple random slots use suffixed codes (RANDOM, RANDOM-2, …).
 */
const RANDOM_SET_CODE = 'RANDOM'
const isRandomSetCode = (code: string): boolean =>
  code === RANDOM_SET_CODE || code.startsWith(`${RANDOM_SET_CODE}-`)

/**
 * The Table axis as one flat row: seat cap per shape, plus why it is out of reach when the lobby
 * already holds too many players. Upper bounds only — a shape that also needs an exact or even
 * count (2HG wants 4; Team vs. Team wants an even 4/6/8) stays selectable and is caught by the
 * start button, so the host can pick the shape first and then fill the seats.
 */
const TABLE_CHOICES: ReadonlyArray<{
  table: TableAxis
  maxPlayers: number
  unavailable: (count: number) => string
}> = [
  { table: 'ONE_V_ONE', maxPlayers: Infinity, unavailable: () => '' },
  { table: 'FREE_FOR_ALL', maxPlayers: 6, unavailable: (n) => `Free-for-All seats at most 6 — this lobby has ${n}` },
  { table: 'TWO_HEADED_GIANT', maxPlayers: 4, unavailable: (n) => `Two-Headed Giant is exactly 4 players — this lobby has ${n}` },
  { table: 'TEAM_VS_TEAM', maxPlayers: 8, unavailable: (n) => `Team vs. Team seats at most 8 — this lobby has ${n}` },
]

const TABLE_CAPTIONS: Record<TableAxis, string> = {
  ONE_V_ONE: 'Two players per game. Everyone plays everyone; most match wins takes it.',
  FREE_FOR_ALL: 'One game, everyone at the same table (2-6 players). Last player standing wins.',
  TWO_HEADED_GIANT:
    'Four players in two teams of two. Each team shares one 30-life total, takes turns together, and attacks and blocks as one. Last team standing wins.',
  TEAM_VS_TEAM:
    'An even pod (4/6/8) split into two teams — 2v2, 3v3, or 4v4. Each player keeps their own 20 life and their own turn; players are knocked out one at a time. The last team with anyone standing wins.',
}

/**
 * Lobby overlay for sealed lobbies.
 */
export function LobbyOverlay({
  lobbyState,
}: {
  lobbyState: LobbyState
}) {
  const startLobby = useGameStore((state) => state.startLobby)
  const leaveLobby = useGameStore((state) => state.leaveLobby)
  const addAiToLobby = useGameStore((state) => state.addAiToLobby)
  const removeAiFromLobby = useGameStore((state) => state.removeAiFromLobby)
  const aiEnabled = useGameStore((state) => state.aiEnabled)
  const updateLobbySettings = useGameStore((state) => state.updateLobbySettings)
  const tournamentState = useGameStore((state) => state.tournamentState)
  const ffaState = useGameStore((state) => state.ffaState)
  const [copied, setCopied] = useState(false)
  const [showSetPicker, setShowSetPicker] = useState(false)

  // Show tournament standings when tournament is active
  if (tournamentState) {
    return <TournamentOverlay tournamentState={tournamentState} />
  }

  // Show Free-for-All standings once the pod has started a game
  if (ffaState) {
    return <FreeForAllOverlay ffaState={ffaState} />
  }

  const isWaiting = lobbyState.state === 'WAITING_FOR_PLAYERS'
  const format = lobbyState.settings.format
  const isDraft = format === 'DRAFT'
  const isWinston = format === 'WINSTON_DRAFT'
  const isGridDraft = format === 'GRID_DRAFT'
  const isSealed = format === 'SEALED'
  const isCommanderDraft = format === 'COMMANDER_DRAFT'
  const isCommanderSealed = format === 'COMMANDER_SEALED'
  const isAnyCommander = isCommanderDraft || isCommanderSealed
  const isPremade = format === 'PREMADE_DECKS'
  const isFfa = lobbyState.settings.gameMode === 'FREE_FOR_ALL'
  // Two-Headed Giant (CR 810): the pod mode that runs two teams of two off the same draft/sealed
  // build. Exactly four players; combat/attack rules are fixed (no per-creature attack picker).
  const is2hg = lobbyState.settings.gameMode === 'TWO_HEADED_GIANT'
  // Team vs. Team (CR 808): two even teams (2v2 / 3v3 / 4v4). Like 2HG but nothing is shared —
  // each player keeps their own life and turn, and is eliminated individually.
  const isTeamVsTeam = lobbyState.settings.gameMode === 'TEAM_VS_TEAM'
  // Any team mode shares the random/manual team-assignment controls below.
  const isTeamGame = is2hg || isTeamVsTeam
  // Anything that isn't a 1v1 bracket puts everyone at one table for a single shared game.
  const isMultiplayer = isFfa || isTeamGame
  // The lobby's point in the three-axis space (Cards / Table / Event) — the vocabulary the header
  // chips and the settings rows below both speak. Derived, never stored: the server still only
  // knows `format` + `gameMode`, and `axes.ts` owns that translation.
  const axes = axesFromLobbySettings(lobbyState.settings)
  // Both team modes split the pod into exactly two even teams; team size follows the player count.
  const teamSize = Math.max(1, Math.floor(lobbyState.players.length / 2))
  // Team setup: random by default, or host-assigned (playerId -> team, defaulting to join order).
  const randomTeams = lobbyState.settings.randomTeams ?? true
  const teamAssignments = lobbyState.settings.teamAssignments ?? {}
  const playerTeam = (playerId: string, index: number): number =>
    teamAssignments[playerId] ?? Math.floor(index / teamSize)
  // Manual teams must be an even split into two equal teams (the server otherwise re-balances).
  const manualTeamsBalanced = lobbyState.players.length >= 4 && lobbyState.players.length % 2 === 0 &&
    [0, 1].every(t => lobbyState.players.filter((p, i) => playerTeam(p.playerId, i) === t).length === teamSize)
  // Move one player to the other team, sending the full explicit assignment for every seat.
  const togglePlayerTeam = (playerId: string, index: number) => {
    const flipped = playerTeam(playerId, index) === 0 ? 1 : 0
    const next: Record<string, number> = {}
    lobbyState.players.forEach((p, i) => {
      next[p.playerId] = p.playerId === playerId ? flipped : playerTeam(p.playerId, i)
    })
    updateLobbySettings({ teamAssignments: next })
  }
  // "Draft-shape" — anything that hands packs around at pick time. Commander Draft fits the
  // shape (same per-pick UI / timer / pack-passing) so it inherits Draft-only settings.
  const isAnyDraft = isDraft || isWinston || isGridDraft || isCommanderDraft
  const isAnySealed = isSealed || isCommanderSealed
  const hasSelectedSets = lobbyState.settings.setCodes.length > 0
  // Extension sets (bonus sheets like The Big Score) can't carry a pool alone — the selection
  // needs at least one regular set. Unknown codes count as regular; the server re-validates.
  const hasBaseSet = lobbyState.settings.setCodes.some(
    (code) => !lobbyState.settings.availableSets.find((s) => s.code === code)?.extensionSet,
  )
  const playerCount = lobbyState.players.length
  const canSwitchToNormalDraft = playerCount <= 8
  const canSwitchToWinston = playerCount <= 2
  const canSwitchToGrid = playerCount <= 4
  // Commander Draft/Sealed are 1v1 for the foreseeable future (multiplayer commander is a
  // separate project — see backlog/commander-format.md Phase 3).
  const canSwitchToCommander = playerCount <= 2
  const playerCheck = isWinston ? playerCount === 2
    : isGridDraft ? playerCount >= 2 && playerCount <= 4
    : isAnyCommander ? playerCount === 2
    : is2hg ? playerCount === 4
    : isTeamVsTeam ? playerCount >= 4 && playerCount % 2 === 0
    : playerCount >= 2
  // Premade format: no boosters generated, so set selection is optional. We do require every
  // connected player to have submitted a deck before the host can start.
  const allConnectedDecksSubmitted = lobbyState.players
    .filter((p) => p.isConnected)
    .every((p) => p.deckSubmitted)
  const canStart = isPremade
    ? playerCheck && allConnectedDecksSubmitted
    : playerCheck && hasSelectedSets && hasBaseSet

  const copyLobbyId = () => {
    navigator.clipboard.writeText(lobbyState.lobbyId)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  // Unified set picker: every set (complete + incomplete) lives behind one searchable modal
  // (`SetPickerModal`, shared with the Quick Game lobby). The lobby itself only shows the
  // *selected* sets as compact chips, so its footprint stays small and stable no matter how many
  // sets exist in total or how many a host picks.
  const allSets = lobbyState.settings.availableSets
  // A selected-set chip is either a concrete set or a deferred "Random Set" placeholder that stays
  // hidden until the server reveals it at game start (see addRandomSet / RANDOM_SET_CODE).
  type SelectedSetChip = { code: string; name: string; partial: boolean; extensionSet: boolean; random: boolean }
  const selectedSets: SelectedSetChip[] = lobbyState.settings.setCodes
    .map((code): SelectedSetChip | null => {
      if (isRandomSetCode(code)) return { code, name: 'Random Set', partial: false, extensionSet: false, random: true }
      const s = allSets.find((x) => x.code === code)
      return s ? { code, name: s.name, partial: s.partial ?? false, extensionSet: s.extensionSet ?? false, random: false } : null
    })
    .filter((s): s is SelectedSetChip => s != null)

  const toggleSet = (code: string) => {
    const isSelected = lobbyState.settings.setCodes.includes(code)
    const newCodes = isSelected
      ? lobbyState.settings.setCodes.filter((c) => c !== code)
      : [...lobbyState.settings.setCodes, code]
    updateLobbySettings({ setCodes: newCodes })
  }

  // "Random Set" in the picker: add a deferred random slot. Unlike a concrete set it stays hidden
  // (shown as "Random Set") until the server rolls a complete, non-extension set at game start.
  // Suffixed codes keep multiple random slots distinct (RANDOM, RANDOM-2, …).
  const addRandomSet = () => {
    const existing = lobbyState.settings.setCodes.filter(isRandomSetCode).length
    const code = existing === 0 ? RANDOM_SET_CODE : `${RANDOM_SET_CODE}-${existing + 1}`
    updateLobbySettings({ setCodes: [...lobbyState.settings.setCodes, code] })
  }

  return (
    <div className={styles.lobbyOverlay} style={{ backgroundImage: `url(${randomBackground})` }}>
      <div className={styles.cornerControls}><FullscreenButton /></div>
      <div className={styles.lobbyContent}>
        {/* Header */}
        <div className={styles.lobbyHeader}>
          <h1 className={styles.lobbyTitle}>
            {isPremade
              ? (is2hg ? 'Premade Decks Two-Headed Giant' : isTeamVsTeam ? 'Premade Decks Team vs. Team' : isFfa ? 'Premade Decks Free-for-All' : 'Premade Decks Tournament')
              : (lobbyState.settings.setNames.join(' + ') || 'Lobby')}
          </h1>
          <p className={styles.lobbySubtitle}>
            {(() => {
              const distText = lobbyState.settings.setCodes.length > 1 && Object.keys(lobbyState.settings.boosterDistribution).length > 0
                ? Object.entries(lobbyState.settings.boosterDistribution).map(([code, count]) => {
                  const idx = lobbyState.settings.setCodes.indexOf(code)
                  const name = idx >= 0 ? (lobbyState.settings.setNames[idx] ?? code) : code
                  return `${count} ${name}`
                }).join(' + ')
                : null
              const presetLabel = lobbyState.settings.commanderPreset === 'COMMANDER' ? 'Commander 30 life' : 'Brawl 25 life'
              if (isGridDraft) return `Grid Draft · ${lobbyState.settings.boosterCount} boosters · ${lobbyState.settings.pickTimeSeconds}s per pick`
              if (isWinston) return `Winston Draft · ${distText ?? `${lobbyState.settings.boosterCount} boosters`} · ${lobbyState.settings.pickTimeSeconds}s per turn`
              if (isCommanderDraft) return `${distText ?? `${lobbyState.settings.boosterCount} packs`} · ${lobbyState.settings.pickTimeSeconds}s per pick${lobbyState.settings.picksPerRound === 2 ? ' · Pick 2' : ''} · ${presetLabel}`
              if (isCommanderSealed) return `${distText ?? `${lobbyState.settings.boosterCount} packs`} · ${presetLabel}`
              if (isDraft) return `${distText ?? `${lobbyState.settings.boosterCount} packs`} · ${lobbyState.settings.pickTimeSeconds}s per pick${lobbyState.settings.picksPerRound === 2 ? ' · Pick 2' : ''}`
              if (isPremade) return 'Premade Decks · bring your own ≥40-card deck'
              return distText ?? `${lobbyState.settings.boosterCount} boosters per player`
            })()}
            {!isFfa && !isTeamGame && (lobbyState.settings.gamesPerMatch ?? 1) > 1 && ` · ${lobbyState.settings.gamesPerMatch} games per matchup`}
          </p>
          <LobbyAxisSummary axes={axes} />
        </div>

        {/* Invite code + scannable QR to pull another device straight into the lobby */}
        <div style={{ alignSelf: 'stretch', display: 'flex', alignItems: 'stretch', gap: 8 }}>
          <div
            onClick={copyLobbyId}
            className={`${styles.inviteBox} ${copied ? styles.inviteBoxCopied : ''}`}
            style={{ flex: 1, marginBottom: 0, justifyContent: 'space-between' }}
          >
            <div>
              <div style={{ color: 'var(--text-disabled)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 3 }}>
                Invite Code
              </div>
              <div className={styles.inviteCode} data-testid="invite-code">
                {lobbyState.lobbyId}
              </div>
            </div>
            <span className={`${styles.inviteCopyLabel} ${copied ? styles.inviteCopyLabelCopied : ''}`} style={{ flexShrink: 0, marginLeft: 12 }}>
              {copied ? 'Copied!' : 'Copy'}
            </span>
          </div>
          <JoinQrModal url={buildJoinUrl(lobbyState.lobbyId)} />
        </div>

        {/* Settings (host only) */}
        {isWaiting && lobbyState.isHost && (
          <div className={styles.settingsPanel}>
            {/* ── Cards: where the deck comes from. Its sub-options (shape, deck legality) are the
                indented rows immediately below; they never hang off a different axis. ── */}
            <div className={styles.settingsRow}>
              <SettingsLabel topicId={cardsTopicId(axes.cards)}>Cards</SettingsLabel>
              <div className={styles.settingsButtons}>
                <button
                  onClick={() => updateLobbySettings({ format: 'PREMADE_DECKS' })}
                  className={`${styles.settingsButton} ${isPremade ? styles.settingsButtonActive : ''}`}
                  title="Everyone plays a deck they already built (saved or pasted)"
                >
                  Bring a deck
                </button>
                <button
                  onClick={() => { if (!isAnySealed) updateLobbySettings({ format: 'SEALED' }) }}
                  className={`${styles.settingsButton} ${isAnySealed ? styles.settingsButtonActive : ''}`}
                  title="Open boosters and build from what you get"
                >
                  Sealed
                </button>
                <button
                  onClick={() => { if (!isAnyDraft) updateLobbySettings({ format: 'DRAFT' }) }}
                  className={`${styles.settingsButton} ${isAnyDraft ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                  title="Pass packs around and pick one card at a time"
                >
                  Draft
                </button>
              </div>
            </div>
            {/* Cards → Bring a deck: which constructed format submitted decks must be legal in. */}
            {isPremade && (
              <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
                <span className={styles.settingsLabel}>Deck legality</span>
                <select
                  value={lobbyState.settings.deckFormat ?? ''}
                  onChange={(e) =>
                    updateLobbySettings({ deckFormat: (e.target.value || '') as never })
                  }
                  className={styles.settingsSelect}
                  title="Restrict submitted decks to a constructed format. No restriction = anything the engine implements."
                >
                  <option value="">No restriction</option>
                  {LEGALITY_OPTIONS.map((f) => (
                    <option key={f.value} value={f.value}>{f.label}</option>
                  ))}
                </select>
              </div>
            )}
            {/* Cards → Sealed: which sealed shape. */}
            {isAnySealed && (
              <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
                <span className={styles.settingsLabel}>Sealed shape</span>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ format: 'SEALED' })}
                      className={`${styles.settingsButton} ${isSealed ? styles.settingsButtonActive : ''}`}
                    >
                      Standard
                    </button>
                    <button
                      onClick={() => canSwitchToCommander && updateLobbySettings({ format: 'COMMANDER_SEALED' })}
                      disabled={!canSwitchToCommander}
                      className={`${styles.settingsButton} ${isCommanderSealed ? styles.settingsButtonActive : ''}`}
                      title={canSwitchToCommander ? '' : 'Commander Sealed is 1v1 — too many players in this lobby'}
                    >
                      Commander
                    </button>
                  </div>
                  <div className={styles.variantCaption}>
                    {isCommanderSealed
                      ? 'Open Commander-shaped packs and build a 60-card deck around a commander from your pool. 1v1.'
                      : 'Open 6 boosters and build a 40-card deck.'}
                  </div>
                </div>
              </div>
            )}
            {/* Cards → Draft: which of the four draft shapes. */}
            {isAnyDraft && (
              <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
                <span className={styles.settingsLabel}>Draft shape</span>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => canSwitchToNormalDraft && updateLobbySettings({ format: 'DRAFT' })}
                      disabled={!canSwitchToNormalDraft}
                      className={`${styles.settingsButton} ${isDraft ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                      title={canSwitchToNormalDraft ? '' : 'Booster Draft seats at most 8 players'}
                    >
                      Booster
                    </button>
                    <button
                      onClick={() => canSwitchToWinston && updateLobbySettings({ format: 'WINSTON_DRAFT' })}
                      disabled={!canSwitchToWinston}
                      className={`${styles.settingsButton} ${isWinston ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                      title={canSwitchToWinston ? '' : 'Winston Draft is exactly 2 players'}
                    >
                      Winston
                    </button>
                    <button
                      onClick={() => canSwitchToGrid && updateLobbySettings({ format: 'GRID_DRAFT' })}
                      disabled={!canSwitchToGrid}
                      className={`${styles.settingsButton} ${isGridDraft ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                      title={canSwitchToGrid ? '' : 'Grid Draft seats at most 4 players'}
                    >
                      Grid
                    </button>
                    <button
                      onClick={() => canSwitchToCommander && updateLobbySettings({ format: 'COMMANDER_DRAFT' })}
                      disabled={!canSwitchToCommander}
                      className={`${styles.settingsButton} ${isCommanderDraft ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                      title={canSwitchToCommander ? '' : 'Commander Draft is 1v1 — too many players in this lobby'}
                    >
                      Commander
                    </button>
                  </div>
                  <div className={styles.variantCaption}>
                    {isCommanderDraft
                      ? 'Commander-shaped 20-card packs; pick a commander from your pool. 1v1.'
                      : isWinston ? 'Pick from 3 face-down piles. 2 players.'
                      : isGridDraft ? 'Pick a row or column from a 3×3 grid. 2-4 players.'
                      : 'Pass packs around the table. 3-8 players.'}
                  </div>
                </div>
              </div>
            )}
            {/* ── Table: who is at it. One flat row of four — the old Mode (Tournament|Multiplayer)
                + Variant (FFA|2HG|Team) pair made 1v1 look like a peer of "multiplayer" rather than
                of the three table shapes, and hid the shapes behind a click. ── */}
            <div className={styles.settingsRow}>
              <SettingsLabel topicId={tableTopicId(axes.table)}>Table</SettingsLabel>
              <div className={styles.variantGroup}>
                <div className={styles.settingsButtons}>
                  {TABLE_CHOICES.map(({ table, maxPlayers, unavailable }) => {
                    const blocked = playerCount > maxPlayers
                    return (
                      <button
                        key={table}
                        onClick={() => { if (!blocked) updateLobbySettings({ gameMode: gameModeForTable(table) }) }}
                        disabled={blocked}
                        className={`${styles.settingsButton} ${axes.table === table ? styles.settingsButtonActive : ''}`}
                        title={blocked ? unavailable(playerCount) : ''}
                      >
                        {tableLabel(table)}
                      </button>
                    )
                  })}
                </div>
                <div className={styles.variantCaption}>{TABLE_CAPTIONS[axes.table]}</div>
              </div>
            </div>
            {/* ── Event: one game, or a series. Derived from Table today — see
                `eventUnavailableReason`. Shown as its own axis anyway, with the unreachable value
                disabled and explained, because that is the hole Phase 5 fills. ── */}
            <div className={styles.settingsRow}>
              <SettingsLabel topicId={eventTopicId(axes.event)}>Event</SettingsLabel>
              <div className={styles.variantGroup}>
                <div className={styles.settingsButtons}>
                  {(['SINGLE_GAME', 'ROUND_ROBIN'] as const).map((event: EventAxis) => {
                    const reason = eventUnavailableReason(axes.table, event)
                    return (
                      <button
                        key={event}
                        disabled={reason !== null}
                        className={`${styles.settingsButton} ${axes.event === event ? styles.settingsButtonActive : ''}`}
                        title={reason ?? ''}
                      >
                        {eventLabel(event)}
                      </button>
                    )
                  })}
                </div>
                <div className={styles.variantCaption}>
                  {eventUnavailableReason(axes.table, axes.event === 'ROUND_ROBIN' ? 'SINGLE_GAME' : 'ROUND_ROBIN')}
                </div>
              </div>
            </div>
            {/* Ranked toggle — 1v1 brackets only. A ranked bracket adjusts each player's ELO per
                match and only counts if everyone is signed in. */}
            {!isMultiplayer && (
              <div className={styles.settingsRow}>
                <SettingsLabel topicId="ranked">Ranked</SettingsLabel>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ ranked: false })}
                      className={`${styles.settingsButton} ${!lobbyState.settings.ranked ? styles.settingsButtonActive : ''}`}
                      title="Casual — no rating change"
                    >
                      Casual
                    </button>
                    <button
                      onClick={() => updateLobbySettings({ ranked: true })}
                      className={`${styles.settingsButton} ${lobbyState.settings.ranked ? styles.settingsButtonActive : ''}`}
                      title="Ranked — adjusts each player's ELO"
                    >
                      Ranked
                    </button>
                  </div>
                  {lobbyState.settings.ranked && (
                    <div className={styles.variantCaption}>
                      Ranked matches adjust each player's ELO. All players must be signed in for the game
                      to count as ranked — otherwise it just plays unranked. Uncheck to play casually.
                    </div>
                  )}
                </div>
              </div>
            )}
            {/* Team setup (2HG — CR 810; Team vs. Team — CR 808): random teams each game, or host-picked teams. */}
            {isTeamGame && (
              <div className={styles.settingsRow}>
                <SettingsLabel topicId="table-two-headed-giant">Teams</SettingsLabel>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ randomTeams: true })}
                      className={`${styles.settingsButton} ${randomTeams ? styles.settingsButtonActive : ''}`}
                      title="Shuffle the players into two even teams when the game starts (re-rolled each game)"
                    >
                      Random
                    </button>
                    <button
                      onClick={() => updateLobbySettings({ randomTeams: false })}
                      className={`${styles.settingsButton} ${!randomTeams ? styles.settingsButtonActive : ''}`}
                      title="Set the teams by hand — click each player's team chip below"
                    >
                      Choose teams
                    </button>
                  </div>
                  <div className={styles.variantCaption}>
                    {randomTeams
                      ? 'Teams are randomised at game start, fresh every game.'
                      : manualTeamsBalanced
                        ? 'Click a player’s team chip below to move them between teams.'
                        : `Click each player’s team chip below — each team needs exactly ${teamSize} player${teamSize === 1 ? '' : 's'}.`}
                  </div>
                </div>
              </div>
            )}
            {/* Free-for-All attack rule (CR 802/803) — only relevant once 3+ players share one table */}
            {isFfa && (
              <div className={styles.settingsRow}>
                <SettingsLabel topicId="table-free-for-all">Attack</SettingsLabel>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    {([
                      ['MULTIPLE', 'Any opponent', 'Each creature may attack any opponent (CR 802)'],
                      ['LEFT', 'Left only', 'Each creature may attack only the player to your left (CR 803)'],
                      ['RIGHT', 'Right only', 'Each creature may attack only the player to your right (CR 803)'],
                    ] as const).map(([mode, label, title]) => (
                      <button
                        key={mode}
                        onClick={() => updateLobbySettings({ attackMode: mode })}
                        className={`${styles.settingsButton} ${(lobbyState.settings.attackMode ?? 'MULTIPLE') === mode ? styles.settingsButtonActive : ''}`}
                        title={title}
                      >
                        {label}
                      </button>
                    ))}
                  </div>
                  <div className={styles.variantCaption}>
                    Who each creature may attack. "Left"/"right" follow the seating order.
                  </div>
                </div>
              </div>
            )}
            {/* Set selection — selected sets shown as chips; the full searchable browser is a modal.
                Skipped for Premade Decks since no boosters are generated. */}
            {!isPremade && (
            <div className={styles.settingsRow} style={{ alignItems: 'flex-start' }}>
              <span className={styles.settingsLabel} style={{ paddingTop: 7 }}>Sets</span>
              <div className={styles.setSelection}>
                {selectedSets.length > 0 ? (
                  <div className={styles.setChips}>
                    {selectedSets.map((set) => (
                      <span
                        key={set.code}
                        className={`${styles.setChip} ${isAnyDraft ? styles.setChipDraft : ''} ${set.partial ? styles.setChipPartial : ''}`}
                        title={set.random
                          ? 'Random Set — revealed when the game starts'
                          : set.partial
                            ? `${set.name} — partial (reduced card pool)`
                            : set.extensionSet
                              ? `${set.name} — extension set (needs a regular set alongside)`
                              : set.name}
                      >
                        {set.random
                          ? <span className={styles.setChipIcon} aria-hidden>🎲</span>
                          : <SetIcon code={set.code} className={styles.setChipIcon} />}
                        <span className={styles.setChipName}>{set.name}</span>
                        <button
                          type="button"
                          className={styles.setChipRemove}
                          aria-label={`Remove ${set.name}`}
                          onClick={() => toggleSet(set.code)}
                        >×</button>
                      </span>
                    ))}
                  </div>
                ) : (
                  <span className={styles.setSelectionEmpty}>No sets selected yet</span>
                )}
                {hasSelectedSets && !hasBaseSet && (
                  <span className={styles.setSelectionEmpty}>
                    Extension sets need a regular set alongside them.
                  </span>
                )}
                <button
                  type="button"
                  onClick={() => setShowSetPicker(true)}
                  className={styles.addSetsButton}
                >
                  + Add sets
                </button>
              </div>
            </div>
            )}
            {/* Chaos boosters toggle — only meaningful with >1 set selected and a booster-based format. */}
            {!isPremade && !isGridDraft && lobbyState.settings.setCodes.length > 1 && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>Booster mix</span>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ chaosBoosters: false })}
                      className={`${styles.settingsButton} ${!lobbyState.settings.chaosBoosters ? styles.settingsButtonActive : ''}`}
                    >
                      Per set
                    </button>
                    <button
                      onClick={() => updateLobbySettings({ chaosBoosters: true })}
                      className={`${styles.settingsButton} ${lobbyState.settings.chaosBoosters ? styles.settingsButtonActive : ''}`}
                    >
                      Chaos
                    </button>
                  </div>
                  <div className={styles.variantCaption}>
                    {lobbyState.settings.chaosBoosters
                      ? 'Each booster mixes cards from all selected sets.'
                      : 'Each booster contains cards from a single set.'}
                  </div>
                </div>
              </div>
            )}
            {/* Booster ban list — host excludes named cards from generated boosters. Not for Premade. */}
            {!isPremade && (
              <BanListEditor
                setCodes={lobbyState.settings.setCodes}
                bannedCardNames={lobbyState.settings.bannedCardNames ?? []}
                onChange={(names) => updateLobbySettings({ bannedCardNames: names })}
              />
            )}
            {/* Boosters setting - for Sealed and Winston (Grid uses fixed counts) */}
            {(isSealed || isWinston || isCommanderSealed) && lobbyState.settings.setCodes.length > 1 && !lobbyState.settings.chaosBoosters && (
              <div className={styles.settingsRow} style={{ flexDirection: 'column', alignItems: 'stretch', gap: 8 }}>
                <span className={styles.settingsLabel}>{isWinston ? 'Boosters (total)' : 'Boosters per player'}</span>
                <div className={styles.boosterDistribution}>
                  {lobbyState.settings.setCodes.map((code) => {
                    const setName = lobbyState.settings.setNames[lobbyState.settings.setCodes.indexOf(code)] ?? code
                    const dist = lobbyState.settings.boosterDistribution
                    const count = dist[code] ?? 0
                    const total = Object.values(dist).reduce((a, b) => a + b, 0)
                    return (
                      <div key={code} className={styles.boosterDistributionRow}>
                        <span className={styles.boosterDistributionSetName}>{setName}</span>
                        <div className={styles.boosterDistributionControls}>
                          <button
                            className={styles.boosterDistributionBtn}
                            disabled={count <= 0}
                            onClick={() => {
                              const newDist = { ...dist, [code]: count - 1 }
                              updateLobbySettings({ boosterDistribution: newDist, boosterCount: total - 1 })
                            }}
                          >-</button>
                          <span className={styles.boosterDistributionCount}>{count}</span>
                          <button
                            className={styles.boosterDistributionBtn}
                            disabled={total >= 16}
                            onClick={() => {
                              const newDist = { ...dist, [code]: count + 1 }
                              updateLobbySettings({ boosterDistribution: newDist, boosterCount: total + 1 })
                            }}
                          >+</button>
                        </div>
                      </div>
                    )
                  })}
                  <div className={styles.boosterDistributionTotal}>
                    <span style={{ flex: 1 }}>Total</span>
                    <span className={styles.boosterDistributionTotalCount}>
                      {Object.values(lobbyState.settings.boosterDistribution).reduce((a, b) => a + b, 0)} boosters
                    </span>
                  </div>
                </div>
              </div>
            )}
            {(isSealed || isWinston || isCommanderSealed) && (lobbyState.settings.setCodes.length <= 1 || lobbyState.settings.chaosBoosters) && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>{isWinston ? 'Total boosters' : 'Boosters per player'}</span>
                <select
                  value={lobbyState.settings.boosterCount}
                  onChange={(e) => updateLobbySettings({ boosterCount: Number(e.target.value) })}
                  className={styles.settingsSelect}
                >
                  {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16].map((n) => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </div>
            )}
            {/* Packs per player - for Draft and Commander Draft */}
            {(isDraft || isCommanderDraft) && lobbyState.settings.setCodes.length > 1 && !lobbyState.settings.chaosBoosters && (
              <div className={styles.settingsRow} style={{ flexDirection: 'column', alignItems: 'stretch', gap: 8 }}>
                <span className={styles.settingsLabel}>Packs per player</span>
                <div className={styles.boosterDistribution}>
                  {lobbyState.settings.setCodes.map((code) => {
                    const setName = lobbyState.settings.setNames[lobbyState.settings.setCodes.indexOf(code)] ?? code
                    const dist = lobbyState.settings.boosterDistribution
                    const count = dist[code] ?? 0
                    const total = Object.values(dist).reduce((a, b) => a + b, 0)
                    return (
                      <div key={code} className={styles.boosterDistributionRow}>
                        <span className={styles.boosterDistributionSetName}>{setName}</span>
                        <div className={styles.boosterDistributionControls}>
                          <button
                            className={styles.boosterDistributionBtn}
                            disabled={count <= 0}
                            onClick={() => {
                              const newDist = { ...dist, [code]: count - 1 }
                              updateLobbySettings({ boosterDistribution: newDist, boosterCount: total - 1 })
                            }}
                          >-</button>
                          <span className={styles.boosterDistributionCount}>{count}</span>
                          <button
                            className={styles.boosterDistributionBtn}
                            disabled={total >= 6}
                            onClick={() => {
                              const newDist = { ...dist, [code]: count + 1 }
                              updateLobbySettings({ boosterDistribution: newDist, boosterCount: total + 1 })
                            }}
                          >+</button>
                        </div>
                      </div>
                    )
                  })}
                  <div className={styles.boosterDistributionTotal}>
                    <span style={{ flex: 1 }}>Total</span>
                    <span className={styles.boosterDistributionTotalCount}>
                      {Object.values(lobbyState.settings.boosterDistribution).reduce((a, b) => a + b, 0)} packs
                    </span>
                  </div>
                </div>
              </div>
            )}
            {(isDraft || isCommanderDraft) && (lobbyState.settings.setCodes.length <= 1 || lobbyState.settings.chaosBoosters) && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>Packs per player</span>
                <select
                  value={lobbyState.settings.boosterCount}
                  onChange={(e) => updateLobbySettings({ boosterCount: Number(e.target.value) })}
                  className={styles.settingsSelect}
                >
                  {[1, 2, 3, 4, 5, 6].map((n) => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </div>
            )}
            {/* Timer setting - for Draft and Winston */}
            {(isAnyDraft) && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>{isWinston ? 'Turn timer (seconds)' : 'Pick timer (seconds)'}</span>
                <select
                  value={lobbyState.settings.pickTimeSeconds}
                  onChange={(e) => updateLobbySettings({ pickTimeSeconds: Number(e.target.value) })}
                  className={styles.settingsSelect}
                >
                  {[30, 45, 60, 90, 120].map((n) => (
                    <option key={n} value={n}>{n}s</option>
                  ))}
                </select>
              </div>
            )}
            {/* Pick 2 mode - for Draft and Commander Draft */}
            {(isDraft || isCommanderDraft) && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>Cards per pick</span>
                <div className={styles.settingsButtons}>
                  <button
                    onClick={() => updateLobbySettings({ picksPerRound: 1 })}
                    className={`${styles.settingsButton} ${lobbyState.settings.picksPerRound === 1 ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                  >
                    1
                  </button>
                  <button
                    onClick={() => updateLobbySettings({ picksPerRound: 2 })}
                    className={`${styles.settingsButton} ${lobbyState.settings.picksPerRound === 2 ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
                  >
                    2
                  </button>
                </div>
              </div>
            )}
            {/* Commander preset + Brawl knobs — only for Commander Draft / Sealed */}
            {isAnyCommander && (
              <>
                <div className={styles.settingsRow}>
                  <span className={styles.settingsLabel}>Preset</span>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ commanderPreset: 'BRAWL' })}
                      className={`${styles.settingsButton} ${lobbyState.settings.commanderPreset === 'BRAWL' ? styles.settingsButtonActive : ''}`}
                      title="Paper Brawl shape — 25 starting life, 16 commander damage"
                    >
                      Brawl (25/16)
                    </button>
                    <button
                      onClick={() => updateLobbySettings({ commanderPreset: 'COMMANDER' })}
                      className={`${styles.settingsButton} ${lobbyState.settings.commanderPreset === 'COMMANDER' ? styles.settingsButtonActive : ''}`}
                      title="Closer to Commander Legends — 30 life, 21 commander damage"
                    >
                      Commander (30/21)
                    </button>
                  </div>
                </div>
                <div className={styles.settingsRow}>
                  <span className={styles.settingsLabel}>Min deck size</span>
                  <select
                    value={lobbyState.settings.deckSizeMin}
                    onChange={(e) => updateLobbySettings({ deckSizeMin: Number(e.target.value) })}
                    className={styles.settingsSelect}
                  >
                    {[40, 50, 60, 75, 100].map((n) => (
                      <option key={n} value={n}>{n}</option>
                    ))}
                  </select>
                </div>
                <div className={styles.settingsRow}>
                  <span className={styles.settingsLabel}>Singleton</span>
                  <div className={styles.settingsButtons}>
                    <button
                      onClick={() => updateLobbySettings({ allowDuplicates: true })}
                      className={`${styles.settingsButton} ${lobbyState.settings.allowDuplicates ? styles.settingsButtonActive : ''}`}
                      title="Allow multiple copies of the same card (drafted Commander default)"
                    >
                      Duplicates OK
                    </button>
                    <button
                      onClick={() => updateLobbySettings({ allowDuplicates: false })}
                      className={`${styles.settingsButton} ${!lobbyState.settings.allowDuplicates ? styles.settingsButtonActive : ''}`}
                      title="Paper-Commander singleton — max 1 of any non-basic card"
                    >
                      Singleton
                    </button>
                  </div>
                </div>
              </>
            )}
            {/* Only a bracket has matchups. Previously shown for 2HG and Team vs. Team too, where a
                single shared game means it did nothing. */}
            {axes.event === 'ROUND_ROBIN' && (
              <div className={styles.settingsRow}>
                <span className={styles.settingsLabel}>Games per matchup</span>
                <select
                  value={lobbyState.settings.gamesPerMatch ?? 1}
                  onChange={(e) => updateLobbySettings({ gamesPerMatch: Number(e.target.value) })}
                  className={styles.settingsSelect}
                >
                  {[1, 2, 3, 4, 5].map((n) => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </div>
            )}
            <div className={styles.settingsRow}>
              <span className={styles.settingsLabel}>Visibility</span>
              <div className={styles.settingsButtons}>
                <button
                  onClick={() => updateLobbySettings({ isPublic: false })}
                  className={`${styles.settingsButton} ${!lobbyState.settings.isPublic ? styles.settingsButtonActive : ''}`}
                >
                  Private
                </button>
                <button
                  onClick={() => updateLobbySettings({ isPublic: true })}
                  className={`${styles.settingsButton} ${lobbyState.settings.isPublic ? styles.settingsButtonActive : ''}`}
                >
                  Public
                </button>
              </div>
            </div>
            <div className={styles.settingsRow}>
              <span className={styles.settingsLabel} title="Lets players use Suggest Pick and Auto-build during this event">
                AI assistance
              </span>
              <div className={styles.settingsButtons}>
                <button
                  onClick={() => updateLobbySettings({ aiAssistEnabled: false })}
                  className={`${styles.settingsButton} ${!lobbyState.settings.aiAssistEnabled ? styles.settingsButtonActive : ''}`}
                >
                  Off
                </button>
                <button
                  onClick={() => updateLobbySettings({ aiAssistEnabled: true })}
                  className={`${styles.settingsButton} ${lobbyState.settings.aiAssistEnabled ? styles.settingsButtonActive : ''}`}
                >
                  On
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Premade Decks: every player picks their own deck right here in the lobby. */}
        {isWaiting && isPremade && (
          <PremadeDeckPickerPanel lobbyState={lobbyState} />
        )}

        {/* Player list */}
        <div className={styles.playerListPanel}>
          <div className={styles.playerListHeader}>
            <span className={styles.playerListTitle}>Players</span>
            <span className={styles.playerCount}>
              {lobbyState.players.length} / {isWinston ? 2 : isGridDraft ? 4 : (lobbyState.settings.maxPlayers || 8)}
            </span>
          </div>
          {lobbyState.players.map((player, i) => (
            <div
              key={player.playerId}
              className={styles.playerRow}
              style={{ borderBottom: i < lobbyState.players.length - 1 ? undefined : 'none' }}
            >
              <div className={styles.playerInfo}>
                <div className={`${styles.statusDot} ${!player.isConnected ? styles.statusDotOffline : styles.statusDotOnline}`} />
                <span className={styles.playerName}>
                  {player.playerName}
                </span>
                {/* Team-game (2HG / Team vs. Team) team chip. Random mode: a neutral chip (teams
                    decided at game start). Manual mode: the assigned team, clickable for the host to
                    reassign. */}
                {isTeamGame && randomTeams && (
                  <span
                    style={{
                      fontSize: 10,
                      fontWeight: 800,
                      letterSpacing: '0.05em',
                      textTransform: 'uppercase',
                      color: 'rgba(226, 232, 240, 0.7)',
                      border: '1px solid rgba(148, 163, 184, 0.45)',
                      background: 'rgba(148, 163, 184, 0.12)',
                      borderRadius: 4,
                      padding: '1px 6px',
                    }}
                  >
                    Random
                  </span>
                )}
                {isTeamGame && !randomTeams && (() => {
                  const team = playerTeam(player.playerId, i)
                  const c = teamColor(team)
                  const chipStyle = {
                    fontSize: 10,
                    fontWeight: 800,
                    letterSpacing: '0.05em',
                    textTransform: 'uppercase' as const,
                    color: c.bright,
                    border: `1px solid ${c.base}`,
                    background: c.soft,
                    borderRadius: 4,
                    padding: '1px 6px',
                  }
                  const hostCanEdit = isWaiting && lobbyState.isHost
                  return hostCanEdit ? (
                    <button
                      onClick={() => togglePlayerTeam(player.playerId, i)}
                      style={{ ...chipStyle, cursor: 'pointer' }}
                      title="Click to move this player to the other team"
                    >
                      Team {team + 1}
                    </button>
                  ) : (
                    <span style={chipStyle}>Team {team + 1}</span>
                  )
                })()}
                {player.isHost && (
                  <span className={styles.hostBadge}>Host</span>
                )}
              </div>
              <div className={styles.playerActions}>
                <span className={`${styles.playerStatus} ${!player.isConnected
                  ? styles.playerStatusDisconnected
                  : player.deckSubmitted
                    ? styles.playerStatusReady
                    : styles.playerStatusJoined
                  }`}>
                  {!player.isConnected
                    ? 'Disconnected'
                    : player.deckSubmitted
                      ? 'Deck Ready'
                      : 'Joined'}
                </span>
                {isWaiting && lobbyState.isHost && player.isAi && (
                  <button
                    onClick={() => removeAiFromLobby(player.playerId)}
                    className={styles.removeAiButton}
                    title="Remove AI player"
                  >
                    ×
                  </button>
                )}
              </div>
            </div>
          ))}
          {lobbyState.players.length === 0 && (
            <div className={styles.emptyPlayerList}>
              Waiting for players to join...
            </div>
          )}
          {isWaiting && lobbyState.isHost && aiEnabled && !isFfa && playerCount < (isWinston ? 2 : isGridDraft ? 4 : (lobbyState.settings.maxPlayers || 8)) && (
            <button onClick={addAiToLobby} className={styles.addAiButton}>
              + Add AI Player
            </button>
          )}
        </div>

        {/* Actions */}
        <div className={styles.actionsRow}>
          {isWaiting && lobbyState.isHost && (
            <button
              onClick={startLobby}
              disabled={!canStart}
              title={
                isPremade
                  ? lobbyState.players.length < 2
                    ? 'Need at least 2 players'
                    : !allConnectedDecksSubmitted
                      ? 'All connected players must submit a deck first'
                      : undefined
                  : !hasSelectedSets
                    ? 'Select at least one set'
                    : !hasBaseSet
                      ? 'Extension sets need a regular set alongside them — add one'
                      : isWinston && lobbyState.players.length !== 2
                      ? 'Winston Draft requires exactly 2 players'
                      : lobbyState.players.length < 2
                        ? 'Need at least 2 players'
                        : undefined
              }
              className={styles.startButton}
            >
              {isAnyDraft ? 'Start Draft' : isPremade ? (isFfa ? 'Start Game' : 'Start Tournament') : 'Start Game'}
            </button>
          )}
          <button onClick={leaveLobby} className={styles.leaveButton}>
            Leave
          </button>
        </div>

        {isWaiting && !lobbyState.isHost && (
          <p className={styles.waitingHint}>
            Waiting for host to start the game...
          </p>
        )}
      </div>

      {showSetPicker && (
        <SetPickerModal
          sets={allSets}
          selectedCodes={lobbyState.settings.setCodes}
          onToggleSet={toggleSet}
          onSelectRandom={addRandomSet}
          onClose={() => setShowSetPicker(false)}
        />
      )}
    </div>
  )
}

/**
 * Embedded deck picker for the Premade Decks tournament format. Each player picks
 * (saved/example/paste) and submits their deck right in the lobby; the host can only
 * start once everybody has submitted.
 */
function PremadeDeckPickerPanel({ lobbyState }: { lobbyState: LobbyState }) {
  const submitLobbyDeck = useGameStore((s) => s.submitLobbyDeck)
  const unsubmitLobbyDeck = useGameStore((s) => s.unsubmitLobbyDeck)
  const playerId = useGameStore((s) => s.playerId)

  const me = lobbyState.players.find((p) => p.playerId === playerId)
  const hasSubmitted = !!me?.deckSubmitted

  const [pendingDeck, setPendingDeck] = useState<Record<string, number>>({})
  const [pendingCommander, setPendingCommander] = useState<string | null>(null)
  const [pendingSideboard, setPendingSideboard] = useState<Record<string, number>>({})
  const [isValid, setIsValid] = useState(false)

  const handleDeckChange = useCallback(
    (deck: Record<string, number>, commander?: string | null, sideboard?: Record<string, number>) => {
      setPendingDeck(deck)
      setPendingCommander(commander ?? null)
      setPendingSideboard(sideboard ?? {})
    },
    [],
  )

  if (hasSubmitted) {
    return (
      <div className={styles.deckSubmittedCard} role="status">
        <div className={styles.deckSubmittedIcon} aria-hidden>✓</div>
        <div className={styles.deckSubmittedBody}>
          <span className={styles.deckSubmittedTitle}>Deck submitted</span>
          <span className={styles.deckSubmittedSubtitle}>
            Waiting for the host to start the tournament.
          </span>
        </div>
        <button onClick={unsubmitLobbyDeck} className={styles.deckSubmittedEditButton}>
          Edit deck
        </button>
      </div>
    )
  }

  const deckFormat = lobbyState.settings.deckFormat
  const isCommanderShape =
    deckFormat === 'COMMANDER' || deckFormat === 'BRAWL' || deckFormat === 'STANDARD_BRAWL'
  const totalCards = Object.values(pendingDeck).reduce((a, b) => a + b, 0)
  const needsCommander = isCommanderShape && !pendingCommander
  const canSubmit = isValid && totalCards >= 40 && !needsCommander

  const submitTitle = !canSubmit
    ? needsCommander
      ? 'Pick a deck with a designated commander to play this format'
      : 'Pick a valid deck of at least 40 cards'
    : undefined

  return (
    <div className={styles.settingsPanel}>
      <div className={styles.settingsRow} style={{ alignItems: 'flex-start', flexDirection: 'column', gap: 12 }}>
        <span className={styles.settingsLabel}>Your Deck</span>
        {deckFormat && (
          <span className={styles.formatRestrictionNotice}>
            <span className={styles.formatRestrictionBadge}>{labelForFormat(deckFormat)}</span>
            <span>Only cards legal in this format will be accepted.</span>
          </span>
        )}
        <DeckPicker
          tabs={['saved', 'examples', 'paste']}
          onDeckChange={handleDeckChange}
          onValidityChange={setIsValid}
          format={deckFormat ?? null}
        />
        <button
          onClick={() => submitLobbyDeck(pendingDeck, isCommanderShape ? pendingCommander : null, pendingSideboard)}
          disabled={!canSubmit}
          title={submitTitle}
          className={styles.startButton}
        >
          Submit Deck
        </button>
      </div>
    </div>
  )
}
