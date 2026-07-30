/**
 * The three axes, as the lobby's primary controls.
 *
 * Every lobby shows all three rows and every value of each, whichever server implementation is
 * backing it. What differs is what a value *costs* — `axisChoices.ts` decides that, and this file
 * only renders the answer:
 *
 * - selectable → a normal button
 * - selectable but only on the other lobby kind → a button marked `⇄`, which asks for confirmation
 *   before tearing this lobby down (plan § 4b v1)
 * - not implemented anywhere yet → **disabled with the reason attached**, not hidden
 *
 * Sub-options hang off their own axis only: deck legality, sealed shape and draft shape are
 * indented rows directly under **Cards**, never a peer row. That rule is what stopped "Format"
 * from meaning two different things again.
 */
import { useEffect } from 'react'
import { SettingsLabel } from '../ui/SettingsLabel'
import {
  COMMANDER_LIMITED_HAS_NO_AI,
  COMMANDER_NEEDS_ITS_OWN_LIFE_TOTAL,
  isCommanderDeckFormat,
  legalityOptionsForTable,
  cardsKindTopicId,
  cardsLabel,
  cardsSeatCap,
  isCommanderLimited,
  eventTopicId,
  tableTopicId,
  type CardsAxis,
  type CardsKind,
  type EventAxis,
  type TableAxis,
} from './axes'
import {
  RECREATE_NOTE,
  cardsChoices,
  eventChoices,
  recreateTargetLabel,
  tableChoices,
  type AxisChoice,
  type RecreateSpec,
} from './axisChoices'
import type { UnifiedLobbyView } from './lobbyViewModel'
import type { LobbyCommands } from './useLobbyCommands'
import styles from '../ui/GameUI.module.css'

const CARDS_CAPTIONS: Record<CardsKind, string> = {
  BRING_A_DECK: 'Everyone plays a deck they already built — saved, pasted or imported.',
  RANDOM:
    'The server rolls you a pool, so there is nothing to prepare. This one is per player: your opponent can still bring a deck of their own.',
  MOMIR:
    'No deckbuilding — everyone runs 60 basics. Discard a card and pay {X} to flip a random creature with mana value X.',
  SEALED: 'Open boosters and build a deck from what you get.',
  DRAFT: 'Pass packs around and pick one card at a time, then build from your picks.',
}

const TABLE_CAPTIONS: Record<TableAxis, string> = {
  ONE_V_ONE: 'Two players per game. In a bracket, everyone plays everyone; most match wins takes it.',
  FREE_FOR_ALL: 'One game, everyone at the same table (2-6 players). Last player standing wins.',
  TWO_HEADED_GIANT:
    'Four players in two teams of two. Each team shares one 30-life total, takes turns together, and attacks and blocks as one. Last team standing wins.',
  TEAM_VS_TEAM:
    'An even pod (4/6/8) split into two teams — 2v2, 3v3, or 4v4. Each player keeps their own 20 life and their own turn; players are knocked out one at a time. The last team with anyone standing wins.',
}

