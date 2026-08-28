/**
 * The in-game coach for the course's missions: one tip at a time, chosen from what the board is
 * asking of the player right now.
 *
 * Pure: {@link coachTip} takes a small projection of the game state and returns the tip to show,
 * so the rules are testable without a store. A mission can re-word any tip by its key
 * (`Mission.hints`); the React overlay (`LearnCoach.tsx`) does the projecting and the drawing.
 *
 * The coach is armed with a mission id by the brief that starts the game and disarmed when that
 * game ends — it rides sessionStorage because the hand-off into a scenario game is a full
 * navigation (`/?token=…`), which drops React state but not the tab.
 */
import type { MissionId } from './missions'
import { missionById } from './missions'

const COACH_KEY = 'argentum.learn.coach'
const INTRO_KEY = 'argentum.learn.coach.intro'

export function armCoach(mission: MissionId) {
  try {
    sessionStorage.setItem(COACH_KEY, mission)
    sessionStorage.removeItem(INTRO_KEY)
  } catch {
    // Storage unavailable — the game still starts, just without a coach.
  }
}

/**
 * The intro card is shown once per game. It rides sessionStorage rather than component state
 * because the board unmounts and remounts across a reconnect or a resync, and the coach with it.
 */
export function introSeen(): boolean {
  try {
    return sessionStorage.getItem(INTRO_KEY) === '1'
  } catch {
    return false
  }
}

export function markIntroSeen() {
  try {
    sessionStorage.setItem(INTRO_KEY, '1')
  } catch {
    // Then the intro shows again after a remount; nothing worse.
  }
}

export function disarmCoach() {
  try {
    sessionStorage.removeItem(COACH_KEY)
    sessionStorage.removeItem(INTRO_KEY)
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
  /** Some other decision (a target, a "may", a discard) is waiting on the player. */
  hasDecision: boolean
  attackersIncoming: number
  /**
   * What the big button at the bottom right says right now — "Pass", "Pass to Attackers",
   * "End Turn", "Resolve". The server computes it; the coach names it so the tip and the button
   * agree.
   */
  passLabel: string
  isGameOver: boolean
  won: boolean | null
}

export interface CoachTip {
  /** Stable key so the overlay can animate only when the tip actually changes. */
  key: string
  title: string
  body: string
  /** Tone drives the accent colour: what to do now, what is happening, or the end of the game. */
  tone: 'act' | 'watch' | 'done'
}

export function coachTip(view: CoachView, overrides: Partial<Record<string, { title: string; body: string }>> = {}): CoachTip {
  const tip = baseTip(view)
  const override = overrides[tip.key]
  const merged = override ? { ...tip, ...override } : tip
  // `{pass}` in any tip is the button's current label, so "press {pass}" always names the real button.
  return { ...merged, title: merged.title.replaceAll('{pass}', view.passLabel), body: merged.body.replaceAll('{pass}', view.passLabel) }
}

function baseTip(view: CoachView): CoachTip {
  if (view.isGameOver) {
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

  if (view.hasDecision) {
    return {
      key: 'decision',
      title: 'The game is asking you something.',
      body: 'Read the prompt — it might want a target, a choice, or a yes/no. Click a highlighted card to pick it.',
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
      body: 'Drag one of your untapped creatures onto the attacker it should block (or click the creature, then the attacker). A blocked attacker hits your creature instead of you. Then press Confirm Blocks — or No Blocks to take the hit.',
      tone: 'act',
    }
  }

  if (view.canAttack) {
    return {
      key: 'attack',
      title: 'Combat — your creatures can attack.',
      body: 'Click a creature to send it in, then Attack with N — or Attack All. Attackers tap, so think about what you leave back to block on their turn. Skip Attacking is always allowed.',
      tone: 'act',
    }
  }

  if (view.isMyTurn && view.hasPriority) {
    if (view.canPlayLand && view.canCast) {
      return {
        key: 'land-and-cast',
        title: 'Your main phase.',
        body: 'Play a land first — click it in your hand, then Play. Then click a creature or spell you can afford and Cast it; your lands tap for you.',
        tone: 'act',
      }
    }
    if (view.canPlayLand) {
      return {
        key: 'land',
        title: 'Play a land.',
        body: 'One per turn, and it never costs anything. Click a land in your hand, then Play — more lands next turn means bigger spells next turn.',
        tone: 'act',
      }
    }
    if (view.canCast) {
      return {
        key: 'cast',
        title: 'You can cast something.',
        body: 'Cards you can afford light up in your hand. Click one, then Cast. Nothing worth casting? Press {pass} to move on.',
        tone: 'act',
      }
    }
    if (view.step === 'PRECOMBAT_MAIN') {
      return {
        key: 'pass-to-combat',
        title: 'Nothing left to play.',
        body: 'Press {pass}, the blue button at the bottom right. If you have a creature that can attack, the game will stop at combat and ask.',
        tone: 'act',
      }
    }
    return {
      key: 'pass',
      title: 'Nothing to do here.',
      body: 'Press {pass}. The game moves on, and hands the turn over when yours is done.',
      tone: 'act',
    }
  }

  if (!view.isMyTurn && view.hasPriority) {
    return {
      key: 'respond',
      title: 'Their turn — you have a moment to respond.',
      body: view.canCast
        ? 'You can cast an instant right now, before their spell resolves — click it, then Cast. Otherwise, press {pass}.'
        : 'Nothing you can cast at instant speed, so press {pass} and watch what they do.',
      tone: 'watch',
    }
  }

  if (view.turnNumber <= 1 && view.isMyTurn) {
    return {
      key: 'first-turn',
      title: 'This is your first turn.',
      body: 'The strip at the top shows where in the turn you are. You get the board back the moment there is something to decide.',
      tone: 'watch',
    }
  }

  return {
    key: 'waiting',
    title: view.isMyTurn ? 'The game is working through your turn.' : 'The opponent is thinking.',
    body: view.isMyTurn
      ? 'Steps with nothing to decide pass on their own. You get the board back the moment there is something to do.'
      : 'When they attack you will be asked to block. When they cast a spell you may get a chance to respond.',
    tone: 'watch',
  }
}
