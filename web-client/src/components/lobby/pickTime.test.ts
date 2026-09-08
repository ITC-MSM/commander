import { describe, it, expect } from 'vitest'
import { NO_PICK_TIME_LIMIT, PICK_TIME_OPTIONS, pickTimeChip, pickTimeLabel, pickTimePhrase } from './pickTime'

describe('pick timer settings', () => {
  it('offers the untimed option last, after the real durations', () => {
    expect(PICK_TIME_OPTIONS).toContain(NO_PICK_TIME_LIMIT)
    expect(PICK_TIME_OPTIONS[PICK_TIME_OPTIONS.length - 1]).toBe(NO_PICK_TIME_LIMIT)
    // Every other option is a usable duration — a stray 0 elsewhere would render as "No limit".
    expect(PICK_TIME_OPTIONS.slice(0, -1).every((n) => n > 0)).toBe(true)
  })

  it('names the untimed setting rather than showing it as zero seconds', () => {
    expect(pickTimeLabel(NO_PICK_TIME_LIMIT)).toBe('No limit')
    expect(pickTimeChip(NO_PICK_TIME_LIMIT)).toBe('no timer')
    expect(pickTimePhrase(NO_PICK_TIME_LIMIT, 'pick')).toBe('no pick timer')
    expect(pickTimePhrase(NO_PICK_TIME_LIMIT, 'turn')).toBe('no turn timer')
  })

  it('renders a real duration as seconds', () => {
    expect(pickTimeLabel(45)).toBe('45s')
    expect(pickTimeChip(45)).toBe('45s')
    expect(pickTimePhrase(45, 'pick')).toBe('45s per pick')
    expect(pickTimePhrase(90, 'turn')).toBe('90s per turn')
  })
})
