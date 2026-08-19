/**
 * Tap-generated ("ghost") mouse events.
 *
 * A tap fires a compatibility mouse sequence — mouseover/mouseenter, mousemove, mousedown, mouseup,
 * click — after touchend. A page can normally suppress it by calling `preventDefault()` on
 * touchstart, but React attaches touchstart, touchmove and wheel as *passive* listeners on its root
 * container, so a component's `e.preventDefault()` inside `onTouchStart` is a no-op. Anything wired
 * to both touch and mouse therefore runs twice per tap.
 *
 * That is what made tapping a selected attacker blink and stay selected: the touch path deselected
 * it, and the phantom mousedown immediately after re-ran the same "start dragging an attacker"
 * handler, which selects an unselected attacker so its arrow can be dragged.
 *
 * Touch handlers call {@link noteTouchInteraction}; mouse handlers bail out on
 * {@link isGhostMouseEvent}. Deliberately time-based rather than keyed off `useHasHover()`: an
 * actual mouse plugged into a touchscreen device keeps working, it just has to move more than
 * `GHOST_MOUSE_WINDOW_MS` after the last touch. The state is module-level rather than per-card
 * because the ghost sequence is not guaranteed to land on the element the touch started on.
 */
const GHOST_MOUSE_WINDOW_MS = 700

let lastTouchAt = 0

export function noteTouchInteraction(): void {
  lastTouchAt = Date.now()
}

export function isGhostMouseEvent(): boolean {
  return Date.now() - lastTouchAt < GHOST_MOUSE_WINDOW_MS
}
