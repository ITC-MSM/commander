/**
 * Real card images for the lessons, by name.
 *
 * One request per page for exactly the cards it shows, through the catalog's own search endpoint
 * (`!"Name" or !"Name"` is the exact-name form the deckbuilder's query language already speaks).
 * The full `/api/cards` catalog is thousands of cards and the deckbuilder's business; a lesson
 * needs four. Falls back to the Scryfall-by-name image so a lesson still renders when the server
 * lacks a printing image — `getCardImageUrl` does that for every other card in the client.
 */
import { useEffect, useState } from 'react'
import type { CardSummary } from '@/components/deckbuilder/cardFilter'
import { getCardImageUrl } from '@/utils/cardImages'

export interface LessonCard {
  name: string
  imageUrl: string
  summary: CardSummary | null
}

const cache = new Map<string, Promise<Map<string, CardSummary>>>()

function fetchSummaries(names: readonly string[]): Promise<Map<string, CardSummary>> {
  const key = [...names].sort().join('|')
  const cached = cache.get(key)
  if (cached) return cached
  const q = names.map((n) => `!"${n.replace(/"/g, '')}"`).join(' or ')
  const promise = fetch(`/api/cards/search?q=${encodeURIComponent(q)}`)
    .then((res) => (res.ok ? res.json() : Promise.reject(new Error(`HTTP ${res.status}`))))
    .then((body: { cards?: CardSummary[] }) => {
      const index = new Map<string, CardSummary>()
      for (const card of body.cards ?? []) index.set(card.name, card)
      return index
    })
    .catch(() => new Map<string, CardSummary>())
  cache.set(key, promise)
  return promise
}

/** Resolve a fixed list of card names to images. Stable across renders for the same names. */
export function useLessonCards(names: readonly string[]): Record<string, LessonCard> {
  const key = names.join('|')
  const [cards, setCards] = useState<Record<string, LessonCard>>(() => fallback(names))

  useEffect(() => {
    let cancelled = false
    const wanted = key.split('|').filter(Boolean)
    fetchSummaries(wanted).then((index) => {
      if (cancelled) return
      const next: Record<string, LessonCard> = {}
      for (const name of wanted) {
        const summary = index.get(name) ?? null
        next[name] = { name, imageUrl: getCardImageUrl(name, summary?.imageUri ?? undefined, 'normal'), summary }
      }
      setCards(next)
    })
    return () => {
      cancelled = true
    }
  }, [key])

  return cards
}

function fallback(names: readonly string[]): Record<string, LessonCard> {
  const out: Record<string, LessonCard> = {}
  for (const name of names) out[name] = { name, imageUrl: getCardImageUrl(name, undefined, 'normal'), summary: null }
  return out
}
