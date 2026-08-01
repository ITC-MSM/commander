/**
 * The AI seat's deck, as a thing the host can actually choose.
 *
 * Before this the answer was fixed and invisible: the AI always played a 40-card sealed pool
 * opened from whatever set *you* had picked, and nothing in the lobby said so. Two consequences
 * this panel exists to fix — you couldn't test a matchup against a specific list, and a lobby with
 * a deck-format restriction applied it to one side of the table only.
 *
 * Three sources, matching `AiDeckSpec` on the server:
 *   - **Auto** — the server decides, honouring the lobby format.
 *   - **From sets** — same builders, card pool pinned to sets you choose (multi-select).
 *   - **Pick a deck** — an exact list, via the same {@link DeckPicker} you use for your own deck.
 *     Its `saved` / `examples` / `paste` tabs are three ways to arrive at a decklist, so all three
 *     collapse into one `deck` spec on the wire; the `random` tab is omitted because that is what
 *     "From sets" already is.
 *
 * The quick lobby opens these controls from the AI's player row. Keeping the source controls and
 * picker in one modal makes it unambiguous whose deck is being changed and keeps the roster as the
 * lobby's visual centre.
 */
import { useCallback, useRef, useState } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { AiDeckSpec, AiDeckSpecView, AvailableSet } from '@/types'
import { DeckPicker } from '../ui/DeckPicker'
import { SetIcon } from '../ui/SetIcon'
import { SetPickerModal } from '../ui/SetPickerModal'
import styles from '../ui/GameUI.module.css'

type Source = 'auto' | 'sets' | 'deck'

/** Commander-shape formats — the AI builds a singleton deck and picks its own commander for these. */
const COMMANDER_SHAPES = ['COMMANDER', 'BRAWL', 'STANDARD_BRAWL']

/**
 * Deck size for a commander shape, matching `DeckValidator`'s profiles. A lobby running Commander
 * rules with no legality restriction builds to paper Commander, which is the same 100.
 */
function commanderDeckSize(format: string | null): number {
  return format === 'STANDARD_BRAWL' ? 60 : 100
}

export type AiDeckSource = Source

/** Which source a lobby should start on, given the server's summary of the current choice. */
export function initialAiSource(aiDeck: AiDeckSpecView | null | undefined): Source {
  return (aiDeck?.kind as Source | undefined) ?? 'auto'
}

export function AiOpponentRow({
  aiDeck,
  format,
  commanderRules,
  disabled,
  source,
  onSourceChange,
}: {
  /** The server's summary of the current choice; re-hydrates the row after a reconnect. */
  aiDeck: AiDeckSpecView | null
  /** The lobby's deck-format restriction, upper-case, or null. */
  format: string | null
  /**
   * Whether the lobby's Rules axis says Commander. Asked separately from {@link format} because a
   * lobby can run Commander with no deck-legality restriction at all, and the AI still needs a
   * commander there — the same split `RandomDeckResolver` makes server-side.
   */
  commanderRules: boolean
  /** True once the host has readied up — the choice is locked in at that point. */
  disabled: boolean
  /** Selected source. Lifted to `LobbyScreen` so it can place {@link AiDeckSection} outside. */
  source: Source
  onSourceChange: (source: Source) => void
}) {
  const setAiDeck = useGameStore((s) => s.setQuickGameAiDeck)
  const availableSets = useGameStore((s) => s.availableSets)

  const [setCodes, setSetCodes] = useState<readonly string[]>(aiDeck?.setCodes ?? [])
  const [showSetPicker, setShowSetPicker] = useState(false)

  const isCommanderShape = commanderRules || (format !== null && COMMANDER_SHAPES.includes(format))
  const commanderSize = commanderDeckSize(format)

  const pick = (next: Source) => {
    onSourceChange(next)
    // Auto is complete the moment it's picked. The other two need a selection first, so they only
    // send once the host has actually chosen sets / a deck — otherwise clicking the tab would
    // submit an empty spec the server has to reject.
    if (next === 'auto') setAiDeck({ type: 'auto' })
    else if (next === 'sets' && setCodes.length > 0) setAiDeck({ type: 'sets', setCodes })
  }

  const toggleSet = (code: string) => {
    const next = setCodes.includes(code) ? setCodes.filter((c) => c !== code) : [...setCodes, code]
    setSetCodes(next)
    // An empty selection is the server's "treat as Auto", so send it rather than going quiet —
    // clearing the last chip should visibly fall back, not silently keep the old pool.
    setAiDeck({ type: 'sets', setCodes: next })
  }

  const addRandomSet = () => {
    const candidates = availableSets.filter((s: AvailableSet) => !s.extensionSet && !setCodes.includes(s.code))
    const chosen = candidates[Math.floor(Math.random() * candidates.length)]
    if (chosen) toggleSet(chosen.code)
  }

  const sourceButtons = (
    <div className={styles.settingsButtons}>
      <SourceButton active={source === 'auto'} disabled={disabled} onClick={() => pick('auto')}>
        Auto
      </SourceButton>
      <SourceButton active={source === 'sets'} disabled={disabled} onClick={() => pick('sets')}>
        From sets
      </SourceButton>
      <SourceButton active={source === 'deck'} disabled={disabled} onClick={() => pick('deck')}>
        Pick a deck
      </SourceButton>
    </div>
  )

  return (
    <div className={styles.settingsRow} data-testid="ai-opponent-panel">
      <span className={styles.settingsLabel}>AI deck</span>
      <div className={styles.variantGroup}>
        {sourceButtons}

        {source === 'auto' && (
          <div className={styles.variantCaption}>
            {isCommanderShape
              ? `The server picks the AI a commander and builds it a ${commanderSize}-card singleton deck in that commander's colours.`
              : format
                ? `The server builds the AI a 60-card ${format.replace('_', ' ').toLowerCase()}-legal deck.`
                : 'The server opens eight boosters from your set and auto-builds the AI a 40-card deck.'}
          </div>
        )}

        {source === 'sets' && (
          <>
            <div className={styles.setChips}>
              {setCodes.map((code) => {
                const set = availableSets.find((s: AvailableSet) => s.code === code)
                return (
                  <span key={code} className={`${styles.setChip} ${set?.partial ? styles.setChipPartial : ''}`}>
                    <SetIcon code={code} className={styles.setChipIcon} />
                    <span className={styles.setChipName}>{set?.name ?? code}</span>
                    {!disabled && (
                      <button
                        type="button"
                        className={styles.setChipRemove}
                        onClick={() => toggleSet(code)}
                        aria-label={`Remove ${set?.name ?? code}`}
                      >
                        ×
                      </button>
                    )}
                  </span>
                )
              })}
              <button
                type="button"
                className={styles.addSetsButton}
                onClick={() => setShowSetPicker(true)}
                disabled={disabled}
              >
                {setCodes.length === 0 ? 'Choose sets' : '+ Add'}
              </button>
            </div>
            <div className={styles.variantCaption}>
              {setCodes.length === 0
                ? 'Pick one or more sets — until you do, the AI falls back to Auto.'
                : isCommanderShape
                  ? `The AI's commander and its ${commanderSize}-card deck are drawn from these sets.`
                  : format
                    ? `Only ${format.replace('_', ' ').toLowerCase()}-legal cards from these sets are used.`
                    : 'Eight boosters are opened across these sets and auto-built into a deck.'}
            </div>
          </>
        )}

      </div>

      {showSetPicker && (
        <SetPickerModal
          sets={availableSets}
          selectedCodes={setCodes}
          onToggleSet={toggleSet}
          onClose={() => setShowSetPicker(false)}
          onSelectRandom={addRandomSet}
          title="Sets for the AI's deck"
        />
      )}
    </div>
  )
}

