/**
 * One lobby screen, whichever server implementation is behind it.
 *
 * Before this there were two: `QuickGameLobbyOverlay` (456 lines) and `LobbyOverlay` (1131), which
 * already shared a stylesheet — `QuickGameLobbyOverlay`'s header comment said so — but not a line
 * of structure. The visual language was identical and the *behaviour* had quietly diverged: only
 * one had a fullscreen button, only one showed a QR code, the two disagreed about who could see
 * the settings, and the axes could only be changed on one of them.
 *
 * The merge is over `UnifiedLobbyView` (`lobbyViewModel.ts`) rather than a shared base component,
 * so the screen never asks which kind it is looking at except where the kinds genuinely differ:
 * the deck section (a quick lobby auto-submits as you pick; a premade tournament lobby has an
 * explicit Submit) and the tournament-only knobs (`TournamentLobbySettings`).
 *
 * The payoff is the thing Phase 4 exists for: someone who entered via "vs AI" can now change the
 * Table to Free-for-All without backing out to the home screen, because the axis rows are the same
 * rows on both kinds and `axisChoices.ts` knows which ones need the lobby recreating.
 */
import { Fragment, useCallback, useEffect, useRef, useState, type ReactNode } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { LobbyState } from '@/store/slices/types'
import { randomBackground } from '@/utils/background'
import { buildJoinUrl } from '@/utils/joinLink'
import { labelForFormat } from '@/utils/deckLegality'
import momirVigUrl from '@/assets/momir-vig.svg'
import { DeckPicker, type DeckPickerTab } from '../ui/DeckPicker'
import { FullscreenButton } from '../ui/FullscreenButton'
import { JoinQrModal } from '../ui/JoinQrModal'
import { SettingsLabel } from '../ui/SettingsLabel'
import { AiDeckSection, AiOpponentRow, initialAiSource, type AiDeckSource } from './AiOpponentPanel'
import { LobbyAiDeckModal, aiDeckSummary } from './LobbyAiDeckModal'
import {
  CardsAxisBody,
  CardsAxisStrip,
  EventAxisBody,
  EventAxisStrip,
  RulesAxisBody,
  RulesAxisStrip,
  TableAxisBody,
  TableAxisStrip,
  eventCaption,
} from './LobbyAxes'
import { SettingsGroup } from './SettingsGroup'
import {
  GROUP_IDS,
  groupLabel,
  groupSummary,
  groupTopicId,
  situationalOptions,
  type GroupId,
} from './settingsGroups'
import { LobbyAxisSummary } from './LobbyAxisSummary'
import { TeamChip, TournamentLobbySettings } from './TournamentLobbySettings'
import { rulesFromLobbySettings } from './axes'
import { recreateTargetLabel, type RecreateSpec } from './axisChoices'
import { fromQuickGameLobby, fromTournamentLobby, type UnifiedLobbyView } from './lobbyViewModel'
import { takePendingLobbyIntent } from '@/store/slices/pendingLobbyIntent'
import { useSetupLibrary } from '@/store/setupLibrary'
import { useCaptureRecipe } from './useCaptureRecipe'
import { useLobbyCommands } from './useLobbyCommands'
import styles from '../ui/GameUI.module.css'

