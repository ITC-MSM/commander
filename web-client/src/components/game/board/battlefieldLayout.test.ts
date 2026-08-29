import { describe, expect, it } from 'vitest'
import {
  ABSOLUTE_MIN_CARD_WIDTH,
  EMPTY_ROW,
  PREFERRED_MIN_CARD_WIDTH,
  SLOT_MAX_CARD_WIDTH,
  SLOT_SPLIT_MIN,
  rowMinHeightFor,
  slotHeightNeeded,
  solvePooledLayout,
  solveSlotLayout,
  type BoardStats,
  type LayoutEnv,
  type RowStats,
} from './battlefieldLayout'

// Desktop responsive values (useResponsive.ts): 8 px gaps, 18 px stack peeks.
const DESKTOP: LayoutEnv = { cardGap: 8, stackOffset: 18 }
const PHONE: LayoutEnv = { cardGap: 2, stackOffset: 12 }

// The reference slot from docs/plans/battlefield-sparse-layout.md — measured off a
// 1546×795 two-player window. Today's solver renders 66 px cards on it for any
// board with both rows populated.
const SLOT_W = 1200
const SLOT_H = 290

const row = (count: number, tapped = 0, stackedExtra = 0): RowStats => ({ count, tapped, stackedExtra })
const board = (front: RowStats, back: RowStats): BoardStats => ({ front, back })

describe('solveSlotLayout', () => {
  it('an empty front row costs no line: lands-only grows to one tall line', () => {
    const layout = solveSlotLayout(SLOT_W, SLOT_H, board(EMPTY_ROW, row(1)), DESKTOP)
    expect(layout.frontLines).toBe(0)
    expect(layout.backLines).toBe(1)
    expect(layout.cardWidth).toBe(168)
    expect(rowMinHeightFor(layout.frontLines, layout.cardHeight, DESKTOP.cardGap)).toBe(0)
  })

  it('a sparse board with both rows pays the divider, at margins scaled to the rendered card', () => {
    const layout = solveSlotLayout(SLOT_W, SLOT_H, board(row(1), row(2)), DESKTOP)
    expect(layout).toMatchObject({ frontLines: 1, backLines: 1, cardWidth: 76 })
    // The whole budget is spent: one more pixel of card no longer fits.
    expect(slotHeightNeeded(board(row(1), row(2)), 1, 1, 76, DESKTOP, false)).toBeLessThanOrEqual(SLOT_H)
    expect(slotHeightNeeded(board(row(1), row(2)), 1, 1, 77, DESKTOP, false)).toBeGreaterThan(SLOT_H)
  })

  it('is height-bound until a row wraps: tapping every land changes nothing', () => {
    const untapped = solveSlotLayout(SLOT_W, SLOT_H, board(row(6), row(6)), DESKTOP)
    const tapped = solveSlotLayout(SLOT_W, SLOT_H, board(row(6), row(6, 6)), DESKTOP)
    expect(untapped.cardWidth).toBe(76)
    expect(tapped.cardWidth).toBe(76)
  })

  it('wraps a crowded row into more lines when that yields a larger card', () => {
    const layout = solveSlotLayout(SLOT_W, SLOT_H, board(row(24), row(6)), DESKTOP)
    expect(layout.frontLines).toBeGreaterThan(1)
    // A single line of 24 stacks would cap the card at (1200 − 23 × 8) / 24 = 42 px.
    expect(layout.cardWidth).toBeGreaterThan(42)
  })

  it('never returns a card wider than the slot ceiling or narrower than the floor', () => {
    const roomy = solveSlotLayout(1600, 900, board(row(1), row(1)), DESKTOP)
    expect(roomy.cardWidth).toBe(SLOT_MAX_CARD_WIDTH)
    const phone = solveSlotLayout(360, 400, board(row(60), row(60)), PHONE)
    expect(phone.cardWidth).toBe(ABSOLUTE_MIN_CARD_WIDTH)
    expect(phone.frontLines).toBe(4)
  })

  it('a LayoutEnv ceiling caps growth: a lone permanent renders at the base card, not the slot max', () => {
    // The app passes useResponsive's battlefieldCardWidth (125 px on desktop) as the ceiling.
    const env: LayoutEnv = { ...DESKTOP, maxCardWidth: 125 }
    expect(solveSlotLayout(1600, 900, board(EMPTY_ROW, row(1)), env).cardWidth).toBe(125)
    expect(solveSlotLayout(SLOT_W, SLOT_H, board(EMPTY_ROW, row(1)), env).cardWidth).toBe(125)
    const pooled = solvePooledLayout(SLOT_W, 2 * SLOT_H, board(EMPTY_ROW, row(1)), board(EMPTY_ROW, row(1)), env)
    expect(pooled.cardWidth).toBe(125)
    // The ceiling only binds when the slot would allow more.
    expect(solveSlotLayout(SLOT_W, SLOT_H, board(row(1), row(2)), env).cardWidth).toBe(76)
  })

  it('trades the breathing gap for size before dropping under the readability floor', () => {
    // 12 stacks per row on a short slot: comfortable pass lands under 60, tight pass recovers it.
    const layout = solveSlotLayout(1200, 236, board(row(12), row(12)), DESKTOP)
    expect(layout.cardWidth).toBeGreaterThanOrEqual(PREFERRED_MIN_CARD_WIDTH)
    expect(
      slotHeightNeeded(board(row(12), row(12)), layout.frontLines, layout.backLines, layout.cardWidth, DESKTOP, true),
    ).toBeLessThanOrEqual(236)
  })

  it('renders the back row at BACK_ROW_SCALE and reclaims the height for the front row', () => {
    const scaled = solveSlotLayout(SLOT_W, SLOT_H, board(row(1), row(2)), { ...DESKTOP, backRowScale: 0.7 })
    expect(scaled.cardWidth).toBeGreaterThan(76)
    expect(scaled.backCardWidth).toBe(Math.round(scaled.cardWidth * 0.7))
  })
})

