/**
 * Reading a pasted Scryfall(-style) card object — **for display only**.
 *
 * The server's reading is the authoritative one: Argentum Assay compiles the same JSON into a real
 * `CardDefinition`, and that is what the game plays. This file exists because the builder has to
 * draw the card *before* a game exists, and the deckbuilder catalog it draws every other card from
 * has never heard of a custom card. It reads the same fields Scryfall prints on the card frame —
 * name, art, mana cost, type line, P/T — and nothing that affects rules.
 *
 * It also has to work on a scenario loaded from JSON, a file, or a share link, where all the client
 * holds is the pasted source with no compile response beside it. That is why it re-reads the JSON
 * rather than taking the compiled definition: one code path for both.
 */
import type { CardSummary } from '@/components/deckbuilder/cardFilter'

/** CR 205.4a — the words before the card types on a type line. */
const SUPERTYPES = new Set(['BASIC', 'LEGENDARY', 'ONGOING', 'SNOW', 'WORLD', 'HOST', 'ELITE'])

/**
 * The name a pasted card object declares. Falls back to a placeholder rather than throwing — the
 * server rejects a nameless card, and this label only has to survive long enough to say so.
 */
export function customCardName(json: string): string {
  const name = readObject(json)?.name
  return typeof name === 'string' && name ? name : 'Unnamed card'
}

/**
 * A `CardSummary` for a pasted card, or null when the text is not a readable card object.
 *
 * Merged into the builder's catalog index, so a custom card gets art, mana symbols, a type line and
 * hover previews from every surface that already reads that index — the zone tiles, the battlefield
 * editor — without any of them knowing custom cards exist.
 */
export function customCardSummary(json: string): CardSummary | null {
  const obj = readObject(json)
  if (!obj) return null
  const name = typeof obj.name === 'string' ? obj.name : null
  if (!name) return null

  const face = firstFace(obj)
  const manaCost = str(face.mana_cost) ?? str(obj.mana_cost) ?? ''
  const typeLine = str(face.type_line) ?? str(obj.type_line) ?? ''
  const types = parseTypeLine(typeLine)

  return {
    name,
    manaCost,
    cmc: manaValue(manaCost),
    colors: costColors(manaCost),
    // No heuristic here: the color identity a builder tile shows is not a rules decision, and
    // guessing one from oracle text would be a worse answer than the cost's own colors.
    colorIdentity: costColors(manaCost),
    ...types,
    basicLand: types.supertypes.includes('BASIC') && types.cardTypes.includes('LAND'),
    rarity: 'COMMON',
    setCode: null,
    collectorNumber: null,
    oracleText: str(face.oracle_text) ?? str(obj.oracle_text) ?? null,
    power: str(face.power) ?? str(obj.power) ?? null,
    toughness: str(face.toughness) ?? str(obj.toughness) ?? null,
    imageUri: customCardImageUri(json),
  }
}

/**
 * `image_uris.normal` off the pasted object — the card's own art, when the author supplied one.
 *
 * The compiler stamps this onto `CardDefinition.metadata.imageUri`, so a card that has it here has
 * it in play too. A card with none renders through the client's existing name-based Scryfall
 * fallback, which finds a real card and finds nothing for an invented one; a name-only tile is the
 * honest result and the builder already draws one.
 */
export function customCardImageUri(json: string): string | null {
  const obj = readObject(json)
  if (!obj) return null
  return imageUri(firstFace(obj)) ?? imageUri(obj)
}

// --- reading -------------------------------------------------------------------------------

type Json = Record<string, unknown>

function readObject(json: string): Json | null {
  try {
    const parsed: unknown = JSON.parse(json)
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? (parsed as Json) : null
  } catch {
    return null
  }
}

/** A multi-faced object prints its front face's characteristics; a single-faced one is its own. */
function firstFace(obj: Json): Json {
  const faces = obj.card_faces
  if (Array.isArray(faces) && faces[0] && typeof faces[0] === 'object') return faces[0] as Json
  return obj
}

function imageUri(obj: Json): string | null {
  const uris = obj.image_uris
  if (!uris || typeof uris !== 'object') return null
  return str((uris as Json).normal) ?? str((uris as Json).large) ?? str((uris as Json).small)
}

function str(value: unknown): string | null {
  return typeof value === 'string' && value !== '' ? value : null
}

/** "Legendary Creature — Bird Soldier" → the three uppercase lists the catalog uses. */
function parseTypeLine(typeLine: string): Pick<CardSummary, 'cardTypes' | 'supertypes' | 'subtypes'> {
  // Scryfall prints an em dash; a hand-written card may well use a hyphen.
  const [head = '', tail = ''] = typeLine.split(/\s+[—–-]\s+/)
  const supertypes: string[] = []
  const cardTypes: string[] = []
  for (const word of head.split(/\s+/).filter(Boolean)) {
    const upper = word.toUpperCase()
    if (SUPERTYPES.has(upper)) supertypes.push(upper)
    else cardTypes.push(upper)
  }
  return {
    supertypes,
    cardTypes,
    subtypes: tail.split(/\s+/).filter(Boolean).map((s) => s.toUpperCase()),
  }
}

function symbols(manaCost: string): string[] {
  return [...manaCost.matchAll(/\{([^}]+)\}/g)].map((m) => m[1]!.toUpperCase())
}

/** CR 202.3 — generic symbols count their number, every other symbol counts 1, `X` counts 0. */
function manaValue(manaCost: string): number {
  return symbols(manaCost).reduce((total, symbol) => {
    const numeric = Number(symbol)
    if (!Number.isNaN(numeric)) return total + numeric
    if (symbol === 'X' || symbol === 'Y' || symbol === 'Z') return total
    return total + 1
  }, 0)
}

function costColors(manaCost: string): string[] {
  const out = new Set<string>()
  for (const symbol of symbols(manaCost)) {
    for (const color of ['W', 'U', 'B', 'R', 'G']) if (symbol.includes(color)) out.add(color)
  }
  return [...out]
}
