/**
 * The landing screen's PLAY tier: three questions, asked one at a time.
 *
 * It replaces six preset cards that were drawn from four different questions — `vs AI` and
 * `vs Friend` answered *who fills the seats*, `Draft & Sealed` and `Variants` answered **Cards**,
 * `Multiplayer` answered **Table** and `Tournament` answered **Event** — which is why no two of them
 * read as alternatives to each other. The diagnosis is written up in
 * `backlog/menu-lobby-restructure-and-help.md` § 3a.
 *
 * What it is *not* is a second mode picker. Since the lobby unified (§ 4a) it shows all three axes on
 * both server kinds and can change any of them, so a grid in front of it was a competing taxonomy.
 * This is the *creation* path: it collects the whole selection while nothing exists yet, which is
 * what lets it create the right lobby kind first time instead of making the first change pay a `⇄`
 * recreate.
 *
 * Two rules keep it from feeling like a form:
 *
 * - **A step whose options collapse to one answer is skipped**, and the resolved value shows in the
 *   stepper marked `auto` — decided and visible, rather than silently assumed. `Just me → Bring a
 *   deck` therefore still reaches a lobby in two clicks, which is what the old `vs AI` card cost.
 * - **The stepper is the back button.** All three questions are always on screen, numbered, with the
 *   answer under each; clicking an answer reopens that step and re-validates the ones after it.
 *
 * Everything selectable comes from `lobby/modeMatrix.ts`; this file only renders it.
 */
import { useMemo, useState } from 'react'
import { HelpTip } from '@/components/help/HelpTip'
import {
  cardsChoices,
  defaultCardsAxis,
  defaultSeats,
  flowStages,
  resolveLaunch,
  rosterChoices,
  rosterLabel,
  seatRule,
  selectionSummary,
  shapeChoices,
  shapeLabel,
  subShapeChoices,
  type Choice,
  type LaunchSpec,
  type Roster,
  type Selection,
  type ShapeId,
} from '../lobby/modeMatrix'
import { cardsLabel, type CardsAxis, type CardsKind } from '../lobby/axes'
import styles from './GameUI.module.css'

/** The three questions. */
type AnsweredStep = 'roster' | 'cards' | 'shape'

/** The three steps, plus the state after the last one is answered. */
type StepId = AnsweredStep | 'done'

/** A selection in progress: each answer is null until given. */
interface Draft {
  roster: Roster | null
  cards: CardsAxis | null
  shape: ShapeId | null
  seats: number | null
}

const EMPTY: Draft = { roster: null, cards: null, shape: null, seats: null }

const LAST_LAUNCH_KEY = 'argentum-last-play-selection'

