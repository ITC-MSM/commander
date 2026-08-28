/**
 * The course as a hand of cards.
 *
 * Four mission cards fanned the way a hand is held, each drawn in a Magic frame in the mission's
 * colour. Finished missions are foil and sealed; the next one to play glows. The same card, at
 * thumbnail size, is the strip on every brief ({@link MiniHand}).
 */
import { Link } from 'react-router-dom'
import { MISSIONS, learnHref, type Mission, type MissionFrame, type MissionId } from '@/learn/missions'
import styles from './learn.module.css'

/** The one glyph in each card's art box — mana-font's symbol for what the mission is about. */
const ART_GLYPH: Record<MissionId, string> = {
  'first-steps': 'ms-land',
  blocking: 'ms-creature',
  instants: 'ms-instant',
  removal: 'ms-enchantment',
  'real-game': 'ms-planeswalker',
}

const TYPE_LINE: Record<MissionId, string> = {
  'first-steps': 'Mission — Lands & creatures',
  blocking: 'Mission — Combat',
  instants: 'Mission — The stack',
  removal: 'Mission — Answers',
  'real-game': 'Game — Full rules',
}

/** Frames light enough to need dark text. */
const LIGHT_FRAMES: ReadonlySet<MissionFrame> = new Set<MissionFrame>(['W', 'gold', 'artifact'])

export function frameVar(frame: MissionFrame): string {
  return `var(--frame-${frame})`
}

function MissionCardFrame({
  mission,
  done,
  next,
  later,
}: {
  mission: Mission
  done: boolean
  next: boolean
  later: boolean
}) {
  const dark = !LIGHT_FRAMES.has(mission.frame)
  const cls = [
    styles.card,
    dark ? styles.cardDark : '',
    done ? styles.cardDone : '',
    next ? styles.cardNext : '',
    later ? styles.cardLocked : '',
  ].join(' ')
  return (
    <Link
      to={learnHref(mission.id)}
      className={cls}
      style={{ ['--frame' as string]: frameVar(mission.frame) }}
      aria-label={`Mission ${mission.number}: ${mission.title}${done ? ' (complete)' : ''}`}
    >
      {done && <span className={styles.cardSeal} aria-hidden="true">✓</span>}
      <div className={styles.cardTitle}>
        <span>{mission.title}</span>
        <span className={styles.cardNumber}>{mission.number}</span>
      </div>
      <div className={styles.cardArt}>
        <i className={`ms ${ART_GLYPH[mission.id]}`} aria-hidden="true" />
      </div>
      <div className={styles.cardType}>{TYPE_LINE[mission.id]}</div>
      <div className={styles.cardText}>{mission.blurb}</div>
      <div className={styles.cardFoot}>
        <span>{mission.number} / {MISSIONS.length}</span>
        <span>~{mission.minutes} min</span>
      </div>
    </Link>
  )
}

/**
 * Fan geometry: card `i` of `n` sits `shift` px from centre, tilted `tilt` degrees, dropped by a
 * parabola so the ends of the hand sit lower than the middle — the shape of held cards.
 */
function slotStyle(i: number, n: number): Record<string, string> {
  const mid = (n - 1) / 2
  const d = i - mid
  return { '--shift': `${d * 190}px`, '--tilt': `${d * 6}deg`, '--lift': `${d * d * 14}px` }
}

export function MissionHand({ completed, next }: { completed: readonly MissionId[]; next: MissionId | undefined }) {
  const nextNumber = MISSIONS.find((m) => m.id === next)?.number ?? Infinity
  return (
    <div className={styles.hand} role="list" aria-label="Missions">
      {MISSIONS.map((mission, i) => {
        const done = completed.includes(mission.id)
        const isNext = mission.id === next
        // Nothing is locked — you can jump ahead — but cards past the next one sit a little
        // dimmer so the path reads left to right.
        const later = !done && !isNext && mission.number > nextNumber
        return (
          <div key={mission.id} className={styles.handSlot} style={slotStyle(i, MISSIONS.length)} role="listitem">
            <MissionCardFrame mission={mission} done={done} next={isNext} later={later} />
          </div>
        )
      })}
    </div>
  )
}

export function MiniHand({ current, completed }: { current: MissionId; completed: readonly MissionId[] }) {
  return (
    <nav className={styles.miniHand} aria-label="All missions">
      {MISSIONS.map((mission) => {
        const dark = !LIGHT_FRAMES.has(mission.frame)
        const cls = [
          styles.miniCard,
          dark ? styles.miniCardDark : '',
          mission.id === current ? styles.miniCardActive : '',
          completed.includes(mission.id) ? styles.miniCardDone : '',
        ].join(' ')
        return (
          <Link
            key={mission.id}
            to={learnHref(mission.id)}
            className={cls}
            style={{ ['--frame' as string]: frameVar(mission.frame) }}
            title={`${mission.number}. ${mission.title}`}
            aria-label={`Mission ${mission.number}: ${mission.title}`}
          >
            {mission.number}
          </Link>
        )
      })}
    </nav>
  )
}
