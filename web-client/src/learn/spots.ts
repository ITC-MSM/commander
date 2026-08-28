/**
 * The places on the game board the coach can point at.
 *
 * A tip or a tour step names a spot; `LearnSpotlight` finds the element and draws a ring around
 * it. The board marks its controls with `data-learn="…"` — an attribute, never a layout change —
 * and the zones already carry `data-zone`. Everything here is a DOM query, so a spot that is not
 * on screen (a phone layout without the log, a step with no combat buttons) simply draws nothing.
 */
import type { EntityId } from '@/types'

export type SpotId =
  | 'hand'
  | 'battlefield'
  | 'opponent-battlefield'
  | 'phase-strip'
  | 'pass'
  | 'combat-buttons'
  | 'controls'
  | 'undo'
  | 'priority-mode'
  | 'stack'
  | 'log'
  | 'opponent-life'
  | 'my-life'
  | 'piles'

export interface SpotContext {
  me: EntityId
  opponent: EntityId | undefined
}

const SELECTORS: Record<SpotId, string | ((ctx: SpotContext) => string)> = {
  hand: '[data-zone="hand"]',
  battlefield: '[data-zone="player-battlefield"]',
  'opponent-battlefield': '[data-zone="opponent-battlefield"]',
  'phase-strip': '[data-learn="phase-strip"]',
  pass: '[data-learn="pass"]',
  'combat-buttons': '[data-learn="combat-buttons"]',
  controls: '[data-learn="controls"]',
  undo: '[data-learn="undo"]',
  'priority-mode': '[data-learn="priority-mode"]',
  stack: '[data-learn="stack"]',
  log: '[data-learn="log"]',
  'opponent-life': (ctx) => (ctx.opponent ? `[data-life-id="${ctx.opponent}"]` : '[data-life-id]'),
  'my-life': (ctx) => `[data-life-id="${ctx.me}"]`,
  piles: '[data-zone="player-library"]',
}

export function spotSelector(spot: SpotId, ctx: SpotContext): string {
  const s = SELECTORS[spot]
  return typeof s === 'function' ? s(ctx) : s
}

/** The first on-screen match — a zone can be rendered twice for two layouts, one of them empty. */
export function findSpot(spot: SpotId, ctx: SpotContext): Element | null {
  const selector = spotSelector(spot, ctx)
  for (const el of document.querySelectorAll(selector)) {
    const r = el.getBoundingClientRect()
    if (r.width > 0 && r.height > 0) return el
  }
  return null
}

export interface SpotRect {
  top: number
  left: number
  width: number
  height: number
}

/**
 * The box to ring: the element's own rect, grown to cover every card inside it. A hand fans its
 * cards with rotations and overlaps that spill past the zone's layout box, and a bounding rect
 * reads the box, not the transformed children — so the ring would cut the outer cards in half.
 * `getBoundingClientRect` on each card does include its transform.
 */
export function spotRect(el: Element): SpotRect {
  let { top, left, right, bottom } = el.getBoundingClientRect()
  for (const card of el.querySelectorAll('[data-card-id]')) {
    const r = card.getBoundingClientRect()
    if (r.width === 0 || r.height === 0) continue
    top = Math.min(top, r.top)
    left = Math.min(left, r.left)
    right = Math.max(right, r.right)
    bottom = Math.max(bottom, r.bottom)
  }
  return { top, left, width: right - left, height: bottom - top }
}

export const ALL_SPOTS: readonly SpotId[] = Object.keys(SELECTORS) as SpotId[]
