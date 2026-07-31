/**
 * One collapsible settings group: an axis strip that never hides, over refinements that do.
 *
 * ```
 * Cards ?  ( Bring a deck | Random | Momir | Sealed | Draft ⇄ )
 *          ECL + BLB · 6 packs · 45s                        ⌄
 * ```
 *
 * The header carries the axis's own buttons rather than putting them behind the chevron, and that is
 * the whole design. Phase 4's marquee payoff was that someone who entered through "vs AI" can switch
 * the Table to Free-for-All without backing out to the home screen; burying the axis strip would
 * half-undo it, and would also be the version of this that *does* violate the disabled-with-reason
 * rule. What collapses is the sub-rows — deck legality, sets, pack counts, timers — plus the
 * multi-line captions that make each of those rows cost 75–90px.
 *
 * The summary line is not decoration. A collapsed group has to answer "what is this set to?" without
 * being opened, or the host has to open all five to find the one that is wrong.
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
  open: boolean
  onToggle: () => void
  testId: string
  /** The refinements. Rendered only when open — and only when there are any. */
  children?: ReactNode
}) {
  const hasBody = Boolean(children)

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
          <div className={styles.settingsGroupSummaryRow}>
            <span className={styles.settingsGroupSummary}>{summary}</span>
            {blocking && (
              <span className={styles.settingsGroupBlockingNote} title={blocking}>! {blocking}</span>
            )}
          </div>
        </div>
        {hasBody && (
          <button
            type="button"
            className={styles.settingsGroupChevron}
            onClick={onToggle}
            aria-expanded={open}
            aria-label={`${open ? 'Hide' : 'Show'} ${label} settings`}
            data-testid={`settings-group-toggle-${testId}`}
          >
            {open ? '⌃' : '⌄'}
          </button>
        )}
      </div>
      {hasBody && open && <div className={styles.settingsGroupBody}>{children}</div>}
    </div>
  )
}
