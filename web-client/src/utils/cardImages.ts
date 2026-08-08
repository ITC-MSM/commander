/**
 * Standard MTG morph face-down card art from Scryfall.
 * This is the official morph token from Commander 2019 (TC19 #27) showing the distinctive helmet artwork.
 * Source: https://scryfall.com/card/tc19/27/morph
 */
export const MORPH_FACE_DOWN_IMAGE_URL = 'https://cards.scryfall.io/normal/front/e/9/e9375cbe-93c0-41a5-a6e3-fb4416f54a69.jpg'

/**
 * Standard MTG manifest face-down card art from Scryfall.
 * The official Manifest token from Duskmourn: House of Horror (TDSK #18). Manifested permanents
 * (CR 701.40) are shown with this instead of the morph token.
 * Source: https://scryfall.com/card/tdsk/18/manifest
 */
export const MANIFEST_FACE_DOWN_IMAGE_URL = 'https://cards.scryfall.io/normal/front/0/1/01104ab1-84e1-4c78-853d-637c6554bdf9.jpg'

/**
 * Standard MTG card back image.
 */
export const CARD_BACK_IMAGE_URL = 'https://backs.scryfall.io/normal/2/2/222b7a3b-2321-4d4c-af19-19338b134971.jpg?1677416389'

/**
 * Degrees to rotate a card's hover preview image. Two families of cards are printed sideways, so
 * their single portrait image has to be rotated 90° to read landscape:
 *
 * - **Split layouts** (Pain // Suffering, Rooms like Unholy Annex // Ritual Chamber — CR 709.5).
 * - **Battles** (CR 310) — a transforming double-faced card, so `layout` is `TRANSFORM`, not
 *   `SPLIT`; only the type line identifies them. The *front* face is the landscape one, which is
 *   also the face these previews show, so reading the card's own type line is correct here.
 *
 * Everything else stays upright.
 */
export function landscapeImageRotateDeg(
  card: { layout?: string; typeLine?: string | null } | null | undefined
): 0 | 90 {
  if (card?.layout === 'SPLIT') return 90
  return isBattleTypeLine(card?.typeLine) ? 90 : 0
}

/**
 * Whether a printed type line names the Battle card type (CR 310). Only the types half of the line
 * is examined — everything before the em dash — so a subtype or a card name can never match.
 */
export function isBattleTypeLine(typeLine: string | null | undefined): boolean {
  if (!typeLine) return false
  const types = typeLine.split('—')[0] ?? ''
  return /\bBattle\b/.test(types)
}

/**
 * Get the image URL for a card.
 *
 * Uses the provided imageUri if available (from card metadata),
 * otherwise falls back to Scryfall API lookup by card name.
 *
 * @param cardName The card's name (used for Scryfall fallback)
 * @param imageUri The card's direct image URI from metadata (optional)
 * @param version The image version/size to request
 * @returns The image URL to use
 */
export function getCardImageUrl(
  cardName: string,
  imageUri?: string | null,
  version: 'small' | 'normal' | 'large' = 'normal'
): string {
  if (imageUri) {
    return imageUri
  }
  return getScryfallFallbackUrl(cardName, version)
}

/**
 * Get a Scryfall API fallback URL for a card image.
 *
 * @param cardName The card's name
 * @param version The image version/size to request
 * @returns The Scryfall API image URL
 */
export function getScryfallFallbackUrl(
  cardName: string,
  version: 'small' | 'normal' | 'large' = 'normal'
): string {
  // Token names have a " Token" suffix (e.g., "Insect Token") that Scryfall doesn't use
  const scryfallName = cardName.endsWith(' Token') ? cardName.slice(0, -6) : cardName
  return `https://api.scryfall.com/cards/named?exact=${encodeURIComponent(scryfallName)}&format=image&version=${version}`
}

/**
 * Get the landscape *art crop* for a card by name (Scryfall `version=art_crop`).
 *
 * Unlike the full-card images above, this returns just the illustration with no frame,
 * which is the right shape for a wide banner/tile background. Used by the saved-deck
 * gallery to paint each deck's hero art from its rarest card.
 *
 * @param cardName The card's name (the default printing's art is used)
 */
export function getScryfallArtCropUrl(cardName: string): string {
  const scryfallName = cardName.endsWith(' Token') ? cardName.slice(0, -6) : cardName
  return `https://api.scryfall.com/cards/named?exact=${encodeURIComponent(scryfallName)}&format=image&version=art_crop`
}

/**
 * Derive the landscape *art crop* directly from a card's stored image URL.
 *
 * Our card metadata already carries a direct `cards.scryfall.io` CDN URL in `imageUri`
 * (almost always the `normal` size). Scryfall keys every size of an image under the same
 * path with only the size segment differing (`small` | `normal` | `large` | `png` |
 * `border_crop` | `art_crop`), so swapping that segment yields the crop on the same CDN
 * host — no `api.scryfall.com` round-trip, no redirect, and no API rate limiting (which
 * only applies to the API host, not the image CDN).
 *
 * Prefer this over {@link getScryfallArtCropUrl} whenever a card's `imageUri` is on hand;
 * fall back to the by-name API lookup only when it isn't (returns null here).
 *
 * @param imageUri A card's `imageUri` (e.g. `https://cards.scryfall.io/normal/front/a/b/<id>.jpg`)
 * @returns The CDN `art_crop` URL, or null when `imageUri` isn't a recognised Scryfall CDN image URL
 */
export function getCdnArtCropUrl(imageUri: string | null | undefined): string | null {
  if (!imageUri) return null
  const match = imageUri.match(
    /^(https:\/\/cards\.scryfall\.io\/)(small|normal|large|png|border_crop)(\/.*?)(\.\w+)(\?.*)?$/
  )
  if (!match) return null
  const [, host, , path, , query = ''] = match
  // art_crop is always served as .jpg, even when the source size (e.g. png) isn't.
  return `${host}art_crop${path}.jpg${query}`
}
