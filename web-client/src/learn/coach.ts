/**
 * The in-game coach for the course's missions: one tip at a time, chosen from what the board is
 * asking of the player right now, and a spot on the board to ring while saying it.
 *
 * Pure: {@link coachTip} takes a small projection of the game state and returns the tip to show,
 * so the rules are testable without a store. A mission can re-word any tip by its key
 * (`Mission.hints`); the React overlay (`LearnCoach.tsx`) does the projecting and the drawing.
 *
 * Every tip teaches the app as well as the game: the button it names is the button's real label
 * (`{pass}` is substituted with the server-computed one), and the gesture it names is the one
 * this device has — `{read}` is "Hover a card" with a mouse and "Press and hold a card" on a
 * touch screen, `{click}` is "Click" or "Tap".
 *
 * The coach is armed with a mission id by the brief that starts the game and disarmed when that
 * game ends — it rides sessionStorage because the hand-off into a scenario game is a full
 * navigation (`/?token=…`), which drops React state but not the tab.
 */
import type { EntityId } from '@/types'
import type { ClientEvent } from '@/types/events'
import type { ClientCard } from '@/types/gameState'
import type { MissionId } from './missions'
import { missionById } from './missions'
import type { SpotId } from './spots'

const COACH_KEY = 'argentum.learn.coach'
const TOUR_KEY = 'argentum.learn.coach.tour'

export function armCoach(mission: MissionId) {
  try {
    sessionStorage.setItem(COACH_KEY, mission)
    sessionStorage.removeItem(TOUR_KEY)
  } catch {
    // Storage unavailable — the game still starts, just without a coach.
  }
}

/**
 * The table tour runs once per game. It rides sessionStorage rather than component state
 * because the board unmounts and remounts across a reconnect or a resync, and the coach with it.
 */
export function tourSeen(): boolean {
  try {
    return sessionStorage.getItem(TOUR_KEY) === '1'
  } catch {
    return false
  }
}

export function markTourSeen() {
  try {
    sessionStorage.setItem(TOUR_KEY, '1')
  } catch {
    // Then the tour shows again after a remount; nothing worse.
  }
}

export function disarmCoach() {
  try {
    sessionStorage.removeItem(COACH_KEY)
    sessionStorage.removeItem(TOUR_KEY)
  } catch {
    // Nothing to remove.
  }
}

/** The mission the coach was armed for, or null when this game was not started by the course. */
export function armedMission(): MissionId | null {
  try {
    return missionById(sessionStorage.getItem(COACH_KEY))?.id ?? null
  } catch {
    return null
  }
}

/** What the coach needs to know — derived from the client game state and the server's legal actions. */
export interface CoachView {
  turnNumber: number
  step: string
  isMyTurn: boolean
  hasPriority: boolean
  /** Server says a land can be played right now. */
  canPlayLand: boolean
  /** Server says at least one spell in hand is castable right now. */
  canCast: boolean
  /** The declare-attackers choice is open. */
  canAttack: boolean
  /** The declare-blockers choice is open. */
  canBlock: boolean
  /** Some other decision (a "may", a discard, a server-side target choice) is waiting on the player. */
  hasDecision: boolean
  /** The player is mid-cast, and the spell wants a target picked on the board. */
  isTargeting: boolean
  /** A spell or ability is waiting on the stack. */
  stackSize: number
  attackersIncoming: number
  /** While declaring attackers: how many creatures are selected to attack so far. */
  attackersSelected: number
  /**
   * Of those, how many the player picked themselves — a creature that must attack is selected
   * by the board before the player touches anything.
   */
  attackersChosen: number
  /** While declaring attackers: untapped creatures of mine that would stay home if the selected ones attack. */
  blockersLeft: number
  /** Creatures the opponent has on the battlefield. */
  theirCreatures: number
  /** The game ended because the player conceded — not a finished mission. */
  conceded: boolean
  /**
   * What the big button at the bottom right says right now — "Pass", "Pass to Attackers",
   * "End Turn", "Resolve". The server computes it; the coach names it so the tip and the button
   * agree.
   */
  passLabel: string
  /** Mouse or trackpad (hover works) rather than a touch screen. */
  hasHover: boolean
  isGameOver: boolean
  won: boolean | null
}

