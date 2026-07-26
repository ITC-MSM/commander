/**
 * The wizard's URL is now its state, so an encoding that loses or mangles an answer is a broken
 * screen rather than a cosmetic bug. This walks the same space `modeMatrix.test.ts` does — every
 * roster × Cards × sub-shape × shape × seat count the wizard can offer — and asserts the round trip.
 */
import { describe, it, expect } from 'vitest'
import {
  ROSTERS,
  SHAPE_IDS,
  cardsChoices,
  defaultCardsAxis,
  defaultSeats,
  seatRule,
  shapeChoices,
  subShapeChoices,
  type Roster,
  type ShapeId,
} from '../lobby/modeMatrix'
import type { CardsAxis } from '../lobby/axes'
import { EMPTY_DRAFT, draftToPath, pathToDraft, type WizardDraft } from './wizardUrl'

/** Every (roster, cards) the wizard actually offers, sub-shapes expanded. */
function offeredCards(roster: Roster): CardsAxis[] {
  return cardsChoices(roster)
    .filter((c) => !c.disabledReason)
    .flatMap((c) => {
      const subs = subShapeChoices(roster, c.value)
      if (subs === null) return [defaultCardsAxis(c.value)]
      return subs.filter((s) => !s.disabledReason).map((s) => s.value)
    })
}

/** Every complete selection reachable through the wizard. */
function everySelection(): WizardDraft[] {
  const out: WizardDraft[] = []
  for (const roster of ROSTERS) {
    for (const cards of offeredCards(roster)) {
      for (const shape of shapeChoices(roster, cards).filter((s) => !s.disabledReason)) {
        const rule = seatRule(roster, cards, shape.value)
        for (const seats of rule.values) {
          out.push({ roster, cards, shape: shape.value, seats })
        }
      }
    }
  }
  return out
}

/** `pathToDraft` needs the path and query separately, the way `useLocation` hands them over. */
function split(path: string): [string, string] {
  const i = path.indexOf('?')
  return i === -1 ? [path, ''] : [path.slice(0, i), path.slice(i)]
}

function roundTrip(draft: WizardDraft): WizardDraft {
  const [pathname, search] = split(draftToPath(draft))
  return pathToDraft(pathname, search, true)
}

describe('wizard URL round trip', () => {
  const selections = everySelection()

  it('covers a non-trivial space', () => {
    expect(selections.length).toBeGreaterThan(40)
  })

  it('survives every complete selection', () => {
    const broken = selections.filter((s) => {
      const back = roundTrip(s)
      return JSON.stringify(back) !== JSON.stringify(s)
    })
    expect(broken.map((s) => draftToPath(s))).toEqual([])
  })

  it('survives every partial selection', () => {
    const partials: WizardDraft[] = [
      EMPTY_DRAFT,
      ...ROSTERS.map((roster) => ({ ...EMPTY_DRAFT, roster })),
    ]
    for (const roster of ROSTERS) {
      for (const cards of offeredCards(roster)) {
        // Only meaningful where step 3 is a real question — with one open shape, decoding fills it
        // in on purpose, which is what makes `/play/solo/bring-a-deck` a complete selection.
        if (shapeChoices(roster, cards).filter((s) => !s.disabledReason).length > 1) {
          partials.push({ ...EMPTY_DRAFT, roster, cards })
        }
      }
    }
    const broken = partials.filter((p) => JSON.stringify(roundTrip(p)) !== JSON.stringify(p))
    expect(broken.map((p) => draftToPath(p))).toEqual([])
  })

  it('gives every selection a distinct path', () => {
    const paths = selections.map(draftToPath)
    expect(new Set(paths).size).toBe(paths.length)
  })

  it('keeps the default seat count out of the query', () => {
    for (const roster of ROSTERS) {
      for (const cards of offeredCards(roster)) {
        for (const shape of shapeChoices(roster, cards).filter((s) => !s.disabledReason)) {
          const rule = seatRule(roster, cards, shape.value)
          const path = draftToPath({ roster, cards, shape: shape.value, seats: defaultSeats(rule) })
          expect(path, `${roster}/${cards.kind}/${shape.value}`).not.toContain('?')
        }
      }
    }
  })

  it('produces readable, lowercase, slash-delimited paths', () => {
    for (const path of selections.map(draftToPath)) {
      expect(path).toMatch(/^\/play(\/[a-z0-9-]+)+(\?seats=\d+)?$/)
    }
  })
})

