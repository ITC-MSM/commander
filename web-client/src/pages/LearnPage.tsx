/**
 * `/learn` — the course home, a hand of four mission cards — and `/learn/:missionId`, the brief
 * for one mission: three lines on what you will do, the cards you start with, and the button
 * that puts you at the table.
 *
 * The course content is `learn/missions.ts`; this file is the frame around it. Starting a game
 * posts the mission's scenario to `/api/scenarios`, arms the coach with the mission id, and hands
 * off with a full navigation (`/?token=…`) so the app makes a clean token-based connect — the
 * same hand-off the scenario builder uses.
 */
import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { COURSE_MINUTES, MISSIONS, TUTOR_NAME, learnHref, missionById, type Mission, type MissionId } from '@/learn/missions'
import { armCoach } from '@/learn/coach'
import { hasStarted, nextIncomplete, useLearnProgress } from '@/learn/progressStore'
import { useAuthStore } from '@/store/authStore'
import type { ScenarioCreateResponse } from '@/components/scenario/types'
import { MissionHand, MiniHand, frameVar } from '@/components/learn/MissionHand'
import { CardImage } from '@/components/learn/CardImage'
import { useLessonCards } from '@/components/learn/useLessonCards'
import styles from '@/components/learn/learn.module.css'

const NAME_KEY = 'argentum-player-name'

function storedName(): string {
  const account = useAuthStore.getState().user?.displayName?.trim()
  if (account) return account
  try {
    return localStorage.getItem(NAME_KEY)?.trim() ?? ''
  } catch {
    return ''
  }
}

export function LearnPage() {
  const { missionId } = useParams<{ missionId?: string }>()
  const mission = missionById(missionId)
  return <div className={styles.root}>{mission ? <Brief mission={mission} /> : <CourseHome />}</div>
}

function TopBar({ current }: { current?: MissionId }) {
  const navigate = useNavigate()
  const completed = useLearnProgress((s) => s.completed)
  return (
    <div className={styles.topBar}>
      <div className={styles.topBarInner}>
        <button type="button" className={styles.topBarLink} onClick={() => navigate('/')}>
          ← Menu
        </button>
        {current && (
          <Link to={learnHref()} className={styles.topBarLink}>
            All missions
          </Link>
        )}
        <span className={styles.topBarEyebrow}>Learn to play</span>
        <div className={styles.pips} aria-label={`${completed.length} of ${MISSIONS.length} missions complete`}>
          {MISSIONS.map((m) => (
            <span
              key={m.id}
              className={[
                styles.pip,
                completed.includes(m.id) ? styles.pipDone : '',
                m.id === current ? styles.pipCurrent : '',
              ].join(' ')}
              title={`${m.number}. ${m.title}`}
            />
          ))}
        </div>
      </div>
    </div>
  )
}

function CourseHome() {
  const navigate = useNavigate()
  const completed = useLearnProgress((s) => s.completed)
  const reset = useLearnProgress((s) => s.reset)
  const next = nextIncomplete(completed)
  const started = hasStarted({ completed })
  const finished = next === undefined

  useEffect(() => {
    document.title = 'Learn to play — Argentum'
    return () => {
      document.title = 'Argentum Engine'
    }
  }, [])

  return (
    <>
      <TopBar />
      <main className={styles.home}>
        <p className={styles.eyebrow}>Never played Magic?</p>
        <h1 className={styles.headline}>
          Learn it
          <br />
          <em>by playing it.</em>
        </h1>
        <p className={styles.lede}>
          Four short games against the AI, each from a board set up to teach one thing, with a coach at your
          elbow saying what to do next. About {COURSE_MINUTES} minutes all told. Nothing to read first,
          nothing to sign up for.
        </p>
        <div className={styles.heroActions}>
          {finished ? (
            <>
              <button type="button" className={styles.cta} onClick={() => navigate('/')}>
                Go play for real
              </button>
              <Link to={learnHref('real-game')} className={styles.ctaQuiet}>
                Another practice game
              </Link>
            </>
          ) : (
            <>
              <button type="button" className={styles.cta} onClick={() => navigate(learnHref(next))}>
                {started ? `Continue — mission ${missionById(next)?.number ?? 1}` : 'Start mission 1'}
              </button>
              {started && (
                <Link to={learnHref('first-steps')} className={styles.ctaQuiet}>
                  Start over from the top
                </Link>
              )}
            </>
          )}
        </div>
        <p className={styles.heroNote}>
          Already know how to play? <Link to="/">Skip to the table</Link> — or read the{' '}
          <Link to="/help">guide to Argentum</Link> instead.
        </p>

        <MissionHand completed={completed} next={next} />
        <p className={styles.handCaption}>Pick a card. They play best in order, and the last one is the real thing.</p>

        {finished && (
          <div className={styles.doneBanner}>
            <h2>Course complete.</h2>
            <p>
              You have played Magic. From here the PLAY menu has quick games against the AI, drafts, and other
              people; the deckbuilder lets you make something of your own.
            </p>
            <button type="button" className={styles.textLink} onClick={reset}>
              Reset my progress
            </button>
          </div>
        )}
      </main>
    </>
  )
}

