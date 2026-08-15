/**
 * Custom cards — paste a Scryfall(-style) card object, have Argentum Assay read it, and play it.
 *
 * The panel is deliberately two things at once, because they are the same act: it *verifies* a
 * card's text against the parser (each printed line with the verdict the gates use, and a caret on
 * the token a decline died on) and it *compiles* that text into a real card the scenario can use.
 * A card only becomes addable once Assay reads every one of its lines — a partially-read card
 * would sit on the board looking right and be missing an ability.
 *
 * The panel stays visible when the server has the sandbox switched off; the check just answers with
 * what to turn on. Hiding it would leave the tester guessing whether the feature exists.
 */
import { useState } from 'react'
import type { AssayCompileResponse } from './types'
import type { CustomCard } from './builderState'
import styles from './ScenarioBuilder.module.css'

const PLACEHOLDER = `{
  "name": "Argentum Sentinel",
  "mana_cost": "{2}{W}",
  "type_line": "Creature — Bird Soldier",
  "oracle_text": "Flying, vigilance\\nWhen this creature enters, draw a card.",
  "power": "2",
  "toughness": "3"
}`

/** The verdict vocabulary is the touchstone's; these are only how each one reads on screen. */
const VERDICT_LABEL: Record<string, string> = {
  ROUND_TRIP: 'read',
  VARIANT: 'read (respelled)',
  DECLINED: 'not read',
  AMBIGUOUS: 'ambiguous',
  MISMATCH: 'mismatch',
}

function verdictClass(verdict: string): string {
  if (verdict === 'ROUND_TRIP') return styles.verdictOk!
  if (verdict === 'VARIANT') return styles.verdictVariant!
  return styles.verdictBad!
}

export function CustomCardPanel({
  cards,
  onAdd,
  onRemove,
  onPlace,
  placeHint,
}: {
  cards: CustomCard[]
  onAdd: (card: CustomCard) => void
  onRemove: (name: string) => void
  /** Put a compiled card into the builder's currently targeted seat + zone. */
  onPlace: (name: string) => void
  /** Where `onPlace` will put it, e.g. "Player 1's battlefield" — the button is otherwise mute. */
  placeHint: string
}) {
  const [json, setJson] = useState('')
  const [checking, setChecking] = useState(false)
  const [result, setResult] = useState<AssayCompileResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  const check = async () => {
    setChecking(true)
    setError(null)
    setResult(null)
    try {
      const res = await fetch('/api/dev/scenarios/assay', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ json }),
      })
      if (res.status === 404) {
        setError(
          'This server has custom cards switched off. Start it with game.dev-endpoints.enabled=true ' +
            '(GAME_DEV_ENDPOINTS_ENABLED=true) to compile cards with Assay.',
        )
        return
      }
      if (!res.ok) {
        setError(`Assay request failed (HTTP ${res.status}).`)
        return
      }
      setResult((await res.json()) as AssayCompileResponse)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Could not reach the server.')
    } finally {
      setChecking(false)
    }
  }

  const addCompiled = () => {
    if (!result?.compiled || !result.cardName) return
    onAdd({ name: result.cardName, json })
    setJson('')
    setResult(null)
  }

  return (
    <div className={styles.jsonDrawer}>
      <span className={styles.hint}>
        Paste a card from Scryfall, or write one by hand — Assay reads the Oracle text into a real
        card. Every line has to be readable before it can be played. Lines are shown as Assay
        normalizes them, where <code>~</code> stands for the card’s own name.
      </span>

      <textarea
        className={styles.jsonArea}
        value={json}
        placeholder={PLACEHOLDER}
        spellCheck={false}
        onChange={(e) => setJson(e.target.value)}
      />

      <div className={styles.jsonActions}>
        <button
          type="button"
          className={styles.ghostBtn}
          disabled={!json.trim() || checking}
          onClick={() => void check()}
        >
          {checking ? 'Reading…' : 'Check with Assay'}
        </button>
        <button
          type="button"
          className={styles.ghostBtn}
          disabled={!result?.compiled}
          onClick={addCompiled}
          title={result?.compiled ? 'Make this card available to the scenario' : 'Check the card first'}
        >
          Add to scenario
        </button>
      </div>

      {error && <div className={styles.assayError}>{error}</div>}

      {result && (
        <div className={styles.assayResult}>
          <div className={result.compiled ? styles.assayHeadOk : styles.assayHeadBad}>
            {result.compiled
              ? `${result.cardName} — read whole, ready to play`
              : `${result.cardName ?? 'This card'} did not compile`}
          </div>

          {result.lines.length > 0 && (
            <ul className={styles.assayLines}>
              {result.lines.map((line) => (
                <li key={line.index}>
                  <span className={verdictClass(line.verdict)}>
                    {VERDICT_LABEL[line.verdict] ?? line.verdict.toLowerCase()}
                  </span>
                  <span className={styles.assayLineText}>{line.text || '(blank)'}</span>
                  {line.printed && (
                    <span className={styles.assayPrinted}>canonical: {line.printed}</span>
                  )}
                  {line.explanation && <pre className={styles.assayCaret}>{line.explanation}</pre>}
                </li>
              ))}
            </ul>
          )}

          {result.declines.length > 0 && (
            <ul className={styles.errorList}>
              {result.declines.map((d, i) => (
                <li key={i}>
                  {d.detail}
                  {d.line ? ` — “${d.line}”` : ''}
                </li>
              ))}
            </ul>
          )}

          {result.warnings.map((w, i) => (
            <div key={i} className={styles.assayWarning}>
              {w}
            </div>
          ))}
        </div>
      )}

      {cards.length > 0 && (
        <div className={styles.customCardList}>
          <span className={styles.smallLabel}>In this scenario</span>
          {cards.map((card) => (
            <div key={card.name} className={styles.customCardRow}>
              <span className={styles.customCardName}>{card.name}</span>
              <button
                type="button"
                className={styles.ghostBtn}
                onClick={() => onPlace(card.name)}
                title={`Add to ${placeHint}`}
              >
                Add to {placeHint}
              </button>
              <button
                type="button"
                className={styles.dangerBtn}
                onClick={() => onRemove(card.name)}
                title="Remove the card and every copy of it on the board"
              >
                Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
