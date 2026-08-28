/**
 * The coach panel for a course mission. Mounted on every game board, renders nothing unless the
 * course armed it (see `learn/coach.ts`). Three states:
 *
 * - **Tour** — when the board first appears, a few steps that each ring one part of the table
 *   (hand, battlefield, turn strip, the pass button …) and say what it is for. Skippable — and
 *   the board stays live under it, so a player who plays the Forest while the tour is still
 *   introducing the hand has simply started; the tour steps aside the moment they do.
 * - **Tip** — one line for what the board is waiting on, worded with the real button label and
 *   this device's gestures, with a quiet ring on the thing it names; above it, for a few
 *   seconds, one line answering the thing the player just did (the Forest is down, the bear
 *   attacks, no blocks); a card note under it when a permanent with a keyword the course has not
 *   named yet is on the table; the mission's objectives tick off underneath as the player does
 *   them.
 * - **Done** — the game ended: what you learned, and the next card in the hand. A concede ends
 *   the game but not the mission.
 *
 * Portalled to `<body>` like the help drawer: `#root` is overflow:hidden and the multiplayer
 * strip transforms its subtree, both of which break `position: fixed` for descendants.
 */
import { useEffect, useMemo, useRef, useState } from 'react'
import { createPortal } from 'react-dom'
import { useNavigate } from 'react-router-dom'
import { useGameStore } from '@/store/gameStore'
import { selectHasPriority, selectIsMyTurn, useStackCards } from '@/store/selectors'
import {
  armedMission,
  coachTip,
  disarmCoach,
  markTourSeen,
  noticeFromLog,
  noticeFromMove,
  playerIsActing,
  tourSeen,
  wordTip,
  type ActionNotice,
  type CoachView,
} from '@/learn/coach'
import { latestNotedPermanent, learnHref, missionById, nextMission } from '@/learn/missions'
import type { SpotContext } from '@/learn/spots'
import { useLearnSignals } from '@/learn/signals'
import { syncLearnProgress, useLearnProgress } from '@/learn/progressStore'
import { GameOverReason, ZoneType } from '@/types/enums'
import type { EntityId } from '@/types'
import type { ClientCard } from '@/types/gameState'
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

/** A phone: the coach is a pill by default there, and a sheet only while the tour or the ending is up. */
function smallScreen(): boolean {
  try {
    return window.matchMedia('(max-width: 640px)').matches
  } catch {
    return false
  }
}

function isCreatureOnBattlefield(c: ClientCard): boolean {
  return c.zone?.zoneType === ZoneType.BATTLEFIELD && c.cardTypes.some((t) => t.toUpperCase() === 'CREATURE')
}

/** How long the line answering the player's last move stays up before the tip stands alone again. */
const NOTICE_MS = 9000

/** The line answering a move, and whether that move was the one that cut the tour short. */
interface ShownNotice extends ActionNotice {
  endedTour: boolean
}

