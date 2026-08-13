import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { CardReadiness, fetchCardReadiness } from '../../api/cardReadiness'
import styles from './cardReadiness.module.css'

const LABELS: Record<string, string> = {
  IMPLEMENTED_UNVERIFIED: 'Implemented — evidence pending',
  IMPLEMENTED_VERIFIED: 'Implemented and verified',
  BLOCKED_FEATURE: 'Blocked by engine feature',
  NOT_PLANNED: 'Not planned',
  UNMATCHED_TRIAGE: 'Needs triage',
}

/** Small release gate dashboard; it must never convert catalog coverage into a readiness claim. */
export function CardReadinessPage() {
  const [readiness, setReadiness] = useState<CardReadiness | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchCardReadiness().then(setReadiness).catch((e) => setError(e instanceof Error ? e.message : String(e)))
  }, [])

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <p className={styles.eyebrow}>Developer evidence dashboard</p>
          <h1>Card readiness</h1>
          <p>Release evidence, not a completion percentage. <Link to="/set-completion">Open Catalog Coverage</Link> for authored catalog presence.</p>
        </div>
        {readiness && <div className={readiness.semantics.releaseEligible ? styles.pass : styles.blocked}>
          {readiness.semantics.releaseEligible ? 'Eligible' : 'Not release eligible'}
        </div>}
      </header>
      {error && <p className={styles.error}>{error}</p>}
      {!readiness && !error && <p>Loading readiness evidence…</p>}
      {readiness && <>
        <p className={styles.reason}>{readiness.semantics.releaseEligibleReason}</p>
        <section className={styles.grid} aria-label="Readiness states">
          {Object.keys(LABELS).map((status) => <article className={styles.card} key={status}>
            <span>{LABELS[status]}</span>
            <strong>{(readiness.counts[status] ?? 0).toLocaleString()}</strong>
            <ul>{(readiness.samples[status] ?? []).map((sample) => <li key={sample.name}>{sample.name}{sample.sets.length ? ` · ${sample.sets.join(', ')}` : ''}</li>)}</ul>
          </article>)}
        </section>
        <section className={styles.provenance}>
          <h2>Provenance</h2>
          <dl>
            <dt>Source commit</dt><dd><code>{readiness.generatedFrom.commit}</code> ({readiness.generatedFrom.commitTimestamp})</dd>
            <dt>Catalog snapshot</dt><dd><code>{readiness.generatedFrom.catalogSource}</code> · <code>{readiness.generatedFrom.catalogSha256}</code></dd>
            <dt>Evidence ledger</dt><dd><code>{readiness.generatedFrom.ledgerSource}</code> · <code>{readiness.generatedFrom.ledgerSha256}</code></dd>
            <dt>Triage ledger</dt><dd><code>{readiness.generatedFrom.triageLedgerSource}</code> · <code>{readiness.generatedFrom.triageLedgerSha256}</code></dd>
          </dl>
          <p>{readiness.semantics.verified}</p>
        </section>
      </>}
    </main>
  )
}
