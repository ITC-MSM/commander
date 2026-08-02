import { describe, expect, it } from 'vitest'
import { getArchetypesForSets } from './SetSynergiesOverlay'

describe('Wilds of Eldraine set synergies', () => {
  it('exposes all ten limited color-pair archetypes', () => {
    const archetypes = getArchetypesForSets(['WOE'])

    expect(archetypes.map((archetype) => archetype.name)).toEqual([
      'Tap Tempo',
      'Faeries',
      'Rats',
      'Ferocious Stompy',
      'Enchanted Creatures',
      'Bargain',
      'Spells',
      'Food',
      'Celebration Aggro',
      'Big Spells',
    ])
    expect(new Set(archetypes.map((archetype) => [...archetype.colors].sort().join('')))).toEqual(
      new Set(['UW', 'BU', 'BR', 'GR', 'GW', 'BW', 'RU', 'BG', 'RW', 'GU']),
    )
  })
})