export function PlayWizard({
  aiEnabled,
  onLaunch,
}: {
  aiEnabled: boolean
  /** Create the lobby this selection describes. The wizard never touches the store itself. */
  onLaunch: (spec: LaunchSpec, selection: Selection) => void
}) {
  const [draft, setDraft] = useState<Draft>(EMPTY)
  const lastSelection = useMemo(() => loadLastSelection(aiEnabled), [aiEnabled])

  // Step 3 is only a step when there is more than one shape to pick between. With one, `pickCards`
  // has already resolved it and the grid would be a question with a single answer.
  const shapeIsAQuestion =
    draft.roster !== null && draft.cards !== null &&
    shapeChoices(draft.roster, draft.cards).filter((c) => !c.disabledReason).length > 1

  const step: StepId =
    draft.roster === null ? 'roster'
      : draft.cards === null ? 'cards'
        : shapeIsAQuestion ? 'shape'
          : 'done'

  /**
   * Why the shapes you *didn't* get are unavailable, when step 3 was skipped.
   *
   * Skipping a one-answer step is right, but it hides the disabled tiles — and with them the reasons.
   * For a solo draft that is no loss ("of course a pod plays a bracket"); for a group picking
   * Commander it hides exactly the thing worth saying, which is that Commander has no multiplayer
   * table rather than some limit on how many people can share the pool.
   */
  const skippedShapeReasons: string[] =
    draft.roster !== null && draft.cards !== null && !shapeIsAQuestion
      ? [...new Set(
          shapeChoices(draft.roster, draft.cards)
            .map((c) => c.disabledReason)
            .filter((r): r is string => !!r),
        )]
      : []

  /** Answer step 1. Cards and shape are re-asked, since both depend on the roster. */
  const pickRoster = (roster: Roster) => setDraft({ ...EMPTY, roster })

  /** Answer step 2. Skips step 3 when the roster and Cards value leave only one shape. */
  const pickCards = (cards: CardsAxis) => {
    const roster = draft.roster
    if (roster === null) return
    const open = shapeChoices(roster, cards).filter((c) => !c.disabledReason)
    const only = open.length === 1 ? open[0]!.value : null
    setDraft({
      roster,
      cards,
      shape: only,
      seats: only === null ? null : defaultSeats(seatRule(roster, cards, only)),
    })
  }

  const pickShape = (shape: ShapeId) => {
    const { roster, cards } = draft
    if (roster === null || cards === null) return
    setDraft({ roster, cards, shape, seats: defaultSeats(seatRule(roster, cards, shape)) })
  }

  /**
   * Reopen an answered step, dropping every answer that depended on it.
   *
   * Reopening the shape is only meaningful when it was a question — with one reachable shape,
   * clearing it would land on a screen with nothing to pick and no way forward, so the stepper
   * renders that answer as `auto` and doesn't offer this.
   */
  const reopen = (target: AnsweredStep) => {
    setDraft((d) => {
      switch (target) {
        case 'roster': return EMPTY
        case 'cards': return { ...EMPTY, roster: d.roster }
        case 'shape': return shapeIsAQuestion ? { ...d, shape: null, seats: null } : d
      }
    })
  }

  const launch = (selection: Selection) => {
    saveLastSelection(selection)
    onLaunch(resolveLaunch(selection), selection)
  }

  const complete: Selection | null =
    draft.roster !== null && draft.cards !== null && draft.shape !== null
      ? { roster: draft.roster, cards: draft.cards, shape: draft.shape, seats: draft.seats ?? 2 }
      : null

  return (
    <>
      {lastSelection && step === 'roster' && (
        <button
          type="button"
          className={styles.playAgainChip}
          data-testid="wizard-play-again"
          onClick={() => launch(lastSelection)}
        >
          <span className={styles.playAgainChipLead}>Play again →</span>
          <span className={styles.playAgainChipBody}>{selectionSummary(lastSelection)}</span>
        </button>
      )}

      <WizardStepper
        step={step}
        draft={draft}
        shapeIsAQuestion={shapeIsAQuestion}
        skippedShapeReasons={skippedShapeReasons}
        onReopen={reopen}
      />

      {step === 'roster' && (
        <OptionGrid
          choices={rosterChoices(aiEnabled)}
          selected={null}
          testIdPrefix="wizard-roster"
          testId={(r) => r.toLowerCase()}
          onPick={pickRoster}
        />
      )}

      {step === 'cards' && draft.roster !== null && (
        <CardsStep roster={draft.roster} onPick={pickCards} />
      )}

      {step === 'shape' && draft.roster !== null && draft.cards !== null && (
        <OptionGrid
          choices={shapeChoices(draft.roster, draft.cards)}
          selected={draft.shape}
          testIdPrefix="wizard-shape"
          testId={(s) => s.toLowerCase().replace(/_/g, '-')}
          onPick={pickShape}
        />
      )}

      {complete !== null && (
        <>
          {/* The stages, on their own full-width row rather than a repeat of the stepper: "Open
              boosters → Build a deck → Everyone plays everyone → Standings" is the only place that
              says how many steps this is before you commit to it. */}
          <p className={styles.wizardFlow}>
            {flowStages(complete).map((stage, i) => (
              <span key={stage}>
                {i > 0 && <span className={styles.wizardFlowArrow} aria-hidden> → </span>}
                <span className={styles.wizardFlowStage}>{stage}</span>
              </span>
            ))}
          </p>
          <div className={styles.wizardFooter}>
            <SeatControl
              selection={complete}
              onChange={(seats) => setDraft((d) => ({ ...d, seats }))}
            />
            <button
              type="button"
              className={styles.primaryButton}
              data-testid="wizard-create"
              onClick={() => launch(complete)}
            >
              {complete.roster === 'SOLO' ? 'Start playing' : 'Create lobby'} →
            </button>
          </div>
        </>
      )}
    </>
  )
}

