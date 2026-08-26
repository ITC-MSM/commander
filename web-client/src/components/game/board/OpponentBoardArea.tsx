import { useLayoutEffect, useMemo, useRef, useState } from 'react'
import type React from 'react'
import type { RefObject } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { ClientPlayer } from '@/types'
import { hand } from '@/types'
import { useRevealedLibraryTopCard, useIdentityColor, useIsSharedLifeTeamGame } from '@/store/selectors'
import { useResponsiveContext } from './shared'
import { Battlefield } from './Battlefield'
import { CardRow } from './HandZone'
import { CommandZone } from './CommandZone'
import { ZonePile } from './ZonePiles'
import { styles } from './styles'
import { isLoneTargetRequirement } from '@/utils/targeting.ts'

/** Height of a shared-strip cell's name-plate band (the pill plus its top margin). */
export const CELL_PLATE_BAND = 34

/**
 * How far an inverted [HandFan] paints above the top of the box it is placed in — its own
 * negative `marginTop` plus the cards' negative `top` edge margin. Pushing an inverted cell hand
 * down by this much is what keeps it clear of the name plate above it.
 */
const INVERTED_FAN_LIFT = 30

/**
 * Sizing for the hand a board renders *inside* a shared-strip cell (table overview, team-split
 * bottom row, combat defender-focus split). The full-width fan is viewport-relative and would
 * run straight over the neighbouring cells, so the cell measures itself and the fan is capped
 * from that — deliberately smaller than a fan that merely fits, because with every board on
 * screen at once the battlefields are what the width is for.
 *
 * [handHeight] is an upper bound (`calculateFittingCardWidth` only ever shrinks the cap), which
 * is what makes it safe to reserve as a fixed band above the board. Exported so the viewer's own
 * bottom-row cell — which has no cell hand, its fan being the full-width one at the screen
 * bottom — can reserve the identical band and stay aligned with its neighbours.
 */
export function useCellHandMetrics(): {
  cellRef: RefObject<HTMLDivElement | null>
  cellWidth: number
  cardWidth: number
  handHeight: number
} {
  const responsive = useResponsiveContext()
  const cellRef = useRef<HTMLDivElement | null>(null)
  const [cellWidth, setCellWidth] = useState(0)
  useLayoutEffect(() => {
    const node = cellRef.current
    if (!node) return
    setCellWidth(node.getBoundingClientRect().width)
    const obs = new ResizeObserver((entries) => {
      const entry = entries[0]
      if (entry) setCellWidth(entry.contentRect.width)
    })
    obs.observe(node)
    return () => obs.disconnect()
  }, [])
  const cardWidth = Math.max(
    22,
    Math.min(Math.round(responsive.smallCardWidth * 0.72), Math.round(cellWidth / 9) || 999),
  )
  return { cellRef, cellWidth, cardWidth, handHeight: Math.round(cardWidth * 1.4) + 12 }
}

/**
 * One opponent's half of the board: hand fan (top), command zone | battlefield |
 * zone piles. This is today's 2-player opponent half, parameterized by player.
 *
 * Two layouts:
 * - `grid` — the classic 2-player placement: the hand is position:fixed at the
 *   viewport top and the board area is a direct grid child on row 2. Renders the
 *   exact markup GameBoard always had, so the 2-player game is pixel-identical.
 * - `strip` — a multiplayer slide item: the component renders a full-height
 *   strip cell; the hand is absolutely positioned inside it (so it slides with
 *   the board) above a reservation band matching grid row 1, and the board area
 *   fills the rest. Card scale machinery (slot sizing) is identical in both.
 */
