/**
 * The Learn to Play course: four short games, each one a card in the hand on `/learn`.
 *
 * Nothing here is read. Every mission is a scripted board sent to the production `/api/scenarios`
 * endpoint with the built-in AI in the other seat, and the teaching happens on the real game
 * board through the coach panel: a handful of objectives that tick off as the player does them,
 * and one line at a time saying what the board is waiting for.
 *
 * The boards are built so the first decision is a real one. A fresh player at a 60-card table
 * spends three turns playing lands and learning patience; here both seats start with lands down
 * and something to do. Libraries are listed top-first (the engine draws `library.first()`), so
 * the opening draws are scripted — a land, then a creature that costs exactly what the lands make,
 * then a trick. Every card is a corpus card using only what the mission has already shown.
 *
 * Audience: has never played Magic. The `/help` guide deliberately assumes you know the game and
 * explains *this app*; this course is the other half.
 */
import type { ScenarioSpec } from '@/components/scenario/types'
import type { ClientCard, ClientGameState } from '@/types/gameState'
import type { EntityId } from '@/types'

export type MissionId = 'first-steps' | 'blocking' | 'instants' | 'real-game'

/**
 * The frame colour each mission card wears — one of Magic's colours, gold for multicolour,
 * silver for the artifact frame. The colour wheel is met through the course before it is named.
 */
export type MissionFrame = 'W' | 'U' | 'B' | 'R' | 'G' | 'gold' | 'artifact'

/** What the coach needs to judge an objective: the whole client state, and who "you" are. */
export interface ObjectiveContext {
  state: ClientGameState
  me: EntityId
  /** Set once the game is over, from the store's game-over state. */
  won: boolean | null
}

export interface Objective {
  id: string
  label: string
  /** Pure: true once the log (or the board) shows it happened. */
  done: (ctx: ObjectiveContext) => boolean
}

/** Mission-specific wording for a coach tip key; the generic tip is used where none is given. */
export interface HintOverride {
  title: string
  body: string
}

export interface Mission {
  id: MissionId
  /** 1-based position in the course; the draw order of the hand. */
  number: number
  title: string
  /** The one-line promise on the mission card. */
  blurb: string
  frame: MissionFrame
  /** Honest playing time. */
  minutes: number
  /** What the brief says you will do — three lines, no more. */
  brief: readonly string[]
  /** What the board shows first, so the brief can put the cards on the table before the game does. */
  openingCards: readonly string[]
  objectives: readonly Objective[]
  /** Shown once, when the board first appears. */
  intro: string
  hints: Partial<Record<string, HintOverride>>
  spec: (playerName: string) => ScenarioSpec
}

/** The name the AI seat plays under. A "tutor" is also what Magic calls a card that finds another. */
export const TUTOR_NAME = 'Tutor'

// ── Objective detectors ─────────────────────────────────────────────────────

function card(ctx: ObjectiveContext, id: EntityId) {
  return ctx.state.cards[id]
}

function log(ctx: ObjectiveContext) {
  return ctx.state.gameLog ?? []
}

/** The server sends card types upper-case (`LAND`); compare case-blind so a casing change cannot silently blind an objective. */
function hasType(card: ClientCard | undefined, type: string): boolean {
  return card !== undefined && card.cardTypes.some((t) => t.toUpperCase() === type)
}

const playedLand: Objective = {
  id: 'land',
  label: 'Play a land',
  done: (ctx) =>
    log(ctx).some((e) => {
      if (e.type !== 'permanentEntered' || e.controllerId !== ctx.me) return false
      return hasType(card(ctx, e.cardId), 'LAND')
    }),
}

const castCreature: Objective = {
  id: 'creature',
  label: 'Cast a creature',
  done: (ctx) =>
    log(ctx).some((e) => {
      if (e.type !== 'spellCast' || e.casterId !== ctx.me) return false
      return hasType(card(ctx, e.spellId), 'CREATURE')
    }),
}

const attacked: Objective = {
  id: 'attack',
  label: 'Attack with a creature',
  done: (ctx) => log(ctx).some((e) => e.type === 'creatureAttacked' && e.attackingPlayerId === ctx.me),
}

const blocked: Objective = {
  id: 'block',
  label: 'Block an attacker',
  done: (ctx) =>
    log(ctx).some((e) => {
      if (e.type !== 'creatureBlocked') return false
      const c = card(ctx, e.blockerId)
      return c !== undefined && c.ownerId === ctx.me
    }),
}

const killedInCombat: Objective = {
  id: 'kill',
  label: 'Kill one of their creatures',
  done: (ctx) =>
    log(ctx).some((e) => {
      if (e.type !== 'creatureDied') return false
      const c = card(ctx, e.creatureId)
      return c !== undefined && c.ownerId !== ctx.me
    }),
}