export interface CoachTip {
  /** Stable key so the overlay can animate only when the tip actually changes. */
  key: string
  title: string
  body: string
  /** Tone drives the accent colour: what to do now, what is happening, a mistake in the making, or the end of the game. */
  tone: 'act' | 'watch' | 'warn' | 'done'
  /** The board element to ring while this tip is up. */
  spot?: SpotId
}

export interface TipOverride {
  title?: string
  body?: string
  spot?: SpotId
}

/** The device-specific words a tip can use, with the mouse spelling first. */
const GESTURES: Record<string, [hover: string, touch: string]> = {
  '{read}': ['Hover a card', 'Press and hold a card'],
  '{read-lower}': ['hover it', 'press and hold it'],
  '{click}': ['Click', 'Tap'],
  '{click-lower}': ['click', 'tap'],
}

/** Substitute `{pass}` and the gesture words so a tip always names this device's real controls. */
export function wordTip(text: string, view: Pick<CoachView, 'passLabel' | 'hasHover'>): string {
  let out = text.replaceAll('{pass}', view.passLabel)
  for (const [token, [hover, touch]] of Object.entries(GESTURES)) {
    out = out.replaceAll(token, view.hasHover ? hover : touch)
  }
  return out
}

export function coachTip(view: CoachView, overrides: Partial<Record<string, TipOverride>> = {}): CoachTip {
  const tip = baseTip(view)
  const override = overrides[tip.key]
  const merged: CoachTip = override ? { ...tip, ...override } : tip
  return { ...merged, title: wordTip(merged.title, view), body: wordTip(merged.body, view) }
}

