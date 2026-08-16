import { useEffect, useMemo, useState } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import { usePlayer } from '@/store/selectors'
import type {
  AtomicBlockTaxManaAbilityOption,
  AtomicBlockTaxManaAbilityRef,
  AtomicBlockTaxManaAbilitySelection,
  SelectAtomicBlockTaxManaAbilitiesDecision,
} from '@/types'
import { AbilityText } from '../ui/ManaSymbols'
import { DraggableBanner } from './DraggableBanner'
import styles from './DecisionUI.module.css'

const optionKey = (ref: AtomicBlockTaxManaAbilityRef): string =>
  `${ref.sourceId}:${ref.printedManaAbilityIndex}`

/**
 * Exact-branch selector for a shared-team block tax. This deliberately uses local selection,
 * rather than the battlefield's entity-id selection state: two legal mana abilities can belong
 * to the same permanent, so an entity id alone would collapse distinct payment costs.
 */
export function AtomicBlockTaxManaAbilitySelectionUI({
  decision,
}: {
  decision: SelectAtomicBlockTaxManaAbilitiesDecision
}) {
  const submit = useGameStore((s) => s.submitAtomicBlockTaxManaAbilitiesDecision)
  const manaPool = usePlayer(decision.playerId)?.manaPool
  const autoPaySelections = useMemo(
    () => decision.autoPaySelections?.length
      ? decision.autoPaySelections
      : decision.autoPaySuggestion.map((ref) => ({ ref, chosenColor: null, taxPaymentColor: null, secondaryTapTargetId: null })),
    [decision.autoPaySelections, decision.autoPaySuggestion],
  )
  const suggestionKey = autoPaySelections.map((selection) => `${optionKey(selection.ref)}:${selection.chosenColor ?? ''}:${selection.taxPaymentColor ?? ''}:${selection.secondaryTapTargetId ?? ''}`).join(',')
  const [selectedKeys, setSelectedKeys] = useState<readonly string[]>([])
  const [chosenColors, setChosenColors] = useState<Readonly<Record<string, string>>>({})
  const [taxPaymentColors, setTaxPaymentColors] = useState<Readonly<Record<string, string>>>({})
  const [secondaryTapTargets, setSecondaryTapTargets] = useState<Readonly<Record<string, string>>>({})

  useEffect(() => {
    const available = new Set(decision.availableOptions.map((option) => optionKey(option.ref)))
    setSelectedKeys(
      autoPaySelections
        .map((selection) => optionKey(selection.ref))
        .filter((key) => available.has(key)),
    )
    setChosenColors(Object.fromEntries(
      autoPaySelections
        .filter((selection) => selection.chosenColor != null)
        .map((selection) => [optionKey(selection.ref), selection.chosenColor!]),
    ))
    setTaxPaymentColors(Object.fromEntries(
      autoPaySelections
        .filter((selection) => selection.taxPaymentColor != null)
        .map((selection) => [optionKey(selection.ref), selection.taxPaymentColor!]),
    ))
    setSecondaryTapTargets(Object.fromEntries(
      autoPaySelections
        .filter((selection) => selection.secondaryTapTargetId != null)
        .map((selection) => [optionKey(selection.ref), selection.secondaryTapTargetId!]),
    ))
  }, [decision.id, suggestionKey, decision.availableOptions, autoPaySelections])

  const selectedOptions = useMemo(
    () => decision.availableOptions.filter((option) => selectedKeys.includes(optionKey(option.ref))),
    [decision.availableOptions, selectedKeys],
  )
  const selectedSelections = useMemo<readonly AtomicBlockTaxManaAbilitySelection[]>(
    () => selectedOptions.map((option) => ({
      ref: option.ref,
      chosenColor: option.colorChoices.length === 0
        ? null
        : (chosenColors[optionKey(option.ref)] ?? option.colorChoices[0] ?? null),
      taxPaymentColor: option.taxPaymentColorChoices?.length
        ? (taxPaymentColors[optionKey(option.ref)] ?? option.taxPaymentColorChoices[0] ?? null)
        : null,
      secondaryTapTargetId: option.secondaryTapTargets?.length
        ? (secondaryTapTargets[optionKey(option.ref)] ?? option.secondaryTapTargets[0]!.entityId) as NonNullable<AtomicBlockTaxManaAbilitySelection['secondaryTapTargetId']>
        : null,
    })),
    [chosenColors, selectedOptions, secondaryTapTargets, taxPaymentColors],
  )
  // Block taxes are generic costs. The server remains authoritative, but keeping the Pay button
  // disabled until the visible choices plus already-floating mana cover that generic amount avoids
  // a round trip that can only be rejected.
  const requiredMana = Number.parseInt(decision.requiredCost.match(/\d+/)?.[0] ?? '0', 10)
  const floatingMana =
    (manaPool?.white ?? 0) +
    (manaPool?.blue ?? 0) +
    (manaPool?.black ?? 0) +
    (manaPool?.red ?? 0) +
    (manaPool?.green ?? 0) +
    (manaPool?.colorless ?? 0)
  const selectedMana = selectedOptions.reduce((total, option) => total + option.manaAmount, 0)
  // A Signet-like branch first spends its printed activation cost from mana that is already
  // floating. Its generated mana cannot retroactively pay that activation. Count the net
  // available pool and require the pre-existing pool to cover every selected activation.
  const activationMana = selectedOptions.reduce((total, option) => {
    const generic = Number.parseInt(option.activationManaCost?.match(/\d+/)?.[0] ?? '0', 10)
    return total + generic
  }, 0)
  const isCovered = floatingMana >= activationMana && floatingMana - activationMana + selectedMana >= requiredMana

  const toggle = (option: AtomicBlockTaxManaAbilityOption) => {
    const key = optionKey(option.ref)
    setSelectedKeys((current) => current.includes(key)
      ? current.filter((entry) => entry !== key)
      // A single permanent can pay through exactly one branch in this atomic transaction.
      : [...current.filter((entry) => entry.split(':')[0] !== option.ref.sourceId), key])
  }

  const confirm = () => submit(selectedSelections, false)
  const autoPay = () => submit([], true)
  const decline = () => submit([], false, true)

  return (
    <DraggableBanner className={styles.sideBannerSelection}>
      <div className={styles.bannerTitleSelection}>
        {decision.canDecline ? 'Pay block tax?' : 'Pay block tax'}
      </div>
      <div className={styles.hint}>
        <AbilityText text={decision.prompt} size={13} />
      </div>
      <div className={styles.hint}>
        Choose mana abilities for {decision.requiredCost}. Each row is one exact ability branch.
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', width: '100%', gap: 5 }}>
        {decision.availableOptions.map((option) => {
          const key = optionKey(option.ref)
          const selected = selectedKeys.includes(key)
          return (
            <button
              key={key}
              type="button"
              onClick={() => toggle(option)}
              aria-pressed={selected}
              style={{
                border: selected ? '2px solid var(--color-target)' : '1px solid var(--border-card)',
                background: selected ? 'rgba(120, 220, 140, 0.16)' : 'var(--bg-surface)',
                color: 'var(--text-primary)',
                borderRadius: 6,
                padding: '6px 8px',
                textAlign: 'left',
                cursor: 'pointer',
              }}
            >
              <strong>{option.sourceName}</strong>
              <div style={{ fontSize: 12, opacity: 0.9 }}>
                <AbilityText text={option.description} size={12} />
              </div>
              {option.colorChoices.length > 0 && selected && (
                <label className={styles.effectHint}>
                  Color
                  <select
                    value={chosenColors[key] ?? option.colorChoices[0]}
                    onClick={(event) => event.stopPropagation()}
                    onChange={(event) => setChosenColors((current) => ({ ...current, [key]: event.target.value }))}
                  >
                    {option.colorChoices.map((color) => <option key={color} value={color}>{color}</option>)}
                  </select>
                </label>
              )}
              {(option.taxPaymentColorChoices?.length ?? 0) > 0 && selected && (
                <label className={styles.effectHint}>
                  Tax mana
                  <select
                    value={taxPaymentColors[key] ?? option.taxPaymentColorChoices![0]}
                    onClick={(event) => event.stopPropagation()}
                    onChange={(event) => setTaxPaymentColors((current) => ({ ...current, [key]: event.target.value }))}
                  >
                    {option.taxPaymentColorChoices!.map((color) => <option key={color} value={color}>{color}</option>)}
                  </select>
                </label>
              )}
              {(option.secondaryTapTargets?.length ?? 0) > 0 && selected && (
                <label className={styles.effectHint}>
                  Tap creature
                  <select
                    value={secondaryTapTargets[key] ?? option.secondaryTapTargets![0]?.entityId}
                    onClick={(event) => event.stopPropagation()}
                    onChange={(event) => setSecondaryTapTargets((current) => ({ ...current, [key]: event.target.value }))}
                  >
                    {option.secondaryTapTargets!.map((target) => <option key={target.entityId} value={target.entityId}>{target.name}</option>)}
                  </select>
                </label>
              )}
              {option.requiresSacrificeSelf && (
                <div className={styles.effectHint}>Will sacrifice this permanent</div>
              )}
            </button>
          )
        })}
      </div>
      <div className={styles.hint}>
        {selectedSelections.length} ability branch{selectedSelections.length === 1 ? '' : 'es'} selected
        {floatingMana > 0 && `; ${floatingMana} mana already floating`}
      </div>
      <div className={styles.buttonContainerSmall}>
        <button onClick={autoPay} className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}>
          Auto Pay
        </button>
        <button
          onClick={confirm}
          disabled={!isCovered}
          className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}
        >
          Pay ({selectedSelections.length})
        </button>
        {decision.canDecline && (
          <button onClick={decline} className={`${styles.confirmButton} ${styles.confirmButtonSmall}`}>
            Decline
          </button>
        )}
      </div>
    </DraggableBanner>
  )
}
