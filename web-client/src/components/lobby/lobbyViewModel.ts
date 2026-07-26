/**
 * One view model over the two unrelated server lobby implementations.
 *
 * The server has `QuickGameLobby` (125 lines, in-memory, hard-capped at two seats, flat DTO) and
 * `TournamentLobby` (1884 lines, Redis-backed, 2–8 seats, a state machine) with **no shared
 * interface and no `kind` discriminator**. A single unified lobby is therefore not a client-only
 * change — see `backlog/menu-lobby-restructure-and-help.md` § *The honest constraint*.
 *
 * So the client unifies the *presentation* first: both slices project onto `UnifiedLobbyView`, and
 * `LobbyScreen` renders that and nothing else. Everything the two kinds genuinely disagree about
 * is named here as a field rather than branched on at every call site — which is what lets the
 * server gaps behind it (Phase 5) be closed one at a time without touching the screen.
 *
 * This module is pure: no store reads, no side effects. The commands that write back live in
 * `useLobbyCommands.ts`.
 */
import type { LobbyState } from '@/store/slices/types'
import type { QuickGameLobbyStateMessage } from '@/types'
import type { DeckPickerTab } from '../ui/DeckPicker'
import {
  COMMANDER_LIMITED_NEEDS_A_1V1_TABLE,
  axesFromLobbySettings,
  axesFromQuickGameLobby,
  isCommanderLimited,
  type AxisTriple,
  type CardsKind,
} from './axes'

/** Which server implementation is backing this lobby. */
export type LobbyKind = 'QUICK' | 'TOURNAMENT'

export interface LobbyViewPlayer {
  playerId: string
  name: string
  isYou: boolean
  isHost: boolean
  isAi: boolean
  isConnected: boolean
  /** Right-hand status text — "Deck Ready", "Choosing deck…", "✓ Ready · Custom (60)". */
  status: string
  tone: 'ready' | 'joined' | 'disconnected'
}

/**
 * How the lobby is told to go.
 *
 * `PER_PLAYER_READY` — everyone toggles ready and the server starts when all are (quick lobbies).
 * `HOST_START` — the host presses one button (tournament lobbies). Gap #4 in the plan's Phase 5
 * list is giving tournament lobbies the per-player flavour too, which is what makes a two-player
 * game *feel* quick; until then the difference is real and named rather than papered over.
 */
export type StartModel = 'PER_PLAYER_READY' | 'HOST_START'

/**
 * Team setup for the two team tables (2HG — CR 810; Team vs. Team — CR 808).
 *
 * `MANUAL` resolves every seat, defaults included, so the player list doesn't have to re-derive
 * "team by join order" the way three separate call sites used to.
 */
export type LobbyTeams =
  | { mode: 'NONE' }
  | { mode: 'RANDOM' }
  | {
      mode: 'MANUAL'
      byPlayerId: Readonly<Record<string, number>>
      /** Seats per team — both teams must hold exactly this many or the server re-balances. */
      size: number
      balanced: boolean
    }

export interface LobbyPrimaryAction {
  kind: 'READY' | 'UNREADY' | 'START'
  label: string
  disabled: boolean
  /** Why it is disabled — becomes the button's tooltip. */
  reason: string | undefined
}

export interface UnifiedLobbyView {
  kind: LobbyKind
  lobbyId: string
  title: string
  subtitle: string
  isHost: boolean
  /** Pre-game staging: settings are editable and the axes can still be changed. */
  isWaiting: boolean
  /** A vs-AI lobby has nobody to invite, so it shows no code and no QR. */
  invitable: boolean
  /** Where this lobby sits in the Cards / Table / Event space. */
  axes: AxisTriple
  players: readonly LobbyViewPlayer[]
  you: LobbyViewPlayer | undefined
  maxPlayers: number
  startModel: StartModel
  /** The one action button beside Leave, or null when there is nothing for this viewer to press. */
  primaryAction: LobbyPrimaryAction | null
  isPublic: boolean
  /** Whether the host may currently add an AI seat. */
  canAddAi: boolean
  ranked: { available: boolean; on: boolean }
  teams: LobbyTeams
}

/* ── Quick game ─────────────────────────────────────────────────────────── */

/**
 * @param opts.deckValid the deck picker's live validity — component-local state, so it has to be
 *   passed in rather than read. A quick lobby's ready button gates on it.
 * @param opts.deckTab the deck picker's live tab, which *is* the Cards axis on a quick lobby:
 *   Random pool is the picker's Random tab, so hoisting it is what keeps the axis row, the header
 *   chip and the picker from disagreeing.
 */
