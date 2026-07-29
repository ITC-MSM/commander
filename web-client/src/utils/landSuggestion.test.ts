import { describe, expect, it } from 'vitest'
import {
  suggestBasicLands,
  type BasicLand,
  type DeckEntry,
} from './landSuggestion'

const basics: BasicLand[] = [
  { name: 'Plains', color: 'W' },
  { name: 'Island', color: 'U' },
  { name: 'Swamp', color: 'B' },
  { name: 'Mountain', color: 'R' },
  { name: 'Forest', color: 'G' },
]

function spell(name: string, manaCost: string, cmc: number, count: number): DeckEntry {
  return {
    name,
    manaCost,
    cmc,
    count,
    isLand: false,
    isBasicLand: false,
    producedColors: [],
  }
}

describe('suggestBasicLands', () => {
  it('keeps the Limited default at 17 lands', () => {
    const result = suggestBasicLands({
      entries: [
        spell('White spell', '{1}{W}', 2, 12),
        spell('Blue spell', '{2}{U}', 3, 11),
      ],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(Object.values(result).reduce((sum, count) => sum + count, 0)).toBe(17)
  })

  it('reserves enough sources for an early double-pipped color', () => {
    const result = suggestBasicLands({
      entries: [
        spell('Red two-drop', '{1}{R}', 2, 12),
        spell('Blue double-pip', '{1}{U}{U}', 3, 2),
        spell('Colorless filler', '{3}', 3, 9),
      ],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(result.Island ?? 0).toBeGreaterThanOrEqual(7)
    expect((result.Mountain ?? 0) + (result.Island ?? 0)).toBe(17)
  })

  it('credits dual lands before allocating basics', () => {
    const dual: DeckEntry = {
      name: 'Azorius dual',
      manaCost: '',
      cmc: 0,
      count: 4,
      isLand: true,
      isBasicLand: false,
      producedColors: ['W', 'U'],
    }
    const result = suggestBasicLands({
      entries: [
        spell('White spell', '{1}{W}', 2, 10),
        spell('Blue spell', '{1}{U}', 2, 9),
        dual,
      ],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect((result.Plains ?? 0) + (result.Island ?? 0)).toBe(17)
    expect(result.Plains ?? 0).toBeGreaterThan(0)
    expect(result.Island ?? 0).toBeGreaterThan(0)
  })

  it('splits hybrid requirements between either payable color', () => {
    const result = suggestBasicLands({
      entries: [spell('Hybrid spell', '{1}{W/U}', 2, 23)],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(Math.abs((result.Plains ?? 0) - (result.Island ?? 0))).toBeLessThanOrEqual(1)
    expect((result.Plains ?? 0) + (result.Island ?? 0)).toBe(17)
  })
})