export function OpponentBoardArea({
  opponent,
  layout,
  topOffset,
  handReservation = 0,
  stripBasis = '100%',
  hideHand = false,
  plateCarriesAnchors = false,
  activeTurnRingColor,
  onToggleCollapse,
  spectatorMode,
  isHijacking,
  hijackedSurfaceStyle,
  isAlly = false,
  allyColor,
  bottomHalf = false,
}: {
  opponent: ClientPlayer
  layout: 'grid' | 'strip'
  topOffset: number
  /** Strip layout only: height of the hand reservation band (grid row 1 height). */
  handReservation?: number
  /**
   * Strip layout only: this cell's share of the strip width as a CSS width value.
   * '100%' (default) for the one-board sliding camera; an equal fraction (or a
   * `calc(...)` share around collapsed tabs) when several boards share the strip
   * (table overview / combat defender-focus split) — card sizing self-measures per slot.
   */
  stripBasis?: string
  /**
   * Strip layout only: shared-strip view (table overview / combat defender-focus split).
   * Hides the opponent hand fan and its reservation band (the fans would overlap across
   * the narrow cells; rail chips carry the hand counts) and renders a seat-colored name
   * plate at the top of the cell instead — the board's "face".
   */
  hideHand?: boolean
  /**
   * Shared-strip view only: the name plate carries this player's anchors
   * (`data-life-id` etc.) so arrows, damage floats, and player-target clicks land on it.
   * False for the *viewed* board, whose anchors stay on the center-HUD life orb —
   * exactly one element per player may carry the anchors (see the OpponentRail comment).
   */
  plateCarriesAnchors?: boolean
  /**
   * Shared-strip view only: seat color for a persistent inset ring marking this cell as the
   * board of the player whose **turn** it is. With every board on screen at once, "whose turn
   * is it" is the thing worth a highlight — which cell the camera nominally tracks isn't.
   * Undefined = no ring.
   */
  activeTurnRingColor?: string
  /**
   * Table overview only: fold this cell down to a narrow tab (MTGO-style per-board
   * collapse) so the other boards split the freed width. Rendered as a small "−"
   * button next to the name plate; the collapsed tab itself is [CollapsedBoardTab].
   */
  onToggleCollapse?: () => void
  spectatorMode: boolean
  /** This opponent's seat is currently driven by this client (Mindslaver / hotseat). */
  isHijacking: boolean
  hijackedSurfaceStyle?: React.CSSProperties
  /**
   * Two-Headed Giant (CR 810): this board belongs to your teammate. You may see their hand
   * (CR 810.5b), so it renders face-up, and the cell gets an "ALLY" marker so it never reads as
   * an enemy board. You still can't act with their cards — only its controller plays from it.
   */
  isAlly?: boolean
  /** Team color for the ally marker (the viewing player's team hue). */
  allyColor?: string
  /**
   * This cell sits on the *bottom* half of a two-row "show table" layout, so its battlefield is
   * oriented like a player's own board — lands toward the bottom edge, creatures toward the center —
   * instead of the opponent orientation. The name plate still pins to the top of the cell.
   */
  bottomHalf?: boolean
}) {
  const revealedTopCard = useRevealedLibraryTopCard(opponent.playerId)
  const ghostCards = useMemo(
    () => (revealedTopCard ? [revealedTopCard] : []),
    [revealedTopCard]
  )
  const { cellRef, cellWidth, cardWidth: cellHandCardWidth, handHeight: cellHandHeight } =
    useCellHandMetrics()
  // A bottom-half cell's board is oriented like a player's own, so its hand hangs the same way
  // as yours (face toward the bottom edge) rather than inverted like an opponent's.
  const cellHandInverted = !bottomHalf

  /* Opponent hand — fixed at top of screen in grid layout; absolute inside the
     strip cell in strip layout (a strip cell starts at the viewport top, so the
     same `top` offset lands in the same place — but the hand travels with its
     board during slides). The face-up promotion during a Mindslaver-style hijack
     is itself the strongest signal that the controller is driving this hand. */
  const handBlock = (
    <div
      data-zone="opponent-hand"
      style={{
        position: layout === 'grid' ? 'fixed' : 'absolute',
        top: topOffset,
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 50,
      }}
    >
      <CardRow
        zoneId={hand(opponent.playerId)}
        faceDown={!isHijacking && !isAlly}
        small
        inverted
        interactive={isHijacking}
        ghostCards={isHijacking ? [] : ghostCards}
      />
    </div>
  )

  const boardBlock = (
    <div
      style={
        layout === 'grid'
          ? styles.opponentArea
          : {
              // styles.opponentArea minus the grid-row binding — the strip cell
              // provides the vertical slot instead.
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'flex-start',
              minHeight: 0,
              overflow: 'hidden',
              flex: 1,
              width: '100%',
            }
      }
    >
      <div style={{ ...styles.playerRowWithZones, alignItems: 'flex-start' }}>
        {/* Opponent command zone (left side) — Commander format only; renders nothing otherwise. */}
        <CommandZone player={opponent} isOpponent />

        <div
          style={{
            ...styles.playerMainArea,
            ...(isHijacking ? hijackedSurfaceStyle : null),
          }}
        >
          {/* Opponent battlefield — lands first (closer to opponent), then creatures. On the
              bottom half of a two-row layout, flip to the player orientation so lands sit toward
              the bottom edge. */}
          <Battlefield isOpponent={!bottomHalf} playerId={opponent.playerId} spectatorMode={spectatorMode} />
        </div>

        {/* Opponent deck/graveyard (right side) */}
        <ZonePile player={opponent} isOpponent />
      </div>
    </div>
  )

  if (layout === 'grid') {
    return (
      <>
        {handBlock}
        {boardBlock}
      </>
    )
  }

  return (
    <div
      ref={cellRef}
      data-opponent-board={opponent.playerId}
      data-ally={isAlly || undefined}
      style={{
        flex: `0 0 ${stripBasis}`,
        minWidth: stripBasis,
        height: '100%',
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
        transition: 'flex-basis 220ms cubic-bezier(0.4, 0, 0.2, 1), min-width 220ms cubic-bezier(0.4, 0, 0.2, 1)',
      }}
    >
      {/* Two-Headed Giant ally marker — a team-colored corner badge so a teammate's board (with
          its face-up hand) is never mistaken for an opponent's. */}
      {isAlly && !hideHand && (
        <div
          aria-hidden
          style={{
            position: 'absolute',
            top: topOffset + 4,
            left: 10,
            zIndex: 55,
            display: 'inline-flex',
            alignItems: 'center',
            gap: 5,
            padding: '2px 9px',
            borderRadius: 999,
            border: `1px solid ${allyColor ?? '#2FD1A4'}`,
            background: 'rgba(8, 12, 18, 0.82)',
            color: allyColor ?? '#2FD1A4',
            fontSize: 10,
            fontWeight: 800,
            letterSpacing: '0.1em',
            textTransform: 'uppercase',
            pointerEvents: 'none',
            userSelect: 'none',
          }}
        >
          <span aria-hidden style={{ width: 7, height: 7, borderRadius: '50%', background: allyColor ?? '#2FD1A4' }} />
          Ally · {opponent.name}
        </div>
      )}
      {/* A hijack-controlled hand must stay visible even in shared-strip views —
          this client is playing from it, so it keeps the full-size interactive fan. */}
      {(!hideHand || isHijacking) && handBlock}
      {/* Shared-strip view: the board's "face" — name (+ life outside a shared-life team
          game) at the top of the cell. Sits below the hand when a hijack forces the fan
          visible. */}
      {hideHand && (
        <BoardNamePlate
          player={opponent}
          carriesAnchors={plateCarriesAnchors}
          top={(isHijacking ? handReservation : 0) + 6}
          isAlly={isAlly}
          {...(allyColor ? { allyColor } : {})}
        />
      )}
      {/* Shared-strip view: this seat's hand, scaled down to the cell and sitting under the
          name plate. Knowing how many cards each player is holding — and, for a Two-Headed
          Giant ally whose hand is open to you (CR 810.5b), *which* cards — is board state you
          shouldn't have to slide the camera onto a board to read. */}
      {hideHand && !isHijacking && (
        <div
          data-zone="opponent-hand"
          style={{
            position: 'absolute',
            // An inverted fan lifts itself 30px above its box (HandFan's negative marginTop plus
            // the cards' own edge margin) — without matching that offset the top row's cards
            // would be drawn straight through the name plate.
            top: CELL_PLATE_BAND + (cellHandInverted ? INVERTED_FAN_LIFT : 0),
            left: 0,
            right: 0,
            height: cellHandHeight,
            display: 'flex',
            // Anchor the fan at the edge it hangs from, so the arc grows into the band rather
            // than out of it: down from the top for an opponent-side hand, up from the bottom
            // for a bottom-row one.
            alignItems: cellHandInverted ? 'flex-start' : 'flex-end',
            justifyContent: 'center',
            zIndex: 50,
            pointerEvents: isAlly ? 'auto' : 'none',
          }}
        >
          <CardRow
            zoneId={hand(opponent.playerId)}
            faceDown={!isAlly}
            small
            inverted={cellHandInverted}
            fan
            fitWidth={Math.max(60, cellWidth - 16)}
            maxCardWidth={cellHandCardWidth}
            ghostCards={[]}
          />
        </div>
      )}
      {/* Fold-away control (table overview): collapse this cell to a tab so the
          other boards grow. Top-right corner, clear of the centered name plate. */}
      {onToggleCollapse && (
        <button
          onClick={onToggleCollapse}
          title={`Collapse ${opponent.name}'s board`}
          style={{
            position: 'absolute',
            top: (isHijacking ? handReservation : 0) + 6,
            right: 8,
            zIndex: 56,
            width: 24,
            height: 24,
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderRadius: 6,
            border: '1px solid #3a3a44',
            background: 'rgba(10, 12, 20, 0.85)',
            color: '#9fb0d0',
            fontSize: 14,
            fontWeight: 800,
            lineHeight: 1,
            cursor: 'pointer',
            padding: 0,
          }}
        >
          −
        </button>
      )}
      {/* Persistent inset ring marking the active player's cell in a shared-strip view. */}
      {activeTurnRingColor && (
        <div
          aria-hidden
          style={{
            position: 'absolute',
            inset: 2,
            pointerEvents: 'none',
            borderRadius: 10,
            boxShadow: `inset 0 0 0 2px ${activeTurnRingColor}55, inset 0 0 18px ${activeTurnRingColor}22`,
          }}
        />
      )}
      {/* Reservation band mirrors grid row 1 so the board area below aligns
          exactly with the 2-player opponent area (grid row 2). Shared-strip views
          replace it with room for the name plate — the board gets the rest of the
          vertical space back. */}
      <div
        style={{
          height: hideHand
            ? (isHijacking
                ? handReservation
                : cellHandHeight + (cellHandInverted ? INVERTED_FAN_LIFT : 0)) + CELL_PLATE_BAND
            : handReservation,
          flexShrink: 0,
        }}
        aria-hidden
      />
      {boardBlock}
    </div>
  )
}

