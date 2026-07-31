/**
 * One settings group: a header that never hides, over refinements that can be put away.
 *
 * ```
 * Cards ?  ( Bring a deck | Random | Momir | Sealed | Draft ⇄ )
 *          ECL + BLB · 6 packs · 45s                 Hide ⌃
 * ```
 *
 * **Open is the default** — the collapsing here is a host's "I'm done with this one", not the state
 * the panel arrives in. See `useGroupOpenState` for why that flipped.
 *
 * The header carries the axis's own buttons rather than putting them behind the chevron, and that is
 * the whole design. Phase 4's marquee payoff was that someone who entered through "vs AI" can switch
 * the Table to Free-for-All without backing out to the home screen; burying the axis strip would
 * half-undo it, and would also be the version of this that *does* violate the disabled-with-reason
 * rule. What a host can collapse is the sub-rows — deck legality, sets, pack counts, timers — plus
 * the multi-line captions that make each of those rows cost 75–90px.
 *
 * The summary line is not decoration. A collapsed group has to answer "what is this set to?" without
 * being opened, or the host has to re-open all five to find the one that is wrong.
 *
 * **The summary line is also the toggle.** It used to sit beside a bare `⌄` glyph in
 * `--text-disabled`, 28px wide and unlabelled, and the two together read as a status readout with a
 * decoration next to it — nothing on the row looked pressable, which is half of how a host ends up
 * never finding the attack rule or the public/private switch. So the whole line is one button with a
 * hit target the width of the header, a hover state, and the word `Edit`/`Hide` beside the chevron.
 *
 * A collapsed group also names the options a host cannot guess are inside — `+ Attack rule`,
 * `+ Private / public` — from {@link situationalOptions}. Only the shape-dependent ones: listing
 * every row would be a second summary line, and the summary already names the values.
 *
 * Not a scroll container, deliberately: `.lobbyOverlay` is the only one on this screen, which is the
 * lesson recorded in `GameUI.module.css` after an earlier settings panel with its own `max-height`
 * produced three competing scrollbars.
 */
import type { ReactNode } from 'react'
import { HelpTip } from '@/components/help/HelpTip'
import styles from '../ui/GameUI.module.css'

export function SettingsGroup({
  label,
  topicId,
  summary,
  axisStrip,
  blocking,
  situational = [],
  revealed = [],
  open,
  onToggle,
  testId,
  children,
}: {
  label: string
  /** Help topic for the *value in effect*, so `?` explains what is selected. */
  topicId: string | null
  /** The live values inside, shown while collapsed. */
  summary: string
  /** The axis's buttons — always visible, never behind the chevron. */
  axisStrip?: ReactNode
  /** This group holds the reason Start is disabled. */
  blocking?: string | undefined
  /** Options in here that only exist in this lobby's shape — named while collapsed. */
  situational?: readonly string[]
  /** Of those, the ones that appeared just now, which is also why the group opened itself. */
  revealed?: readonly string[]
  open: boolean
  onToggle: () => void
  testId: string
  /** The refinements. Rendered only when open — and only when there are any. */
  children?: ReactNode
}) {
  const hasBody = Boolean(children)

  const summaryLine = (
    <>
      <span className={styles.settingsGroupSummary}>{summary}</span>
      {blocking && (
        <span className={styles.settingsGroupBlockingNote} title={blocking}>! {blocking}</span>
      )}
      {/* One chip each rather than a `·`-joined run: the summary beside them is `·`-joined too, and
          "private · AI assist off + Private / public · AI assistance" reads as one list of five
          things. Once open the controls speak for themselves and the chips go away. */}
      {hasBody && !open && situational.map((option) => (
        <span key={option} className={styles.settingsGroupSituational}>+ {option}</span>
      ))}
      {open && revealed.length > 0 && (
        <span className={styles.settingsGroupRevealed}>New: {revealed.join(' · ')}</span>
      )}
    </>
  )

  return (
    <div
      className={`${styles.settingsGroup} ${blocking ? styles.settingsGroupBlocking : ''}`}
      data-testid={`settings-group-${testId}`}
      data-open={open}
    >
      <div className={styles.settingsGroupHeader}>
        <div className={styles.settingsGroupLabel}>
          <span>{label}</span>
          {topicId && <HelpTip topicId={topicId} label={`What is ${label}?`} size="sm" />}
        </div>
        <div className={styles.settingsGroupMain}>
          {axisStrip}
          {hasBody ? (
            <button
              type="button"
              className={styles.settingsGroupToggle}
              onClick={onToggle}
              aria-expanded={open}
              aria-label={`${open ? 'Hide' : 'Show'} ${label} settings`}
              data-testid={`settings-group-toggle-${testId}`}
            >
              {summaryLine}
              <span className={styles.settingsGroupMore}>
                {open ? 'Hide ⌃' : 'Edit ⌄'}
              </span>
            </button>
          ) : (
            <div className={styles.settingsGroupSummaryRow}>{summaryLine}</div>
          )}
        </div>
      </div>
      {hasBody && open && <div className={styles.settingsGroupBody}>{children}</div>}
    </div>
  )
}
