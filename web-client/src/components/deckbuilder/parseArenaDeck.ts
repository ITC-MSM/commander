/**
 * Multi-format deck-list parser. Accepts MTG Arena, Moxfield, and plain-text
 * formats interchangeably so the user can paste whatever their other tool spits
 * out. Recognised line shapes:
 *
 *   `4 Lightning Bolt`                     plain
 *   `4x Lightning Bolt`                    plain (Moxfield-style "x")
 *   `4 Lightning Bolt (LEA) 161`           Arena
 *   `1 Cardname (SET) *F* *A* 42 #tag`     Moxfield bulk-edit (foil/alter/tags)
 *   `SB: 2 Counterspell`                   MTGO sideboard prefix (Moxfield)
 *
 * Section headers (case-insensitive) include `Deck` / `Mainboard` / `Main Deck`
 * / `Maindeck`, `Sideboard` / `Side` / `SB`, `Commander` / `Commanders` / `EDH`,
 * `Companion`, and `About`. Main deck, sideboard, and commander are returned as
 * separate lists; `Companion` and `About` are skipped (the latter only donates
 * its `Name <…>` line). Blank lines and lines starting with `//` or `#` are
 * ignored.
 */
import type { CardSummary } from './cardFilter'

export interface ParsedEntry {
  count: number
  name: string
  setCode?: string
  collectorNumber?: string
  /** Original line — kept for error messages. */
  raw: string
  /** Line number in the source text (1-based) — kept for error messages. */
  line: number
}

export interface ParseResult {
  /** Main-deck entries, in source order. */
  entries: ParsedEntry[]
  /** Sideboard entries — the constructed "outside the game" board (CR 400.11a). */
  sideboard: ParsedEntry[]
  /**
   * Commander entries — cards listed under a `Commander` / `Commanders` /
   * `EDH` header. Most decks have exactly one; partner pairs etc. produce two.
   */
  commander: ParsedEntry[]
  /**
   * Lines that looked like card entries but couldn't be parsed. `section` records which
   * board the line sat under, so a caller that rescues unparseable lines (the lobby's paste
   * box tolerates a bare card name as one copy) can rescue them onto the right board.
   */
  errors: Array<{ line: number; raw: string; reason: string; section: CardSection }>
  /**
   * Deck name pulled from an Arena-style `Name <…>` line in the leading
   * `About` block, if present.
   */
  deckName?: string
}

// Count + optional 'x' suffix (e.g. `4` or `4x`), name (lazy), optional
// `(SET) collector` suffix. The line is pre-stripped of Moxfield decorations
// (`*F*`, `*A*`, `#tag`, trailing `*`) before this regex runs.
const ENTRY_RE = /^(\d+)x?\s+(.+?)(?:\s+\(([A-Za-z0-9]{2,5})\)(?:\s+(\S+))?)?\s*$/

/** A board a parsed entry can land on. */
export type CardSection = 'main' | 'side' | 'commander'

type Section = CardSection | 'ignore'

const SECTION_HEADERS: Record<string, Section> = {
  deck: 'main',
  maindeck: 'main',
  'main deck': 'main',
  main: 'main',
  mainboard: 'main',
  sideboard: 'side',
  'side board': 'side',
  side: 'side',
  sb: 'side',
  commander: 'commander',
  commanders: 'commander',
  edh: 'commander',
  companion: 'ignore',
  about: 'ignore',
}

/**
 * Strip Moxfield-style decorations from a card line so the simpler entry
 * regex can match across formats. Returns the cleaned line and a flag for the
 * `SB:` sideboard prefix that some Moxfield/MTGO exports use per-line.
 */