/**
 * All three questions, numbered, with each answer under its own heading.
 *
 * The first version of this was a row of small chips floating to the right of the current step's
 * title, which read as decoration: nothing said they were previous *answers*, that they could be
 * changed, or what order they came in. This says all three — the numbers give the sequence, the
 * question word says what each one decided, and an answered step is a button with a pencil on it.
 *
 * A step that was skipped is shown as `auto` rather than as a button, because there is nothing to
 * pick: with one reachable shape, reopening it would land on a dead screen.
 */
function WizardStepper({
  step,
  draft,
  shapeIsAQuestion,
  skippedShapeReasons,
  onReopen,
}: {
  step: StepId
  draft: Draft
  shapeIsAQuestion: boolean
  /** Reasons the other shapes were unavailable, when step 3 was skipped. */
  skippedShapeReasons: string[]
  onReopen: (step: AnsweredStep) => void
}) {
  const slots: Array<{
    id: AnsweredStep
    word: string
    value: string | null
    /** Decided for you — there was only one possibility. */
    auto: boolean
  }> = [
    { id: 'roster', word: 'Who with', value: draft.roster && rosterLabel(draft.roster), auto: false },
    { id: 'cards', word: 'What with', value: draft.cards && cardsLabel(draft.cards), auto: false },
    {
      id: 'shape',
      word: 'How',
      value: draft.shape && shapeLabel(draft.shape),
      auto: draft.shape !== null && !shapeIsAQuestion,
    },
  ]

  const titles: Record<StepId, string> = {
    roster: 'Who are you playing with?',
    cards: 'What are you playing with?',
    shape: 'How do you play it?',
    done: 'Ready when you are.',
  }

  return (
    <div className={styles.wizardStepper}>
      <ol className={styles.wizardStepperTrack}>
        {slots.map((slot, i) => {
          const isCurrent = step === slot.id
          // An answer shows as soon as it exists, including on the step you are standing on — that
          // step's tiles are already the way to change it, so it needs no pencil of its own.
          const answered = slot.value !== null && !isCurrent
          const currentValue = slot.value !== null && isCurrent
          return (
            <li
              key={slot.id}
              className={[
                styles.wizardStepSlot,
                isCurrent ? styles.wizardStepSlotCurrent : '',
                answered ? styles.wizardStepSlotDone : '',
              ].filter(Boolean).join(' ')}
            >
              <span className={styles.wizardStepNum} aria-hidden>{i + 1}</span>
              <span className={styles.wizardStepBody}>
                <span className={styles.wizardStepWord}>{slot.word}</span>
                {answered ? (
                  slot.auto ? (
                    <span
                      className={styles.wizardStepAuto}
                      title={
                        skippedShapeReasons.length > 0
                          ? `The only option for what you picked. ${skippedShapeReasons.join(' ')}`
                          : 'The only option for what you picked.'
                      }
                    >
                      {slot.value}
                      <span className={styles.wizardStepAutoMark}> auto</span>
                    </span>
                  ) : (
                    <button
                      type="button"
                      className={styles.wizardStepChange}
                      onClick={() => onReopen(slot.id)}
                      data-testid={`wizard-back-${slot.id}`}
                      title={`Change this — currently ${slot.value}`}
                    >
                      {slot.value}
                      <span className={styles.wizardStepPencil} aria-hidden>✎</span>
                    </button>
                  )
                ) : currentValue ? (
                  <span className={styles.wizardStepCurrentValue}>{slot.value}</span>
                ) : (
                  <span className={styles.wizardStepPending}>
                    {isCurrent ? 'choosing…' : '—'}
                  </span>
                )}
              </span>
            </li>
          )
        })}
      </ol>
      <p className={styles.wizardStepTitle}>{titles[step]}</p>
      {/* Stated, not hidden behind a tooltip on a tile that isn't rendered any more. */}
      {step === 'done' && skippedShapeReasons.map((reason) => (
        <p key={reason} className={styles.wizardAutoNote}>{reason}</p>
      ))}
    </div>
  )
}