export function fromQuickGameLobby(
  lobby: QuickGameLobbyStateMessage,
  opts: { deckValid: boolean; deckTab: DeckPickerTab | undefined },
): UnifiedLobbyView {
  const you = lobby.players.find((p) => p.playerId === lobby.youPlayerId)
  // Host is the first non-AI seat — the same convention the server's leave handler uses.
  const isHost = lobby.players.find((p) => !p.isAi)?.playerId === lobby.youPlayerId
  const isMomir = lobby.momirBasic ?? false
  const youReady = you?.ready ?? false
  const needsDeck = !isMomir && (!opts.deckValid || !you?.deckSelected)
  const axes = axesFromQuickGameLobby(lobby, you, opts.deckTab)

  const players = lobby.players.map((p): LobbyViewPlayer => ({
    playerId: p.playerId,
    name: p.playerName,
    isYou: p.playerId === lobby.youPlayerId,
    isHost: p.playerId === lobby.players.find((q) => !q.isAi)?.playerId,
    isAi: p.isAi,
    // Quick lobbies drop disconnected players outright, so anyone listed is connected.
    isConnected: true,
    status: !p.deckSelected
      ? 'Choosing deck…'
      : p.ready
        ? `✓ Ready · ${p.deckLabel}`
        : `Deck: ${p.deckLabel}`,
    tone: p.ready ? 'ready' : 'joined',
  }))

  return {
    kind: 'QUICK',
    lobbyId: lobby.lobbyId,
    title: lobby.vsAi ? 'vs AI' : 'Lobby',
    subtitle: quickSubtitle(axes.cards.kind, lobby.vsAi),
    isHost,
    // A quick lobby has no state machine: it is staging right up until the game starts.
    isWaiting: true,
    invitable: !lobby.vsAi,
    axes,
    players,
    you: players.find((p) => p.isYou),
    maxPlayers: 2,
    startModel: 'PER_PLAYER_READY',
    primaryAction: youReady
      ? { kind: 'UNREADY', label: 'Cancel ready', disabled: false, reason: undefined }
      : {
          kind: 'READY',
          label: "I'm ready",
          disabled: needsDeck,
          reason: needsDeck ? 'Pick a deck first' : undefined,
        },
    isPublic: lobby.isPublic,
    // AI is a create-time flag on a quick lobby, not a seat the host can add later.
    canAddAi: false,
    ranked: { available: lobby.rankedEligible ?? false, on: lobby.ranked ?? false },
    // The server's `QuickGameLobby.twoHeadedGiant` exists but no client has ever reached it (gap
    // #6): it isn't in `QuickGameLobbyStateMessage` at all, so there is nothing here to read.
    teams: { mode: 'NONE' },
  }
}

/**
 * The line under a quick lobby's title: what it still needs from you, and how it starts.
 *
 * It follows the **Cards** axis and not just `vsAi`, because the three values ask for different
 * things — and two of them ask for nothing. The previous copy branched on `vsAi` alone, so a lobby
 * created from the wizard's "Random pool" opened telling the player to "pick a deck", which is the
 * one instruction that answer had already made obsolete.
 */
function quickSubtitle(cards: CardsKind, vsAi: boolean): string {
  const start = vsAi
    ? 'Ready up and the AI starts.'
    : 'Share the invite code with a friend, then both players ready up.'
  switch (cards) {
    case 'RANDOM':
      // 8 boosters, auto-built into a 40-card deck — `SealedDeckGenerator.generate`.
      return `Nothing to prepare — the server opens boosters and builds your deck when the game starts. ${start}`
    case 'MOMIR':
      return `No deckbuilding — everyone runs 60 basics and flips creatures with the Momir Vig avatar. ${start}`
    default:
      return `Pick a deck. ${start}`
  }
}

/* ── Tournament ─────────────────────────────────────────────────────────── */