export function LobbyAxes({
  view,
  commands,
  onRecreate,
}: {
  view: UnifiedLobbyView
  commands: LobbyCommands
  /** A value that lives on the other lobby kind — confirmed by the screen before it happens. */
  onRecreate: (spec: RecreateSpec) => void
}) {
  const cards = view.axes.cards
  const legalityOptions = legalityOptionsForTable(view.axes.table)

  useEffect(() => {
    if (
      cards.kind === 'BRING_A_DECK' &&
      view.axes.table === 'TWO_HEADED_GIANT' &&
      isCommanderDeckFormat(cards.legality)
    ) {
      commands.setLegality(null)
    }
  }, [cards, commands, view.axes.table])

  return (
    <>
      {/* ── Cards: where the deck comes from. ── */}
      <div className={styles.settingsRow}>
        <SettingsLabel topicId={cardsKindTopicId(cards.kind)}>Cards</SettingsLabel>
        <div className={styles.variantGroup}>
          <AxisButtons
            choices={cardsChoices(view)}
            onPick={commands.setCards}
            onRecreate={onRecreate}
          />
          <div className={styles.variantCaption}>{CARDS_CAPTIONS[cards.kind]}</div>
        </div>
      </div>

      {/* Cards → Bring a deck: which constructed format submitted decks must be legal in. */}
      {cards.kind === 'BRING_A_DECK' && (
        <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
          <span className={styles.settingsLabel}>Deck legality</span>
          <select
            value={cards.legality ?? ''}
            onChange={(e) => commands.setLegality((e.target.value || null) as never)}
            className={styles.settingsSelect}
            title="Restrict submitted decks to a constructed format. No restriction = anything the engine implements."
          >
            <option value="">No restriction</option>
            {legalityOptions.map((f) => (
              <option key={f.value} value={f.value}>{f.label}</option>
            ))}
          </select>
        </div>
      )}

      {/* Cards → Sealed: which sealed shape. */}
      {cards.kind === 'SEALED' && (
        <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
          <span className={styles.settingsLabel}>Sealed shape</span>
          <div className={styles.variantGroup}>
            <div className={styles.settingsButtons}>
              <ShapeButton
                label="Standard"
                active={cards.shape === 'STANDARD'}
                onClick={() => commands.setCardsShape('SEALED')}
              />
              <ShapeButton
                label="Commander"
                active={cards.shape === 'COMMANDER'}
                blocked={shapeBlock(view, { kind: 'SEALED', shape: 'COMMANDER' })}
                onClick={() => commands.setCardsShape('COMMANDER_SEALED')}
              />
            </div>
            <div className={styles.variantCaption}>
              {cards.shape === 'COMMANDER'
                ? 'Open Commander-shaped packs and build a 60-card deck around a commander from your pool. Up to 8 players, playing a 1v1 bracket or one pod at 40 life.'
                : 'Open 6 boosters and build a 40-card deck.'}
            </div>
          </div>
        </div>
      )}

      {/* Cards → Draft: which of the four draft shapes. */}
      {cards.kind === 'DRAFT' && (
        <div className={`${styles.settingsRow} ${styles.settingsRowSub}`}>
          <span className={styles.settingsLabel}>Draft shape</span>
          <div className={styles.variantGroup}>
            <div className={styles.settingsButtons}>
              <ShapeButton
                label="Booster"
                draft
                active={cards.shape === 'BOOSTER'}
                blocked={shapeBlock(view, { kind: 'DRAFT', shape: 'BOOSTER' })}
                onClick={() => commands.setCardsShape('DRAFT')}
              />
              <ShapeButton
                label="Winston"
                draft
                active={cards.shape === 'WINSTON'}
                blocked={shapeBlock(view, { kind: 'DRAFT', shape: 'WINSTON' })}
                onClick={() => commands.setCardsShape('WINSTON_DRAFT')}
              />
              <ShapeButton
                label="Grid"
                draft
                active={cards.shape === 'GRID'}
                blocked={shapeBlock(view, { kind: 'DRAFT', shape: 'GRID' })}
                onClick={() => commands.setCardsShape('GRID_DRAFT')}
              />
              <ShapeButton
                label="Commander"
                draft
                active={cards.shape === 'COMMANDER'}
                blocked={shapeBlock(view, { kind: 'DRAFT', shape: 'COMMANDER' })}
                onClick={() => commands.setCardsShape('COMMANDER_DRAFT')}
              />
            </div>
            <div className={styles.variantCaption}>
              {cards.shape === 'COMMANDER'
                ? 'Commander-shaped 20-card packs; pick a commander from your pool. Up to 8 drafters, playing a 1v1 bracket or one pod at 40 life.'
                : cards.shape === 'WINSTON' ? 'Pick from 3 face-down piles. 2 players.'
                : cards.shape === 'GRID' ? 'Pick a row or column from a 3×3 grid. 2-4 players.'
                : 'Pass packs around the table. 3-8 players.'}
            </div>
          </div>
        </div>
      )}

      {/* ── Table: who is at it. ── */}
      <div className={styles.settingsRow}>
        <SettingsLabel topicId={tableTopicId(view.axes.table)}>Table</SettingsLabel>
        <div className={styles.variantGroup}>
          <AxisButtons
            choices={tableChoices(view)}
            onPick={commands.setTable}
            onRecreate={onRecreate}
          />
          <div className={styles.variantCaption}>{TABLE_CAPTIONS[view.axes.table]}</div>
        </div>
      </div>

      {/* ── Event: one game, or a series. ── */}
      <div className={styles.settingsRow}>
        <SettingsLabel topicId={eventTopicId(view.axes.event)}>Event</SettingsLabel>
        <div className={styles.variantGroup}>
          <AxisButtons
            choices={eventChoices(view)}
            // Event has no directly-settable second value on either kind: server-side
            // `gameMode = TOURNAMENT` *is* the bracket. Every cross-value pick recreates.
            onPick={() => {}}
            onRecreate={onRecreate}
          />
          <div className={styles.variantCaption}>{eventCaption(view)}</div>
        </div>
      </div>
    </>
  )
}

