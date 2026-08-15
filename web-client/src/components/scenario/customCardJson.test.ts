import { describe, expect, it } from 'vitest'
import { customCardImageUri, customCardName, customCardSummary } from './customCardJson'

const bear = JSON.stringify({
  name: 'Grizzly Bears',
  mana_cost: '{1}{G}',
  type_line: 'Creature — Bear',
  oracle_text: '',
  power: '2',
  toughness: '2',
  image_uris: { normal: 'https://cards.scryfall.io/normal/front/4/0/bear.jpg' },
})

describe('customCardSummary', () => {
  it('reads the art the pasted object carries, which is the art the game will show', () => {
    expect(customCardImageUri(bear)).toBe('https://cards.scryfall.io/normal/front/4/0/bear.jpg')
    expect(customCardSummary(bear)?.imageUri).toBe(
      'https://cards.scryfall.io/normal/front/4/0/bear.jpg',
    )
  })

  it('takes the front face of a multi-faced object, where the printed art lives', () => {
    const dfc = JSON.stringify({
      name: 'Front // Back',
      card_faces: [
        {
          name: 'Front',
          mana_cost: '{1}{R}',
          type_line: 'Creature — Human',
          image_uris: { normal: 'front.jpg' },
        },
        { name: 'Back', type_line: 'Creature — Werewolf', image_uris: { normal: 'back.jpg' } },
      ],
    })

    expect(customCardImageUri(dfc)).toBe('front.jpg')
    expect(customCardSummary(dfc)?.manaCost).toBe('{1}{R}')
  })

  it('splits the type line the way the catalog spells it', () => {
    const summary = customCardSummary(
      JSON.stringify({ name: 'X', type_line: 'Legendary Artifact Creature — Golem Soldier' }),
    )

    expect(summary?.supertypes).toEqual(['LEGENDARY'])
    expect(summary?.cardTypes).toEqual(['ARTIFACT', 'CREATURE'])
    expect(summary?.subtypes).toEqual(['GOLEM', 'SOLDIER'])
  })

  it('counts mana value and colors off the printed cost', () => {
    const summary = customCardSummary(
      JSON.stringify({ name: 'X', mana_cost: '{X}{2}{W}{U/B}', type_line: 'Sorcery' }),
    )

    expect(summary?.cmc).toBe(4) // X counts 0, {2} counts 2, two colored symbols count 1 each
    expect(summary?.colors.sort()).toEqual(['B', 'U', 'W'])
  })

  it('has no art for a card that carries none, rather than inventing one', () => {
    expect(customCardImageUri(JSON.stringify({ name: 'Invented', type_line: 'Creature' }))).toBeNull()
  })

  it('survives text that is not a card object', () => {
    expect(customCardSummary('not json')).toBeNull()
    expect(customCardSummary('{"no": "name"}')).toBeNull()
    expect(customCardName('not json')).toBe('Unnamed card')
  })
})