function baseTip(view: CoachView): CoachTip {
  if (view.isGameOver) {
    if (view.conceded) {
      return {
        key: 'conceded',
        title: 'You conceded.',
        body: 'That ends the game but not the mission — it only counts when a game is played to the end, won or lost. Play it again whenever you like.',
        tone: 'done',
      }
    }
    if (view.won === true) {
      return {
        key: 'won',
        title: 'You won.',
        body: 'Mission complete. The next card in the hand is waiting — or play this one again.',
        tone: 'done',
      }
    }
    if (view.won === false) {
      return {
        key: 'lost',
        title: 'Game over.',
        body: 'That was a real game, and losing one is how everybody starts. Play it again, or move on — the mission counts either way.',
        tone: 'done',
      }
    }
    return { key: 'draw', title: 'A draw.', body: 'Rare, but it happens. The mission counts.', tone: 'done' }
  }

  if (view.isTargeting) {
    return {
      key: 'target',
      title: 'Pick the target.',
      body: 'The spell needs something to aim at. The cards it can legally target are highlighted — {click-lower} one, then Confirm. Cancel takes the spell back into your hand.',
      tone: 'act',
      spot: 'battlefield',
    }
  }

  if (view.hasDecision) {
    return {
      key: 'decision',
      title: 'The game is asking you something.',
      body: 'Read the prompt — it wants a target, a choice, or a yes/no. Highlighted cards are the legal picks; {click-lower} one. Back and Cancel are always there if you change your mind.',
      tone: 'act',
    }
  }

  if (view.canBlock) {
    return {
      key: 'block',
      title:
        view.attackersIncoming > 1
          ? `${view.attackersIncoming} creatures are attacking you.`
          : 'A creature is attacking you.',
      body: 'Drag one of your untapped creatures onto the attacker it should block; an arrow shows the block. A blocked attacker hits your creature instead of you. Then press Confirm Blocks — or No Blocks to take the hit.',
      tone: 'act',
      spot: 'battlefield',
    }
  }

  if (view.canAttack) {
    // The classic first mistake: sending in the only creature that could block, into a board that
    // can hit back. Said before the attack is confirmed, while it can still be undone with a click.
    if (view.attackersSelected > 0 && view.blockersLeft === 0 && view.theirCreatures > 0) {
      return {
        key: 'attack-warning',
        title: 'That leaves nobody home.',
        body:
          view.theirCreatures > 1
            ? `Attackers tap, and they have ${view.theirCreatures} creatures that can hit back next turn with nothing of yours untapped to block. Sure? {click} a creature again to keep it back, or go ahead with Attack with N.`
            : 'Attackers tap, and they have a creature that can hit back next turn with nothing of yours untapped to block. Sure? {click} a creature again to keep it back, or go ahead with Attack with N.',
        tone: 'warn',
        spot: 'combat-buttons',
      }
    }
    return {
      key: 'attack',
      title: 'Combat — your creatures can attack.',
      body: '{click} a creature to send it in, then Attack with N — or press Attack All. Attackers tap, so think about what you leave back to block on their turn. Skip Attacking is always allowed.',
      tone: 'act',
      spot: 'combat-buttons',
    }
  }

  if (view.isMyTurn && view.hasPriority) {
    if (view.stackSize > 0) {
      return {
        key: 'stack-mine',
        title: 'A spell is waiting on the stack.',
        body: view.canCast
          ? 'Nothing has happened yet. You can answer with an instant — drag it out or {click-lower} it — or press {pass} and let the stack resolve, last spell first.'
          : 'Nothing to answer it with, so press {pass}. The stack resolves top-down: the last spell cast happens first, then the one under it.',
        tone: view.canCast ? 'act' : 'watch',
        spot: 'stack',
      }
    }
    if (view.canPlayLand && view.canCast) {
      return {
        key: 'land-and-cast',
        title: 'Your main phase.',
        body: 'Play a land first — drag it from your hand onto the battlefield, or {click-lower} it and choose Play. Then do the same with a creature or spell you can afford; your lands tap for you.',
        tone: 'act',
        spot: 'hand',
      }
    }
    if (view.canPlayLand) {
      return {
        key: 'land',
        title: 'Play a land.',
        body: 'One per turn, and it never costs anything. Drag a land from your hand onto the battlefield, or {click-lower} it and choose Play — more lands next turn means bigger spells next turn.',
        tone: 'act',
        spot: 'hand',
      }
    }
    if (view.canCast) {
      return {
        key: 'cast',
        title: 'You can cast something.',
        body: 'Cards you can afford glow in your hand. Drag one onto the battlefield, or {click-lower} it and choose Cast. Nothing worth casting? Press {pass} to move on.',
        tone: 'act',
        spot: 'hand',
      }
    }
    if (view.step === 'PRECOMBAT_MAIN') {
      return {
        key: 'pass-to-combat',
        title: 'Nothing left to play.',
        body: 'Press {pass}, the blue button at the bottom right. If you have a creature that can attack, the game will stop at combat and ask.',
        tone: 'act',
        spot: 'pass',
      }
    }
    return {
      key: 'pass',
      title: 'Nothing to do here.',
      body: 'Press {pass}. The game moves on, and hands the turn over when yours is done.',
      tone: 'act',
      spot: 'pass',
    }
  }

  if (!view.isMyTurn && view.hasPriority) {
    if (view.attackersIncoming > 0 && view.stackSize === 0) {
      return {
        key: 'respond-attack',
        title: view.attackersIncoming > 1 ? 'They are attacking with ' + view.attackersIncoming + ' creatures.' : 'They are attacking.',
        body: view.canCast
          ? 'Attackers are declared; blocks come next. A flash creature or an instant cast now still counts for this combat — or press {pass} to go straight to your blocks.'
          : 'Attackers are declared; blocks come next. Press {pass} and the game will ask you to block.',
        tone: view.canCast ? 'act' : 'watch',
        spot: view.canCast ? 'hand' : 'pass',
      }
    }
    if (view.stackSize > 0 && view.canCast) {
      return {
        key: 'respond',
        title: 'Their spell is waiting — you can respond.',
        body: 'It sits on the stack in the middle of the table and has not happened yet. Cast an instant now and yours resolves first, because it was cast last. Or press {pass} to let theirs happen.',
        tone: 'act',
        spot: 'hand',
      }
    }
    if (view.canCast) {
      return {
        key: 'respond-window',
        title: 'Their turn — you have a moment to respond.',
        body: 'You can cast an instant right now — drag it out or {click-lower} it, then Cast. Otherwise, press {pass}.',
        tone: 'watch',
        spot: 'hand',
      }
    }
    return {
      key: 'respond-idle',
      title: 'Their turn — you have a moment to respond.',
      body: 'Nothing you can cast at instant speed, so press {pass} and watch what they do.',
      tone: 'watch',
      spot: 'pass',
    }
  }

  if (view.turnNumber <= 1 && view.isMyTurn) {
    return {
      key: 'first-turn',
      title: 'This is your first turn.',
      body: 'The strip at the top shows where in the turn you are. You get the board back the moment there is something to decide.',
      tone: 'watch',
      spot: 'phase-strip',
    }
  }

  // Nothing to decide right now. Two different keys, because they are two different situations —
  // a mission's "the Tutor will play a land and pass" must never show in the player's own combat.
  const phase = phaseOf(view.step)
  if (view.isMyTurn) {
    return {
      key: 'working',
      title: phase === 'combat' ? 'Combat is playing out.' : phase === 'end' ? 'Your turn is ending.' : 'The game is working through your turn.',
      body:
        phase === 'combat'
          ? 'Attackers hit, blockers hit back, and the damage is dealt — the strip at the top walks through it. You get the board back the moment there is something to decide.'
          : phase === 'end'
            ? 'The Tutor gets a last chance to act, then it is their turn. Watch the strip at the top.'
            : 'Steps with nothing to decide pass on their own. You get the board back the moment there is something to do.',
      tone: 'watch',
      spot: 'phase-strip',
    }
  }
  return {
    key: 'waiting',
    title: 'The Tutor is taking a turn.',
    body:
      phase === 'combat'
        ? 'They are in combat. If a creature of theirs attacks, the game will stop and ask you to block.'
        : phase === 'end'
          ? 'Their turn is ending. Yours is next — the strip at the top comes back to blue.'
          : phase === 'beginning'
            ? 'They untap and draw. Then their main phase: a land, and whatever they can afford.'
            : 'They can play a land and cast spells. If they cast something you may get a chance to respond; if they attack you will be asked to block.',
    tone: 'watch',
    spot: 'phase-strip',
  }
}

