/**
 * The knobs that only a tournament-backed lobby has.
 *
 * `LobbyScreen` owns everything both lobby kinds share — the axes, visibility, ranked, the player
 * list, the actions row. What's left is genuinely specific to the tournament implementation: a
 * card pool has sets, boosters and a ban list; a draft has timers; a team table has team setup.
 * Keeping them here rather than behind `view.kind === 'TOURNAMENT'` checks scattered through the
 * screen is what keeps the screen readable as one thing.
 *
 * Everything in this file is a faithful move out of the old `LobbyOverlay`, except that the rows
 * are ordered by what they belong to rather than by the order they were added.
 */
import { useState } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { LobbyState } from '@/store/slices/types'
import { teamColor } from '@/styles/seatColors'
import { BanListEditor } from '../ui/BanListEditor'
import { CubePanel } from './CubePanel'
import { SetIcon } from '../ui/SetIcon'
import { SetPickerModal } from '../ui/SetPickerModal'
import { SettingsLabel } from '../ui/SettingsLabel'
import { cardsSeatCap } from './axes'
import type { UnifiedLobbyView } from './lobbyViewModel'
import styles from '../ui/GameUI.module.css'

/**
 * Sentinel set code for a deferred "Random Set" pick. The concrete set stays hidden (shown as
 * "Random Set") until the server rolls it at game start — mirrors `TournamentLobby.RANDOM_SET_CODE`.
 * Multiple random slots use suffixed codes (RANDOM, RANDOM-2, …).
 */
const RANDOM_SET_CODE = 'RANDOM'
const isRandomSetCode = (code: string): boolean =>
  code === RANDOM_SET_CODE || code.startsWith(`${RANDOM_SET_CODE}-`)

/**
 * Seat counts this lobby could be set to, from the same seat facts the landing wizard uses.
 *
 * A single option (or none) means the shape decides it: Two-Headed Giant is exactly four, and the
 * two-player sub-shapes cap themselves. Team vs. Team needs an even pod. Never offers fewer seats
 * than the lobby is already holding.
 */
function seatOptions(view: UnifiedLobbyView): number[] {
  const cap = Math.min(
    cardsSeatCap(view.axes.cards),
    view.axes.table === 'FREE_FOR_ALL' ? 6 : 8,
  )
  if (view.axes.table === 'TWO_HEADED_GIANT') return []
  const floor = Math.max(2, view.players.length)
  const all = view.axes.table === 'TEAM_VS_TEAM' ? [4, 6, 8] : [2, 3, 4, 5, 6, 7, 8]
  return all.filter((n) => n <= cap && n >= floor)
}