export function LearnCoach() {
  const [missionId] = useState(armedMission)
  const mission = useMemo(() => missionById(missionId), [missionId])
  const [tourStep, setTourStep] = useState<number | null>(() => (tourSeen() ? null : 0))
  // On a phone the panel would cover the hand, so it starts tucked away unless the tour is up.
  const [collapsed, setCollapsed] = useState(() => smallScreen() && tourSeen())
  const [hasHover] = useState(deviceHasHover)
  // Once the game is over the coach disarms itself, so a remount after that renders nothing —
  // which is also what stops the mission being finished twice.
  const [finished, setFinished] = useState(false)
  const [notice, setNotice] = useState<ShownNotice | null>(null)
  // What the coach has already answered: the log entries read so far, and the view it last saw.
  const seenLog = useRef<number | null>(null)
  const lastView = useRef<CoachView | null>(null)
  const navigate = useNavigate()
  const finish = useLearnProgress((s) => s.finish)
  const returnToMenu = useGameStore((s) => s.returnToMenu)

  const gameState = useGameStore((s) => s.gameState)
  const legalActions = useGameStore((s) => s.legalActions)
  const pendingDecision = useGameStore((s) => s.pendingDecision)
  const isTargeting = useGameStore((s) => s.targetingState !== null)
  const combatState = useGameStore((s) => s.combatState)
  const gameOverState = useGameStore((s) => s.gameOverState)
  const nextStopPoint = useGameStore((s) => s.nextStopPoint)
  const isMyTurn = useGameStore(selectIsMyTurn)
  const hasPriority = useGameStore(selectHasPriority)
  const stackSize = useStackCards().length
  const signals = useLearnSignals((s) => s.marked)

  const won = gameOverState ? (gameOverState.result === 'draw' ? null : gameOverState.result === 'win') : null
  const conceded = gameOverState?.reason === GameOverReason.CONCESSION && gameOverState.result === 'lose'

  const me = gameState?.viewingPlayerId
  const players = gameState?.players

  const view = useMemo<CoachView | null>(() => {
    if (!gameState) return null
    const types = new Set(legalActions.map((a) => a.actionType))
    const selected = combatState?.mode === 'declareAttackers' ? combatState.selectedAttackers : []
    const mandatory = combatState?.mode === 'declareAttackers' ? combatState.mandatoryAttackers : []
    const cards = Object.values(gameState.cards)
    const mine = cards.filter((c) => c.controllerId === gameState.viewingPlayerId && isCreatureOnBattlefield(c))
    const theirs = cards.filter((c) => c.controllerId !== gameState.viewingPlayerId && isCreatureOnBattlefield(c))
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
      attackersSelected: selected.length,
      attackersChosen: selected.filter((id) => !mandatory.includes(id)).length,
      blockersLeft: mine.filter((c) => !c.isTapped && !selected.includes(c.id)).length,
      theirCreatures: theirs.length,
      conceded,
      passLabel: nextStopPoint ?? 'Pass',
      hasHover,
      isGameOver: gameState.isGameOver || gameOverState !== null,
      won,
    }
  }, [
    gameState,
    legalActions,
    pendingDecision,
    isTargeting,
    combatState,
    gameOverState,
    nextStopPoint,
    isMyTurn,
    hasPriority,
    stackSize,
    conceded,
    hasHover,
    won,
  ])

  const spotCtx = useMemo<SpotContext>(
    () => ({
      me: me ?? ('' as EntityId),
      opponent: players?.find((p) => p.playerId !== me)?.playerId,
    }),
    [me, players],
  )

  const objectives = useMemo(() => {
    if (!mission || !gameState) return []
    const ctx = { state: gameState, me: gameState.viewingPlayerId, won, signals }
    return mission.objectives.map((o) => ({ id: o.id, label: o.label, done: o.done(ctx) }))
  }, [mission, gameState, won, signals])

  const cardNote = useMemo(() => (gameState ? latestNotedPermanent(gameState) : null), [gameState])

  const endTour = () => {
    markTourSeen()
    setTourStep(null)
    if (smallScreen()) setCollapsed(true)
  }

  // Answer the player's own moves. The log is the server's full per-player list, so the entries
  // past the ones already read are what happened since the last look — the first look reads
  // nothing, so a remount mid-game does not replay the whole game back. Passes, answered prompts
  // and combat choices leave no entry; the previous view tells those apart. A move during the
  // tour ends the tour: the player is ahead of it, and a tip about the live board beats a step
  // about the opening one.
  useEffect(() => {
    if (!view || !gameState) return
    const log = gameState.gameLog ?? []
    const seen = seenLog.current
    const prev = lastView.current
    seenLog.current = log.length
    lastView.current = view
    if (view.isGameOver) return
    // A shorter log is an undo: the game rewound, and the board moving back is not a move.
    const rewound = seen !== null && log.length < seen
    const found = rewound
      ? { key: `undo-${log.length}`, text: 'Undone — the game is back where it was.' }
      : (noticeFromLog(log.slice(seen ?? log.length), gameState.viewingPlayerId, gameState.cards, seen ?? log.length) ??
        (prev ? noticeFromMove(prev, view) : null))
    const touring = tourStep !== null
    if (found) setNotice({ ...found, endedTour: touring })
    if (touring && (found || playerIsActing(view))) {
      if (!found) setNotice({ key: `ahead-${view.turnNumber}-${view.step}`, text: 'You went ahead.', endedTour: true })
      endTour()
    }
    // `tourStep` is read, not reacted to: a tour click has nothing new to answer.
  }, [view, gameState])

  // The notice is a moment, not a fixture: the tip is what stays.
  useEffect(() => {
    if (!notice) return
    const id = window.setTimeout(() => setNotice((n) => (n?.key === notice.key ? null : n)), NOTICE_MS)
    return () => window.clearTimeout(id)
  }, [notice])

  // A game played to its end finishes the mission, win or lose; a concede does not. Either way
  // the coach disarms, so the next game — a rematch, or anything from the menu — has no coach.
  useEffect(() => {
    if (mission && view?.isGameOver && !finished) {
      if (!view.conceded) {
        finish(mission.id)
        void syncLearnProgress()
      }
      disarmCoach()
      setFinished(true)
    }
  }, [mission, view?.isGameOver, view?.conceded, finished, finish])

  if (!mission || !view) return null

  const tip = coachTip(view, mission.hints)
  const next = nextMission(mission.id)
  const doneCount = objectives.filter((o) => o.done).length
  const touring = tourStep !== null && !view.isGameOver && tourStep < mission.tour.length
  const step = touring ? mission.tour[tourStep] : undefined


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
        <span className={styles.pillTitle}>· {tip.title}</span>
      </button>,
      document.body,
    )
  }

  const tone = touring ? 'watch' : tip.tone
  const spot = touring ? step?.spot : tip.tone === 'act' || tip.tone === 'warn' ? tip.spot : undefined

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
            {!view.conceded && (
              <>
                <div className={styles.lessonsHead}>What you now know</div>
                <ul className={styles.lessons}>
                  {mission.lessons.map((line) => (
                    <li key={line}>{wordTip(line, view)}</li>
                  ))}
                </ul>
              </>
            )}
          </div>
        ) : (
          <div key={tip.key} className={styles.body}>
            {notice && (
              <div key={notice.key} className={styles.notice} role="status">
                <span className={styles.noticeTick} aria-hidden="true">
                  ✓
                </span>
                <span>
                  {notice.text}
                  {notice.endedTour && ' You are ahead of the tour, so it steps aside — the coach follows your moves from here.'}
                </span>
              </div>
            )}
            <div className={styles.title}>{tip.title}</div>
            <p className={styles.text}>{tip.body}</p>
          </div>
        )}

        {!touring && !view.isGameOver && cardNote && (
          <div key={cardNote.name} className={styles.note} aria-label={`About ${cardNote.name}`}>
            <span className={styles.noteGlyph} aria-hidden="true">
              ✦
            </span>
            <span>
              <span className={styles.noteName}>{cardNote.name}</span> — <span className={styles.noteKeyword}>{cardNote.note.keyword}.</span>{' '}
              {cardNote.note.body}
            </span>
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
            {view.conceded ? (
              <button type="button" className={styles.primary} onClick={() => leave(learnHref(mission.id))}>
                Play it again →
              </button>
            ) : next ? (
              <button type="button" className={styles.primary} onClick={() => leave(learnHref(next.id))}>
                Next: {next.title} →
              </button>
            ) : (
              <button type="button" className={styles.primary} onClick={() => leave(learnHref())}>
                Course complete →
              </button>
            )}
            <button type="button" className={styles.link} onClick={() => leave(view.conceded ? learnHref() : learnHref(mission.id))}>
              {view.conceded ? 'Back to the missions' : 'Play this one again'}
            </button>
          </div>
        )}
      </aside>
    </>,
    document.body,
  )
}