function preprocessLine(raw: string): { line: string; sideboard: boolean } {
  let line = raw
  let sideboard = false

  // Per-line MTGO/Moxfield sideboard prefix (e.g. `SB: 2 Counterspell`).
  const sbMatch = line.match(/^SB:\s*/i)
  if (sbMatch) {
    sideboard = true
    line = line.slice(sbMatch[0].length)
  }

  // Moxfield trailing tags: ` #tag`, ` #!globaltag`. Strip from the end as
  // long as the trailing token still looks like a tag, so a card whose name
  // contains `#` somewhere internally isn't accidentally truncated.
  while (true) {
    const m = line.match(/\s+#!?\S+\s*$/)
    if (!m) break
    line = line.slice(0, m.index).trimEnd()
  }

  // Moxfield foil / alter flags can appear anywhere between name and number.
  // Format is `*F*` / `*A*` (case-insensitive).
  line = line.replace(/\s+\*[A-Za-z]\*/g, '').trim()

  return { line, sideboard }
}

export function parseArenaDeckList(text: string): ParseResult {
  const entries: ParsedEntry[] = []
  const sideboard: ParsedEntry[] = []
  const commander: ParsedEntry[] = []
  const errors: ParseResult['errors'] = []
  let deckName: string | undefined

  let section: Section = 'main'

  const rawLines = text.split(/\r?\n/)
  for (let i = 0; i < rawLines.length; i++) {
    const raw = rawLines[i] ?? ''
    const trimmed = raw.trim()
    if (trimmed === '') continue
    if (trimmed.startsWith('//') || trimmed.startsWith('#')) continue

    const headerKey = trimmed.toLowerCase().replace(/[:\s]+$/, '')
    if (headerKey in SECTION_HEADERS) {
      section = SECTION_HEADERS[headerKey]!
      continue
    }

    const { line: cleaned, sideboard: lineIsSideboard } = preprocessLine(trimmed)
    const targetSection: Section = lineIsSideboard ? 'side' : section

    if (targetSection === 'ignore') {
      // Arena's About block carries a `Name <deck-name>` metadata line. Grab
      // the first one so the import flow can suggest a deck name.
      if (deckName === undefined) {
        const nameMatch = cleaned.match(/^name\s*:?\s+(.+)$/i)
        if (nameMatch) {
          const value = nameMatch[1]!.trim().replace(/^"(.*)"$/, '$1').trim()
          if (value) deckName = value
        }
      }
      continue
    }

    const match = ENTRY_RE.exec(cleaned)
    if (!match) {
      errors.push({
        line: i + 1,
        raw: trimmed,
        reason: 'unrecognised line format',
        section: targetSection,
      })
      continue
    }
    const count = parseInt(match[1]!, 10)
    if (!Number.isFinite(count) || count <= 0) {
      errors.push({ line: i + 1, raw: trimmed, reason: 'invalid card count', section: targetSection })
      continue
    }
    const entry: ParsedEntry = {
      count,
      name: match[2]!.trim(),
      raw: trimmed,
      line: i + 1,
      ...(match[3] ? { setCode: match[3].toUpperCase() } : {}),
      ...(match[4] ? { collectorNumber: match[4] } : {}),
    }
    if (targetSection === 'side') sideboard.push(entry)
    else if (targetSection === 'commander') commander.push(entry)
    else entries.push(entry)
  }

  return deckName !== undefined
    ? { entries, sideboard, commander, errors, deckName }
    : { entries, sideboard, commander, errors }
}

export interface ResolvedEntry {
  entry: ParsedEntry
  match: CardSummary | null
  /** True if the name was matched but the requested set code didn't match. */
  setMismatch?: boolean
}

export interface ResolveResult {
  resolved: ResolvedEntry[]
  /** Aggregated {name → count} of successfully matched entries. */
  deckCards: Record<string, number>
  /**
   * Aggregated {name → count} of unmatched entries (cards not in the catalogue).
   * Kept separate so callers can choose whether to include unimplemented cards
   * as placeholder rows in the imported deck.
   */
  unmatchedCards: Record<string, number>
  /** Total cards across resolved entries (matched copies only). */
  matchedCards: number
  /** Total cards in the parsed list, matched or not. */
  totalCards: number
  /** Entries we couldn't match by name. */
  unmatched: ResolvedEntry[]
  /** Entries that exceeded the four-of limit (or basic-land exemption). */
  truncated: Array<{ name: string; requested: number; capped: number }>
}

/**
 * Match parsed entries against the catalogue by name (case-insensitive). The
 * set code, if present, is used only to flag a soft mismatch — the name still
 * resolves so users aren't blocked by a stale set tag. Copy counts above the
 * four-of limit (basic lands exempted) are silently capped, with the original
 * request reported back so the UI can surface a warning.
 */
export function resolveAgainstCatalog(
  entries: ParsedEntry[],
  catalog: CardSummary[]
): ResolveResult {
  const byName = new Map<string, CardSummary>()
  for (const c of catalog) byName.set(c.name.toLowerCase(), c)

  const resolved: ResolvedEntry[] = []
  const deckCards: Record<string, number> = {}
  const unmatchedCards: Record<string, number> = {}
  const truncated: ResolveResult['truncated'] = []
  let matchedCards = 0
  let totalCards = 0

  for (const entry of entries) {
    totalCards += entry.count
    const card = byName.get(entry.name.toLowerCase()) ?? null
    if (!card) {
      resolved.push({ entry, match: null })
      // Aggregate unmatched copies under the as-typed name, capped at 4 since
      // we can't tell whether the card is a basic land without a catalogue hit.
      const previous = unmatchedCards[entry.name] ?? 0
      unmatchedCards[entry.name] = Math.min(previous + entry.count, 4)
      continue
    }
    const setMismatch =
      !!entry.setCode && !!card.setCode && entry.setCode.toUpperCase() !== card.setCode.toUpperCase()
    resolved.push({ entry, match: card, ...(setMismatch ? { setMismatch: true } : {}) })

    const max = card.basicLand ? Infinity : 4
    const previous = deckCards[card.name] ?? 0
    const requested = previous + entry.count
    const capped = Math.min(requested, max)
    deckCards[card.name] = capped
    matchedCards += capped - previous
    if (capped < requested) {
      const existing = truncated.find((t) => t.name === card.name)
      if (existing) {
        existing.requested = requested
        existing.capped = capped
      } else {
        truncated.push({ name: card.name, requested, capped })
      }
    }
  }

  const unmatched = resolved.filter((r) => r.match === null)
  return { resolved, deckCards, unmatchedCards, matchedCards, totalCards, unmatched, truncated }
}
