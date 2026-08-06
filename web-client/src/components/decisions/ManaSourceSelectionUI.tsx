import { useEffect, useMemo } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import { usePlayer } from '@/store/selectors'
import type { DecisionSelectionState } from '@/store/slices'
import type {
  ClientManaPool,
  EntityId,
  ManaSourceOption,
  SelectManaSourcesDecision,
} from '@/types'
import { parseManaCost } from '@/utils/manaCost'
import { AbilityText, ManaSymbol } from '../ui/ManaSymbols'
import { DraggableBanner } from './DraggableBanner'
import styles from './DecisionUI.module.css'

// Server serializes Color enums by name ("BLACK"), but cost symbols use pip letters ("B").
const COLOR_NAME_TO_PIP: Record<string, string> = {
  WHITE: 'W', BLUE: 'U', BLACK: 'B', RED: 'R', GREEN: 'G',
}

const toPip = (color: string): string => COLOR_NAME_TO_PIP[color] ?? color

interface PipCoverage {
  symbol: string
  /** Covered by mana already floating in the pool. */
  floating: boolean
  /** Covered by a source the player has selected but not yet tapped. */
  pending: boolean
}

/**
 * Works out which pips of the cost are already covered, and by what.
 *
 * Floating mana is applied first (it is real, and the resumers spend the pool before tapping
 * anything), then the selected sources. Mirrors the engine's solver well enough for a UI readout —
 * the server still re-solves on submit. Skips X (variable). `extraGeneric` folds in non-mana
 * payment: each tapped Waterbend permanent pays {1} generic.
 */
const COLOR_OR_COLORLESS = new Set(['W', 'U', 'B', 'R', 'G', 'C'])

/**
 * The colours a pip will accept. A hybrid pays with *either* half (CR 107.4d), so `{W/B}` accepts
 * white or black; a monocolour hybrid like `{2/W}` accepts white here and its generic half in the
 * generic pass. Phyrexian `{W/P}` keeps its colour half (the life option isn't paid from sources).
 */
export function pipColorOptions(symbol: string): string[] {
  return symbol.split('/').filter((part) => COLOR_OR_COLORLESS.has(part))
}

/** The generic amount a pip can be paid with, if any: `3` -> 3, `2/W` -> 2, `W` -> null. */
export function pipGenericAmount(symbol: string): number | null {
  for (const part of symbol.split('/')) {
    const parsed = parseInt(part, 10)
    if (!isNaN(parsed)) return parsed
  }
  return null
}

export function computeCoverage(
  costSymbols: readonly string[],
  pool: ClientManaPool | null,
  selectedIds: readonly EntityId[],
  availableSources: readonly ManaSourceOption[],
  extraGeneric = 0,
): PipCoverage[] {
  const pips: PipCoverage[] = costSymbols.map((symbol) => ({ symbol, floating: false, pending: false }))

  const floatingByColor: Record<string, number> = {
    W: pool?.white ?? 0,
    U: pool?.blue ?? 0,
    B: pool?.black ?? 0,
    R: pool?.red ?? 0,
    G: pool?.green ?? 0,
    C: pool?.colorless ?? 0,
  }

  // Pass 1 — floating mana against coloured pips it exactly matches.
  for (const pip of pips) {
    const paidWith = pipColorOptions(pip.symbol).find((color) => (floatingByColor[color] ?? 0) > 0)
    if (paidWith !== undefined) {
      floatingByColor[paidWith] = (floatingByColor[paidWith] ?? 0) - 1
      pip.floating = true
    }
  }

  // Pass 2 — selected sources against the coloured pips still open.
  const sourceById = new Map(availableSources.map((s) => [s.entityId, s]))
  const flexibleSources: ManaSourceOption[] = []
  for (const id of selectedIds) {
    const source = sourceById.get(id)
    if (!source) continue
    const colors = (source.producesColors ?? []).map(toPip)
    const match = pips.find(
      (pip) =>
        !pip.floating &&
        !pip.pending &&
        pipColorOptions(pip.symbol).some((option) => colors.includes(option)),
    )
    if (match) match.pending = true
    else flexibleSources.push(source)
  }

  // Pass 3 — whatever is left (leftover floating, sources that matched no coloured pip, Waterbend
  // taps) pays generic pips, cheapest first.
  let leftoverFloating = Object.values(floatingByColor).reduce((a, b) => a + b, 0)
  let leftoverPending = flexibleSources.length + extraGeneric
  for (const pip of pips) {
    if (pip.floating || pip.pending) continue
    const amount = pipGenericAmount(pip.symbol)
    if (amount === null) continue
    if (leftoverFloating >= amount) {
      leftoverFloating -= amount
      pip.floating = true
    } else if (leftoverFloating + leftoverPending >= amount) {
      leftoverPending -= amount - leftoverFloating
      leftoverFloating = 0
      pip.pending = true
    }
  }

  return pips
}

