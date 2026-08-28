/**
 * The coach panel for a course mission. Mounted on every game board, renders nothing unless the
 * course armed it (see `learn/coach.ts`). Three states:
 *
 * - **Tour** — when the board first appears, a few steps that each ring one part of the table
 *   (hand, battlefield, turn strip, the pass button …) and say what it is for. Skippable.
 * - **Tip** — one line for what the board is waiting on, worded with the real button label and
 *   this device's gestures, with a quiet ring on the thing it names; the mission's objectives
 *   tick off underneath as the player does them.
 * - **Done** — the game ended: what you learned, and the next card in the hand.
 *
 * Portalled to `<body>` like the help drawer: `#root` is overflow:hidden and the multiplayer
 * strip transforms its subtree, both of which break `position: fixed` for descendants.
 */
import { useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'
import { useNavigate } from 'react-router-dom'
import { useGameStore } from '@/store/gameStore'
import { selectHasPriority, selectIsMyTurn, useStackCards } from '@/store/selectors'
import { armedMission, coachTip, disarmCoach, markTourSeen, tourSeen, wordTip, type CoachView } from '@/learn/coach'
import { learnHref, missionById, nextMission } from '@/learn/missions'
import type { SpotContext } from '@/learn/spots'
import type { EntityId } from '@/types'
import { useLearnProgress } from '@/learn/progressStore'
import { LearnSpotlight } from './LearnSpotlight'
import styles from './LearnCoach.module.css'

/** A mouse or trackpad: hover previews work and "click" is the word. Touch screens get long-press and "tap". */
function deviceHasHover(): boolean {
  try {
    return window.matchMedia('(hover: hover) and (pointer: fine)').matches
  } catch {
    return true
  }
}

export function LearnCoach() {
  const [missionId] = useState(armedMission)
  const mission = useMemo(() => missionById(missionId), [missionId])
  const [collapsed, setCollapsed] = useState(false)
  const [tourStep, setTourStep] = useState<number | null>(() => (tourSeen() ? null : 0))
  const [hasHover] = useState(deviceHasHover)
  // Once the game is over the coach disarms itself, so a remount after that renders nothing —
  // which is also what stops the mission being finished twice.
  const [finished, setFinished] = useState(false)
  const navigate = useNavigate()
  const finish = useLearnProgress((s) => s.finish)
  const returnToMenu = useGameStore((s) => s.returnToMenu)

  const gameState = useGameStore((s) => s.gameState)
  const legalActions = useGameStore((s) => s.legalActions)
  const pendingDecision = useGameStore((s) => s.pendingDecision)
  const isTargeting = useGameStore((s) => s.targetingState !== null)
  const gameOverState = useGameStore((s) => s.gameOverState)
  const nextStopPoint = useGameStore((s) => s.nextStopPoint)
  const isMyTurn = useGameStore(selectIsMyTurn)
  const hasPriority = useGameStore(selectHasPriority)
  const stackSize = useStackCards().length

  const won = gameOverState ? (gameOverState.result === 'draw' ? null : gameOverState.result === 'win') : null

  const view = useMemo<CoachView | null>(() => {
    if (!gameState) return null
    const types = new Set(legalActions.map((a) => a.actionType))
    return {
      turnNumber: gameState.turnNumber,
      step: gameState.currentStep,
      isMyTurn,
      hasPriority,
      canPlayLand: types.has('PlayLand'),
      canCast: types.has('CastSpell'),
      canAttack: types.has('DeclareAttackers'),
      canBlock: types.has('DeclareBlockers'),
      hasDecision: pendingDecision !== null,
      isTargeting,
      stackSize,
      attackersIncoming: gameState.combat?.attackers.length ?? 0,
      passLabel: nextStopPoint ?? 'Pass',
      hasHover,
      isGameOver: gameState.isGameOver || gameOverState !== null,
      won,
    }
  }, [gameState, legalActions, pendingDecision, isTargeting, gameOverState, nextStopPoint, isMyTurn, hasPriority, stackSize, hasHover, won])

  const me = gameState?.viewingPlayerId
  const players = gameState?.players
  const spotCtx = useMemo<SpotContext>(
    () => ({
      me: me ?? ('' as EntityId),
      opponent: players?.find((p) => p.playerId !== me)?.playerId,
    }),
    [me, players],
  )

  const objectives = useMemo(() => {
    if (!mission || !gameState) return []
    const ctx = { state: gameState, me: gameState.viewingPlayerId, won }
    return mission.objectives.map((o) => ({ id: o.id, label: o.label, done: o.done(ctx) }))
  }, [mission, gameState, won])

  // The game ending is what finishes the mission, win or lose. Disarm so the next game — a
  // rematch, or anything started from the menu — plays without a coach.
  useEffect(() => {
    if (mission && view?.isGameOver && !finished) {
      finish(mission.id)
      disarmCoach()
      setFinished(true)
    }
  }, [mission, view?.isGameOver, finished, finish])

  if (!mission || !view) return null

  const tip = coachTip(view, mission.hints)
  const next = nextMission(mission.id)
  const doneCount = objectives.filter((o) => o.done).length
  const touring = tourStep !== null && !view.isGameOver && tourStep < mission.tour.length
  const step = touring ? mission.tour[tourStep] : undefined

  const endTour = () => {
    markTourSeen()
    setTourStep(null)
  }

  const leave = (to: string) => {
    returnToMenu()
    navigate(to)
  }

  if (collapsed && !view.isGameOver) {
    return createPortal(
      <button
        type="button"
        className={`${styles.pill} ${styles[tip.tone]}`}
        onClick={() => setCollapsed(false)}
        aria-label="Show the coach"
      >
        <span className={styles.pillDot} aria-hidden="true" />
        Coach · {doneCount}/{objectives.length}
      </button>,
      document.body,
    )
  }

  const tone = touring ? 'watch' : tip.tone
  const spot = touring ? step?.spot : tip.tone === 'act' ? tip.spot : undefined

  return createPortal(
    <>
      <LearnSpotlight spot={spot} ctx={spotCtx} strong={touring} />
      <aside className={`${styles.coach} ${styles[tone]}`} aria-live="polite" aria-label="Coach">
        <div className={styles.head}>
          <span className={styles.eyebrow}>
            Mission {mission.number} · {mission.title}
          </span>
          {!view.isGameOver && (
            <button
              type="button"
              className={styles.close}
              onClick={() => setCollapsed(true)}
              aria-label="Tuck the coach away"
              title="Tuck the coach away — click the pill to bring it back"
            >
              –
            </button>
          )}
        </div>

        {touring && step ? (
          <div key={`tour-${tourStep}`} className={styles.body}>
            <div className={styles.tourCount}>
              A look around the table · {tourStep + 1} of {mission.tour.length}
            </div>
            <div className={styles.title}>{wordTip(step.title, view)}</div>
            <p className={styles.text}>{wordTip(step.body, view)}</p>
            <div className={styles.tourNav}>
              {tourStep > 0 ? (
                <button type="button" className={styles.link} onClick={() => setTourStep(tourStep - 1)}>
                  ← Back
                </button>
              ) : (
                <button type="button" className={styles.link} onClick={endTour}>
                  Skip the tour
                </button>
              )}
              {tourStep + 1 < mission.tour.length ? (
                <button type="button" className={styles.primary} onClick={() => setTourStep(tourStep + 1)}>
                  Next →
                </button>
              ) : (
                <button type="button" className={styles.primary} onClick={endTour}>
                  Let’s play
                </button>
              )}
            </div>
          </div>
        ) : view.isGameOver ? (
          <div key={tip.key} className={styles.body}>
            <div className={styles.title}>{tip.title}</div>
            <p className={styles.text}>{tip.body}</p>
            <div className={styles.lessonsHead}>What you now know</div>
            <ul className={styles.lessons}>
              {mission.lessons.map((line) => (
                <li key={line}>{wordTip(line, view)}</li>
              ))}
            </ul>
          </div>
        ) : (
          <div key={tip.key} className={styles.body}>
            <div className={styles.title}>{tip.title}</div>
            <p className={styles.text}>{tip.body}</p>
          </div>
        )}

        <ol className={styles.objectives} aria-label={`Objectives, ${doneCount} of ${objectives.length} done`}>
          {objectives.map((o) => (
            <li key={o.id} className={`${styles.objective} ${o.done ? styles.objectiveDone : ''}`}>
              <span className={styles.tick} aria-hidden="true">
                {o.done ? '✓' : ''}
              </span>
              <span>{o.label}</span>
            </li>
          ))}
        </ol>

        {view.isGameOver && (
          <div className={styles.actions}>
            {next ? (
              <button type="button" className={styles.primary} onClick={() => leave(learnHref(next.id))}>
                Next: {next.title} →
              </button>
            ) : (
              <button type="button" className={styles.primary} onClick={() => leave(learnHref())}>
                Course complete →
              </button>
            )}
            <button type="button" className={styles.link} onClick={() => leave(learnHref(mission.id))}>
              Play this one again
            </button>
          </div>
        )}
      </aside>
    </>,
    document.body,
  )
}
