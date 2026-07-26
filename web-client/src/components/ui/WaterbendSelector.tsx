import { useMemo } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import { useViewingPlayer } from '@/store/selectors'
import { applyManaPoolToCost, totalManaNeeded } from '@/utils/manaCost'
import { ManaSymbol } from './ManaSymbols'

/**
 * Parse a mana cost string into individual symbols. e.g. "{2}{W}" -> ["2", "W"].
 */
function parseManaCost(manaCost: string): string[] {
  const symbols: string[] = []
  const regex = /\{([^}]+)\}/g
  let match
  while ((match = regex.exec(manaCost)) !== null) {
    symbols.push(match[1]!)
  }
  return symbols
}

/**
 * Waterbend pays only generic mana — each tapped artifact/creature removes {1} of generic.
 */
function reduceGenericBy(symbols: string[], count: number): string[] {
  const remaining = [...symbols]
  for (let i = 0; i < count; i++) {
    const idx = remaining.findIndex((s) => /^\d+$/.test(s))
    if (idx < 0) break
    const value = parseInt(remaining[idx]!, 10)
    if (value > 1) remaining[idx] = String(value - 1)
    else remaining.splice(idx, 1)
  }
  return remaining
}

function totalManaAvailable(
  sources: readonly { entityId?: string; manaAmount?: number }[] | undefined | null,
  excludedIds: ReadonlySet<string> = new Set(),
): number {
  if (!sources) return 0
  let total = 0
  for (const s of sources) {
    if (s.entityId && excludedIds.has(s.entityId)) continue
    total += s.manaAmount ?? 1
  }
  return total
}

/**
 * Compact floating HUD bar for Waterbend (Avatar: The Last Airbender). Mirrors the Convoke
 * selector but generic-only: each tapped artifact/creature pays {1}. Permanents are selected
 * directly on the battlefield; this bar shows progress and confirm/cancel.
 */
export function WaterbendSelector() {
  const waterbendSelectionState = useGameStore((state) => state.waterbendSelectionState)
  const cancelWaterbendSelection = useGameStore((state) => state.cancelWaterbendSelection)
  const confirmWaterbendSelection = useGameStore((state) => state.confirmWaterbendSelection)
  const viewingPlayer = useViewingPlayer()
  const manaPool = viewingPlayer?.manaPool

  const originalSymbols = useMemo(() => {
    if (!waterbendSelectionState) return []
    return parseManaCost(waterbendSelectionState.manaCost)
  }, [waterbendSelectionState?.manaCost])

  const remainingSymbols = useMemo(() => {
    if (!waterbendSelectionState) return []
    return reduceGenericBy(originalSymbols, waterbendSelectionState.selectedPermanents.length)
  }, [originalSymbols, waterbendSelectionState?.selectedPermanents])

  // Conditional mana counts only where the server judged it eligible for this payment.
  const eligibleRestricted = waterbendSelectionState?.actionInfo.eligibleRestrictedMana

  const symbolsAfterPool = useMemo(
    () => applyManaPoolToCost(remainingSymbols, manaPool, eligibleRestricted),
    [remainingSymbols, manaPool, eligibleRestricted],
  )

  const tappedIds = useMemo(
    () => new Set(waterbendSelectionState?.selectedPermanents ?? []),
    [waterbendSelectionState?.selectedPermanents],
  )

  if (!waterbendSelectionState) return null

  const { cardName, selectedPermanents, actionInfo, maxTaps } = waterbendSelectionState

  const manaNeeded = totalManaNeeded(symbolsAfterPool)
  const manaFromSources = totalManaAvailable(actionInfo.availableManaSources, tappedIds)
  const canAfford = manaNeeded <= manaFromSources

  return (
    <div style={styles.bar}>
      <span style={styles.label}>
        {/* Card name first, then the waterbend prompt in parentheses with the amount {N} rendered
            as a proper mana pip (not literal "{N}" text): e.g. "Ruinous Waterbending (waterbend {4})". */}
        <strong>{cardName}</strong>
        <span style={{ display: 'inline-flex', alignItems: 'center', marginLeft: 6 }}>
          <span style={{ marginRight: 3 }}>(waterbend</span>
          <ManaSymbol symbol={String(maxTaps)} size={16} />
          <span>)</span>
        </span>
      </span>
      <span style={styles.divider} />
      <span style={styles.costLabel}>Cost:</span>
      <div style={styles.manaSymbols}>
        {originalSymbols.map((symbol, i) => (
          <ManaSymbol key={i} symbol={symbol} size={18} />
        ))}
      </div>
      <span style={styles.arrow}>→</span>
      <div style={styles.manaSymbols}>
        {remainingSymbols.length > 0 ? (
          remainingSymbols.map((symbol, i) => <ManaSymbol key={i} symbol={symbol} size={18} />)
        ) : (
          <span style={styles.freeCast}>Free!</span>
        )}
      </div>
      <span style={styles.count}>({selectedPermanents.length} tapped)</span>
      <span style={styles.divider} />
      <button onClick={cancelWaterbendSelection} style={styles.cancelButton}>
        Cancel
      </button>
      <button
        onClick={canAfford ? confirmWaterbendSelection : undefined}
        style={canAfford ? styles.confirmButton : styles.confirmButtonDisabled}
      >
        Activate
      </button>
    </div>
  )
}

const styles: Record<string, React.CSSProperties> = {
  bar: {
    position: 'absolute',
    bottom: 12,
    left: '50%',
    transform: 'translateX(-50%)',
    display: 'flex',
    alignItems: 'center',
    gap: 10,
    padding: '10px 20px',
    backgroundColor: 'rgba(20, 30, 48, 0.95)',
    border: '2px solid #3a6a8a',
    borderRadius: 10,
    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.6)',
    zIndex: 1500,
    whiteSpace: 'nowrap',
  },
  label: { color: '#cce', fontSize: 14 },
  divider: { width: 1, height: 20, backgroundColor: '#3a6a8a' },
  costLabel: { color: '#88a', fontSize: 13 },
  manaSymbols: { display: 'flex', alignItems: 'center', gap: 3 },
  arrow: { color: '#668', fontSize: 14 },
  freeCast: { color: '#4caf50', fontWeight: 'bold', fontSize: 13 },
  count: { color: '#668', fontSize: 12 },
  cancelButton: {
    padding: '6px 14px', fontSize: 13, backgroundColor: '#444', color: '#fff',
    border: 'none', borderRadius: 6, cursor: 'pointer',
  },
  confirmButton: {
    padding: '6px 14px', fontSize: 13, backgroundColor: '#0088cc', color: '#fff',
    border: 'none', borderRadius: 6, cursor: 'pointer',
  },
  confirmButtonDisabled: {
    padding: '6px 14px', fontSize: 13, backgroundColor: '#333', color: '#666',
    border: 'none', borderRadius: 6, cursor: 'not-allowed',
  },
}