/**
 * A collapsed board's stand-in in the table overview (MTGO-style per-board collapse):
 * a narrow full-height tab with the seat color, a "+" affordance, and the player's name
 * running vertically. The whole tab is one click target that re-expands the board. The
 * seat's real board stays mounted off-screen (with the other hidden boards) so its card
 * anchors keep bundling to the rail chip, which also carries the player anchors — the
 * tab itself carries none.
 */
export function CollapsedBoardTab({
  player,
  onExpand,
}: {
  player: ClientPlayer
  onExpand: () => void
}) {
  const seat = useIdentityColor(player.playerId)
  const tomb = player.hasLost
  return (
    <div
      data-collapsed-board={player.playerId}
      role="button"
      title={`Expand ${player.name}'s board`}
      onClick={onExpand}
      style={{
        flex: `0 0 ${COLLAPSED_TAB_WIDTH}px`,
        minWidth: COLLAPSED_TAB_WIDTH,
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: 8,
        padding: '8px 0',
        boxSizing: 'border-box',
        borderRadius: 8,
        border: `1px solid ${seat.base}55`,
        background: `linear-gradient(180deg, ${seat.soft}, rgba(10, 12, 20, 0.85))`,
        cursor: 'pointer',
        userSelect: 'none',
        overflow: 'hidden',
        transition: 'flex-basis 220ms cubic-bezier(0.4, 0, 0.2, 1), min-width 220ms cubic-bezier(0.4, 0, 0.2, 1)',
      }}
    >
      <span
        aria-hidden
        style={{
          width: 20,
          height: 20,
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          borderRadius: 5,
          border: `1px solid ${seat.base}`,
          color: seat.bright,
          fontSize: 13,
          fontWeight: 800,
          lineHeight: 1,
          flexShrink: 0,
        }}
      >
        +
      </span>
      <span
        aria-hidden
        style={{
          width: 8,
          height: 8,
          borderRadius: '50%',
          background: seat.base,
          boxShadow: `0 0 5px ${seat.base}`,
          flexShrink: 0,
          filter: tomb ? 'grayscale(1)' : 'none',
        }}
      />
      <span
        style={{
          writingMode: 'vertical-rl',
          fontSize: 12,
          fontWeight: 700,
          letterSpacing: '0.06em',
          color: seat.bright,
          maxHeight: '55%',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
        }}
      >
        {player.name}
      </span>
    </div>
  )
}