export function fromTournamentLobby(
  lobbyState: LobbyState,
  opts: { aiEnabled: boolean; playerId: string | null },
): UnifiedLobbyView {
  const s = lobbyState.settings
  const isWaiting = lobbyState.state === 'WAITING_FOR_PLAYERS'
  const axes = axesFromLobbySettings(s)
  const playerCount = lobbyState.players.length
  const isWinston = s.format === 'WINSTON_DRAFT'
  const isGridDraft = s.format === 'GRID_DRAFT'
  const isFfa = s.gameMode === 'FREE_FOR_ALL'
  const maxPlayers = isWinston ? 2 : isGridDraft ? 4 : (s.maxPlayers || 8)

  const players = lobbyState.players.map((p): LobbyViewPlayer => ({
    playerId: p.playerId,
    name: p.playerName,
    isYou: p.playerId === opts.playerId,
    isHost: p.isHost,
    isAi: p.isAi,
    isConnected: p.isConnected,
    status: !p.isConnected ? 'Disconnected' : p.deckSubmitted ? 'Deck Ready' : 'Joined',
    tone: !p.isConnected ? 'disconnected' : p.deckSubmitted ? 'ready' : 'joined',
  }))

  const blockReason = startBlockReason(lobbyState)

  return {
    kind: 'TOURNAMENT',
    lobbyId: lobbyState.lobbyId,
    title: tournamentTitle(lobbyState),
    subtitle: tournamentSubtitle(lobbyState),
    isHost: lobbyState.isHost,
    isWaiting,
    invitable: true,
    axes,
    players,
    you: players.find((p) => p.isYou),
    maxPlayers,
    startModel: 'HOST_START',
    primaryAction: isWaiting && lobbyState.isHost
      ? {
          kind: 'START',
          label: startLabel(lobbyState),
          disabled: blockReason !== null,
          reason: blockReason ?? undefined,
        }
      : null,
    isPublic: s.isPublic,
    // Commander limited excluded: `buildAiSealedDeck` submits a 40-card deck with no commander, and
    // `TournamentLobby.validateDeck` doesn't check for one — so the AI would sit down without a
    // commander rather than be rejected. `LobbyScreen` hides the button; the wizard says why.
    canAddAi: isWaiting && lobbyState.isHost && opts.aiEnabled && !isFfa
      && !isCommanderLimited(axes.cards) && playerCount < maxPlayers,
    // Ranked is a 1v1-bracket-only concept server-side (`TournamentLobby.rankedEligible`).
    ranked: { available: axes.table === 'ONE_V_ONE', on: s.ranked ?? false },
    teams: tournamentTeams(lobbyState),
  }
}

function tournamentTeams(lobbyState: LobbyState): LobbyTeams {
  const s = lobbyState.settings
  if (s.gameMode !== 'TWO_HEADED_GIANT' && s.gameMode !== 'TEAM_VS_TEAM') return { mode: 'NONE' }
  if (s.randomTeams ?? true) return { mode: 'RANDOM' }

  // Both team modes split the pod into exactly two even teams, so team size follows the seat count
  // and unassigned seats fall back to join order.
  const size = Math.max(1, Math.floor(lobbyState.players.length / 2))
  const assigned = s.teamAssignments ?? {}
  const byPlayerId: Record<string, number> = {}
  lobbyState.players.forEach((p, i) => {
    byPlayerId[p.playerId] = assigned[p.playerId] ?? Math.floor(i / size)
  })
  const n = lobbyState.players.length
  const balanced = n >= 4 && n % 2 === 0 &&
    [0, 1].every((t) => Object.values(byPlayerId).filter((v) => v === t).length === size)
  return { mode: 'MANUAL', byPlayerId, size, balanced }
}

function tournamentTitle(lobbyState: LobbyState): string {
  const s = lobbyState.settings
  if (s.format !== 'PREMADE_DECKS') return s.setNames.join(' + ') || 'Lobby'
  switch (s.gameMode) {
    case 'TWO_HEADED_GIANT': return 'Premade Decks Two-Headed Giant'
    case 'TEAM_VS_TEAM': return 'Premade Decks Team vs. Team'
    case 'FREE_FOR_ALL': return 'Premade Decks Free-for-All'
    case 'TOURNAMENT': return 'Premade Decks Tournament'
  }
}