/** X is chosen elsewhere, so an X pip never blocks the Pay button. */
const isCovered = (pip: PipCoverage) => pip.floating || pip.pending || pip.symbol === 'X'

/**
 * Payment UI for a [SelectManaSourcesDecision] — the engine asking one player for mana outside of
 * casting a spell (ward, "you may pay {B}", an attack tax, a draw replacement).
 *
 * There are two ways to produce the mana, and the banner has to make both discoverable:
 *  1. **Click a highlighted source.** The engine pre-computed a menu of `{T}`-shaped sources; they
 *     light up on the battlefield and are tapped when the player confirms.
 *  2. **Activate a mana ability from a permanent's menu.** CR 605.3a allows this whenever a rule or
 *     effect asks for a mana payment, and it is the only route for anything the solver can't model
 *     — Ashnod's Altar, a Forage sub-cost, an ability with no `{T}` in its activation cost. That
 *     mana lands in the pool immediately, so the readout below counts it as already paid.
 */
export function ManaSourceSelectionUI({
  decision,
}: {
  decision: SelectManaSourcesDecision
}) {
  const startDecisionSelection = useGameStore((s) => s.startDecisionSelection)
  const decisionSelectionState = useGameStore((s) => s.decisionSelectionState)
  const cancelDecisionSelection = useGameStore((s) => s.cancelDecisionSelection)
  const submitManaSourcesDecision = useGameStore((s) => s.submitManaSourcesDecision)
  const manaPool = usePlayer(decision.playerId)?.manaPool ?? null

  const waterbendPermanents = decision.waterbendPermanents ?? []
  const waterbendIds = useMemo(
    () => new Set(waterbendPermanents.map((p) => p.entityId)),
    [waterbendPermanents],
  )

  // Start decision selection state when this component mounts. Both mana sources and
  // Waterbend-eligible permanents are clickable on the battlefield; they're partitioned on submit.
  //
  // Re-runs on `autoPaySuggestion` as well as on a new decision id. Activating a mana ability
  // mid-payment re-raises the *same* decision, refreshed: the source just tapped is gone from
  // `availableSources` and the suggestion now covers only what the new floating mana doesn't. That
  // is the signal that the board moved under the player, so the selection is re-seeded from it —
  // otherwise a pre-selected land stays ticked and Pay taps it on top of mana already in the pool.
  const suggestionKey = decision.autoPaySuggestion.join(',')
  useEffect(() => {
    const validOptions = [
      ...decision.availableSources.map((s) => s.entityId),
      ...waterbendPermanents.map((p) => p.entityId),
    ]
    const selectionState: DecisionSelectionState = {
      decisionId: decision.id,
      validOptions,
      selectedOptions: decision.autoPaySuggestion.filter((id) => validOptions.includes(id)),
      minSelections: 1,
      maxSelections: validOptions.length,
      prompt: decision.prompt,
    }
    startDecisionSelection(selectionState)

    return () => {
      cancelDecisionSelection()
    }
  }, [decision.id, suggestionKey])

  const selectedOptions = decisionSelectionState?.selectedOptions

  // Partition the clicked permanents: mana sources vs Waterbend taps (each pays {1} generic).
  const selectedWaterbend = useMemo(
    () => (selectedOptions ?? []).filter((id) => waterbendIds.has(id)),
    [selectedOptions, waterbendIds],
  )
  const selectedManaSources = useMemo(
    () => (selectedOptions ?? []).filter((id) => !waterbendIds.has(id)),
    [selectedOptions, waterbendIds],
  )

  const sacrificedSources = useMemo(() => {
    if (!selectedOptions) return []
    const byId = new Map(decision.availableSources.map((s) => [s.entityId, s]))
    return selectedOptions
      .map((id) => byId.get(id))
      .filter((s): s is ManaSourceOption => !!s && !!s.requiresSacrifice)
  }, [selectedOptions, decision.availableSources])

  const costSymbols = useMemo(
    () => parseManaCost(decision.requiredCost),
    [decision.requiredCost],
  )
  const coverage = useMemo(
    () =>
      computeCoverage(
        costSymbols,
        manaPool,
        selectedManaSources,
        decision.availableSources,
        selectedWaterbend.length,
      ),
    [costSymbols, manaPool, selectedManaSources, selectedWaterbend, decision.availableSources],
  )
  const isCostCovered = coverage.every(isCovered)
  const floatingCoversAll = coverage.every((pip) => pip.floating || pip.symbol === 'X')

  const handleAutoPay = () => {
    submitManaSourcesDecision([], true)
    cancelDecisionSelection()
  }

  const handleConfirm = () => {
    if (!isCostCovered) return
    // A payment made entirely from mana the player floated themselves submits no sources; the
    // server distinguishes it from a refusal by the absence of the `declined` flag.
    submitManaSourcesDecision(selectedManaSources, false, selectedWaterbend)
    cancelDecisionSelection()
  }

  const handleDecline = () => {
    submitManaSourcesDecision([], false, [], true)
    cancelDecisionSelection()
  }

  const payLabel = floatingCoversAll && selectedManaSources.length === 0 ? 'Pay' : `Pay (${selectedManaSources.length})`

  return (
    <DraggableBanner className={styles.sideBannerSelection}>
      <div className={styles.bannerTitleSelection}>
        {decision.canDecline ? 'Pay cost?' : 'Pay cost'}
      </div>
      {decision.context.sourceName && (
        <div className={styles.hint}>
          <AbilityText text={decision.prompt} size={13} />
        </div>
      )}

      {/* Live readout: solid = already floating, outlined = will be tapped on Pay, dim = missing. */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 6, margin: '6px 0' }}>
        {coverage.map((pip, i) => (
          <span
            key={i}
            title={pip.floating ? 'Paid from your mana pool' : pip.pending ? 'Covered by a selected source' : 'Not yet covered'}
            style={{
              display: 'inline-flex',
              borderRadius: '50%',
              opacity: pip.floating ? 1 : pip.pending ? 0.85 : 0.3,
              filter: isCovered(pip) ? 'none' : 'grayscale(70%)',
              boxShadow: pip.pending ? '0 0 0 2px rgba(120, 220, 140, 0.9)' : 'none',
              transition: 'opacity 0.15s, filter 0.15s, box-shadow 0.15s',
            }}
          >
            <ManaSymbol symbol={pip.symbol} size={20} />
          </span>
        ))}
      </div>

      <div className={styles.hint}>
        {isCostCovered ? 'Cost covered — press Pay.' : 'Click a highlighted source to tap it.'}
      </div>
      {/* Always visible: the escape hatch for costs the highlighted menu can't cover. */}
      <div className={styles.hint} style={{ opacity: 0.7, fontSize: 11 }}>
        You can also click any permanent to use its mana ability.
      </div>
      {waterbendPermanents.length > 0 && (
        <div className={styles.hint}>
          <AbilityText
            text="Waterbend: tap artifacts/creatures you control to pay {1} each."
            size={12}
          />
          {selectedWaterbend.length > 0 && (
            <div>{selectedWaterbend.length} tapped for Waterbend</div>
          )}
        </div>
      )}
      {sacrificedSources.length > 0 && (
        <div className={styles.effectHint}>
          Will sacrifice: {sacrificedSources.map((s) => s.name).join(', ')}
        </div>
      )}

      <div className={styles.buttonContainerSmall}>
        {!decision.canDecline && (
          <button
            onClick={handleAutoPay}
            className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}
          >
            Auto Pay
          </button>
        )}
        <button
          onClick={handleConfirm}
          disabled={!isCostCovered}
          className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}
        >
          {payLabel}
        </button>
        {decision.canDecline && (
          <button
            onClick={handleDecline}
            className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}
          >
            Decline
          </button>
        )}
      </div>
    </DraggableBanner>
  )
}