// ── Answering the player's own moves ────────────────────────────────────────
//
// A tip says what the board is waiting for; a notice says what the player just did. Without the
// second, a player who plays the Forest while the tour is still introducing the hand is left
// with a coach describing a table that no longer exists. Every notice is read off the same
// facts the objectives use — the log for plays, casts, attacks and blocks — plus two consecutive
// views for the moves that leave no log entry (a pass, an answered prompt, a skipped attack).

/** One line said back to the player about the thing they just did. */
export interface ActionNotice {
  /** Stable per action, so the line animates once and a re-render does not replay it. */
  key: string
  text: string
}

function hasType(card: ClientCard | undefined, type: string): boolean {
  return card !== undefined && card.cardTypes.some((t) => t.toUpperCase() === type)
}

function listNames(names: readonly string[]): string {
  if (names.length <= 1) return names[0] ?? ''
  return `${names.slice(0, -1).join(', ')} and ${names[names.length - 1]}`
}

/**
 * What the player did in the log entries that arrived since the coach last looked, worded back
 * to them — or null when none of those entries were theirs. `offset` is where `fresh` starts in
 * the full log, so the key stays stable across renders. The last thing they did wins: a creature
 * cast is logged as a cast and then as an arrival, and the arrival is the one worth saying.
 */
