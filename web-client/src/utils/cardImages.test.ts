import { describe, expect, it } from 'vitest'
import { isBattleTypeLine, landscapeImageRotateDeg } from './cardImages'

describe('isBattleTypeLine', () => {
  it('matches the Battle card type', () => {
    expect(isBattleTypeLine('Battle — Siege')).toBe(true)
  })

  it('ignores everything after the em dash, so a subtype can never match', () => {
    // Contrived, but the point is that only the types half is examined.
    expect(isBattleTypeLine('Creature — Battle Angel')).toBe(false)
  })

  it('does not match a card whose name merely contains the word', () => {
    // Type lines carry types, not names — but the word-boundary check is what makes
    // "Battlefield"-ish types safe too.
    expect(isBattleTypeLine('Sorcery')).toBe(false)
    expect(isBattleTypeLine('Enchantment — Battlefield Forge')).toBe(false)
  })

  it('treats a missing type line as not a battle', () => {
    expect(isBattleTypeLine(null)).toBe(false)
    expect(isBattleTypeLine(undefined)).toBe(false)
    expect(isBattleTypeLine('')).toBe(false)
  })
})

describe('landscapeImageRotateDeg', () => {
  it('rotates split layouts', () => {
    expect(landscapeImageRotateDeg({ layout: 'SPLIT', typeLine: 'Enchantment — Room' })).toBe(90)
  })

  it('rotates battles, whose layout is TRANSFORM rather than SPLIT', () => {
    // The regression this guards: keying only on `layout === SPLIT` left every battle upright,
    // rendering it sideways in the draft / sealed / deckbuilder / cube hover previews.
    expect(landscapeImageRotateDeg({ layout: 'TRANSFORM', typeLine: 'Battle — Siege' })).toBe(90)
  })

  it('leaves ordinary portrait cards alone, including other transforming DFCs', () => {
    expect(landscapeImageRotateDeg({ layout: 'NORMAL', typeLine: 'Creature — Human Wizard' })).toBe(0)
    expect(landscapeImageRotateDeg({ layout: 'TRANSFORM', typeLine: 'Creature — Human Cleric' })).toBe(0)
    expect(landscapeImageRotateDeg(null)).toBe(0)
    expect(landscapeImageRotateDeg(undefined)).toBe(0)
  })
})