describe('solvePooledLayout', () => {
  const POOLED = 2 * SLOT_H

  it('gives both players one width and the crowded side the height it needs', () => {
    const pooled = solvePooledLayout(SLOT_W, POOLED, board(row(6), row(6)), board(EMPTY_ROW, row(1)), DESKTOP)
    expect(pooled.player.cardWidth).toBe(pooled.cardWidth)
    expect(pooled.opponent.cardWidth).toBe(pooled.cardWidth)
    // Per-slot the crowded side would be 76 px; pooled it grows to the clamp.
    expect(pooled.cardWidth).toBeGreaterThan(76)
    expect(pooled.playerHeight).toBeGreaterThan(pooled.opponentHeight)
    expect(pooled.playerHeight + pooled.opponentHeight).toBeCloseTo(POOLED, 6)
  })

  it('clamps the split so the sparse side keeps SLOT_SPLIT_MIN of the pool', () => {
    const pooled = solvePooledLayout(SLOT_W, POOLED, board(row(6), row(6)), board(EMPTY_ROW, row(1)), DESKTOP)
    expect(pooled.opponentHeight).toBeGreaterThanOrEqual(SLOT_SPLIT_MIN * POOLED - 1e-9)
    expect(pooled.playerHeight).toBeLessThanOrEqual((1 - SLOT_SPLIT_MIN) * POOLED + 1e-9)
  })

  it('lets a wrapping board take a third line without shrinking the other side', () => {
    const perSlot = solveSlotLayout(SLOT_W, SLOT_H, board(row(24), row(6)), DESKTOP)
    const pooled = solvePooledLayout(SLOT_W, POOLED, board(row(24), row(6)), board(row(1), row(1)), DESKTOP)
    expect(pooled.player.frontLines).toBe(2)
    expect(pooled.cardWidth).toBeGreaterThan(perSlot.cardWidth)
  })

  it('splits evenly when both boards are the same shape', () => {
    const pooled = solvePooledLayout(SLOT_W, POOLED, board(row(3), row(4)), board(row(3), row(4)), DESKTOP)
    expect(pooled.playerHeight).toBeCloseTo(pooled.opponentHeight, 6)
    expect(pooled.cardWidth).toBe(solveSlotLayout(SLOT_W, SLOT_H, board(row(3), row(4)), DESKTOP).cardWidth)
  })

  it('falls back to the floor with a need-proportional split when nothing fits', () => {
    const pooled = solvePooledLayout(360, 300, board(row(30), row(30)), board(row(30), row(30)), PHONE)
    expect(pooled.cardWidth).toBe(ABSOLUTE_MIN_CARD_WIDTH)
    expect(pooled.playerHeight + pooled.opponentHeight).toBeCloseTo(300, 6)
  })
})
