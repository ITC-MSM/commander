/**
 * What *one* AI seat at a pod plays.
 *
 * The per-seat twin of {@link AiOpponentPanel}, which asks the same question of the quick lobby's
 * single AI. The two differ only in shape, and for a reason: a quick lobby has one AI and can
 * afford it a settings row plus a full-width deck picker, while a pod has up to five and cannot —
 * five inline pickers would bury the roster they belong to. So the roster row carries a summary
 * ("Auto", "Mono-Red Burn (60)") and the choice opens here, over one named seat.
 *
 * The three sources are `AiDeckSpec` on the server, and they mean the same things they do for a
 * quick game:
 *   - **Auto** — the server rolls it one, honouring the lobby's deck-legality axis.
 *   - **From sets** — same builders, pool pinned to sets you pick.
 *   - **Pick a deck** — an exact list, through the same {@link DeckPicker} you use for your own.
 *     Its `saved` / `examples` / `paste` tabs are three ways to reach a decklist and collapse into
 *     one `deck` spec on the wire; `random` is omitted because "From sets" already is that.
 *
 * Only reachable in a premade-decks lobby. Everywhere else the AI builds from the pool it was dealt
 * — that is the format working, not a gap — and the server refuses the message rather than
 * accepting a choice it would silently ignore.
 */
import { useCallback, useRef, useState } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { AiDeckSpec, AiDeckSpecView } from '@/types'
import { DeckPicker } from '../ui/DeckPicker'
import { SetIcon } from '../ui/SetIcon'
import { SetPickerModal } from '../ui/SetPickerModal'
import styles from '../ui/GameUI.module.css'

type Source = 'auto' | 'sets' | 'deck'

/** The row summary: what this seat is bringing, short enough to sit beside a name. */
export function aiDeckSummary(aiDeck: AiDeckSpecView | null | undefined): string {
  if (!aiDeck) return 'Auto'
  switch (aiDeck.kind) {
    case 'sets':
      return aiDeck.setCodes?.length ? aiDeck.setCodes.join(', ') : 'Auto'
    case 'deck':
      return aiDeck.cardCount ? `${aiDeck.label ?? 'Chosen deck'} (${aiDeck.cardCount})` : (aiDeck.label ?? 'Chosen deck')
    default:
      return 'Auto'
  }
}

