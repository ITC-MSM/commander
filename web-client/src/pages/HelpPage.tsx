/**
 * `/help` — the full guide. Deep-linkable as `/help/<section>#<topic-id>`, which is what every
 * inline {@link HelpTip}'s "Read more" points at.
 *
 * Content comes entirely from `src/help/topics.ts`; this file is layout only.
 */
import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { HELP_SECTIONS, topicsInSection, topicById, type HelpSection } from '@/help/topics'
import { HelpTopicView } from '@/components/help/HelpTopicView'
import styles from './HelpPage.module.css'

const DEFAULT_SECTION: HelpSection = 'getting-started'

function isSection(value: string | undefined): value is HelpSection {
  return HELP_SECTIONS.some((s) => s.id === value)
}

export function HelpPage() {
  const { section: sectionParam } = useParams<{ section?: string }>()
  const navigate = useNavigate()
  const section: HelpSection = isSection(sectionParam) ? sectionParam : DEFAULT_SECTION
  const meta = HELP_SECTIONS.find((s) => s.id === section)!

  // Honour the #topic-id fragment once the section has rendered.
  useEffect(() => {
    const id = window.location.hash.slice(1)
    if (!id) return
    const timer = window.setTimeout(() => {
      document.getElementById(id)?.scrollIntoView({ block: 'start', behavior: 'smooth' })
    }, 50)
    return () => window.clearTimeout(timer)
  }, [section])

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <button type="button" className={styles.backButton} onClick={() => navigate('/')}>
          ← Menu
        </button>
        <h1 className={styles.title}>Help</h1>
        <p className={styles.subtitle}>
          For players who know Magic and are new to Argentum. This does not teach the rules — it
          explains what this app does with them.
        </p>
      </header>

      <div className={styles.layout}>
        <nav className={styles.nav} aria-label="Help sections">
          {HELP_SECTIONS.map((s) => (
            <button
              key={s.id}
              type="button"
              className={`${styles.navItem} ${s.id === section ? styles.navItemActive : ''}`}
              onClick={() => navigate(`/help/${s.id}`)}
            >
              <span className={styles.navItemTitle}>{s.title}</span>
              <span className={styles.navItemBlurb}>{s.blurb}</span>
            </button>
          ))}
        </nav>

        <main className={styles.content}>
          <h2 className={styles.sectionTitle}>{meta.title}</h2>
          <p className={styles.sectionBlurb}>{meta.blurb}</p>
          <div className={styles.topics}>
            {topicsInSection(section).map((topic) => (
              <HelpTopicView
                key={topic.id}
                topic={topic}
                onNavigate={(id) => {
                  const target = topicById(id)
                  if (!target) return
                  navigate(`/help/${target.section}#${id}`)
                  // Same-section links don't re-run the hash effect, so scroll here too.
                  window.setTimeout(() => {
                    document.getElementById(id)?.scrollIntoView({ block: 'start', behavior: 'smooth' })
                  }, 50)
                }}
              />
            ))}
          </div>
        </main>
      </div>
    </div>
  )
}
