import { useSyncExternalStore } from 'react'

/**
 * Whether the primary pointer can hover — false on phones and tablets, true with a mouse or
 * trackpad (including a hybrid laptop whose screen also takes touch).
 *
 * This is a *capability* question, not a size question, so it is deliberately not
 * `useResponsive().isMobile` (viewport width): a narrow desktop window still hovers, and a
 * landscape tablet still can't. Anything that exists only because hover exists — the card preview
 * that follows the cursor — or only because it doesn't — the "View card" row in the action menu,
 * tap-to-dismiss on the preview — keys off this.
 *
 * Every card on the board calls this, so the MediaQueryList is a module-level singleton —
 * `useSyncExternalStore` calls `getSnapshot` on every render, and re-running `matchMedia()` there
 * would allocate a query object per card per repaint. Reading `.matches` off the one instance is
 * free and always current, which a cached copy would not be: a change that lands while nothing is
 * subscribed has no listener to record it.
 *
 * Assume hover wherever the query can't be answered (SSR, jsdom). The desktop behaviour is the safe
 * default — it costs a phone nothing it didn't already have, while the reverse would strip hover
 * previews from a real mouse.
 */
const HOVER_QUERY = '(hover: hover)'

const mediaQuery: MediaQueryList | null =
  typeof window !== 'undefined' && window.matchMedia ? window.matchMedia(HOVER_QUERY) : null

function subscribe(onChange: () => void): () => void {
  if (!mediaQuery) return () => {}
  mediaQuery.addEventListener('change', onChange)
  return () => mediaQuery.removeEventListener('change', onChange)
}

function getSnapshot(): boolean {
  return mediaQuery ? mediaQuery.matches : true
}

function getServerSnapshot(): boolean {
  return true
}

export function useHasHover(): boolean {
  return useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot)
}
