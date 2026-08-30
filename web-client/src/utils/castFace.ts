import type { ClientCard, LegalActionInfo } from '@/types'

/**
 * Rendering a card as the face it would actually be *cast* as, rather than the face its zone is
 * showing.
 *
 * Almost always those are the same card and this is the identity function. Disturb (CR 702.146a)
 * is the exception: the card lies in the graveyard printed front face up, but casting it puts the
 * **back** face on the stack (CR 712.8c). So the ghost card offered in hand — and the hover
 * preview behind it — would otherwise show Lunarch Veteran while the spell on offer is Luminous
 * Phantom: a different name, a different creature, and different rules text.
 *
 * Which offers do this is the server's call ({@link LegalActionInfo.castsTransformed}); this file
 * only decides what a swapped face looks like on screen.
 */

/**
 * [card] as the face [actions] would cast it as.
 *
 * Returns [card] itself when no action on it casts transformed, or when the card carries no back
 * face to swap in — so callers can apply it unconditionally.
 *
 * The face's own characteristics move with it — name, type line, text, art, printed P/T and
 * keywords — so the preview never pairs the back's art with the front's stats. `manaCost` stays
 * the front's on purpose: a disturbed spell's mana value is still calculated from the front
 * face's mana cost, and what it costs *to cast* comes from the offer's own `manaCostString`.
 * `cardTypes` / `subtypes` keep the front's values, since the server sends no back-face copy of
 * them and their readers (combat, the battlefield overlays) never draw a graveyard card.
 *
 * The back-face slots are filled with the front face's values, so pressing F on the hover preview
 * still flips back to the printed front.
 */
export function castOfferFace(card: ClientCard, actions: readonly LegalActionInfo[]): ClientCard {
  if (!card.backFaceName) return card
  const castsTransformed = actions.some(
    (a) => a.castsTransformed === true && a.action.type === 'CastSpell' && a.action.cardId === card.id
  )
  if (!castsTransformed) return card
  return {
    ...card,
    name: card.backFaceName,
    typeLine: card.backFaceTypeLine ?? card.typeLine,
    oracleText: card.backFaceOracleText ?? card.oracleText,
    imageUri: card.backFaceImageUri ?? null,
    isLandscapeFace: card.backFaceIsLandscape ?? false,
    currentFace: 'BACK',
    power: card.backFacePower ?? null,
    toughness: card.backFaceToughness ?? null,
    basePower: card.backFacePower ?? null,
    baseToughness: card.backFaceToughness ?? null,
    keywords: card.backFaceKeywords ?? [],
    backFaceName: card.name,
    backFaceTypeLine: card.typeLine,
    backFaceOracleText: card.oracleText,
    backFaceImageUri: card.imageUri ?? null,
    backFaceIsLandscape: card.isLandscapeFace ?? false,
    backFacePower: card.power,
    backFaceToughness: card.toughness,
    backFaceKeywords: card.keywords,
  }
}
