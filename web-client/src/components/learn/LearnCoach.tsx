/**
 * The coach panel for a course mission. Mounted on every game board, renders nothing unless the
 * course armed it (see `learn/coach.ts`); then shows the mission's objectives ticking off as the
 * player does them, and one tip for what the board is waiting on. When the game ends it marks
 * the mission finished and points at the next card in the hand.
 *
 * Portalled to `<body>` like the help drawer: `#root` is overflow:hidden and the multiplayer
 * strip transforms its subtree, both of which break `position: fixed` for descendants.
 */
import { useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'
import { useNavigate } from 'react-router-dom'
import { useGameStore } from '@/store/gameStore'
import { selectHasPriority, selectIsMyTurn } from '@/store/selectors'
import { armedMission, coachTip, disarmCoach, introSeen, markIntroSeen, type CoachView } from '@/learn/coach'
import { learnHref, missionById, nextMission } from '@/learn/missions'
import { useLearnProgress } from '@/learn/progressStore'
import styles from './LearnCoach.module.css'

export function LearnCoach() {
  const [missionId] = useState(armedMission)
  const mission = useMemo(() => missionById(missionId), [missionId])
  const [hidden, setHidden] = useState(false)
  const [showIntro, setShowIntro] = useState(() => !introSeen())
  // Once the game is over the coach disarms itself, so a remount after that renders nothing —
  // which is also what stops the mission being finished twice.
  const [finished, setFinished] = useState(false)
  const navigate = useNavigate()
  const finish = useLearnProgress((s) => s.finish)
  const returnToMenu = useGameStore((s) => s.returnToMenu)

  const gameState = useGameStore((s) => s.gameState)
  const legalActions = useGameStore((s) => s.legalActions)
  const pendingDecision = useGameStore((s) => s.pendingDecision)
  const gameOverState = useGameStore((s) => s.gameOverState)
  const nextStopPoint = useGameStore((s) => s.nextStopPoint)
  const isMyTurn = useGameStore(selectIsMyTurn)
  const hasPriority = useGameStore(selectHasPriority)

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
      attackersIncoming: gameState.combat?.attackers.length ?? 0,
      passLabel: nextStopPoint ?? 'Pass',
      isGameOver: gameState.isGameOver || gameOverState !== null,
      won,
    }
  }, [gameState, legalActions, pendingDecision, gameOverState, nextStopPoint, isMyTurn, hasPriority, won])

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

  if (!mission || hidden || !view) return null
  const tip = coachTip(view, mission.hints)
  const next = nextMission(mission.id)
  const doneCount = objectives.filter((o) => o.done).length

  const leave = (to: string) => {
    returnToMenu()
    navigate(to)
  }

  return createPortal(
    <aside className={`${styles.coach} ${styles[tip.tone]}`} aria-live="polite" aria-label="Coach">
      <div className={styles.head}>
        <span className={styles.eyebrow}>
          Mission {mission.number} · {mission.title}
        </span>
        <button
          type="button"
          className={styles.close}
          onClick={() => setHidden(true)}
          aria-label="Hide the coach for the rest of this game"
          title="Hide the coach for the rest of this game"
        >
          ×
        </button>
      </div>

      {showIntro && !view.isGameOver ? (
        <div className={styles.body}>
          <div className={styles.title}>Welcome to the table.</div>
          <p className={styles.text}>{mission.intro}</p>
          <button
            type="button"
            className={styles.primary}
            onClick={() => {
              markIntroSeen()
              setShowIntro(false)
            }}
          >
            Let’s go
          </button>
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
    </aside>,
    document.body,
  )
}