const castInstant: Objective = {
  id: 'instant',
  label: 'Cast an instant',
  done: (ctx) =>
    log(ctx).some((e) => {
      if (e.type !== 'spellCast' || e.casterId !== ctx.me) return false
      return hasType(card(ctx, e.spellId), 'INSTANT')
    }),
}

const won: Objective = {
  id: 'win',
  label: 'Win the game',
  done: (ctx) => ctx.won === true || (ctx.state.isGameOver && ctx.state.winnerId === ctx.me),
}

// ── Decks ────────────────────────────────────────────────────────────────────

/** Repeat lands between spells so a list reads as a curve, top-first. */
function curve(spells: readonly string[], lands: readonly string[]): string[] {
  const out: string[] = []
  spells.forEach((spell, i) => {
    out.push(lands[i % lands.length]!)
    out.push(spell)
  })
  return out
}

const GREEN_WHITE_SPELLS = [
  'Centaur Courser',
  'Giant Growth',
  'Silvercoat Lion',
  'Trained Armodon',
  'Pacifism',
  'Youthful Knight',
  'Giant Spider',
  'Rumbling Baloth',
  'Titanic Growth',
  'Serra Angel',
  'Divine Verdict',
  'Pillarfield Ox',
  'Craw Wurm',
  'Oakenform',
  'Elvish Warrior',
]

const RED_BLACK_SPELLS = [
  'Gray Ogre',
  'Shock',
  'Walking Corpse',
  'Bloodrock Cyclops',
  'Lightning Strike',
  'Child of Night',
  'Scathe Zombies',
  'Thundering Giant',
  'Doom Blade',
  'Raging Goblin',
  'Mind Rot',
  'Bog Imp',
  'Hill Giant',
  'Volcanic Hammer',
  'Drudge Skeletons',
]

const GW_LANDS = ['Forest', 'Plains']
const RB_LANDS = ['Mountain', 'Swamp']

// ── Missions ────────────────────────────────────────────────────────────────

