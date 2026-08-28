/**
 * A spot is only useful if the board actually carries its anchor. The selectors are DOM queries
 * against attributes the board components render (`data-learn`, `data-zone`, `data-life-id`),
 * so this reads the component sources and checks each attribute value is written somewhere —
 * a renamed or dropped anchor fails here instead of silently drawing no ring.
 */
import { readdirSync, readFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { describe, expect, it } from 'vitest'
import { ALL_SPOTS, spotSelector } from './spots'

const COMPONENTS = resolve(__dirname, '../components')

function sources(): string {
  const chunks: string[] = []
  for (const entry of readdirSync(COMPONENTS, { recursive: true, withFileTypes: true })) {
    if (!entry.isFile() || !entry.name.endsWith('.tsx')) continue
    chunks.push(readFileSync(resolve(entry.parentPath ?? '', entry.name), 'utf8'))
  }
  return chunks.join('\n')
}

describe('spots', () => {
  const src = sources()
  const ctx = { me: 'p1' as never, opponent: 'p2' as never }

  it.each(ALL_SPOTS)('%s has an anchor the board renders', (spot) => {
    const selector = spotSelector(spot, ctx)
    const m = /\[(data-[a-z-]+)(?:="([^"]*)")?\]/.exec(selector)
    expect(m, selector).not.toBeNull()
    const [, attr, value] = m!
    // A literal value must be written as that literal; an id-valued attribute (`data-life-id`)
    // only needs the attribute itself to be rendered.
    const needle = value === undefined || value === 'p2' || value === 'p1' ? `${attr}=` : `${attr}="${value}"`
    if (needle === `${attr}=`) {
      expect(src, needle).toContain(needle)
    } else {
      // `data-zone` values are chosen by a ternary in places, so accept the value as a quoted string too.
      expect(src.includes(needle) || src.includes(`'${value}'`), needle).toBe(true)
    }
  })
})