/**
 * Step 2, plus the sub-shape row Sealed and Draft need.
 *
 * The sub-shape is a second click rather than eight top-level tiles, because "Winston or Grid" is a
 * question only someone who has already chosen to draft can have an opinion about — and its seat
 * limits are what bound step 3.
 */
function CardsStep({ roster, onPick }: { roster: Roster; onPick: (cards: CardsAxis) => void }) {
  const [expanded, setExpanded] = useState<CardsKind | null>(null)
  const subChoices = expanded === null ? null : subShapeChoices(roster, expanded)

  return (
    <>
      <OptionGrid
        choices={cardsChoices(roster)}
        selected={expanded}
        testIdPrefix="wizard-cards"
        testId={(k) => k.toLowerCase().replace(/_/g, '-')}
        onPick={(kind) => {
          const subs = subShapeChoices(roster, kind)
          if (subs === null) {
            onPick(defaultCardsAxis(kind))
            return
          }
          // Sealed and Draft open their sub-shape row instead of committing.
          setExpanded(kind)
        }}
      />
      {subChoices !== null && (
        <div className={styles.wizardSubRow}>
          <span className={styles.wizardSubLabel}>
            {expanded === 'SEALED' ? 'Sealed shape' : 'Draft shape'}
          </span>
          <div className={styles.settingsButtons}>
            {subChoices.map((choice) => (
              <button
                key={subShapeKey(choice.value)}
                type="button"
                disabled={!!choice.disabledReason}
                className={styles.settingsButton}
                title={choice.disabledReason ?? choice.caption}
                data-testid={`wizard-subshape-${subShapeKey(choice.value)}`}
                onClick={() => onPick(choice.value)}
              >
                {choice.label}
              </button>
            ))}
          </div>
        </div>
      )}
    </>
  )
}

function subShapeKey(cards: CardsAxis): string {
  if (cards.kind === 'SEALED' || cards.kind === 'DRAFT') {
    return `${cards.kind}-${cards.shape}`.toLowerCase()
  }
  return cards.kind.toLowerCase()
}

/**
 * How many seats the lobby opens with — an optional narrowing, not a question to get past.
 *
 * It defaults to the maximum and the caption says so, because for a group the count is a *cap*:
 * `startBlockReason` only ever counts the players actually present, so the host starts when everyone
 * has arrived and nobody has to predict the number up front. `TournamentLobbySettings` has the same
 * control, so it stays changeable afterwards too.
 *
 * Only rendered when there is something to narrow: a pair is always two, Two-Headed Giant is always
 * four. The allowed counts are an explicit list rather than a range because Team vs. Team needs an
 * even number — a slider with holes in it is a control that lets you pick something the start button
 * then refuses.
 */
function SeatControl({
  selection,
  onChange,
}: {
  selection: Selection
  onChange: (seats: number) => void
}) {
  const rule = seatRule(selection.roster, selection.cards, selection.shape)
  if (rule.fixed || rule.values.length <= 1) return null
  return (
    <span className={styles.wizardSeats}>
      <span className={styles.wizardSeatsRow}>
        <span className={styles.wizardSubLabel}>{rule.label}</span>
        <div className={styles.settingsButtons}>
          {rule.values.map((n) => (
            <button
              key={n}
              type="button"
              className={`${styles.settingsButton} ${n === selection.seats ? styles.settingsButtonActive : ''}`}
              data-testid={`wizard-seats-${n}`}
              onClick={() => onChange(n)}
            >
              {n}
            </button>
          ))}
        </div>
      </span>
      <p className={styles.wizardSeatsCaption}>{rule.caption}</p>
    </span>
  )
}

