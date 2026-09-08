import { test, expect } from '../../fixtures/scenarioFixture'
import { PLAYER_BATTLEFIELD, OPPONENT_BATTLEFIELD, cardByName } from '../../helpers/selectors'

test('Spawnbroker chooses dependent targets and offers the exchange', async ({ createGame }) => {
  test.setTimeout(120_000)
  const { player1, player2 } = await createGame({
    player1: {
      hand: ['Spawnbroker'],
      battlefield: [{ name: 'Island' }, { name: 'Island' }, { name: 'Island' }, { name: 'Grizzly Bears' }],
      library: ['Island', 'Island'],
    },
    player2: {
      battlefield: [{ name: 'Llanowar Elves' }, { name: 'Hill Giant' }],
      library: ['Forest', 'Forest'],
    },
    phase: 'PRECOMBAT_MAIN',
    activePlayer: 1,
  })
  const p1 = player1.gamePage
  await p1.clickCard('Spawnbroker')
  await p1.selectAction('Cast')
  await expect(player1.page.getByText('creature you control', { exact: true })).toBeVisible()
  await p1.selectTarget('Grizzly Bears')
  await p1.confirmTargets()
  await p1.selectTarget('Llanowar Elves')
  await p1.confirmTargets()
  // The opponent gets a response window after both targets have been announced.
  await player2.gamePage.pass()
  await p1.answerYes()
  await expect(player1.page.locator(PLAYER_BATTLEFIELD).locator(cardByName('Llanowar Elves'))).toBeVisible()
  await expect(player1.page.locator(OPPONENT_BATTLEFIELD).locator(cardByName('Grizzly Bears'))).toBeVisible()
  await expect(player2.page.locator(PLAYER_BATTLEFIELD).locator(cardByName('Grizzly Bears'))).toBeVisible()
})