describe('wizard URL decoding is defensive', () => {
  it('ignores paths it does not own', () => {
    for (const path of ['/', '/help', '/deckbuilder', '/tournament/abc', '/playground']) {
      expect(pathToDraft(path, '', true), path).toEqual(EMPTY_DRAFT)
    }
  })

  it('drops an unknown roster entirely', () => {
    expect(pathToDraft('/play/nobody', '', true)).toEqual(EMPTY_DRAFT)
    expect(pathToDraft('/play', '', true)).toEqual(EMPTY_DRAFT)
  })

  it('drops a solo selection when the server has no AI', () => {
    expect(pathToDraft('/play/solo/bring-a-deck', '', false)).toEqual(EMPTY_DRAFT)
    expect(pathToDraft('/play/solo/bring-a-deck', '', true).roster).toBe('SOLO')
  })

  it('truncates to the roster when the Cards value is unreachable for it', () => {
    // Momir and Random pool exist only on the two-seat lobby that plays one game.
    for (const slug of ['momir', 'random']) {
      const back = pathToDraft(`/play/group/${slug}`, '', true)
      expect(back, slug).toEqual({ ...EMPTY_DRAFT, roster: 'GROUP' })
    }
  })

  it('truncates to the roster when a sub-shape is unreachable for it', () => {
    // Winston is exactly two players, so a group cannot have it.
    expect(pathToDraft('/play/group/draft-winston', '', true))
      .toEqual({ ...EMPTY_DRAFT, roster: 'GROUP' })
  })

  it('drops an unknown or unreachable shape but keeps the answers before it', () => {
    const back = pathToDraft('/play/group/draft-booster/not-a-shape', '', true)
    expect(back.roster).toBe('GROUP')
    expect(back.cards).toEqual({ kind: 'DRAFT', shape: 'BOOSTER' })
    expect(back.shape).toBeNull()
  })

  it('auto-resolves a one-answer shape step', () => {
    const back = pathToDraft('/play/friend/random', '', true)
    expect(back.shape).toBe<ShapeId>('ONE_GAME')
    expect(back.seats).toBe(2)
  })

  it('falls back to the default seat count for a nonsense one', () => {
    for (const seats of ['0', '99', 'abc', '']) {
      const back = pathToDraft('/play/group/draft-booster/bracket', `?seats=${seats}`, true)
      expect(back.seats, seats).toBe(defaultSeats(seatRule('GROUP', { kind: 'DRAFT', shape: 'BOOSTER' }, 'BRACKET')))
    }
  })

  it('honours a seat count the rule allows', () => {
    const back = pathToDraft('/play/group/draft-booster/bracket', '?seats=4', true)
    expect(back.seats).toBe(4)
  })

  it('tolerates a trailing slash', () => {
    expect(pathToDraft('/play/group/', '', true)).toEqual({ ...EMPTY_DRAFT, roster: 'GROUP' })
  })

  it('has a slug for every shape id', () => {
    // A missing case would make the path collide or read `undefined`; the regex catches that here
    // rather than in the address bar.
    for (const shape of SHAPE_IDS) {
      const path = draftToPath({
        roster: 'GROUP', cards: { kind: 'BRING_A_DECK', legality: null }, shape, seats: null,
      })
      expect(path, shape).toMatch(/^\/play\/group\/bring-a-deck\/[a-z-]+$/)
    }
  })
})