export function noticeFromLog(
  fresh: readonly ClientEvent[],
  me: EntityId,
  cards: Readonly<Record<EntityId, ClientCard>>,
  offset: number,
): ActionNotice | null {
  let notice: ActionNotice | null = null
  const attackers: string[] = []
  for (const [i, e] of fresh.entries()) {
    const key = `${e.type}-${offset + i}`
    switch (e.type) {
      case 'permanentEntered': {
        if (e.controllerId !== me) continue
        const card = cards[e.cardId]
        if (hasType(card, 'LAND')) {
          notice = { key, text: `${e.cardName} is on the battlefield — one more mana every turn from now on.` }
        } else if (hasType(card, 'CREATURE')) {
          notice = { key, text: `${e.cardName} is on the battlefield. It can attack from your next turn.` }
        } else {
          notice = { key, text: `${e.cardName} is on the battlefield.` }
        }
        continue
      }
      case 'spellCast':
        if (e.casterId !== me) continue
        notice = { key, text: `You cast ${e.spellName}. It sits on the stack until everyone passes.` }
        continue
      case 'creatureAttacked':
        if (e.attackingPlayerId !== me) continue
        attackers.push(e.creatureName)
        notice = {
          key: `attack-${offset + i - attackers.length + 1}`,
          text: `${listNames(attackers)} ${attackers.length > 1 ? 'attack' : 'attacks'}.`,
        }
        continue
      case 'creatureBlocked':
        if (cards[e.blockerId]?.controllerId !== me) continue
        notice = { key, text: `${e.blockerName} blocks ${e.attackerName} — the damage goes to your creature, not to you.` }
        continue
      default:
        continue
    }
  }
  return notice
}

/**
 * The moves that leave no log entry, read off two consecutive views. The game cannot take
 * priority, a prompt or a combat choice away from the player — only their own action does — so
 * the transition is the proof. Null when nothing between the two views was the player's doing.
 * Call it after {@link noticeFromLog}: a declared attack or block is logged, and the log's line
 * is the better one.
 */
export function noticeFromMove(prev: CoachView, next: CoachView): ActionNotice | null {
  const at = `${next.turnNumber}-${next.step}`
  if (prev.canBlock && !next.canBlock) {
    return { key: `no-blocks-${at}`, text: 'No blocks — the attack comes through to you.' }
  }
  if (prev.canAttack && !next.canAttack) {
    return { key: `no-attack-${at}`, text: 'No attack this turn — your creatures stay home to block.' }
  }
  if (prev.hasDecision && !next.hasDecision) {
    return { key: `answered-${at}`, text: 'Answered.' }
  }
  if (prev.hasPriority && next.stackSize < prev.stackSize) {
    return { key: `resolved-${at}`, text: 'You passed, and the stack resolved — the last spell cast happened first.' }
  }
  if (prev.hasPriority && (next.step !== prev.step || next.turnNumber !== prev.turnNumber)) {
    return { key: `passed-${at}`, text: 'You passed.' }
  }
  return null
}

/**
 * The player is in the middle of something — a spell waiting on its target, creatures picked to
 * attack. Not yet a move, but the tour has no business talking over it.
 */
export function playerIsActing(view: CoachView): boolean {
  return view.isTargeting || view.attackersChosen > 0
}

type Phase = 'beginning' | 'main' | 'combat' | 'end'

/** The five phases behind the thirteen step names the server sends. */
export function phaseOf(step: string): Phase {
  switch (step) {
    case 'UNTAP':
    case 'UPKEEP':
    case 'DRAW':
      return 'beginning'
    case 'BEGIN_COMBAT':
    case 'DECLARE_ATTACKERS':
    case 'DECLARE_BLOCKERS':
    case 'FIRST_STRIKE_COMBAT_DAMAGE':
    case 'COMBAT_DAMAGE':
    case 'END_COMBAT':
      return 'combat'
    case 'END':
    case 'CLEANUP':
      return 'end'
    default:
      return 'main'
  }
}