function tournamentSubtitle(lobbyState: LobbyState): string {
  const s = lobbyState.settings
  // With more than one set selected the header names the split rather than a bare total.
  const distText = s.setCodes.length > 1 && Object.keys(s.boosterDistribution).length > 0
    ? Object.entries(s.boosterDistribution)
        .map(([code, count]) => {
          const idx = s.setCodes.indexOf(code)
          return `${count} ${idx >= 0 ? (s.setNames[idx] ?? code) : code}`
        })
        .join(' + ')
    : null
  const presetLabel = s.commanderPreset === 'COMMANDER' ? 'Commander 30 life' : 'Brawl 25 life'
  const pick2 = s.picksPerRound === 2 ? ' · Pick 2' : ''

  const base = (() => {
    switch (s.format) {
      case 'GRID_DRAFT':
        return `Grid Draft · ${s.boosterCount} boosters · ${s.pickTimeSeconds}s per pick`
      case 'WINSTON_DRAFT':
        return `Winston Draft · ${distText ?? `${s.boosterCount} boosters`} · ${s.pickTimeSeconds}s per turn`
      case 'COMMANDER_DRAFT':
        return `${distText ?? `${s.boosterCount} packs`} · ${s.pickTimeSeconds}s per pick${pick2} · ${presetLabel}`
      case 'COMMANDER_SEALED':
        return `${distText ?? `${s.boosterCount} packs`} · ${presetLabel}`
      case 'DRAFT':
        return `${distText ?? `${s.boosterCount} packs`} · ${s.pickTimeSeconds}s per pick${pick2}`
      case 'PREMADE_DECKS':
        return 'Premade Decks · bring your own ≥40-card deck'
      case 'SEALED':
        return distText ?? `${s.boosterCount} boosters per player`
    }
  })()

  const isMultiplayer = s.gameMode !== 'TOURNAMENT'
  const games = s.gamesPerMatch ?? 1
  return !isMultiplayer && games > 1 ? `${base} · ${games} games per matchup` : base
}

function startLabel(lobbyState: LobbyState): string {
  const s = lobbyState.settings
  const isAnyDraft = s.format === 'DRAFT' || s.format === 'WINSTON_DRAFT' ||
    s.format === 'GRID_DRAFT' || s.format === 'COMMANDER_DRAFT'
  if (isAnyDraft) return 'Start Draft'
  if (s.format === 'PREMADE_DECKS' && s.gameMode === 'TOURNAMENT') return 'Start Tournament'
  return 'Start Game'
}

/**
 * Why the host can't press start yet, or null when they can.
 *
 * Every branch of the seat-count rule gets its own sentence. The old inline version fell through
 * to a bare "Need at least 2 players" for the exact-count shapes, so a Two-Headed Giant lobby
 * holding three players offered a disabled button and no explanation.
 */
function startBlockReason(lobbyState: LobbyState): string | null {
  const s = lobbyState.settings
  const n = lobbyState.players.length

  switch (s.gameMode) {
    case 'TWO_HEADED_GIANT':
      if (n !== 4) return `Two-Headed Giant is exactly 4 players — this lobby has ${n}`
      break
    case 'TEAM_VS_TEAM':
      if (n < 4 || n % 2 !== 0) return `Team vs. Team needs an even pod of 4, 6 or 8 — this lobby has ${n}`
      break
    default:
      break
  }
  switch (s.format) {
    case 'WINSTON_DRAFT':
      if (n !== 2) return `Winston Draft is exactly 2 players — this lobby has ${n}`
      break
    case 'GRID_DRAFT':
      if (n < 2 || n > 4) return `Grid Draft seats 2 to 4 players — this lobby has ${n}`
      break
    case 'COMMANDER_DRAFT':
    case 'COMMANDER_SEALED':
      // Not a seat limit. The client used to require exactly two here, which conflated sharing a
      // *pool* with sharing a *game*: eight people can draft Commander and play the bracket out as
      // 1v1 matches, and the server never restricted it (`LobbyHandler.kt:605-616`). What is
      // genuinely missing is Commander at a multiplayer table — blocked on the Table axis — and
      // Commander with AI seats, whose auto-deckbuild never picks a commander.
      if (s.gameMode !== 'TOURNAMENT') return COMMANDER_LIMITED_NEEDS_A_1V1_TABLE
      break
    default:
      break
  }
  if (n < 2) return 'Need at least 2 players'

  if (s.format === 'PREMADE_DECKS') {
    const allSubmitted = lobbyState.players.filter((p) => p.isConnected).every((p) => p.deckSubmitted)
    return allSubmitted ? null : 'All connected players must submit a deck first'
  }
  if (s.setCodes.length === 0) return 'Select at least one set'
  // Extension sets (bonus sheets) can't carry a pool alone. Unknown codes count as regular; the
  // server re-validates. A deferred random slot always rolls a regular set, so it satisfies this.
  const hasBaseSet = s.setCodes.some(
    (code) => !s.availableSets.find((a) => a.code === code)?.extensionSet,
  )
  if (!hasBaseSet) return 'Extension sets need a regular set alongside them — add one'
  return null
}
