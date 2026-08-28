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
import type { SpotId } from './spots'

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
  title?: string
  body?: string
  /** The board element to ring while the tip is up — see `learn/spots.ts`. */
  spot?: SpotId
}

/** A card the brief shows before the game does, with one plain line on what it is for. */
export interface OpeningCard {
  name: string
  note: string
}

/**
 * One step of the table tour the coach walks through when the board first appears: a spot on the
 * board to ring, and two sentences about it. Gesture words (`{read}`, `{click}`) and `{pass}` are
 * substituted for this device and the real button — see `wordTip` in `learn/coach.ts`.
 */
export interface TourStep {
  spot: SpotId
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
  openingCards: readonly OpeningCard[]
  objectives: readonly Objective[]
  /** Walked through once, when the board first appears — each step rings one part of the table. */
  tour: readonly TourStep[]
  /** What the closing card says you now know — two or three lines, game and app together. */
  lessons: readonly string[]
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
      'Play one land a turn. Lands make mana, and your lands tap for you when you cast something.',
      'Cast creatures. A creature that just arrived has to wait a turn before it can attack.',
      'Attack with what can attack. Get the Tutor from 8 life to 0.',
    ],
    openingCards: [
      { name: 'Forest', note: 'A land. Free to play, one per turn; tap it for one green mana.' },
      { name: 'Grizzly Bears', note: 'A creature. Costs two mana; hits for 2 and takes 2.' },
      { name: 'Runeclaw Bear', note: 'The same bear in a different coat. Two of them is 4 damage a turn.' },
    ],
    objectives: [playedLand, castCreature, attacked, won],
    tour: [
      {
        spot: 'hand',
        title: 'Your hand.',
        body: 'The cards you can play from. Cards you can afford glow. {read} to read it in full — that works anywhere on the table.',
      },
      {
        spot: 'battlefield',
        title: 'Your battlefield.',
        body: 'Where your lands and creatures live once played. Two Forests are already down. The Tutor’s side is the mirror image above.',
      },
      {
        spot: 'phase-strip',
        title: 'The turn strip.',
        body: 'A turn is a row of steps; the lit dot is where you are. Your turn, then theirs, then yours again.',
      },
      {
        spot: 'pass',
        title: 'The big button.',
        body: 'When you are done, press it. It always says where it takes you next — right now, {pass}.',
      },
    ],
    lessons: [
      'Lands make mana; one a turn. Creatures cost mana and wait a turn before attacking.',
      'Drag a card onto the battlefield to play it, or {click-lower} it and choose. {read} to read it.',
      'The blue button moves the game on and names the next stop.',
    ],
    hints: {
      land: {
        title: 'Play a land.',
        body: 'Drag the Forest from your hand up onto the battlefield — or {click-lower} it and choose Play Forest. Lands are free, one per turn, and every land is one more mana each turn.',
      },
      'land-and-cast': {
        title: 'Land first, then a creature.',
        body: 'Drag the Forest up onto the battlefield, or {click-lower} it and choose Play Forest. Then do the same with Grizzly Bears — it costs two mana, and your lands tap for you.',
      },
      cast: {
        title: 'Cast a creature.',
        body: 'The cards you can afford glow. Drag Grizzly Bears onto the battlefield, or {click-lower} it and choose Cast. It lands but cannot attack until your next turn — that is summoning sickness.',
      },
      attack: {
        title: 'Attack!',
        body: 'Press Attack All — or {click-lower} a creature and then Attack with 1. The Tutor has nothing to block with, so every point of power comes straight off their life.',
      },
      'pass-to-combat': {
        title: 'Nothing left to do — press {pass}.',
        body: 'The blue button at the bottom right. The turn moves on; if a creature of yours can attack, the game stops at combat and asks you.',
      },
      pass: {
        title: 'Nothing to do here — press {pass}.',
        body: 'The Tutor takes a turn, then it is back to you with a fresh card drawn. A creature you cast this turn can attack on the next one.',
      },
      waiting: {
        title: 'The Tutor is taking a turn.',
        body: 'They will play a land and pass. Watch the strip at the top — the lit dot walks through their turn and comes back to you.',
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
    openingCards: [
      { name: 'Giant Spider', note: 'On the battlefield already. 2 power, 4 toughness: it blocks a 3/3 and lives.' },
      { name: 'Centaur Courser', note: 'A 3/3 for three mana. Kills their Ogre in a fight and survives.' },
      { name: 'Benalish Knight', note: 'Flash: an instant-speed creature. Cast it on their turn and surprise-block.' },
    ],
    objectives: [blocked, killedInCombat, won],
    tour: [
      {
        spot: 'opponent-battlefield',
        title: 'Their side.',
        body: 'Two creatures to your one. The Bloodrock Cyclops (3/3) has to attack every combat — {read} to see it written on the card.',
      },
      {
        spot: 'battlefield',
        title: 'Your Giant Spider.',
        body: 'The numbers in the corner are power / toughness: 2/4. Four toughness means it survives three damage, so the Cyclops cannot kill it.',
      },
      {
        spot: 'log',
        title: 'The log.',
        body: 'Everything that happens is written down here. Combat goes by quickly; when you wonder what just hit you, open the log.',
      },
    ],
    lessons: [
      'Block by dragging your creature onto the attacker. An untapped creature can block one attacker.',
      'Power is what a creature deals, toughness is what it survives. A blocker with more toughness than their power walks away.',
      'Attacking taps a creature, so it cannot block on their turn. Keep a wall home when you need one.',
    ],
    hints: {
      block: {
        title: 'Choose your blocks.',
        body: 'Drag Giant Spider onto the Cyclops — an arrow appears. It survives 3 damage, so that block costs you nothing; whatever is unblocked hits you. Then press Confirm Blocks.',
        spot: 'battlefield',
      },
      'land-and-cast': {
        title: 'Your turn — build up.',
        body: 'Play a land, then a creature — drag them up, or {click-lower} and choose. Centaur Courser is a 3/3: it kills the Ogre and survives, and trades with the Cyclops.',
      },
      attack: {
        title: 'Attack — but count first.',
        body: '{click} the creatures to send in, then Attack with N. Anything that attacks is tapped on their turn and cannot block, so keep the Spider home if you still need a wall. Skip Attacking is fine too.',
      },
      respond: {
        title: 'Their spell is on the stack.',
        body: 'Benalish Knight has flash — it can be cast on their turn, like an instant. Cast it now and you have a second blocker before they attack; or press {pass} and keep it up your sleeve.',
      },
      'respond-window': {
        title: 'Their turn.',
        body: 'Benalish Knight has flash, so you may cast it now — otherwise press {pass}. If they attack, you will be asked to block.',
      },
      'respond-attack': {
        title: 'The Cyclops is coming.',
        body: 'Attackers are declared; blocks come next. Benalish Knight cast now can block this very attack — first strike means it deals its damage before the creature it blocks. Or press {pass} and block with the Spider alone.',
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
    openingCards: [
      { name: 'Grizzly Bears', note: 'Already on the battlefield, and about to be shot at.' },
      { name: 'Giant Growth', note: 'An instant: +3/+3 until end of turn. Cast it in response and the bear survives the Bolt.' },
      { name: 'Centaur Courser', note: 'Your next creature, once you have a third Forest and a quiet moment.' },
    ],
    objectives: [castInstant, attacked, won],
    tour: [
      {
        spot: 'stack',
        title: 'The stack.',
        body: 'When anyone casts a spell it appears here first, and nothing happens until everybody passes. That gap is your window to respond.',
      },
      {
        spot: 'priority-mode',
        title: 'Auto.',
        body: 'This switch decides how often the game stops to ask you. On Auto it stops whenever you hold an instant you can cast — so the Bolt will wait for you.',
      },
      {
        spot: 'hand',
        title: 'Giant Growth.',
        body: 'An instant: +3/+3 until end of turn. When Lightning Bolt shows up on the stack aimed at your bear, cast this at the bear.',
      },
    ],
    lessons: [
      'A spell waits on the stack until everyone passes. The last spell cast resolves first.',
      'An instant can be cast on anyone’s turn — you get a window whenever the game stops for you.',
      'The Auto switch controls when the game stops to ask; it stops on its own when you hold an instant.',
    ],
    hints: {
      respond: {
        title: 'Respond!',
        body: 'Their spell is on the stack and has not happened yet. Drag Giant Growth out — or {click-lower} it and choose Cast — and pick Grizzly Bears as the target. Yours resolves first, because it was cast last.',
        spot: 'hand',
      },
      target: {
        title: 'Aim it at the bear.',
        body: 'Giant Growth needs a creature to grow. Grizzly Bears is highlighted — {click-lower} it, then Confirm. It becomes 5/5 until end of turn, and 3 damage no longer kills it.',
      },
      'land-and-cast': {
        title: 'Your turn.',
        body: 'Play the Forest, then cast Centaur Courser. Keep Giant Growth in hand when you can — a trick you have not cast is one your opponent has to fear.',
      },
      attack: {
        title: 'Attack.',
        body: '{click} a creature, then Attack with N. Even when they have a blocker, an instant in hand turns a bad fight into a good one — cast it after blocks are declared.',
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
        // Bolt is the only burn in hand: given Shock as well, the AI opens with the cheaper one
        // and the brief's "when Lightning Bolt targets your bear" names a card that never shows.
        hand: ['Lightning Bolt', 'Mountain'],
        library: curve(['Shock', 'Goblin Piker', 'Volcanic Hammer', 'Gray Ogre', 'Lightning Strike', 'Hill Giant', 'Raging Goblin', 'Bloodrock Cyclops'], ['Mountain']),
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
    openingCards: [
      { name: 'Grizzly Bears', note: 'On the battlefield from the start, ready to attack.' },
      { name: 'Benalish Knight', note: 'A 2/2 with first strike and flash — it deals its damage before the other creature does.' },
      { name: 'Runeclaw Bear', note: 'A second bear for the second turn.' },
    ],
    objectives: [playedLand, castCreature, attacked, won],
    tour: [
      {
        spot: 'controls',
        title: 'The controls.',
        body: 'Undo takes back a tap or a cast the game can still rewind. The land icon switches to choosing your own lands to tap. ? Help explains everything else.',
      },
      {
        spot: 'piles',
        title: 'Your piles.',
        body: 'Deck, graveyard and exile. {click} a pile to browse what is in it — your graveyard is where creatures go when they die.',
      },
      {
        spot: 'opponent-life',
        title: 'Twenty life.',
        body: 'Life totals sit either side of the turn strip. Zero loses. The coach keeps saying what the board is waiting for; the decisions are yours.',
      },
    ],
    lessons: [
      'A full game: play a land every turn, spend your mana, count before you attack or block.',
      'Undo, manual tapping and ? Help are in the bottom-right corner; the piles open when clicked.',
      'You have played Magic. The PLAY menu has quick games against the AI, drafts and other people.',
    ],
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
