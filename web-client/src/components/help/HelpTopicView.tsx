/**
 * Renders one {@link HelpTopic}. Shared by `/help` and the in-game drawer so the two can never
 * drift — the whole point of the single topic registry.
 */
import { HELP_TOPICS, topicById, helpHref, type HelpTopic } from '@/help/topics'
import { SHORTCUTS, shortcutById } from '@/help/shortcuts'
import styles from './help.module.css'

export function HelpTopicView({
  topic,
  onNavigate,
}: {
  topic: HelpTopic
  /** How a related-topic link should be followed. Omit for a plain anchor (the `/help` page). */
  onNavigate?: (topicId: string) => void
}) {
  return (
    <article id={topic.id} className={styles.topic}>
      <h3 className={styles.topicTitle}>{topic.title}</h3>
      <p className={styles.topicSummary}>{topic.summary}</p>

      {topic.body?.map((block, i) => {
        if (block.kind === 'p') return <p key={i} className={styles.topicBody}>{block.text}</p>
        if (block.kind === 'ul') {
          return (
            <ul key={i} className={styles.topicList}>
              {block.items.map((item, j) => <li key={j}>{item}</li>)}
            </ul>
          )
        }
        return <ShortcutTable key={i} />
      })}

      {topic.shortcuts && topic.shortcuts.length > 0 && (
        <div className={styles.shortcutChips}>
          {topic.shortcuts.map((id) => {
            const s = shortcutById(id)
            return s ? (
              <span key={id} className={styles.shortcutChip}>
                <kbd className={styles.kbd}>{s.keys}</kbd>
                {s.label}
              </span>
            ) : null
          })}
        </div>
      )}

      {topic.related && topic.related.length > 0 && (
        <div className={styles.relatedRow}>
          <span className={styles.relatedLabel}>See also</span>
          {topic.related.map((id) => {
            const related = topicById(id)
            if (!related) return null
            return onNavigate ? (
              <button
                key={id}
                type="button"
                className={styles.relatedLink}
                onClick={() => onNavigate(id)}
              >
                {related.title}
              </button>
            ) : (
              <a key={id} href={helpHref(related)} className={styles.relatedLink}>
                {related.title}
              </a>
            )
          })}
        </div>
      )}
    </article>
  )
}

export function ShortcutTable() {
  return (
    <div className={styles.shortcutTableWrap}>
      <table className={styles.shortcutTable}>
        <thead>
          <tr>
            <th>Key</th>
            <th>Does</th>
            <th>Where</th>
          </tr>
        </thead>
        <tbody>
          {SHORTCUTS.map((s) => (
            <tr key={s.id}>
              <td>
                {s.keys.split(' / ').map((k, i) => (
                  <span key={k}>
                    {i > 0 && <span className={styles.kbdSep}>or</span>}
                    <kbd className={styles.kbd}>{k}</kbd>
                  </span>
                ))}
              </td>
              <td>{s.label}</td>
              <td className={styles.shortcutWhere}>{s.where}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

/** Every topic, for the drawer's "everything" view. */
export const ALL_TOPICS = HELP_TOPICS
