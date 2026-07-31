/**
 * The landing wizard and the lobby have to agree about what is playable.
 *
 * `modeMatrix.ts` answers "can I have this combination?" before a lobby exists; `axisChoices.ts`
 * answers it from inside one. They are separate modules on purpose — the *reasons* are phrased for
 * different situations ("go back and pick A group" is meaningless in a lobby that already has five
 * players) — but a combination one of them offers and the other calls impossible is a bug in
 * whichever is wrong, and it would show up as a wizard that creates a lobby the lobby then says
 * cannot exist.
 *
 * So: for every selection the wizard will offer, create the lobby it resolves to, project it, and
 * assert the lobby considers the axis triple it is now sitting on to be the selected one.
 */
import { describe, expect, it } from 'vitest'
import {
  CARDS_KINDS,
  axesFromLobbySettings,
  axesFromQuickGameLobby,
} from './axes'
import {
  cardsChoices,
  defaultCardsAxis,
  lobbyKindFor,
  resolveLaunch,
  seatCap,
  shapeAxes,
  shapeChoices,
  ROSTERS,
  type Roster,
  type Selection,
} from './modeMatrix'

/**
 * Every selection the wizard can reach: roster × enabled Cards kind × open shapes.
 *
 * Seats are not part of the space: the lobby opens at the cap its shape allows and people join until
 * it is full, so a selection no longer carries a count. Cards is a *kind* at its default sub-shape,
 * because that is all the wizard commits to — which sealed or draft shape it is stays a lobby
 * sub-option, alongside deck legality. The lobby's own reachability for the non-default shapes lives
 * in `axisChoices`/`LobbyAxes.shapeBlock`, not here.
 */
function everySelection(): Selection[] {
  const out: Selection[] = []
  for (const roster of ROSTERS) {
    for (const cardsChoice of cardsChoices(roster)) {
      if (cardsChoice.disabledReason) continue
      for (const cards of [defaultCardsAxis(cardsChoice.value)]) {
        for (const shapeChoice of shapeChoices(roster, cards)) {
          if (shapeChoice.disabledReason) continue
          out.push({ roster, cards, shape: shapeChoice.value })
        }
      }
    }
  }
  return out
}

describe('modeMatrix', () => {
  it('offers at least one shape for every roster and enabled Cards value', () => {
    for (const roster of ROSTERS) {
      for (const choice of cardsChoices(roster)) {
        if (choice.disabledReason) continue
        const cards = defaultCardsAxis(choice.value)
        const open = shapeChoices(roster, cards).filter((c) => !c.disabledReason)
        expect(open.length, `${roster} / ${choice.value}`).toBeGreaterThan(0)
      }
    }
  })

  it('opens every lobby at a cap the server will accept', () => {
    for (const selection of everySelection()) {
      const { roster, cards, shape } = selection
      const seats = seatCap(roster, cards, shape)
      const label = JSON.stringify(selection)
      // `LobbyHandler.seatCapFor` — the board layout caps a Free-for-All at 6, everything else at 8.
      expect(seats, label).toBeLessThanOrEqual(shape === 'FREE_FOR_ALL' ? 6 : 8)
      expect(seats, label).toBeGreaterThanOrEqual(2)
      // Two even teams, and the server refuses a Team vs. Team pod under four.
      if (shape === 'TEAM_VS_TEAM') {
        expect(seats % 2, label).toBe(0)
        expect(seats, label).toBeGreaterThanOrEqual(4)
      }
      if (shape === 'TWO_HEADED_GIANT') expect(seats, label).toBe(4)
    }
  })

  it('resolves every selection to a lobby whose axes read back as the selection', () => {
    for (const selection of everySelection()) {
      const spec = resolveLaunch(selection)
      const label = `${JSON.stringify(selection)} → ${spec.kind}`

      if (spec.kind === 'QUICK') {
        // The wizard only ever creates a quick lobby for a 1v1 single game.
        expect(shapeAxes(selection.shape), label).toEqual({ table: 'ONE_V_ONE', event: 'SINGLE_GAME' })
        const axes = axesFromQuickGameLobby(
          { momirBasic: spec.momirBasic, format: null },
          undefined,
          spec.deckTab,
        )
        expect(axes.cards.kind, label).toBe(selection.cards.kind)
        expect(axes.table, label).toBe('ONE_V_ONE')
        expect(axes.event, label).toBe('SINGLE_GAME')
      } else {
        const axes = axesFromLobbySettings({
          format: spec.format,
          gameMode: spec.gameMode,
          deckFormat: null,
        } as never)
        expect(axes.cards, label).toEqual(selection.cards)
        expect(axes.table, label).toEqual(shapeAxes(selection.shape).table)
        expect(axes.event, label).toEqual(shapeAxes(selection.shape).event)
      }
    }
  })

  it('only asks for AI seats where the server will accept them', () => {
    for (const selection of everySelection()) {
      const spec = resolveLaunch(selection)
      if (spec.kind !== 'TOURNAMENT' || spec.aiSeats === 0) continue
      // `LobbyHandler.handleAddAiToLobby` rejects PREMADE_DECKS and every multiplayer shape
      // (`TournamentLobby.isFreeForAll` covers FFA, 2HG and Team vs. Team).
      expect(spec.format, JSON.stringify(selection)).not.toBe('PREMADE_DECKS')
      expect(spec.gameMode, JSON.stringify(selection)).toBe('TOURNAMENT')
      expect(spec.aiSeats).toBeLessThan(spec.maxPlayers)
    }
  })

  it('routes Momir and a rolled pool to the quick lobby, which is the only thing that has them', () => {
    for (const kind of CARDS_KINDS) {
      if (kind !== 'MOMIR' && kind !== 'RANDOM') continue
      for (const roster of ROSTERS) {
        const enabled = cardsChoices(roster).find((c) => c.value === kind && !c.disabledReason)
        if (!enabled) continue
        const cards = defaultCardsAxis(kind)
        const shape = shapeChoices(roster, cards).find((c) => !c.disabledReason)!.value
        expect(lobbyKindFor({ roster, cards, shape })).toBe('QUICK')
      }
    }
  })

  it('a group is never offered a 1v1 single game, and solo is never offered a multiplayer table', () => {
    const groupShapes = shapeChoices('GROUP', { kind: 'BRING_A_DECK', legality: null })
    expect(groupShapes.map((c) => c.value)).not.toContain('ONE_GAME')

    const soloOpen = (roster: Roster) =>
      shapeChoices(roster, { kind: 'BRING_A_DECK', legality: null })
        .filter((c) => !c.disabledReason)
        .map((c) => c.value)
    expect(soloOpen('SOLO')).toEqual(['ONE_GAME'])
    expect(soloOpen('FRIEND')).toEqual(['ONE_GAME', 'BRACKET'])
  })
})
