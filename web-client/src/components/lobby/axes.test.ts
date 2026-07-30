import { describe, expect, it } from 'vitest'
import { legalityOptionsForTable } from './axes'

describe('legalityOptionsForTable', () => {
  it('does not offer Commander-shaped formats for Two-Headed Giant', () => {
    const values = legalityOptionsForTable('TWO_HEADED_GIANT').map((option) => option.value)

    expect(values).not.toContain('COMMANDER')
    expect(values).not.toContain('BRAWL')
    expect(values).not.toContain('STANDARD_BRAWL')
    expect(values).toContain('STANDARD')
  })

  it('keeps Commander-shaped formats available for Team vs Team', () => {
    const values = legalityOptionsForTable('TEAM_VS_TEAM').map((option) => option.value)

    expect(values).toContain('COMMANDER')
    expect(values).toContain('BRAWL')
    expect(values).toContain('STANDARD_BRAWL')
  })
})