export function LobbyAiDeckModal({
  playerId,
  playerName,
  aiDeck,
  format,
  onClose,
}: {
  playerId: string
  playerName: string
  /** The server's summary of this seat's current choice; re-hydrates the modal after a reconnect. */
  aiDeck: AiDeckSpecView | null
  /** The lobby's deck-legality restriction, upper-case, or null. */
  format: string | null
  onClose: () => void
}) {
  const setLobbyAiDeck = useGameStore((s) => s.setLobbyAiDeck)
  const availableSets = useGameStore((s) => s.availableSets)

  const [source, setSource] = useState<Source>((aiDeck?.kind as Source | undefined) ?? 'auto')
  const [setCodes, setSetCodes] = useState<readonly string[]>(aiDeck?.setCodes ?? [])
  const [showSetPicker, setShowSetPicker] = useState(false)
  const lastDeckKeyRef = useRef<string | null>(null)

  const pick = (next: Source) => {
    setSource(next)
    // Auto is complete the moment it is picked. The other two need a selection first, so they only
    // send once one exists — otherwise switching tabs would submit an empty spec to be rejected.
    if (next === 'auto') setLobbyAiDeck(playerId, { type: 'auto' })
    else if (next === 'sets' && setCodes.length > 0) setLobbyAiDeck(playerId, { type: 'sets', setCodes })
  }

  const toggleSet = (code: string) => {
    const next = setCodes.includes(code) ? setCodes.filter((c) => c !== code) : [...setCodes, code]
    setSetCodes(next)
    if (next.length > 0) setLobbyAiDeck(playerId, { type: 'sets', setCodes: next })
  }

  // Deduped like the human picker's submission path: DeckPicker re-emits its current deck on every
  // render, and each send costs a re-roll and a lobby broadcast.
  const handleDeckChange = useCallback(
    (deckList: Record<string, number>, commander?: string | null) => {
      const total = Object.values(deckList).reduce((a, b) => a + b, 0)
      if (total === 0) return
      const key = `${Object.entries(deckList).sort().map(([n, c]) => `${n}:${c}`).join('|')}|${commander ?? ''}`
      if (key === lastDeckKeyRef.current) return
      lastDeckKeyRef.current = key
      setLobbyAiDeck(
        playerId,
        {
          type: 'deck',
          deckList,
          label: 'Chosen deck',
          commander: commander ?? null,
        } satisfies AiDeckSpec,
      )
    },
    [playerId, setLobbyAiDeck],
  )

  return (
    <div className={styles.confirmBackdrop} role="dialog" aria-modal="true" onClick={onClose}>
      <div
        className={styles.confirmPanel}
        style={{ maxWidth: '640px', width: '92vw' }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className={styles.confirmTitle}>{playerName}’s deck</div>
        <p className={styles.confirmBody}>
          What this AI brings to the table. Each AI seat is chosen separately, so they need not all
          play the same thing.
        </p>

        <div className={styles.settingsButtons} style={{ marginBottom: '0.75rem' }}>
          <SourceButton active={source === 'auto'} onClick={() => pick('auto')}>Auto</SourceButton>
          <SourceButton active={source === 'sets'} onClick={() => pick('sets')}>From sets</SourceButton>
          <SourceButton active={source === 'deck'} onClick={() => pick('deck')}>Pick a deck</SourceButton>
        </div>

        {source === 'auto' && (
          <div className={styles.aiDeckSectionHint}>
            The server rolls it a deck{format ? ` legal in ${format.toLowerCase()}` : ''}, fresh each game.
          </div>
        )}

        {source === 'sets' && (
          <div>
            <div className={styles.aiDeckSectionHint}>
              Same builders, card pool pinned to what you choose. Clearing every set means Auto.
            </div>
            <div className={styles.settingsButtons} style={{ marginTop: '0.5rem', flexWrap: 'wrap' }}>
              {setCodes.map((code) => (
                <button
                  key={code}
                  type="button"
                  onClick={() => toggleSet(code)}
                  className={`${styles.settingsButton} ${styles.settingsButtonActive}`}
                  title="Remove this set"
                >
                  <SetIcon code={code} /> {code}
                </button>
              ))}
              <button type="button" onClick={() => setShowSetPicker(true)} className={styles.settingsButton}>
                + Choose sets
              </button>
            </div>
          </div>
        )}

        {source === 'deck' && (
          <div className={styles.aiDeckSection}>
            <div className={styles.aiDeckSectionHeader}>
              <span className={styles.aiDeckSectionTitle}>Not your own deck — this one is {playerName}’s</span>
              <span className={styles.aiDeckSectionHint}>Checked against the lobby format when you pick it.</span>
            </div>
            <DeckPicker
              onDeckChange={handleDeckChange}
              availableSets={availableSets}
              format={format}
              tabs={['saved', 'examples', 'paste']}
            />
          </div>
        )}

        <div className={styles.confirmActions}>
          <button type="button" onClick={onClose} className={styles.startButton}>Done</button>
        </div>
      </div>

      {showSetPicker && (
        <SetPickerModal
          sets={availableSets}
          selectedCodes={setCodes}
          onToggleSet={toggleSet}
          onClose={() => setShowSetPicker(false)}
        />
      )}
    </div>
  )
}

function SourceButton({
  active,
  onClick,
  children,
}: {
  active: boolean
  onClick: () => void
  children: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`${styles.settingsButton} ${active ? styles.settingsButtonActive : ''}`}
    >
      {children}
    </button>
  )
}
