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
import { useCallback, useEffect, useRef, useState } from 'react'
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
import { LobbyAxes } from './LobbyAxes'
import { LobbyAxisSummary } from './LobbyAxisSummary'
import { TeamChip, TournamentLobbySettings } from './TournamentLobbySettings'
import { rulesFromLobbySettings } from './axes'
import { recreateTargetLabel, type RecreateSpec } from './axisChoices'
import { fromQuickGameLobby, fromTournamentLobby, type UnifiedLobbyView } from './lobbyViewModel'
import { takePendingLobbyIntent } from '@/store/slices/pendingLobbyIntent'
import { useLobbyCommands } from './useLobbyCommands'
import styles from '../ui/GameUI.module.css'

export function LobbyScreen() {
  const quickLobby = useGameStore((s) => s.quickGameLobbyState)
  const lobbyState = useGameStore((s) => s.lobbyState)
  const aiEnabled = useGameStore((s) => s.aiEnabled)
  const playerId = useGameStore((s) => s.playerId)

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

  const view: UnifiedLobbyView | null = quickLobby
    ? fromQuickGameLobby(quickLobby, { deckValid, deckTab })
    : lobbyState
      ? fromTournamentLobby(lobbyState, { aiEnabled, playerId })
      : null

  const commands = useLobbyCommands(view, setDeckTab)

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
        </div>

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

        {showSettings && (
          <div className={styles.settingsPanel}>
            <LobbyAxes view={view} commands={commands} onRecreate={setPendingRecreate} />

            {view.ranked.available && (
              <div className={styles.settingsRow}>
                <SettingsLabel topicId="ranked">Ranked</SettingsLabel>
                <div className={styles.variantGroup}>
                  <div className={styles.settingsButtons}>
                    <button
                      type="button"
                      onClick={() => commands.setRanked(false)}
                      className={`${styles.settingsButton} ${!view.ranked.on ? styles.settingsButtonActive : ''}`}
                      title="Casual — no rating change"
                    >
                      Casual
                    </button>
                    <button
                      type="button"
                      onClick={() => commands.setRanked(true)}
                      className={`${styles.settingsButton} ${view.ranked.on ? styles.settingsButtonActive : ''}`}
                      title="Ranked — adjusts each player's ELO"
                    >
                      Ranked
                    </button>
                  </div>
                  {view.ranked.on && (
                    <div className={styles.variantCaption}>
                      Ranked games adjust each player's ELO. All players must be signed in for it to
                      count — otherwise it just plays unranked.
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* A lobby nobody can join has nothing to make public — the server forces a vs-AI
                lobby private regardless (`QuickGameLobbyHandler.handleCreate`). */}
            {view.invitable && (
            <div className={styles.settingsRow}>
              <span className={styles.settingsLabel}>Visibility</span>
              <div className={styles.settingsButtons}>
                <button
                  type="button"
                  onClick={() => commands.setPublic(false)}
                  className={`${styles.settingsButton} ${!view.isPublic ? styles.settingsButtonActive : ''}`}
                >
                  Private
                </button>
                <button
                  type="button"
                  onClick={() => commands.setPublic(true)}
                  className={`${styles.settingsButton} ${view.isPublic ? styles.settingsButtonActive : ''}`}
                >
                  Public
                </button>
              </div>
            </div>
            )}

            {/* Only a quick vs-AI lobby has an AI seat whose deck is the host's to choose.
                Momir Basic hands every seat the same fixed 60 basics, so there is nothing to pick. */}
            {view.kind === 'QUICK' && quickLobby?.vsAi && !isMomir && (
              <AiOpponentRow
                aiDeck={quickLobby.aiDeck ?? null}
                format={quickLobby.format ?? null}
                disabled={view.you?.tone === 'ready'}
                source={aiSource}
                onSourceChange={setAiSource}
              />
            )}

            {lobbyState && view.kind === 'TOURNAMENT' && (
              <TournamentLobbySettings view={view} lobbyState={lobbyState} />
            )}
          </div>
        )}

        {/* Deck section. The two kinds genuinely differ here: a quick lobby submits as you pick
            (and un-readies you when you change your mind), while a premade tournament lobby has an
            explicit Submit the host waits on. */}
        {view.kind === 'TOURNAMENT' && lobbyState && view.isWaiting &&
          lobbyState.settings.format === 'PREMADE_DECKS' && (
            <PremadeDeckPickerPanel lobbyState={lobbyState} playerId={playerId} />
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
          />
        )}

        <div className={styles.playerListPanel}>
          <div className={styles.playerListHeader}>
            <span className={styles.playerListTitle}>Players</span>
            <span className={styles.playerCount}>{view.players.length} / {view.maxPlayers}</span>
          </div>
          {view.players.map((player, i) => (
            <div
              key={player.playerId}
              className={styles.playerRow}
              style={{ borderBottom: i < view.players.length - 1 ? undefined : 'none' }}
            >
              <div className={styles.playerInfo}>
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
          {view.canAddAi && (
            <button onClick={commands.addAi} className={styles.addAiButton}>+ Add AI Player</button>
          )}
        </div>

        <div className={styles.actionsRow}>
          {view.primaryAction && (
            <button
              type="button"
              onClick={commands.runPrimary}
              disabled={view.primaryAction.disabled}
              title={view.primaryAction.reason ?? ''}
              className={styles.startButton}
            >
              {view.primaryAction.label}
            </button>
          )}
          <button onClick={commands.leave} className={styles.leaveButton} type="button">Leave</button>
        </div>

        {view.isWaiting && !view.isHost && view.startModel === 'HOST_START' && (
          <p className={styles.waitingHint}>Waiting for host to start the game...</p>
        )}
      </div>

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
}: {
  youSetCode: string | null
  format: string | null
  disabled: boolean
  tab: DeckPickerTab | undefined
  onTabChange: (tab: DeckPickerTab) => void
  onValidityChange: (valid: boolean) => void
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
    />
  )
}

/**
 * Embedded deck picker for the Premade Decks tournament format. Each player picks and submits
 * their deck right in the lobby; the host can only start once everybody has.
 */
function PremadeDeckPickerPanel({
  lobbyState,
  playerId,
}: {
  lobbyState: LobbyState
  playerId: string | null
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
