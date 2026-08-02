import { describe, expect, it } from 'vitest'
import { getArchetypesForSets } from './SetSynergiesOverlay'

describe('Aetherdrift set synergies', () => {
  it('provides every two-color limited archetype', () => {
    const archetypes = getArchetypesForSets(['DFT'])

    expect(archetypes.map(({ name }) => name)).toEqual([
      'Artifact Value',
      'Artifact Bleeder',
      'Max Speed Aggro',
      'Exhaust Midrange',
      'Vehicles and Mounts Midrange',
      'Max Speed Attrition',
      'Discard Aggro',
      'Graveyard',
      'Vehicles and Mounts Aggro',
      'Exhaust Ramp',
    ])
    expect(new Set(archetypes.map(({ colors }) => colors.join('')))).toEqual(
      new Set(['WU', 'UB', 'BR', 'RG', 'GW', 'WB', 'UR', 'BG', 'RW', 'GU']),
    )
    expect(archetypes.every(({ keyCard }) => keyCard != null)).toBe(true)
  })
})