/** The caption under Event: what the value you *didn't* pick would take. */
function eventCaption(view: UnifiedLobbyView): string {
  const other = eventChoices(view).find((c) => !c.selected)
  if (!other) return ''
  switch (other.availability.kind) {
    case 'BLOCKED':
      return other.availability.reason
    case 'RECREATE':
      return `“${other.label}” runs on a different lobby — picking it starts a fresh one.`
    case 'DIRECT':
      return ''
  }
}

function AxisButtons<V extends CardsKind | TableAxis | EventAxis>({
  choices,
  onPick,
  onRecreate,
}: {
  choices: AxisChoice<V>[]
  onPick: (value: V) => void
  onRecreate: (spec: RecreateSpec) => void
}) {
  return (
    <div className={styles.settingsButtons}>
      {choices.map((choice) => {
        const a = choice.availability
        const recreates = a.kind === 'RECREATE'
        return (
          <button
            key={choice.value}
            type="button"
            disabled={a.kind === 'BLOCKED'}
            aria-pressed={choice.selected}
            onClick={() => {
              if (a.kind === 'RECREATE') onRecreate(a.spec)
              else if (a.kind === 'DIRECT' && !choice.selected) onPick(choice.value)
            }}
            className={[
              styles.settingsButton,
              choice.selected ? styles.settingsButtonActive : '',
              recreates ? styles.settingsButtonRecreate : '',
            ].filter(Boolean).join(' ')}
            title={
              a.kind === 'BLOCKED' ? a.reason
                : a.kind === 'RECREATE'
                  ? `Starts a new lobby: ${recreateTargetLabel(a.spec)}. ${RECREATE_NOTE[a.spec.to]} Your invite code will change.`
                  : ''
            }
            data-testid={`axis-choice-${choice.value.toLowerCase().replace(/_/g, '-')}`}
          >
            {choice.label}
            {recreates && <span className={styles.settingsButtonRecreateMark} aria-hidden> ⇄</span>}
          </button>
        )
      })}
    </div>
  )
}

/**
 * Why a Cards sub-shape can't be picked here, or null.
 *
 * Two reasons, both facts shared with the landing wizard rather than numbers written at the call
 * site: the shape seats fewer players than this lobby is holding ({@link cardsSeatCap}), or it is a
 * Commander shape at a Two-Headed Giant table, whose shared life total contradicts Commander's
 * per-player 40. Note the second is about the *table*, not the seat count — Free-for-All and Team
 * vs. Team pods play Commander, and a bracket plays a shared pool out as 1v1 matches.
 */
function shapeBlock(view: UnifiedLobbyView, cards: CardsAxis): string | null {
  const cap = cardsSeatCap(cards)
  if (view.players.length > cap) {
    return `${cardsLabel(cards)} seats at most ${cap} — this lobby has ${view.players.length}`
  }
  if (isCommanderLimited(cards) && view.axes.table === 'TWO_HEADED_GIANT') {
    return COMMANDER_NEEDS_ITS_OWN_LIFE_TOTAL
  }
  if (isCommanderLimited(cards) && view.players.some((p) => p.isAi)) {
    return COMMANDER_LIMITED_HAS_NO_AI
  }
  return null
}

function ShapeButton({
  label,
  active,
  blocked = null,
  draft = false,
  onClick,
}: {
  label: string
  active: boolean
  blocked?: string | null
  draft?: boolean
  onClick: () => void
}) {
  return (
    <button
      type="button"
      disabled={blocked !== null}
      aria-pressed={active}
      onClick={() => { if (blocked === null && !active) onClick() }}
      className={[
        styles.settingsButton,
        active ? styles.settingsButtonActive : '',
        active && draft ? styles.settingsButtonDraft : '',
      ].filter(Boolean).join(' ')}
      title={blocked ?? ''}
    >
      {label}
    </button>
  )
}