export function TournamentLobbySettings({
  view,
  lobbyState,
}: {
  view: UnifiedLobbyView
  lobbyState: LobbyState
}) {
  const updateLobbySettings = useGameStore((s) => s.updateLobbySettings)
  const [showSetPicker, setShowSetPicker] = useState(false)

  const s = lobbyState.settings
  const format = s.format
  const isSealed = format === 'SEALED'
  const isCommanderSealed = format === 'COMMANDER_SEALED'
  const isDraft = format === 'DRAFT'
  const isWinston = format === 'WINSTON_DRAFT'
  const isGridDraft = format === 'GRID_DRAFT'
  const isCommanderDraft = format === 'COMMANDER_DRAFT'
  const isPremade = format === 'PREMADE_DECKS'
  const isAnyDraft = isDraft || isWinston || isGridDraft || isCommanderDraft
  const isAnyCommander = isCommanderDraft || isCommanderSealed
  const isFfa = s.gameMode === 'FREE_FOR_ALL'
  const isCube = Boolean(s.cubeName)
  // Pool Play hands out the whole cube instead of dealing packs, so the pack-count controls are
  // meaningless while it's on. Matches TournamentLobby.isCubePoolPlay.
  const isPoolPlay = isCube && isSealed && Boolean(s.cubePoolPlay)

  const allSets = s.availableSets
  // A selected-set chip is either a concrete set or a deferred "Random Set" placeholder.
  type SelectedSetChip = { code: string; name: string; partial: boolean; extensionSet: boolean; random: boolean }
  const selectedSets: SelectedSetChip[] = s.setCodes
    .map((code): SelectedSetChip | null => {
      if (isRandomSetCode(code)) return { code, name: 'Random Set', partial: false, extensionSet: false, random: true }
      const set = allSets.find((x) => x.code === code)
      return set
        ? { code, name: set.name, partial: set.partial ?? false, extensionSet: set.extensionSet ?? false, random: false }
        : null
    })
    .filter((x): x is SelectedSetChip => x != null)

  const toggleSet = (code: string) => {
    const next = s.setCodes.includes(code)
      ? s.setCodes.filter((c) => c !== code)
      : [...s.setCodes, code]
    updateLobbySettings({ setCodes: next })
  }

  // "Random Set" in the picker: a deferred slot the server rolls to a complete, non-extension set
  // at game start. Suffixed codes keep multiple random slots distinct.
  const addRandomSet = () => {
    const existing = s.setCodes.filter(isRandomSetCode).length
    const code = existing === 0 ? RANDOM_SET_CODE : `${RANDOM_SET_CODE}-${existing + 1}`
    updateLobbySettings({ setCodes: [...s.setCodes, code] })
  }

  const hasSelectedSets = s.setCodes.length > 0
  const hasBaseSet = s.setCodes.some((code) => !allSets.find((a) => a.code === code)?.extensionSet)
  const perSetCounts = s.setCodes.length > 1 && !s.chaosBoosters
  // Booster Draft and Commander Draft count *packs*, capped at 6. Sealed and Winston count
  // boosters, capped at 16 — Winston hands out a shared pile, so it sits with sealed here even
  // though it is a draft everywhere else.
  const countsPacks = isDraft || isCommanderDraft
  const boosterCap = countsPacks ? 6 : 16

  return (
    <>
      {/* Team setup (2HG — CR 810; Team vs. Team — CR 808). */}
      {view.teams.mode !== 'NONE' && (
        <div className={styles.settingsRow}>
          <SettingsLabel topicId="table-two-headed-giant">Teams</SettingsLabel>
          <div className={styles.variantGroup}>
            <div className={styles.settingsButtons}>
              <button
                onClick={() => updateLobbySettings({ randomTeams: true })}
                className={`${styles.settingsButton} ${view.teams.mode === 'RANDOM' ? styles.settingsButtonActive : ''}`}
                title="Shuffle the players into two even teams when the game starts (re-rolled each game)"
              >
                Random
              </button>
              <button
                onClick={() => updateLobbySettings({ randomTeams: false })}
                className={`${styles.settingsButton} ${view.teams.mode === 'MANUAL' ? styles.settingsButtonActive : ''}`}
                title="Set the teams by hand — click each player's team chip below"
              >
                Choose teams
              </button>
            </div>
            <div className={styles.variantCaption}>
              {view.teams.mode === 'RANDOM'
                ? 'Teams are randomised at game start, fresh every game.'
                : view.teams.balanced
                  ? 'Click a player’s team chip below to move them between teams.'
                  : `Click each player’s team chip below — each team needs exactly ${view.teams.size} player${view.teams.size === 1 ? '' : 's'}.`}
            </div>
          </div>
        </div>
      )}

      {/* Free-for-All attack rule (CR 802/803) — only relevant once 3+ players share one table. */}
      {isFfa && (
        <div className={styles.settingsRow}>
          <SettingsLabel topicId="table-free-for-all">Attack</SettingsLabel>
          <div className={styles.variantGroup}>
            <div className={styles.settingsButtons}>
              {([
                ['MULTIPLE', 'Any opponent', 'Each creature may attack any opponent (CR 802)'],
                ['LEFT', 'Left only', 'Each creature may attack only the player to your left (CR 803)'],
                ['RIGHT', 'Right only', 'Each creature may attack only the player to your right (CR 803)'],
              ] as const).map(([mode, label, title]) => (
                <button
                  key={mode}
                  onClick={() => updateLobbySettings({ attackMode: mode })}
                  className={`${styles.settingsButton} ${(s.attackMode ?? 'MULTIPLE') === mode ? styles.settingsButtonActive : ''}`}
                  title={title}
                >
                  {label}
                </button>
              ))}
            </div>
            <div className={styles.variantCaption}>
              Who each creature may attack. "Left"/"right" follow the seating order.
            </div>
          </div>
        </div>
      )}

      {/* Set selection — chips here, the full searchable browser behind a modal. Premade Decks
          generates no boosters, so it needs none of this. */}
      {/* Cube — a pack source, so it stands in for the set picker rather than adding to it. Always
          offered (even with no cube yet) so the host can find it; picking one hides the set controls. */}
      {!isPremade && (
        <div className={styles.settingsRow} style={{ alignItems: 'flex-start' }}>
          <span style={{ paddingTop: 7 }}>
            <SettingsLabel topicId="cards-cube">Cube</SettingsLabel>
          </span>
          <CubePanel
            settings={s}
            playerCount={view.players.length}
            updateLobbySettings={updateLobbySettings}
          />
        </div>
      )}

      {/* Pool Play (cube Sealed only): no draft at all — everyone builds from the whole cube. */}
      {isCube && isSealed && (
        <div className={styles.settingsRow}>
          <SettingsLabel topicId="cube-pool-play">Card pool</SettingsLabel>
          <div className={styles.variantGroup}>
            <div className={styles.settingsButtons}>
              <button
                onClick={() => updateLobbySettings({ cubePoolPlay: false })}
                className={`${styles.settingsButton} ${!s.cubePoolPlay ? styles.settingsButtonActive : ''}`}
                title="Deal each player their own sealed pool from the cube"
              >
                Sealed packs
              </button>
              <button
                onClick={() => updateLobbySettings({ cubePoolPlay: true })}
                className={`${styles.settingsButton} ${s.cubePoolPlay ? styles.settingsButtonActive : ''}`}
                title="Pool Play: every player builds from the entire cube, up to 4 copies of any card"
              >
                Pool Play
              </button>
            </div>
            <div className={styles.variantCaption}>
              {s.cubePoolPlay
                ? 'Every player builds from the whole cube at once, up to 4 copies of any card. Nothing is dealt, so the cube can be any size and no two players compete for a card.'
                : 'Each player opens their own packs dealt from the cube. No card appears twice across the table.'}
            </div>
          </div>
        </div>
      )}

      {!isPremade && !isCube && (
        <>
          <div className={styles.settingsRow} style={{ alignItems: 'flex-start' }}>
            <span className={styles.settingsLabel} style={{ paddingTop: 7 }}>Sets</span>
            <div className={styles.setSelection}>
              {selectedSets.length > 0 ? (
                <div className={styles.setChips}>
                  {selectedSets.map((set) => (
                    <span
                      key={set.code}
                      className={`${styles.setChip} ${isAnyDraft ? styles.setChipDraft : ''} ${set.partial ? styles.setChipPartial : ''}`}
                      title={set.random
                        ? 'Random Set — revealed when the game starts'
                        : set.partial
                          ? `${set.name} — partial (reduced card pool)`
                          : set.extensionSet
                            ? `${set.name} — extension set (needs a regular set alongside)`
                            : set.name}
                    >
                      {set.random
                        ? <span className={styles.setChipIcon} aria-hidden>🎲</span>
                        : <SetIcon code={set.code} className={styles.setChipIcon} />}
                      <span className={styles.setChipName}>{set.name}</span>
                      <button
                        type="button"
                        className={styles.setChipRemove}
                        aria-label={`Remove ${set.name}`}
                        onClick={() => toggleSet(set.code)}
                      >×</button>
                    </span>
                  ))}
                </div>
              ) : (
                <span className={styles.setSelectionEmpty}>No sets selected yet</span>
              )}
              {hasSelectedSets && !hasBaseSet && (
                <span className={styles.setSelectionEmpty}>
                  Extension sets need a regular set alongside them.
                </span>
              )}
              <button type="button" onClick={() => setShowSetPicker(true)} className={styles.addSetsButton}>
                + Add sets
              </button>
            </div>
          </div>

          {/* Chaos boosters — only meaningful with >1 set and a booster-based format. */}
          {!isGridDraft && s.setCodes.length > 1 && (
            <div className={styles.settingsRow}>
              <span className={styles.settingsLabel}>Booster mix</span>
              <div className={styles.variantGroup}>
                <div className={styles.settingsButtons}>
                  <button
                    onClick={() => updateLobbySettings({ chaosBoosters: false })}
                    className={`${styles.settingsButton} ${!s.chaosBoosters ? styles.settingsButtonActive : ''}`}
                  >
                    Per set
                  </button>
                  <button
                    onClick={() => updateLobbySettings({ chaosBoosters: true })}
                    className={`${styles.settingsButton} ${s.chaosBoosters ? styles.settingsButtonActive : ''}`}
                  >
                    Chaos
                  </button>
                </div>
                <div className={styles.variantCaption}>
                  {s.chaosBoosters
                    ? 'Each booster mixes cards from all selected sets.'
                    : 'Each booster contains cards from a single set.'}
                </div>
              </div>
            </div>
          )}

          <BanListEditor
            setCodes={s.setCodes}
            bannedCardNames={s.bannedCardNames ?? []}
            onChange={(names) => updateLobbySettings({ bannedCardNames: names })}
          />
        </>
      )}

      {/* Booster/pack counts. Grid Draft uses fixed counts, Premade generates none, and Pool Play
          deals no packs at all — so it gets no pack count rather than an inert one. */}
      {!isPremade && !isGridDraft && !isPoolPlay && (
        perSetCounts ? (
          <div className={styles.settingsRow} style={{ flexDirection: 'column', alignItems: 'stretch', gap: 8 }}>
            <span className={styles.settingsLabel}>{boosterCountLabel(isWinston, countsPacks)}</span>
            <div className={styles.boosterDistribution}>
              {s.setCodes.map((code) => {
                const setName = s.setNames[s.setCodes.indexOf(code)] ?? code
                const dist = s.boosterDistribution
                const count = dist[code] ?? 0
                const total = Object.values(dist).reduce((a, b) => a + b, 0)
                return (
                  <div key={code} className={styles.boosterDistributionRow}>
                    <span className={styles.boosterDistributionSetName}>{setName}</span>
                    <div className={styles.boosterDistributionControls}>
                      <button
                        className={styles.boosterDistributionBtn}
                        disabled={count <= 0}
                        onClick={() => updateLobbySettings({
                          boosterDistribution: { ...dist, [code]: count - 1 },
                          boosterCount: total - 1,
                        })}
                      >-</button>
                      <span className={styles.boosterDistributionCount}>{count}</span>
                      <button
                        className={styles.boosterDistributionBtn}
                        disabled={total >= boosterCap}
                        onClick={() => updateLobbySettings({
                          boosterDistribution: { ...dist, [code]: count + 1 },
                          boosterCount: total + 1,
                        })}
                      >+</button>
                    </div>
                  </div>
                )
              })}
              <div className={styles.boosterDistributionTotal}>
                <span style={{ flex: 1 }}>Total</span>
                <span className={styles.boosterDistributionTotalCount}>
                  {Object.values(s.boosterDistribution).reduce((a, b) => a + b, 0)}
                  {countsPacks ? ' packs' : ' boosters'}
                </span>
              </div>
            </div>
          </div>
        ) : (
          <div className={styles.settingsRow}>
            <span className={styles.settingsLabel}>{boosterCountLabel(isWinston, countsPacks)}</span>
            <select
              value={s.boosterCount}
              onChange={(e) => updateLobbySettings({ boosterCount: Number(e.target.value) })}
              className={styles.settingsSelect}
            >
              {Array.from({ length: boosterCap }, (_, i) => i + 1).map((n) => (
                <option key={n} value={n}>{n}</option>
              ))}
            </select>
          </div>
        )
      )}

      {/* Draft timing and pick size. */}
      {isAnyDraft && (
        <div className={styles.settingsRow}>
          <span className={styles.settingsLabel}>{isWinston ? 'Turn timer (seconds)' : 'Pick timer (seconds)'}</span>
          <select
            value={s.pickTimeSeconds}
            onChange={(e) => updateLobbySettings({ pickTimeSeconds: Number(e.target.value) })}
            className={styles.settingsSelect}
          >
            {[30, 45, 60, 90, 120].map((n) => <option key={n} value={n}>{n}s</option>)}
          </select>
        </div>
      )}
      {(isDraft || isCommanderDraft) && (
        <div className={styles.settingsRow}>
          <span className={styles.settingsLabel}>Cards per pick</span>
          <div className={styles.settingsButtons}>
            {[1, 2].map((n) => (
              <button
                key={n}
                onClick={() => updateLobbySettings({ picksPerRound: n })}
                className={`${styles.settingsButton} ${s.picksPerRound === n ? `${styles.settingsButtonActive} ${styles.settingsButtonDraft}` : ''}`}
              >
                {n}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Commander preset + Brawl knobs — Commander Draft / Sealed only. */}
      {isAnyCommander && (
        <>
          <div className={styles.settingsRow}>
            <span className={styles.settingsLabel}>Preset</span>
            <div className={styles.settingsButtons}>
              <button
                onClick={() => updateLobbySettings({ commanderPreset: 'BRAWL' })}
                className={`${styles.settingsButton} ${s.commanderPreset === 'BRAWL' ? styles.settingsButtonActive : ''}`}
                title="Paper Brawl shape — 25 starting life, 16 commander damage"
              >
                Brawl (25/16)
              </button>
              <button
                onClick={() => updateLobbySettings({ commanderPreset: 'COMMANDER' })}
                className={`${styles.settingsButton} ${s.commanderPreset === 'COMMANDER' ? styles.settingsButtonActive : ''}`}
                title="Closer to Commander Legends — 30 life, 21 commander damage"
              >
                Commander (30/21)
              </button>
            </div>
          </div>
          <div className={styles.settingsRow}>
            <span className={styles.settingsLabel}>Min deck size</span>
            <select
              value={s.deckSizeMin}
              onChange={(e) => updateLobbySettings({ deckSizeMin: Number(e.target.value) })}
              className={styles.settingsSelect}
            >
              {[40, 50, 60, 75, 100].map((n) => <option key={n} value={n}>{n}</option>)}
            </select>
          </div>
          <div className={styles.settingsRow}>
            <span className={styles.settingsLabel}>Singleton</span>
            <div className={styles.settingsButtons}>
              <button
                onClick={() => updateLobbySettings({ allowDuplicates: true })}
                className={`${styles.settingsButton} ${s.allowDuplicates ? styles.settingsButtonActive : ''}`}
                title="Allow multiple copies of the same card (drafted Commander default)"
              >
                Duplicates OK
              </button>
              <button
                onClick={() => updateLobbySettings({ allowDuplicates: false })}
                className={`${styles.settingsButton} ${!s.allowDuplicates ? styles.settingsButtonActive : ''}`}
                title="Paper-Commander singleton — max 1 of any non-basic card"
              >
                Singleton
              </button>
            </div>
          </div>
        </>
      )}

      {/* How many seats the lobby can hold.
          A cap, not a quorum — `startBlockReason` counts the players actually present — so the host
          can leave it wide and start when everyone has arrived. It lives here as well as on the
          landing wizard precisely so the wizard doesn't have to make anyone predict the number:
          the server has always accepted `maxPlayers` on `updateLobbySettings`, and until now no
          client could send it. Shapes with a forced count (Two-Headed Giant is exactly four) and the
          sub-shapes that cap themselves (Winston, Grid, the Commander pair) are excluded. */}
      {seatOptions(view).length > 1 && (
        <div className={styles.settingsRow}>
          <span
            className={styles.settingsLabel}
            title="The most players this lobby will hold. You can start before it is full."
          >
            Seats
          </span>
          <select
            value={view.maxPlayers}
            onChange={(e) => updateLobbySettings({ maxPlayers: Number(e.target.value) })}
            className={styles.settingsSelect}
          >
            {seatOptions(view).map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
        </div>
      )}

      {/* Only a bracket has matchups. */}
      {view.axes.event === 'ROUND_ROBIN' && (
        <div className={styles.settingsRow}>
          <span className={styles.settingsLabel}>Games per matchup</span>
          <select
            value={s.gamesPerMatch ?? 1}
            onChange={(e) => updateLobbySettings({ gamesPerMatch: Number(e.target.value) })}
            className={styles.settingsSelect}
          >
            {[1, 2, 3, 4, 5].map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
        </div>
      )}

      <div className={styles.settingsRow}>
        <span className={styles.settingsLabel} title="Lets players use Suggest Pick and Auto-build during this event">
          AI assistance
        </span>
        <div className={styles.settingsButtons}>
          <button
            onClick={() => updateLobbySettings({ aiAssistEnabled: false })}
            className={`${styles.settingsButton} ${!s.aiAssistEnabled ? styles.settingsButtonActive : ''}`}
          >
            Off
          </button>
          <button
            onClick={() => updateLobbySettings({ aiAssistEnabled: true })}
            className={`${styles.settingsButton} ${s.aiAssistEnabled ? styles.settingsButtonActive : ''}`}
          >
            On
          </button>
        </div>
      </div>

      {showSetPicker && (
        <SetPickerModal
          sets={allSets}
          selectedCodes={s.setCodes}
          onToggleSet={toggleSet}
          onSelectRandom={addRandomSet}
          onClose={() => setShowSetPicker(false)}
        />
      )}
    </>
  )
}

function boosterCountLabel(isWinston: boolean, countsPacks: boolean): string {
  if (isWinston) return 'Boosters (total)'
  return countsPacks ? 'Packs per player' : 'Boosters per player'
}

/** Team chip for the player list — colour-coded, and clickable for the host in manual mode. */
export function TeamChip({
  team,
  editable,
  onClick,
}: {
  /** null = teams are randomised at game start. */
  team: number | null
  editable: boolean
  onClick: () => void
}) {
  const c = team === null ? null : teamColor(team)
  const style = {
    fontSize: 10,
    fontWeight: 800,
    letterSpacing: '0.05em',
    textTransform: 'uppercase' as const,
    color: c?.bright ?? 'rgba(226, 232, 240, 0.7)',
    border: `1px solid ${c?.base ?? 'rgba(148, 163, 184, 0.45)'}`,
    background: c?.soft ?? 'rgba(148, 163, 184, 0.12)',
    borderRadius: 4,
    padding: '1px 6px',
  }
  const label = team === null ? 'Random' : `Team ${team + 1}`
  return editable && team !== null ? (
    <button onClick={onClick} style={{ ...style, cursor: 'pointer' }} title="Click to move this player to the other team">
      {label}
    </button>
  ) : (
    <span style={style}>{label}</span>
  )
}
