/**
 * Curve- and pip-aware basic land suggestion, shared between the sealed and
 * the standalone deckbuilders.
 *
 * Given a list of deck entries with each card's CMC, mana cost, land flags,
 * and the colors it produces, this module returns target counts for each
 * available basic land.
 *
 * Algorithm (Karsten-flavoured):
 *   1. Curve-based total land target — aggro decks want fewer lands than
 *      control. We pick a land ratio off the average non-land CMC.
 *   2. Discount the target by non-basic lands (full credit) and by
 *      mana-producing non-lands like rocks/dorks (half credit).
 *   3. Turn every colored cost into an on-curve castability requirement.
 *   4. Allocate basics one at a time to the color with the largest increase
 *      in hypergeometric castability. This naturally values early and
 *      double-pipped spells more than late single-pipped spells.
 *
 * Callers map their own card shapes (`SealedCardInfo`, `CardSummary`, …)
 * into `DeckEntry` and apply the returned counts to their own store.
 */

export type LandColor = 'W' | 'U' | 'B' | 'R' | 'G'

const COLORS: readonly LandColor[] = ['W', 'U', 'B', 'R', 'G']

const BASIC_SUBTYPE_TO_COLOR: Record<string, LandColor> = {
  plains: 'W',
  island: 'U',
  swamp: 'B',
  mountain: 'R',
  forest: 'G',
}

export interface DeckEntry {
  readonly name: string
  readonly manaCost: string
  readonly cmc: number
  readonly isLand: boolean
  readonly isBasicLand: boolean
  /** Colors this card can produce (detected from land subtypes or `Add {X}` text). */
  readonly producedColors: readonly LandColor[]
  readonly count: number
}

export interface BasicLand {
  readonly name: string
  readonly color: LandColor
}

export interface SuggestLandsInput {
  readonly entries: readonly DeckEntry[]
  readonly availableBasics: readonly BasicLand[]
  /**
   * Floor on total deck size — basics will be padded so spells + lands ≥ this.
   * Use 40 for sealed, 60 for constructed, 0 / undefined for no floor.
   */
  readonly minDeckSize?: number
}

/**
 * Returns target counts keyed by basic-land name. Every entry in
 * `availableBasics` is present in the result (count may be 0). Caller is
 * responsible for applying these to its store.
 */
