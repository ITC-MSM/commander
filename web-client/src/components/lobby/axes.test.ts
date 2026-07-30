/**
 * Deck legality is a property of the deck, not of the table.
 *
 * This file used to assert the opposite: `legalityOptionsForTable(table)` filtered Commander, Brawl
 * and Standard Brawl out of the dropdown at a Two-Headed Giant table, and an effect in `LobbyAxes`
 * cleared the value if you reached it another way. Both were really the *Commander × 2HG* rule
 * wearing deck-legality's clothes, and they only made sense while picking commander legality
 * silently turned Commander rules on.
 *
 * With Rules as its own axis it doesn't: a 2HG lobby may require Commander-legal decks — singleton,
 * colour identity, Commander's card legality — and play them under 2HG's shared 30 life, which is a
 * perfectly ordinary house rule. So the filter is gone (the option list takes no table at all any
 * more, which is what this pins), and the one real conflict is stated once in `rulesTableBlock` —
 * see `rulesAxis.test.ts`.
 */
import { describe, expect, it } from 'vitest'
import { LEGALITY_OPTIONS, isCommanderDeckLegality } from './axes'

describe('deck legality options', () => {
  it('are one table-independent list that includes the commander formats', () => {
    const values = LEGALITY_OPTIONS.map((option) => option.value)

    expect(values).toContain('COMMANDER')
    expect(values).toContain('BRAWL')
    expect(values).toContain('STANDARD_BRAWL')
    expect(values).toContain('STANDARD')
  })

  it('name the commander-shaped ones, which default the Rules axis without being it', () => {
    expect(isCommanderDeckLegality('COMMANDER')).toBe(true)
    expect(isCommanderDeckLegality('BRAWL')).toBe(true)
    expect(isCommanderDeckLegality('STANDARD_BRAWL')).toBe(true)
    expect(isCommanderDeckLegality('STANDARD')).toBe(false)
    expect(isCommanderDeckLegality(null)).toBe(false)
  })
})