function SourceButton({
  active,
  disabled,
  onClick,
  children,
}: {
  active: boolean
  disabled: boolean
  onClick: () => void
  children: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={`${styles.settingsButton} ${active ? styles.settingsButtonActive : ''}`}
    >
      {children}
    </button>
  )
}

/** The exact-deck branch inside the AI seat's modal. */
export function AiDeckSection({
  format,
  disabled,
}: {
  format: string | null
  disabled: boolean
}) {
  const setAiDeck = useGameStore((s) => s.setQuickGameAiDeck)
  const availableSets = useGameStore((s) => s.availableSets)
  const lastDeckKeyRef = useRef<string | null>(null)

  // Deduped like the human picker's submission path: DeckPicker re-emits its current deck on every
  // render, and each send costs a lobby broadcast.
  const handleDeckChange = useCallback(
    (deckList: Record<string, number>, commander?: string | null) => {
      const total = Object.values(deckList).reduce((a, b) => a + b, 0)
      if (total === 0) return
      const key = `${Object.entries(deckList).sort().map(([n, c]) => `${n}:${c}`).join('|')}|${commander ?? ''}`
      if (key === lastDeckKeyRef.current) return
      lastDeckKeyRef.current = key
      setAiDeck({
        type: 'deck',
        deckList,
        label: 'Chosen deck',
        commander: commander ?? null,
      } satisfies AiDeckSpec)
    },
    [setAiDeck],
  )

  return (
    <div className={styles.aiDeckSection} data-testid="ai-deck-section">
      <div className={styles.aiDeckSectionHeader}>
        <span className={styles.aiDeckSectionTitle}>The AI opponent’s deck</span>
        <span className={styles.aiDeckSectionHint}>
          Checked against the lobby format when you pick it.
        </span>
      </div>
      <DeckPicker
        onDeckChange={handleDeckChange}
        availableSets={availableSets}
        disabled={disabled}
        format={format}
        tabs={['saved', 'examples', 'paste']}
      />
    </div>
  )
}

/** Seat-scoped wrapper used by the quick lobby's AI player row. */
export function QuickAiDeckModal({
  playerName,
  aiDeck,
  format,
  commanderRules,
  disabled,
  source,
  onSourceChange,
  onClose,
}: {
  playerName: string
  aiDeck: AiDeckSpecView | null
  format: string | null
  commanderRules: boolean
  disabled: boolean
  source: AiDeckSource
  onSourceChange: (source: AiDeckSource) => void
  onClose: () => void
}) {
  return (
    <div className={styles.confirmBackdrop} role="dialog" aria-modal="true" onClick={onClose}>
      <div className={styles.deckPickerModal} onClick={(event) => event.stopPropagation()}>
        <div className={styles.deckPickerModalHeader}>
          <div>
            <div className={styles.confirmTitle}>{playerName}’s deck</div>
            <p className={styles.confirmBody}>Choose what this seat brings to the game.</p>
          </div>
          <button type="button" onClick={onClose} className={styles.deckPickerModalClose} aria-label="Close deck picker">
            ×
          </button>
        </div>
        <AiOpponentRow
          aiDeck={aiDeck}
          format={format}
          commanderRules={commanderRules}
          disabled={disabled}
          source={source}
          onSourceChange={onSourceChange}
        />
        {source === 'deck' && <AiDeckSection format={format} disabled={disabled} />}
        <div className={styles.confirmActions}>
          <button type="button" onClick={onClose} className={styles.startButton}>Done</button>
        </div>
      </div>
    </div>
  )
}