export function suggestBasicLands(input: SuggestLandsInput): Record<string, number> {
  const { entries, availableBasics, minDeckSize = 0 } = input

  const result: Record<string, number> = {}
  for (const land of availableBasics) result[land.name] = 0
  if (availableBasics.length === 0) return result

  // Materialise non-basic deck cards copy-by-copy so multi-copy slots and
  // multi-pip costs both contribute to demand naturally.
  const spells: DeckEntry[] = []
  let nonBasicLandCount = 0
  let nonLandManaSourceCount = 0
  let spellCount = 0
  const existingSources: Record<LandColor, number> = { W: 0, U: 0, B: 0, R: 0, G: 0 }

  for (const entry of entries) {
    if (entry.count <= 0 || entry.isBasicLand) continue
    if (entry.isLand) {
      nonBasicLandCount += entry.count
      for (const c of entry.producedColors) existingSources[c] += entry.count
    } else {
      spellCount += entry.count
      if (entry.producedColors.length > 0) {
        nonLandManaSourceCount += entry.count
        for (const c of entry.producedColors) existingSources[c] += 0.5 * entry.count
      }
      for (let i = 0; i < entry.count; i++) spells.push(entry)
    }
  }
  if (spellCount === 0 && nonBasicLandCount === 0) return result

  // Curve-based total land target.
  const manaRockReduction = Math.floor(nonLandManaSourceCount / 2)
  const curveTotal = curveBasedLandCount(spells)
  const ratioBasedBasics = curveTotal - nonBasicLandCount - manaRockReduction
  const minBasedBasics = Math.max(minDeckSize - spellCount - nonBasicLandCount, 0)
  const targetBasics = Math.max(ratioBasedBasics, minBasedBasics, 0)
  if (targetBasics === 0) return result

  const requirements = spells.flatMap(colorRequirements)
  const usedColors = new Set(requirements.map((r) => r.color))

  // Colorless deck: dump everything into the first available basic.
  if (requirements.length === 0) {
    const first = availableBasics[0]
    if (first) result[first.name] = targetBasics
    return result
  }

  const colorToLand = mapColorsToLands(availableBasics)
  const allocatable = COLORS.filter((c) => usedColors.has(c) && colorToLand[c])
  const basicsByColor: Record<LandColor, number> = { W: 0, U: 0, B: 0, R: 0, G: 0 }
  const deckSize = Math.max(spellCount + nonBasicLandCount + targetBasics, minDeckSize)

  // A color represented in the spell suite should not be stranded at zero.
  // Seed up to three total sources, then let probability gains decide every
  // remaining slot. Existing duals/fixing count toward this floor.
  let remaining = targetBasics
  for (const color of allocatable) {
    const seed = Math.min(remaining, Math.max(0, Math.ceil(3 - existingSources[color])))
    basicsByColor[color] += seed
    remaining -= seed
  }

  while (remaining > 0 && allocatable.length > 0) {
    let bestColor = allocatable[0]!
    let bestGain = Number.NEGATIVE_INFINITY
    for (const color of allocatable) {
      const sources = existingSources[color] + basicsByColor[color]
      const gain = requirements
        .filter((r) => r.color === color)
        .reduce(
          (sum, r) =>
            sum +
            r.weight *
              (castability(deckSize, sources + 1, r.pips, r.turn) -
                castability(deckSize, sources, r.pips, r.turn)),
          0,
        )
      if (gain > bestGain) {
        bestGain = gain
        bestColor = color
      }
    }
    basicsByColor[bestColor]++
    remaining--
  }

  // A partial/custom card catalog may not expose the basic type a deck asks
  // for. Preserve the promised land total with the first available basic
  // rather than silently returning an undersized deck.
  if (remaining > 0) {
    const fallback = availableBasics[0]
    if (fallback) basicsByColor[fallback.color] += remaining
  }

  for (const color of COLORS) {
    const land = colorToLand[color]
    if (land) result[land] = basicsByColor[color]
  }

  return result
}

// ---------------------------------------------------------------------------
// Card-shape adapters
// ---------------------------------------------------------------------------

/**
 * Detect mana colors a card can produce, given any combination of typeLine
 * (e.g. `"Land — Plains Forest"`), explicit subtypes, and oracle text
 * (`"Add {G}"`, `"Add one mana of any color"`).
 */
export function detectProducedColors(opts: {
  typeLine?: string | null
  subtypes?: readonly string[] | null
  oracleText?: string | null
}): LandColor[] {
  const out = new Set<LandColor>()
  const typeLine = (opts.typeLine ?? '').toLowerCase()
  for (const sub of opts.subtypes ?? []) {
    const c = BASIC_SUBTYPE_TO_COLOR[sub.toLowerCase()]
    if (c) out.add(c)
  }
  if (typeLine) {
    for (const [sub, color] of Object.entries(BASIC_SUBTYPE_TO_COLOR)) {
      if (typeLine.includes(sub)) out.add(color)
    }
  }
  const text = (opts.oracleText ?? '').toLowerCase()
  if (text.includes('add')) {
    if (text.includes('{w}')) out.add('W')
    if (text.includes('{u}')) out.add('U')
    if (text.includes('{b}')) out.add('B')
    if (text.includes('{r}')) out.add('R')
    if (text.includes('{g}')) out.add('G')
    if (text.includes('any color')) for (const c of COLORS) out.add(c)
  }
  return [...out]
}

// ---------------------------------------------------------------------------
// Internals
// ---------------------------------------------------------------------------

