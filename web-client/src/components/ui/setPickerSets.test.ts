import { describe, expect, it } from 'vitest'
import type { AvailableSet } from '@/types/messages'
import { visiblePickerSets } from './setPickerSets'

const fra: AvailableSet = { code: 'FRA', name: 'Reality Fracture', partial: true, implementedCount: 25 }
const full: AvailableSet = { code: 'BLB', name: 'Bloomburrow', partial: false, implementedCount: 261 }
const bonus: AvailableSet = { code: 'BIG', name: 'The Big Score', extensionSet: true, implementedCount: 30 }
const options = { search: '', single: false, showPartial: false, minCards: 30 }

describe('set picker discovery', () => {
  it('finds Reality Fracture by name or code despite default browse filters', () => {
    for (const search of ['reality fracture', ' FRA ']) {
      expect(visiblePickerSets([fra, full], [], { ...options, search })).toEqual([fra])
    }
  })
  it('preserves browse filters and selected sets when search is cleared', () => {
    expect(visiblePickerSets([fra, full], [], options)).toEqual([full])
    expect(visiblePickerSets([fra, full], ['FRA'], options)).toEqual([fra, full])
    expect(visiblePickerSets([fra], [], { ...options, showPartial: true, minCards: 0 })).toEqual([fra])
  })
  it('keeps extension sets out of single-set games even when searched', () => {
    expect(visiblePickerSets([bonus], [], { ...options, search: 'big', single: true })).toEqual([])
    expect(visiblePickerSets([bonus], [], { ...options, search: 'big' })).toEqual([bonus])
  })
})
