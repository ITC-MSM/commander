/**
 * The landing page's pointer to the course, in two sizes.
 *
 * `variant="arrival"` is for the one screen a brand-new visitor sees — the name prompt — and
 * says the course needs no name. `variant="tier"` is the quieter row inside PLAY for someone
 * already connected: it shows progress once the course is started, and stays put once the course
 * is done — dimmed, marked complete, and still the way back in to replay any mission. A finished
 * course is a thing you did, not a thing to hide, and this row is the landing page's only pointer
 * at `/learn` (the `/help` guide has the other one).
 */
import { useNavigate } from 'react-router-dom'
import { COURSE_COUNT_WORD, COURSE_MINUTES, MISSIONS } from '@/learn/missions'
import { hasStarted, nextIncomplete, useLearnProgress } from '@/learn/progressStore'
import styles from './LearnCallout.module.css'

export function LearnCallout({ variant }: { variant: 'arrival' | 'tier' }) {
  const navigate = useNavigate()
  const completed = useLearnProgress((s) => s.completed)
  const started = hasStarted({ completed })
  const next = nextIncomplete(completed)
  const finished = next === undefined

  const go = () => navigate('/learn')

  if (variant === 'arrival') {
    return (
      <button type="button" className={styles.arrival} onClick={go}>
        <span className={styles.arrivalGlyph} aria-hidden="true">
          <i className="ms ms-planeswalker" />
        </span>
        <span className={styles.arrivalText}>
          <span className={styles.arrivalTitle}>
            {finished
              ? 'Course complete — replay it any time'
              : started
                ? 'Pick up the course where you left off'
                : 'Never played Magic?'}
          </span>
          <span className={styles.arrivalBody}>
            {finished
              ? `All ${MISSIONS.length} missions done. Any of them can be played again — no name needed.`
              : started
                ? `${completed.length} of ${MISSIONS.length} missions done — no name needed to continue.`
                : `Learn by playing: ${COURSE_COUNT_WORD} short guided games with a coach, about ${COURSE_MINUTES} minutes. No name needed.`}
          </span>
        </span>
        <span className={styles.arrivalArrow} aria-hidden="true">→</span>
      </button>
    )
  }

  return (
    <button
      type="button"
      className={finished ? `${styles.tier} ${styles.tierDone}` : styles.tier}
      onClick={go}
    >
      <span className={styles.tierGlyph} aria-hidden="true">
        <i className="ms ms-planeswalker" />
      </span>
      <span className={styles.tierLabel}>
        {finished
          ? 'Learn to play — course complete'
          : started
            ? 'Continue learning to play'
            : 'New to Magic? Learn to play'}
      </span>
      <span className={styles.tierMeta}>
        {finished ? 'Replay' : started ? `${completed.length}/${MISSIONS.length}` : `${COURSE_MINUTES} min`}
      </span>
    </button>
  )
}
