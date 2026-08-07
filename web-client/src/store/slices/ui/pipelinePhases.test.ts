import { describe, it, expect } from 'vitest'
import { computePhases } from './pipelinePhases'
import type { LegalActionInfo } from '@/types/messages'

/**
 * Minimal CastSpell LegalActionInfo factory — only the fields computePhases reads matter.
 */
function castAction(over: Record<string, unknown>): LegalActionInfo {
  return {
    actionType: 'CastSpellModal',
    description: 'Cast Test',
    action: { type: 'CastSpell', playerId: 'p1', cardId: 'c1' },
    ...over,
  } as unknown as LegalActionInfo
}

describe('computePhases — choose-N modal', () => {
  it('plain choose-N modal (Spree) runs only the modalModes phase', () => {
    const info = castAction({
      modalEnumeration: {
        chooseCount: 3,
        minChooseCount: 1,
        allowRepeat: true,
        modes: [],
      },
    })
    expect(computePhases(info)).toEqual([{ type: 'modalModes' }])
  })

  it('"choose both if you blight" modal (Pyrrhic Strike) also collects the blight target', () => {
    // The engine forces every mode on the blight variant but only unlocks the extra modes
    // once the submitted action carries blightTargets. The client must therefore run a
    // costPayment phase to pick the creature to blight — otherwise the action submits with
    // no blight and the engine rejects it ("Too many modes chosen").
    const info = castAction({
      modalEnumeration: {
        chooseCount: 2,
        minChooseCount: 2,
        allowRepeat: false,
        modes: [],
      },
      additionalCostInfo: {
        costType: 'Blight',
        description: 'creature to blight',
        validBlightTargets: ['fodder1'],
        blightAmount: 2,
      },
    })
    expect(computePhases(info)).toEqual([{ type: 'modalModes' }, { type: 'costPayment' }])
  })
})

describe('computePhases — emerge sacrifice', () => {
  function emergeAction(): LegalActionInfo {
    return castAction({
      actionType: 'CastWithAlternativeCost',
      action: {
        type: 'CastSpell',
        playerId: 'p1',
        cardId: 'c1',
        useAlternativeCost: true,
        alternativeCostType: 'EMERGE',
      },
      manaCostString: '{5}{U}',
      additionalCostInfo: {
        costType: 'SacrificePermanent',
        description: 'a creature to sacrifice (its mana value reduces the emerge cost)',
        validSacrificeTargets: ['bear', 'ogre'],
        sacrificeCount: 1,
        costAfterSacrifice: { bear: '{3}{U}', ogre: '{U}' },
      },
      availableManaSources: [{ entityId: 'island', producesColors: ['U'] }],
    })
  }

  it('picks the sacrifice BEFORE manual mana-source selection, since it changes the cost owed', () => {
    // With auto-tap off the mana step would otherwise run first and price the cast against the
    // un-reduced emerge cost — the player would be asked to tap for {5}{U} and then discover the
    // sacrifice made it {U}.
    expect(computePhases(emergeAction(), { autoTapEnabled: false })).toEqual([
      { type: 'costPayment' },
      { type: 'manaSource' },
    ])
  })

  it('runs the sacrifice step exactly once when auto-tap handles the mana', () => {
    expect(computePhases(emergeAction(), { autoTapEnabled: true })).toEqual([
      { type: 'costPayment' },
    ])
  })
})
