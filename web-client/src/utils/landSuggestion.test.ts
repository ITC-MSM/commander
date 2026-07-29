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

  it('puts every basic into the sole color of a mono-color Limited deck', () => {
    const result = suggestBasicLands({
      entries: [spell('Green spell', '{2}{G}', 3, 23)],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(result.Forest).toBe(17)
    expect(result.Plains).toBe(0)
    expect(result.Island).toBe(0)
    expect(result.Swamp).toBe(0)
    expect(result.Mountain).toBe(0)
  })

  it('keeps a late single-card splash smaller than the early main color', () => {
    const result = suggestBasicLands({
      entries: [
        spell('Red two-drop', '{1}{R}', 2, 18),
        spell('Black finisher', '{5}{B}', 6, 1),
        spell('Colorless filler', '{3}', 3, 4),
      ],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(result.Swamp).toBeGreaterThanOrEqual(3)
    expect(result.Mountain ?? 0).toBeGreaterThan(result.Swamp ?? 0)
    expect((result.Mountain ?? 0) + (result.Swamp ?? 0)).toBe(17)
  })

  it('scales the land total for a normal 60-card constructed curve', () => {
    const result = suggestBasicLands({
      entries: [
        spell('White two-drop', '{1}{W}', 2, 18),
        spell('Blue three-drop', '{2}{U}', 3, 18),
      ],
      availableBasics: basics,
      minDeckSize: 60,
    })

    expect(Object.values(result).reduce((sum, count) => sum + count, 0)).toBe(27)
    expect(Math.abs((result.Plains ?? 0) - (result.Island ?? 0))).toBeLessThanOrEqual(1)
  })

  it('lets two mana-producing nonlands replace one land when the deck has enough spells', () => {
    const manaRock: DeckEntry = {
      name: 'Mana rock',
      manaCost: '{2}',
      cmc: 2,
      count: 2,
      isLand: false,
      isBasicLand: false,
      producedColors: ['W', 'U'],
    }
    const result = suggestBasicLands({
      entries: [
        spell('White spell', '{1}{W}', 2, 11),
        spell('Blue spell', '{1}{U}', 2, 11),
        manaRock,
      ],
      availableBasics: basics,
      minDeckSize: 40,
    })

    expect(Object.values(result).reduce((sum, count) => sum + count, 0)).toBe(16)
  })

  it('uses an available fallback basic when the matching basic is unavailable', () => {
    const result = suggestBasicLands({
      entries: [spell('Blue spell', '{1}{U}', 2, 23)],
      availableBasics: [{ name: 'Wastes-like fallback', color: 'W' }],
      minDeckSize: 40,
    })

    expect(result['Wastes-like fallback']).toBe(17)
  })

  it('returns zeroes for an empty deck instead of inventing a mana base', () => {
    const result = suggestBasicLands({ entries: [], availableBasics: basics, minDeckSize: 40 })

    expect(result).toEqual({ Plains: 0, Island: 0, Swamp: 0, Mountain: 0, Forest: 0 })
  })
})