interface ColorRequirement {
  readonly color: LandColor
  readonly pips: number
  readonly turn: number
  readonly weight: number
}

function colorRequirements(card: DeckEntry): ColorRequirement[] {
  const plain: Record<LandColor, number> = { W: 0, U: 0, B: 0, R: 0, G: 0 }
  const hybridWeights: Record<LandColor, number> = { W: 0, U: 0, B: 0, R: 0, G: 0 }
  for (const match of card.manaCost.matchAll(/\{([^}]+)\}/g)) {
    const symbol = match[1]!.toUpperCase()
    if (COLORS.includes(symbol as LandColor)) {
      plain[symbol as LandColor]++
      continue
    }
    const sides = symbol.split('/').filter((s): s is LandColor => COLORS.includes(s as LandColor))
    if (sides.length > 0) {
      for (const side of sides) hybridWeights[side] += 1 / sides.length
    }
  }

  const turn = Math.max(1, Math.ceil(card.cmc))
  const out: ColorRequirement[] = []
  for (const color of COLORS) {
    // Missing the second/third source for a pip-dense spell is more damaging
    // than missing one source for a splash card. This severity multiplier
    // keeps a pile of single-pip cards from drowning out an early {C}{C}
    // requirement merely because the pile contains more copies.
    if (plain[color] > 0) {
      out.push({ color, pips: plain[color], turn, weight: pipSeverity(plain[color]) })
    }
    if (hybridWeights[color] > 0) out.push({ color, pips: 1, turn, weight: hybridWeights[color] })
  }
  return out
}

function pipSeverity(pips: number): number {
  if (pips <= 1) return 1
  if (pips === 2) return 3
  return 3 + (pips - 2) * 3
}

/**
 * Probability of seeing at least [pips] sources by the spell's on-curve turn.
 * Fractional sources (mana dorks/rocks) are linearly interpolated.
 */
function castability(deckSize: number, sources: number, pips: number, turn: number): number {
  const lower = Math.floor(sources)
  const fraction = sources - lower
  const cardsSeen = Math.min(deckSize, 7 + Math.max(0, turn - 1))
  const low = hypergeometricAtLeast(deckSize, lower, cardsSeen, pips)
  if (fraction === 0) return low
  const high = hypergeometricAtLeast(deckSize, lower + 1, cardsSeen, pips)
  return low + (high - low) * fraction
}

function hypergeometricAtLeast(
  population: number,
  successes: number,
  draws: number,
  needed: number,
): number {
  if (needed <= 0) return 1
  if (successes < needed || draws < needed) return 0
  const max = Math.min(successes, draws)
  let probability = 0
  for (let hits = needed; hits <= max; hits++) {
    probability +=
      (combination(successes, hits) * combination(population - successes, draws - hits)) /
      combination(population, draws)
  }
  return Math.min(1, probability)
}

function combination(n: number, k: number): number {
  if (k < 0 || k > n) return 0
  const small = Math.min(k, n - k)
  let value = 1
  for (let i = 1; i <= small; i++) value = (value * (n - small + i)) / i
  return value
}

/** Total land target driven by avg non-land CMC. Aggro 16, midrange 17, control 18. */
function curveBasedLandCount(spells: readonly DeckEntry[]): number {
  if (spells.length === 0) return 0
  const totalCmc = spells.reduce((s, c) => s + c.cmc, 0)
  const avg = totalCmc / spells.length
  const ratio = avg < 2.3 ? 0.4 : avg < 3.2 ? 0.425 : 0.45
  const total = Math.max(Math.round(spells.length / (1 - ratio)), 40)
  return Math.round(total * ratio)
}

function mapColorsToLands(basics: readonly BasicLand[]): Partial<Record<LandColor, string>> {
  const map: Partial<Record<LandColor, string>> = {}
  for (const land of basics) {
    if (!(land.color in map)) map[land.color] = land.name
  }
  return map
}