function Brief({ mission }: { mission: Mission }) {
  const completed = useLearnProgress((s) => s.completed)
  const plays = useLearnProgress((s) => s.plays)
  const done = completed.includes(mission.id)
  const cards = useLessonCards(mission.openingCards)
  const [name, setName] = useState(storedName)
  const [starting, setStarting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    document.querySelector(`.${styles.root}`)?.scrollTo({ top: 0 })
    document.title = `${mission.number}. ${mission.title} — Learn to play`
    return () => {
      document.title = 'Argentum Engine'
    }
  }, [mission])

  const start = async () => {
    const trimmed = name.trim()
    if (!trimmed) return
    setStarting(true)
    setError(null)
    try {
      localStorage.setItem(NAME_KEY, trimmed)
    } catch {
      // The name still goes into the scenario; only the memory of it is lost.
    }
    try {
      const res = await fetch('/api/scenarios', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mission.spec(trimmed)),
      })
      if (!res.ok) {
        const body: unknown = await res.json().catch(() => null)
        const errors =
          body && typeof body === 'object' && Array.isArray((body as { errors?: unknown }).errors)
            ? (body as { errors: string[] }).errors
            : [`The server could not start the game (HTTP ${res.status}).`]
        setError(errors.join(' '))
        setStarting(false)
        return
      }
      const data = (await res.json()) as ScenarioCreateResponse
      const human = data.player1.token && data.player1.token !== '(AI)' ? data.player1 : data.player2
      armCoach(mission.id)
      window.location.href = `/?token=${encodeURIComponent(human.token)}`
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Could not reach the server.')
      setStarting(false)
    }
  }

  const playCount = plays[mission.id] ?? 0

  return (
    <>
      <TopBar current={mission.id} />
      <main className={styles.brief}>
        <header className={styles.briefHeader} style={{ ['--frame' as string]: frameVar(mission.frame) }}>
          <div className={styles.lessonKicker}>
            <span className={styles.lessonSwatch} aria-hidden="true" />
            Mission {mission.number} of {MISSIONS.length} · about {mission.minutes} min
            {done && ' · complete'}
          </div>
          <h1 className={styles.lessonTitle}>{mission.title}</h1>
          <p className={styles.lessonBlurb}>{mission.blurb}</p>
        </header>

        <div className={styles.briefBody}>
          <section className={styles.briefColumn}>
            <h2 className={styles.briefHeading}>What you’ll do</h2>
            <ol className={styles.briefList}>
              {mission.brief.map((line, i) => (
                <li key={line}>
                  <span className={styles.briefIndex}>{i + 1}</span>
                  <span>{line}</span>
                </li>
              ))}
            </ol>

            <h2 className={styles.briefHeading}>Take a seat</h2>
            <p className={styles.briefNote}>
              You play against {TUTOR_NAME}, the built-in AI. What should the table call you?
            </p>
            <div className={styles.nameField}>
              <input
                type="text"
                className={styles.nameInput}
                value={name}
                maxLength={20}
                placeholder="Your name"
                onChange={(e) => setName(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') void start()
                }}
                aria-label="Your name"
              />
              <button
                type="button"
                className={styles.cta}
                disabled={!name.trim() || starting}
                onClick={() => void start()}
              >
                {starting ? 'Setting up the table…' : playCount > 0 ? 'Play again' : 'Play'}
              </button>
            </div>
            {error && <p className={styles.errorText}>{error}</p>}
          </section>

          <aside className={styles.briefColumn} aria-label="Your opening cards">
            <h2 className={styles.briefHeading}>Your opening cards</h2>
            <div className={styles.briefCards}>
              {mission.openingCards.map((n) => (
                <CardImage key={n} src={cards[n]?.imageUrl ?? ''} name={n} caption={n} />
              ))}
            </div>
            <p className={styles.briefNote}>Hover any card at the table to read it in full.</p>
          </aside>
        </div>

        <MiniHand current={mission.id} completed={completed} />
      </main>
    </>
  )
}
