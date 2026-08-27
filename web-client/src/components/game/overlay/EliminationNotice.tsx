import { useEffect, useRef, useState } from 'react'
import { useGameStore } from '@/store/gameStore'
import { identitySeatColor, selectGameState, selectTeamMap, selectViewingPlayerId } from '@/store/selectors'
import type { EntityId } from '@/types'

/** How long an elimination notice stays up. Long enough to read, short enough to never queue. */
const NOTICE_MS = 5000

interface Notice {
  key: number
  name: string
  color: string
}

/**
 * "💀 Bob has been eliminated" — a transient top-centre notice when another seat leaves a
 * multiplayer game (CR 800.4a). Until now the only signal was a rail chip quietly turning into
 * a tombstone and a grey log line; in a four-seat pod that is easy to miss, and "who is still in
 * this" is the question the whole table is asking at that moment.
 *
 * Derived from the roster (`hasLost` flipping true), not from the elimination message, so it
 * fires however the seat died — concede, damage, poison, decking — and for spectators too. The
 * seats already out when this mounts (a reconnect, a spectator joining late) are not announced.
 * Your own elimination is the defeat overlay's business, not a toast, and so is the elimination
 * that ends the game — fewer than two teams left standing (CR 104.2a / 104.2c) — which in
 * Two-Headed Giant is always both members of the losing team at once (CR 810.8a).
 */
export function EliminationNotice({ topOffset = 0 }: { topOffset?: number }) {
  const gameState = useGameStore(selectGameState)
  const viewingPlayerId = useGameStore(selectViewingPlayerId)
  const teamMap = useGameStore(selectTeamMap)
  const players = gameState?.players
  const isMulti = (players?.length ?? 0) > 2

  const seenLost = useRef<Set<EntityId> | null>(null)
  const [notice, setNotice] = useState<Notice | null>(null)

  useEffect(() => {
    if (!players) return
    if (seenLost.current === null) {
      seenLost.current = new Set(players.filter((p) => p.hasLost).map((p) => p.playerId))
      return
    }
    const seen = seenLost.current
    const livingTeams = new Set(players.filter((p) => !p.hasLost).map((p) => teamMap[p.playerId] ?? p.playerId))
    const gameOver = livingTeams.size < 2
    players.forEach((p, idx) => {
      if (!p.hasLost || seen.has(p.playerId)) return
      seen.add(p.playerId)
      if (!isMulti || gameOver || p.playerId === viewingPlayerId) return
      setNotice({ key: Date.now(), name: p.name, color: identitySeatColor(teamMap, p.playerId, idx).bright })
    })
  }, [players, isMulti, viewingPlayerId, teamMap])

  useEffect(() => {
    if (!notice) return
    const timer = setTimeout(() => setNotice((n) => (n?.key === notice.key ? null : n)), NOTICE_MS)
    return () => clearTimeout(timer)
  }, [notice])

  if (!notice) return null
  return (
    <div
      key={notice.key}
      role="status"
      aria-live="polite"
      onClick={() => setNotice(null)}
      style={{
        position: 'fixed',
        top: topOffset + 52,
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 120,
        display: 'inline-flex',
        alignItems: 'center',
        gap: 8,
        padding: '6px 14px',
        borderRadius: 999,
        border: `1px solid ${notice.color}`,
        background: 'rgba(12, 12, 18, 0.92)',
        boxShadow: `0 0 14px ${notice.color}55`,
        color: '#e8ecf5',
        fontSize: 12,
        fontWeight: 600,
        letterSpacing: '0.02em',
        whiteSpace: 'nowrap',
        cursor: 'pointer',
        animation: 'eliminationNoticeIn 220ms ease-out',
      }}
    >
      <span aria-hidden>💀</span>
      <span style={{ color: notice.color, fontWeight: 800 }}>{notice.name}</span>
      <span>has been eliminated</span>
      <style>{`
        @keyframes eliminationNoticeIn {
          from { opacity: 0; transform: translateX(-50%) translateY(-6px); }
          to { opacity: 1; transform: translateX(-50%) translateY(0); }
        }
      `}</style>
    </div>
  )
}
