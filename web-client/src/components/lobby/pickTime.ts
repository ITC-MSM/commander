/**
 * The draft pick timer, and its "no time limit" setting.
 *
 * A `pickTimeSeconds` of exactly {@link NO_PICK_TIME_LIMIT} means the draft runs no timer at
 * all: the server never starts a countdown and never auto-picks, so players take as long as
 * they like. On the wire that shows up as a `null` `timeRemainingSeconds` rather than a `0`,
 * so no client can read an untimed pick as "out of time".
 */

/** Sentinel `pickTimeSeconds` value meaning the draft has no pick timer. */
export const NO_PICK_TIME_LIMIT = 0

/** Pick-timer choices offered in lobby settings, in menu order. */
export const PICK_TIME_OPTIONS: readonly number[] = [30, 45, 60, 90, 120, NO_PICK_TIME_LIMIT]

/** Menu label for one option — `"45s"`, or `"No limit"` for the untimed setting. */
export function pickTimeLabel(seconds: number): string {
  return seconds === NO_PICK_TIME_LIMIT ? 'No limit' : `${seconds}s`
}

/** Compact summary-chip text — `"45s"`, or `"no timer"` for the untimed setting. */
export function pickTimeChip(seconds: number): string {
  return seconds === NO_PICK_TIME_LIMIT ? 'no timer' : `${seconds}s`
}

/** Sentence fragment — `"45s per pick"`, or `"no pick timer"` for the untimed setting. */
export function pickTimePhrase(seconds: number, unit: 'pick' | 'turn'): string {
  return seconds === NO_PICK_TIME_LIMIT ? `no ${unit} timer` : `${seconds}s per ${unit}`
}
