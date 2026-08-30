import { describe, expect, it } from 'vitest'
import type { ClientCard, LegalActionInfo } from '@/types'
import { Keyword } from '@/types'
import { castOfferFace } from './castFace'

/** Lunarch Veteran // Luminous Phantom, as the server sends it while it sits in the graveyard. */
const veteran = (extra: Partial<ClientCard> = {}): ClientCard =>
  ({
    id: 'c1',
    name: 'Lunarch Veteran',
    manaCost: '{W}',
    typeLine: 'Creature — Human Cleric',
    oracleText: 'Whenever another creature enters, you gain 1 life.',
    imageUri: 'front.jpg',
    power: 1,
    toughness: 1,
    basePower: 1,
    baseToughness: 1,
    keywords: [],
    isDoubleFaced: true,
    currentFace: 'FRONT',
    backFaceName: 'Luminous Phantom',
    backFaceTypeLine: 'Creature — Spirit Cleric',
    backFaceOracleText: 'Whenever another creature dies, you gain 1 life.',
    backFaceImageUri: 'back.jpg',
    backFacePower: 1,
    backFaceToughness: 1,
    backFaceKeywords: [Keyword.FLYING],
    ...extra,
  }) as unknown as ClientCard

const disturbOffer: LegalActionInfo = {
  action: { type: 'CastSpell', cardId: 'c1' },
  actionType: 'CastWithDisturb',
  description: 'Cast Luminous Phantom (Disturb)',
  manaCostString: '{2}{W}',
  sourceZone: 'GRAVEYARD',
  castsTransformed: true,
} as unknown as LegalActionInfo

const flashbackOffer: LegalActionInfo = {
  action: { type: 'CastSpell', cardId: 'c1' },
  actionType: 'CastWithFlashback',
  description: 'Cast Lunarch Veteran (Flashback)',
  sourceZone: 'GRAVEYARD',
} as unknown as LegalActionInfo

describe('castOfferFace', () => {
  it('shows the back face when the server says the cast is transformed (CR 712.8c)', () => {
    const shown = castOfferFace(veteran(), [disturbOffer])
    expect(shown.name).toBe('Luminous Phantom')
    expect(shown.typeLine).toBe('Creature — Spirit Cleric')
    expect(shown.oracleText).toContain('dies')
    expect(shown.imageUri).toBe('back.jpg')
    expect(shown.currentFace).toBe('BACK')
    expect(shown.keywords).toEqual([Keyword.FLYING])
  })

  it('leaves the card alone when no offer casts it transformed', () => {
    const card = veteran()
    expect(castOfferFace(card, [flashbackOffer])).toBe(card)
    expect(castOfferFace(card, [])).toBe(card)
  })

  it('leaves the card alone when the offer belongs to a different card', () => {
    const card = veteran()
    const elsewhere = { ...disturbOffer, action: { type: 'CastSpell', cardId: 'c2' } } as LegalActionInfo
    expect(castOfferFace(card, [elsewhere])).toBe(card)
  })

  it('leaves a single-faced card alone even if something flags the cast', () => {
    const single = veteran({ backFaceName: null, backFaceImageUri: null })
    expect(castOfferFace(single, [disturbOffer])).toBe(single)
  })

  // The swap has to be reversible or pressing F on the hover preview flips to the face already
  // on screen — the printed front has to end up in the back-face slots.
  it('puts the printed front face into the back-face slots so the preview can still flip to it', () => {
    const shown = castOfferFace(veteran(), [disturbOffer])
    expect(shown.backFaceName).toBe('Lunarch Veteran')
    expect(shown.backFaceTypeLine).toBe('Creature — Human Cleric')
    expect(shown.backFaceImageUri).toBe('front.jpg')
    expect(shown.backFaceKeywords).toEqual([])
    expect(shown.isDoubleFaced).toBe(true)
  })

  // The back face's printed stats travel with it; showing them against the front's `basePower`
  // would paint the preview's "buffed" box on a card nothing has modified.
  it('carries the back face stats into both the current and the base P/T', () => {
    const shown = castOfferFace(veteran({ backFacePower: 2, backFaceToughness: 3 }), [disturbOffer])
    expect([shown.power, shown.toughness]).toEqual([2, 3])
    expect([shown.basePower, shown.baseToughness]).toEqual([2, 3])
  })

  // A disturb spell's mana value is still calculated from the front face's mana cost, and the
  // price of the offer itself rides on the legal action.
  it('keeps the front face mana cost', () => {
    expect(castOfferFace(veteran(), [disturbOffer]).manaCost).toBe('{W}')
  })
})
