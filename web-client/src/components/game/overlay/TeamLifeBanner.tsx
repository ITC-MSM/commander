import { useGameStore } from '@/store/gameStore.ts'
import type { EntityId } from '@/types'
import { useIdentityColor, useTeamLabelFor, useTeammateNames } from '@/store/selectors'

/**
 * A Two-Headed Giant team's shared life total (CR 810.4), pinned to that team's edge of the table.
 *
 * A team's life belongs to its side of the board, not to the middle of the screen. The enemy team's
 * banner sits above their two boards and yours below yours, so each total is next to the cards it
 * is keeping alive — and the two boards under a banner read as what the rules say they are: two
 * heads sharing one life total.
 *
 * The banner deliberately carries **no player anchors and no click target**. A team is not a legal
 * target: CR 805.10b makes each attacking creature name one defending *player*, and every "target
 * player" effect names one player too. The heads under it — the board name plates — are the things
 * you click, and they already carry the anchors, the defender-assign gesture and the targeting
 * crosshair. Pointing an attacker at "the opponents" and having the client silently pick one of
 * them for you is the bug this replaces.
 */
export function TeamLifeBanner({
  teamMemberIds,
  anchor,
  isEnemyTeam,
}: {
  /** The team's seats, in turn order. The first is used for the team's label and hue. */
  teamMemberIds: readonly EntityId[]
  /** Which screen edge this team occupies — `'top'` for the far side, `'bottom'` for yours. */
  anchor: 'top' | 'bottom'
  /** True for a team the viewer is playing against — drives the declare-attackers hint. */
  isEnemyTeam: boolean
}) {
  const representative = teamMemberIds[0] ?? null
  const seat = useIdentityColor(representative)
  const label = useTeamLabelFor(representative)
  const members = useTeammateNames(representative)
  const gameState = useGameStore((state) => state.spectatingState?.gameState ?? state.gameState)
  const combatState = useGameStore((state) => state.combatState)

  const memberSeats = teamMemberIds
    .map((id) => gameState?.players.find((p) => p.playerId === id))
    .filter((p): p is NonNullable<typeof p> => p != null)
  if (memberSeats.length === 0) return null

  // Life and poison are pooled per team (CR 810.4 / 810.10), so every member reports the same
  // number and the first living one stands for the team.
  const living = memberSeats.filter((p) => !p.hasLost)
  const source = living[0] ?? memberSeats[0]!
  const life = source.life
  const poison = source.poisonCounters ?? 0

  // Declare-attackers hint. The banner isn't the target — the heads are — so when attackers are
  // waiting on a defender it says so and points down at them rather than quietly eating the click.
  const awaitingDefender =
    isEnemyTeam &&
    combatState?.mode === 'declareAttackers' &&
    combatState.selectedAttackers.some((id) => !combatState.attackerTargets[id])

  return (
    <div
      aria-label={`${label}: ${life} life${members ? ` (${members})` : ''}`}
      style={{
        position: 'absolute',
        // The top banner sits level with the two board plates it spans, not above them — that row
        // is the enemy team's whole header, and giving the banner a line of its own pushed their
        // boards down for nothing.
        ...(anchor === 'bottom' ? { bottom: 0 } : { top: 12 }),
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 57,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: 3,
        pointerEvents: 'none',
        userSelect: 'none',
      }}
    >
      {anchor === 'bottom' && awaitingDefender && <DefenderHint anchor={anchor} />}
      <div
        title={members ? `${label} — ${members}` : label}
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: 9,
          height: 28,
          padding: '0 14px',
          borderRadius: 999,
          border: `1px solid ${seat.base}`,
          background: 'rgba(8, 11, 18, 0.92)',
          boxShadow: `0 0 14px ${seat.base}33`,
          whiteSpace: 'nowrap',
        }}
      >
        <span
          style={{
            fontSize: 10,
            fontWeight: 800,
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            color: seat.bright,
          }}
        >
          {label}
        </span>
        <span
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 4,
            fontSize: 17,
            fontWeight: 800,
            fontVariantNumeric: 'tabular-nums',
            color: life <= 5 ? '#ff5555' : '#ffffff',
            textShadow: `0 0 8px ${seat.base}80, 0 1px 2px rgba(0, 0, 0, 0.8)`,
          }}
        >
          <span aria-hidden style={{ color: '#ff6b6b', fontSize: 12 }}>❤</span>
          {life}
        </span>
        {poison > 0 && (
          <span
            title={`${poison} poison counters (the team loses at 15 — CR 810.8d)`}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 3,
              fontSize: 12,
              fontWeight: 700,
              color: poison >= 15 ? '#ff5555' : '#9ae66e',
              fontVariantNumeric: 'tabular-nums',
            }}
          >
            <span aria-hidden>☠</span>
            {poison}
          </span>
        )}
      </div>
      {anchor === 'top' && awaitingDefender && <DefenderHint anchor={anchor} />}
    </div>
  )
}

/**
 * "Pick a head" — shown on the enemy banner while a selected attacker has no defender. CR 805.10b
 * requires each attacking creature to name one defending player, so the choice can't be collapsed
 * to "the opposing team"; this points at the two plates that are the actual targets.
 */
function DefenderHint({ anchor }: { anchor: 'top' | 'bottom' }) {
  return (
    <div
      role="status"
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 5,
        padding: '2px 9px',
        borderRadius: 999,
        border: '1px solid #ef5350',
        background: 'rgba(30, 8, 8, 0.92)',
        color: '#ffb3b3',
        fontSize: 10,
        fontWeight: 700,
        letterSpacing: '0.06em',
        textTransform: 'uppercase',
        whiteSpace: 'nowrap',
      }}
    >
      <span aria-hidden>{anchor === 'top' ? '↓' : '↑'}</span>
      Attack which head?
    </div>
  )
}
