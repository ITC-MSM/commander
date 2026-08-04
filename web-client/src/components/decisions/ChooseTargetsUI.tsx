import { useState } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import type { ChooseTargetsDecision, EntityId } from '@/types'
import { useResponsive } from '@/hooks/useResponsive.ts'
import { getPileTargetCards } from '@/utils/targeting.ts'
import { BattlefieldTargetingUI } from './BattlefieldTargetingUI'
import { GraveyardTargetingUI } from './GraveyardTargetingUI'

/**
 * Walks a ChooseTargetsDecision one target requirement at a time, routing **each** requirement to
 * the UI that can collect it, and submits every slot together once the last one is answered.
 *
 * The routing has to be per requirement, not per decision: a requirement whose legal targets all
 * live in a graveyard or exile pile needs the pile picker ([GraveyardTargetingUI]) because a pile
 * isn't clickable card-by-card on the board, while everything else is picked by clicking the board
 * ([BattlefieldTargetingUI]). The Spot, Living Portal's ETB has one of each — "exile up to one
 * target nonland permanent **and** up to one target nonland permanent card from a graveyard" — so
 * deciding once for the whole decision strands one of the two slots: the graveyard card would have
 * no clickable target at all, and a graveyard-first decision would submit slot 0 and silently drop
 * the rest.
 *
 * Player-only lone requirements are handled before this by PlayerTargetingUI (click a life orb).
 */
export function ChooseTargetsUI({ decision }: { decision: ChooseTargetsDecision }) {
  const gameState = useGameStore((s) => s.gameState)
  const submitTargetsDecision = useGameStore((s) => s.submitTargetsDecision)
  const responsive = useResponsive()

  const [currentReqIndex, setCurrentReqIndex] = useState(0)
  const [collectedTargets, setCollectedTargets] = useState<Record<number, readonly EntityId[]>>({})
  // Picks to pre-select because the player stepped Back into a requirement they'd already answered.
  const [restoredSelection, setRestoredSelection] = useState<readonly EntityId[]>([])

  const totalRequirements = decision.targetRequirements.length

  // Targets confirmed for other requirements can't be picked again (collectedTargets never holds
  // the current index — Back deletes it before stepping into it).
  const alreadySelected = Object.values(collectedTargets).flat()
  const legalTargets = (decision.legalTargets[currentReqIndex] ?? [])
    .filter((id) => !alreadySelected.includes(id))

  const handleComplete = (targets: readonly EntityId[]) => {
    const updatedTargets = { ...collectedTargets, [currentReqIndex]: targets }
    if (currentReqIndex + 1 < totalRequirements) {
      setCollectedTargets(updatedTargets)
      setRestoredSelection([])
      setCurrentReqIndex(currentReqIndex + 1)
    } else {
      submitTargetsDecision(updatedTargets)
    }
  }

  const handleBack = () => {
    // Step back to the previous requirement, restoring its confirmed picks so the player can
    // revise them. The current requirement's in-progress picks are discarded; its pool is
    // recomputed on re-confirm against the revised selection.
    if (currentReqIndex === 0) return
    const prevIndex = currentReqIndex - 1
    setRestoredSelection(collectedTargets[prevIndex] ?? [])
    const remaining = { ...collectedTargets }
    delete remaining[prevIndex]
    setCollectedTargets(remaining)
    setCurrentReqIndex(prevIndex)
  }

  const backProps = currentReqIndex > 0 ? { onBack: handleBack } : {}
  const pileCards = getPileTargetCards(legalTargets, gameState?.cards)

  if (pileCards) {
    return (
      <GraveyardTargetingUI
        key={currentReqIndex}
        decision={decision}
        graveyardCards={pileCards}
        responsive={responsive}
        requirementIndex={currentReqIndex}
        totalRequirements={totalRequirements}
        initialSelection={restoredSelection}
        onComplete={handleComplete}
        {...backProps}
      />
    )
  }

  return (
    <BattlefieldTargetingUI
      decision={decision}
      requirementIndex={currentReqIndex}
      totalRequirements={totalRequirements}
      legalTargets={legalTargets}
      initialSelection={restoredSelection}
      onComplete={handleComplete}
      {...backProps}
    />
  )
}