export const MISSIONS: readonly Mission[] = [
  {
    id: 'first-steps',
    number: 1,
    title: 'First steps',
    blurb: 'Play lands, cast creatures, attack. The opponent will not fight back — this one is yours.',
    frame: 'G',
    minutes: 4,
    brief: [
      'Play one land a turn and tap them for mana — your lands tap on their own when you cast.',
      'Cast creatures. A creature that just arrived has to wait a turn before it can attack.',
      'Attack with what can attack. Get the Tutor from 8 life to 0.',
    ],
    openingCards: ['Forest', 'Grizzly Bears', 'Runeclaw Bear'],
    objectives: [playedLand, castCreature, attacked, won],
    intro:
      'This is the table. Your cards are at the bottom — the hand you can play from — and your lands and creatures go on the battlefield above them. Up top is the Tutor, on 8 life. Bring that to zero.',
    hints: {
      land: {
        title: 'Play a land.',
        body: 'Click the Forest in your hand, then Play Forest. Lands are free, one per turn, and every land you have is one more mana next turn.',
      },
      'land-and-cast': {
        title: 'Land first, then a creature.',
        body: 'Click the Forest in your hand, then Play Forest. Then click Grizzly Bears and Cast it — it costs two mana, and your lands tap for you.',
      },
      cast: {
        title: 'Cast a creature.',
        body: 'The cards you can afford light up. Click Grizzly Bears, then Cast. It lands on the battlefield, but it cannot attack until your next turn — that is summoning sickness.',
      },
      attack: {
        title: 'Attack!',
        body: 'Press Attack All — or click a creature and then Attack with 1. The Tutor has nothing to block with, so every point of power goes straight to their life total.',
      },
      'pass-to-combat': {
        title: 'Nothing left to do — press {pass}.',
        body: 'It is the blue button at the bottom right. The turn moves on; if a creature of yours can attack, the game stops at combat and asks you.',
      },
      pass: {
        title: 'Nothing to do here — press {pass}.',
        body: 'The Tutor takes a turn, then it is back to you with a fresh card drawn. A creature you cast this turn can attack on the next one.',
      },
      waiting: {
        title: 'The Tutor is taking a turn.',
        body: 'They will play a land and pass. Watch the phase strip at the top — it comes back to you soon.',
      },
    },
    spec: (name) => ({
      player1Name: name,
      player2Name: TUTOR_NAME,
      player1: {
        lifeTotal: 20,
        battlefield: [{ name: 'Forest' }, { name: 'Forest' }],
        hand: ['Forest', 'Grizzly Bears', 'Runeclaw Bear'],
        library: curve(['Centaur Courser', 'Elvish Warrior', 'Trained Armodon', 'Rumbling Baloth', 'Craw Wurm'], ['Forest']),
      },
      player2: {
        lifeTotal: 8,
        battlefield: [{ name: 'Mountain' }],
        hand: ['Mountain', 'Mountain'],
        library: ['Mountain', 'Swamp', 'Mountain', 'Swamp', 'Mountain', 'Swamp', 'Mountain', 'Swamp', 'Mountain', 'Swamp'],
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
      priorityPlayer: 1,
      mode: 'AI',
      aiPlayer: 2,
    }),
  },
  {
    id: 'blocking',
    number: 2,
    title: 'Holding the line',
    blurb: 'The Tutor’s Cyclops has to attack every turn. Block well, lose nothing, then turn the game around.',
    frame: 'W',
    minutes: 5,
    brief: [
      'When they attack, each of your untapped creatures can block one attacker.',
      'A blocked attacker hits your creature instead of you. Compare power against toughness before you block.',
      'You start on 14 life with one creature. Their Bloodrock Cyclops must attack every turn — make it pay, then build up and win.',
    ],
    openingCards: ['Giant Spider', 'Centaur Courser', 'Benalish Knight'],
    objectives: [blocked, killedInCombat, won],
    intro:
      'It is the Tutor’s turn, and they have two creatures to your one. Their Bloodrock Cyclops (3/3) is forced to attack every combat. Your Giant Spider is a 2/4 — four toughness means it survives three damage. When they attack, put it in front of the biggest thing coming.',
    hints: {
      block: {
        title: 'Choose your blocks.',
        body: 'Drag Giant Spider onto the Cyclops (or click the Spider, then the Cyclops). It survives 3 damage, so that block costs you nothing; whatever is unblocked hits you. Then press Confirm Blocks.',
      },
      'land-and-cast': {
        title: 'Your turn — build up.',
        body: 'Click a land in your hand and play it, then click a creature and cast it. Centaur Courser is a 3/3: it kills the Ogre and survives, and trades with the Cyclops.',
      },
      attack: {
        title: 'Attack — but count first.',
        body: 'Click the creatures to send in, then Attack with N. Anything that attacks is tapped on their turn and cannot block, so keep the Spider home if you still need a wall. Skip Attacking is fine too.',
      },
      respond: {
        title: 'Their turn.',
        body: 'You have nothing to cast at instant speed, so press {pass}. If they attack, you will be asked to block.',
      },
    },
    spec: (name) => ({
      player1Name: name,
      player2Name: TUTOR_NAME,
      player1: {
        // Two unblocked swings (5 a turn) must not end the mission on the spot: 14 leaves room for one mistake.
        lifeTotal: 14,
        battlefield: [{ name: 'Forest' }, { name: 'Forest' }, { name: 'Plains' }, { name: 'Giant Spider', summoningSickness: false }],
        hand: ['Plains', 'Centaur Courser', 'Benalish Knight'],
        library: curve(['Trained Armodon', 'Silvercoat Lion', 'Rumbling Baloth', 'Pacifism', 'Serra Angel', 'Craw Wurm', 'Pillarfield Ox'], GW_LANDS),
      },
      player2: {
        lifeTotal: 10,
        battlefield: [
          { name: 'Mountain' },
          { name: 'Mountain' },
          { name: 'Swamp' },
          // "Attacks each combat if able" — the lesson's attack is guaranteed, not left to the AI's judgement.
          { name: 'Bloodrock Cyclops', summoningSickness: false },
          { name: 'Gray Ogre', summoningSickness: false },
        ],
        hand: ['Mountain', 'Goblin Piker'],
        library: curve(['Walking Corpse', 'Hill Giant', 'Scathe Zombies', 'Raging Goblin', 'Bog Imp', 'Drudge Skeletons', 'Thundering Giant'], RB_LANDS),
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 2,
      priorityPlayer: 2,
      mode: 'AI',
      aiPlayer: 2,
    }),
  },
  {
    id: 'instants',
    number: 3,
    title: 'In response',
    blurb: 'The Tutor has burn spells. You have Giant Growth. Learn why the last spell cast happens first.',
    frame: 'U',
    minutes: 5,
    brief: [
      'Instants can be cast at almost any time — including while an opponent’s spell is waiting to resolve.',
      'Spells go on the stack and resolve top-down: your response happens before the spell it answers.',
      'When Lightning Bolt targets your bear, save it with Giant Growth. Then win.',
    ],
    openingCards: ['Grizzly Bears', 'Giant Growth', 'Centaur Courser'],
    objectives: [castInstant, attacked, won],
    intro:
      'You have Grizzly Bears on the battlefield and Giant Growth in hand — an instant that gives +3/+3 until end of turn. It is the Tutor’s turn, and they have three Mountains. When a spell of theirs appears in the middle of the table, that is the stack, and the game will stop to ask if you want to respond.',
    hints: {
      respond: {
        title: 'Respond!',
        body: 'Their spell is on the stack and has not happened yet. Click Giant Growth in your hand, Cast it, and pick Grizzly Bears as the target. Yours resolves first, because it was cast last.',
      },
      'land-and-cast': {
        title: 'Your turn.',
        body: 'Play the Forest, then cast Centaur Courser. Keep Giant Growth in hand when you can — a trick you have not cast is one your opponent has to fear.',
      },
      attack: {
        title: 'Attack.',
        body: 'Click a creature, then Attack with N. Even when they have a blocker, an instant in hand turns a bad fight into a good one — cast it after blocks are declared.',
      },
    },
    spec: (name) => ({
      player1Name: name,
      player2Name: TUTOR_NAME,
      player1: {
        lifeTotal: 12,
        battlefield: [{ name: 'Forest' }, { name: 'Forest' }, { name: 'Forest' }, { name: 'Grizzly Bears', summoningSickness: false }],
        hand: ['Giant Growth', 'Forest', 'Centaur Courser'],
        library: curve(['Titanic Growth', 'Runeclaw Bear', 'Giant Growth', 'Trained Armodon', 'Giant Spider', 'Rumbling Baloth', 'Craw Wurm'], ['Forest']),
      },
      player2: {
        lifeTotal: 8,
        battlefield: [{ name: 'Mountain' }, { name: 'Mountain' }, { name: 'Mountain' }],
        hand: ['Lightning Bolt', 'Shock', 'Mountain'],
        library: curve(['Goblin Piker', 'Volcanic Hammer', 'Gray Ogre', 'Lightning Strike', 'Hill Giant', 'Raging Goblin', 'Bloodrock Cyclops'], ['Mountain']),
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 2,
      priorityPlayer: 2,
      mode: 'AI',
      aiPlayer: 2,
    }),
  },
  {
    id: 'real-game',
    number: 4,
    title: 'A real game',
    blurb: 'Twenty life each, full decks, everything at once. The coach stays — but the game is yours.',
    frame: 'artifact',
    minutes: 10,
    brief: [
      'Green-white creatures and tricks against red-black goblins, giants and burn.',
      'Play a land every turn, spend your mana, and count before you attack or block.',
      'Win or lose, finishing this game finishes the course.',
    ],
    openingCards: ['Grizzly Bears', 'Benalish Knight', 'Runeclaw Bear'],
    objectives: [playedLand, castCreature, attacked, won],
    intro:
      'A real game: twenty life each, a creature apiece already on the board, and a full deck to draw from. The coach keeps saying what the board is waiting for; the decisions are yours.',
    hints: {},
    spec: (name) => ({
      player1Name: name,
      player2Name: TUTOR_NAME,
      player1: {
        lifeTotal: 20,
        battlefield: [{ name: 'Forest' }, { name: 'Forest' }, { name: 'Plains' }, { name: 'Grizzly Bears', summoningSickness: false }],
        hand: ['Forest', 'Benalish Knight', 'Runeclaw Bear', 'Plains'],
        library: curve(GREEN_WHITE_SPELLS, GW_LANDS),
      },
      player2: {
        lifeTotal: 20,
        battlefield: [{ name: 'Mountain' }, { name: 'Swamp' }, { name: 'Mountain' }, { name: 'Goblin Piker', summoningSickness: false }],
        hand: ['Swamp', 'Hill Giant', 'Typhoid Rats', 'Mountain'],
        library: curve(RED_BLACK_SPELLS, RB_LANDS),
      },
      phase: 'PRECOMBAT_MAIN',
      activePlayer: 1,
      priorityPlayer: 1,
      mode: 'AI',
      aiPlayer: 2,
    }),
  },
]

export const MISSION_IDS: readonly MissionId[] = MISSIONS.map((m) => m.id)

export function missionById(id: string | undefined | null): Mission | undefined {
  return MISSIONS.find((m) => m.id === id)
}

export function nextMission(id: MissionId): Mission | undefined {
  const index = MISSIONS.findIndex((m) => m.id === id)
  return index >= 0 ? MISSIONS[index + 1] : undefined
}

export function learnHref(id?: MissionId): string {
  return id ? `/learn/${id}` : '/learn'
}

/** Playing time for the whole course — what the home page promises. */
export const COURSE_MINUTES = MISSIONS.reduce((sum, m) => sum + m.minutes, 0)

/** Every card name any mission can put in play — what the catalog test checks. */
export function missionCardNames(): readonly string[] {
  const names = new Set<string>()
  for (const mission of MISSIONS) {
    const spec = mission.spec('x')
    for (const seat of [spec.player1, spec.player2]) {
      seat?.battlefield?.forEach((c) => names.add(c.name))
      seat?.hand?.forEach((n) => names.add(n))
      seat?.library?.forEach((n) => names.add(n))
    }
  }
  return [...names]
}