/**
 * One step's options.
 *
 * Disabled tiles stay visible with their reason on hover, which is the same rule the lobby's axis
 * rows follow: an option you can see and can't use teaches the shape of the system, while an option
 * that isn't rendered just looks like nobody thought of it. Every one of them is a Phase 5 gap.
 */
function OptionGrid<V extends string>({
  choices,
  selected,
  testIdPrefix,
  testId,
  onPick,
}: {
  choices: Choice<V>[]
  selected: V | null
  testIdPrefix: string
  testId: (value: V) => string
  onPick: (value: V) => void
}) {
  return (
    <div className={styles.presetGrid}>
      {choices.map((choice) => (
        // A wrapper div rather than one big button: the HelpTip is itself a button, and nesting
        // interactive elements is invalid HTML (and unreachable by keyboard).
        <div
          key={choice.value}
          className={`${styles.presetCard} ${choice.value === selected ? styles.presetCardSelected : ''}`}
        >
          <span className={styles.presetCardHelp}>
            <HelpTip topicId={choice.topicId} label={`What is ${choice.label}?`} size="sm" />
          </span>
          <button
            type="button"
            disabled={!!choice.disabledReason}
            onClick={() => onPick(choice.value)}
            data-testid={`${testIdPrefix}-${testId(choice.value)}`}
            className={styles.presetCardButton}
            title={choice.disabledReason ?? ''}
          >
            <span className={styles.presetCardTitle}>{choice.label}</span>
            <span className={styles.presetCardTagline}>
              {choice.disabledReason ?? choice.caption}
            </span>
            {choice.badge && !choice.disabledReason && (
              <span
                className={`${styles.presetCardBadge} ${
                  choice.badge.weight === 'EVENT'
                    ? styles.presetCardBadge_event
                    : styles.presetCardBadge_quick
                }`}
              >
                {choice.badge.text}
              </span>
            )}
          </button>
        </div>
      ))}
    </div>
  )
}

/* ── Play again ─────────────────────────────────────────────────────────────
 * A wizard is excellent once and tedious the fifth time. The last completed selection comes back as
 * a one-click chip — the honest answer to that, and the reason the wizard can afford to be explicit.
 * ─────────────────────────────────────────────────────────────────────────── */

function saveLastSelection(selection: Selection): void {
  try {
    localStorage.setItem(LAST_LAUNCH_KEY, JSON.stringify(selection))
  } catch {
    // Private browsing / full quota — the chip is a convenience, so failing to store is not an error.
  }
}

/**
 * Re-validate on the way in rather than trusting it: the stored selection may predate a server whose
 * AI is switched off, or a build where the combination has changed shape.
 */
function loadLastSelection(aiEnabled: boolean): Selection | null {
  let parsed: unknown
  try {
    const raw = localStorage.getItem(LAST_LAUNCH_KEY)
    if (!raw) return null
    parsed = JSON.parse(raw)
  } catch {
    return null
  }
  if (typeof parsed !== 'object' || parsed === null) return null
  const { roster, cards, shape, seats } = parsed as Partial<Selection>
  if (!roster || !cards || !shape || typeof seats !== 'number') return null
  if (roster === 'SOLO' && !aiEnabled) return null

  const cardsOk = cardsChoices(roster).some((c) => c.value === cards.kind && !c.disabledReason)
  const shapeOk = shapeChoices(roster, cards).some((c) => c.value === shape && !c.disabledReason)
  if (!cardsOk || !shapeOk) return null

  const rule = seatRule(roster, cards, shape)
  return { roster, cards, shape, seats: rule.values.includes(seats) ? seats : defaultSeats(rule) }
}