/** Width of a collapsed board's tab in the table overview. */
export const COLLAPSED_TAB_WIDTH = 30

/**
 * The board's "face" in a shared-strip view: a compact seat-colored pill (name + life)
 * pinned to the top of the cell. When [carriesAnchors] it holds this player's anchor
 * attributes, so attack/targeting arrows and damage floats land on it instead of the
 * board's lands, and it doubles as the player-level click target — defender assignment
 * while declaring attackers, player targeting during a selection (same handling as the
 * rail chip's crosshair).
 */
function BoardNamePlate({
  player,
  carriesAnchors,
  top,
  isAlly = false,
  allyColor,
}: {
  player: ClientPlayer
  carriesAnchors: boolean
  top: number
  /**
   * Two-Headed Giant: mark this board as your teammate's. The floating corner badge stands down
   * wherever a plate renders — two labels naming the same player is one too many — so the plate
   * carries the marker instead.
   */
  isAlly?: boolean
  allyColor?: string
}) {
  const seat = useIdentityColor(player.playerId)
  const playerId = player.playerId
  // Two-Headed Giant (CR 810): the team's single shared life lives on the center-HUD team orb,
  // so repeating it on every teammate's plate would print the same number up to four times.
  // The plate keeps the name (which is the thing a plate is for) and drops the life.
  const sharedLifeTeam = useIsSharedLifeTeamGame()

  const combatState = useGameStore((state) => state.combatState)
  const assignDefender = useGameStore((state) => state.assignDefenderToSelectedAttackers)
  const draggingAttackerId = useGameStore((state) => state.draggingAttackerId)
  const targetingState = useGameStore((state) => state.targetingState)
  const addTarget = useGameStore((state) => state.addTarget)
  const removeTarget = useGameStore((state) => state.removeTarget)
  const pendingDecision = useGameStore((state) => state.pendingDecision)
  const submitTargetsDecision = useGameStore((state) => state.submitTargetsDecision)
  const decisionSelectionState = useGameStore((state) => state.decisionSelectionState)
  const toggleDecisionSelection = useGameStore((state) => state.toggleDecisionSelection)

  // Defender assignment (mirrors RailChip): legal while declaring attackers with a
  // selection or an attacker drag in flight.
  const declaringAttackers = combatState?.mode === 'declareAttackers'
  const isDefenderTarget =
    declaringAttackers && (combatState?.validAttackTargets.includes(playerId) ?? false)
  const isDefenderAssignTarget =
    isDefenderTarget &&
    ((combatState?.selectedAttackers.length ?? 0) > 0 || draggingAttackerId !== null)

  // Player-as-target (mirrors RailChip's crosshair handling).
  const isTargetingSelected = targetingState?.selectedTargets.includes(playerId) ?? false
  const isValidTargetingTarget = targetingState?.validTargets.includes(playerId) ?? false
  const isChooseTargetsDecision = pendingDecision?.type === 'ChooseTargetsDecision'
  // Only a lone single-target requirement uses the immediate click-to-submit path; a multi-target
  // player slot (e.g. Parker Luck's "two target players") is picked via the decisionSelectionState
  // toggle path (isValidDecisionSelection) instead, so it must NOT match here.
  const isValidDecisionTarget =
    isChooseTargetsDecision &&
    isLoneTargetRequirement(pendingDecision) &&
    (pendingDecision.legalTargets[0] ?? []).includes(playerId)
  const isValidDecisionSelection = decisionSelectionState?.validOptions.includes(playerId) ?? false
  const isSelectedDecisionOption = decisionSelectionState?.selectedOptions.includes(playerId) ?? false
  const isPlayerTargetable = isValidTargetingTarget || isValidDecisionTarget || isValidDecisionSelection
  const isPlayerTargetSelected = isTargetingSelected || isSelectedDecisionOption

  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    if (isDefenderTarget && (combatState?.selectedAttackers.length ?? 0) > 0) {
      assignDefender(playerId)
      return
    }
    if (isTargetingSelected) {
      removeTarget(playerId)
      return
    }
    if (isValidTargetingTarget) {
      addTarget(playerId)
      return
    }
    if (isValidDecisionTarget) {
      submitTargetsDecision({ 0: [playerId] })
      return
    }
    if (isValidDecisionSelection) {
      toggleDecisionSelection(playerId)
    }
  }

  const interactive = isDefenderAssignTarget || isPlayerTargetable || isPlayerTargetSelected
  const lifeDanger = player.life <= 5
  const borderColor = isDefenderAssignTarget
    ? '#ff4444'
    : isPlayerTargetSelected
      ? '#ffff00'
      : isPlayerTargetable
        ? '#ff4444'
        : seat.base

  return (
    <div
      data-board-plate={playerId}
      {...(carriesAnchors
        ? {
            'data-player-id': playerId,
            'data-life-id': playerId,
            'data-life-display': playerId,
          }
        : {})}
      role={interactive ? 'button' : undefined}
      title={
        isDefenderAssignTarget
          ? `Attack ${player.name}`
          : isPlayerTargetable || isPlayerTargetSelected
            ? (isPlayerTargetSelected ? `Unselect ${player.name}` : `Target ${player.name}`)
            : player.name
      }
      onClick={interactive ? handleClick : undefined}
      style={{
        position: 'absolute',
        top,
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 56,
        display: 'inline-flex',
        alignItems: 'center',
        gap: 6,
        height: 24,
        padding: '0 11px',
        borderRadius: 999,
        border: `${interactive ? 2 : 1}px solid ${borderColor}`,
        background: 'rgba(10, 12, 20, 0.9)',
        color: '#dde3f0',
        fontSize: 12,
        fontWeight: 700,
        whiteSpace: 'nowrap',
        userSelect: 'none',
        cursor: interactive ? 'pointer' : 'default',
        pointerEvents: 'auto',
        boxShadow: isDefenderAssignTarget
          ? '0 0 12px rgba(255, 68, 68, 0.6)'
          : isPlayerTargetSelected
            ? '0 0 10px rgba(255, 255, 0, 0.6)'
            : 'none',
        transition: 'border-color 150ms, box-shadow 150ms',
      }}
    >
      <span
        aria-hidden
        style={{
          width: 9,
          height: 9,
          borderRadius: '50%',
          background: seat.base,
          boxShadow: `0 0 5px ${seat.base}`,
          flexShrink: 0,
        }}
      />
      <span
        style={{
          maxWidth: 140,
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          color: seat.bright,
        }}
      >
        {player.name}
      </span>
      {isAlly && (
        <span
          aria-hidden
          title="Your teammate"
          style={{
            fontSize: 9,
            fontWeight: 800,
            letterSpacing: '0.1em',
            color: allyColor ?? seat.bright,
            border: `1px solid ${allyColor ?? seat.base}`,
            padding: '0 4px',
            borderRadius: 3,
            lineHeight: '13px',
            flexShrink: 0,
          }}
        >
          ALLY
        </span>
      )}
      {!sharedLifeTeam && (
        <span
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 3,
            fontVariantNumeric: 'tabular-nums',
            color: lifeDanger ? '#ff5555' : '#ffffff',
          }}
        >
          <span aria-hidden style={{ color: '#ff6b6b', fontSize: 11 }}>❤</span>
          {player.life}
        </span>
      )}
    </div>
  )
}
