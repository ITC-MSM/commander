import { describe, expect, it } from 'vitest'
import type { EntityId } from '@/types'
import { bandIsLegal, mergedBand } from './combatBands'

const id = (s: string) => s as EntityId
const banding = new Set(['knight', 'squire'])
const hasBanding = (x: EntityId) => banding.has(x)

describe('mergedBand', () => {
  it('forms a fresh pair when neither creature is banded', () => {
    expect(mergedBand([], id('knight'), id('bear'))).toEqual(['knight', 'bear'])
  })

  it('folds both existing bands into one', () => {
    const bands = [[id('knight'), id('bear')], [id('squire'), id('wolf')]]
    expect(mergedBand(bands, id('knight'), id('squire'))).toEqual(['knight', 'squire', 'bear', 'wolf'])
  })
})

describe('bandIsLegal (CR 702.22c)', () => {
  it('allows one non-banding creature alongside banders', () => {
    expect(bandIsLegal([id('knight'), id('squire'), id('bear')], hasBanding)).toBe(true)
  })

  it('refuses two creatures without banding', () => {
    expect(bandIsLegal([id('bear'), id('wolf')], hasBanding)).toBe(false)
    expect(bandIsLegal([id('knight'), id('bear'), id('wolf')], hasBanding)).toBe(false)
  })
})