export function LobbyScreen() {
  const quickLobby = useGameStore((s) => s.quickGameLobbyState)
  const lobbyState = useGameStore((s) => s.lobbyState)
  const aiEnabled = useGameStore((s) => s.aiEnabled)
  const playerId = useGameStore((s) => s.playerId)
  const captureLast = useSetupLibrary((s) => s.captureLast)
  const saveSetup = useSetupLibrary((s) => s.saveSetup)

  // Deck-picker state the Cards axis needs to read: its validity gates the ready button, and its
  // tab *is* the Cards value on a quick lobby (Random pool is the Random tab).
  const [deckValid, setDeckValid] = useState(true)
  // Whatever created this lobby had things to say about it that no message could carry — which tab
  // the deck picker opens on, which saved deck to preselect, whether to start straight away, and
  // anything a setup couldn't restore. Read once, on mount. See `pendingLobbyIntent.ts`.
  const [intent] = useState(takePendingLobbyIntent)
  const [deckTab, setDeckTab] = useState<DeckPickerTab | undefined>(intent?.deckTab)
  const [copied, setCopied] = useState(false)
  // Which source the AI-deck control is on. Lifted out of `AiOpponentRow` because its "Pick a
  // deck" picker renders outside the settings panel the row lives in (see `AiDeckSection`).
  const [aiSource, setAiSource] = useState<AiDeckSource>(() => initialAiSource(quickLobby?.aiDeck))
  const [pendingRecreate, setPendingRecreate] = useState<RecreateSpec | null>(null)
  /** Which AI seat's deck the host is choosing, by player id. Null = the modal is closed. */
  const [aiDeckSeat, setAiDeckSeat] = useState<string | null>(null)
  // Which saved deck is loaded, by name — the identity `onDeckChange`'s card list can't carry, and
  // the one thing a setup needs in order to bring the same deck back. See `lobbyRecipe.ts`.
  const [savedDeckName, setSavedDeckName] = useState<string | null>(null)
  const [savingSetup, setSavingSetup] = useState(false)
  const [notes, setNotes] = useState<readonly string[]>(intent?.notes ?? [])

  const view: UnifiedLobbyView | null = quickLobby
    ? fromQuickGameLobby(quickLobby, { deckValid, deckTab })
    : lobbyState
      ? fromTournamentLobby(lobbyState, { aiEnabled, playerId })
      : null

  const commands = useLobbyCommands(view, setDeckTab)
  const capture = useCaptureRecipe(view, lobbyState, quickLobby, deckTab, savedDeckName)
  // Which shape-dependent options each group is currently holding — read twice: named in the
  // collapsed header, and diffed by `useGroupOpenState` to open a group the moment one appears.
  const situational = view
    ? (Object.fromEntries(
        GROUP_IDS.map((id) => [id, situationalOptions(id, view, lobbyState)]),
      ) as Record<GroupId, readonly string[]>)
    : null
  const { isGroupOpen, toggleGroup, revealedIn } = useGroupOpenState(
    view?.blockGroup ?? null,
    // A lobby a setup built already has its sets; a fresh one is about to need them.
    intent !== undefined,
    situational,
  )

  /**
   * Start the game, remembering what it was first.
   *
   * The primary action is the right moment and the only one: it is where the settings stop moving
   * and the deck has been chosen. The wizard is too early (it knows three of twenty-odd answers) and
   * the server is too late (it never learns which of *your* decks this is).
   */
  const runPrimary = () => {
    const kind = view?.primaryAction?.kind
    if (kind === 'START' || kind === 'READY') captureLast(capture().recipe)
    commands.runPrimary()
  }

  /**
   * Auto-start, for a setup that had nothing left to ask.
   *
   * Not a second launch path — the same button, pressed for you — so nothing here can drift from the
   * normal flow, and a recipe that only half-applied leaves you sitting in a correctly-built lobby
   * with its notes on screen instead of in a game you didn't mean to start.
   *
   * **Only when nobody can join.** A lobby with an invite code exists so that people can use it;
   * starting it the instant it opens would slam the door on them.
   */
  const canAutoStart = intent?.autoStart === true && view !== null && !view.invitable &&
    view.primaryAction?.kind === 'READY' && !view.primaryAction.disabled
  const autoStarted = useRef(false)
  useEffect(() => {
    if (!canAutoStart || autoStarted.current) return
    autoStarted.current = true
    runPrimary()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [canAutoStart])

  if (!view) return null

  const copyLobbyId = () => {
    navigator.clipboard.writeText(view.lobbyId)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const showSettings = view.isWaiting && view.isHost
  const isMomir = view.axes.cards.kind === 'MOMIR'

  return (
    <div className={styles.lobbyOverlay} style={{ backgroundImage: `url(${randomBackground})` }}>
      <div className={styles.cornerControls}><FullscreenButton /></div>
      <div className={styles.lobbyContent}>
        <div className={styles.lobbyHeader}>
          {isMomir && <MomirCrest />}
          <h1 className={styles.lobbyTitle}>{view.title}</h1>
          <p className={styles.lobbySubtitle}>{view.subtitle}</p>
          <LobbyAxisSummary axes={view.axes} />
          {view.isWaiting && view.isHost && (
            <button
              type="button"
              className={styles.saveSetupButton}
              data-testid="save-setup"
              onClick={() => setSavingSetup(true)}
              title="Save this lobby — sets, packs, timer, deck and all — to launch again in one click"
            >
              ★ Save setup
            </button>
          )}
        </div>

        {/* What a setup couldn't bring back, said once and dismissable.
            The server aborts an entire `updateLobbySettings` on a field it can't resolve — an unknown
            set code, a cube card that isn't implemented — so a partially-applied setup is a real
            outcome, and one that is invisible unless it is stated. */}
        {notes.length > 0 && (
          <div className={styles.lobbyNotes} role="status">
            <div className={styles.lobbyNotesBody}>
              {notes.map((note) => <p key={note}>{note}</p>)}
            </div>
            <button
              type="button"
              className={styles.lobbyNotesDismiss}
              aria-label="Dismiss"
              onClick={() => setNotes([])}
            >
              ×
            </button>
          </div>
        )}

        {view.invitable && (
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
                <div className={styles.inviteCode} data-testid="invite-code">{view.lobbyId}</div>
              </div>
              <span
                className={`${styles.inviteCopyLabel} ${copied ? styles.inviteCopyLabelCopied : ''}`}
                style={{ flexShrink: 0, marginLeft: 12 }}
              >
                {copied ? 'Copied!' : 'Copy'}
              </span>
            </div>
            <JoinQrModal url={buildJoinUrl(view.lobbyId)} />
          </div>
        )}

        {/* Deck section. The two kinds genuinely differ here: a quick lobby submits as you pick
            (and un-readies you when you change your mind), while a premade tournament lobby has an
            explicit Submit the host waits on. */}
        {view.kind === 'TOURNAMENT' && lobbyState && view.isWaiting &&
          lobbyState.settings.format === 'PREMADE_DECKS' && (
            <PremadeDeckPickerPanel
              lobbyState={lobbyState}
              playerId={playerId}
              initialSavedDeckName={intent?.deckName}
              onSavedDeckNameChange={setSavedDeckName}
            />
        )}
        {view.kind === 'QUICK' && quickLobby?.vsAi && !isMomir && aiSource === 'deck' && (
          <AiDeckSection
            format={quickLobby.format ?? null}
            disabled={view.you?.tone === 'ready'}
          />
        )}
        {view.kind === 'QUICK' && quickLobby && !isMomir && (
          <QuickGameDeckPicker
            youSetCode={quickLobby.players.find((p) => p.playerId === quickLobby.youPlayerId)?.setCode ?? null}
            format={quickLobby.format ?? null}
            disabled={view.you?.tone === 'ready'}
            tab={deckTab}
            onTabChange={setDeckTab}
            onValidityChange={setDeckValid}
            initialSavedDeckName={intent?.deckName}
            onSavedDeckNameChange={setSavedDeckName}
          />
        )}

        <div className={styles.playerListPanel}>
          <div className={styles.playerListHeader}>
            <span className={styles.playerListTitle}>Players</span>
            <span className={styles.playerCount}>{view.players.length} / {view.maxPlayers}</span>
            {/* Beside the count rather than under a list that can be eight rows long — at a full
                pod the old placement put "+ Add AI Player" below the fold of its own panel. */}
            {view.canAddAi && (
              <button onClick={commands.addAi} className={styles.addAiInlineButton}>+ Add AI</button>
            )}
          </div>
          {seatRows(view).map(({ player, seat, teamHeader }) => (
            <Fragment key={player.playerId}>
              {teamHeader && (
                <div className={styles.teamHeader}>
                  <span>{teamHeader.label}</span>
                  <span className={teamHeader.balanced ? styles.teamHeaderCount : styles.teamHeaderCountOff}>
                    {teamHeader.have} / {teamHeader.need}
                  </span>
                </div>
              )}
            <div className={styles.playerRow}>
              <div className={styles.playerInfo}>
                {/* "Attack: left only" says left and right follow the seating order, and until now
                    the seating order was nowhere on screen. */}
                <span className={styles.seatNumber}>{seat}</span>
                <div className={`${styles.statusDot} ${player.isConnected ? styles.statusDotOnline : styles.statusDotOffline}`} />
                <span className={styles.playerName}>{player.name}</span>
                {player.isYou && <span className={styles.hostBadge}>You</span>}
                {player.isAi && <span className={styles.hostBadge}>AI</span>}
                {view.teams.mode !== 'NONE' && (
                  <TeamChip
                    team={view.teams.mode === 'MANUAL' ? (view.teams.byPlayerId[player.playerId] ?? 0) : null}
                    editable={view.isWaiting && view.isHost}
                    onClick={() => commands.togglePlayerTeam(player.playerId)}
                  />
                )}
                {player.isHost && <span className={styles.hostBadge}>Host</span>}
              </div>
              <div className={styles.playerActions}>
                {/* The AI's deck is the host's to pick only where the lobby deals it no pool; the
                    view model already answers that by leaving `aiDeck` null everywhere else. */}
                {view.isWaiting && view.isHost && player.isAi && player.aiDeck && (
                  <button
                    onClick={() => setAiDeckSeat(player.playerId)}
                    className={styles.settingsButton}
                    title={`Choose what ${player.name} plays`}
                  >
                    {aiDeckSummary(player.aiDeck)}
                  </button>
                )}
                <span className={`${styles.playerStatus} ${statusClass(player.tone)}`}>{player.status}</span>
                {view.isWaiting && view.isHost && player.isAi && view.kind === 'TOURNAMENT' && (
                  <button
                    onClick={() => commands.removeAi(player.playerId)}
                    className={styles.removeAiButton}
                    title="Remove AI player"
                  >
                    ×
                  </button>
                )}
              </div>
            </div>
            </Fragment>
          ))}
          {view.players.length === 0 && (
            <div className={styles.emptyPlayerList}>Waiting for players to join...</div>
          )}
          {view.players.length === 1 && view.invitable && (
            <div
              className={styles.playerRow}
              style={{ borderBottom: 'none', justifyContent: 'center', color: 'var(--text-faint)', fontStyle: 'italic' }}
            >
              Waiting for opponent…
            </div>
          )}
        </div>

        {/* Settings sit below the players and the deck picker, not above them.
            Below the fold is where a *configured* lobby wants them: with the groups collapsed the
            whole panel is around 320px, it is host-only, and it is set once. What you keep coming
            back to while waiting is who has arrived and whether the teams are even. */}
        {showSettings && (
          <div className={styles.settingsPanel}>
            {GROUP_IDS.map((id) => {
              const axisStrip = {
                CARDS: <CardsAxisStrip view={view} commands={commands} onRecreate={setPendingRecreate} />,
                RULES: <RulesAxisStrip view={view} commands={commands} onRecreate={setPendingRecreate} />,
                TABLE: <TableAxisStrip view={view} commands={commands} onRecreate={setPendingRecreate} />,
                EVENT: <EventAxisStrip view={view} commands={commands} onRecreate={setPendingRecreate} />,
                LOBBY: null,
              }[id]
              // Built as a list rather than a fragment so "is this group empty?" is answerable:
              // an empty group must render no chevron, and a group that is empty *and* has no axis
              // strip must not render at all.
              const rows: ReactNode[] = []
              if (id === 'CARDS') rows.push(<CardsAxisBody key="axis" view={view} commands={commands} />)
              if (id === 'RULES') rows.push(<RulesAxisBody key="axis" view={view} />)
              if (id === 'TABLE') rows.push(<TableAxisBody key="axis" view={view} />)
              if (id === 'EVENT' && eventCaption(view) !== '') {
                rows.push(<EventAxisBody key="axis" view={view} />)
              }
              if (id === 'EVENT' && view.ranked.available) {
                rows.push(<RankedRow key="ranked" on={view.ranked.on} onChange={commands.setRanked} />)
              }
              // A lobby nobody can join has nothing to make public — the server forces a vs-AI
              // lobby private regardless (`QuickGameLobbyHandler.handleCreate`).
              if (id === 'LOBBY' && view.invitable) {
                rows.push(<VisibilityRow key="vis" isPublic={view.isPublic} onChange={commands.setPublic} />)
              }
              // Only a quick vs-AI lobby has an AI seat whose deck is the host's to choose. Momir
              // Basic hands every seat the same fixed 60 basics, so there is nothing to pick.
              if (id === 'LOBBY' && view.kind === 'QUICK' && quickLobby?.vsAi && !isMomir) {
                rows.push(
                  <AiOpponentRow
                    key="ai"
                    aiDeck={quickLobby.aiDeck ?? null}
                    format={quickLobby.format ?? null}
                    disabled={view.you?.tone === 'ready'}
                    source={aiSource}
                    onSourceChange={setAiSource}
                  />,
                )
              }
              // Event is the one group whose tournament rows can *all* be absent — everything else
              // has either an unconditional row (AI assistance) or an axis caption. Asked here as one
              // condition rather than as a table of "which rows does this group have", which would be
              // a second copy of what `TournamentLobbySettings` already decides and would drift.
              if (lobbyState && view.kind === 'TOURNAMENT' &&
                  (id !== 'EVENT' || view.axes.event === 'ROUND_ROBIN')) {
                rows.push(
                  <TournamentLobbySettings key="tournament" group={id} view={view} lobbyState={lobbyState} />,
                )
              }
              // A group with neither a strip nor a row is not a group: a quick lobby has no
              // AI-assistance switch and no tournament rows, so "This lobby" would be an empty box.
              if (!axisStrip && rows.length === 0) return null
              return (
                <SettingsGroup
                  key={id}
                  label={groupLabel(id)}
                  topicId={groupTopicId(id, view)}
                  summary={groupSummary(id, view, lobbyState)}
                  axisStrip={axisStrip}
                  blocking={view.blockGroup === id ? view.primaryAction?.reason : undefined}
                  situational={situational?.[id] ?? EMPTY_OPTIONS}
                  revealed={revealedIn(id)}
                  open={isGroupOpen(id)}
                  onToggle={() => toggleGroup(id)}
                  testId={id.toLowerCase()}
                >
                  {rows.length > 0 ? rows : null}
                </SettingsGroup>
              )
            })}
          </div>
        )}
        <div className={styles.actionsRow}>
          {view.primaryAction && (
            <button
              type="button"
              onClick={runPrimary}
              disabled={view.primaryAction.disabled}
              title={view.primaryAction.reason ?? ''}
              className={styles.startButton}
            >
              {view.primaryAction.label}
            </button>
          )}
          <button onClick={commands.leave} className={styles.leaveButton} type="button">Leave</button>
          {/* Said, not just hovered: a `title` on a disabled button is the least discoverable place
              to put the one sentence explaining why it can't be pressed. */}
          {view.primaryAction?.disabled && view.primaryAction.reason && (
            <span className={styles.actionsBlockReason}>{view.primaryAction.reason}</span>
          )}
        </div>

        {view.isWaiting && !view.isHost && view.startModel === 'HOST_START' && (
          <p className={styles.waitingHint}>Waiting for host to start the game...</p>
        )}
      </div>

      {aiDeckSeat && (() => {
        // Read the seat back out of the view each render rather than closing over it: the roster is
        // re-broadcast on every change, and a seat removed while its modal is open must close it.
        const seat = view.players.find((p) => p.playerId === aiDeckSeat && p.isAi && p.aiDeck)
        if (!seat) return null
        return (
          <LobbyAiDeckModal
            playerId={seat.playerId}
            playerName={seat.name}
            aiDeck={seat.aiDeck ?? null}
            format={lobbyState?.settings.deckFormat ?? null}
            onClose={() => setAiDeckSeat(null)}
          />
        )
      })()}

      {savingSetup && (
        <SaveSetupDialog
          defaultName={view.title}
          onCancel={() => setSavingSetup(false)}
          onSave={(name) => {
            saveSetup({ name, recipe: capture().recipe })
            setSavingSetup(false)
          }}
        />
      )}

      {pendingRecreate && (
        <RecreateConfirm
          spec={pendingRecreate}
          view={view}
          onCancel={() => setPendingRecreate(null)}
          onConfirm={() => { commands.recreate(pendingRecreate); setPendingRecreate(null) }}
        />
      )}
    </div>
  )
}

/**
 * The player list as numbered seats, grouped by team when the host is choosing them.
 *
 * Two problems this fixes, both only visible at 3+ players — the case the lobby was least designed
 * for and the one a group setup lands you in.
 *
 * **Seat numbers.** The Free-for-All attack rule offers "left only" and "right only" and its own
 * caption says they follow the seating order, but the seating order was nowhere on the screen. The
 * order the server uses is the order of `view.players`, so numbering the rows is all it takes.
 *
 * **Team grouping.** At a 6-player Team vs. Team the host had six rows of identical shape with a
 * colour-coded chip on each, and the only statement of whether the teams were *legal* was a sentence
 * inside a settings caption. `view.teams` already computes `size` and `balanced`; this surfaces them
 * where the decision is actually made. Grouping only happens in MANUAL mode — under RANDOM the teams
 * are rolled at game start, so any grouping shown here would be fiction.
 */
function seatRows(view: UnifiedLobbyView): Array<{
  player: UnifiedLobbyView['players'][number]
  seat: number
  teamHeader: { label: string; have: number; need: number; balanced: boolean } | null
}> {
  const seatOf = new Map(view.players.map((p, i) => [p.playerId, i + 1]))
  const teams = view.teams
  if (teams.mode !== 'MANUAL') {
    return view.players.map((player) => ({ player, seat: seatOf.get(player.playerId)!, teamHeader: null }))
  }

  const out: ReturnType<typeof seatRows> = []
  for (const team of [0, 1]) {
    const members = view.players.filter((p) => (teams.byPlayerId[p.playerId] ?? 0) === team)
    members.forEach((player, i) => {
      out.push({
        player,
        seat: seatOf.get(player.playerId)!,
        teamHeader: i === 0
          ? {
              label: `Team ${team + 1}`,
              have: members.length,
              need: teams.size,
              balanced: members.length === teams.size,
            }
          : null,
      })
    })
  }
  return out
}

/** Ranked is a 1v1-bracket concept server-side, so it refines **Event** rather than the lobby. */
function RankedRow({ on, onChange }: { on: boolean; onChange: (ranked: boolean) => void }) {
  return (
    <div className={styles.settingsRow}>
      <SettingsLabel topicId="ranked">Ranked</SettingsLabel>
      <div className={styles.variantGroup}>
        <div className={styles.settingsButtons}>
          <button
            type="button"
            onClick={() => onChange(false)}
            className={`${styles.settingsButton} ${!on ? styles.settingsButtonActive : ''}`}
            title="Casual — no rating change"
          >
            Casual
          </button>
          <button
            type="button"
            onClick={() => onChange(true)}
            className={`${styles.settingsButton} ${on ? styles.settingsButtonActive : ''}`}
            title="Ranked — adjusts each player's ELO"
          >
            Ranked
          </button>
        </div>
        {on && (
          <div className={styles.variantCaption}>
            Ranked games adjust each player's ELO. All players must be signed in for it to count —
            otherwise it just plays unranked.
          </div>
        )}
      </div>
    </div>
  )
}

function VisibilityRow({
  isPublic,
  onChange,
}: {
  isPublic: boolean
  onChange: (isPublic: boolean) => void
}) {
  return (
    <div className={styles.settingsRow}>
      <span className={styles.settingsLabel}>Visibility</span>
      <div className={styles.settingsButtons}>
        <button
          type="button"
          onClick={() => onChange(false)}
          className={`${styles.settingsButton} ${!isPublic ? styles.settingsButtonActive : ''}`}
        >
          Private
        </button>
        <button
          type="button"
          onClick={() => onChange(true)}
          className={`${styles.settingsButton} ${isPublic ? styles.settingsButtonActive : ''}`}
        >
          Public
        </button>
      </div>
    </div>
  )
}

/**
 * Which settings groups are open, derived rather than hand-tuned.
 *
 * Four rules, in order:
 *
 * 1. **The group holding the reason Start is disabled is open.** Not a courtesy — it is what keeps
 *    collapsing compatible with the project's disabled-with-reason rule: nothing you need to act on
 *    can be hidden. It reads `view.blockGroup`, which comes from the same `startBlockReason` that
 *    writes the Start button's tooltip.
 * 2. **A group that just gained an option opens itself**, and says which one. Switching the Table
 *    axis to Free-for-All *creates* the attack rule; switching to Two-Headed Giant creates the team
 *    setup. Landing that new decision inside a box the host has never expanded is how it stays
 *    unfound — and the host is looking at the axis strip they just clicked, which is exactly where
 *    the group that opens is. Only genuine appearances count (see {@link situationalOptions}); a
 *    value changing inside an option that was already there does not re-open anything.
 * 3. **Cards is open on a lobby that wasn't launched from a setup**, because a fresh lobby's next
 *    move is almost always choosing sets. A lobby a setup built already has them.
 * 4. Otherwise closed — and whatever the host opened or closed by hand wins over all of it, kept in
 *    localStorage so it survives the recreate that switching an axis can cause.
 *
 * Rule 2 writes into the same overrides the host's own clicks use, rather than sitting beside them
 * as a third source of truth: a group opened this way is a group the host can simply close again,
 * and it stays closed.
 */
function useGroupOpenState(
  blockGroup: GroupId | null,
  fromSetup: boolean,
  /** Null until the lobby state has arrived — see the effect. */
  situational: Record<GroupId, readonly string[]> | null,
) {
  const [overrides, setOverrides] = useState<Partial<Record<GroupId, boolean>>>(() => {
    try {
      const raw = localStorage.getItem(GROUP_OPEN_KEY)
      return raw ? (JSON.parse(raw) as Partial<Record<GroupId, boolean>>) : {}
    } catch {
      return {}
    }
  })
  const [revealed, setRevealed] = useState<Partial<Record<GroupId, readonly string[]>>>({})
  const previous = useRef<Record<GroupId, readonly string[]> | null>(null)

  // Depend on the *contents*, not the object: `situational` is rebuilt every render because the view
  // it derives from is.
  const signature = situational
    ? GROUP_IDS.map((id) => `${id}:${situational[id].join(',')}`).join('|')
    : null
  useEffect(() => {
    // Nothing to compare against yet. Tracking the empty pre-state would make the lobby's *first*
    // state message look like five groups' worth of options appearing at once, and open all of them.
    if (!situational) return
    const before = previous.current
    previous.current = situational
    // First state for this lobby: everything present arrived with it, so nothing "appeared".
    if (!before) return

    const gained: Partial<Record<GroupId, readonly string[]>> = {}
    for (const id of GROUP_IDS) {
      const added = situational[id].filter((option) => !before[id].includes(option))
      if (added.length > 0) gained[id] = added
    }
    const ids = Object.keys(gained) as GroupId[]
    if (ids.length === 0) return

    setRevealed(gained)
    setOverrides((prev) => persistOverrides({ ...prev, ...Object.fromEntries(ids.map((id) => [id, true])) }))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [signature])

  const isGroupOpen = (id: GroupId): boolean => {
    if (blockGroup === id) return true
    const override = overrides[id]
    if (override !== undefined) return override
    return id === 'CARDS' && !fromSetup
  }

  const toggleGroup = (id: GroupId) => {
    // The "New:" note answered "why did this open?". Acting on the group is the answer being read.
    setRevealed((prev) => (prev[id] ? { ...prev, [id]: undefined } : prev))
    setOverrides((prev) => persistOverrides({ ...prev, [id]: !isGroupOpen(id) }))
  }

  return { isGroupOpen, toggleGroup, revealedIn: (id: GroupId) => revealed[id] ?? EMPTY_OPTIONS }
}

function persistOverrides(next: Partial<Record<GroupId, boolean>>): Partial<Record<GroupId, boolean>> {
  try {
    localStorage.setItem(GROUP_OPEN_KEY, JSON.stringify(next))
  } catch {
    // Private browsing / full quota. Remembering the panel's shape is a nicety.
  }
  return next
}

const EMPTY_OPTIONS: readonly string[] = []

const GROUP_OPEN_KEY = 'argentum-lobby-groups'

function statusClass(tone: 'ready' | 'joined' | 'disconnected'): string {
  switch (tone) {
    case 'ready': return styles.playerStatusReady ?? ''
    case 'disconnected': return styles.playerStatusDisconnected ?? ''
    case 'joined': return styles.playerStatusJoined ?? ''
  }
}

function MomirCrest() {
  return (
    <div
      aria-hidden
      style={{
        width: 84,
        height: 84,
        margin: '0 auto 8px',
        backgroundColor: 'var(--accent-teal, #6fd3c0)',
        WebkitMaskImage: `url(${momirVigUrl})`,
        maskImage: `url(${momirVigUrl})`,
        WebkitMaskSize: 'contain',
        maskSize: 'contain',
        WebkitMaskRepeat: 'no-repeat',
        maskRepeat: 'no-repeat',
        WebkitMaskPosition: 'center',
        maskPosition: 'center',
        opacity: 0.9,
      }}
    />
  )
}

/**
 * The stop sign in front of a cross-kind axis switch (plan § 4b v1).
 *
 * A recreate is cheap when you are alone in a lobby you just opened — the common case — and
 * expensive once anyone has joined, so the dialog counts them rather than warning in the abstract.
 */
function RecreateConfirm({
  spec,
  view,
  onCancel,
  onConfirm,
}: {
  spec: RecreateSpec
  view: UnifiedLobbyView
  onCancel: () => void
  onConfirm: () => void
}) {
  const others = view.players.filter((p) => !p.isYou && !p.isAi).length
  const ais = view.players.filter((p) => p.isAi).length

  return (
    <div className={styles.confirmBackdrop} role="dialog" aria-modal="true" onClick={onCancel}>
      <div className={styles.confirmPanel} onClick={(e) => e.stopPropagation()}>
        <div className={styles.confirmTitle}>Start a new lobby?</div>
        <p className={styles.confirmBody}>
          “{recreateTargetLabel(spec)}” runs on a different lobby to this one, so switching opens a
          fresh one.
        </p>
        <ul className={styles.confirmCosts}>
          <li>Your invite code changes — you'll need to re-share it.</li>
          {others > 0 && (
            <li>
              {others === 1 ? '1 player who has' : `${others} players who have`} joined will be
              dropped.
            </li>
          )}
          {ais > 0 && <li>{ais === 1 ? 'The AI seat' : `${ais} AI seats`} will need adding again.</li>}
          {view.kind === 'TOURNAMENT' && <li>Set selection and any submitted decks are reset.</li>}
        </ul>
        <div className={styles.confirmActions}>
          <button type="button" onClick={onCancel} className={styles.leaveButton}>Cancel</button>
          <button type="button" onClick={onConfirm} className={styles.startButton} data-testid="confirm-recreate">
            Switch
          </button>
        </div>
      </div>
    </div>
  )
}

/**
 * The quick lobby's deck picker, with the submission plumbing it has always needed.
 *
 * Submissions are throttled *and* deduped: the picker fires several times per keystroke, and it
 * re-emits its current deck on every re-render — including the ones a server broadcast causes — so
 * without the dedupe an unchanged deck would be resent, which server-side clears your `ready` flag
 * and triggers another broadcast. That was a real spam loop.
 */
function QuickGameDeckPicker({
  youSetCode,
  format,
  disabled,
  tab,
  onTabChange,
  onValidityChange,
  initialSavedDeckName,
  onSavedDeckNameChange,
}: {
  youSetCode: string | null
  format: string | null
  disabled: boolean
  tab: DeckPickerTab | undefined
  onTabChange: (tab: DeckPickerTab) => void
  onValidityChange: (valid: boolean) => void
  /** A saved setup's deck, preselected once the library hydrates. */
  initialSavedDeckName: string | undefined
  onSavedDeckNameChange: (name: string | null) => void
}) {
  const submitDeck = useGameStore((s) => s.submitQuickGameLobbyDeck)
  const setSetCode = useGameStore((s) => s.setQuickGameLobbySetCode)
  const availableSets = useGameStore((s) => s.availableSets)

  const pendingDeckRef = useRef<Record<string, number> | null>(null)
  const pendingCommanderRef = useRef<string | null>(null)
  const lastSubmittedKeyRef = useRef<string | null>(null)
  const debounceRef = useRef<number | null>(null)

  const handleDeckChange = useCallback(
    (deckList: Record<string, number>, commander?: string | null) => {
      // The commander rides in the dedupe key, so swapping commanders on otherwise-identical deck
      // contents still resubmits.
      const key = `${serializeDeck(deckList)}|${commander ?? ''}`
      if (key === lastSubmittedKeyRef.current) return
      pendingDeckRef.current = deckList
      pendingCommanderRef.current = commander ?? null
      if (debounceRef.current !== null) window.clearTimeout(debounceRef.current)
      debounceRef.current = window.setTimeout(() => {
        const pending = pendingDeckRef.current
        if (!pending) return
        const pendingCmdr = pendingCommanderRef.current
        const pendingKey = `${serializeDeck(pending)}|${pendingCmdr ?? ''}`
        if (pendingKey === lastSubmittedKeyRef.current) return
        lastSubmittedKeyRef.current = pendingKey
        submitDeck(pending, pendingCmdr)
      }, 250)
    },
    [submitDeck],
  )

  // Flush any pending deck on unmount so the user's last edit isn't dropped.
  useEffect(() => {
    return () => {
      if (debounceRef.current !== null) window.clearTimeout(debounceRef.current)
      const pending = pendingDeckRef.current
      const pendingCmdr = pendingCommanderRef.current
      const pendingKey = pending ? `${serializeDeck(pending)}|${pendingCmdr ?? ''}` : null
      if (pending && pendingKey !== lastSubmittedKeyRef.current) submitDeck(pending, pendingCmdr)
    }
  }, [submitDeck])

  return (
    <DeckPicker
      onDeckChange={handleDeckChange}
      onValidityChange={onValidityChange}
      onSetCodeChange={setSetCode}
      initialSetCode={youSetCode}
      availableSets={availableSets}
      disabled={disabled}
      format={format}
      tab={tab}
      onTabChange={onTabChange}
      initialSavedDeckName={initialSavedDeckName}
      onSavedDeckNameChange={onSavedDeckNameChange}
    />
  )
}

/**
 * Name a setup before saving it.
 *
 * Deliberately a real dialog rather than a `window.prompt`: this is the moment someone commits to
 * replaying a lobby they may have spent a few minutes configuring, and the default name is worth
 * showing selected and editable rather than as a browser chrome string.
 */
function SaveSetupDialog({
  defaultName,
  onCancel,
  onSave,
}: {
  defaultName: string
  onCancel: () => void
  onSave: (name: string) => void
}) {
  const [name, setName] = useState(defaultName)
  const trimmed = name.trim()

  return (
    <div className={styles.confirmBackdrop} role="dialog" aria-modal="true" onClick={onCancel}>
      <div className={styles.confirmPanel} onClick={(e) => e.stopPropagation()}>
        <div className={styles.confirmTitle}>Save this setup</div>
        <p className={styles.confirmBody}>
          It comes back on the home screen as one click — sets, packs, timer, ban list, cube and the
          deck you picked.
        </p>
        <input
          className={styles.settingsSelect}
          style={{ width: '100%' }}
          value={name}
          autoFocus
          onChange={(e) => setName(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter' && trimmed) onSave(trimmed) }}
          aria-label="Setup name"
        />
        <div className={styles.confirmActions}>
          <button type="button" onClick={onCancel} className={styles.leaveButton}>Cancel</button>
          <button
            type="button"
            onClick={() => onSave(trimmed)}
            disabled={!trimmed}
            className={styles.startButton}
            data-testid="confirm-save-setup"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  )
}

/**
 * Embedded deck picker for the Premade Decks tournament format. Each player picks and submits
 * their deck right in the lobby; the host can only start once everybody has.
 */
function PremadeDeckPickerPanel({
  lobbyState,
  playerId,
  initialSavedDeckName,
  onSavedDeckNameChange,
}: {
  lobbyState: LobbyState
  playerId: string | null
  initialSavedDeckName: string | undefined
  onSavedDeckNameChange: (name: string | null) => void
}) {
  const submitLobbyDeck = useGameStore((s) => s.submitLobbyDeck)
  const unsubmitLobbyDeck = useGameStore((s) => s.unsubmitLobbyDeck)

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
          <span className={styles.deckSubmittedSubtitle}>Waiting for the host to start.</span>
        </div>
        <button onClick={unsubmitLobbyDeck} className={styles.deckSubmittedEditButton}>Edit deck</button>
      </div>
    )
  }

  const deckFormat = lobbyState.settings.deckFormat
  // Whether this submission needs a commander follows the lobby's Rules axis, not its deck legality:
  // the server's deck-submit path keys on `usesCommanderRules`, so deriving it from the legality here
  // would ask for a commander the server doesn't want (Commander-legal decks under Standard rules) or
  // — worse — not ask for one it requires.
  const isCommanderShape = rulesFromLobbySettings(lobbyState.settings) === 'COMMANDER'
  const totalCards = Object.values(pendingDeck).reduce((a, b) => a + b, 0)
  const needsCommander = isCommanderShape && !pendingCommander
  const canSubmit = isValid && totalCards >= 40 && !needsCommander

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
          initialSavedDeckName={initialSavedDeckName}
          onSavedDeckNameChange={onSavedDeckNameChange}
        />
        <button
          onClick={() => submitLobbyDeck(pendingDeck, isCommanderShape ? pendingCommander : null, pendingSideboard)}
          disabled={!canSubmit}
          title={
            canSubmit ? undefined
              : needsCommander
                ? 'Pick a deck with a designated commander to play this format'
                : 'Pick a valid deck of at least 40 cards'
          }
          className={styles.startButton}
        >
          Submit Deck
        </button>
      </div>
    </div>
  )
}

/**
 * Stable key for a deck list, used to dedupe submissions. Sorted by name so two equal decks always
 * serialize the same regardless of insertion order in the picker.
 */
function serializeDeck(deck: Record<string, number>): string {
  return Object.entries(deck)
    .filter(([, n]) => n > 0)
    .sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))
    .map(([name, n]) => `${name}=${n}`)
    .join('|')
}
